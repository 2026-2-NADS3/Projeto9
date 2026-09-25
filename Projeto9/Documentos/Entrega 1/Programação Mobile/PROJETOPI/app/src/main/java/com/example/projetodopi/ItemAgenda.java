package com.example.projetodopi;

// Classe modelo: representa um encontro na agenda do aluno
public class ItemAgenda {

    private String data;
    private String horario;
    private String disciplina;
    private String local;

    public ItemAgenda(String data, String horario, String disciplina, String local) {
        this.data = data;
        this.horario = horario;
        this.disciplina = disciplina;
        this.local = local;
    }

    public String getData() {
        return data;
    }

    public String getHorario() {
        return horario;
    }

    public String getDisciplina() {
        return disciplina;
    }

    public String getLocal() {
        return local;
    }
}