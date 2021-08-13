package br.com.dw.comanda_facil.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.List;

import br.com.dw.comanda_facil.R;
import br.com.dw.comanda_facil.entidades.Comanda;
import br.com.dw.comanda_facil.entidades.Mesa;

public class Adp_ComandasMesas extends BaseAdapter {

    Context context;
    List<Comanda> comandas;

    public Adp_ComandasMesas(Context context, List<Comanda> comandas){
        this.context = context;
        this.comandas = comandas;
    }
    @Override
    public int getCount() {
        return comandas.size();
    }

    @Override
    public Object getItem(int position) {
        return comandas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return comandas.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        DecimalFormat df = new DecimalFormat("#,###.00");
        View view = LayoutInflater.from(context).inflate(R.layout.adp_comandasmesa,parent,false);
        TextView cliente =view.findViewById(R.id.adp_cliente);
        TextView status = view.findViewById(R.id.adp_statuscomanda);
        TextView total = view.findViewById(R.id.adp_totalcomanda);

        Comanda comanda = comandas.get(position);
        cliente.setText(comanda.getCliente());
        status.setText(comanda.getStatus());
        if(status.getText().equals("ABERTO")){
            status.setTextColor(Color.RED);
        }else if (status.getText().equals("PARCIAL")){
            status.setTextColor(Color.YELLOW);
        }else if(status.getText().equals("ATENDIDO")){
            status.setTextColor(Color.GREEN);
        }
        total.setText("R$ "+df.format(comanda.getValor_total()));

        return view;
    }

}
