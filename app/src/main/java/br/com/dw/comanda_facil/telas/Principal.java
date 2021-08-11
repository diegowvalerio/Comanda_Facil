package br.com.dw.comanda_facil.telas;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.GridView;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import br.com.dw.comanda_facil.R;
import br.com.dw.comanda_facil.adapters.Adp_Comanda;
import br.com.dw.comanda_facil.banco.DatabaseHelper;
import br.com.dw.comanda_facil.dao.Dao_Mesa;
import br.com.dw.comanda_facil.entidades.Mesa;
import br.com.dw.comanda_facil.telas.mesa.Mesas;
import br.com.dw.comanda_facil.telas.produto.Produtos;

public class Principal extends AppCompatActivity {
    GridView gridView;
    DatabaseHelper banco;
    List<Mesa> mesas = new ArrayList<>();
    Dao_Mesa dao_mesa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);

        gridView = findViewById(R.id.gridview);
    }

    public void tela_produtos(View view){
        Intent intent = new Intent(this, Produtos.class);
        startActivity(intent);
    }

    public void tela_mesas(View view){
        Intent intent = new Intent(this, Mesas.class);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        preenchelista();
    }

    private void preenchelista() {

        banco = new DatabaseHelper(this);
        try {
            dao_mesa = new Dao_Mesa(banco.getConnectionSource());
            mesas = dao_mesa.queryBuilder().where().eq("status",true).query();
            gridView.setAdapter(new Adp_Comanda(this,mesas));

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}