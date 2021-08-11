package br.com.dw.comanda_facil.telas;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import br.com.dw.comanda_facil.R;
import br.com.dw.comanda_facil.telas.mesa.Mesas;
import br.com.dw.comanda_facil.telas.produto.Produtos;

public class Principal extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);
    }

    public void tela_produtos(View view){
        Intent intent = new Intent(this, Produtos.class);
        startActivity(intent);
    }

    public void tela_mesas(View view){
        Intent intent = new Intent(this, Mesas.class);
        startActivity(intent);
    }
}