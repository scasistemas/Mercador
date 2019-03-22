package br.com.mobiwork.mercador.model;

import android.database.Cursor;

/**
 * Created by LuisGustavo on 26/05/14.
 */
public class Visitas {
    private String _id;
    private int idvendedor;
    private String  idVisita ;
    private String cliente;
    private String data;
    private String hora;
    private String nomeCli;
    private String datahora;


    public void setVisitas(Cursor c){

        this._id = c.getString(c.getColumnIndex("_id"));
        this.idvendedor = c.getInt(c.getColumnIndex("idvendedor"));
        this.hora = c.getString(c.getColumnIndex("hora"));
        this.cliente = c.getString(c.getColumnIndex("cliente"));
        this.data = c.getString(c.getColumnIndex("data"));
        this.nomeCli= c.getString(c.getColumnIndex("nomeCli"));
        this.idVisita=c.getString(c.getColumnIndex("idVisita"));
        this.datahora=c.getString(c.getColumnIndex("datahora"));

    }

    public String getDatahora() {
        return datahora;
    }

    public void setDatahora(String datahora) {
        this.datahora = datahora;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public String getIdVisita() {
        return idVisita;
    }

    public void setIdVisita(String idVisita) {
        this.idVisita = idVisita;
    }



    public String getNomeCli() {
        return nomeCli;
    }

    public void setNomeCli(String nomeCli) {
        this.nomeCli = nomeCli;
    }


    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public int getIdvendedor() {
        return idvendedor;
    }

    public void setIdvendedor(int idvendedor) {
        this.idvendedor = idvendedor;
    }

    public String getCliente() {
        return cliente;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }



}
