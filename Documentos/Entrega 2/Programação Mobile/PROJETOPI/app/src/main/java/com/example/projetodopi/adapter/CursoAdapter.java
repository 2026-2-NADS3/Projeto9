package com.example.projetodopi.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projetodopi.R;
import com.example.projetodopi.model.Curso;
import com.example.projetodopi.model.Inscricao;
import com.example.projetodopi.util.DateUtils;
import com.example.projetodopi.util.ImageUtils;
import com.example.projetodopi.util.TelaUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Adapter: liga a lista de cursos ao RecyclerView
public class CursoAdapter extends RecyclerView.Adapter<CursoAdapter.CursoViewHolder> {

    public interface OnCursoClickListener {
        void onCursoClick(Curso curso);
    }

    private final List<Curso> listaCursos;
    private final Map<Integer, Inscricao> inscricoes;   // cursoId -> inscrição do aluno
    private final OnCursoClickListener listener;

    public CursoAdapter(List<Curso> listaCursos, Map<Integer, Inscricao> inscricoes,
                        OnCursoClickListener listener) {
        this.listaCursos = listaCursos;
        this.inscricoes = inscricoes != null ? inscricoes : new HashMap<>();
        this.listener = listener;
    }

    // Cria a visualização de cada item usando o layout item_curso.xml
    @NonNull
    @Override
    public CursoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_curso, parent, false);
        return new CursoViewHolder(view);
    }

    // Preenche cada item com os dados do curso daquela posição
    @Override
    public void onBindViewHolder(@NonNull CursoViewHolder holder, int position) {
        Curso curso = listaCursos.get(position);

        ImageUtils.carregarImagemCurso(holder.ivCurso, curso.getImagem());
        holder.tvCursoTitulo.setText(curso.getTitulo());
        holder.tvCursoInstituicao.setText(curso.getInstituicao());
        holder.tvCursoDesc.setText(curso.getDescricao());
        holder.tvCursoPeriodo.setText(DateUtils.periodo(curso));
        holder.tvCursoCarga.setText("Carga horária: " + curso.getCargaHoraria() + "h • "
                + curso.getTotalAulas() + (curso.getTotalAulas() == 1 ? " encontro" : " encontros"));

        // Curso de dia único: faixa amarela e etiqueta "DIA ÚNICO"
        boolean diaUnico = curso.isDiaUnico();
        holder.faixaLateral.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(),
                diaUnico ? R.color.dia_unico_fundo : R.color.pe_primary));
        holder.tvTagDiaUnico.setVisibility(diaUnico ? View.VISIBLE : View.GONE);
        TelaUtils.pintarTag(holder.tvTagDiaUnico, R.color.dia_unico_fundo, R.color.dia_unico_texto);

        // Situação da inscrição do aluno neste curso
        Inscricao inscricao = inscricoes.get(curso.getId());
        if (inscricao == null) {
            holder.tvTagStatus.setVisibility(View.GONE);
        } else if (inscricao.isConcluido()) {
            holder.tvTagStatus.setVisibility(View.VISIBLE);
            holder.tvTagStatus.setText("CONCLUÍDO");
            TelaUtils.pintarTag(holder.tvTagStatus, R.color.verde_concluido, R.color.white);
        } else {
            holder.tvTagStatus.setVisibility(View.VISIBLE);
            holder.tvTagStatus.setText("INSCRITO • " + inscricao.getPercentual() + "%");
            TelaUtils.pintarTag(holder.tvTagStatus, R.color.pe_primary_container, R.color.pe_on_primary_container);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onCursoClick(curso);
            }
        });
    }

    // Informa ao RecyclerView quantos itens existem na lista
    @Override
    public int getItemCount() {
        return listaCursos.size();
    }

    // ViewHolder: guarda os componentes de cada item da lista
    public static class CursoViewHolder extends RecyclerView.ViewHolder {

        View faixaLateral;
        ImageView ivCurso;
        TextView tvCursoTitulo;
        TextView tvCursoInstituicao;
        TextView tvCursoDesc;
        TextView tvCursoPeriodo;
        TextView tvCursoCarga;
        TextView tvTagDiaUnico;
        TextView tvTagStatus;

        public CursoViewHolder(@NonNull View itemView) {
            super(itemView);
            faixaLateral = itemView.findViewById(R.id.faixaLateral);
            ivCurso = itemView.findViewById(R.id.ivCurso);
            tvCursoTitulo = itemView.findViewById(R.id.tvCursoTitulo);
            tvCursoInstituicao = itemView.findViewById(R.id.tvCursoInstituicao);
            tvCursoDesc = itemView.findViewById(R.id.tvCursoDesc);
            tvCursoPeriodo = itemView.findViewById(R.id.tvCursoPeriodo);
            tvCursoCarga = itemView.findViewById(R.id.tvCursoCarga);
            tvTagDiaUnico = itemView.findViewById(R.id.tvTagDiaUnico);
            tvTagStatus = itemView.findViewById(R.id.tvTagStatus);
        }
    }
}
