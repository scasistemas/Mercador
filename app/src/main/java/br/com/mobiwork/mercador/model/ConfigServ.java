package br.com.mobiwork.mercador.model;

import android.database.Cursor;

/**
 * Created by LuisGustavo on 23/10/14.
 */
public class ConfigServ {
    private Integer _id;
    private String endereco;
    private String login;
    private String senha;

    public void ConfigServ(){
        this._id=0;
        this.endereco="";
        this.login="";
        this.senha="";
    }

    public void setConfig(Cursor c ){
        this._id = c.getInt(c.getColumnIndex("_id"));
        this.endereco = c.getString(c.getColumnIndex("servidor"));
        this.login = c.getString(c.getColumnIndex("login"));
        this.senha = c.getString(c.getColumnIndex("senha"));
    }

    public Integer get_id() {
        return _id;
    }

    public void set_id(Integer _id) {
        this._id = _id;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }
}
