package br.com.dw.comanda_facil.dao;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;

import br.com.dw.comanda_facil.entidades.Mesa;

public class Dao_Mesa extends BaseDaoImpl<Mesa,Integer> {

    public Dao_Mesa(ConnectionSource connectionSource) throws SQLException {
        super(Mesa.class);
        setConnectionSource(connectionSource);
        initialize();
    }
}
