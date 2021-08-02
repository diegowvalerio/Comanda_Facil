package br.com.dw.comanda_facil.dao;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;

import br.com.dw.comanda_facil.entidades.Produto;

public class Dao_Produto extends BaseDaoImpl<Produto,Integer> {

    public Dao_Produto(ConnectionSource connectionSource) throws SQLException {
        super(Produto.class);
        setConnectionSource(connectionSource);
        initialize();
    }
}
