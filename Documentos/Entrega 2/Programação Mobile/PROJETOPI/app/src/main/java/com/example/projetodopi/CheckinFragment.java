package com.example.projetodopi;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projetodopi.adapter.CheckinAdapter;
import com.example.projetodopi.data.CheckinDao;
import com.example.projetodopi.data.CheckinManager;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

// Fragment de check-in: lê o QR Code do encontro e registra a presença
public class CheckinFragment extends Fragment implements Atualizavel {

    private TextInputLayout tilCodigo;
    private TextInputEditText etCodigo;
    private MaterialCardView cardResultado;
    private ImageView ivResultado;
    private TextView tvResultadoTitulo, tvResultadoMensagem, tvPendentes;
    private RecyclerView rvUltimasPresencas;

    // Abre a câmera (biblioteca ZXing) e recebe o texto do QR Code
    private final ActivityResultLauncher<ScanOptions> leitorQrCode =
            registerForActivityResult(new ScanContract(), resultado -> {
                if (resultado.getContents() == null) {
                    Toast.makeText(getContext(), "Leitura cancelada", Toast.LENGTH_SHORT).show();
                } else {
                    processarCodigo(resultado.getContents());
                }
            });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_checkin, container, false);

        tilCodigo = view.findViewById(R.id.tilCodigo);
        etCodigo = view.findViewById(R.id.etCodigo);
        cardResultado = view.findViewById(R.id.cardResultado);
        ivResultado = view.findViewById(R.id.ivResultado);
        tvResultadoTitulo = view.findViewById(R.id.tvResultadoTitulo);
        tvResultadoMensagem = view.findViewById(R.id.tvResultadoMensagem);
        tvPendentes = view.findViewById(R.id.tvPendentes);
        rvUltimasPresencas = view.findViewById(R.id.rvUltimasPresencas);
        Button btnEscanear = view.findViewById(R.id.btnEscanear);
        Button btnConfirmarCodigo = view.findViewById(R.id.btnConfirmarCodigo);
        Button btnSincronizarCheckin = view.findViewById(R.id.btnSincronizarCheckin);
        Button btnHistorico = view.findViewById(R.id.btnHistorico);

        rvUltimasPresencas.setLayoutManager(new LinearLayoutManager(getContext()));

        // Botão "Escanear QR Code"
        btnEscanear.setOnClickListener(v -> {
            ScanOptions opcoes = new ScanOptions();
            opcoes.setDesiredBarcodeFormats(ScanOptions.QR_CODE);
            opcoes.setPrompt("Aponte a câmera para o QR Code do encontro");
            opcoes.setBeepEnabled(true);
            opcoes.setOrientationLocked(false);
            leitorQrCode.launch(opcoes);
        });

        // Código digitado manualmente
        btnConfirmarCodigo.setOnClickListener(v -> {
            String codigo = etCodigo.getText() == null ? "" : etCodigo.getText().toString().trim();
            if (codigo.isEmpty()) {
                tilCodigo.setError("Digite o código do encontro");
                return;
            }
            tilCodigo.setError(null);
            processarCodigo(codigo);
        });

        btnSincronizarCheckin.setOnClickListener(v -> pedirSincronizacao());

        btnHistorico.setOnClickListener(v ->
                startActivity(new Intent(requireContext(), HistoricoActivity.class)));

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        atualizarDados();
    }

    private void processarCodigo(String codigo) {
        CheckinManager.Resultado resultado = CheckinManager.registrar(requireContext(), codigo);
        mostrarResultado(resultado);
        if (resultado.sucesso) {
            etCodigo.setText("");
            pedirSincronizacao(); // envia para a API se houver conexão
        }
        atualizarDados();
    }

    private void mostrarResultado(CheckinManager.Resultado resultado) {
        cardResultado.setVisibility(View.VISIBLE);
        tvResultadoTitulo.setText(resultado.titulo);
        tvResultadoMensagem.setText(resultado.mensagem);
        ivResultado.setImageResource(resultado.sucesso ? R.drawable.ic_check : R.drawable.ic_erro);
        int cor = resultado.sucesso ? R.color.verde_concluido : R.color.vermelho_badge;
        ivResultado.setColorFilter(ContextCompat.getColor(requireContext(), cor));
    }

    private void pedirSincronizacao() {
        if (getActivity() instanceof FormActivity) {
            ((FormActivity) getActivity()).solicitarSincronizacao();
        }
    }

    @Override
    public void atualizarDados() {
        if (getView() == null) {
            return;
        }
        CheckinDao checkinDao = new CheckinDao(requireContext());
        int pendentes = checkinDao.contarPendentes();
        tvPendentes.setText(pendentes == 0
                ? "Todas as presenças foram enviadas para a API"
                : pendentes + " presença(s) aguardando sincronização");
        rvUltimasPresencas.setAdapter(new CheckinAdapter(checkinDao.listar(3)));
    }
}
