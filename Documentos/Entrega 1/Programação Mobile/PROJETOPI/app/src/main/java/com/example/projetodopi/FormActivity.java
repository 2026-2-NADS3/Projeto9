package com.example.projetodopi;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RatingBar;

import androidx.appcompat.app.AppCompatActivity;

// Tela principal do aluno: exibe os Fragments de Cursos e de Agenda
public class FormActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);

        Button btnAbaCursos = findViewById(R.id.btnAbaCursos);
        Button btnAbaAgenda = findViewById(R.id.btnAbaAgenda);
        RatingBar rbAvaliacao = findViewById(R.id.rbAvaliacao);
        Button btnInscricoes = findViewById(R.id.btnInscricoes);

        // Ao abrir a tela, mostra o Fragment de cursos
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frameConteudo, new CursosFragment())
                .commit();

        // Botão "Cursos": troca para o Fragment de cursos
        btnAbaCursos.setOnClickListener(v -> {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frameConteudo, new CursosFragment())
                    .commit();
        });

        // Botão "Agenda": troca para o Fragment da agenda
        btnAbaAgenda.setOnClickListener(v -> {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frameConteudo, new AgendaFragment())
                    .commit();
        });

        // Botão "Ver minhas inscrições": envia a avaliação para a próxima tela
        btnInscricoes.setOnClickListener(v -> {

            float avaliacao = rbAvaliacao.getRating();

            String resultado = "3 cursos inscritos • 120h no total" +
                    "\nAvaliação do app: " + avaliacao;

            Intent intent = new Intent(FormActivity.this, ResultadoActivity.class);
            intent.putExtra("resultado", resultado);
            startActivity(intent);
        });
    }
}