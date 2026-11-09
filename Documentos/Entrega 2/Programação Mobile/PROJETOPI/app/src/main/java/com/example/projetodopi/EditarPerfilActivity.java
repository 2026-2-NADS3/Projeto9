package com.example.projetodopi;

import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.projetodopi.data.SyncManager;
import com.example.projetodopi.model.Aluno;
import com.example.projetodopi.util.AppExecutors;
import com.example.projetodopi.util.ImageUtils;
import com.example.projetodopi.util.SessionManager;
import com.example.projetodopi.util.TelaUtils;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.io.IOException;

// Edição do perfil do aluno (dados pessoais, acadêmicos e foto)
public class EditarPerfilActivity extends AppCompatActivity {

    private SessionManager sessao;
    private ShapeableImageView ivFotoEditar;
    private TextInputLayout tilNome;
    private TextInputEditText etNome, etEmailPerfil, etTelefone, etEscola;
    private RadioGroup rgSerie;

    // Escolher foto na galeria
    private final ActivityResultLauncher<String> escolherFoto =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    salvarFoto(uri);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_editar_perfil);
        TelaUtils.aplicarInsets(findViewById(R.id.main), true);

        sessao = new SessionManager(this);
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        ivFotoEditar = findViewById(R.id.ivFotoEditar);
        tilNome = findViewById(R.id.tilNome);
        etNome = findViewById(R.id.etNome);
        etEmailPerfil = findViewById(R.id.etEmailPerfil);
        etTelefone = findViewById(R.id.etTelefone);
        etEscola = findViewById(R.id.etEscola);
        rgSerie = findViewById(R.id.rgSerie);
        Button btnTrocarFoto = findViewById(R.id.btnTrocarFoto);
        Button btnSalvarPerfil = findViewById(R.id.btnSalvarPerfil);

        preencherCampos();

        btnTrocarFoto.setOnClickListener(v -> escolherFoto.launch("image/*"));
        btnSalvarPerfil.setOnClickListener(v -> salvar());
    }

    private void preencherCampos() {
        Aluno aluno = sessao.getAluno();
        ImageUtils.carregarFotoPerfil(ivFotoEditar, sessao.getFotoPath());
        etNome.setText(aluno.getNome());
        etEmailPerfil.setText(aluno.getEmail());
        etTelefone.setText(aluno.getTelefone());
        etEscola.setText(aluno.getEscola());

        // Marca o RadioButton cujo texto é igual à série salva
        for (int i = 0; i < rgSerie.getChildCount(); i++) {
            RadioButton opcao = (RadioButton) rgSerie.getChildAt(i);
            if (opcao.getText().toString().equals(aluno.getSerie())) {
                opcao.setChecked(true);
            }
        }
    }

    private void salvarFoto(Uri uri) {
        AppExecutors.io().execute(() -> {
            try {
                String caminho = ImageUtils.salvarFotoPerfil(this, uri);
                sessao.setFotoPath(caminho);
                runOnUiThread(() -> ImageUtils.carregarFotoPerfil(ivFotoEditar, caminho));
            } catch (IOException e) {
                runOnUiThread(() -> Toast.makeText(this, "Não foi possível usar esta imagem",
                        Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void salvar() {
        String nome = texto(etNome);
        if (nome.isEmpty()) {
            tilNome.setError("Informe seu nome");
            return;
        }
        tilNome.setError(null);

        Aluno aluno = sessao.getAluno();
        aluno.setNome(nome);
        aluno.setTelefone(texto(etTelefone));
        aluno.setEscola(texto(etEscola));
        int idSerie = rgSerie.getCheckedRadioButtonId();
        if (idSerie != -1) {
            RadioButton serie = findViewById(idSerie);
            aluno.setSerie(serie.getText().toString());
        }

        // Salva no aparelho e marca para enviar à API na próxima sincronização
        sessao.salvarAluno(aluno);
        sessao.setPerfilPendente(true);
        SyncManager.sincronizar(this, null);

        Toast.makeText(this, "Perfil atualizado!", Toast.LENGTH_SHORT).show();
        finish();
    }

    private String texto(TextInputEditText campo) {
        return campo.getText() == null ? "" : campo.getText().toString().trim();
    }
}
