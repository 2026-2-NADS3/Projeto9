package com.example.projetodopi.model;

// Classe modelo: registro de presença feito pela leitura do QR Code
public class Checkin {

    private long id;              // id no banco local
    private int remoteId;         // id na API (0 = pendente de envio)
    private int agendaId;
    private String codigoQr;
    private String dataHora;      // formato yyyy-MM-dd HH:mm:ss
    private boolean sincronizado;

    // Dados do encontro (JOIN com agenda)
    private String tituloEncontro;
    private String dataEncontro;
    private String horarioEncontro;

    public Checkin() {
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public int getRemoteId() { return remoteId; }
    public void setRemoteId(int remoteId) { this.remoteId = remoteId; }

    public int getAgendaId() { return agendaId; }
    public void setAgendaId(int agendaId) { this.agendaId = agendaId; }

    public String getCodigoQr() { return codigoQr; }
    public void setCodigoQr(String codigoQr) { this.codigoQr = codigoQr; }

    public String getDataHora() { return dataHora; }
    public void setDataHora(String dataHora) { this.dataHora = dataHora; }

    public boolean isSincronizado() { return sincronizado; }
    public void setSincronizado(boolean sincronizado) { this.sincronizado = sincronizado; }

    public String getTituloEncontro() { return tituloEncontro; }
    public void setTituloEncontro(String tituloEncontro) { this.tituloEncontro = tituloEncontro; }

    public String getDataEncontro() { return dataEncontro; }
    public void setDataEncontro(String dataEncontro) { this.dataEncontro = dataEncontro; }

    public String getHorarioEncontro() { return horarioEncontro; }
    public void setHorarioEncontro(String horarioEncontro) { this.horarioEncontro = horarioEncontro; }
}
