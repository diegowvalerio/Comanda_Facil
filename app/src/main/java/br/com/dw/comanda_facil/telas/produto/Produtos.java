package br.com.dw.comanda_facil.telas.produto;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import br.com.dw.comanda_facil.R;
import br.com.dw.comanda_facil.adapters.Adp_produtos;
import br.com.dw.comanda_facil.banco.DatabaseHelper;
import br.com.dw.comanda_facil.dao.Dao_Produto;
import br.com.dw.comanda_facil.entidades.Produto;

public class Produtos extends AppCompatActivity  implements  AdapterView.OnItemClickListener{
    private ListView listView;
    private DatabaseHelper banco;
    private Dao_Produto dao_produto;
    private Produto produto = new Produto();
    private Adp_produtos adp_produtos;
    private List<Produto> produtos = new ArrayList<>();
    private List<Produto> produtos_filtrados = new ArrayList<>();
    private EditText filtro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_produtos);

        filtro = findViewById(R.id.p_filtro);
        listView = findViewById(R.id.listview_produtos);
        listView.setOnItemClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        filtro.setText("");
        preenchelista();
    }

    public void preenchelista(){
        banco =  new DatabaseHelper(this);
        try{
            dao_produto = new Dao_Produto(banco.getConnectionSource());
            produtos = dao_produto.queryForAll();
            adp_produtos = new Adp_produtos(this,produtos);
            listView.setAdapter(adp_produtos);
            listView.setTextFilterEnabled(true);
            filtro.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    Pesquisar();
                    adp_produtos = new Adp_produtos(Produtos.this,produtos_filtrados);
                    listView.setAdapter(adp_produtos);
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
        }catch (SQLException e){
            e.printStackTrace();
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
                if(condicao.contains(pq) || condicao2.contains(pq) || condicao3.contains(pq) || condicao4.contains(pq)){
                    produtos_filtrados.add(data);
                }
            }else{
                produtos_filtrados.addAll(produtos);
            }
        }
    }

    public void tela_produto(View view){
        Intent intent = new Intent(this, TelaProduto.class);
        startActivity(intent);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Produto produto = (Produto) parent.getItemAtPosition(position);
        Intent intent = new Intent(this,TelaProduto.class);
        intent.putExtra("id",produto.getId());
        startActivity(intent);
    }
}