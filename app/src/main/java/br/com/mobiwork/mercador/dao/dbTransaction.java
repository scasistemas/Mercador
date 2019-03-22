package br.com.mobiwork.mercador.dao;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public final class dbTransaction {
    private static SQLiteDatabase db;

    public static SQLiteDatabase getTransaction(Context context){
        if (db == null){
            daoCreateDBM daoDB = new daoCreateDBM(context);
            db =  daoDB.getWritableDatabase();
            openTransaction();
        }
        return db;
    }

    public static void  openTransaction(){
        db.beginTransaction();
    }
    public static void  closeCommitTransaction(){
        db.setTransactionSuccessful();
        db.endTransaction();
        db = null;
    }

    public static void  closeRollbackTransaction(){
        db.endTransaction();
        db = null;
    }

}
