package br.com.mobiwork.mercador.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.google.api.services.drive.model.File;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import br.com.mobiwork.mercador.dao.daoConfig;
import br.com.mobiwork.mercador.dao.daoCreateDBM;
import br.com.mobiwork.mercador.model.Config;

/**
 * Created by LuisGustavo on 14/06/14.
 */
public class ArquivoTxt {


    private String mDLValltx;
    private Config localConfig;
    private SQLiteDatabase db;
    String linha="";
    daoConfig dc;

    public  ArquivoTxt(Context ctx){

        this.db = new daoCreateDBM(ctx).getWritableDatabase();
        localConfig = new daoConfig(ctx).consultar(this.db);
        dc= new daoConfig(ctx);

    }

    public String getLinha() {
        return linha;
    }

    public void setLinha(String linha) {
        this.linha = linha;
    }

    public boolean  lertxt() throws IOException {
        boolean testar=false;

        double result,result2;
        mDLValltx = "ver_"+this.localConfig.getVendid()+".txt";
        BufferedReader leitor = null;
        FileReader reader = null;
        final java.io.File file = new java.io.File(ConfigVendedor.getExternalStorageDirectory(),
                mDLValltx);
        if(file.exists()){
            String t = "entrou";
            File file2 = new File();
            try {
                reader = new FileReader(String.valueOf(file));
                leitor = new BufferedReader(reader);
                this.linha = null;

                this.linha=leitor.readLine();
                String data2=localConfig.getVersaotabela();
                Double anoatu,dataatu,horaatu;
                Double ano,data,hora;
                anoatu=Double.parseDouble(this.linha.substring(0,4));
                dataatu=Double.parseDouble(this.linha.substring(4,8));
                horaatu=Double.parseDouble(this.linha.substring(8,12));
                ano=0.0;
                data=0.0;
                hora=0.0;
                if(localConfig.getVersaotabela()!=null){
                    ano=Double.parseDouble(localConfig.getVersaotabela().substring(0, 4));
                    data=Double.parseDouble(localConfig.getVersaotabela().substring(4, 8));
                    hora=Double.parseDouble(localConfig.getVersaotabela().substring(8, 12));
                }
                if(anoatu>ano){
                    testar=true;
                }
                else if(anoatu>=ano){
                    if(dataatu>data){
                        testar=true;
                    }
                    else if(dataatu>=data&&horaatu>hora){
                        testar=true;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            leitor.close();
            reader.close();
        }else{
            testar=true;
        }


        return testar;
    }
}
