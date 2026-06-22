package com.example.trabalhopratico1;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {

    private static final String NOME_BANCO = "BancoChamados";
    private static final int VERSAO = 2;

    public DBHelper(Context context) {
        super(context, NOME_BANCO, null, VERSAO);
    }

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
                "solucao TEXT, " +
                "caminho_imagem TEXT)";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE chamados ADD COLUMN caminho_imagem TEXT");
            // Migra status antigo para novo padrao
            db.execSQL("UPDATE chamados SET status = 'Em andamento' WHERE status = 'Em Atendimento'");
        }
    }

    public void salvarChamado(Chamado chamado) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues valores = new ContentValues();
        valores.put("titulo", chamado.getTitulo());
        valores.put("local", chamado.getLocal());
        valores.put("tipo", chamado.getTipo());
        valores.put("descricao", chamado.getDescricao());
        valores.put("data", chamado.getData());
        valores.put("status", chamado.getStatus());
        valores.put("caminho_imagem", chamado.getCaminhoImagem());
        db.insert("chamados", null, valores);
        db.close();
    }

    public List<Chamado> listarTodos() {
        List<Chamado> lista = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM chamados", null);

        if (cursor.moveToFirst()) {
            do {
                Chamado c = new Chamado(
                        cursor.getString(cursor.getColumnIndexOrThrow("titulo")),
                        cursor.getString(cursor.getColumnIndexOrThrow("local")),
                        cursor.getString(cursor.getColumnIndexOrThrow("tipo")),
                        cursor.getString(cursor.getColumnIndexOrThrow("descricao")),
                        cursor.getString(cursor.getColumnIndexOrThrow("data"))
                );
                c.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
                c.setStatus(cursor.getString(cursor.getColumnIndexOrThrow("status")));

                String solucao = cursor.getString(cursor.getColumnIndexOrThrow("solucao"));
                if (solucao != null) c.setSolucao(solucao);

                String caminhoImagem = cursor.getString(cursor.getColumnIndexOrThrow("caminho_imagem"));
                if (caminhoImagem != null) c.setCaminhoImagem(caminhoImagem);

                lista.add(c);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return lista;
    }

    public void atualizarChamado(Chamado chamado) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues valores = new ContentValues();
        valores.put("status", chamado.getStatus());
        valores.put("solucao", chamado.getSolucao());
        db.update("chamados", valores, "id=?", new String[]{String.valueOf(chamado.getId())});
        db.close();
    }

    /**
     * Retorna um array com [total, abertos, emAndamento, concluidos]
     * em uma única query.
     */
    public int[] contarEstatisticas() {
        SQLiteDatabase db = this.getReadableDatabase();
        int[] resultado = new int[4];
        Cursor cursor = db.rawQuery(
                "SELECT " +
                "COUNT(*) AS total, " +
                "SUM(CASE WHEN status = 'Aberto' THEN 1 ELSE 0 END) AS abertos, " +
                "SUM(CASE WHEN status = 'Em andamento' THEN 1 ELSE 0 END) AS em_andamento, " +
                "SUM(CASE WHEN status = 'Concluído' THEN 1 ELSE 0 END) AS concluidos " +
                "FROM chamados", null);
        if (cursor.moveToFirst()) {
            resultado[0] = cursor.getInt(0);
            resultado[1] = cursor.getInt(1);
            resultado[2] = cursor.getInt(2);
            resultado[3] = cursor.getInt(3);
        }
        cursor.close();
        db.close();
        return resultado;
    }
}
