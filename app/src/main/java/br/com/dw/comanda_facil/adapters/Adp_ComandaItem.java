package br.com.dw.comanda_facil.adapters;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import br.com.dw.comanda_facil.R;
import br.com.dw.comanda_facil.entidades.Comanda_Item;
import br.com.dw.comanda_facil.entidades.Produto;

public class Adp_ComandaItem extends BaseAdapter {

    Context context;
    List<Comanda_Item> comanda_items;

    public Adp_ComandaItem(Context context, List<Comanda_Item> comanda_items){
        this.context = context;
        this.comanda_items = comanda_items;
    }
    @Override
    public int getCount() {
        return comanda_items.size();
    }

    @Override
    public Object getItem(int position) {
        return comanda_items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return comanda_items.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm");
        DecimalFormat df = new DecimalFormat("#,###.00");

        View view = LayoutInflater.from(context).inflate(R.layout.adp_comandaitens,parent,false);
        TextView produto =view.findViewById(R.id.adp_item);
        TextView qtdeunitario = view.findViewById(R.id.adp_itemqtde_unitario);
        TextView status = view.findViewById(R.id.adp_itemstatus);
        TextView valor = view.findViewById(R.id.adp_itemtotal);
        TextView datapedido = view.findViewById(R.id.adp_itemdatapedido);
        TextView dataentrega = view.findViewById(R.id.adp_itemdataentrega);
        ImageView imagem = view.findViewById(R.id.adp_produto_imagem);

        Comanda_Item comanda_item = comanda_items.get(position);

        produto.setText(comanda_item.getProduto().getDescricao());
        status.setText(comanda_item.getStatus());
        if(status.getText().equals("ABERTO")){
            status.setTextColor(Color.RED);
        }else if (status.getText().equals("PARCIAL")){
            status.setTextColor(Color.YELLOW);
        }else if(status.getText().equals("ATENDIDO")){
            status.setTextColor(Color.GREEN);
        }
        qtdeunitario.setText(comanda_item.getQtde()+" x R$ "+comanda_item.getValor_unitario());
        valor.setText("R$ "+df.format(comanda_item.getValor_total()));
        datapedido.setText("Data: "+sdf.format(comanda_item.getData_pedido()));
        if(comanda_item.getData_entrega() !=null) {
            dataentrega.setText("Entrega:" + sdf.format(comanda_item.getData_entrega()));
        }else{
            dataentrega.setText("Entrega:");
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 2;
        options.inJustDecodeBounds = false;
        byte[] b = comanda_item.getProduto().getImagem();
        imagem.setImageBitmap(BitmapFactory.decodeByteArray(b,0,b.length,options));

        return view;
    }

}
