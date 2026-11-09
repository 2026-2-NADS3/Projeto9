package com.example.projetodopi.network;

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Tratamento de JSON: converte as respostas da API em objetos Java
// e prepara os parâmetros enviados nos POSTs
public final class JsonParser {

    private JsonParser() {
    }

    // ---------- JSON -> objetos ----------

    public static Aluno lerAluno(JSONObject json) {
        Aluno aluno = new Aluno();
        aluno.setId(json.optInt("id"));
        aluno.setNome(json.optString("nome"));
        aluno.setEmail(json.optString("email"));
        aluno.setTelefone(json.optString("telefone"));
        aluno.setEscola(json.optString("escola"));
        aluno.setSerie(json.optString("serie"));
        aluno.setPerfilVocacional(json.optString("perfilVocacional"));
        return aluno;
    }

    public static List<Curso> lerCursos(JSONArray array) throws JSONException {
        List<Curso> lista = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            JSONObject json = array.getJSONObject(i);
            Curso curso = new Curso();
            curso.setId(json.getInt("id"));
            curso.setTitulo(json.optString("titulo"));
            curso.setDescricao(json.optString("descricao"));
            curso.setInstituicao(json.optString("instituicao"));
            curso.setArea(json.optString("area"));
            curso.setCargaHoraria(json.optInt("cargaHoraria"));
            curso.setDataInicio(json.optString("dataInicio"));
            // Se a API não enviar dataFim, o curso é de dia único
            curso.setDataFim(json.optString("dataFim", curso.getDataInicio()));
            curso.setHorario(json.optString("horario"));
            curso.setLocal(json.optString("local"));
            curso.setImagem(json.optString("imagem"));
            curso.setTotalAulas(json.optInt("totalAulas", 1));
            lista.add(curso);
        }
        return lista;
    }

    public static List<ItemAgenda> lerAgenda(JSONArray array) throws JSONException {
        List<ItemAgenda> lista = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            JSONObject json = array.getJSONObject(i);
            ItemAgenda item = new ItemAgenda();
            item.setId(json.getInt("id"));
            item.setCursoId(json.optInt("cursoId"));
            item.setTitulo(json.optString("titulo"));
            item.setData(json.optString("data"));
            item.setHorario(json.optString("horario"));
            item.setLocal(json.optString("local"));
            item.setCodigoQr(json.optString("codigoQr"));
            lista.add(item);
        }
        return lista;
    }

    public static List<Inscricao> lerInscricoes(JSONArray array) throws JSONException {
        List<Inscricao> lista = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            JSONObject json = array.getJSONObject(i);
            Inscricao inscricao = new Inscricao();
            inscricao.setRemoteId(json.getInt("id"));
            inscricao.setCursoId(json.optInt("cursoId"));
            inscricao.setAulasAssistidas(json.optInt("aulasAssistidas"));
            inscricao.setTotalAulas(json.optInt("totalAulas", 1));
            inscricao.setConcluido(json.optBoolean("concluido"));
            inscricao.setDataConclusao(json.optString("dataConclusao"));
            inscricao.setCodigoCertificado(json.optString("codigoCertificado"));
            inscricao.setSincronizado(true);
            lista.add(inscricao);
        }
        return lista;
    }

    public static List<Checkin> lerCheckins(JSONArray array) throws JSONException {
        List<Checkin> lista = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            JSONObject json = array.getJSONObject(i);
            Checkin checkin = new Checkin();
            checkin.setRemoteId(json.getInt("id"));
            checkin.setAgendaId(json.optInt("agendaId"));
            checkin.setCodigoQr(json.optString("codigoQr"));
            checkin.setDataHora(json.optString("dataHora"));
            checkin.setSincronizado(true);
            lista.add(checkin);
        }
        return lista;
    }

    public static List<Notificacao> lerNotificacoes(JSONArray array) throws JSONException {
        List<Notificacao> lista = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            JSONObject json = array.getJSONObject(i);
            Notificacao notificacao = new Notificacao();
            notificacao.setRemoteId(json.getInt("id"));
            notificacao.setTitulo(json.optString("titulo"));
            notificacao.setMensagem(json.optString("mensagem"));
            notificacao.setTipo(json.optString("tipo", Notificacao.TIPO_AVISO));
            notificacao.setData(json.optString("data"));
            notificacao.setCursoId(json.optInt("cursoId"));
            lista.add(notificacao);
        }
        return lista;
    }

    public static List<Mensagem> lerMensagens(JSONArray array) throws JSONException {
        List<Mensagem> lista = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            JSONObject json = array.getJSONObject(i);
            Mensagem mensagem = new Mensagem();
            mensagem.setRemoteId(json.getInt("id"));
            mensagem.setTexto(json.optString("texto"));
            mensagem.setAutor(json.optString("autor", Mensagem.AUTOR_ONG));
            mensagem.setDataHora(json.optString("dataHora"));
            mensagem.setSincronizado(true);
            lista.add(mensagem);
        }
        return lista;
    }

    // Mantém só os itens de um aluno (usado nos dados de demonstração offline)
    public static JSONArray filtrarPorAluno(JSONArray array, int alunoId) throws JSONException {
        JSONArray filtrado = new JSONArray();
        for (int i = 0; i < array.length(); i++) {
            JSONObject json = array.getJSONObject(i);
            if (json.optInt("alunoId") == alunoId) {
                filtrado.put(json);
            }
        }
        return filtrado;
    }

    // ---------- objetos -> parâmetros do POST (envio para a API) ----------

    public static Map<String, String> checkinParaParametros(int alunoId, Checkin checkin) {
        Map<String, String> parametros = new HashMap<>();
        parametros.put("alunoId", String.valueOf(alunoId));
        parametros.put("agendaId", String.valueOf(checkin.getAgendaId()));
        parametros.put("codigoQr", checkin.getCodigoQr());
        parametros.put("dataHora", checkin.getDataHora());
        return parametros;
    }

    public static Map<String, String> mensagemParaParametros(int alunoId, Mensagem mensagem) {
        Map<String, String> parametros = new HashMap<>();
        parametros.put("alunoId", String.valueOf(alunoId));
        parametros.put("texto", mensagem.getTexto());
        parametros.put("autor", mensagem.getAutor());
        parametros.put("dataHora", mensagem.getDataHora());
        return parametros;
    }

    public static Map<String, String> inscricaoParaParametros(int alunoId, Inscricao inscricao) {
        Map<String, String> parametros = new HashMap<>();
        parametros.put("alunoId", String.valueOf(alunoId));
        parametros.put("cursoId", String.valueOf(inscricao.getCursoId()));
        parametros.put("aulasAssistidas", String.valueOf(inscricao.getAulasAssistidas()));
        parametros.put("totalAulas", String.valueOf(inscricao.getTotalAulas()));
        parametros.put("concluido", inscricao.isConcluido() ? "1" : "0");
        parametros.put("dataConclusao", inscricao.getDataConclusao());
        parametros.put("codigoCertificado", inscricao.getCodigoCertificado());
        return parametros;
    }

    public static Map<String, String> perfilParaParametros(Aluno aluno) {
        Map<String, String> parametros = new HashMap<>();
        parametros.put("id", String.valueOf(aluno.getId()));
        parametros.put("nome", aluno.getNome());
        parametros.put("telefone", aluno.getTelefone());
        parametros.put("escola", aluno.getEscola());
        parametros.put("serie", aluno.getSerie());
        parametros.put("perfilVocacional", aluno.getPerfilVocacional());
        return parametros;
    }
}
