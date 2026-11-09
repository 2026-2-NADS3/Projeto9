package com.example.projetodopi.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;

import com.example.projetodopi.model.Mensagem;

import java.util.ArrayList;
import java.util.List;

// Acesso à tabela de mensagens (chat com a ONG)
public class MensagemDao {

    private final DatabaseHelper helper;

    public MensagemDao(Context context) {
        helper = DatabaseHelper.getInstance(context);
    }

    // Mensagem escrita pelo aluno: pendente até ser enviada para a API
    public void inserirLocal(String texto, String autor, String dataHora) {
        ContentValues valores = new ContentValues();
        valores.put("texto", texto);
        valores.put("autor", autor);
        valores.put("data_hora", dataHora);
        valores.put("sincronizado", 0);
        helper.getWritableDatabase().insert(DatabaseHelper.T_MENSAGENS, null, valores);
    }

    // Mensagem vinda da API (resposta da ONG ou mensagem já enviada antes)
    public void salvarRemotaSeNaoExistir(Mensagem mensagem) {
        long existentes = DatabaseUtils.queryNumEntries(helper.getReadableDatabase(),
                DatabaseHelper.T_MENSAGENS, "remote_id = ?", new String[]{String.valueOf(mensagem.getRemoteId())});
        if (existentes > 0) {
            return;
        }
        ContentValues valores = new ContentValues();
        valores.put("remote_id", mensagem.getRemoteId());
        valores.put("texto", mensagem.getTexto());
        valores.put("autor", mensagem.getAutor());
        valores.put("data_hora", mensagem.getDataHora());
        valores.put("sincronizado", 1);
        helper.getWritableDatabase().insert(DatabaseHelper.T_MENSAGENS, null, valores);
    }

    public List<Mensagem> listar() {
        return consultar(null);
    }

    public List<Mensagem> listarPendentes() {
        return consultar("sincronizado = 0");
    }

    public void marcarSincronizada(long idLocal, int remoteId) {
        ContentValues valores = new ContentValues();
        valores.put("remote_id", remoteId);
        valores.put("sincronizado", 1);
        helper.getWritableDatabase().update(DatabaseHelper.T_MENSAGENS, valores, "id = ?",
                new String[]{String.valueOf(idLocal)});
    }

    private List<Mensagem> consultar(String filtro) {
        List<Mensagem> lista = new ArrayList<>();
        Cursor c = helper.getReadableDatabase().query(DatabaseHelper.T_MENSAGENS, null, filtro, null,
                null, null, "data_hora ASC, id ASC");
        try {
            while (c.moveToNext()) {
                Mensagem mensagem = new Mensagem();
                mensagem.setId(DatabaseHelper.longo(c, "id"));
                mensagem.setRemoteId(DatabaseHelper.inteiro(c, "remote_id"));
                mensagem.setTexto(DatabaseHelper.texto(c, "texto"));
                mensagem.setAutor(DatabaseHelper.texto(c, "autor"));
                mensagem.setDataHora(DatabaseHelper.texto(c, "data_hora"));
                mensagem.setSincronizado(DatabaseHelper.inteiro(c, "sincronizado") == 1);
                lista.add(mensagem);
            }
        } finally {
            c.close();
        }
        return lista;
    }
}
