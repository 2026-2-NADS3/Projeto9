package com.example.projetodopi;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.projetodopi.util.SessionManager;
import com.example.projetodopi.util.TelaUtils;

// Tela inicial: boas-vindas e entrada no app
public class MainActivity extends AppCompatActivity {

    private SessionManager sessao;
    private Button btnProximo;
    private TextView tvMainSubtitulo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        TelaUtils.aplicarInsets(findViewById(R.id.main), false);

        sessao = new SessionManager(this);
        btnProximo = findViewById(R.id.btnProximo);
        tvMainSubtitulo = findViewById(R.id.tvMainSubtitulo);

        // Se o aluno já fez login, vai direto para a Área do Aluno
        btnProximo.setOnClickListener(v -> {
            Intent intent;
            if (sessao.isLogado()) {
                intent = new Intent(MainActivity.this, FormActivity.class);
            } else {
                intent = new Intent(MainActivity.this, LoginActivity.class);
            }
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (sessao.isLogado()) {
            btnProximo.setText("Continuar");
            tvMainSubtitulo.setText("Olá, " + sessao.getAluno().getPrimeiroNome() + "! Que bom ter você de volta.");
        } else {
            btnProximo.setText("Entrar");
        }
    }
}
