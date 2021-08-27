package br.com.dw.comanda_facil.telas.comanda;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

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
    List<Comanda> totalgeralcomadas = new ArrayList<>();
    int idmesa;
    ListView listView;
    Adp_ComandasMesas adp_comandasMesas;
    private AdView mAdView;
    private InterstitialAd mInterstitialAd;
    int n = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comanda);

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        mesaselecionada = findViewById(R.id.mesaselecionada);
        listView = findViewById(R.id.listvew_pedidos);
        listView.setOnItemClickListener(this);

        chamaanuncio();
    }

    @Override
    protected void onStart() {
        super.onStart();
        preenche();
        if (mInterstitialAd != null) {
            mInterstitialAd.show(this);
        }
    }

    public void comanda_pedido(View view){
        if (totalgeralcomadas.size() == 100 || totalgeralcomadas.size() > 100) {
            Toast.makeText(this, "Você atingiu o limite de 100 comandas registradas ! ", Toast.LENGTH_SHORT).show();
        } else {
            chamaanuncio();
            Intent intent = new Intent(this, Comanda_Pedido.class);
            intent.putExtra("id", idmesa);
            startActivity(intent);
        }
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

                Object[] status = {"ABERTO","PARCIAL","ATENDIDO"};
                dao_comanda = new Dao_Comanda(banco.getConnectionSource());
                comandas = dao_comanda.queryBuilder().where().eq("mesa",idmesa).and().in("status",status).query();
                adp_comandasMesas = new Adp_ComandasMesas(this,comandas);
                listView.setAdapter(adp_comandasMesas);

            } catch (SQLException e) {
                e.printStackTrace();
            }

        }

        try {
            //pega total de comandas do sistema
            totalgeralcomadas.clear();
            totalgeralcomadas = dao_comanda.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (mInterstitialAd != null) {
            mInterstitialAd.show(this);
        }
        Comanda comanda = (Comanda) parent.getItemAtPosition(position);
        Intent intent = new Intent(this, Comanda_Pedido.class);
        intent.putExtra("idcomanda",comanda.getId());
        startActivity(intent);
    }

    private void chamaanuncio() {
        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(this,"ca-app-pub-3925364440483118/3335699766", adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        mInterstitialAd = interstitialAd;
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        mInterstitialAd = null;
                    }
                });
        n = 1;

    }
}