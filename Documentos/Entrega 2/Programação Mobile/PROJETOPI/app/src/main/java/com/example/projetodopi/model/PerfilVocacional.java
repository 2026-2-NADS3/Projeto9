package com.example.projetodopi.model;

// Áreas usadas no teste de perfil e na classificação dos cursos
public final class PerfilVocacional {

    public static final String ADM = "ADM";
    public static final String FIN = "FIN";
    public static final String COM = "COM";
    public static final String TEC = "TEC";

    private PerfilVocacional() {
    }

    public static String nome(String area) {
        if (ADM.equals(area)) return "Administração e Gestão";
        if (FIN.equals(area)) return "Contábil e Financeira";
        if (COM.equals(area)) return "Comunicação e Publicidade";
        if (TEC.equals(area)) return "Tecnologia e Inovação";
        return "Ainda não definido";
    }

    public static String descricao(String area) {
        if (ADM.equals(area)) {
            return "Você gosta de organizar, planejar e liderar pessoas. Cursos de gestão, "
                    + "empreendedorismo e liderança combinam com o seu jeito.";
        }
        if (FIN.equals(area)) {
            return "Você tem facilidade com números e gosta de tomar decisões com base em dados. "
                    + "Carreiras em contabilidade, finanças e economia são um bom caminho.";
        }
        if (COM.equals(area)) {
            return "Você se expressa bem, é criativo e gosta de se relacionar. Publicidade, "
                    + "marketing, jornalismo e relações públicas combinam com você.";
        }
        if (TEC.equals(area)) {
            return "Você é curioso, gosta de resolver problemas e de entender como as coisas "
                    + "funcionam. Tecnologia, dados e inovação são áreas para explorar.";
        }
        return "Faça o teste de perfil para descobrir as áreas que combinam com você.";
    }
}
