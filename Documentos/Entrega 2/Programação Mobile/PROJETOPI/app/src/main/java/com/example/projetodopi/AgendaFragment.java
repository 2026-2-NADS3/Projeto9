package com.example.projetodopi;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projetodopi.adapter.AgendaAdapter;
import com.example.projetodopi.data.AgendaDao;
import com.example.projetodopi.model.ItemAgenda;
import com.example.projetodopi.util.TelaUtils;

import java.util.List;

// Fragment que mostra a agenda individual do aluno
public class AgendaFragment extends Fragment implements Atualizavel {

    private RecyclerView rvAgenda;
    private TextView tvTotalEncontros;
    private TextView tvVazioAgenda;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        // Carrega o layout do fragment
        View view = inflater.inflate(R.layout.fragment_agenda, container, false);

        rvAgenda = view.findViewById(R.id.rvAgenda);
        tvTotalEncontros = view.findViewById(R.id.tvTotalEncontros);
        tvVazioAgenda = view.findViewById(R.id.tvVazioAgenda);
        TextView tvLegendaDiaUnico = view.findViewById(R.id.tvLegendaDiaUnico);
        TelaUtils.pintarTag(tvLegendaDiaUnico, R.color.dia_unico_fundo, R.color.dia_unico_texto);

        rvAgenda.setLayoutManager(new LinearLayoutManager(getContext()));
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
        List<ItemAgenda> agenda = new AgendaDao(requireContext()).listar();

        // Dados numéricos: número de encontros e presenças confirmadas
        int presencas = 0;
        int diasUnicos = 0;
        for (ItemAgenda item : agenda) {
            if (item.isPresencaConfirmada()) presencas++;
            if (item.isDiaUnico()) diasUnicos++;
        }
        tvTotalEncontros.setText(agenda.size() + " encontros • " + presencas + " presenças confirmadas • "
                + diasUnicos + " de dia único");

        rvAgenda.setAdapter(new AgendaAdapter(agenda, item -> {
            Intent intent = new Intent(requireContext(), DetalheCursoActivity.class);
            intent.putExtra(DetalheCursoActivity.EXTRA_CURSO_ID, item.getCursoId());
            startActivity(intent);
        }));
        tvVazioAgenda.setVisibility(agenda.isEmpty() ? View.VISIBLE : View.GONE);
    }
}
