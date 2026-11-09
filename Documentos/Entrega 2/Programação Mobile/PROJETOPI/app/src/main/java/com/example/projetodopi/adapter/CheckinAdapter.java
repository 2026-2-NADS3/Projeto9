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
import com.example.projetodopi.model.Checkin;
import com.example.projetodopi.util.DateUtils;
import com.example.projetodopi.util.TelaUtils;

import java.util.List;

// Adapter: histórico de presenças (check-ins)
public class CheckinAdapter extends RecyclerView.Adapter<CheckinAdapter.CheckinViewHolder> {

    private final List<Checkin> listaCheckins;

    public CheckinAdapter(List<Checkin> listaCheckins) {
        this.listaCheckins = listaCheckins;
    }

    @NonNull
    @Override
    public CheckinViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_checkin, parent, false);
        return new CheckinViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CheckinViewHolder holder, int position) {
        Checkin checkin = listaCheckins.get(position);

        holder.tvCheckinTitulo.setText(checkin.getTituloEncontro().isEmpty()
                ? "Código " + checkin.getCodigoQr() : checkin.getTituloEncontro());
        holder.tvCheckinEncontro.setText("Encontro: " + DateUtils.formatarData(checkin.getDataEncontro())
                + " às " + checkin.getHorarioEncontro());
        holder.tvCheckinRegistro.setText("Registrado em " + DateUtils.formatarDataHora(checkin.getDataHora()));

        // Sincronizado com a API ou pendente no aparelho
        if (checkin.isSincronizado()) {
            holder.ivStatusCheckin.setImageResource(R.drawable.ic_nuvem_ok);
            holder.ivStatusCheckin.setColorFilter(ContextCompat.getColor(holder.itemView.getContext(),
                    R.color.verde_concluido));
            holder.tvCheckinStatus.setText("ENVIADO");
            TelaUtils.pintarTag(holder.tvCheckinStatus, R.color.verde_concluido, R.color.white);
        } else {
            holder.ivStatusCheckin.setImageResource(R.drawable.ic_pendente);
            holder.ivStatusCheckin.setColorFilter(ContextCompat.getColor(holder.itemView.getContext(),
                    R.color.laranja_andamento));
            holder.tvCheckinStatus.setText("PENDENTE");
            TelaUtils.pintarTag(holder.tvCheckinStatus, R.color.laranja_andamento, R.color.white);
        }
    }

    @Override
    public int getItemCount() {
        return listaCheckins.size();
    }

    public static class CheckinViewHolder extends RecyclerView.ViewHolder {

        ImageView ivStatusCheckin;
        TextView tvCheckinTitulo;
        TextView tvCheckinStatus;
        TextView tvCheckinEncontro;
        TextView tvCheckinRegistro;

        public CheckinViewHolder(@NonNull View itemView) {
            super(itemView);
            ivStatusCheckin = itemView.findViewById(R.id.ivStatusCheckin);
            tvCheckinTitulo = itemView.findViewById(R.id.tvCheckinTitulo);
            tvCheckinStatus = itemView.findViewById(R.id.tvCheckinStatus);
            tvCheckinEncontro = itemView.findViewById(R.id.tvCheckinEncontro);
            tvCheckinRegistro = itemView.findViewById(R.id.tvCheckinRegistro);
        }
    }
}
