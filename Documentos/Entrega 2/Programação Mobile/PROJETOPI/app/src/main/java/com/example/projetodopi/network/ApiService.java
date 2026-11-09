package com.example.projetodopi.network;

import android.content.Context;

import com.example.projetodopi.model.Aluno;
import com.example.projetodopi.model.Checkin;
import com.example.projetodopi.model.Curso;
import com.example.projetodopi.model.Inscricao;
import com.example.projetodopi.model.ItemAgenda;
import com.example.projetodopi.model.Mensagem;
import com.example.projetodopi.model.Notificacao;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Métodos de acesso à API REST do projeto (um método para cada script PHP)
public class ApiService {

    private final ApiClient cliente;

    public ApiService(Context context, String urlBase) {
        this.cliente = new ApiClient(context, urlBase);
    }

    // Autenticação: POST login.php (email, senha)
    // Retorna null se e-mail ou senha estiverem errados
    public Aluno login(String email, String senha) throws IOException, JSONException {
        Map<String, String> parametros = new HashMap<>();
        parametros.put("email", email);
        parametros.put("senha", senha);
        JSONObject resposta = new JSONObject(cliente.post(ApiConfig.LOGIN, parametros));
        if (!resposta.optBoolean("success")) {
            return null;
        }
        return JsonParser.lerAluno(resposta.getJSONObject("aluno"));
    }

    // GET cursos.php
    public List<Curso> listarCursos() throws IOException, JSONException {
        return JsonParser.lerCursos(new JSONArray(cliente.get(ApiConfig.CURSOS)));
    }

    // GET agenda.php?alunoId=1 (agenda individual do aluno)
    public List<ItemAgenda> listarAgenda(int alunoId) throws IOException, JSONException {
        return JsonParser.lerAgenda(new JSONArray(cliente.get(ApiConfig.AGENDA + "?alunoId=" + alunoId)));
    }

    // GET inscricoes.php?alunoId=1
    public List<Inscricao> listarInscricoes(int alunoId) throws IOException, JSONException {
        return JsonParser.lerInscricoes(new JSONArray(cliente.get(ApiConfig.INSCRICOES + "?alunoId=" + alunoId)));
    }

    // GET checkins.php?alunoId=1 (histórico de presenças já enviado)
    public List<Checkin> listarCheckins(int alunoId) throws IOException, JSONException {
        return JsonParser.lerCheckins(new JSONArray(cliente.get(ApiConfig.CHECKINS + "?alunoId=" + alunoId)));
    }

    // GET notificacoes.php
    public List<Notificacao> listarNotificacoes() throws IOException, JSONException {
        return JsonParser.lerNotificacoes(new JSONArray(cliente.get(ApiConfig.NOTIFICACOES)));
    }

    // GET mensagens.php?alunoId=1
    public List<Mensagem> listarMensagens(int alunoId) throws IOException, JSONException {
        return JsonParser.lerMensagens(new JSONArray(cliente.get(ApiConfig.MENSAGENS + "?alunoId=" + alunoId)));
    }

    // POST checkins.php -> devolve o id criado no banco
    public int enviarCheckin(int alunoId, Checkin checkin) throws IOException, JSONException {
        return enviar(ApiConfig.CHECKINS, JsonParser.checkinParaParametros(alunoId, checkin));
    }

    // POST mensagens.php
    public int enviarMensagem(int alunoId, Mensagem mensagem) throws IOException, JSONException {
        return enviar(ApiConfig.MENSAGENS, JsonParser.mensagemParaParametros(alunoId, mensagem));
    }

    // POST inscricoes.php (sem id = nova inscrição)
    public int criarInscricao(int alunoId, Inscricao inscricao) throws IOException, JSONException {
        return enviar(ApiConfig.INSCRICOES, JsonParser.inscricaoParaParametros(alunoId, inscricao));
    }

    // POST inscricoes.php (com id = atualiza aulas assistidas e conclusão)
    public void atualizarInscricao(int alunoId, Inscricao inscricao) throws IOException, JSONException {
        Map<String, String> parametros = JsonParser.inscricaoParaParametros(alunoId, inscricao);
        parametros.put("id", String.valueOf(inscricao.getRemoteId()));
        enviar(ApiConfig.INSCRICOES, parametros);
    }

    // POST perfil.php (dados do perfil e resultado do teste)
    public void atualizarPerfil(Aluno aluno) throws IOException, JSONException {
        enviar(ApiConfig.PERFIL, JsonParser.perfilParaParametros(aluno));
    }

    // Envia um POST e confere a resposta {"success": true, "id": 10}
    private int enviar(String caminho, Map<String, String> parametros) throws IOException, JSONException {
        JSONObject resposta = new JSONObject(cliente.post(caminho, parametros));
        if (!resposta.optBoolean("success")) {
            throw new IOException(resposta.optString("message", "A API recusou os dados"));
        }
        return resposta.optInt("id");
    }
}
