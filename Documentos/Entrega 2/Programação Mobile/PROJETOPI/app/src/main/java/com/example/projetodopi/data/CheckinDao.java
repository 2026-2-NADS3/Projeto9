package com.example.projetodopi.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;

import com.example.projetodopi.model.Checkin;

import java.util.ArrayList;
import java.util.List;

// Acesso à tabela de check-ins (presenças registradas pelo QR Code)
public class CheckinDao {

    private final DatabaseHelper helper;

    public CheckinDao(Context context) {
        helper = DatabaseHelper.getInstance(context);
    }

    // Presença registrada no aparelho: fica pendente (sincronizado = 0) até ir para a API
    public long inserir(int agendaId, String codigoQr, String dataHora) {
        ContentValues valores = new ContentValues();
        valores.put("agenda_id", agendaId);
        valores.put("codigo_qr", codigoQr);
        valores.put("data_hora", dataHora);
        valores.put("sincronizado", 0);
        return helper.getWritableDatabase().insert(DatabaseHelper.T_CHECKINS, null, valores);
    }

    // Presença que já está na API (histórico baixado na sincronização)
    public void inserirRemotoSeNaoExistir(Checkin checkin) {
        SQLiteDatabase db = helper.getWritableDatabase();
        long existentes = DatabaseUtils.queryNumEntries(db, DatabaseHelper.T_CHECKINS,
                "remote_id = ? OR agenda_id = ?",
                new String[]{String.valueOf(checkin.getRemoteId()), String.valueOf(checkin.getAgendaId())});
        if (existentes > 0) {
            return;
        }
        ContentValues valores = new ContentValues();
        valores.put("remote_id", checkin.getRemoteId());
        valores.put("agenda_id", checkin.getAgendaId());
        valores.put("codigo_qr", checkin.getCodigoQr());
        valores.put("data_hora", checkin.getDataHora());
        valores.put("sincronizado", 1);
        db.insert(DatabaseHelper.T_CHECKINS, null, valores);
    }

    public boolean existeParaAgenda(int agendaId) {
        return DatabaseUtils.queryNumEntries(helper.getReadableDatabase(), DatabaseHelper.T_CHECKINS,
                "agenda_id = ?", new String[]{String.valueOf(agendaId)}) > 0;
    }

    public void marcarSincronizado(long idLocal, int remoteId) {
        ContentValues valores = new ContentValues();
        valores.put("remote_id", remoteId);
        valores.put("sincronizado", 1);
        helper.getWritableDatabase().update(DatabaseHelper.T_CHECKINS, valores, "id = ?",
                new String[]{String.valueOf(idLocal)});
    }

    // Histórico completo (limite = 0) ou só os últimos registros
    public List<Checkin> listar(int limite) {
        return consultar(null, limite);
    }

    public List<Checkin> listarPendentes() {
        return consultar("k.sincronizado = 0", 0);
    }

    public int contar() {
        return (int) DatabaseUtils.queryNumEntries(helper.getReadableDatabase(), DatabaseHelper.T_CHECKINS);
    }

    public int contarPendentes() {
        return (int) DatabaseUtils.queryNumEntries(helper.getReadableDatabase(), DatabaseHelper.T_CHECKINS,
                "sincronizado = 0");
    }

    private List<Checkin> consultar(String filtro, int limite) {
        String sql = "SELECT k.*, a.titulo AS a_titulo, a.data AS a_data, a.horario AS a_horario "
                + "FROM " + DatabaseHelper.T_CHECKINS + " k "
                + "LEFT JOIN " + DatabaseHelper.T_AGENDA + " a ON a.id = k.agenda_id"
                + (filtro != null ? " WHERE " + filtro : "")
                + " ORDER BY k.data_hora DESC"
                + (limite > 0 ? " LIMIT " + limite : "");
        List<Checkin> lista = new ArrayList<>();
        Cursor c = helper.getReadableDatabase().rawQuery(sql, null);
        try {
            while (c.moveToNext()) {
                Checkin checkin = new Checkin();
                checkin.setId(DatabaseHelper.longo(c, "id"));
                checkin.setRemoteId(DatabaseHelper.inteiro(c, "remote_id"));
                checkin.setAgendaId(DatabaseHelper.inteiro(c, "agenda_id"));
                checkin.setCodigoQr(DatabaseHelper.texto(c, "codigo_qr"));
                checkin.setDataHora(DatabaseHelper.texto(c, "data_hora"));
                checkin.setSincronizado(DatabaseHelper.inteiro(c, "sincronizado") == 1);
                checkin.setTituloEncontro(DatabaseHelper.texto(c, "a_titulo"));
                checkin.setDataEncontro(DatabaseHelper.texto(c, "a_data"));
                checkin.setHorarioEncontro(DatabaseHelper.texto(c, "a_horario"));
                lista.add(checkin);
            }
        } finally {
            c.close();
        }
        return lista;
    }
}
