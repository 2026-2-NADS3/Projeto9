package com.example.projetodopi.adapter;

import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projetodopi.R;
import com.example.projetodopi.model.ItemAgenda;
import com.example.projetodopi.util.DateUtils;
import com.example.projetodopi.util.TelaUtils;

import java.util.List;

// Adapter: liga a lista de encontros da agenda ao RecyclerView
public class AgendaAdapter extends RecyclerView.Adapter<AgendaAdapter.AgendaViewHolder> {

    public interface OnEncontroClickListener {
        void onEncontroClick(ItemAgenda item);
    }

    private final List<ItemAgenda> listaAgenda;
    private final OnEncontroClickListener listener;

    public AgendaAdapter(List<ItemAgenda> listaAgenda, OnEncontroClickListener listener) {
        this.listaAgenda = listaAgenda;
        this.listener = listener;
    }

    // Cria a visualização de cada item usando o layout item_agenda.xml
    @NonNull
    @Override
    public AgendaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_agenda, parent, false);
        return new AgendaViewHolder(view);
    }

    // Preenche cada item com os dados do encontro daquela posição
    @Override
    public void onBindViewHolder(@NonNull AgendaViewHolder holder, int position) {
        ItemAgenda item = listaAgenda.get(position);

        holder.tvAgendaDia.setText(DateUtils.dia(item.getData()));
        holder.tvAgendaMes.setText(DateUtils.mesAbreviado(item.getData()));
        holder.tvAgendaDisciplina.setText(item.getTitulo());
        holder.tvAgendaData.setText(DateUtils.diaDaSemanaEData(item.getData()) + " • " + item.getInstituicao());
        holder.tvAgendaHorario.setText(item.getHorario());
        holder.tvAgendaLocal.setText(item.getLocal());

        // Curso de dia único: bloco de data amarelo + etiqueta
        int corFundo = item.isDiaUnico() ? R.color.dia_unico_fundo : R.color.pe_primary_container;
        int corTexto = item.isDiaUnico() ? R.color.dia_unico_texto : R.color.pe_on_primary_container;
        ViewCompat.setBackgroundTintList(holder.blocoData,
                ColorStateList.valueOf(ContextCompat.getColor(holder.itemView.getContext(), corFundo)));
        holder.tvAgendaDia.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), corTexto));
        holder.tvAgendaMes.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), corTexto));
        holder.tvTagDiaUnico.setVisibility(item.isDiaUnico() ? View.VISIBLE : View.GONE);
        TelaUtils.pintarTag(holder.tvTagDiaUnico, R.color.dia_unico_fundo, R.color.dia_unico_texto);

        // Situação do encontro
        boolean passado = DateUtils.isPassado(item.getData());
        holder.ivPresenca.setVisibility(item.isPresencaConfirmada() ? View.VISIBLE : View.GONE);
        if (item.isPresencaConfirmada()) {
            holder.tvAgendaSituacao.setText("PRESENÇA CONFIRMADA");
            TelaUtils.pintarTag(holder.tvAgendaSituacao, R.color.verde_concluido, R.color.white);
        } else if (passado) {
            holder.tvAgendaSituacao.setText("SEM PRESENÇA");
            TelaUtils.pintarTag(holder.tvAgendaSituacao, R.color.card_cinza, R.color.white);
        } else if (item.getData().equals(DateUtils.hoje())) {
            holder.tvAgendaSituacao.setText("HOJE • FAÇA O CHECK-IN");
            TelaUtils.pintarTag(holder.tvAgendaSituacao, R.color.laranja_andamento, R.color.white);
        } else {
            holder.tvAgendaSituacao.setText("PRÓXIMO");
            TelaUtils.pintarTag(holder.tvAgendaSituacao, R.color.pe_secondary_container,
                    R.color.pe_on_secondary_container);
        }
        holder.itemView.setAlpha(passado ? 0.7f : 1f);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEncontroClick(item);
            }
        });
    }

    // Informa ao RecyclerView quantos itens existem na lista
    @Override
    public int getItemCount() {
        return listaAgenda.size();
    }

    // ViewHolder: guarda os componentes de cada item da lista
    public static class AgendaViewHolder extends RecyclerView.ViewHolder {

        LinearLayout blocoData;
        TextView tvAgendaDia;
        TextView tvAgendaMes;
        TextView tvAgendaDisciplina;
        TextView tvAgendaData;
        TextView tvAgendaHorario;
        TextView tvAgendaLocal;
        TextView tvTagDiaUnico;
        TextView tvAgendaSituacao;
        ImageView ivPresenca;

        public AgendaViewHolder(@NonNull View itemView) {
            super(itemView);
            blocoData = itemView.findViewById(R.id.blocoData);
            tvAgendaDia = itemView.findViewById(R.id.tvAgendaDia);
            tvAgendaMes = itemView.findViewById(R.id.tvAgendaMes);
            tvAgendaDisciplina = itemView.findViewById(R.id.tvAgendaDisciplina);
            tvAgendaData = itemView.findViewById(R.id.tvAgendaData);
            tvAgendaHorario = itemView.findViewById(R.id.tvAgendaHorario);
            tvAgendaLocal = itemView.findViewById(R.id.tvAgendaLocal);
            tvTagDiaUnico = itemView.findViewById(R.id.tvTagDiaUnico);
            tvAgendaSituacao = itemView.findViewById(R.id.tvAgendaSituacao);
            ivPresenca = itemView.findViewById(R.id.ivPresenca);
        }
    }
}
