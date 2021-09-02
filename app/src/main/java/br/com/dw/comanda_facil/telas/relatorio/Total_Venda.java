package br.com.dw.comanda_facil.telas.relatorio;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IPieDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import br.com.dw.comanda_facil.R;
import br.com.dw.comanda_facil.banco.DatabaseHelper;
import br.com.dw.comanda_facil.dao.Dao_Comanda;
import br.com.dw.comanda_facil.entidades.Comanda;

public class Total_Venda extends AppCompatActivity {

    EditText data1, data2;
    Calendar calendario = Calendar.getInstance();
    Calendar calendario2 = Calendar.getInstance();
    String myFormat = "dd/MM/yyyy"; //In which you need put here
    SimpleDateFormat sdf = new SimpleDateFormat(myFormat, new Locale("pt","BR"));

    DatabaseHelper banco;
    Dao_Comanda dao_comanda;
    List<Comanda> comadas = new ArrayList<>();
    PieChart totalvendapizza;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_total__venda);

        banco = new DatabaseHelper(this);
        try {
            dao_comanda = new Dao_Comanda(banco.getConnectionSource());
            comadas = dao_comanda.queryBuilder().where().between("data_abertura",calendario.getTime(),calendario2.getTime()).query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        totalvendapizza = findViewById(R.id.totalvenda_pizza);

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
                new DatePickerDialog(Total_Venda.this, date, calendario
                        .get(Calendar.YEAR), calendario.get(Calendar.MONTH),
                        calendario.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        data2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(Total_Venda.this, date2, calendario2
                        .get(Calendar.YEAR), calendario2.get(Calendar.MONTH),
                        calendario2.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        criapizza();
    }

    @Override
    protected void onStart() {
        super.onStart();
        entrada();
    }

    //como criar o grafico
    //https://www.youtube.com/watch?v=vhKtbECeazQ&ab_channel=ChiragKachhadiya
    public void criapizza(){
        final DecimalFormat df = new DecimalFormat("R$ #,###.00");
        ArrayList<PieEntry> status = new ArrayList<>();
        List<Integer> colors = new ArrayList<Integer>();

        if(comadas.size()>0){
            double aberto = 0;
            double parcial = 0;
            double atendido = 0;
            double fechado = 0;
            for(Comanda c:comadas){
                if(c.getStatus() != null) {
                    if (c.getStatus().equals("ABERTO")) {
                        aberto = aberto + c.getValor_total();
                    } else if (c.getStatus().equals("PARCIAL")) {
                        parcial = parcial + c.getValor_total();
                    } else if (c.getStatus().equals("ATENDIDO")) {
                        atendido = atendido + c.getValor_total();
                    } else if (c.getStatus().equals("FECHADO")) {
                        fechado = fechado + c.getValor_total();
                    }
                }
            }
            if(aberto >0) {
                status.add(new PieEntry((float) aberto, "Aberto"));
                colors.add(Color.parseColor("#ffff4444"));
            }
            if(parcial >0) {
                status.add(new PieEntry((float) parcial, "Parcial"));
                colors.add(Color.parseColor("#ffff8800"));
            }
            if(atendido >0) {
                status.add(new PieEntry((float) atendido, "Atendido"));
                colors.add(Color.parseColor("#ff99cc00"));
            }
            if(fechado >0) {
                status.add(new PieEntry((float) fechado, "Recebido"));
                colors.add(Color.parseColor("#ff00ddff"));
            }
        }else {
            status.add(new PieEntry(0, "Vazio"));
        }

        ValueFormatter formato = new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return df.format(value);
            }
        };

        PieDataSet dataset = new PieDataSet(status,"");
        //dataset.setColors(ColorTemplate.LIBERTY_COLORS);
        dataset.setColors(colors);
        dataset.setValueTextColor(Color.BLACK);
        dataset.setValueTextSize(12f);

        PieData data = new PieData(dataset);
        data.setValueFormatter(formato);

        totalvendapizza.setData(data);
        totalvendapizza.getDescription().setEnabled(false);
        totalvendapizza.setCenterText("Total Venda x Status");
        totalvendapizza.animateY(1000);
        totalvendapizza.invalidate();
    }

    public void entrada(){
        SimpleDateFormat dt = new SimpleDateFormat("dd/MM/yyyy HH:mm",new Locale("pt","BR"));

        Calendar c1 = Calendar.getInstance();
        c1.set(calendario.get(Calendar.YEAR), calendario.get(Calendar.MONTH), calendario.get(Calendar.DATE),00,00,00);
        Calendar c2 = Calendar.getInstance();
        c2.set(calendario2.get(Calendar.YEAR), calendario2.get(Calendar.MONTH), calendario2.get(Calendar.DATE),23,59,59);

        try {
            Date d1 = dt.parse(dt.format(c1.getTime()));
            Date d2 = dt.parse(dt.format(c2.getTime()));

            dao_comanda = new Dao_Comanda(banco.getConnectionSource());
            comadas = dao_comanda.queryBuilder().where().between("data_abertura_long",c1.getTime(),c2.getTime()).query();
            criapizza();

        } catch (SQLException | ParseException e) {
            e.printStackTrace();
        }
    }

    public void pesquisar(View view){
        SimpleDateFormat dt = new SimpleDateFormat("dd/MM/yyyy HH:mm",new Locale("pt","BR"));

        Calendar c1 = Calendar.getInstance();
        c1.set(calendario.get(Calendar.YEAR), calendario.get(Calendar.MONTH), calendario.get(Calendar.DATE),00,00,00);
        Calendar c2 = Calendar.getInstance();
        c2.set(calendario2.get(Calendar.YEAR), calendario2.get(Calendar.MONTH), calendario2.get(Calendar.DATE),23,59,59);

        try {
            Date d1 = dt.parse(dt.format(c1.getTime()));
            Date d2 = dt.parse(dt.format(c2.getTime()));

            dao_comanda = new Dao_Comanda(banco.getConnectionSource());
            comadas = dao_comanda.queryBuilder().where().between("data_abertura_long",c1.getTime(),c2.getTime()).query();
            criapizza();

        } catch (SQLException | ParseException e) {
            e.printStackTrace();
        }
    }
}