package com.example.projetodopi.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

// Banco de dados local (SQLite): guarda os dados da API para o app funcionar sem internet
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String NOME_BANCO = "proxima_etapa.db";
    private static final int VERSAO = 1;

    public static final String T_CURSOS = "cursos";
    public static final String T_AGENDA = "agenda";
    public static final String T_INSCRICOES = "inscricoes";
    public static final String T_CHECKINS = "checkins";
    public static final String T_NOTIFICACOES = "notificacoes";
    public static final String T_MENSAGENS = "mensagens";

    private static DatabaseHelper instancia;

    // Uma única instância para o app inteiro (padrão Singleton)
    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instancia == null) {
            instancia = new DatabaseHelper(context.getApplicationContext());
        }
        return instancia;
    }

    private DatabaseHelper(Context context) {
        super(context, NOME_BANCO, null, VERSAO);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + T_CURSOS + " ("
                + "id INTEGER PRIMARY KEY, titulo TEXT, descricao TEXT, instituicao TEXT, area TEXT, "
                + "carga_horaria INTEGER, data_inicio TEXT, data_fim TEXT, horario TEXT, local TEXT, "
                + "imagem TEXT, total_aulas INTEGER)");

        db.execSQL("CREATE TABLE " + T_AGENDA + " ("
                + "id INTEGER PRIMARY KEY, curso_id INTEGER, titulo TEXT, data TEXT, horario TEXT, "
                + "local TEXT, codigo_qr TEXT)");

        // sincronizado = 0 indica dado criado/alterado sem internet, aguardando envio para a API
        db.execSQL("CREATE TABLE " + T_INSCRICOES + " ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, remote_id INTEGER, curso_id INTEGER, "
                + "aulas_assistidas INTEGER, total_aulas INTEGER, concluido INTEGER, "
                + "data_conclusao TEXT, codigo_certificado TEXT, sincronizado INTEGER)");

        db.execSQL("CREATE TABLE " + T_CHECKINS + " ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, remote_id INTEGER, agenda_id INTEGER, "
                + "codigo_qr TEXT, data_hora TEXT, sincronizado INTEGER)");

        db.execSQL("CREATE TABLE " + T_NOTIFICACOES + " ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, remote_id INTEGER UNIQUE, titulo TEXT, "
                + "mensagem TEXT, tipo TEXT, data TEXT, curso_id INTEGER, lida INTEGER)");

        db.execSQL("CREATE TABLE " + T_MENSAGENS + " ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, remote_id INTEGER UNIQUE, texto TEXT, "
                + "autor TEXT, data_hora TEXT, sincronizado INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int versaoAntiga, int versaoNova) {
        String[] tabelas = {T_CURSOS, T_AGENDA, T_INSCRICOES, T_CHECKINS, T_NOTIFICACOES, T_MENSAGENS};
        for (String tabela : tabelas) {
            db.execSQL("DROP TABLE IF EXISTS " + tabela);
        }
        onCreate(db);
    }

    // Apaga os dados ao sair da conta (outro aluno pode usar o mesmo aparelho)
    public void limparTudo() {
        SQLiteDatabase db = getWritableDatabase();
        String[] tabelas = {T_CURSOS, T_AGENDA, T_INSCRICOES, T_CHECKINS, T_NOTIFICACOES, T_MENSAGENS};
        for (String tabela : tabelas) {
            db.delete(tabela, null, null);
        }
    }

    // Ajudantes para ler colunas do Cursor pelo nome
    static String texto(Cursor c, String coluna) {
        int indice = c.getColumnIndexOrThrow(coluna);
        return c.isNull(indice) ? "" : c.getString(indice);
    }

    static int inteiro(Cursor c, String coluna) {
        int indice = c.getColumnIndexOrThrow(coluna);
        return c.isNull(indice) ? 0 : c.getInt(indice);
    }

    static long longo(Cursor c, String coluna) {
        int indice = c.getColumnIndexOrThrow(coluna);
        return c.isNull(indice) ? 0 : c.getLong(indice);
    }
}
