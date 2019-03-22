package br.com.mobiwork.mercador.util;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import java.io.File;

import br.com.mobiwork.mercador.model.Config;

public final class ConfigVendedor   {
    public static Config config;
    private static SQLiteDatabase db;



    public static Config getConfig(SQLiteDatabase db){
        if (config == null){
            config = new Config();
            Cursor c = db.rawQuery("SELECT * FROM config tb " +
                    " WHERE tb._id = ?",  new String[]{"1"});
            if (c.moveToFirst()) {
                config.setConfig(c, db.getVersion(),db);
            }
        }

        return config;
    }


    public static Config getConfig(){
        if (config == null){
            config = new Config();
            Cursor c = db.rawQuery("SELECT * FROM config tb " +
                    " WHERE tb._id = ?",  new String[]{"1"});
            if (c.moveToFirst()) {
                config.setConfig(c, db.getVersion(),db);
            }
        }

        return config;
    }
    public static void setConfig(Config config) {
        ConfigVendedor.config = config;
    }

    public static String getExternalStorageDirectory(){
        String dirMercadorName = Environment.getExternalStorageDirectory().toString() + "//Mercador" ;
        File file = new File(dirMercadorName);
        if(!file.isDirectory()){
            file.mkdir();
            if(!file.isDirectory()){
                dirMercadorName = "/mnt/sdcard" + "//Mercador" ;  // Juares
                file = new File(dirMercadorName);
                if(!file.isDirectory()){
                    file.mkdir();
                    if(!file.isDirectory()){
                        dirMercadorName = "/sdcard" + "//Mercador" ;  // Juares
                        file = new File(dirMercadorName);
                        if(!file.isDirectory()){
                            file.mkdir();
                        }
                    }
                }
            }
        }
        return dirMercadorName;

    }
    public static String getExternalStorageDirectoryVs(){
        String dirMercadorName = Environment.getExternalStorageDirectory().toString() + "//Mercador/Vs" ;
        File file = new File(dirMercadorName);
        if(!file.isDirectory()){
            file.mkdir();
            if(!file.isDirectory()){
                dirMercadorName = "/mnt/sdcard" + "//Mercador/Vs" ;  // Juares
                file = new File(dirMercadorName);
                if(!file.isDirectory()){
                    file.mkdir();
                    if(!file.isDirectory()){
                        dirMercadorName = "/sdcard" + "//Mercador/Vs" ;  // Juares
                        file = new File(dirMercadorName);
                        if(!file.isDirectory()){
                            file.mkdir();
                        }
                    }
                }
            }
        }
        return dirMercadorName;

    }

    public static File getExternalStorageDirectoryFile(){
        String dirMercadorName = Environment.getExternalStorageDirectory().toString() + "//Mercador" ;
        File file = new File(dirMercadorName);
        if(!file.isDirectory()){
            file.mkdir();
            if(!file.isDirectory()){
                dirMercadorName = "/mnt/sdcard" + "//Mercador" ;  // Juares
                file = new File(dirMercadorName);
                if(!file.isDirectory()){
                    file.mkdir();
                    if(!file.isDirectory()){
                        dirMercadorName = "/sdcard" + "//Mercador" ;  // Juares
                        file = new File(dirMercadorName);
                        if(!file.isDirectory()){
                            file.mkdir();
                        }
                    }
                }
            }
        }
        return file;

    }

    public static String getExternalStorageDirectoryBackupSeg(){
        String dirMercadorName = Environment.getExternalStorageDirectory().toString() + "//Mercador/backupseg" ;
        File file = new File(dirMercadorName);
        if(!file.isDirectory()){
            file.mkdir();
            if(!file.isDirectory()){
                dirMercadorName = "/mnt/sdcard" + "//Mercador/backupseg" ;  // Juares
                file = new File(dirMercadorName);
                if(!file.isDirectory()){
                    file.mkdir();
                    if(!file.isDirectory()){
                        dirMercadorName = "/sdcard" + "//Mercador/backupseg" ;  // Juares
                        file = new File(dirMercadorName);
                        if(!file.isDirectory()){
                            file.mkdir();
                        }
                    }
                }
            }
        }
        return dirMercadorName;

    }
}
