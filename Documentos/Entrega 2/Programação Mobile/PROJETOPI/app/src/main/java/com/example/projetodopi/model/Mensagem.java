package com.example.projetodopi.model;

// Classe modelo: mensagem do chat entre o aluno e a equipe da ONG
public class Mensagem {

    public static final String AUTOR_ALUNO = "aluno";
    public static final String AUTOR_ONG = "ong";

    private long id;
    private int remoteId;      // 0 = ainda não enviada para a API
    private String texto;
    private String autor;
    private String dataHora;   // formato yyyy-MM-dd HH:mm:ss
    private boolean sincronizado;

    public Mensagem() {
    }

    public boolean isDoAluno() {
        return AUTOR_ALUNO.equals(autor);
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public int getRemoteId() { return remoteId; }
    public void setRemoteId(int remoteId) { this.remoteId = remoteId; }

    public String getTexto() { return texto; }
    public void setTexto(String texto) { this.texto = texto; }

    public String getAutor() { return autor; }
    public void setAutor(String autor) { this.autor = autor; }

    public String getDataHora() { return dataHora; }
    public void setDataHora(String dataHora) { this.dataHora = dataHora; }

    public boolean isSincronizado() { return sincronizado; }
    public void setSincronizado(boolean sincronizado) { this.sincronizado = sincronizado; }
}
