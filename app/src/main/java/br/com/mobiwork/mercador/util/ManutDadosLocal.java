package br.com.mobiwork.mercador.util;

import java.io.File;
import java.io.FilenameFilter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import br.com.mobiwork.mercador.model.Config;


/**
 * Created by LuisGustavo on 19/05/2015.
 */
public class ManutDadosLocal {


    public Config config;
    public int ca;
    DateFormat formatterd,formatterd2,hora;

    public ManutDadosLocal(Config c){
        this.config=c;
        formatterd = new SimpleDateFormat("dd/MM/yyyy");
        formatterd2 = new SimpleDateFormat("MM/dd/yyyy");
        hora = new SimpleDateFormat("hh:mm:ss a");
    }

    public List<HashMap<String, String>> litBackup( )  {
        File sc=null;
        File[] domains ;
        List<HashMap<String, String>> fillMaps = new ArrayList<HashMap<String, String>>();
        fillMaps.clear();
        try {
            sc = (new File(ConfigVendedor.getExternalStorageDirectoryBackupSeg() + "/"));
            if(!sc.isDirectory()){
                sc.mkdir();
            }
            domains=sc.listFiles();
            for (int i = 0; i < domains.length; i++) {
                System.out.println(domains[i]);
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("_id", "" +domains[i].getName());
                map.put("data", String.valueOf(formatterd.format(new Date(domains[i].lastModified()))));
                map.put("hora", String.valueOf(hora.format(new Date(domains[i].lastModified()))));
                fillMaps.add(map);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fillMaps;
    }

    public List<HashMap<String, String>> listMerc( )  {
        File sc=null;
        File[] domains ;
        List<HashMap<String, String>> fillMaps = new ArrayList<HashMap<String, String>>();
        fillMaps.clear();
        try {
            sc = (new File(ConfigVendedor.getExternalStorageDirectory() + "/"));
            if(!sc.isDirectory()){
                sc.mkdir();
            }
            domains=sc.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File smbFile, String s) {
                    return s.endsWith(".db");
                }
            });
            for (int i = 0; i < domains.length; i++) {
                System.out.println(domains[i]);
                if(!domains[i].getName().equalsIgnoreCase("mercadodbm"+config.getVendid()+".db")
                   &&!domains[i].getName().equalsIgnoreCase("mercadoDB") &&!domains[i].getName().equalsIgnoreCase("mercadoDBM")){
                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put("_id", "" +domains[i].getName());
                //    map.put("data", String.valueOf(formatterd.format(new Date(domains[i].lastModified()))));
                    map.put("data",dataArq(domains[i].getName()));
                    map.put("hora", String.valueOf(hora.format(new Date(domains[i].lastModified()))));
                    fillMaps.add(map);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fillMaps;
    }
    public String dataArq(String arq){

        String dia="";
        String mes="";
        String ano="";
        int data=0;
        for(int i=0;i<arq.length();i++){

            if(data==1){
                if(!arq.substring(i,i+1).equalsIgnoreCase("_")){
                    dia=dia+arq.substring(i,i+1);
                }
            }else if(data==2){
                if(!arq.substring(i,i+1).equalsIgnoreCase("_")) {
                    mes = mes + arq.substring(i, i + 1);
                }
            }else if(data==3){
                if(!arq.substring(i,i+1).equalsIgnoreCase("_")&&!arq.substring(i,i+1).equalsIgnoreCase(" ")) {
                    ano = ano + arq.substring(i, i + 1);
                }

            }
            if(arq.substring(i,i+1).equalsIgnoreCase("_")){
                data=data+1;
            }
            if(arq.substring(i,i+1).equalsIgnoreCase(" ")&&!dia.equalsIgnoreCase("")&&!mes.equalsIgnoreCase("")&&!ano.equalsIgnoreCase("")){
                break;
            }

        }
        return mes+"/"+dia+"/"+ano;

    }


    public boolean checkFile(String carga)  {
        File sc=null;
        File[] domains ;
        List<HashMap<String, String>> fillMaps = new ArrayList<HashMap<String, String>>();
        fillMaps.clear();
        try {
            sc = (new File(ConfigVendedor.getExternalStorageDirectoryBackupSeg() + "/"));
            domains=sc.listFiles();
            for (int i = 0; i < domains.length; i++) {
              if(carga.equalsIgnoreCase(domains[i].getName())){
                  return true;
              }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    public String deleteFile(int dias)  {

        // data final igual a hoje
        Date dataFinal = new Date();
        Calendar calendarData = Calendar.getInstance();
        calendarData.setTime(dataFinal);
        calendarData.add(Calendar.DATE, -dias);


        File sc=null;
        try {
         List<HashMap<String, String>> fillMaps = listMerc();
            for(int i=0;i<fillMaps.size();i++){
                Date dataInicial =new Date(Date.parse(String.valueOf(formatterd2.format(calendarData.getTime()))));
                Date datarq=new Date(Date.parse(fillMaps.get(i).get("data")));
                String nome=fillMaps.get(i).get("_id");
                if(dataInicial.after(datarq)){
                    sc= (new File(ConfigVendedor.getExternalStorageDirectory() + "/"+fillMaps.get(i).get("_id")));
                    sc.delete();
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
        return "1";
    }

    public String deleteFile(String arquivo)  {
        File sc=null;
        try {
                sc = (new File(ConfigVendedor.getExternalStorageDirectoryBackupSeg() + "/"+arquivo));
            sc.delete();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "1";
    }

}
