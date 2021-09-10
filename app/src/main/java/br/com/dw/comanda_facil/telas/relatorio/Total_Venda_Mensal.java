package br.com.dw.comanda_facil.telas.relatorio;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import br.com.dw.comanda_facil.R;
import br.com.dw.comanda_facil.banco.DatabaseHelper;
import br.com.dw.comanda_facil.dao.Dao_Comanda;
import br.com.dw.comanda_facil.dao.Dao_Mesa;
import br.com.dw.comanda_facil.entidades.Comanda;
import br.com.dw.comanda_facil.util.GirarRotulo_LineChart;

public class Total_Venda_Mensal extends AppCompatActivity {

    EditText ed_mes;
    EditText ed_ano;

    DatabaseHelper banco;
    Dao_Comanda dao_comanda;
    List<Comanda> comadas = new ArrayList<>();

    int mesdefaul,anodefault;

    LineChart totalvenda_linha_mensal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_total__venda__mensal);

        totalvenda_linha_mensal = findViewById(R.id.totalvenda_linha_mensal);

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

        banco = new DatabaseHelper(this);
        try {
            dao_comanda = new Dao_Comanda(banco.getConnectionSource());
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void filtrar(View view){
        if(ed_mes.getText().length() > 0 && ed_ano.getText().length()>0){
            criarelatorio();
        }else {
            Toast.makeText(this, "Selecione Mês/Ano para Filtrar ! ", Toast.LENGTH_SHORT).show();
        }
    }

    private void criarelatorio() {
        final DecimalFormat df = new DecimalFormat("R$ #,###.00");

        comadas.clear();
        Calendar c1 = Calendar.getInstance();
        c1.set(anodefault,mesdefaul,01,00,00,00);

        Calendar c2 = Calendar.getInstance();
        c2.set(anodefault, mesdefaul, c1.get(Calendar.DAY_OF_MONTH),23,59,59);
        c2.set(Calendar.DAY_OF_MONTH, c1.getActualMaximum(Calendar.DAY_OF_MONTH));

        ArrayList<Entry> linhas = new ArrayList<>();
        try {
            comadas = dao_comanda.queryBuilder().where().between("data_abertura_long",c1.getTime(),c2.getTime()).query();
        }catch (SQLException e){
            e.printStackTrace();
        }

        if(comadas.size()>0){
            for(int i =1;i <= c2.get(Calendar.DAY_OF_MONTH);i++){
                double total = 0;
                for(Comanda c:comadas){
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(c.getData_abertura_long());
                    int day = cal.get(Calendar.DAY_OF_MONTH);
                    if(day == i) {
                        total = total + c.getValor_total();
                    }
                }
                linhas.add(new Entry(i,(float) total));
            }
        }

        LineDataSet dataSet = new LineDataSet(linhas,ed_mes.getText()+"/"+ed_ano.getText()+" -> Valor por Dia");
        //dataSet.setColors(ColorTemplate.LIBERTY_COLORS);//LIBERTY_COLORS
        dataSet.setColor(Color.TRANSPARENT);
        dataSet.setDrawFilled(true);

        LineData data = new LineData(dataSet);
        ValueFormatter formato = new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                if (value == 0.0f)
                    return "";
                return df.format(value);
            }
        };

        data.setValueFormatter(formato);
        XAxis eixox = totalvenda_linha_mensal.getXAxis();
        eixox.setGranularity(1f);
        eixox.setGranularityEnabled(true);
        eixox.setLabelCount(c2.get(Calendar.DAY_OF_MONTH),false);

        totalvenda_linha_mensal.setRenderer(new GirarRotulo_LineChart(totalvenda_linha_mensal, totalvenda_linha_mensal.getAnimator(), totalvenda_linha_mensal.getViewPortHandler()));
        totalvenda_linha_mensal.getAxisRight().setEnabled(false);
        totalvenda_linha_mensal.setData(data);
        totalvenda_linha_mensal.getDescription().setEnabled(false);
        totalvenda_linha_mensal.animateY(1000);
        totalvenda_linha_mensal.getLegend().setEnabled(true);
        totalvenda_linha_mensal.invalidate();

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
                mesdefaul = mes.getValue();
            }
        });
        AlertDialog.Builder builder = new AlertDialog.Builder(this).setView(mes);
        builder.setTitle("Selecione Mês");
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ed_mes.setText(mes.getDisplayedValues()[mes.getValue()]);
                mesdefaul = mes.getValue();
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
                anodefault = ano.getValue();
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(this).setView(ano);
        builder.setTitle("Selecione Ano");
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ed_ano.setText(""+ano.getValue());
                anodefault = ano.getValue();
            }
        });
        builder.show();

        //recupera valor
        // String value = mes.getDisplayedValues()[mes.getValue()];
    }
}