package com.example.projetodopi;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

// Adapter: liga a lista de encontros da agenda ao RecyclerView
public class AgendaAdapter extends RecyclerView.Adapter<AgendaAdapter.AgendaViewHolder> {

    private List<ItemAgenda> listaAgenda;

    public AgendaAdapter(List<ItemAgenda> listaAgenda) {
        this.listaAgenda = listaAgenda;
    }

    // Cria a visualização de cada item usando o layout item_agenda.xml
    @NonNull
    @Override
    public AgendaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_agenda, parent, false);
        return new AgendaViewHolder(view);
    }

    // Preenche cada item com os dados do encontro daquela posição
    @Override
    public void onBindViewHolder(@NonNull AgendaViewHolder holder, int position) {
        ItemAgenda item = listaAgenda.get(position);

        holder.tvAgendaData.setText(item.getData());
        holder.tvAgendaHorario.setText(item.getHorario());
        holder.tvAgendaDisciplina.setText(item.getDisciplina());
        holder.tvAgendaLocal.setText(item.getLocal());
    }

    // Informa ao RecyclerView quantos itens existem na lista
    @Override
    public int getItemCount() {
        return listaAgenda.size();
    }

    // ViewHolder: guarda os componentes de cada item da lista
    public static class AgendaViewHolder extends RecyclerView.ViewHolder {

        TextView tvAgendaData;
        TextView tvAgendaHorario;
        TextView tvAgendaDisciplina;
        TextView tvAgendaLocal;

        public AgendaViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAgendaData = itemView.findViewById(R.id.tvAgendaData);
            tvAgendaHorario = itemView.findViewById(R.id.tvAgendaHorario);
            tvAgendaDisciplina = itemView.findViewById(R.id.tvAgendaDisciplina);
            tvAgendaLocal = itemView.findViewById(R.id.tvAgendaLocal);
        }
    }
}