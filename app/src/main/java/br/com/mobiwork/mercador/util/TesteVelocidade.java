package br.com.mobiwork.mercador.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Calendar;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;


import br.com.mobiwork.mercador.R;


public class TesteVelocidade extends Activity {

    public static final int DIALOG_DOWNLOAD_PROGRESS = 0;
    private Button startBtn;
    private ProgressDialog mProgressDialog;
    private String banda="";
    private Alertas a;
    private  float bandwidth;
    private ProgressDialog dialog;


    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.velocimetro);
        startBtn = (Button)findViewById(R.id.button1);
        startBtn.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                startDownload();
            }
        });
        a= new Alertas(this);
    }

    private void startDownload() {
        //String url = "http://www.4shared.com/download/PjAhdz8ece/blue.png?lgfp=1000";
        String url = "http://oi66.tinypic.com/1538o03.jpg";
        procurar();
        new DownloadFileAsync().execute(url);

    }
    public void voltar(View v){
        TesteVelocidade.this.finish();
    }


    public void procurar(){
        boolean isSDPresent = Environment.getExternalStorageState()
                .equals(Environment.MEDIA_MOUNTED);

        if (isSDPresent) {

            File file[] = ConfigVendedor.getExternalStorageDirectoryFile().listFiles();
            if (file != null) {
                for (int i = 0; i < file.length; i++) {

                    String t2=file[i].getName();
                    String verifiq=t2.substring(0, 2);
                    if(verifiq.equalsIgnoreCase("$$")){
                        file[i].delete();
                    }
                }
            }
        }
    }
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DIALOG_DOWNLOAD_PROGRESS:
                mProgressDialog = new ProgressDialog(this);
                mProgressDialog.setMessage("Testando...");
                mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                mProgressDialog.setCancelable(false);
                mProgressDialog.show();
                return mProgressDialog;
            default:
                return null;
        }
    }

    class DownloadFileAsync extends AsyncTask<String, String, String> {

        private ProgressDialog dialog;
        @Override
        protected void onPreExecute() {
            this.dialog = new ProgressDialog(TesteVelocidade.this);
            super.onPreExecute();
            this.dialog.setMessage("Teste");
            this.dialog.setMessage("Testando Internet...");
            dialog.setProgressStyle(dialog.STYLE_HORIZONTAL);
            dialog.setProgress(0);
            dialog.setMax(3);

            dialog.setButton(ProgressDialog.BUTTON_NEUTRAL,
                    "Cancel",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //button click stuff here
                            try {
                                // SincUp.this.finalize();
                                //SincUp.this.cancel(false);
                                cancel(true);


                            } catch (Throwable throwable) {
                                throwable.printStackTrace();
                            }

                            //   dialog.dismiss();
                        }
                    });

            this.dialog.show();

        }

        @Override
        protected String doInBackground(String... aurl) {
            int count;
            bandwidth=0;
            try {
                long startTime = System.currentTimeMillis();
                URL url = new URL(aurl[0]);
                dialog.setProgress(1);
                changeMessage("Abrindo Conexão");
                URLConnection conexion = url.openConnection();
                float lenghtOfFile = conexion.getContentLength(); //tamanho do arquivo
                conexion.connect();
                Log.d("ANDRO_ASYNC", "Lenght of file: " + lenghtOfFile);
                double t=lenghtOfFile;
                String hora=pegarHora();
                //ler
                InputStream input = new BufferedInputStream(url.openStream());
                //escrever
                OutputStream output = new FileOutputStream(ConfigVendedor.getExternalStorageDirectory()+"/$$"+hora+".png");
                byte data[] = new byte[1024];
                long total = 0;
                changeMessage("Testando Internet...");
                dialog.setProgress(2);
                while ((count = input.read(data)) != -1) {
                    total += count;
                   // publishProgress(""+(int)((total*100)/lenghtOfFile));
                    output.write(data, 0, count);
                }
                File f = new File(ConfigVendedor.getExternalStorageDirectory()+"/$$"+hora+".png");
                long endTime = System.currentTimeMillis();
                long tempo=endTime-startTime;
                bandwidth = lenghtOfFile / ((endTime-startTime) / 1000);
                banda="Sua banda é de:"+bandwidth+" MB";
                output.flush();
                output.close();
                input.close();


            } catch (Exception e) {
                String s=e.getMessage()+"MB";
            }
            return null;

        }
        protected void onProgressUpdate(String... progress) {
            Log.d("ANDRO_ASYNC",progress[0]);
            dialog.setProgress(Integer.parseInt(progress[0]));
        }

        @Override
        protected void onPostExecute(String unused) {
            dialog.getButton(ProgressDialog.BUTTON_NEUTRAL).setText("OK");
            this.dialog.setMessage(statusNet());
            dialog.setProgress(3);
        }
        public void changeMessage(final String msg){
            TesteVelocidade.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    dialog.setMessage(msg);
                }
            });

        }
    }



    public String pegarHora(){
        final Calendar c = Calendar.getInstance();
        String t=c.get(Calendar.HOUR)+"."+ c.get(Calendar.MINUTE)+"."+c.get(Calendar.SECOND)+"  "+c.get(Calendar.DAY_OF_MONTH)+"-"+c.get(Calendar.MONTH)+"-"+c.get(Calendar.YEAR);
        return t ;

    }
    public String statusNet(){
        String result="";
        if(bandwidth==0){
            result="Sem Conexao com a Internet";
        }
        else if(bandwidth>0&&bandwidth<1000){
            result="Apresenta muita lentidao";
        }else if(bandwidth>1000&&bandwidth<7000){
            result="Lenta, Mercador pode não sincronizar ";
        }else if(bandwidth>7000&&bandwidth<10000){
            result="Bom";
        }else if(bandwidth>10000){
            result="Excelente";
        }

        return result;
    }
}