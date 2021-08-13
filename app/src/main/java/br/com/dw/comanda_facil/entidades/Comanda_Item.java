package br.com.dw.comanda_facil.entidades;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

@DatabaseTable(tableName = "tbcomanda_item")
public class Comanda_Item {

    @DatabaseField(generatedId = true)
    private Integer id;
    @DatabaseField(foreign = true,columnName = "comanda",columnDefinition = "INTEGER REFERENCES tbcomanda (id) ON DELETE CASCADE",foreignAutoRefresh = true)
    private Comanda comanda;
    @DatabaseField
    private String status; //ABERTO //PARCIAL //ATENDIDO
    @DatabaseField(dataType = DataType.DATE_STRING, format = "dd/MM/yyyy hh:mm")
    private Date data_pedido;
    @DatabaseField(dataType = DataType.DATE_STRING, format = "dd/MM/yyyy hh:mm")
    private Date data_entrega;

    @DatabaseField(foreign = true,columnName = "produto",columnDefinition = "INTEGER REFERENCES tbproduto (id) ON DELETE CASCADE",foreignAutoRefresh = true)
    private Produto produto;
    @DatabaseField
    private Integer qtde;
    @DatabaseField
    private double valor_unitario;
    @DatabaseField
    private double valor_total;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Comanda getComanda() {
        return comanda;
    }

    public void setComanda(Comanda comanda) {
        this.comanda = comanda;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getData_pedido() {
        return data_pedido;
    }

    public void setData_pedido(Date data_pedido) {
        this.data_pedido = data_pedido;
    }

    public Date getData_entrega() {
        return data_entrega;
    }

    public void setData_entrega(Date data_entrega) {
        this.data_entrega = data_entrega;
    }

    public Produto getProduto() {
        return produto;
    }

    public void setProduto(Produto produto) {
        this.produto = produto;
    }

    public Integer getQtde() {
        return qtde;
    }

    public void setQtde(Integer qtde) {
        this.qtde = qtde;
    }

    public double getValor_unitario() {
        return valor_unitario;
    }

    public void setValor_unitario(double valor_unitario) {
        this.valor_unitario = valor_unitario;
    }

    public double getValor_total() {
        return valor_total;
    }

    public void setValor_total(double valor_total) {
        this.valor_total = valor_total;
    }
}
