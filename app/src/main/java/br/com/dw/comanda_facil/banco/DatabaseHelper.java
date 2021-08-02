package br.com.dw.comanda_facil.banco;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

import br.com.dw.comanda_facil.entidades.Produto;

public class DatabaseHelper extends OrmLiteSqliteOpenHelper {
    private static final String databaseName = "BD.db";
    private static final Integer databaseVersion = 1;

    public DatabaseHelper(Context context) {
            super(context, databaseName, null, databaseVersion);
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try{
            database.execSQL("PRAGMA foreign_keys = ON");
            //cria as tabelas
            TableUtils.createTable(connectionSource, Produto.class);

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        //excluir tabelas e gerar novamente
        try {
            TableUtils.dropTable(connectionSource, Produto.class,true);

            onCreate(database, connectionSource);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close(){
        super.close();
    }
}
