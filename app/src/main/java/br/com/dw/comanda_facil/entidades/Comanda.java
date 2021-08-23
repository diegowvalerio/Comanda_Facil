package br.com.dw.comanda_facil.entidades;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Collection;
import java.util.Date;

@DatabaseTable(tableName = "tbcomanda")
public class Comanda {

    @DatabaseField(generatedId = true)
    private Integer id;
    @DatabaseField(foreign = true,columnName = "mesa",columnDefinition = "INTEGER REFERENCES tbmesa (id) ON DELETE CASCADE",foreignAutoRefresh = true)
    private Mesa mesa;
    @DatabaseField
    private String cliente;
    @DatabaseField
    private String status; //ABERTO //PARCIAL //ATENDIDO //FECHADO //FECHADO_PARCIAL
    @DatabaseField(dataType = DataType.DATE_STRING, format = "dd/MM/yyyy hh:mm")
    private Date data_abertura;
    @DatabaseField
    private Integer qtde_pessoas;
    @DatabaseField
    private double valor_total;
    @DatabaseField
    private double valor_pago;
    @DatabaseField
    private double valor_troco;
    @DatabaseField
    private double valor_desconto;
    @DatabaseField
    private double valor_recebido;

    @DatabaseField(dataType = DataType.DATE_STRING, format = "dd/MM/yyyy hh:mm")
    private Date data_recebimento;

    @ForeignCollectionField(eager = true)
    private Collection<Comanda_Item> items;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCliente() {
        return cliente;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getData_abertura() {
        return data_abertura;
    }

    public void setData_abertura(Date data_abertura) {
        this.data_abertura = data_abertura;
    }

    public Integer getQtde_pessoas() {
        return qtde_pessoas;
    }

    public void setQtde_pessoas(Integer qtde_pessoas) {
        this.qtde_pessoas = qtde_pessoas;
    }

    public double getValor_total() {
        return valor_total;
    }

    public void setValor_total(double valor_total) {
        this.valor_total = valor_total;
    }

    public double getValor_pago() {
        return valor_pago;
    }

    public void setValor_pago(double valor_pago) {
        this.valor_pago = valor_pago;
    }

    public double getValor_troco() {
        return valor_troco;
    }

    public void setValor_troco(double valor_troco) {
        this.valor_troco = valor_troco;
    }

    public double getValor_desconto() {
        return valor_desconto;
    }

    public void setValor_desconto(double valor_desconto) {
        this.valor_desconto = valor_desconto;
    }

    public double getValor_recebido() {
        return valor_recebido;
    }

    public void setValor_recebido(double valor_recebido) {
        this.valor_recebido = valor_recebido;
    }

    public Collection<Comanda_Item> getItems() {
        return items;
    }

    public void setItems(Collection<Comanda_Item> items) {
        this.items = items;
    }

    public Mesa getMesa() {
        return mesa;
    }

    public void setMesa(Mesa mesa) {
        this.mesa = mesa;
    }

    public Date getData_recebimento() {
        return data_recebimento;
    }

    public void setData_recebimento(Date data_recebimento) {
        this.data_recebimento = data_recebimento;
    }
}
