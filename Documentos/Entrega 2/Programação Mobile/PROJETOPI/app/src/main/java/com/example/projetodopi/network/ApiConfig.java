package com.example.projetodopi.network;

// Endereço da API REST do projeto (scripts PHP no XAMPP).
// 10.0.2.2 é o "localhost" do computador visto de dentro do emulador.
// Em um celular de verdade, troque pelo IP do computador na rede
// (ex.: http://192.168.0.10/proxima_etapa_api/) em Perfil > Endereço da API.
public final class ApiConfig {

    public static final String URL_PADRAO = "http://10.0.2.2/proxima_etapa_api/";

    // Scripts (endpoints) da API
    public static final String LOGIN = "login.php";
    public static final String PERFIL = "perfil.php";
    public static final String CURSOS = "cursos.php";
    public static final String AGENDA = "agenda.php";
    public static final String INSCRICOES = "inscricoes.php";
    public static final String CHECKINS = "checkins.php";
    public static final String NOTIFICACOES = "notificacoes.php";
    public static final String MENSAGENS = "mensagens.php";

    private ApiConfig() {
    }
}
