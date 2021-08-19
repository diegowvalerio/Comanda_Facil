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
    private int total_aberto;
    private int total_parcial;
    private int total_atendido;
    private int total_fechado_parcial;


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

    public int getTotal_aberto() {
        return total_aberto;
    }

    public void setTotal_aberto(int total_aberto) {
        this.total_aberto = total_aberto;
    }

    public int getTotal_parcial() {
        return total_parcial;
    }

    public void setTotal_parcial(int total_parcial) {
        this.total_parcial = total_parcial;
    }

    public int getTotal_fechado_parcial() {
        return total_fechado_parcial;
    }

    public void setTotal_fechado_parcial(int total_fechado_parcial) {
        this.total_fechado_parcial = total_fechado_parcial;
    }

    public int getTotal_atendido() {
        return total_atendido;
    }

    public void setTotal_atendido(int total_atendido) {
        this.total_atendido = total_atendido;
    }

    public void setTotalcomandas(int totalcomandas) {
        this.totalcomandas = totalcomandas;
    }
}
