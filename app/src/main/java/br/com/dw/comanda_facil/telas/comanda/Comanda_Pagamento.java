package br.com.dw.comanda_facil.telas.comanda;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.sql.SQLException;
import java.text.DecimalFormat;

import br.com.dw.comanda_facil.R;
import br.com.dw.comanda_facil.banco.DatabaseHelper;
import br.com.dw.comanda_facil.dao.Dao_Comanda;
import br.com.dw.comanda_facil.entidades.Comanda;

public class Comanda_Pagamento extends AppCompatActivity {

    private TextInputEditText vltotal,vldesconto,vltroco,vlpago,vlrecebido;
    DatabaseHelper banco;
    Comanda comanda = new Comanda();
    Dao_Comanda dao_comanda;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comanda__pagamento);

        banco = new DatabaseHelper(this);
        try {
            dao_comanda = new Dao_Comanda(banco.getConnectionSource());
        } catch (SQLException e) {
            e.printStackTrace();
        }

        vltotal = findViewById(R.id.ed_total);
        vldesconto = findViewById(R.id.ed_desconto);
        vlpago = findViewById(R.id.ed_pago);
        vltroco = findViewById(R.id.ed_troco);
        vlrecebido = findViewById(R.id.ed_recebido);

        vltotal.setEnabled(false);
        vltroco.setEnabled(false);
        vltroco.setText("0");
        vlrecebido.setEnabled(false);
        vlrecebido.setText("0");
        vlpago.setText("0");
        vldesconto.setText("0");
        //editavel somente vldesconto e vltroco
        vldesconto.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!vldesconto.getText().toString().equals("")){
                    
                }
            }
        });
    }

    public void finalizar(View view){
        if(comanda.getValor_recebido() > 0){

        }else{
            Toast.makeText(this, "Preencha corretamente os campos", Toast.LENGTH_SHORT).show();
        }

    }

    public void cancelar(View view){
        finish();
    }

    private void preenche(){
        DecimalFormat df = new DecimalFormat("R$ #,###.00");
        Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.containsKey("idcomanda")) {
            try {
                comanda = dao_comanda.queryForId(bundle.getInt("idcomanda"));
                vltotal.setText(df.format(comanda.getValor_total()));

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        preenche();
    }
}