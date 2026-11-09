package com.example.projetodopi.model;

// Classe modelo: curso oferecido por uma instituição parceira da Próxima Etapa
public class Curso {

    private int id;
    private String titulo;
    private String descricao;
    private String instituicao;
    private String area;          // área do teste de perfil (ADM, FIN, COM, TEC)
    private int cargaHoraria;     // dado numérico: carga horária em horas
    private String dataInicio;    // formato yyyy-MM-dd
    private String dataFim;       // formato yyyy-MM-dd
    private String horario;
    private String local;
    private String imagem;        // nome da imagem na pasta drawable (ex.: curso1)
    private int totalAulas;       // dado numérico: número de encontros

    public Curso() {
    }

    // Curso de dia único: começa e termina na mesma data
    public boolean isDiaUnico() {
        return dataInicio != null && dataInicio.equals(dataFim);
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public String getInstituicao() { return instituicao; }
    public void setInstituicao(String instituicao) { this.instituicao = instituicao; }

    public String getArea() { return area; }
    public void setArea(String area) { this.area = area; }

    public int getCargaHoraria() { return cargaHoraria; }
    public void setCargaHoraria(int cargaHoraria) { this.cargaHoraria = cargaHoraria; }

    public String getDataInicio() { return dataInicio; }
    public void setDataInicio(String dataInicio) { this.dataInicio = dataInicio; }

    public String getDataFim() { return dataFim; }
    public void setDataFim(String dataFim) { this.dataFim = dataFim; }

    public String getHorario() { return horario; }
    public void setHorario(String horario) { this.horario = horario; }

    public String getLocal() { return local; }
    public void setLocal(String local) { this.local = local; }

    public String getImagem() { return imagem; }
    public void setImagem(String imagem) { this.imagem = imagem; }

    public int getTotalAulas() { return totalAulas; }
    public void setTotalAulas(int totalAulas) { this.totalAulas = totalAulas; }
}
