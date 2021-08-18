package br.com.dw.comanda_facil.telas.mesa;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import br.com.dw.comanda_facil.R;
import br.com.dw.comanda_facil.adapters.Adp_mesas;
import br.com.dw.comanda_facil.banco.DatabaseHelper;
import br.com.dw.comanda_facil.dao.Dao_Mesa;
import br.com.dw.comanda_facil.entidades.Mesa;


public class Mesas extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private ListView listView;
    private DatabaseHelper banco;
    private Dao_Mesa dao_mesa;
    private Adp_mesas adp_mesas;
    private List<Mesa> mesas = new ArrayList<>();
    private List<Mesa> mesasfiltradas = new ArrayList<>();
    private EditText filtro;
    private AlertDialog alerta;
    CheckBox filtro_ativo;
    final Activity activity = this;
    int v =0;

    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mesas);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        filtro = findViewById(R.id.m_filtro);
        filtro_ativo = findViewById(R.id.filtro_ativo);
        filtro_ativo.setChecked(true);
        filtro_ativo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Pesquisar_ativos();
                adp_mesas = new Adp_mesas(Mesas.this, mesasfiltradas);
                listView.setAdapter(adp_mesas);
            }
        });
        listView = findViewById(R.id.listview_mesas);
        listView.setOnItemClickListener(this);
    }

    public void tela_mesa(View view){
        Intent intent = new Intent(this, TelaMesa.class);
        startActivity(intent);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Mesa mesa = (Mesa) parent.getItemAtPosition(position);
        Intent intent = new Intent(this, TelaMesa.class);
        intent.putExtra("id",mesa.getId());
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        filtro.setText("");
        preenchelista();
    }

    private void preenchelista() {
        if( v == 0) {
            banco = new DatabaseHelper(this);
            try {
                dao_mesa = new Dao_Mesa(banco.getConnectionSource());
                mesas = dao_mesa.queryForAll();
                Pesquisar_ativos();
                adp_mesas = new Adp_mesas(this,mesasfiltradas);
                listView.setAdapter(adp_mesas);
                listView.setTextFilterEnabled(true);
                filtro.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }
                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        Pesquisar();
                        adp_mesas = new Adp_mesas(Mesas.this, mesasfiltradas);
                        listView.setAdapter(adp_mesas);
                    }
                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });

            }catch (SQLException e) {
                    e.printStackTrace();
            }
        }
    }

    private void Pesquisar() {
        mesasfiltradas.clear();
        for(int i = 0;i < mesas.size();i++){
            Mesa data =mesas.get(i);
            if(!filtro.getText().equals("")){
                String pq = filtro.getText().toString().toLowerCase();
                String condicao = data.getDescricao().toLowerCase();
                String condicao2 = data.getId().toString();

                if((condicao.contains(pq) || condicao2.contains(pq) ) && (filtro_ativo.isChecked() == data.isStatus())){
                    mesasfiltradas.add(data);
                }
            }else{
                mesasfiltradas.addAll(mesas);
            }
        }
    }

    private void Pesquisar_ativos(){
        mesasfiltradas.clear();
        for(int i = 0;i < mesas.size();i++){
            Mesa data =mesas.get(i);
            if(!filtro.getText().equals("")){
                Pesquisar();
            }else if(filtro_ativo.isChecked() == data.isStatus()){
                mesasfiltradas.add(data);
            }
        }
    }
}