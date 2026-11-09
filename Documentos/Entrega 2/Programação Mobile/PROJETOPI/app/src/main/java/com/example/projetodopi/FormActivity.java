package com.example.projetodopi;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.projetodopi.data.NotificacaoDao;
import com.example.projetodopi.data.SyncManager;
import com.example.projetodopi.util.DateUtils;
import com.example.projetodopi.util.NetworkUtils;
import com.example.projetodopi.util.SessionManager;
import com.example.projetodopi.util.TelaUtils;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.snackbar.Snackbar;

// Área do Aluno: troca os Fragments pela navegação inferior e cuida da sincronização com a API
public class FormActivity extends AppCompatActivity {

    // Extra para abrir uma aba específica (ex.: putExtra(FormActivity.EXTRA_ABA, R.id.navCheckin))
    public static final String EXTRA_ABA = "aba";

    private MaterialToolbar toolbar;
    private TextView tvBannerOffline;
    private TextView tvBadgeNotificacoes;
    private LinearProgressIndicator progressSync;
    private BottomNavigationView bottomNav;
    private SessionManager sessao;
    private NetworkUtils.Monitor monitorRede;
    private boolean sincronizando = false;

    // Pedido de permissão de notificação (Android 13+)
    private final ActivityResultLauncher<String> pedirPermissaoNotificacao =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), concedida -> { });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_form);
        TelaUtils.aplicarInsets(findViewById(R.id.main), false);

        sessao = new SessionManager(this);
        if (!sessao.isLogado()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        toolbar = findViewById(R.id.toolbar);
        tvBannerOffline = findViewById(R.id.tvBannerOffline);
        progressSync = findViewById(R.id.progressSync);
        bottomNav = findViewById(R.id.bottomNav);

        toolbar.setTitle("Olá, " + sessao.getAluno().getPrimeiroNome());
        configurarMenuToolbar();

        // Navegação inferior: cada item abre um Fragment
        bottomNav.setOnItemSelectedListener(item -> {
            abrirFragment(criarFragment(item.getItemId()));
            return true;
        });
        if (savedInstanceState == null) {
            bottomNav.setSelectedItemId(getIntent().getIntExtra(EXTRA_ABA, R.id.navCursos));
        }

        // Monitora a conexão: mostra o aviso e sincroniza quando a internet volta
        monitorRede = new NetworkUtils.Monitor(this, online -> {
            atualizarBanner(online);
            if (online) {
                sincronizar(false);
            }
        });

        pedirPermissaoDeNotificacao();
        atualizarBanner(NetworkUtils.isOnline(this));
        sincronizar(false);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        // Ex.: botão "Voltar aos cursos" da tela de inscrições
        if (intent.hasExtra(EXTRA_ABA)) {
            bottomNav.setSelectedItemId(intent.getIntExtra(EXTRA_ABA, R.id.navCursos));
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (monitorRede != null) {
            monitorRede.iniciar();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (monitorRede != null) {
            monitorRede.parar();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (toolbar != null) {
            toolbar.setTitle("Olá, " + sessao.getAluno().getPrimeiroNome());
            atualizarBadge();
            atualizarSubtitulo();
        }
    }

    // ---------- Navegação ----------

    private Fragment criarFragment(int itemId) {
        if (itemId == R.id.navAgenda) {
            return new AgendaFragment();
        } else if (itemId == R.id.navCheckin) {
            return new CheckinFragment();
        } else if (itemId == R.id.navCard) {
            return new CardFragment();
        } else if (itemId == R.id.navPerfil) {
            return new PerfilFragment();
        }
        return new CursosFragment();
    }

    private void abrirFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frameConteudo, fragment)
                .commit();
    }

    // Usado pelos Fragments para trocar de aba
    public void irParaAba(int itemId) {
        bottomNav.setSelectedItemId(itemId);
    }

    // ---------- Toolbar ----------

    private void configurarMenuToolbar() {
        toolbar.inflateMenu(R.menu.menu_toolbar);
        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.menuSincronizar) {
                sincronizar(true);
                return true;
            }
            return false;
        });

        View acaoNotificacoes = toolbar.getMenu().findItem(R.id.menuNotificacoes).getActionView();
        if (acaoNotificacoes != null) {
            tvBadgeNotificacoes = acaoNotificacoes.findViewById(R.id.tvBadgeNotificacoes);
            acaoNotificacoes.setOnClickListener(v ->
                    startActivity(new Intent(FormActivity.this, NotificacoesActivity.class)));
        }
    }

    private void atualizarBadge() {
        if (tvBadgeNotificacoes == null) {
            return;
        }
        int naoLidas = new NotificacaoDao(this).contarNaoLidas();
        tvBadgeNotificacoes.setVisibility(naoLidas > 0 ? View.VISIBLE : View.GONE);
        tvBadgeNotificacoes.setText(naoLidas > 9 ? "9+" : String.valueOf(naoLidas));
    }

    private void atualizarSubtitulo() {
        String ultima = sessao.getUltimaSincronizacao();
        if (sessao.isModoOffline() || ultima.isEmpty()) {
            toolbar.setSubtitle("Modo offline • dados salvos no aparelho");
        } else {
            toolbar.setSubtitle("Sincronizado em " + DateUtils.formatarDataHora(ultima));
        }
    }

    private void atualizarBanner(boolean online) {
        tvBannerOffline.setVisibility(online ? View.GONE : View.VISIBLE);
    }

    // ---------- Sincronização ----------

    // Chamado pelos Fragments (ex.: depois de um check-in)
    public void solicitarSincronizacao() {
        sincronizar(false);
    }

    private void sincronizar(boolean manual) {
        if (sincronizando) {
            return;
        }
        sincronizando = true;
        progressSync.setVisibility(View.VISIBLE);

        SyncManager.sincronizar(this, resultado -> {
            sincronizando = false;
            if (isFinishing() || isDestroyed()) {
                return;
            }
            progressSync.setVisibility(View.GONE);
            atualizarBadge();
            atualizarSubtitulo();
            atualizarFragmentAtual();
            if (manual || !resultado.sucesso || resultado.novasNotificacoes > 0) {
                Snackbar.make(findViewById(R.id.main), resultado.mensagem, Snackbar.LENGTH_LONG)
                        .setAnchorView(bottomNav)
                        .show();
            }
        });
    }

    private void atualizarFragmentAtual() {
        Fragment atual = getSupportFragmentManager().findFragmentById(R.id.frameConteudo);
        if (atual instanceof Atualizavel) {
            ((Atualizavel) atual).atualizarDados();
        }
    }

    private void pedirPermissaoDeNotificacao() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                && ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
            pedirPermissaoNotificacao.launch(Manifest.permission.POST_NOTIFICATIONS);
        }
    }
}
