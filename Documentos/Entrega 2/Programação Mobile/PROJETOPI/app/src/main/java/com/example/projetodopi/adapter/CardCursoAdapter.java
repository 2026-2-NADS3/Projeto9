package com.example.projetodopi.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projetodopi.R;
import com.example.projetodopi.model.Curso;

import java.util.List;
import java.util.Locale;
import java.util.Set;

// Adapter: cursos exibidos no Card Digital (em duas colunas)
public class CardCursoAdapter extends RecyclerView.Adapter<CardCursoAdapter.CardViewHolder> {

    private final List<Curso> cursos;
    private final Set<Integer> concluidos;   // ids dos cursos concluídos pelo aluno

    public CardCursoAdapter(List<Curso> cursos, Set<Integer> concluidos) {
        this.cursos = cursos;
        this.concluidos = concluidos;
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card_curso, parent, false);
        return new CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        Curso curso = cursos.get(position);
        boolean concluido = concluidos.contains(curso.getId());

        holder.tvSiglaCard.setText(curso.getInstituicao());
        holder.tvNomeCursoCard.setText(curso.getTitulo());

        // Concluído: nome em verde e instituição colorida. Não concluído: tudo em cinza claro.
        if (concluido) {
            holder.tvNomeCursoCard.setTextColor(ContextCompat.getColor(holder.itemView.getContext(),
                    R.color.verde_concluido));
            holder.tvSiglaCard.setTextColor(corDaInstituicao(curso.getInstituicao()));
            holder.itemView.setAlpha(1f);
        } else {
            int cinza = ContextCompat.getColor(holder.itemView.getContext(), R.color.card_cinza);
            holder.tvNomeCursoCard.setTextColor(cinza);
            holder.tvSiglaCard.setTextColor(cinza);
            holder.itemView.setAlpha(0.6f);
        }
    }

    @Override
    public int getItemCount() {
        return cursos.size();
    }

    // Cor de destaque para o nome de cada instituição parceira
    private int corDaInstituicao(String instituicao) {
        if (instituicao == null) return Color.DKGRAY;
        switch (instituicao.toUpperCase(Locale.ROOT)) {
            case "FECAP": return Color.parseColor("#1B6E2E");
            case "ESPM": return Color.parseColor("#B3202A");
            case "INSPER": return Color.parseColor("#5C5C5C");
            case "FAAP": return Color.parseColor("#3D5A80");
            case "OSWALDO CRUZ": return Color.parseColor("#1A237E");
            case "FSA": return Color.parseColor("#5C6BC0");
            case "INTELI": return Color.parseColor("#D84315");
            default: return Color.parseColor("#6A2FB8");
        }
    }

    public static class CardViewHolder extends RecyclerView.ViewHolder {

        TextView tvSiglaCard;
        TextView tvNomeCursoCard;

        public CardViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSiglaCard = itemView.findViewById(R.id.tvSiglaCard);
            tvNomeCursoCard = itemView.findViewById(R.id.tvNomeCursoCard);
        }
    }
}
