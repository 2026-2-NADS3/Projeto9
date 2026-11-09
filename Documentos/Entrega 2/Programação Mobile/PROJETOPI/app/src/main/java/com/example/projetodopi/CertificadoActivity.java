package com.example.projetodopi;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.example.projetodopi.data.InscricaoDao;
import com.example.projetodopi.model.Inscricao;
import com.example.projetodopi.util.DateUtils;
import com.example.projetodopi.util.SessionManager;
import com.example.projetodopi.util.TelaUtils;
import com.google.android.material.appbar.MaterialToolbar;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

// Certificado de conclusão de um curso, com opção de compartilhar como imagem
public class CertificadoActivity extends AppCompatActivity {

    private View containerCertificado;
    private Inscricao inscricao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_certificado);
        TelaUtils.aplicarInsets(findViewById(R.id.main), false);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        int cursoId = getIntent().getIntExtra(DetalheCursoActivity.EXTRA_CURSO_ID, 0);
        inscricao = new InscricaoDao(this).buscarPorCurso(cursoId);
        if (inscricao == null || !inscricao.isConcluido()) {
            Toast.makeText(this, "Certificado disponível apenas para cursos concluídos", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        containerCertificado = findViewById(R.id.containerCertificado);
        TextView tvCertNome = findViewById(R.id.tvCertNome);
        TextView tvCertTexto = findViewById(R.id.tvCertTexto);
        TextView tvCertCodigo = findViewById(R.id.tvCertCodigo);
        Button btnCompartilhar = findViewById(R.id.btnCompartilhar);

        String nome = new SessionManager(this).getAluno().getNome();
        tvCertNome.setText(nome);
        tvCertTexto.setText("concluiu o curso \"" + inscricao.getCurso().getTitulo() + "\", oferecido por "
                + inscricao.getCurso().getInstituicao() + " em parceria com a ONG Próxima Etapa, "
                + "com carga horária de " + inscricao.getCurso().getCargaHoraria() + " horas.\n\n"
                + "São Paulo, " + DateUtils.formatarData(inscricao.getDataConclusao()));
        tvCertCodigo.setText("Código de verificação: " + inscricao.getCodigoCertificado());

        btnCompartilhar.setOnClickListener(v -> compartilhar());
    }

    // Transforma o certificado em imagem PNG e abre o menu de compartilhamento do Android
    private void compartilhar() {
        try {
            Bitmap imagem = Bitmap.createBitmap(containerCertificado.getWidth(),
                    containerCertificado.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(imagem);
            canvas.drawColor(0xFFFFFFFF);
            containerCertificado.draw(canvas);

            File pasta = new File(getCacheDir(), "certificados");
            if (!pasta.exists() && !pasta.mkdirs()) {
                throw new IOException("Não foi possível criar a pasta");
            }
            File arquivo = new File(pasta, "certificado_" + inscricao.getCursoId() + ".png");
            try (FileOutputStream saida = new FileOutputStream(arquivo)) {
                imagem.compress(Bitmap.CompressFormat.PNG, 100, saida);
            }

            Uri uri = FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", arquivo);
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("image/png");
            intent.putExtra(Intent.EXTRA_STREAM, uri);
            intent.putExtra(Intent.EXTRA_TEXT, "Concluí o curso \"" + inscricao.getCurso().getTitulo()
                    + "\" pela Próxima Etapa!");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(Intent.createChooser(intent, "Compartilhar certificado"));
        } catch (IOException | IllegalArgumentException e) {
            Toast.makeText(this, "Não foi possível gerar a imagem do certificado", Toast.LENGTH_LONG).show();
        }
    }
}
