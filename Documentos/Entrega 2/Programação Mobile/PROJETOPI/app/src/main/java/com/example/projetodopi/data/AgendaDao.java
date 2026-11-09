package com.example.projetodopi.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.projetodopi.model.ItemAgenda;
import com.example.projetodopi.util.DateUtils;

import java.util.ArrayList;
import java.util.List;

// Acesso à tabela da agenda individual do aluno
public class AgendaDao {

    private final DatabaseHelper helper;

    public AgendaDao(Context context) {
        helper = DatabaseHelper.getInstance(context);
    }

    public void substituirTodos(List<ItemAgenda> itens) {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.beginTransaction();
        try {
            db.delete(DatabaseHelper.T_AGENDA, null, null);
            for (ItemAgenda item : itens) {
                ContentValues valores = new ContentValues();
                valores.put("id", item.getId());
                valores.put("curso_id", item.getCursoId());
                valores.put("titulo", item.getTitulo());
                valores.put("data", item.getData());
                valores.put("horario", item.getHorario());
                valores.put("local", item.getLocal());
                valores.put("codigo_qr", item.getCodigoQr());
                db.insert(DatabaseHelper.T_AGENDA, null, valores);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    // Lista os encontros: primeiro os próximos, depois os que já aconteceram.
    // O JOIN traz se o curso é de dia único e se já existe presença registrada.
    public List<ItemAgenda> listar() {
        String sql = "SELECT a.*, c.data_inicio AS c_inicio, c.data_fim AS c_fim, c.instituicao AS c_instituicao, "
                + "(SELECT COUNT(*) FROM " + DatabaseHelper.T_CHECKINS + " k WHERE k.agenda_id = a.id) AS presencas "
                + "FROM " + DatabaseHelper.T_AGENDA + " a "
                + "LEFT JOIN " + DatabaseHelper.T_CURSOS + " c ON c.id = a.curso_id "
                + "ORDER BY CASE WHEN a.data < ? THEN 1 ELSE 0 END, a.data ASC, a.horario ASC";
        List<ItemAgenda> lista = new ArrayList<>();
        Cursor c = helper.getReadableDatabase().rawQuery(sql, new String[]{DateUtils.hoje()});
        try {
            while (c.moveToNext()) {
                ItemAgenda item = lerItem(c);
                String inicio = DatabaseHelper.texto(c, "c_inicio");
                item.setDiaUnico(!inicio.isEmpty() && inicio.equals(DatabaseHelper.texto(c, "c_fim")));
                item.setInstituicao(DatabaseHelper.texto(c, "c_instituicao"));
                item.setPresencaConfirmada(DatabaseHelper.inteiro(c, "presencas") > 0);
                lista.add(item);
            }
        } finally {
            c.close();
        }
        return lista;
    }

    // Procura o encontro pelo código lido no QR Code
    public ItemAgenda buscarPorCodigo(String codigo) {
        Cursor c = helper.getReadableDatabase().query(DatabaseHelper.T_AGENDA, null,
                "codigo_qr = ? COLLATE NOCASE", new String[]{codigo}, null, null, null);
        try {
            return c.moveToFirst() ? lerItem(c) : null;
        } finally {
            c.close();
        }
    }

    private ItemAgenda lerItem(Cursor c) {
        ItemAgenda item = new ItemAgenda();
        item.setId(DatabaseHelper.inteiro(c, "id"));
        item.setCursoId(DatabaseHelper.inteiro(c, "curso_id"));
        item.setTitulo(DatabaseHelper.texto(c, "titulo"));
        item.setData(DatabaseHelper.texto(c, "data"));
        item.setHorario(DatabaseHelper.texto(c, "horario"));
        item.setLocal(DatabaseHelper.texto(c, "local"));
        item.setCodigoQr(DatabaseHelper.texto(c, "codigo_qr"));
        return item;
    }
}
