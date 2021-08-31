package br.com.dw.comanda_facil.telas.relatorio;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.io.IOException;

import br.com.dw.comanda_facil.R;
import br.com.dw.comanda_facil.telas.produto.Produtos;
import br.com.dw.comanda_facil.util.Util;

public class Relatorios extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_relatorio);
    }

    public void rel_totalvenda(View view) throws IOException, InterruptedException {
        if(Util.isOnline()) {
            Intent intent = new Intent(this, Total_Venda.class);
            startActivity(intent);
        }else{
            Toast.makeText(this, "Nescessário acesso a internet ! ", Toast.LENGTH_SHORT).show();
        }
    }

    public void rel_totalvendamesa(View view) throws IOException, InterruptedException {
        if(Util.isOnline()) {
            Intent intent = new Intent(this, Total_Venda_Mesa.class);
            startActivity(intent);
        }else{
            Toast.makeText(this, "Nescessário acesso a internet ! ", Toast.LENGTH_SHORT).show();
        }
    }
}