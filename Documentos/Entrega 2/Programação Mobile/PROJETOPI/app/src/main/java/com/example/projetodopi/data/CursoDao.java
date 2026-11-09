package com.example.projetodopi.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.projetodopi.model.Curso;

import java.util.ArrayList;
import java.util.List;

// Acesso à tabela de cursos (DAO = Data Access Object)
public class CursoDao {

    private final DatabaseHelper helper;

    public CursoDao(Context context) {
        helper = DatabaseHelper.getInstance(context);
    }

    // Troca todos os cursos pelos que vieram da API
    public void substituirTodos(List<Curso> cursos) {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.beginTransaction();
        try {
            db.delete(DatabaseHelper.T_CURSOS, null, null);
            for (Curso curso : cursos) {
                ContentValues valores = new ContentValues();
                valores.put("id", curso.getId());
                valores.put("titulo", curso.getTitulo());
                valores.put("descricao", curso.getDescricao());
                valores.put("instituicao", curso.getInstituicao());
                valores.put("area", curso.getArea());
                valores.put("carga_horaria", curso.getCargaHoraria());
                valores.put("data_inicio", curso.getDataInicio());
                valores.put("data_fim", curso.getDataFim());
                valores.put("horario", curso.getHorario());
                valores.put("local", curso.getLocal());
                valores.put("imagem", curso.getImagem());
                valores.put("total_aulas", curso.getTotalAulas());
                db.insert(DatabaseHelper.T_CURSOS, null, valores);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public List<Curso> listarTodos() {
        return consultar(null, null);
    }

    public List<Curso> listarPorArea(String area) {
        return consultar("area = ?", new String[]{area});
    }

    public Curso buscarPorId(int id) {
        List<Curso> lista = consultar("id = ?", new String[]{String.valueOf(id)});
        return lista.isEmpty() ? null : lista.get(0);
    }

    private List<Curso> consultar(String filtro, String[] argumentos) {
        List<Curso> lista = new ArrayList<>();
        Cursor c = helper.getReadableDatabase().query(DatabaseHelper.T_CURSOS, null, filtro, argumentos,
                null, null, "data_inicio ASC, titulo ASC");
        try {
            while (c.moveToNext()) {
                lista.add(lerCurso(c));
            }
        } finally {
            c.close();
        }
        return lista;
    }

    static Curso lerCurso(Cursor c) {
        Curso curso = new Curso();
        curso.setId(DatabaseHelper.inteiro(c, "id"));
        curso.setTitulo(DatabaseHelper.texto(c, "titulo"));
        curso.setDescricao(DatabaseHelper.texto(c, "descricao"));
        curso.setInstituicao(DatabaseHelper.texto(c, "instituicao"));
        curso.setArea(DatabaseHelper.texto(c, "area"));
        curso.setCargaHoraria(DatabaseHelper.inteiro(c, "carga_horaria"));
        curso.setDataInicio(DatabaseHelper.texto(c, "data_inicio"));
        curso.setDataFim(DatabaseHelper.texto(c, "data_fim"));
        curso.setHorario(DatabaseHelper.texto(c, "horario"));
        curso.setLocal(DatabaseHelper.texto(c, "local"));
        curso.setImagem(DatabaseHelper.texto(c, "imagem"));
        curso.setTotalAulas(DatabaseHelper.inteiro(c, "total_aulas"));
        return curso;
    }
}
