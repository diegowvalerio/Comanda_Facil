package br.com.dw.comanda_facil.telas;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import br.com.dw.comanda_facil.R;
import br.com.dw.comanda_facil.adapters.Adp_Comanda;
import br.com.dw.comanda_facil.banco.DatabaseHelper;
import br.com.dw.comanda_facil.dao.Dao_Comanda;
import br.com.dw.comanda_facil.dao.Dao_Mesa;
import br.com.dw.comanda_facil.entidades.Comanda;
import br.com.dw.comanda_facil.entidades.Mesa;
import br.com.dw.comanda_facil.telas.comanda.Comandas_Mesa;
import br.com.dw.comanda_facil.telas.mesa.Mesas;
import br.com.dw.comanda_facil.telas.produto.Produtos;
import br.com.dw.comanda_facil.telas.produto.TelaProduto;
import br.com.dw.comanda_facil.util.Util;

public class Principal extends AppCompatActivity implements AdapterView.OnItemClickListener {
    GridView gridView;
    DatabaseHelper banco;
    List<Mesa> mesas = new ArrayList<>();
    Dao_Mesa dao_mesa;
    Dao_Comanda dao_comanda;
    List<Comanda> comadas = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);

        gridView = findViewById(R.id.gridview);
        gridView.setOnItemClickListener(this);
    }

    public void tela_produtos(View view) throws IOException, InterruptedException {
        if(Util.isOnline()) {
            Intent intent = new Intent(this, Produtos.class);
            startActivity(intent);
        }else{
            Toast.makeText(this, "Nescessário acesso a internet ! ", Toast.LENGTH_SHORT).show();
        }
    }

    public void tela_mesas(View view) throws IOException, InterruptedException {
        if(Util.isOnline()) {
            Intent intent = new Intent(this, Mesas.class);
            startActivity(intent);
        }else{
            Toast.makeText(this, "Nescessário acesso a internet ! ", Toast.LENGTH_SHORT).show();
        }

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
            dao_comanda = new Dao_Comanda(banco.getConnectionSource());

            mesas = dao_mesa.queryBuilder().where().eq("status",true).query();
            Object[] status = {"ABERTO","PARCIAL","ATENDIDO"};
            for(Mesa m:mesas){
                comadas.clear();
                comadas = dao_comanda.queryBuilder().where().eq("mesa",m.getId()).and().in("status",status).query();
                m.setTotalcomandas(comadas.size());
            }
            gridView.setAdapter(new Adp_Comanda(this,mesas));

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Mesa mesa = (Mesa) parent.getItemAtPosition(position);
        Intent intent = new Intent(this, Comandas_Mesa.class);
        intent.putExtra("id",mesa.getId());
        startActivity(intent);
        Toast.makeText(this, "Selecionado: "+mesa.getDescricao(), Toast.LENGTH_SHORT).show();
    }
}