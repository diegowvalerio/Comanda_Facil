package br.com.dw.comanda_facil.telas.mesa;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import java.sql.SQLException;

import br.com.dw.comanda_facil.R;
import br.com.dw.comanda_facil.banco.DatabaseHelper;
import br.com.dw.comanda_facil.dao.Dao_Mesa;
import br.com.dw.comanda_facil.entidades.Mesa;

public class TelaMesa extends AppCompatActivity {

    EditText m_descricao;
    CheckBox m_ativo;

    DatabaseHelper banco;
    Mesa mesa = new Mesa();
    Dao_Mesa dao_mesa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_mesa);

        banco = new DatabaseHelper(this);
        try {
            dao_mesa = new Dao_Mesa(banco.getConnectionSource());
        }catch (SQLException e){
            e.printStackTrace();
        }

        m_descricao = findViewById(R.id.m_descricao);
        m_ativo = findViewById(R.id.m_ativo);
    }

    public void salvar(View view){
        if(m_descricao.getText().length() >0){
            mesa.setDescricao(m_descricao.getText().toString().toUpperCase());
            mesa.setStatus(m_ativo.isChecked());
            try {
                dao_mesa.createOrUpdate(mesa);
                Toast.makeText(this, "Mesa salva com Sucesso !", Toast.LENGTH_SHORT).show();
                finish();
            } catch (SQLException e) {
                e.printStackTrace();
                Toast.makeText(this, "Erro, não foi possivel salvar a Mesa !", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(this, "Preencha o nome da Mesa !", Toast.LENGTH_SHORT).show();
        }

    }
}