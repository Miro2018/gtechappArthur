package dev.com.br.gtechapp.persistence;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import dev.com.br.gtechapp.entity.ProdutoInsulina;
import dev.com.br.gtechapp.entity.TipoInsulina;

/**
 * Created by Arthur on 09/07/2018.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "gtechappdb";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {

        // create notes table
        db.execSQL("CREATE TABLE TB_TIPO_INSULINA\n" +
                "(\n" +
                    "COD_TIPO_INSULINA INTEGER PRIMARY KEY,\n" +
                    "NOM_TIPO_INSULINA TEXT NOT NULL\n" +
                ");");

        db.execSQL("CREATE TABLE TB_TIPO_INSULINA_PRODUTO\n" +
                "(\n" +
                    "COD_PRODUTO INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                    "NOM_PRODUTO TEXT NOT NULL,\n" +
                    "COD_TIPO_INSULINA INTEGER NOT NULL\n" +
                ");");

        db.execSQL("INSERT INTO TB_TIPO_INSULINA SELECT 1, 'Ultrarrápida';");
        db.execSQL("INSERT INTO TB_TIPO_INSULINA_PRODUTO (NOM_PRODUTO, COD_TIPO_INSULINA) VALUES ('Apidra (Glulisina)', 1);");
        db.execSQL("INSERT INTO TB_TIPO_INSULINA_PRODUTO (NOM_PRODUTO, COD_TIPO_INSULINA) VALUES ('Humalog (Lispro)', 1);");
        db.execSQL("INSERT INTO TB_TIPO_INSULINA_PRODUTO (NOM_PRODUTO, COD_TIPO_INSULINA) VALUES ('NovoRapid (Asparte)', 1);");

        db.execSQL("INSERT INTO TB_TIPO_INSULINA SELECT 2, 'Rápida';");
        db.execSQL("INSERT INTO TB_TIPO_INSULINA_PRODUTO (NOM_PRODUTO, COD_TIPO_INSULINA) VALUES ('Humulin', 2);");
        db.execSQL("INSERT INTO TB_TIPO_INSULINA_PRODUTO (NOM_PRODUTO, COD_TIPO_INSULINA) VALUES ('Novolin', 2);");

        db.execSQL("INSERT INTO TB_TIPO_INSULINA SELECT 3, 'Ação intermediária';");
        db.execSQL("INSERT INTO TB_TIPO_INSULINA_PRODUTO (NOM_PRODUTO, COD_TIPO_INSULINA) VALUES ('Humulin N', 3);");
        db.execSQL("INSERT INTO TB_TIPO_INSULINA_PRODUTO (NOM_PRODUTO, COD_TIPO_INSULINA) VALUES ('Novolin N', 3);");

        db.execSQL("INSERT INTO TB_TIPO_INSULINA SELECT 4, 'Longa duração';");
        db.execSQL("INSERT INTO TB_TIPO_INSULINA_PRODUTO (NOM_PRODUTO, COD_TIPO_INSULINA) VALUES ('Lantus (Glargina)', 4);");
        db.execSQL("INSERT INTO TB_TIPO_INSULINA_PRODUTO (NOM_PRODUTO, COD_TIPO_INSULINA) VALUES ('Levemir (Detemir)', 4);");
        db.execSQL("INSERT INTO TB_TIPO_INSULINA_PRODUTO (NOM_PRODUTO, COD_TIPO_INSULINA) VALUES ('Tresiba (Degludeca)', 4);");

        db.execSQL("INSERT INTO TB_TIPO_INSULINA SELECT 5, 'Pré-misturada regular';");
        db.execSQL("INSERT INTO TB_TIPO_INSULINA_PRODUTO (NOM_PRODUTO, COD_TIPO_INSULINA) VALUES ('Humulin 70/30', 5);");
        db.execSQL("INSERT INTO TB_TIPO_INSULINA_PRODUTO (NOM_PRODUTO, COD_TIPO_INSULINA) VALUES ('Novolin 70/30', 5);");

        db.execSQL("INSERT INTO TB_TIPO_INSULINA SELECT 6, 'Pré-misturada análoga';");
        db.execSQL("INSERT INTO TB_TIPO_INSULINA_PRODUTO (NOM_PRODUTO, COD_TIPO_INSULINA) VALUES ('NovoMix 30', 6);");
        db.execSQL("INSERT INTO TB_TIPO_INSULINA_PRODUTO (NOM_PRODUTO, COD_TIPO_INSULINA) VALUES ('HumalogMix 25', 6);");
        db.execSQL("INSERT INTO TB_TIPO_INSULINA_PRODUTO (NOM_PRODUTO, COD_TIPO_INSULINA) VALUES ('HumalogMix 50', 6);");
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS TB_TIPO_INSULINA");
        db.execSQL("DROP TABLE IF EXISTS TB_TIPO_INSULINA_PRODUTO");

        // Create tables again
        onCreate(db);
    }

    public List<TipoInsulina> obterTiposInsulina() {
        List<TipoInsulina> listaTiposInsulina = new ArrayList<TipoInsulina>();

        SQLiteDatabase db = this.getReadableDatabase();

        try{
            String selectQuery = "SELECT COD_TIPO_INSULINA, NOM_TIPO_INSULINA FROM TB_TIPO_INSULINA ORDER BY COD_TIPO_INSULINA";
            Cursor c = db.rawQuery(selectQuery, null);

            if (c.moveToFirst()) {
                do {
                    TipoInsulina ti = new TipoInsulina();
                    ti.setCodTipoInsulina(c.getInt(c.getColumnIndex("COD_TIPO_INSULINA")));
                    ti.setNomTipoInsulina(c.getString(c.getColumnIndex("NOM_TIPO_INSULINA")));

                    listaTiposInsulina.add(ti);
                } while (c.moveToNext());
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            db.close();
        }

        return listaTiposInsulina;
    }

    public List<ProdutoInsulina> obterProdutosInsulina(int intCodTipoInsulina) {
        List<ProdutoInsulina> listaProdutosInsulina = new ArrayList<ProdutoInsulina>();

        SQLiteDatabase db = this.getReadableDatabase();

        try{
            String selectQuery = "SELECT COD_PRODUTO, NOM_PRODUTO FROM TB_TIPO_INSULINA_PRODUTO WHERE COD_TIPO_INSULINA = ? ORDER BY COD_PRODUTO";
            Cursor c = db.rawQuery(selectQuery, new String [] {String.valueOf(intCodTipoInsulina)});

            if (c.moveToFirst()) {
                do {
                    ProdutoInsulina p = new ProdutoInsulina();
                    p.setCodProdutoInsulina(c.getInt(c.getColumnIndex("COD_PRODUTO")));
                    p.setNomProdutoInsulina(c.getString(c.getColumnIndex("NOM_PRODUTO")));

                    listaProdutosInsulina.add(p);
                } while (c.moveToNext());
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            db.close();
        }

        return listaProdutosInsulina;
    }

    public String obterNomTipoInsulina(int intCodTipoInsulina) {


        SQLiteDatabase db = this.getReadableDatabase();

        try{

            String selectQuery = "SELECT NOM_TIPO_INSULINA FROM TB_TIPO_INSULINA WHERE COD_TIPO_INSULINA = ?";

            Cursor c = db.rawQuery(selectQuery, new String [] {String.valueOf(intCodTipoInsulina)});

            if (c.moveToFirst()) {
                do {
                    return c.getString(c.getColumnIndex("NOM_TIPO_INSULINA"));

                } while (c.moveToNext());
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            db.close();
        }
        return "";
    }

    public String obterNomProdutoInsulina(int intCodProdutoInsulina) {

        SQLiteDatabase db = this.getReadableDatabase();

        try {
            String selectQuery = "SELECT NOM_PRODUTO FROM TB_TIPO_INSULINA_PRODUTO WHERE COD_PRODUTO = ?";
            Cursor c = db.rawQuery(selectQuery, new String [] {String.valueOf(intCodProdutoInsulina)});

            if (c.moveToFirst()) {
                do {
                    return c.getString(c.getColumnIndex("NOM_PRODUTO"));

                } while (c.moveToNext());
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            db.close();
        }

        return "";
    }
}
