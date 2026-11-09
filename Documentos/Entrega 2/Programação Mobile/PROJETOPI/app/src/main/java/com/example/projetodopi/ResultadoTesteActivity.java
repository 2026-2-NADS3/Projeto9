package com.example.projetodopi;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projetodopi.adapter.CursoAdapter;
import com.example.projetodopi.data.CursoDao;
import com.example.projetodopi.data.InscricaoDao;
import com.example.projetodopi.data.SyncManager;
import com.example.projetodopi.model.Aluno;
import com.example.projetodopi.model.PerfilVocacional;
import com.example.projetodopi.util.SessionManager;
import com.example.projetodopi.util.TelaUtils;
import com.google.android.material.appbar.MaterialToolbar;

// Resultado do teste de perfil: recebe a área via Intent, salva no perfil e recomenda cursos
public class ResultadoTesteActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_resultado_teste);
        TelaUtils.aplicarInsets(findViewById(R.id.main), false);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        // Recebe os dados enviados pela TestePerfilActivity
        String area = getIntent().getStringExtra("area");
        int pontos = getIntent().getIntExtra("pontos", 0);
        int total = getIntent().getIntExtra("total", 5);

        TextView tvAreaTitulo = findViewById(R.id.tvAreaTitulo);
        TextView tvAreaDescricao = findViewById(R.id.tvAreaDescricao);
        TextView tvPontuacao = findViewById(R.id.tvPontuacao);
        RecyclerView rvRecomendados = findViewById(R.id.rvRecomendados);
        Button btnRefazer = findViewById(R.id.btnRefazer);
        Button btnConcluirTeste = findViewById(R.id.btnConcluirTeste);

        tvAreaTitulo.setText(PerfilVocacional.nome(area));
        tvAreaDescricao.setText(PerfilVocacional.descricao(area));
        tvPontuacao.setText(pontos + " de " + total + " respostas apontaram para esta área");

        // Salva o resultado no perfil e envia para a API
        SessionManager sessao = new SessionManager(this);
        Aluno aluno = sessao.getAluno();
        aluno.setPerfilVocacional(area);
        sessao.salvarAluno(aluno);
        sessao.setPerfilPendente(true);
        SyncManager.sincronizar(this, null);

        // Cursos da mesma área do resultado
        rvRecomendados.setLayoutManager(new LinearLayoutManager(this));
        rvRecomendados.setAdapter(new CursoAdapter(new CursoDao(this).listarPorArea(area),
                new InscricaoDao(this).mapaPorCurso(), curso -> {
            Intent intent = new Intent(ResultadoTesteActivity.this, DetalheCursoActivity.class);
            intent.putExtra(DetalheCursoActivity.EXTRA_CURSO_ID, curso.getId());
            startActivity(intent);
        }));

        btnRefazer.setOnClickListener(v -> {
            startActivity(new Intent(ResultadoTesteActivity.this, TestePerfilActivity.class));
            finish();
        });
        btnConcluirTeste.setOnClickListener(v -> finish());
    }
}
