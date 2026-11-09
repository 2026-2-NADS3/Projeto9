package com.example.projetodopi;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projetodopi.adapter.CardCursoAdapter;
import com.example.projetodopi.data.CursoDao;
import com.example.projetodopi.data.InscricaoDao;
import com.example.projetodopi.model.Aluno;
import com.example.projetodopi.model.Inscricao;
import com.example.projetodopi.util.DateUtils;
import com.example.projetodopi.util.ImageUtils;
import com.example.projetodopi.util.SessionManager;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

// Card Digital: identificação do aluno com os cursos concluídos e um QR Code no verso
public class CardFragment extends Fragment implements Atualizavel {

    private View cardFrente, cardVerso;
    private ImageView ivFotoCard, ivQrCodeAluno;
    private TextView tvQtdConcluidos, tvNomeCard, tvEscolaCard, tvRodapeCard, tvNomeVerso, tvDadosVerso;
    private RecyclerView rvCursosCard;
    private Button btnVirarCard;
    private boolean mostrandoFrente = true;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_card, container, false);

        cardFrente = view.findViewById(R.id.cardFrente);
        cardVerso = view.findViewById(R.id.cardVerso);
        ivFotoCard = view.findViewById(R.id.ivFotoCard);
        ivQrCodeAluno = view.findViewById(R.id.ivQrCodeAluno);
        tvQtdConcluidos = view.findViewById(R.id.tvQtdConcluidos);
        tvNomeCard = view.findViewById(R.id.tvNomeCard);
        tvEscolaCard = view.findViewById(R.id.tvEscolaCard);
        tvRodapeCard = view.findViewById(R.id.tvRodapeCard);
        tvNomeVerso = view.findViewById(R.id.tvNomeVerso);
        tvDadosVerso = view.findViewById(R.id.tvDadosVerso);
        rvCursosCard = view.findViewById(R.id.rvCursosCard);
        btnVirarCard = view.findViewById(R.id.btnVirarCard);

        // Duas colunas de cursos, como no modelo do card da Próxima Etapa
        rvCursosCard.setLayoutManager(new GridLayoutManager(getContext(), 2));

        // Interação: tocar no card ou no botão vira o card
        cardFrente.setOnClickListener(v -> virarCard());
        cardVerso.setOnClickListener(v -> virarCard());
        btnVirarCard.setOnClickListener(v -> virarCard());

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        atualizarDados();
    }

    @Override
    public void atualizarDados() {
        if (getView() == null) {
            return;
        }
        SessionManager sessao = new SessionManager(requireContext());
        Aluno aluno = sessao.getAluno();

        // Cursos concluídos (dado numérico do selo)
        Set<Integer> concluidos = new HashSet<>();
        for (Inscricao inscricao : new InscricaoDao(requireContext()).listarConcluidas()) {
            concluidos.add(inscricao.getCursoId());
        }

        ImageUtils.carregarFotoPerfil(ivFotoCard, sessao.getFotoPath());
        tvQtdConcluidos.setText(String.valueOf(concluidos.size()));
        tvNomeCard.setText(aluno.getNome());
        tvEscolaCard.setText(aluno.getEscola());
        String matricula = String.format(Locale.ROOT, "PE-%05d", aluno.getId());
        tvRodapeCard.setText("Matrícula " + matricula + " • Válido em " + DateUtils.ano());
        rvCursosCard.setAdapter(new CardCursoAdapter(new CursoDao(requireContext()).listarTodos(), concluidos));

        // Verso: QR Code gerado com os dados de identificação do aluno
        tvNomeVerso.setText(aluno.getNome());
        tvDadosVerso.setText("Matrícula " + matricula + "\n" + aluno.getEmail());
        String conteudoQr = "PROXIMAETAPA|ALUNO|" + aluno.getId() + "|" + aluno.getEmail();
        try {
            Bitmap qrCode = new BarcodeEncoder().encodeBitmap(conteudoQr, BarcodeFormat.QR_CODE, 600, 600);
            ivQrCodeAluno.setImageBitmap(qrCode);
        } catch (WriterException e) {
            ivQrCodeAluno.setImageResource(R.drawable.ic_qr);
        }
    }

    // Animação de "virar" o card (gira 90°, troca a face e gira de volta)
    private void virarCard() {
        View esconder = mostrandoFrente ? cardFrente : cardVerso;
        View mostrar = mostrandoFrente ? cardVerso : cardFrente;
        float distancia = 8000 * getResources().getDisplayMetrics().density;
        esconder.setCameraDistance(distancia);
        mostrar.setCameraDistance(distancia);

        esconder.animate().rotationY(90f).setDuration(180).withEndAction(() -> {
            esconder.setVisibility(View.GONE);
            esconder.setRotationY(0f);
            mostrar.setRotationY(-90f);
            mostrar.setVisibility(View.VISIBLE);
            mostrar.animate().rotationY(0f).setDuration(180).start();
        }).start();

        mostrandoFrente = !mostrandoFrente;
        btnVirarCard.setText(mostrandoFrente ? "Mostrar QR Code" : "Mostrar cursos");
    }
}
