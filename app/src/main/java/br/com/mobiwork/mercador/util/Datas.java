package br.com.mobiwork.mercador.util;

/**
 * Created by LuisGustavo on 11/06/14.
 */
public class Datas {

    private String ano,dia,mes;
    private String horas,min,seg;


    public String convData(String data){
        ano=data.substring(0, 4);
        mes=data.substring(5, 7);
        dia=data.substring(8, 10);
        return dia+"/"+mes+"/"+ano;
    }
    public String convHora(String hora){

        horas=String.valueOf(Integer.parseInt(hora.substring(11,13))-3);
        min=hora.substring(14,16);
        seg=hora.substring(17,19);

        return horas+":"+min+":"+seg;
    }

}
