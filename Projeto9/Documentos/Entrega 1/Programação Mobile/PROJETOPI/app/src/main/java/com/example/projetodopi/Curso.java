package com.example.projetodopi;

// Classe modelo: representa um curso do aluno
public class Curso {

    private String titulo;
    private String descricao;
    private int cargaHoraria; // dado numérico: carga horária em horas
    private int imagem;       // imagem do curso (pasta drawable)

    public Curso(String titulo, String descricao, int cargaHoraria, int imagem) {
        this.titulo = titulo;
        this.descricao = descricao;
        this.cargaHoraria = cargaHoraria;
        this.imagem = imagem;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public int getCargaHoraria() {
        return cargaHoraria;
    }

    public int getImagem() {
        return imagem;
    }
}