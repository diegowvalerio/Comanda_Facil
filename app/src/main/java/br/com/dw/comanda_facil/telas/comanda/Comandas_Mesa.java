package br.com.dw.comanda_facil.telas.comanda;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import br.com.dw.comanda_facil.R;
import br.com.dw.comanda_facil.adapters.Adp_ComandasMesas;
import br.com.dw.comanda_facil.banco.DatabaseHelper;
import br.com.dw.comanda_facil.dao.Dao_Comanda;
import br.com.dw.comanda_facil.dao.Dao_Mesa;
import br.com.dw.comanda_facil.entidades.Comanda;
import br.com.dw.comanda_facil.entidades.Mesa;
import br.com.dw.comanda_facil.telas.mesa.TelaMesa;

public class Comandas_Mesa extends AppCompatActivity implements AdapterView.OnItemClickListener {
    DatabaseHelper banco;
    Dao_Comanda dao_comanda;
    Dao_Mesa dao_mesa;
    Mesa mesa;
    TextView mesaselecionada;
    List<Comanda> comandas = new ArrayList<>();
    int idmesa;
    ListView listView;
    Adp_ComandasMesas adp_comandasMesas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comanda);

        mesaselecionada = findViewById(R.id.mesaselecionada);
        listView = findViewById(R.id.listvew_pedidos);
        listView.setOnItemClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        preenche();
    }

    public void comanda_pedido(View view){
        Intent intent = new Intent(this, Comanda_Pedido.class);
        intent.putExtra("id",idmesa);
        startActivity(intent);
    }

    private void preenche() {
        //busca comandas em aberto,parcial ,atendido da mesa selecionada.

        Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.containsKey("id")) {
            idmesa = bundle.getInt("id");
            banco = new DatabaseHelper(this);
            try {
                dao_mesa = new Dao_Mesa(banco.getConnectionSource());
                mesa = dao_mesa.queryForId(idmesa);
                mesaselecionada.setText(mesa.getDescricao());

                String[] status = {"ABERTO","PARCIAL","ATENDIDO"};
                dao_comanda = new Dao_Comanda(banco.getConnectionSource());
                comandas = dao_comanda.queryBuilder().where().eq("mesa",idmesa).and().in("status",status).query();
                adp_comandasMesas = new Adp_ComandasMesas(this,comandas);
                listView.setAdapter(adp_comandasMesas);

            } catch (SQLException e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Comanda comanda = (Comanda) parent.getItemAtPosition(position);
        Intent intent = new Intent(this, Comanda_Pedido.class);
        intent.putExtra("idcomanda",comanda.getId());
        startActivity(intent);
    }
}