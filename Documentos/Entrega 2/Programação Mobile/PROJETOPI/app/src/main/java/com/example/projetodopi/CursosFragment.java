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

import com.example.projetodopi.adapter.CursoAdapter;
import com.example.projetodopi.data.CursoDao;
import com.example.projetodopi.data.InscricaoDao;
import com.example.projetodopi.model.Curso;
import com.example.projetodopi.model.Inscricao;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

// Fragment que mostra os cursos (dados da API salvos no SQLite)
public class CursosFragment extends Fragment implements Atualizavel {

    private RecyclerView rvCursos;
    private TextView tvTotalHoras;
    private TextView tvVazioCursos;
    private ChipGroup chipGroupFiltro;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        // Carrega o layout do fragment
        View view = inflater.inflate(R.layout.fragment_cursos, container, false);

        rvCursos = view.findViewById(R.id.rvCursos);
        tvTotalHoras = view.findViewById(R.id.tvTotalHoras);
        tvVazioCursos = view.findViewById(R.id.tvVazioCursos);
        chipGroupFiltro = view.findViewById(R.id.chipGroupFiltro);

        rvCursos.setLayoutManager(new LinearLayoutManager(getContext()));

        // Ao trocar o filtro, recarrega a lista
        chipGroupFiltro.setOnCheckedStateChangeListener((grupo, ids) -> atualizarDados());

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
        List<Curso> todos = new CursoDao(requireContext()).listarTodos();
        Map<Integer, Inscricao> inscricoes = new InscricaoDao(requireContext()).mapaPorCurso();

        // Aplica o filtro escolhido nos chips
        int filtro = chipGroupFiltro.getCheckedChipId();
        List<Curso> filtrados = new ArrayList<>();
        for (Curso curso : todos) {
            if (filtro == R.id.chipInscritos && !inscricoes.containsKey(curso.getId())) {
                continue;
            }
            if (filtro == R.id.chipDiaUnico && !curso.isDiaUnico()) {
                continue;
            }
            filtrados.add(curso);
        }

        // Soma a carga horária dos cursos em que o aluno está inscrito (dado numérico)
        int totalHoras = 0;
        for (Inscricao inscricao : inscricoes.values()) {
            if (inscricao.getCurso() != null) {
                totalHoras += inscricao.getCurso().getCargaHoraria();
            }
        }
        tvTotalHoras.setText(inscricoes.size() + " cursos inscritos • " + totalHoras + "h no total • "
                + todos.size() + " disponíveis");

        rvCursos.setAdapter(new CursoAdapter(filtrados, inscricoes, curso -> {
            Intent intent = new Intent(requireContext(), DetalheCursoActivity.class);
            intent.putExtra(DetalheCursoActivity.EXTRA_CURSO_ID, curso.getId());
            startActivity(intent);
        }));
        tvVazioCursos.setVisibility(filtrados.isEmpty() ? View.VISIBLE : View.GONE);
    }
}
