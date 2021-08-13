package br.com.dw.comanda_facil.telas.comanda;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import br.com.dw.comanda_facil.R;
import br.com.dw.comanda_facil.adapters.Adp_produtos;
import br.com.dw.comanda_facil.banco.DatabaseHelper;
import br.com.dw.comanda_facil.dao.Dao_Produto;
import br.com.dw.comanda_facil.entidades.Produto;
import br.com.dw.comanda_facil.telas.produto.Produtos;

public class Comanda_Produto extends AppCompatActivity implements AdapterView.OnItemClickListener {
    ListView listView;
    DatabaseHelper banco;
    List<Produto> produtos = new ArrayList<>();
    List<Produto> produtos_filtrados = new ArrayList<>();
    Dao_Produto dao_produto;
    Adp_produtos adp_produtos;
    private EditText filtro;
    ImageButton btn_leitura;
    CheckBox filtro_ativo;
    int v =0;
    final Activity activity = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comanda__produto);

        listView = findViewById(R.id.listview_comandaproduto);
        listView.setOnItemClickListener(this);
        filtro = findViewById(R.id.p_filtro2);
        filtro_ativo = findViewById(R.id.filtro_ativo2);
        filtro_ativo.setChecked(true);
        filtro_ativo.setEnabled(false);
        filtro_ativo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Pesquisar_ativos();
                adp_produtos = new Adp_produtos(Comanda_Produto.this, produtos_filtrados);
                listView.setAdapter(adp_produtos);
            }
        });

        btn_leitura = findViewById(R.id.btn_leitura3);
        btn_leitura.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator integrator = new IntentIntegrator(activity);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
                integrator.setPrompt("Leitor de Código de Barras");
                integrator.setCameraId(0);
                integrator.setBeepEnabled(true);
                integrator.setOrientationLocked(false);
                integrator.initiateScan();

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        //filtro.setText("");
        preenchelista();
    }

    private void preenchelista() {
        if( v == 0) {
            banco = new DatabaseHelper(this);
            try {
                dao_produto = new Dao_Produto(banco.getConnectionSource());
                produtos = dao_produto.queryForAll();
                Pesquisar_ativos();
                adp_produtos = new Adp_produtos(this, produtos_filtrados);
                listView.setAdapter(adp_produtos);
                listView.setTextFilterEnabled(true);
                filtro.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        Pesquisar();
                        adp_produtos = new Adp_produtos(Comanda_Produto.this, produtos_filtrados);
                        listView.setAdapter(adp_produtos);
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void Pesquisar_ativos(){
        produtos_filtrados.clear();
        for(int i = 0;i < produtos.size();i++){
            Produto data =produtos.get(i);
            if(!filtro.getText().equals("")){
                Pesquisar();
            }else if(filtro_ativo.isChecked() == data.isStatus()){
                produtos_filtrados.add(data);
            }
        }
    }

    private void Pesquisar() {
        produtos_filtrados.clear();
        for(int i = 0;i < produtos.size();i++){
            Produto data =produtos.get(i);
            if(!filtro.getText().equals("")){
                String pq = filtro.getText().toString().toLowerCase();
                String condicao = data.getDescricao().toLowerCase();
                String condicao2 = data.getId().toString();
                String condicao3 = data.getEan();
                String condicao4 = Double.toString(data.getValor());
                if((condicao.contains(pq) || condicao2.contains(pq) || condicao3.contains(pq) || condicao4.contains(pq)) && (filtro_ativo.isChecked() == data.isStatus())){
                    produtos_filtrados.add(data);
                }
            }else{
                produtos_filtrados.addAll(produtos);
            }
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (result != null) {
            if (result.getContents() != null) {
                filtro.setText(result.getContents());
                Pesquisar();
                v = 1;
            } else {
                Toast.makeText(activity, "Leitor Cancelado", Toast.LENGTH_SHORT).show();
            }

        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Produto produto = (Produto) parent.getItemAtPosition(position);
        SharedPreferences lt = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = lt.edit();
        String t = lt.getString("idproduto","vazio");
        editor.putString("idproduto", produto.getId().toString());
        editor.commit();

        finish();
    }
}