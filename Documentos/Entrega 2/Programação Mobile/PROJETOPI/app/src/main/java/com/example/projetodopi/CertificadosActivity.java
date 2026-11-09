package com.example.projetodopi;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projetodopi.adapter.CertificadoAdapter;
import com.example.projetodopi.data.InscricaoDao;
import com.example.projetodopi.model.Inscricao;
import com.example.projetodopi.util.TelaUtils;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.List;

// Lista de certificados dos cursos concluídos
public class CertificadosActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_certificados);
        TelaUtils.aplicarInsets(findViewById(R.id.main), false);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        RecyclerView rvCertificados = findViewById(R.id.rvCertificados);
        TextView tvResumoCertificados = findViewById(R.id.tvResumoCertificados);
        TextView tvVazioCertificados = findViewById(R.id.tvVazioCertificados);

        InscricaoDao inscricaoDao = new InscricaoDao(this);
        List<Inscricao> concluidas = inscricaoDao.listarConcluidas();
        tvResumoCertificados.setText(concluidas.size() + " certificado(s) • "
                + inscricaoDao.somarHorasConcluidas() + "h de formação complementar");

        rvCertificados.setLayoutManager(new LinearLayoutManager(this));
        rvCertificados.setAdapter(new CertificadoAdapter(concluidas, inscricao -> {
            Intent intent = new Intent(CertificadosActivity.this, CertificadoActivity.class);
            intent.putExtra(DetalheCursoActivity.EXTRA_CURSO_ID, inscricao.getCursoId());
            startActivity(intent);
        }));
        tvVazioCertificados.setVisibility(concluidas.isEmpty() ? View.VISIBLE : View.GONE);
    }
}
