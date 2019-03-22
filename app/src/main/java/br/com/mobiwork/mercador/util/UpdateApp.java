package br.com.mobiwork.mercador.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import java.io.File;

/**
 * Created by LuisGustavo on 17/10/14.
 */
public class UpdateApp extends AsyncTask<String,Void,Void> {
    private Context context;
    private String arquivo;
    public void setContext(Context contextf,String arquivoparam){
        context = contextf;
        arquivo=arquivoparam;
    }

    @Override
    protected Void doInBackground(String... arg0) {
        try {
            File file = new File(ConfigVendedor.getExternalStorageDirectoryVs());
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(new File(file, arquivo)), "application/vnd.android.package-archive");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // without this flag android returned a intent error!
            context.startActivity(intent);


        } catch (Exception e) {
            String w=e.getMessage();
            Log.e("UpdateAPP", "Update error! " + e.getMessage());
        }
        return null;
    }}