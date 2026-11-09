package com.example.projetodopi.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projetodopi.R;
import com.example.projetodopi.model.Inscricao;
import com.example.projetodopi.util.DateUtils;

import java.util.List;

// Adapter: lista de certificados (cursos concluídos)
public class CertificadoAdapter extends RecyclerView.Adapter<CertificadoAdapter.CertificadoViewHolder> {

    public interface OnCertificadoClickListener {
        void onCertificadoClick(Inscricao inscricao);
    }

    private final List<Inscricao> concluidas;
    private final OnCertificadoClickListener listener;

    public CertificadoAdapter(List<Inscricao> concluidas, OnCertificadoClickListener listener) {
        this.concluidas = concluidas;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CertificadoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_certificado, parent, false);
        return new CertificadoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CertificadoViewHolder holder, int position) {
        Inscricao inscricao = concluidas.get(position);
        holder.tvCertItemCurso.setText(inscricao.getCurso().getTitulo());
        holder.tvCertItemInfo.setText(inscricao.getCurso().getInstituicao() + " • "
                + inscricao.getCurso().getCargaHoraria() + "h • concluído em "
                + DateUtils.formatarData(inscricao.getDataConclusao()));
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onCertificadoClick(inscricao);
            }
        });
    }

    @Override
    public int getItemCount() {
        return concluidas.size();
    }

    public static class CertificadoViewHolder extends RecyclerView.ViewHolder {

        TextView tvCertItemCurso, tvCertItemInfo;

        public CertificadoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCertItemCurso = itemView.findViewById(R.id.tvCertItemCurso);
            tvCertItemInfo = itemView.findViewById(R.id.tvCertItemInfo);
        }
    }
}
