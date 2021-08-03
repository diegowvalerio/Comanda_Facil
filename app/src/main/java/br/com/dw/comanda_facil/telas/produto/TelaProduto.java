package br.com.dw.comanda_facil.telas.produto;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.sql.SQLException;

import br.com.dw.comanda_facil.R;
import br.com.dw.comanda_facil.banco.DatabaseHelper;
import br.com.dw.comanda_facil.dao.Dao_Produto;
import br.com.dw.comanda_facil.entidades.Produto;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class TelaProduto extends AppCompatActivity {

    int v =0;
    EditText p_descricao,p_valor,p_ean;
    ImageButton btn_leitura;
    CheckBox p_status;
    ImageView p_imagem;
    Produto produto = new Produto();
    Dao_Produto dao_produto;
    DatabaseHelper banco;
    static final int CAMERA = 1;
    static final int GALERIA = 2;
    static final int REQUEST_CODE_WRITE_EXTERNAL_STORAGE_PERMISSION=1;
    static final int REQUEST_CODE_CAMERA_STORAGE_PERMISSION = 1;
    static final int MAX_FILE_SIZE = 100 * 1024;
    final Activity activity = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_produto);

        banco = new DatabaseHelper(this);
        try {
            dao_produto = new Dao_Produto(banco.getConnectionSource());
        }catch (SQLException e){
            e.printStackTrace();
        }

        p_descricao = findViewById(R.id.p_descricao);
        p_valor = findViewById(R.id.p_valor);
        p_ean = findViewById(R.id.p_ean);
        p_status = findViewById(R.id.p_status);
        p_imagem = findViewById(R.id.p_imagem);
        btn_leitura = findViewById(R.id.btn_leitura);
        btn_leitura.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator integrator = new IntentIntegrator(activity);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
                integrator.setPrompt("Leitor de Código de Barras");
                integrator.setCameraId(0);
                integrator.setBeepEnabled(true);
                integrator.setOrientationLocked(false);
                integrator.initiateScan();

            }
        });

    }
    @Override
    protected void onStart() {
        super.onStart();
        preencheproduto();
    }

    public void galeriaproduto(View view){
        writeStoragePermissionGranted();
        Intent intentPegaFoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(intentPegaFoto,GALERIA);

    }

    public void cameraproduto(View view){
        cameraPermissionGranted();
        Intent it = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(it, CAMERA);

    }


    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (requestCode == CAMERA) {
            if (resultCode == RESULT_OK) {
                Bitmap bitmap = (Bitmap) intent.getExtras().get("data");
                p_imagem.setImageBitmap(bitmap);
                Toast.makeText(activity, "Imagem adicionada com sucesso !", Toast.LENGTH_SHORT).show();
                p_imagem.refreshDrawableState();
            }
        }else if (requestCode == GALERIA) {
            if (resultCode == RESULT_OK) {
                Uri selectedImage = intent.getData();
                String[] filePathColumn = { MediaStore.Images.Media.DATA };
                Cursor cursor = getContentResolver().query(selectedImage,filePathColumn, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String picturePath = cursor.getString(columnIndex);
                cursor.close();
                p_imagem.setImageBitmap(BitmapFactory.decodeFile(picturePath));

            }
        }else{
            IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
            if (result != null) {
                if (result.getContents() != null) {
                    p_ean.setText(result.getContents());

                }else {
                    Toast.makeText(activity, "Leitor Cancelado", Toast.LENGTH_SHORT).show();
                }

            }
        }//7452575//

    }

    public static byte[] imageViewToByte(ImageView image) {
        //Bitmap bitmap1 = ((BitmapDrawable) image.getDrawable()).getBitmap();
        Bitmap bitmap1 = bitmaps(image.getDrawable());
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap1.compress( Bitmap.CompressFormat.JPEG, 50, stream );
        byte[] byteArray = stream.toByteArray();
        return byteArray;
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

    public void salvarproduto(View view){
       if(p_descricao.getText().length() >0 && p_valor.getText().length()>0){
            produto.setDescricao(p_descricao.getText().toString());
            double v = 0;
            v = Double.parseDouble(p_valor.getText().toString());
            produto.setValor(v);
            produto.setEan(p_ean.getText().toString());
            produto.setStatus(p_status.isChecked());
            produto.setImagem(imageViewToByte(p_imagem));
            try{
               dao_produto.createOrUpdate(produto);
                Toast.makeText(this, "Produto Salvo com Sucesso ! ", Toast.LENGTH_SHORT).show();
               finish();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Erro, não foi possivel salvar produto", Toast.LENGTH_SHORT).show();
            }

       }else{
           Toast.makeText(this, "Preencha os campos minimos !", Toast.LENGTH_SHORT).show();
       }
    }

    public void writeStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                //startPeriodicRequest();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_WRITE_EXTERNAL_STORAGE_PERMISSION);
            }
        } else {
            return;
        }
    }

    public void cameraPermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                //startPeriodicRequest();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CODE_CAMERA_STORAGE_PERMISSION);
            }
        } else {
            return;
        }
    }

    public void preencheproduto(){
        if( v == 0) {
            Bundle bundle = getIntent().getExtras();
            if (bundle != null && bundle.containsKey("id")) {
                try {
                    produto = dao_produto.queryForId(bundle.getInt("id"));
                    if (!produto.getId().equals("")) {
                        p_descricao.setText(produto.getDescricao());
                        p_valor.setText(Double.toString(produto.getValor()));
                        p_status.setChecked(produto.isStatus());
                        p_ean.setText(produto.getEan());
                        p_imagem.setImageBitmap(BitmapFactory.decodeByteArray(produto.getImagem(), 0, produto.getImagem().length));
                        v = 1;
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}