package com.example.projetodopi;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projetodopi.adapter.NotificacaoAdapter;
import com.example.projetodopi.data.NotificacaoDao;
import com.example.projetodopi.data.SyncManager;
import com.example.projetodopi.model.Notificacao;
import com.example.projetodopi.util.TelaUtils;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.List;

// Área de notificações: novos cursos disponíveis e avisos importantes
public class NotificacoesActivity extends AppCompatActivity {

    private NotificacaoDao notificacaoDao;
    private RecyclerView rvNotificacoes;
    private TextView tvVazioNotificacoes;
    private MaterialToolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_notificacoes);
        TelaUtils.aplicarInsets(findViewById(R.id.main), false);

        notificacaoDao = new NotificacaoDao(this);
        toolbar = findViewById(R.id.toolbar);
        rvNotificacoes = findViewById(R.id.rvNotificacoes);
        tvVazioNotificacoes = findViewById(R.id.tvVazioNotificacoes);
        rvNotificacoes.setLayoutManager(new LinearLayoutManager(this));

        toolbar.setNavigationOnClickListener(v -> finish());
        toolbar.inflateMenu(R.menu.menu_notificacoes);
        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.menuMarcarLidas) {
                notificacaoDao.marcarTodasLidas();
                carregarNotificacoes();
                return true;
            }
            return false;
        });

        // Busca notificações novas na API ao abrir a tela
        SyncManager.sincronizar(this, resultado -> {
            if (!isFinishing()) {
                carregarNotificacoes();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        carregarNotificacoes();
    }

    private void carregarNotificacoes() {
        List<Notificacao> notificacoes = notificacaoDao.listar();
        int naoLidas = notificacaoDao.contarNaoLidas();
        toolbar.setSubtitle(naoLidas > 0 ? naoLidas + " não lida(s)" : "Tudo em dia");

        rvNotificacoes.setAdapter(new NotificacaoAdapter(notificacoes, notificacao -> {
            notificacaoDao.marcarLida(notificacao.getId());
            // Notificação ligada a um curso: abre os detalhes do curso
            if (notificacao.getCursoId() > 0) {
                Intent intent = new Intent(NotificacoesActivity.this, DetalheCursoActivity.class);
                intent.putExtra(DetalheCursoActivity.EXTRA_CURSO_ID, notificacao.getCursoId());
                startActivity(intent);
            } else {
                carregarNotificacoes();
            }
        }));
        tvVazioNotificacoes.setVisibility(notificacoes.isEmpty() ? View.VISIBLE : View.GONE);
    }
}
