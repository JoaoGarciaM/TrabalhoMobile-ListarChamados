package com.example.trabalhopratico1;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {

    // Nome e versão do banco
    private static final String NOME_BANCO = "BancoChamados";
    private static final int VERSAO = 1;

    public DBHelper(Context context) {
        super(context, NOME_BANCO, null, VERSAO);
    }

    // Cria a tabela na primeira vez que o app roda
    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE chamados (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "titulo TEXT, " +
                "local TEXT, " +
                "tipo TEXT, " +
                "descricao TEXT, " +
                "data TEXT, " +
                "status TEXT, " +
                "solucao TEXT)";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS chamados");
        onCreate(db);
    }

    // 1. Salvar novo chamado
    public void salvarChamado(Chamado chamado) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues valores = new ContentValues();

        valores.put("titulo", chamado.getTitulo());
        valores.put("local", chamado.getLocal());
        valores.put("tipo", chamado.getTipo());
        valores.put("descricao", chamado.getDescricao());
        valores.put("data", chamado.getData());
        valores.put("status", chamado.getStatus());
        // Solução começa vazia, então não precisa colocar no insert

        db.insert("chamados", null, valores);
        db.close();
    }

    // 2. Listar todos os chamados
    public List<Chamado> listarTodos() {
        List<Chamado> lista = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM chamados", null);

        if (cursor.moveToFirst()) {
            do {
                // Remonta o objeto Chamado a partir da linha do banco
                Chamado c = new Chamado(
                        cursor.getString(cursor.getColumnIndexOrThrow("titulo")),
                        cursor.getString(cursor.getColumnIndexOrThrow("local")),
                        cursor.getString(cursor.getColumnIndexOrThrow("tipo")),
                        cursor.getString(cursor.getColumnIndexOrThrow("descricao")),
                        cursor.getString(cursor.getColumnIndexOrThrow("data"))
                );

                c.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
                c.setStatus(cursor.getString(cursor.getColumnIndexOrThrow("status")));

                // Pega a solução (se existir)
                String solucao = cursor.getString(cursor.getColumnIndexOrThrow("solucao"));
                if (solucao != null) {
                    c.setSolucao(solucao);
                }

                lista.add(c);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return lista;
    }

    // 3. Atualizar um chamado (Status e Solução)
    public void atualizarChamado(Chamado chamado) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues valores = new ContentValues();

        valores.put("status", chamado.getStatus());
        valores.put("solucao", chamado.getSolucao());

        // Atualiza onde o ID for igual ao ID do nosso objeto
        db.update("chamados", valores, "id=?", new String[]{String.valueOf(chamado.getId())});
        db.close();
    }
}