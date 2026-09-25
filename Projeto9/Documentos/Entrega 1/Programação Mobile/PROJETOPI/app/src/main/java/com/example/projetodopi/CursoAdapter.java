package com.example.projetodopi;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

// Adapter: liga a lista de cursos ao RecyclerView
public class CursoAdapter extends RecyclerView.Adapter<CursoAdapter.CursoViewHolder> {

    private List<Curso> listaCursos;

    public CursoAdapter(List<Curso> listaCursos) {
        this.listaCursos = listaCursos;
    }

    // Cria a visualização de cada item usando o layout item_curso.xml
    @NonNull
    @Override
    public CursoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_curso, parent, false);
        return new CursoViewHolder(view);
    }

    // Preenche cada item com os dados do curso daquela posição
    @Override
    public void onBindViewHolder(@NonNull CursoViewHolder holder, int position) {
        Curso curso = listaCursos.get(position);

        holder.ivCurso.setImageResource(curso.getImagem());
        holder.tvCursoTitulo.setText(curso.getTitulo());
        holder.tvCursoDesc.setText(curso.getDescricao());
        holder.tvCursoCarga.setText("Carga horária: " + curso.getCargaHoraria() + "h");
    }

    // Informa ao RecyclerView quantos itens existem na lista
    @Override
    public int getItemCount() {
        return listaCursos.size();
    }

    // ViewHolder: guarda os componentes de cada item da lista
    public static class CursoViewHolder extends RecyclerView.ViewHolder {

        ImageView ivCurso;
        TextView tvCursoTitulo;
        TextView tvCursoDesc;
        TextView tvCursoCarga;

        public CursoViewHolder(@NonNull View itemView) {
            super(itemView);
            ivCurso = itemView.findViewById(R.id.ivCurso);
            tvCursoTitulo = itemView.findViewById(R.id.tvCursoTitulo);
            tvCursoDesc = itemView.findViewById(R.id.tvCursoDesc);
            tvCursoCarga = itemView.findViewById(R.id.tvCursoCarga);
        }
    }
}