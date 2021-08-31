package br.com.dw.comanda_facil.telas.relatorio;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import br.com.dw.comanda_facil.R;
import br.com.dw.comanda_facil.banco.DatabaseHelper;
import br.com.dw.comanda_facil.dao.Dao_Comanda;
import br.com.dw.comanda_facil.dao.Dao_Mesa;
import br.com.dw.comanda_facil.entidades.Comanda;
import br.com.dw.comanda_facil.entidades.Mesa;

public class Total_Venda_Mesa extends AppCompatActivity {

    EditText data1, data2;
    Calendar calendario = Calendar.getInstance();
    Calendar calendario2 = Calendar.getInstance();
    String myFormat = "dd/MM/yyyy"; //In which you need put here
    SimpleDateFormat sdf = new SimpleDateFormat(myFormat, new Locale("pt","BR"));

    DatabaseHelper banco;
    Dao_Comanda dao_comanda;
    List<Comanda> comadas = new ArrayList<>();

    Dao_Mesa dao_mesa;
    List<Mesa> mesas = new ArrayList<>();

    BarChart totalvendapizza;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_total__venda__mesa);

        banco = new DatabaseHelper(this);
        try {
            dao_comanda = new Dao_Comanda(banco.getConnectionSource());
            dao_mesa = new Dao_Mesa(banco.getConnectionSource());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        totalvendapizza = findViewById(R.id.totalvenda_pizza_mesa);

        data1 = findViewById(R.id.data1);
        data1.setText(sdf.format(calendario.getTime()));
        data2 = findViewById(R.id.data2);
        data2.setText(sdf.format(calendario2.getTime()));

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendario.set(Calendar.YEAR, year);
                calendario.set(Calendar.MONTH, month);
                calendario.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                data1.setText(sdf.format(calendario.getTime()));
            }
        };

        final DatePickerDialog.OnDateSetListener date2 = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendario2.set(Calendar.YEAR, year);
                calendario2.set(Calendar.MONTH, month);
                calendario2.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                data2.setText(sdf.format(calendario2.getTime()));
            }
        };

        data1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(Total_Venda_Mesa.this, date, calendario
                        .get(Calendar.YEAR), calendario.get(Calendar.MONTH),
                        calendario.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        data2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(Total_Venda_Mesa.this, date2, calendario2
                        .get(Calendar.YEAR), calendario2.get(Calendar.MONTH),
                        calendario2.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        criapizza();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    //como criar o grafico
    //https://www.youtube.com/watch?v=vhKtbECeazQ&ab_channel=ChiragKachhadiya
    public void criapizza(){
        try {
            mesas = dao_mesa.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        SimpleDateFormat dt = new SimpleDateFormat("dd/MM/yyyy HH:mm",new Locale("pt","BR"));

        Calendar c1 = Calendar.getInstance();
        c1.set(calendario.get(Calendar.YEAR), calendario.get(Calendar.MONTH),calendario.get(Calendar.DATE),00,00,00);

        Calendar c2 = Calendar.getInstance();
        c2.set(calendario2.get(Calendar.YEAR), calendario2.get(Calendar.MONTH), calendario2.get(Calendar.DATE),23,59,59);

        final DecimalFormat df = new DecimalFormat("R$ #,###.00");
        ArrayList<BarEntry> status = new ArrayList<>();
        final ArrayList<String> x = new ArrayList<>();

        if(mesas.size()>0){
            int i = 0;
            for(Mesa m:mesas){
                double total = 0;
                comadas.clear();
                try {
                    Date d1 = dt.parse(dt.format(c1.getTime()));
                    Date d2 = dt.parse(dt.format(c2.getTime()));
                    comadas = dao_comanda.queryBuilder().where().between("data_abertura",c1.getTime(),c2.getTime()).and().eq("mesa",m.getId()).query();
                    //Toast.makeText(this, ""+c1.getTimeInMillis(), Toast.LENGTH_SHORT).show();
                } catch (SQLException | ParseException e) {
                    e.printStackTrace();
                }
                if(comadas.size()>0){
                    for(Comanda c:comadas){
                        total = total + c.getValor_total();
                    }
                }
                if(total>0) {
                    status.add(new BarEntry(i, (float) total));
                    x.add(m.getDescricao());
                    i++;
                }
            }
        }

        BarDataSet dataSet = new BarDataSet(status,"Mesas");
        dataSet.setColors(ColorTemplate.LIBERTY_COLORS);//LIBERTY_COLORS
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setValueTextSize(8f);

        BarData data = new BarData(dataSet);
        ValueFormatter formato = new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return df.format(value);
            }
        };
        data.setValueFormatter(formato);
        XAxis eixox = totalvendapizza.getXAxis();
        eixox.setGranularity(1f);
        eixox.setGranularityEnabled(true);
        eixox.setPosition(XAxis.XAxisPosition.BOTTOM);
        eixox.setLabelRotationAngle(45);
        eixox.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return x.get((int) value);
            }
        });

        totalvendapizza.getXAxis().setValueFormatter(new IndexAxisValueFormatter(x));
        totalvendapizza.setData(data);
        totalvendapizza.setFitBars(true);
        totalvendapizza.getDescription().setEnabled(false);
        totalvendapizza.animateY(1000);
        totalvendapizza.getLegend().setEnabled(false);//ocultar legenda
        totalvendapizza.invalidate();
    }

    public void pesquisar(View view){
        criapizza();
    }
}