package com.example.projetodopi.model;

// Classe modelo: inscrição do aluno em um curso, com o progresso de participação
public class Inscricao {

    private long id;                 // id no banco local (SQLite)
    private int remoteId;            // id na API (0 = ainda não enviada)
    private int cursoId;
    private int aulasAssistidas;     // dado numérico: encontros com presença
    private int totalAulas;
    private boolean concluido;
    private String dataConclusao;    // formato yyyy-MM-dd
    private String codigoCertificado;
    private boolean sincronizado;

    private Curso curso;             // dados do curso (JOIN)

    public Inscricao() {
    }

    // Percentual de participação (0 a 100)
    public int getPercentual() {
        if (totalAulas <= 0) {
            return 0;
        }
        return Math.min(100, aulasAssistidas * 100 / totalAulas);
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public int getRemoteId() { return remoteId; }
    public void setRemoteId(int remoteId) { this.remoteId = remoteId; }

    public int getCursoId() { return cursoId; }
    public void setCursoId(int cursoId) { this.cursoId = cursoId; }

    public int getAulasAssistidas() { return aulasAssistidas; }
    public void setAulasAssistidas(int aulasAssistidas) { this.aulasAssistidas = aulasAssistidas; }

    public int getTotalAulas() { return totalAulas; }
    public void setTotalAulas(int totalAulas) { this.totalAulas = totalAulas; }

    public boolean isConcluido() { return concluido; }
    public void setConcluido(boolean concluido) { this.concluido = concluido; }

    public String getDataConclusao() { return dataConclusao; }
    public void setDataConclusao(String dataConclusao) { this.dataConclusao = dataConclusao; }

    public String getCodigoCertificado() { return codigoCertificado; }
    public void setCodigoCertificado(String codigoCertificado) { this.codigoCertificado = codigoCertificado; }

    public boolean isSincronizado() { return sincronizado; }
    public void setSincronizado(boolean sincronizado) { this.sincronizado = sincronizado; }

    public Curso getCurso() { return curso; }
    public void setCurso(Curso curso) { this.curso = curso; }
}
