package com.example.projetodopi.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projetodopi.R;
import com.example.projetodopi.model.Mensagem;
import com.example.projetodopi.util.DateUtils;

import java.util.List;

// Adapter do chat: dois tipos de item (mensagem do aluno à direita, da ONG à esquerda)
public class MensagemAdapter extends RecyclerView.Adapter<MensagemAdapter.MensagemViewHolder> {

    private static final int TIPO_ALUNO = 1;
    private static final int TIPO_ONG = 2;

    private final List<Mensagem> mensagens;

    public MensagemAdapter(List<Mensagem> mensagens) {
        this.mensagens = mensagens;
    }

    @Override
    public int getItemViewType(int position) {
        return mensagens.get(position).isDoAluno() ? TIPO_ALUNO : TIPO_ONG;
    }

    @NonNull
    @Override
    public MensagemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layout = viewType == TIPO_ALUNO ? R.layout.item_mensagem_aluno : R.layout.item_mensagem_ong;
        View view = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
        return new MensagemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MensagemViewHolder holder, int position) {
        Mensagem mensagem = mensagens.get(position);
        holder.tvTextoMensagem.setText(mensagem.getTexto());

        String hora = DateUtils.formatarData(mensagem.getDataHora()) + " " + DateUtils.hora(mensagem.getDataHora());
        if (mensagem.isDoAluno()) {
            hora += mensagem.isSincronizado() ? " • enviada" : " • aguardando conexão";
        }
        holder.tvHoraMensagem.setText(hora);
    }

    @Override
    public int getItemCount() {
        return mensagens.size();
    }

    public static class MensagemViewHolder extends RecyclerView.ViewHolder {

        TextView tvTextoMensagem, tvHoraMensagem;

        public MensagemViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTextoMensagem = itemView.findViewById(R.id.tvTextoMensagem);
            tvHoraMensagem = itemView.findViewById(R.id.tvHoraMensagem);
        }
    }
}
