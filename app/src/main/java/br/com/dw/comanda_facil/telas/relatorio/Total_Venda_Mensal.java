package br.com.dw.comanda_facil.telas.relatorio;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.NumberPicker;

import java.util.Calendar;

import br.com.dw.comanda_facil.R;

public class Total_Venda_Mensal extends AppCompatActivity {

    EditText ed_mes;
    EditText ed_ano;

    int mesdefaul,anodefault;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_total__venda__mensal);

        ed_mes = findViewById(R.id.mes);
        ed_mes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                numberpickerdialog();
            }
        });

        ed_ano = findViewById(R.id.ano);
        ed_ano.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                numberpickerdialog2();
            }
        });

        Calendar calendar = Calendar.getInstance();
        mesdefaul = calendar.get(Calendar.MONTH);
        anodefault = calendar.get(Calendar.YEAR);
    }

    private void numberpickerdialog(){
        final NumberPicker mes = new NumberPicker(this);
        mes.setMinValue(0);
        mes.setMaxValue(11);
        mes.setWrapSelectorWheel(true);
        mes.setValue(mesdefaul);
        String meses[]={"Janeiro","Fevereiro","Março","Abril","Maio","Junho","Julho","Agosto","Setembro","Outubro","Novembro","Dezembro"};
        mes.setDisplayedValues(meses);
        mes.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                ed_mes.setText(mes.getDisplayedValues()[mes.getValue()]);
            }
        });
        AlertDialog.Builder builder = new AlertDialog.Builder(this).setView(mes);
        builder.setTitle("Selecione Mês");
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ed_mes.setText(mes.getDisplayedValues()[mes.getValue()]);
            }
        });
        builder.show();

        //recupera valor
       // String value = mes.getDisplayedValues()[mes.getValue()];
    }

    private void numberpickerdialog2(){
        final NumberPicker ano = new NumberPicker(this);
        ano.setMinValue(2021);
        ano.setMaxValue(2051);
        ano.setWrapSelectorWheel(true);
        ano.setValue(anodefault);
        ano.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                ed_ano.setText(""+ano.getValue());
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(this).setView(ano);
        builder.setTitle("Selecione Ano");
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ed_ano.setText(""+ano.getValue());
            }
        });
        builder.show();

        //recupera valor
        // String value = mes.getDisplayedValues()[mes.getValue()];
    }
}