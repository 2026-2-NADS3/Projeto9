package com.example.projetodopi.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;

import com.example.projetodopi.model.Notificacao;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

// Acesso à tabela de notificações do aluno
public class NotificacaoDao {

    private final DatabaseHelper helper;

    public NotificacaoDao(Context context) {
        helper = DatabaseHelper.getInstance(context);
    }

    // Salva uma notificação da API. Retorna true se ela é nova (ainda não existia no aparelho).
    public boolean salvarRemota(Notificacao notificacao) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues valores = new ContentValues();
        valores.put("titulo", notificacao.getTitulo());
        valores.put("mensagem", notificacao.getMensagem());
        valores.put("tipo", notificacao.getTipo());
        valores.put("data", notificacao.getData());
        valores.put("curso_id", notificacao.getCursoId());

        int atualizadas = db.update(DatabaseHelper.T_NOTIFICACOES, valores, "remote_id = ?",
                new String[]{String.valueOf(notificacao.getRemoteId())});
        if (atualizadas > 0) {
            return false;
        }
        valores.put("remote_id", notificacao.getRemoteId());
        valores.put("lida", 0);
        db.insert(DatabaseHelper.T_NOTIFICACOES, null, valores);
        return true;
    }

    // Notificação criada pelo próprio app (ex.: presença confirmada)
    public void inserirLocal(String titulo, String mensagem, String tipo, int cursoId) {
        ContentValues valores = new ContentValues();
        valores.put("titulo", titulo);
        valores.put("mensagem", mensagem);
        valores.put("tipo", tipo);
        valores.put("data", new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ROOT).format(new Date()));
        valores.put("curso_id", cursoId);
        valores.put("lida", 0);
        helper.getWritableDatabase().insert(DatabaseHelper.T_NOTIFICACOES, null, valores);
    }

    public List<Notificacao> listar() {
        List<Notificacao> lista = new ArrayList<>();
        Cursor c = helper.getReadableDatabase().query(DatabaseHelper.T_NOTIFICACOES, null, null, null,
                null, null, "data DESC, id DESC");
        try {
            while (c.moveToNext()) {
                Notificacao notificacao = new Notificacao();
                notificacao.setId(DatabaseHelper.longo(c, "id"));
                notificacao.setRemoteId(DatabaseHelper.inteiro(c, "remote_id"));
                notificacao.setTitulo(DatabaseHelper.texto(c, "titulo"));
                notificacao.setMensagem(DatabaseHelper.texto(c, "mensagem"));
                notificacao.setTipo(DatabaseHelper.texto(c, "tipo"));
                notificacao.setData(DatabaseHelper.texto(c, "data"));
                notificacao.setCursoId(DatabaseHelper.inteiro(c, "curso_id"));
                notificacao.setLida(DatabaseHelper.inteiro(c, "lida") == 1);
                lista.add(notificacao);
            }
        } finally {
            c.close();
        }
        return lista;
    }

    public int contarNaoLidas() {
        return (int) DatabaseUtils.queryNumEntries(helper.getReadableDatabase(),
                DatabaseHelper.T_NOTIFICACOES, "lida = 0");
    }

    public void marcarLida(long id) {
        ContentValues valores = new ContentValues();
        valores.put("lida", 1);
        helper.getWritableDatabase().update(DatabaseHelper.T_NOTIFICACOES, valores, "id = ?",
                new String[]{String.valueOf(id)});
    }

    public void marcarTodasLidas() {
        ContentValues valores = new ContentValues();
        valores.put("lida", 1);
        helper.getWritableDatabase().update(DatabaseHelper.T_NOTIFICACOES, valores, null, null);
    }
}
