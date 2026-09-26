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

// Fragment que mostra a lista de cursos do aluno
public class CursosFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        // Carrega o layout do fragment
        View view = inflater.inflate(R.layout.fragment_cursos, container, false);

        RecyclerView rvCursos = view.findViewById(R.id.rvCursos);
        TextView tvTotalHoras = view.findViewById(R.id.tvTotalHoras);

        // Lista com os cursos em que o aluno está inscrito
        List<Curso> cursos = new ArrayList<>();
        cursos.add(new Curso("Programação Mobile", "Aprenda a criar apps Android com Java", 40, R.drawable.curso1));
        cursos.add(new Curso("Design de Interfaces", "Crie telas bonitas e fáceis de usar", 30, R.drawable.curso2));
        cursos.add(new Curso("Banco de Dados", "Organize e consulte dados com SQL", 50, R.drawable.curso3));

        // Soma a carga horária de todos os cursos (dado numérico)
        int totalHoras = 0;
        for (Curso curso : cursos) {
            totalHoras += curso.getCargaHoraria();
        }
        tvTotalHoras.setText(cursos.size() + " cursos inscritos • " + totalHoras + "h no total");

        // Configura o RecyclerView: lista vertical usando o CursoAdapter
        rvCursos.setLayoutManager(new LinearLayoutManager(getContext()));
        rvCursos.setAdapter(new CursoAdapter(cursos));

        return view;
    }
}