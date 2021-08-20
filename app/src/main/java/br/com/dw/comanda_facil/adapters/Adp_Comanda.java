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
        TextView resumo = view.findViewById(R.id.adp_resumo);

        Mesa mesa = mesas.get(position);
        descricao.setText(mesa.getDescricao());
        if(mesa.getTotal_aberto()>0){
            view.setBackgroundResource(R.drawable.grid_row_border_aberto);
            resumo.setText("Status: Aberto");
        }else if(mesa.getTotal_parcial()>0){
            view.setBackgroundResource(R.drawable.grid_row_border_parcial);
            resumo.setText("Status: Parcial");
        }else if(mesa.getTotal_atendido()>0){
            view.setBackgroundResource(R.drawable.grid_row_border_atendido);
            resumo.setText("Status: Atendido");
        }else if(mesa.getTotal_fechado_parcial()>0){
            view.setBackgroundResource(R.drawable.grid_row_border_fechadoparcial);
            resumo.setText("Status: Fechado Parcial");
        }
        return view;
    }

}
