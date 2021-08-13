package br.com.dw.comanda_facil.entidades;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "tbmesa")
public class Mesa {

    @DatabaseField(generatedId = true)
    private Integer id;
    @DatabaseField
    private String descricao;
    @DatabaseField(dataType = DataType.BOOLEAN)
    private boolean status;

    private int totalcomandas;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public int getTotalcomandas() {
        return totalcomandas;
    }

    public void setTotalcomandas(int totalcomandas) {
        this.totalcomandas = totalcomandas;
    }
}
