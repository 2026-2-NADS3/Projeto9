package com.example.projetodopi.util;

import com.example.projetodopi.model.Curso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

// Conversões de data: a API e o banco usam yyyy-MM-dd, a tela usa dd/MM/yyyy
public final class DateUtils {

    private static final Locale PT_BR = Locale.forLanguageTag("pt-BR");
    private static final String[] MESES = {"JAN", "FEV", "MAR", "ABR", "MAI", "JUN",
            "JUL", "AGO", "SET", "OUT", "NOV", "DEZ"};

    private DateUtils() {
    }

    // Data e hora atuais para gravar no banco (yyyy-MM-dd HH:mm:ss)
    public static String agora() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ROOT).format(new Date());
    }

    // Data atual (yyyy-MM-dd)
    public static String hoje() {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.ROOT).format(new Date());
    }

    public static String ano() {
        return new SimpleDateFormat("yyyy", Locale.ROOT).format(new Date());
    }

    private static Date lerData(String isoData) {
        if (isoData == null || isoData.length() < 10) {
            return null;
        }
        try {
            return new SimpleDateFormat("yyyy-MM-dd", Locale.ROOT).parse(isoData.substring(0, 10));
        } catch (ParseException e) {
            return null;
        }
    }

    // "2026-11-12" -> "12/11/2026"
    public static String formatarData(String isoData) {
        Date data = lerData(isoData);
        return data == null ? "" : new SimpleDateFormat("dd/MM/yyyy", PT_BR).format(data);
    }

    // "2026-11-12" -> "Qui, 12/11/2026"
    public static String diaDaSemanaEData(String isoData) {
        Date data = lerData(isoData);
        if (data == null) {
            return "";
        }
        String texto = new SimpleDateFormat("EEE, dd/MM/yyyy", PT_BR).format(data).replace(".", "");
        return texto.substring(0, 1).toUpperCase(PT_BR) + texto.substring(1);
    }

    // "2026-11-12" -> "12"
    public static String dia(String isoData) {
        return isoData != null && isoData.length() >= 10 ? isoData.substring(8, 10) : "--";
    }

    // "2026-11-12" -> "NOV"
    public static String mesAbreviado(String isoData) {
        if (isoData == null || isoData.length() < 7) {
            return "";
        }
        try {
            int mes = Integer.parseInt(isoData.substring(5, 7));
            return MESES[mes - 1];
        } catch (RuntimeException e) {
            return "";
        }
    }

    // "2026-11-08 13:05:00" -> "08/11/2026 às 13:05"
    public static String formatarDataHora(String isoDataHora) {
        if (isoDataHora == null || isoDataHora.length() < 16) {
            return formatarData(isoDataHora);
        }
        return formatarData(isoDataHora) + " às " + isoDataHora.substring(11, 16);
    }

    // "2026-11-08 13:05:00" -> "13:05"
    public static String hora(String isoDataHora) {
        return isoDataHora != null && isoDataHora.length() >= 16 ? isoDataHora.substring(11, 16) : "";
    }

    // Verdadeiro se a data já passou (comparação de texto funciona no formato yyyy-MM-dd)
    public static boolean isPassado(String isoData) {
        return isoData != null && isoData.compareTo(hoje()) < 0;
    }

    // Texto do período do curso, com tratamento especial para curso de dia único
    public static String periodo(Curso curso) {
        if (curso.isDiaUnico()) {
            return diaDaSemanaEData(curso.getDataInicio()) + " • " + curso.getHorario();
        }
        return formatarData(curso.getDataInicio()) + " a " + formatarData(curso.getDataFim());
    }
}
