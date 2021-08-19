package br.com.dw.comanda_facil.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import br.com.dw.comanda_facil.R;
import br.com.dw.comanda_facil.entidades.Mesa;

public class Adp_Comanda extends BaseAdapter {

    Context context;
    List<Mesa> mesas;

    public Adp_Comanda(Context context, List<Mesa> mesas){
        this.context = context;
        this.mesas = mesas;
    }
    @Override
    public int getCount() {
        return mesas.size();
    }

    @Override
    public Object getItem(int position) {
        return mesas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mesas.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.adp_gridview,parent,false);
        TextView descricao =view.findViewById(R.id.adp_gridview_descricao);
        Mesa mesa = mesas.get(position);
        descricao.setText(mesa.getDescricao());
        if(mesa.getTotal_aberto()>0){
            view.setBackgroundResource(R.drawable.grid_row_border_aberto);
        }else if(mesa.getTotal_parcial()>0){
            view.setBackgroundResource(R.drawable.grid_row_border_parcial);
        }else if(mesa.getTotal_atendido()>0){
            view.setBackgroundResource(R.drawable.grid_row_border_atendido);
        }else if(mesa.getTotal_fechado_parcial()>0){
            view.setBackgroundResource(R.drawable.grid_row_border_fechadoparcial);
        }
        return view;
    }

}
