package com.example.projetodopi;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.projetodopi.data.DatabaseHelper;
import com.example.projetodopi.data.SeedData;
import com.example.projetodopi.data.SyncManager;
import com.example.projetodopi.model.Aluno;
import com.example.projetodopi.network.ApiService;
import com.example.projetodopi.util.AppExecutors;
import com.example.projetodopi.util.SessionManager;
import com.example.projetodopi.util.TelaUtils;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;

import java.io.IOException;

// Tela de login: autentica o aluno na API (ou entra em modo offline com a conta de demonstração)
public class LoginActivity extends AppCompatActivity {

    private TextInputLayout tilEmail, tilSenha;
    private TextInputEditText etEmail, etSenha;
    private Button btnEntrar;
    private LinearProgressIndicator progressLogin;
    private TextView tvAvisoLogin;
    private SessionManager sessao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        TelaUtils.aplicarInsets(findViewById(R.id.main), true);

        sessao = new SessionManager(this);
        tilEmail = findViewById(R.id.tilEmail);
        tilSenha = findViewById(R.id.tilSenha);
        etEmail = findViewById(R.id.etEmail);
        etSenha = findViewById(R.id.etSenha);
        btnEntrar = findViewById(R.id.btnEntrar);
        progressLogin = findViewById(R.id.progressLogin);
        tvAvisoLogin = findViewById(R.id.tvAvisoLogin);
        Button btnDemo = findViewById(R.id.btnDemo);

        btnDemo.setOnClickListener(v -> {
            etEmail.setText("aluno@proximaetapa.org");
            etSenha.setText("123456");
        });

        btnEntrar.setOnClickListener(v -> fazerLogin());
    }

    private void fazerLogin() {
        String email = etEmail.getText() == null ? "" : etEmail.getText().toString().trim();
        String senha = etSenha.getText() == null ? "" : etSenha.getText().toString();

        // Validação dos campos
        tilEmail.setError(null);
        tilSenha.setError(null);
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.setError("Digite um e-mail válido");
            return;
        }
        if (senha.isEmpty()) {
            tilSenha.setError("Digite sua senha");
            return;
        }

        mostrarCarregando(true);
        String urlApi = sessao.getApiUrl();

        // A chamada à API (Volley) roda fora da thread principal
        AppExecutors.io().execute(() -> {
            try {
                Aluno aluno = new ApiService(this, urlApi).login(email, senha);
                if (aluno == null) {
                    runOnUiThread(() -> mostrarErro("E-mail ou senha incorretos."));
                    return;
                }
                // Login pela API: limpa dados de outro aluno e baixa os dados atualizados
                DatabaseHelper.getInstance(this).limparTudo();
                sessao.iniciarSessao(aluno, false);
                runOnUiThread(() -> SyncManager.sincronizar(this, resultado -> abrirAreaDoAluno()));
            } catch (IOException | JSONException e) {
                entrarOffline(email, senha);
            }
        });
    }

    // API fora do ar: usa a conta e os dados de demonstração salvos no app
    private void entrarOffline(String email, String senha) {
        Aluno aluno = SeedData.buscarAluno(this, email, senha);
        if (aluno == null) {
            runOnUiThread(() -> mostrarErro("Não foi possível conectar à API. Verifique sua internet "
                    + "ou use a conta de demonstração."));
            return;
        }
        try {
            DatabaseHelper.getInstance(this).limparTudo();
            SeedData.carregar(this, aluno.getId());
            sessao.iniciarSessao(aluno, true);
            runOnUiThread(() -> {
                Toast.makeText(this, "API indisponível: entrando em modo offline", Toast.LENGTH_LONG).show();
                abrirAreaDoAluno();
            });
        } catch (IOException | JSONException e) {
            runOnUiThread(() -> mostrarErro("Erro ao carregar os dados de demonstração."));
        }
    }

    private void abrirAreaDoAluno() {
        Intent intent = new Intent(LoginActivity.this, FormActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void mostrarErro(String mensagem) {
        mostrarCarregando(false);
        tvAvisoLogin.setText(mensagem);
        tvAvisoLogin.setVisibility(View.VISIBLE);
    }

    private void mostrarCarregando(boolean carregando) {
        progressLogin.setVisibility(carregando ? View.VISIBLE : View.GONE);
        btnEntrar.setEnabled(!carregando);
        if (carregando) {
            tvAvisoLogin.setVisibility(View.GONE);
        }
    }
}
