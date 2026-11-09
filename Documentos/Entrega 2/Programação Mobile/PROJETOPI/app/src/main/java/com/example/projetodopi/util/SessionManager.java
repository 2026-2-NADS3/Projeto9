package com.example.projetodopi.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.projetodopi.model.Aluno;
import com.example.projetodopi.network.ApiConfig;

// Guarda a sessão do aluno e as configurações no SharedPreferences
public class SessionManager {

    private static final String ARQUIVO = "sessao_proxima_etapa";

    private static final String LOGADO = "logado";
    private static final String ALUNO_ID = "aluno_id";
    private static final String NOME = "nome";
    private static final String EMAIL = "email";
    private static final String TELEFONE = "telefone";
    private static final String ESCOLA = "escola";
    private static final String SERIE = "serie";
    private static final String PERFIL = "perfil_vocacional";
    private static final String FOTO = "foto_path";
    private static final String MODO_OFFLINE = "modo_offline";
    private static final String ULTIMA_SYNC = "ultima_sincronizacao";
    private static final String PERFIL_PENDENTE = "perfil_pendente";
    private static final String NOTIFICACOES_ATIVAS = "notificacoes_ativas";
    private static final String API_URL = "api_url";
    private static final String AVALIACAO = "avaliacao_app";

    private final SharedPreferences prefs;

    public SessionManager(Context context) {
        prefs = context.getApplicationContext().getSharedPreferences(ARQUIVO, Context.MODE_PRIVATE);
    }

    // ---------- Sessão ----------

    public void iniciarSessao(Aluno aluno, boolean offline) {
        prefs.edit()
                .putBoolean(LOGADO, true)
                .putBoolean(MODO_OFFLINE, offline)
                .putString(ULTIMA_SYNC, "")
                .putBoolean(PERFIL_PENDENTE, false)
                .apply();
        salvarAluno(aluno);
    }

    public void encerrarSessao() {
        String apiUrl = getApiUrl();
        prefs.edit().clear().putString(API_URL, apiUrl).apply();
    }

    public boolean isLogado() {
        return prefs.getBoolean(LOGADO, false);
    }

    // ---------- Dados do aluno ----------

    public void salvarAluno(Aluno aluno) {
        prefs.edit()
                .putInt(ALUNO_ID, aluno.getId())
                .putString(NOME, aluno.getNome())
                .putString(EMAIL, aluno.getEmail())
                .putString(TELEFONE, aluno.getTelefone())
                .putString(ESCOLA, aluno.getEscola())
                .putString(SERIE, aluno.getSerie())
                .putString(PERFIL, aluno.getPerfilVocacional())
                .apply();
    }

    public Aluno getAluno() {
        Aluno aluno = new Aluno();
        aluno.setId(prefs.getInt(ALUNO_ID, 0));
        aluno.setNome(prefs.getString(NOME, ""));
        aluno.setEmail(prefs.getString(EMAIL, ""));
        aluno.setTelefone(prefs.getString(TELEFONE, ""));
        aluno.setEscola(prefs.getString(ESCOLA, ""));
        aluno.setSerie(prefs.getString(SERIE, ""));
        aluno.setPerfilVocacional(prefs.getString(PERFIL, ""));
        return aluno;
    }

    public int getAlunoId() {
        return prefs.getInt(ALUNO_ID, 0);
    }

    public String getFotoPath() {
        return prefs.getString(FOTO, "");
    }

    public void setFotoPath(String caminho) {
        prefs.edit().putString(FOTO, caminho).apply();
    }

    // ---------- Sincronização ----------

    public boolean isModoOffline() {
        return prefs.getBoolean(MODO_OFFLINE, false);
    }

    public void setModoOffline(boolean offline) {
        prefs.edit().putBoolean(MODO_OFFLINE, offline).apply();
    }

    public String getUltimaSincronizacao() {
        return prefs.getString(ULTIMA_SYNC, "");
    }

    public void setUltimaSincronizacao(String dataHora) {
        prefs.edit().putString(ULTIMA_SYNC, dataHora).apply();
    }

    // Perfil alterado sem internet: será enviado na próxima sincronização
    public boolean isPerfilPendente() {
        return prefs.getBoolean(PERFIL_PENDENTE, false);
    }

    public void setPerfilPendente(boolean pendente) {
        prefs.edit().putBoolean(PERFIL_PENDENTE, pendente).apply();
    }

    // ---------- Configurações ----------

    public boolean isNotificacoesAtivas() {
        return prefs.getBoolean(NOTIFICACOES_ATIVAS, true);
    }

    public void setNotificacoesAtivas(boolean ativas) {
        prefs.edit().putBoolean(NOTIFICACOES_ATIVAS, ativas).apply();
    }

    public String getApiUrl() {
        return prefs.getString(API_URL, ApiConfig.URL_PADRAO);
    }

    public void setApiUrl(String url) {
        prefs.edit().putString(API_URL, url).apply();
    }

    public float getAvaliacao() {
        return prefs.getFloat(AVALIACAO, 0f);
    }

    public void setAvaliacao(float nota) {
        prefs.edit().putFloat(AVALIACAO, nota).apply();
    }
}
