package com.example.projetodopi;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.projetodopi.data.CursoDao;
import com.example.projetodopi.data.InscricaoDao;
import com.example.projetodopi.data.NotificacaoDao;
import com.example.projetodopi.data.SyncManager;
import com.example.projetodopi.model.Curso;
import com.example.projetodopi.model.Inscricao;
import com.example.projetodopi.model.Notificacao;
import com.example.projetodopi.model.PerfilVocacional;
import com.example.projetodopi.util.DateUtils;
import com.example.projetodopi.util.ImageUtils;
import com.example.projetodopi.util.TelaUtils;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.progressindicator.LinearProgressIndicator;

// Detalhes de um curso: datas (com destaque para dia único), carga horária, progresso e inscrição
public class DetalheCursoActivity extends AppCompatActivity {

    public static final String EXTRA_CURSO_ID = "cursoId";

    private int cursoId;
    private Curso curso;
    private MaterialCardView cardProgresso;
    private TextView tvProgressoTexto;
    private LinearProgressIndicator pbProgresso;
    private Button btnInscrever, btnVerCertificado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detalhe_curso);
        TelaUtils.aplicarInsets(findViewById(R.id.main), false);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        // Recebe o id do curso enviado pela tela anterior
        cursoId = getIntent().getIntExtra(EXTRA_CURSO_ID, 0);
        curso = new CursoDao(this).buscarPorId(cursoId);
        if (curso == null) {
            Toast.makeText(this, "Curso não encontrado", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        ImageView ivDetalheImagem = findViewById(R.id.ivDetalheImagem);
        TextView tvDetalheTitulo = findViewById(R.id.tvDetalheTitulo);
        TextView tvDetalheInstituicao = findViewById(R.id.tvDetalheInstituicao);
        TextView tvTagDiaUnico = findViewById(R.id.tvTagDiaUnico);
        TextView tvTagArea = findViewById(R.id.tvTagArea);
        TextView tvDetalhePeriodo = findViewById(R.id.tvDetalhePeriodo);
        TextView tvDetalheHorario = findViewById(R.id.tvDetalheHorario);
        TextView tvDetalheLocal = findViewById(R.id.tvDetalheLocal);
        TextView tvDetalheCarga = findViewById(R.id.tvDetalheCarga);
        TextView tvDetalheDescricao = findViewById(R.id.tvDetalheDescricao);
        cardProgresso = findViewById(R.id.cardProgresso);
        tvProgressoTexto = findViewById(R.id.tvProgressoTexto);
        pbProgresso = findViewById(R.id.pbProgresso);
        btnInscrever = findViewById(R.id.btnInscrever);
        btnVerCertificado = findViewById(R.id.btnVerCertificado);

        ImageUtils.carregarImagemCurso(ivDetalheImagem, curso.getImagem());
        tvDetalheTitulo.setText(curso.getTitulo());
        tvDetalheInstituicao.setText(curso.getInstituicao() + " • em parceria com a Próxima Etapa");
        tvDetalheDescricao.setText(curso.getDescricao());
        tvDetalheHorario.setText("Horário: " + curso.getHorario());
        tvDetalheLocal.setText("Local: " + curso.getLocal());
        tvDetalheCarga.setText("Carga horária: " + curso.getCargaHoraria() + "h • "
                + curso.getTotalAulas() + (curso.getTotalAulas() == 1 ? " encontro" : " encontros"));

        // Curso de dia único: mostra uma data só e a etiqueta amarela
        if (curso.isDiaUnico()) {
            tvDetalhePeriodo.setText("Dia único: " + DateUtils.diaDaSemanaEData(curso.getDataInicio()));
            tvTagDiaUnico.setVisibility(View.VISIBLE);
            TelaUtils.pintarTag(tvTagDiaUnico, R.color.dia_unico_fundo, R.color.dia_unico_texto);
        } else {
            tvDetalhePeriodo.setText("Período: " + DateUtils.formatarData(curso.getDataInicio())
                    + " a " + DateUtils.formatarData(curso.getDataFim()));
            tvTagDiaUnico.setVisibility(View.GONE);
        }
        tvTagArea.setText(PerfilVocacional.nome(curso.getArea()).toUpperCase());
        TelaUtils.pintarTag(tvTagArea, R.color.pe_primary_container, R.color.pe_on_primary_container);

        btnInscrever.setOnClickListener(v -> inscrever());
        btnVerCertificado.setOnClickListener(v -> {
            Intent intent = new Intent(DetalheCursoActivity.this, CertificadoActivity.class);
            intent.putExtra(EXTRA_CURSO_ID, cursoId);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (curso != null) {
            mostrarSituacao();
        }
    }

    // Mostra o progresso se o aluno estiver inscrito; senão, o botão de inscrição
    private void mostrarSituacao() {
        Inscricao inscricao = new InscricaoDao(this).buscarPorCurso(cursoId);
        if (inscricao == null) {
            cardProgresso.setVisibility(View.GONE);
            btnInscrever.setVisibility(View.VISIBLE);
            btnVerCertificado.setVisibility(View.GONE);
            return;
        }
        cardProgresso.setVisibility(View.VISIBLE);
        btnInscrever.setVisibility(View.GONE);
        pbProgresso.setProgress(inscricao.getPercentual());

        String texto = inscricao.getAulasAssistidas() + " de " + inscricao.getTotalAulas()
                + " encontros com presença (" + inscricao.getPercentual() + "%)";
        if (inscricao.isConcluido()) {
            texto += "\nConcluído em " + DateUtils.formatarData(inscricao.getDataConclusao());
        }
        if (!inscricao.isSincronizado()) {
            texto += "\nAlterações aguardando sincronização com a API";
        }
        tvProgressoTexto.setText(texto);
        btnVerCertificado.setVisibility(inscricao.isConcluido() ? View.VISIBLE : View.GONE);
    }

    private void inscrever() {
        new InscricaoDao(this).inscreverLocal(curso);
        new NotificacaoDao(this).inserirLocal("Inscrição realizada",
                "Você se inscreveu em \"" + curso.getTitulo() + "\" (" + curso.getInstituicao() + ").",
                Notificacao.TIPO_AVISO, curso.getId());
        Toast.makeText(this, "Inscrição registrada!", Toast.LENGTH_SHORT).show();
        mostrarSituacao();

        // Envia a inscrição para a API (se não houver internet, fica pendente)
        SyncManager.sincronizar(this, resultado -> {
            if (!isFinishing()) {
                mostrarSituacao();
            }
        });
    }
}
