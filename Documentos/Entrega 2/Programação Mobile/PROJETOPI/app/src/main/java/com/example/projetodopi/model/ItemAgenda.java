package com.example.projetodopi.model;

// Classe modelo: um encontro (aula/palestra) na agenda individual do aluno
public class ItemAgenda {

    private int id;
    private int cursoId;
    private String titulo;
    private String data;        // formato yyyy-MM-dd
    private String horario;
    private String local;
    private String codigoQr;    // código que o QR Code do encontro carrega

    // Dados extras vindos do JOIN com cursos e check-ins
    private boolean diaUnico;
    private boolean presencaConfirmada;
    private String instituicao;

    public ItemAgenda() {
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getCursoId() { return cursoId; }
    public void setCursoId(int cursoId) { this.cursoId = cursoId; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }


    public String getDisciplina() { return titulo; }

    public String getData() { return data; }
    public void setData(String data) { this.data = data; }

    public String getHorario() { return horario; }
    public void setHorario(String horario) { this.horario = horario; }

    public String getLocal() { return local; }
    public void setLocal(String local) { this.local = local; }

    public String getCodigoQr() { return codigoQr; }
    public void setCodigoQr(String codigoQr) { this.codigoQr = codigoQr; }

    public boolean isDiaUnico() { return diaUnico; }
    public void setDiaUnico(boolean diaUnico) { this.diaUnico = diaUnico; }

    public boolean isPresencaConfirmada() { return presencaConfirmada; }
    public void setPresencaConfirmada(boolean presencaConfirmada) { this.presencaConfirmada = presencaConfirmada; }

    public String getInstituicao() { return instituicao; }
    public void setInstituicao(String instituicao) { this.instituicao = instituicao; }
}
