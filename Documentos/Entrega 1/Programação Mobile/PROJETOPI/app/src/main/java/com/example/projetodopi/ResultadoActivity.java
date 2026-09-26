package com.example.projetodopi;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

// Tela de inscrições: mostra o progresso do aluno em cada curso
public class ResultadoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resultado);

        TextView tvResumo = findViewById(R.id.tvResumo);
        Button btnVoltarCursos = findViewById(R.id.btnVoltarCursos);
        Button btnVoltar = findViewById(R.id.btnVoltar);

        // Recebe o texto enviado pela FormActivity e mostra no resumo
        String resultado = getIntent().getStringExtra("resultado");
        tvResumo.setText(resultado);

        // Volta para a tela de cursos
        btnVoltarCursos.setOnClickListener(v -> {
            Intent intent = new Intent(ResultadoActivity.this, FormActivity.class);
            startActivity(intent);
        });

        // Volta para a tela inicial
        btnVoltar.setOnClickListener(v -> {
            Intent intent = new Intent(ResultadoActivity.this, MainActivity.class);
            startActivity(intent);
        });
    }
}