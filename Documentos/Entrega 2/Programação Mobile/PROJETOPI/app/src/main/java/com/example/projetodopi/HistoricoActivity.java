package com.example.projetodopi;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projetodopi.adapter.CheckinAdapter;
import com.example.projetodopi.data.CheckinDao;
import com.example.projetodopi.data.SyncManager;
import com.example.projetodopi.model.Checkin;
import com.example.projetodopi.util.TelaUtils;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.List;

// Histórico de presenças: todos os check-ins, com a situação de sincronização de cada um
public class HistoricoActivity extends AppCompatActivity {

    private RecyclerView rvHistorico;
    private TextView tvResumoHistorico, tvVazioHistorico;
    private Button btnSincronizarHistorico;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_historico);
        TelaUtils.aplicarInsets(findViewById(R.id.main), false);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        rvHistorico = findViewById(R.id.rvHistorico);
        tvResumoHistorico = findViewById(R.id.tvResumoHistorico);
        tvVazioHistorico = findViewById(R.id.tvVazioHistorico);
        btnSincronizarHistorico = findViewById(R.id.btnSincronizarHistorico);
        rvHistorico.setLayoutManager(new LinearLayoutManager(this));

        btnSincronizarHistorico.setOnClickListener(v -> {
            btnSincronizarHistorico.setEnabled(false);
            SyncManager.sincronizar(this, resultado -> {
                if (isFinishing()) {
                    return;
                }
                btnSincronizarHistorico.setEnabled(true);
                Toast.makeText(this, resultado.mensagem, Toast.LENGTH_LONG).show();
                carregarHistorico();
            });
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        carregarHistorico();
    }

    private void carregarHistorico() {
        CheckinDao checkinDao = new CheckinDao(this);
        List<Checkin> checkins = checkinDao.listar(0);
        int pendentes = checkinDao.contarPendentes();

        tvResumoHistorico.setText(checkins.size() + " presença(s)\n" + pendentes + " pendente(s) de envio");
        rvHistorico.setAdapter(new CheckinAdapter(checkins));
        tvVazioHistorico.setVisibility(checkins.isEmpty() ? View.VISIBLE : View.GONE);
    }
}
