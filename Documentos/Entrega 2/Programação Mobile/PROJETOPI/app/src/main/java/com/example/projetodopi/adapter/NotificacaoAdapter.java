package com.example.projetodopi.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projetodopi.R;
import com.example.projetodopi.model.Notificacao;
import com.example.projetodopi.util.DateUtils;

import java.util.List;

// Adapter: lista de notificações (novos cursos, avisos, presenças)
public class NotificacaoAdapter extends RecyclerView.Adapter<NotificacaoAdapter.NotificacaoViewHolder> {

    public interface OnNotificacaoClickListener {
        void onNotificacaoClick(Notificacao notificacao);
    }

    private final List<Notificacao> notificacoes;
    private final OnNotificacaoClickListener listener;

    public NotificacaoAdapter(List<Notificacao> notificacoes, OnNotificacaoClickListener listener) {
        this.notificacoes = notificacoes;
        this.listener = listener;
    }

    @NonNull
    @Override
    public NotificacaoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notificacao, parent, false);
        return new NotificacaoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificacaoViewHolder holder, int position) {
        Notificacao notificacao = notificacoes.get(position);

        holder.tvNotifTitulo.setText(notificacao.getTitulo());
        holder.tvNotifMensagem.setText(notificacao.getMensagem());
        holder.tvNotifData.setText(DateUtils.formatarDataHora(notificacao.getData()));
        holder.viewNaoLida.setVisibility(notificacao.isLida() ? View.INVISIBLE : View.VISIBLE);
        holder.itemView.setAlpha(notificacao.isLida() ? 0.75f : 1f);

        // Ícone de acordo com o tipo da notificação
        String tipo = notificacao.getTipo();
        int icone = R.drawable.ic_notificacao;
        if (Notificacao.TIPO_NOVO_CURSO.equals(tipo)) {
            icone = R.drawable.ic_cursos;
        } else if (Notificacao.TIPO_PRESENCA.equals(tipo)) {
            icone = R.drawable.ic_check;
        } else if (Notificacao.TIPO_MENSAGEM.equals(tipo)) {
            icone = R.drawable.ic_chat;
        }
        holder.ivTipoNotificacao.setImageResource(icone);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onNotificacaoClick(notificacao);
            }
        });
    }

    @Override
    public int getItemCount() {
        return notificacoes.size();
    }

    public static class NotificacaoViewHolder extends RecyclerView.ViewHolder {

        ImageView ivTipoNotificacao;
        TextView tvNotifTitulo, tvNotifMensagem, tvNotifData;
        View viewNaoLida;

        public NotificacaoViewHolder(@NonNull View itemView) {
            super(itemView);
            ivTipoNotificacao = itemView.findViewById(R.id.ivTipoNotificacao);
            tvNotifTitulo = itemView.findViewById(R.id.tvNotifTitulo);
            tvNotifMensagem = itemView.findViewById(R.id.tvNotifMensagem);
            tvNotifData = itemView.findViewById(R.id.tvNotifData);
            viewNaoLida = itemView.findViewById(R.id.viewNaoLida);
        }
    }
}
