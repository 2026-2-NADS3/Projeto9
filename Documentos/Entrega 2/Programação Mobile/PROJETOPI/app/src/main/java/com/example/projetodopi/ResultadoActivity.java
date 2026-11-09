package com.example.projetodopi;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projetodopi.adapter.InscricaoAdapter;
import com.example.projetodopi.data.InscricaoDao;
import com.example.projetodopi.model.Inscricao;
import com.example.projetodopi.util.TelaUtils;

import java.util.List;

// Tela de inscrições: mostra o progresso do aluno em cada curso
public class ResultadoActivity extends AppCompatActivity {

    private TextView tvResumo;
    private RecyclerView rvInscricoes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_resultado);
        TelaUtils.aplicarInsets(findViewById(R.id.main), false);

        tvResumo = findViewById(R.id.tvResumo);
        rvInscricoes = findViewById(R.id.rvInscricoes);
        Button btnVoltarCursos = findViewById(R.id.btnVoltarCursos);
        Button btnVoltar = findViewById(R.id.btnVoltar);

        rvInscricoes.setLayoutManager(new LinearLayoutManager(this));

        // Volta para a aba de cursos da Área do Aluno
        btnVoltarCursos.setOnClickListener(v -> {
            Intent intent = new Intent(ResultadoActivity.this, FormActivity.class);
            intent.putExtra(FormActivity.EXTRA_ABA, R.id.navCursos);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });

        // Volta para a tela inicial
        btnVoltar.setOnClickListener(v -> {
            Intent intent = new Intent(ResultadoActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        carregarInscricoes();
    }

    private void carregarInscricoes() {
        List<Inscricao> inscricoes = new InscricaoDao(this).listar();

        // Dados numéricos: total de cursos, horas e percentual médio de participação
        int horas = 0;
        int concluidos = 0;
        int somaPercentual = 0;
        for (Inscricao inscricao : inscricoes) {
            horas += inscricao.getCurso().getCargaHoraria();
            somaPercentual += inscricao.getPercentual();
            if (inscricao.isConcluido()) concluidos++;
        }
        int media = inscricoes.isEmpty() ? 0 : somaPercentual / inscricoes.size();
        String resumo = inscricoes.size() + " cursos inscritos • " + horas + "h no total\n"
                + concluidos + " concluídos • participação média de " + media + "%";

        // texto opcional enviado por outra tela via putExtra
        String extra = getIntent().getStringExtra("resultado");
        if (extra != null) {
            resumo = resumo + "\n" + extra;
        }
        tvResumo.setText(resumo);

        rvInscricoes.setAdapter(new InscricaoAdapter(inscricoes, inscricao -> {
            Intent intent = new Intent(ResultadoActivity.this, DetalheCursoActivity.class);
            intent.putExtra(DetalheCursoActivity.EXTRA_CURSO_ID, inscricao.getCursoId());
            startActivity(intent);
        }));
    }
}
