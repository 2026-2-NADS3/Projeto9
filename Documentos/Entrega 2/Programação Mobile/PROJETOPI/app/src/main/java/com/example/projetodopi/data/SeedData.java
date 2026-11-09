package com.example.projetodopi.data;

import android.content.Context;

import com.example.projetodopi.model.Aluno;
import com.example.projetodopi.model.Checkin;
import com.example.projetodopi.model.Mensagem;
import com.example.projetodopi.model.Notificacao;
import com.example.projetodopi.network.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

// Dados de demonstração (assets/dados_iniciais.json, mesmo formato do db.json da API).
// São usados quando a API não está disponível, para o app funcionar offline.
public final class SeedData {

    private static final String ARQUIVO = "dados_iniciais.json";

    private SeedData() {
    }

    // Login offline: confere e-mail e senha com a conta de demonstração
    public static Aluno buscarAluno(Context context, String email, String senha) {
        try {
            JSONArray alunos = lerArquivo(context).getJSONArray("alunos");
            for (int i = 0; i < alunos.length(); i++) {
                JSONObject json = alunos.getJSONObject(i);
                if (json.optString("email").equalsIgnoreCase(email) && json.optString("senha").equals(senha)) {
                    return JsonParser.lerAluno(json);
                }
            }
        } catch (IOException | JSONException e) {
            return null;
        }
        return null;
    }

    // Copia os dados de demonstração do aluno para o banco local
    public static void carregar(Context context, int alunoId) throws IOException, JSONException {
        JSONObject dados = lerArquivo(context);

        new CursoDao(context).substituirTodos(JsonParser.lerCursos(dados.getJSONArray("cursos")));
        new AgendaDao(context).substituirTodos(JsonParser.lerAgenda(
                JsonParser.filtrarPorAluno(dados.getJSONArray("agenda"), alunoId)));
        new InscricaoDao(context).substituirSincronizadas(JsonParser.lerInscricoes(
                JsonParser.filtrarPorAluno(dados.getJSONArray("inscricoes"), alunoId)));

        CheckinDao checkinDao = new CheckinDao(context);
        for (Checkin checkin : JsonParser.lerCheckins(
                JsonParser.filtrarPorAluno(dados.getJSONArray("checkins"), alunoId))) {
            checkinDao.inserirRemotoSeNaoExistir(checkin);
        }

        NotificacaoDao notificacaoDao = new NotificacaoDao(context);
        for (Notificacao notificacao : JsonParser.lerNotificacoes(dados.getJSONArray("notificacoes"))) {
            notificacaoDao.salvarRemota(notificacao);
        }

        MensagemDao mensagemDao = new MensagemDao(context);
        for (Mensagem mensagem : JsonParser.lerMensagens(
                JsonParser.filtrarPorAluno(dados.getJSONArray("mensagens"), alunoId))) {
            mensagemDao.salvarRemotaSeNaoExistir(mensagem);
        }
    }

    private static JSONObject lerArquivo(Context context) throws IOException, JSONException {
        StringBuilder texto = new StringBuilder();
        try (BufferedReader leitor = new BufferedReader(new InputStreamReader(
                context.getAssets().open(ARQUIVO), StandardCharsets.UTF_8))) {
            String linha;
            while ((linha = leitor.readLine()) != null) {
                texto.append(linha);
            }
        }
        return new JSONObject(texto.toString());
    }
}
