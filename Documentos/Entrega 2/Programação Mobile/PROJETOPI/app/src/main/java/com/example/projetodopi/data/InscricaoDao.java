package com.example.projetodopi.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.projetodopi.model.Curso;
import com.example.projetodopi.model.Inscricao;
import com.example.projetodopi.util.DateUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

// Acesso à tabela de inscrições (progresso do aluno em cada curso)
public class InscricaoDao {

    private final DatabaseHelper helper;

    public InscricaoDao(Context context) {
        helper = DatabaseHelper.getInstance(context);
    }

    // Recebe as inscrições da API. As alterações locais ainda não enviadas são preservadas.
    public void substituirSincronizadas(List<Inscricao> remotas) {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.beginTransaction();
        try {
            db.delete(DatabaseHelper.T_INSCRICOES, "sincronizado = 1", null);
            for (Inscricao inscricao : remotas) {
                if (buscarPorCurso(db, inscricao.getCursoId()) != null) {
                    continue; // existe uma versão local pendente deste curso
                }
                ContentValues valores = paraValores(inscricao);
                valores.put("remote_id", inscricao.getRemoteId());
                valores.put("sincronizado", 1);
                db.insert(DatabaseHelper.T_INSCRICOES, null, valores);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    // Inscrição feita pelo aluno no app (fica pendente até ser enviada para a API)
    public void inscreverLocal(Curso curso) {
        Inscricao inscricao = new Inscricao();
        inscricao.setCursoId(curso.getId());
        inscricao.setTotalAulas(Math.max(curso.getTotalAulas(), 1));
        ContentValues valores = paraValores(inscricao);
        valores.put("sincronizado", 0);
        helper.getWritableDatabase().insert(DatabaseHelper.T_INSCRICOES, null, valores);
    }

    // Soma uma aula assistida após o check-in. Retorna true se o curso acabou de ser concluído.
    public boolean registrarAula(int cursoId) {
        SQLiteDatabase db = helper.getWritableDatabase();
        Inscricao inscricao = buscarPorCurso(db, cursoId);
        if (inscricao == null) {
            return false;
        }
        int total = Math.max(inscricao.getTotalAulas(), 1);
        int aulas = Math.min(inscricao.getAulasAssistidas() + 1, total);
        boolean concluiuAgora = !inscricao.isConcluido() && aulas >= total;

        ContentValues valores = new ContentValues();
        valores.put("aulas_assistidas", aulas);
        valores.put("sincronizado", 0);
        if (concluiuAgora) {
            valores.put("concluido", 1);
            valores.put("data_conclusao", DateUtils.hoje());
            valores.put("codigo_certificado", gerarCodigoCertificado(cursoId));
        }
        db.update(DatabaseHelper.T_INSCRICOES, valores, "id = ?", new String[]{String.valueOf(inscricao.getId())});
        return concluiuAgora;
    }

    public List<Inscricao> listar() {
        return consultar(null, null, "i.concluido ASC, c.data_inicio ASC");
    }

    public List<Inscricao> listarConcluidas() {
        return consultar("i.concluido = 1", null, "i.data_conclusao DESC");
    }

    public List<Inscricao> listarPendentes() {
        return consultar("i.sincronizado = 0", null, "i.id ASC");
    }

    public Inscricao buscarPorCurso(int cursoId) {
        List<Inscricao> lista = consultar("i.curso_id = ?", new String[]{String.valueOf(cursoId)}, null);
        return lista.isEmpty() ? null : lista.get(0);
    }

    // Mapa cursoId -> inscrição (para marcar "inscrito"/"concluído" na lista de cursos)
    public Map<Integer, Inscricao> mapaPorCurso() {
        Map<Integer, Inscricao> mapa = new HashMap<>();
        for (Inscricao inscricao : listar()) {
            mapa.put(inscricao.getCursoId(), inscricao);
        }
        return mapa;
    }

    public void marcarSincronizada(long idLocal, int remoteId) {
        ContentValues valores = new ContentValues();
        valores.put("remote_id", remoteId);
        valores.put("sincronizado", 1);
        helper.getWritableDatabase().update(DatabaseHelper.T_INSCRICOES, valores, "id = ?",
                new String[]{String.valueOf(idLocal)});
    }

    // ---------- Totais para o perfil e o card ----------

    public int contarConcluidas() {
        return listarConcluidas().size();
    }

    public int somarHorasConcluidas() {
        int horas = 0;
        for (Inscricao inscricao : listarConcluidas()) {
            if (inscricao.getCurso() != null) {
                horas += inscricao.getCurso().getCargaHoraria();
            }
        }
        return horas;
    }

    // ---------- Auxiliares ----------

    private Inscricao buscarPorCurso(SQLiteDatabase db, int cursoId) {
        Cursor c = db.query(DatabaseHelper.T_INSCRICOES, null, "curso_id = ?",
                new String[]{String.valueOf(cursoId)}, null, null, null);
        try {
            return c.moveToFirst() ? lerInscricao(c) : null;
        } finally {
            c.close();
        }
    }

    private List<Inscricao> consultar(String filtro, String[] argumentos, String ordem) {
        String sql = "SELECT i.*, c.titulo AS c_titulo, c.instituicao AS c_instituicao, c.area AS c_area, "
                + "c.carga_horaria AS c_carga, c.data_inicio AS c_inicio, c.data_fim AS c_fim, "
                + "c.horario AS c_horario, c.imagem AS c_imagem "
                + "FROM " + DatabaseHelper.T_INSCRICOES + " i "
                + "LEFT JOIN " + DatabaseHelper.T_CURSOS + " c ON c.id = i.curso_id"
                + (filtro != null ? " WHERE " + filtro : "")
                + (ordem != null ? " ORDER BY " + ordem : "");
        List<Inscricao> lista = new ArrayList<>();
        Cursor c = helper.getReadableDatabase().rawQuery(sql, argumentos);
        try {
            while (c.moveToNext()) {
                Inscricao inscricao = lerInscricao(c);
                Curso curso = new Curso();
                curso.setId(inscricao.getCursoId());
                curso.setTitulo(DatabaseHelper.texto(c, "c_titulo"));
                curso.setInstituicao(DatabaseHelper.texto(c, "c_instituicao"));
                curso.setArea(DatabaseHelper.texto(c, "c_area"));
                curso.setCargaHoraria(DatabaseHelper.inteiro(c, "c_carga"));
                curso.setDataInicio(DatabaseHelper.texto(c, "c_inicio"));
                curso.setDataFim(DatabaseHelper.texto(c, "c_fim"));
                curso.setHorario(DatabaseHelper.texto(c, "c_horario"));
                curso.setImagem(DatabaseHelper.texto(c, "c_imagem"));
                inscricao.setCurso(curso);
                lista.add(inscricao);
            }
        } finally {
            c.close();
        }
        return lista;
    }

    private Inscricao lerInscricao(Cursor c) {
        Inscricao inscricao = new Inscricao();
        inscricao.setId(DatabaseHelper.longo(c, "id"));
        inscricao.setRemoteId(DatabaseHelper.inteiro(c, "remote_id"));
        inscricao.setCursoId(DatabaseHelper.inteiro(c, "curso_id"));
        inscricao.setAulasAssistidas(DatabaseHelper.inteiro(c, "aulas_assistidas"));
        inscricao.setTotalAulas(DatabaseHelper.inteiro(c, "total_aulas"));
        inscricao.setConcluido(DatabaseHelper.inteiro(c, "concluido") == 1);
        inscricao.setDataConclusao(DatabaseHelper.texto(c, "data_conclusao"));
        inscricao.setCodigoCertificado(DatabaseHelper.texto(c, "codigo_certificado"));
        inscricao.setSincronizado(DatabaseHelper.inteiro(c, "sincronizado") == 1);
        return inscricao;
    }

    private ContentValues paraValores(Inscricao inscricao) {
        ContentValues valores = new ContentValues();
        valores.put("curso_id", inscricao.getCursoId());
        valores.put("aulas_assistidas", inscricao.getAulasAssistidas());
        valores.put("total_aulas", inscricao.getTotalAulas());
        valores.put("concluido", inscricao.isConcluido() ? 1 : 0);
        valores.put("data_conclusao", inscricao.getDataConclusao());
        valores.put("codigo_certificado", inscricao.getCodigoCertificado());
        return valores;
    }

    private String gerarCodigoCertificado(int cursoId) {
        long sufixo = System.currentTimeMillis() % 10000;
        return String.format(Locale.ROOT, "PE-%s-%03d%04d", DateUtils.ano(), cursoId, sufixo);
    }
}
