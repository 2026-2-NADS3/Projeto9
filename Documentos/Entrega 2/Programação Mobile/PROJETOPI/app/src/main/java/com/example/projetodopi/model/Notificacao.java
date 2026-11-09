package com.example.projetodopi.model;

// Classe modelo: aviso para o aluno (novo curso, lembrete, presença...)
public class Notificacao {

    public static final String TIPO_NOVO_CURSO = "novo_curso";
    public static final String TIPO_AVISO = "aviso";
    public static final String TIPO_PRESENCA = "presenca";
    public static final String TIPO_MENSAGEM = "mensagem";

    private long id;
    private int remoteId;     // 0 = notificação criada pelo próprio app
    private String titulo;
    private String mensagem;
    private String tipo;
    private String data;      // formato yyyy-MM-dd HH:mm
    private int cursoId;      // curso relacionado (0 = nenhum)
    private boolean lida;

    public Notificacao() {
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public int getRemoteId() { return remoteId; }
    public void setRemoteId(int remoteId) { this.remoteId = remoteId; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getMensagem() { return mensagem; }
    public void setMensagem(String mensagem) { this.mensagem = mensagem; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getData() { return data; }
    public void setData(String data) { this.data = data; }

    public int getCursoId() { return cursoId; }
    public void setCursoId(int cursoId) { this.cursoId = cursoId; }

    public boolean isLida() { return lida; }
    public void setLida(boolean lida) { this.lida = lida; }
}
