package br.com.dw.comanda_facil.banco;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

import br.com.dw.comanda_facil.entidades.Comanda;
import br.com.dw.comanda_facil.entidades.Comanda_Item;
import br.com.dw.comanda_facil.entidades.Mesa;
import br.com.dw.comanda_facil.entidades.Produto;

public class DatabaseHelper extends OrmLiteSqliteOpenHelper {
    private static final String databaseName = "BD.db";
    private static final Integer databaseVersion = 7;

    public DatabaseHelper(Context context) {
            super(context, databaseName, null, databaseVersion);
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try{
            database.execSQL("PRAGMA foreign_keys = ON");
            //cria as tabelas
            TableUtils.createTableIfNotExists(connectionSource, Produto.class);
            TableUtils.createTableIfNotExists(connectionSource, Mesa.class);
            TableUtils.createTableIfNotExists(connectionSource, Comanda.class);
            TableUtils.createTableIfNotExists(connectionSource, Comanda_Item.class);

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        //excluir tabelas e gerar novamente
        try {
            if(oldVersion == 6) {
                TableUtils.dropTable(connectionSource, Comanda_Item.class, true);
                TableUtils.dropTable(connectionSource, Comanda.class, true);
            }else {
                TableUtils.dropTable(connectionSource, Produto.class, true);
                TableUtils.dropTable(connectionSource, Mesa.class, true);
            }

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
