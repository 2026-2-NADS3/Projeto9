package com.example.projetodopi.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projetodopi.R;
import com.example.projetodopi.model.Curso;
import com.example.projetodopi.model.Inscricao;
import com.example.projetodopi.util.DateUtils;
import com.example.projetodopi.util.TelaUtils;
import com.google.android.material.progressindicator.LinearProgressIndicator;

import java.util.List;

// Adapter: inscrições do aluno com o percentual de participação
public class InscricaoAdapter extends RecyclerView.Adapter<InscricaoAdapter.InscricaoViewHolder> {

    public interface OnInscricaoClickListener {
        void onInscricaoClick(Inscricao inscricao);
    }

    private final List<Inscricao> inscricoes;
    private final OnInscricaoClickListener listener;

    public InscricaoAdapter(List<Inscricao> inscricoes, OnInscricaoClickListener listener) {
        this.inscricoes = inscricoes;
        this.listener = listener;
    }

    @NonNull
    @Override
    public InscricaoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_inscricao, parent, false);
        return new InscricaoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InscricaoViewHolder holder, int position) {
        Inscricao inscricao = inscricoes.get(position);
        Curso curso = inscricao.getCurso();

        holder.tvInscNome.setText(curso.getTitulo());
        holder.tvInscInstituicao.setText(curso.getInstituicao());
        holder.tvInscCarga.setText("Carga horária: " + curso.getCargaHoraria() + "h • " + DateUtils.periodo(curso));
        holder.tvInscAulas.setText("Encontros com presença: " + inscricao.getAulasAssistidas() + " de "
                + inscricao.getTotalAulas() + " (" + inscricao.getPercentual() + "%)");
        holder.pbCurso.setProgress(inscricao.getPercentual());

        if (inscricao.isConcluido()) {
            holder.tvInscStatus.setText("CONCLUÍDO");
            TelaUtils.pintarTag(holder.tvInscStatus, R.color.verde_concluido, R.color.white);
        } else {
            holder.tvInscStatus.setText("EM ANDAMENTO");
            TelaUtils.pintarTag(holder.tvInscStatus, R.color.laranja_andamento, R.color.white);
        }

        holder.tvTagDiaUnico.setVisibility(curso.isDiaUnico() ? View.VISIBLE : View.GONE);
        TelaUtils.pintarTag(holder.tvTagDiaUnico, R.color.dia_unico_fundo, R.color.dia_unico_texto);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onInscricaoClick(inscricao);
            }
        });
    }

    @Override
    public int getItemCount() {
        return inscricoes.size();
    }

    public static class InscricaoViewHolder extends RecyclerView.ViewHolder {

        TextView tvInscNome, tvInscStatus, tvInscInstituicao, tvInscCarga, tvInscAulas, tvTagDiaUnico;
        LinearProgressIndicator pbCurso;

        public InscricaoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvInscNome = itemView.findViewById(R.id.tvInscNome);
            tvInscStatus = itemView.findViewById(R.id.tvInscStatus);
            tvInscInstituicao = itemView.findViewById(R.id.tvInscInstituicao);
            tvInscCarga = itemView.findViewById(R.id.tvInscCarga);
            tvInscAulas = itemView.findViewById(R.id.tvInscAulas);
            tvTagDiaUnico = itemView.findViewById(R.id.tvTagDiaUnico);
            pbCurso = itemView.findViewById(R.id.pbCurso);
        }
    }
}
