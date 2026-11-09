package com.example.projetodopi.model;

// Classe modelo: dados pessoais e acadêmicos do aluno logado
public class Aluno {

    private int id;
    private String nome;
    private String email;
    private String telefone;
    private String escola;
    private String serie;
    private String perfilVocacional;   // resultado do teste de perfil (ADM, FIN, COM, TEC)

    public Aluno() {
    }

    public String getPrimeiroNome() {
        if (nome == null || nome.trim().isEmpty()) {
            return "aluno";
        }
        return nome.trim().split(" ")[0];
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }

    public String getEscola() { return escola; }
    public void setEscola(String escola) { this.escola = escola; }

    public String getSerie() { return serie; }
    public void setSerie(String serie) { this.serie = serie; }

    public String getPerfilVocacional() { return perfilVocacional; }
    public void setPerfilVocacional(String perfilVocacional) { this.perfilVocacional = perfilVocacional; }
}
