package com.example.projetodopi;

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

import java.util.ArrayList;
import java.util.List;

// Fragment que mostra a agenda individual do aluno
public class AgendaFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        // Carrega o layout do fragment
        View view = inflater.inflate(R.layout.fragment_agenda, container, false);

        RecyclerView rvAgenda = view.findViewById(R.id.rvAgenda);
        TextView tvTotalEncontros = view.findViewById(R.id.tvTotalEncontros);

        // Lista com os encontros da semana do aluno
        List<ItemAgenda> agenda = new ArrayList<>();
        agenda.add(new ItemAgenda("Seg, 29/09", "19h00", "Programação Mobile", "Sala 3 - Bloco A"));
        agenda.add(new ItemAgenda("Ter, 30/09", "19h00", "Design de Interfaces", "Laboratório 2"));
        agenda.add(new ItemAgenda("Qua, 01/10", "20h40", "Banco de Dados", "Sala 5 - Bloco B"));
        agenda.add(new ItemAgenda("Qui, 02/10", "19h00", "Programação Mobile", "Laboratório 1"));

        // Mostra a quantidade de encontros da semana (dado numérico)
        tvTotalEncontros.setText(agenda.size() + " encontros nesta semana");

        // Configura o RecyclerView: lista vertical usando o AgendaAdapter
        rvAgenda.setLayoutManager(new LinearLayoutManager(getContext()));
        rvAgenda.setAdapter(new AgendaAdapter(agenda));

        return view;
    }
}