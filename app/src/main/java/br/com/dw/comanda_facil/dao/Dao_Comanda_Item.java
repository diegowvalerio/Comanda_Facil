package br.com.dw.comanda_facil.dao;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;

import br.com.dw.comanda_facil.entidades.Comanda_Item;

public class Dao_Comanda_Item extends BaseDaoImpl<Comanda_Item,Integer> {

    public Dao_Comanda_Item(ConnectionSource connectionSource) throws SQLException {
        super(Comanda_Item.class);
        setConnectionSource(connectionSource);
        initialize();
    }
}
