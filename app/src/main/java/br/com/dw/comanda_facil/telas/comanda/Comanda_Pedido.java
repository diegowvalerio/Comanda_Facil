package br.com.dw.comanda_facil.telas.comanda;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPageEvent;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;
import com.itextpdf.text.pdf.parser.Line;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;

import br.com.dw.comanda_facil.R;
import br.com.dw.comanda_facil.adapters.Adp_ComandaItem;
import br.com.dw.comanda_facil.banco.DatabaseHelper;
import br.com.dw.comanda_facil.dao.Dao_Comanda;
import br.com.dw.comanda_facil.dao.Dao_Comanda_Item;
import br.com.dw.comanda_facil.dao.Dao_Mesa;
import br.com.dw.comanda_facil.dao.Dao_Produto;
import br.com.dw.comanda_facil.entidades.Comanda;
import br.com.dw.comanda_facil.entidades.Comanda_Item;
import br.com.dw.comanda_facil.entidades.Produto;
import br.com.dw.comanda_facil.telas.produto.Produtos;

public class Comanda_Pedido extends AppCompatActivity implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    EditText cliente,qtdepessoas,dataabertura;
    TextView vltotal,vlpago,vltroco,vldesconto,vlrecebido;
    ListView listView;

    DatabaseHelper banco;
    Comanda comanda = new Comanda();
    Comanda_Item comanda_item;
    List<Comanda_Item> comanda_itens = new ArrayList<>();
    Dao_Comanda dao_comanda;
    Dao_Comanda_Item dao_comanda_item;
    Dao_Mesa dao_mesa;
    Dao_Produto dao_produto;
    Adp_ComandaItem adp_comandaItem;
    Produto produto;
    private AlertDialog alerta;
    Date d = new Date();
    int idmesa =0;
    double total;
    int v = 0;
    final Activity activity = this;
    File pdffile = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comanda__pedido);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

        cliente = findViewById(R.id.c_cliente);
        qtdepessoas = findViewById(R.id.c_qtdepessoas);
        dataabertura = findViewById(R.id.c_dataabertura);
        dataabertura.setText(sdf.format(d));
        dataabertura.setEnabled(false);

        vltotal = findViewById(R.id.c_vltotal);
        vltotal.setText("0");

        listView = findViewById(R.id.listview_itens);
        listView.setOnItemClickListener(this);
        listView.setOnItemLongClickListener(this);
        banco = new DatabaseHelper(this);
        try {
            dao_comanda = new Dao_Comanda(banco.getConnectionSource());
            dao_comanda_item = new Dao_Comanda_Item(banco.getConnectionSource());
            dao_mesa = new Dao_Mesa(banco.getConnectionSource());
            dao_produto = new Dao_Produto(banco.getConnectionSource());
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.containsKey("id")) {
            idmesa = bundle.getInt("id");
        }
    }

    public static Bitmap bitmaps(Drawable drawable){
        try {
            Bitmap bitmap;

            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);

            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
            return bitmap;
        } catch (OutOfMemoryError e) {
            // Handle the error
            return null;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void gerarpdf(View view){
            try {
                String filename = "" + comanda.getMesa().getDescricao() + "_comanda_" + comanda.getCliente() + ".pdf";
                File path = new File(Environment.getExternalStorageDirectory(), "Comanda_Facil/pfd");
                pdffile = new File(path, filename);
                path.mkdirs();
                //File file = createFile();
                Document doc = new Document();
                PdfWriter w = PdfWriter.getInstance(doc, new FileOutputStream(pdffile));
                w.setPageEvent(new PdfPageEvent() {
                    @Override
                    public void onOpenDocument(PdfWriter writer, Document document) {

                    }

                    @Override
                    public void onStartPage(PdfWriter writer, Document document) {

                    }

                    @Override
                    public void onEndPage(PdfWriter writer, Document document) {
                        PdfContentByte cb = writer.getDirectContent();
                        cb.saveState();
                        try {
                            String txt = "Página "+writer.getPageNumber();
                            BaseFont bf = null;
                            try {
                                bf = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.WINANSI, BaseFont.NOT_EMBEDDED);
                            } catch (DocumentException e) {
                                e.printStackTrace();
                            }

                            float txtBase = document.top();
                            float txtSize = bf.getWidthPoint(txt, 8);
                            float adj = bf.getWidthPoint("0", 80);

                            cb.beginText();
                            cb.setFontAndSize(bf, 8);

                            cb.setTextMatrix(document.right() - txtSize - adj, txtBase);
                            cb.showText(txt);

                            cb.endText();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        cb.restoreState();

                    }

                    @Override
                    public void onCloseDocument(PdfWriter writer, Document document) {

                    }

                    @Override
                    public void onParagraph(PdfWriter writer, Document document, float paragraphPosition) {

                    }

                    @Override
                    public void onParagraphEnd(PdfWriter writer, Document document, float paragraphPosition) {

                    }

                    @Override
                    public void onChapter(PdfWriter writer, Document document, float paragraphPosition, Paragraph title) {

                    }

                    @Override
                    public void onChapterEnd(PdfWriter writer, Document document, float paragraphPosition) {

                    }

                    @Override
                    public void onSection(PdfWriter writer, Document document, float paragraphPosition, int depth, Paragraph title) {

                    }

                    @Override
                    public void onSectionEnd(PdfWriter writer, Document document, float paragraphPosition) {

                    }

                    @Override
                    public void onGenericTag(PdfWriter writer, Document document, Rectangle rect, String text) {

                    }
                });
                doc.open();//abrir o documento


                //Paragraph texto_estilo = new Paragraph("Ensinar quem sabe menos e aprender com quem sabe mais".toUpperCase(), FontFactory.getFont(FontFactory.HELVETICA, 20, Font.BOLD, BaseColor.RED));

                //criar
                Drawable drawable = getResources().getDrawable(R.mipmap.comanda_facil);
                Bitmap bitmap1 = bitmaps(drawable);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap1.compress(Bitmap.CompressFormat.JPEG, 50, stream);
                byte[] byteArray = stream.toByteArray();

                Image img = Image.getInstance(byteArray);
                img.scaleAbsolute(50, 50);
                img.setAlignment(Image.TEXTWRAP);

                //criar parágrafos
                Paragraph cabecalho = new Paragraph();
                cabecalho.add(new Phrase("Comanda Fácil",FontFactory.getFont(FontFactory.TIMES_ROMAN,20,Font.BOLD)));
                cabecalho.add(Chunk.NEWLINE);

                cabecalho.add("Um jeito fácil de controlar");
                cabecalho.setAlignment(Element.ALIGN_CENTER);
                doc.add(img);
                doc.add(new LineSeparator());
                doc.add(cabecalho);
                doc.add(Chunk.NEWLINE);

                Paragraph conteudo = new Paragraph();
                doc.add(conteudo);


                //criar nova página
                //doc.newPage();
                //adicionar parágrafo na nova página
               // doc.add(texto_estilo);

                //adicionar informações de autoria do documento
                doc.addAuthor("Dw Equipamentos & Soluções");
                //depois de tudo fechamos o documento
                doc.close();

                //exibir pdf
                Intent intent = new Intent(Intent.ACTION_VIEW);
                Uri uri = FileProvider.getUriForFile(this,"br.com.dw.comanda_facil.fileprovider",pdffile);
                intent.setType("application/pdf");
                intent.setData(uri);
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(intent);

                //https://issuu.com/charleseduardo/docs/02_-_relatorios_com_itext
                //https://www.devmedia.com.br/itext-blocos-de-construcao-anchor-chapter-section-image/30041
                //https://www.devmedia.com.br/itext-blocos-de-construcao-paragraph-list-e-listitem/29969

            } catch (Exception e) {
                e.printStackTrace();
            }
    }

    public void tela_produtos(View view) throws ParseException {
        salvarparaadicionaritens();
        Intent intent = new Intent(this, Comanda_Produto.class);
        startActivity(intent);
        v = 1;
    }

    @Override
    protected void onStart() {
        super.onStart();
        preenche_itens();
        pegaproduto();
        if(pdffile != null){
            pdffile.delete();
        }
    }

    public void fecharcomanda(View view){
        if(comanda_itens.size()>0) {
            try {
                calculatotal();
                dao_comanda.createOrUpdate(comanda);
                if (comanda.getStatus().equals("ATENDIDO")) {
                    Intent intent = new Intent(this, Comanda_Pagamento.class);
                    intent.putExtra("idcomanda",comanda.getId());
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(activity, "Existe produto não atendido ! Verifique ", Toast.LENGTH_LONG).show();
                }
            } catch (SQLException e) {
                e.printStackTrace();
                Toast.makeText(activity, "Erro", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(activity, "Não há produto inserido !", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        final Comanda_Item item = (Comanda_Item) parent.getItemAtPosition(position);
        if(!item.getStatus().equals("ATENDIDO")) {
            ArrayList<String> itens = new ArrayList<>();
            itens.add("Atender");
            itens.add("Excluir");
            ArrayAdapter adapter = new ArrayAdapter(this, R.layout.item_alerta, itens);
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("O que deseja fazer ?");
            builder.setSingleChoiceItems(adapter, 0, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface arg0, int arg1) {
                    if (arg1 == 0) { //atender
                        AlertDialog.Builder mensagem = new AlertDialog.Builder(activity);
                        mensagem.setTitle(item.getProduto().getDescricao());
                        mensagem.setMessage("Digite a quantidade atendida:");
                        final EditText input = new EditText(activity);
                        int v = item.getQtde() - item.getQtde_atendido();
                        input.setText(""+v);
                        input.setInputType(InputType.TYPE_CLASS_NUMBER);
                        mensagem.setView(input);
                        mensagem.setPositiveButton("Atender", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                if(input.getText().length()>0 ) {
                                    int q = Integer.parseInt(input.getText().toString());
                                    if (q+item.getQtde_atendido() == item.getQtde()){
                                        item.setStatus("ATENDIDO");
                                        item.setQtde_atendido(item.getQtde_atendido()+q);
                                        item.setData_entrega(new Date());
                                        try {
                                            dao_comanda_item.createOrUpdate(item);
                                            calculatotal();
                                            dao_comanda.createOrUpdate(comanda);
                                        } catch (SQLException e) {
                                            e.printStackTrace();
                                        }
                                    }else if (q+item.getQtde_atendido() < item.getQtde()){
                                        item.setStatus("PARCIAL");
                                        item.setQtde_atendido(item.getQtde_atendido()+q);
                                        item.setData_entrega(new Date());
                                        try {
                                            dao_comanda_item.createOrUpdate(item);
                                            calculatotal();
                                            dao_comanda.createOrUpdate(comanda);
                                        } catch (SQLException e) {
                                            e.printStackTrace();
                                        }
                                    }else{
                                        Toast.makeText(Comanda_Pedido.this, "Quantidade invalida !", Toast.LENGTH_SHORT).show();
                                    }
                                    }else{
                                        Toast.makeText(Comanda_Pedido.this, "Quantidade invalida !", Toast.LENGTH_SHORT).show();
                                    }
                                }
                        });
                        mensagem.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                        mensagem.show();
                    } else if (arg1 == 1) {//Excluir
                        AlertDialog.Builder mensagem = new AlertDialog.Builder(activity);
                        mensagem.setTitle(item.getProduto().getDescricao());
                        mensagem.setMessage("Confirma Exclusão ?");
                        mensagem.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    dao_comanda_item.delete(item);
                                    calculatotal();
                                    dao_comanda.createOrUpdate(comanda);
                                    Toast.makeText(Comanda_Pedido.this, "Excluido com Sucesso !", Toast.LENGTH_SHORT).show();
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        mensagem.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                        mensagem.show();
                    }
                    alerta.dismiss();
                }
            });
            alerta = builder.create();
            alerta.show();
        }
        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final DecimalFormat df = new DecimalFormat("#,###.00");
        final Comanda_Item item = (Comanda_Item) parent.getItemAtPosition(position);

        AlertDialog.Builder mensagem = new AlertDialog.Builder(this);
        mensagem.setTitle(item.getProduto().getDescricao());
        mensagem.setMessage("Editando a quantidade:");
        final EditText input = new EditText(this);
        input.setText(item.getQtde().toString());
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        mensagem.setView(input);
        mensagem.setPositiveButton("Editar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if(input.getText().length()>0 ) {
                    if (Integer.parseInt(input.getText().toString()) > 0) {
                        try {

                            if (item.getStatus().equals("ATENDIDO") && Integer.parseInt(input.getText().toString()) > item.getQtde()) {
                                item.setStatus("PARCIAL");
                                item.setQtde(Integer.parseInt(input.getText().toString()));
                            }else if (item.getStatus().equals("ATENDIDO") && Integer.parseInt(input.getText().toString()) < item.getQtde()) {
                                //não muda a quantidade nem o status
                                Toast.makeText(Comanda_Pedido.this, "Item ja entregue !", Toast.LENGTH_SHORT).show();
                            }else{
                                item.setQtde(Integer.parseInt(input.getText().toString()));
                            }
                            double total = item.getValor_unitario() * item.getQtde();
                            df.format(total);
                            item.setValor_total(total);
                            item.setData_pedido(new Date());
                            dao_comanda_item.createOrUpdate(item);
                            calculatotal();
                            dao_comanda.createOrUpdate(comanda);
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(Comanda_Pedido.this, "Quantidade invalida !", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(Comanda_Pedido.this, "Quantidade invalida !", Toast.LENGTH_SHORT).show();
                }
            }

        });
        mensagem.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        mensagem.show();

    }

    private void pegaproduto() {
        final DecimalFormat df = new DecimalFormat("#,###.00");
        if(v==1){
            SharedPreferences lt = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            SharedPreferences.Editor editor = lt.edit();
            String t = lt.getString("idproduto","vazio");
            editor.putString("idproduto", "vazio");
            editor.commit();
            if (!t.equals("vazio")) {

                try {
                    produto = new Produto();
                    produto = dao_produto.queryForId(Integer.parseInt(t));

                    AlertDialog.Builder mensagem = new AlertDialog.Builder(this);
                    mensagem.setTitle(produto.getDescricao());
                    mensagem.setMessage("Digite a quantidade:");
                    // DECLARACAO DO EDITTEXT
                    final EditText input = new EditText(this);
                    input.setInputType(InputType.TYPE_CLASS_NUMBER);
                    mensagem.setView(input);
                    mensagem.setPositiveButton("Adicionar", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            if(input.getText().length()>0) {
                                if (Integer.parseInt(input.getText().toString()) > 0) {
                                    try {
                                        comanda_item = new Comanda_Item();
                                        comanda_item.setComanda(comanda);
                                        comanda_item.setProduto(produto);
                                        comanda_item.setQtde(Integer.parseInt(input.getText().toString()));
                                        comanda_item.setValor_unitario(produto.getValor());
                                        double total = produto.getValor() * comanda_item.getQtde();
                                        df.format(total);
                                        comanda_item.setValor_total(total);
                                        comanda_item.setStatus("ABERTO");
                                        comanda_item.setData_pedido(new Date());
                                        comanda_item.setQtde_atendido(0);
                                        dao_comanda_item.createOrUpdate(comanda_item);
                                        calculatotal();
                                        dao_comanda.createOrUpdate(comanda);
                                    } catch (SQLException e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    Toast.makeText(Comanda_Pedido.this, "Quantidade invalida !", Toast.LENGTH_SHORT).show();
                                }
                            }else {
                                Toast.makeText(Comanda_Pedido.this, "Quantidade invalida !", Toast.LENGTH_SHORT).show();
                            }
                        }

                    });
                    mensagem.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    mensagem.show();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }else {
                Toast.makeText(this, "Produto invalido", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void preenche_itens() {
        DecimalFormat df = new DecimalFormat("R$ #,###.00");

        if(v == 0) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            Bundle bundle = getIntent().getExtras();
            if (bundle != null && bundle.containsKey("idcomanda")) {
                try {
                    comanda = dao_comanda.queryForId(bundle.getInt("idcomanda"));
                    comanda_itens = dao_comanda_item.queryBuilder().where().eq("comanda", comanda.getId()).query();
                    cliente.setText(comanda.getCliente());
                    qtdepessoas.setText(comanda.getQtde_pessoas().toString());
                    dataabertura.setText(sdf.format(comanda.getData_abertura()));
                    vltotal.setText(df.format(comanda.getValor_total()));

                    adp_comandaItem = new Adp_ComandaItem(this, comanda_itens);
                    listView.setAdapter(adp_comandaItem);
                    idmesa = comanda.getMesa().getId();
                    calculatotal();
                } catch (SQLException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    public void salvarcomanda(View view) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        if(cliente.getText().length() > 0 && qtdepessoas.getText().length()>0){
            try {
                comanda.setCliente(cliente.getText().toString().toUpperCase());
                comanda.setQtde_pessoas(Integer.parseInt(qtdepessoas.getText().toString()));
                comanda.setMesa(dao_mesa.queryForId(idmesa));
                d = sdf.parse(String.valueOf(dataabertura.getText()));
                comanda.setData_abertura(d);
                comanda.setData_abertura_long(d);
                if(comanda.getStatus()==null){
                    comanda.setStatus("ABERTO");
                }
                calculatotal();
                dao_comanda.createOrUpdate(comanda);
                finish();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }else{
            Toast.makeText(this, "Preencha os dados minimos !", Toast.LENGTH_SHORT).show();
        }
    }

    public void salvarparaadicionaritens(){
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        if(cliente.getText().length() > 0 && qtdepessoas.getText().length()>0){
            try {
                comanda.setCliente(cliente.getText().toString().toUpperCase());
                comanda.setQtde_pessoas(Integer.parseInt(qtdepessoas.getText().toString()));
                comanda.setMesa(dao_mesa.queryForId(idmesa));
                d = sdf.parse(String.valueOf(dataabertura.getText()));
                comanda.setData_abertura(d);
                comanda.setData_abertura_long(d);
                calculatotal();
                dao_comanda.createOrUpdate(comanda);
            } catch (SQLException | ParseException e) {
                e.printStackTrace();
            }
        }else{
            Toast.makeText(this, "Preencha os dados minimos !", Toast.LENGTH_SHORT).show();
        }
    }

    public void calculatotal(){
        int totalitens =0,totalaberto =0,totalparcial = 0,totalatendido = 0;
        DecimalFormat df = new DecimalFormat("R$ #,###.00");

        total = 0;
        if(comanda.getId() != null) {
            try {
                comanda_itens = dao_comanda_item.queryBuilder().where().eq("comanda", comanda.getId()).query();
                if (comanda_itens.size() > 0) {
                    for (Comanda_Item item : comanda_itens) {
                        total = total + item.getValor_total();
                        if(item.getStatus().equals("PARCIAL")) {
                            totalparcial ++;
                        }
                        if(item.getStatus().equals("ATENDIDO")) {
                            totalatendido ++;
                        }
                        if(item.getStatus().equals("ABERTO")) {
                            totalaberto ++;
                        }
                        totalitens++;
                    }
                    //df.format(total);
                    vltotal.setText(df.format(total));
                    comanda.setValor_total(total);
                }else{
                    comanda.setValor_total(0);
                    vltotal.setText(df.format(0));
                }
                adp_comandaItem = new Adp_ComandaItem(this, comanda_itens);
                listView.setAdapter(adp_comandaItem);
                if(totalparcial > 0 || (totalaberto != totalitens)){
                    comanda.setStatus("PARCIAL");
                }
                if(totalatendido == totalitens){
                    comanda.setStatus("ATENDIDO");
                }
                if(totalaberto == totalitens){
                    comanda.setStatus("ABERTO");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

}