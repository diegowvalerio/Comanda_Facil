package br.com.dw.comanda_facil.telas.comanda;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.com.dw.comanda_facil.R;
import br.com.dw.comanda_facil.adapters.Adp_ComandaItem;
import br.com.dw.comanda_facil.banco.DatabaseHelper;
import br.com.dw.comanda_facil.dao.Dao_Comanda;
import br.com.dw.comanda_facil.dao.Dao_Comanda_Item;
import br.com.dw.comanda_facil.dao.Dao_Mesa;
import br.com.dw.comanda_facil.dao.Dao_Produto;
import br.com.dw.comanda_facil.entidades.Comanda;
import br.com.dw.comanda_facil.entidades.Comanda_Item;
import br.com.dw.comanda_facil.entidades.Produto;
import br.com.dw.comanda_facil.telas.produto.Produtos;

public class Comanda_Pedido extends AppCompatActivity {

    EditText cliente,qtdepessoas,dataabertura;
    TextView vltotal,vlpago,vltroco,vldesconto,vlrecebido;
    ListView listView;

    DatabaseHelper banco;
    Comanda comanda = new Comanda();
    Comanda_Item comanda_item;
    List<Comanda_Item> comanda_itens = new ArrayList<>();
    Dao_Comanda dao_comanda;
    Dao_Comanda_Item dao_comanda_item;
    Dao_Mesa dao_mesa;
    Dao_Produto dao_produto;
    Adp_ComandaItem adp_comandaItem;
    Produto produto;

    Date d = new Date();
    int idmesa =0;
    double total,pago,troco,desconto,recebido;
    int v = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comanda__pedido);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm");

        cliente = findViewById(R.id.c_cliente);
        qtdepessoas = findViewById(R.id.c_qtdepessoas);
        dataabertura = findViewById(R.id.c_dataabertura);
        dataabertura.setText(sdf.format(d));
        dataabertura.setEnabled(false);

        vltotal = findViewById(R.id.c_vltotal);
        vltotal.setText("0");
        vlpago = findViewById(R.id.c_vlpago);
        vlpago.setText("0");
        vltroco = findViewById(R.id.c_vltroco);
        vltroco.setText("0");
        vldesconto = findViewById(R.id.c_vldesconto);
        vldesconto.setText("0");
        vlrecebido = findViewById(R.id.c_vlrecebido);
        vlrecebido.setText("0");

        listView = findViewById(R.id.listview_itens);

        banco = new DatabaseHelper(this);
        try {
            dao_comanda = new Dao_Comanda(banco.getConnectionSource());
            dao_comanda_item = new Dao_Comanda_Item(banco.getConnectionSource());
            dao_mesa = new Dao_Mesa(banco.getConnectionSource());
            dao_produto = new Dao_Produto(banco.getConnectionSource());
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.containsKey("id")) {
            idmesa = bundle.getInt("id");
        }
    }

    public void tela_produtos(View view) throws ParseException {
        salvarparaadicionaritens();
        Intent intent = new Intent(this, Comanda_Produto.class);
        startActivity(intent);
        v = 1;
    }

    @Override
    protected void onStart() {
        super.onStart();
        preenche_itens();
        pegaproduto();
    }

    private void pegaproduto() {
        final DecimalFormat df = new DecimalFormat("#,###.00");
        if(v==1){
            SharedPreferences lt = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            SharedPreferences.Editor editor = lt.edit();
            String t = lt.getString("idproduto","vazio");
            if (!t.equals("vazio")) {
                try {
                    produto = new Produto();
                    produto = dao_produto.queryForId(Integer.parseInt(t));

                    AlertDialog.Builder mensagem = new AlertDialog.Builder(this);
                    mensagem.setTitle(produto.getDescricao());
                    mensagem.setMessage("Digite a quantidade:");
                    // DECLARACAO DO EDITTEXT
                    final EditText input = new EditText(this);
                    input.setInputType(InputType.TYPE_CLASS_NUMBER);
                    mensagem.setView(input);
                    mensagem.setPositiveButton("Adicionar", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            if(input.getText().length()>0) {
                                try {
                                    comanda_item = new Comanda_Item();
                                    comanda_item.setComanda(comanda);
                                    comanda_item.setProduto(produto);
                                    comanda_item.setQtde(Integer.parseInt(input.getText().toString()));
                                    comanda_item.setValor_unitario(produto.getValor());
                                    double total = produto.getValor() * comanda_item.getQtde();
                                    df.format(total);
                                    comanda_item.setValor_total(total);
                                    comanda_item.setStatus("ABERTO");
                                    comanda_item.setData_pedido(new Date());
                                    dao_comanda_item.createOrUpdate(comanda_item);
                                    calculatotal();
                                    dao_comanda.createOrUpdate(comanda);
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }
                            }else{
                                Toast.makeText(Comanda_Pedido.this, "Quantidade invalida !", Toast.LENGTH_SHORT).show();
                            }
                        }

                    });
                    mensagem.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    mensagem.show();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }else {
                Toast.makeText(this, "Produto invalido", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void preenche_itens() {
        DecimalFormat df = new DecimalFormat("R$ #,###.00");

        if(v == 0) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm");
            Bundle bundle = getIntent().getExtras();
            if (bundle != null && bundle.containsKey("idcomanda")) {
                try {
                    comanda = dao_comanda.queryForId(bundle.getInt("idcomanda"));
                    comanda_itens = dao_comanda_item.queryBuilder().where().eq("comanda", comanda.getId()).query();
                    cliente.setText(comanda.getCliente());
                    qtdepessoas.setText(comanda.getQtde_pessoas().toString());
                    dataabertura.setText(sdf.format(comanda.getData_abertura()));
                    vltotal.setText(df.format(comanda.getValor_total()));

                    adp_comandaItem = new Adp_ComandaItem(this, comanda_itens);
                    listView.setAdapter(adp_comandaItem);
                    idmesa = comanda.getMesa().getId();
                    calculatotal();
                } catch (SQLException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    public void salvarcomanda(View view) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm");
        if(cliente.getText().length() > 0 && qtdepessoas.getText().length()>0){
            try {
                comanda.setCliente(cliente.getText().toString().toUpperCase());
                comanda.setQtde_pessoas(Integer.parseInt(qtdepessoas.getText().toString()));
                comanda.setMesa(dao_mesa.queryForId(idmesa));
                d = sdf.parse(String.valueOf(dataabertura.getText()));
                comanda.setData_abertura(d);
                calculatotal();
                dao_comanda.createOrUpdate(comanda);
                finish();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }else{
            Toast.makeText(this, "Preencha os dados minimos !", Toast.LENGTH_SHORT).show();
        }
    }

    public void salvarparaadicionaritens(){
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm");
        if(cliente.getText().length() > 0 && qtdepessoas.getText().length()>0){
            try {
                comanda.setCliente(cliente.getText().toString().toUpperCase());
                comanda.setQtde_pessoas(Integer.parseInt(qtdepessoas.getText().toString()));
                comanda.setMesa(dao_mesa.queryForId(idmesa));
                d = sdf.parse(String.valueOf(dataabertura.getText()));
                comanda.setData_abertura(d);
                calculatotal();
                dao_comanda.createOrUpdate(comanda);
            } catch (SQLException | ParseException e) {
                e.printStackTrace();
            }
        }else{
            Toast.makeText(this, "Preencha os dados minimos !", Toast.LENGTH_SHORT).show();
        }
    }

    public void calculatotal(){
        int totalitens =0,totalaberto =0,totalparcial = 0,totalatendido = 0;
        DecimalFormat df = new DecimalFormat("R$ #,###.00");

        total = 0;
        if(comanda.getId() != null) {
            try {
                comanda_itens = dao_comanda_item.queryBuilder().where().eq("comanda", comanda.getId()).query();
                if (comanda_itens.size() > 0) {
                    for (Comanda_Item item : comanda_itens) {
                        total = total + item.getValor_total();
                        if(item.getStatus().equals("PARCIAL")) {
                            totalparcial ++;
                        }
                        if(item.getStatus().equals("ATENDIDO")) {
                            totalatendido ++;
                        }
                        if(item.getStatus().equals("ABERTO")) {
                            totalaberto ++;
                        }
                        totalitens++;
                    }
                    //df.format(total);
                    vltotal.setText(df.format(total));
                    comanda.setValor_total(total);
                }
                adp_comandaItem = new Adp_ComandaItem(this, comanda_itens);
                listView.setAdapter(adp_comandaItem);
                if(totalparcial > 0){
                    comanda.setStatus("PARCIAL");
                }
                if(totalatendido == totalitens){
                    comanda.setStatus("ATENDIDO");
                }
                if(totalaberto == totalitens){
                    comanda.setStatus("ABERTO");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}