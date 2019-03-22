package br.com.mobiwork.mercador.sinc;

import android.accounts.AccountManager;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.security.keystore.UserNotAuthenticatedException;
import android.widget.ListView;
import android.widget.Toast;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.googleapis.media.MediaHttpUploader;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.InputStreamContent;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.api.services.drive.model.ParentReference;
import com.google.api.services.drive.model.User;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import br.com.mobiwork.mercador.dao.daoConfig;
import br.com.mobiwork.mercador.dao.daoCreateDBM;
import br.com.mobiwork.mercador.util.ConfigVendedor;
import br.com.mobiwork.mercador.model.Config;

/**
 * Created by LuisGustavo on 13/06/14.
 */
public class GoogleDrive extends Activity {

    static final int 				REQUEST_ACCOUNT_PICKER = 1;
    static final int 				REQUEST_AUTHORIZATION = 2;
    static final int 				RESULT_STORE_FILE = 4;
    static String folderpai="",folderatu;
    private static Uri mFileUri;
    private static Drive mService;
    private GoogleAccountCredential mCredential;
    private Context mContext;
    private ListView mListView;
    private String[] 				mFileArray;
    private String 					mDLVal,mDLValtx,mDLValltx;
    SQLiteDatabase db;

    private ProgressDialog dialog,dialog2;
    private String tConn;
    private String us;
    Config localConfig;
    boolean erro;
    private List<File> mResultList;

    public GoogleDrive(Context ctx){


        this.db = new daoCreateDBM(ctx).getWritableDatabase();
        localConfig = new daoConfig(ctx).consultar(this.db);
        ConfigVendedor.setConfig(localConfig);

        // Connect to Google Drive
        mCredential = GoogleAccountCredential.usingOAuth2(ctx, Arrays.asList(DriveScopes.DRIVE));
        //  startActivityForResult(mCredential.newChooseAccountIntent(), REQUEST_ACCOUNT_PICKER);
        mCredential.setSelectedAccountName(localConfig.getUsuftp());
        mService = getDriveService(mCredential);

        mContext = ctx;

    }

    private Drive getDriveService(GoogleAccountCredential credential) {
        return new Drive.Builder(AndroidHttp.newCompatibleTransport(), new GsonFactory(), credential)
                .build();
    }


    public void getDriveContents(String extensao,String arquivo)
    {
        mResultList = new ArrayList<File>();
        com.google.api.services.drive.Drive.Files f1 = mService.files();
        com.google.api.services.drive.Drive.Files.List request = null;
        do
        {
            try {
                request = f1.list();
                // request.setQ("trashed=false");
                String query = "(mimeType contains 'application/zip' or mimeType contains 'text/plain') and trashed=false";// AND '" + parentId + "' in parents";//   Logger.info(TAG + ": isFolderExists(): Query= " + query);
                request.setQ(query);
                com.google.api.services.drive.model.FileList fileList = request.execute();
                mResultList.addAll(fileList.getItems());
                request.setPageToken(fileList.getNextPageToken());
            } catch (UserRecoverableAuthIOException e) {
                Intent es=e.getIntent();
                startActivityForResult(e.getIntent(), REQUEST_AUTHORIZATION);
            } catch (IOException e) {
                e.printStackTrace();
                if (request != null) {
                    request.setPageToken(null);
                }
            }
        } while (request.getPageToken() !=null && request.getPageToken().length() > 0);
        downloadItemFromList(arquivo+extensao);
    }

    public void criarPasta(String pasta) throws IOException {
        if(getExistsFolder(mService,pasta).equalsIgnoreCase("")){
            com.google.api.services.drive.model.File body = new com.google.api.services.drive.model.File();
            body.setTitle(pasta);
            body.setMimeType("application/vnd.google-apps.folder");
            try {
                mService.files().insert(body).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    private String getExistsFolder(Drive service,String title) throws IOException
    {
        String folderpai;
        Drive.Files.List request;
        request = service.files().list();
        String query = "mimeType='application/vnd.google-apps.folder' AND trashed=false AND title='" + title + "'";// AND '" + parentId + "' in parents";
        //   Logger.info(TAG + ": isFolderExists(): Query= " + query);
        request = request.setQ(query);
        FileList files = request.execute();
        //   Logger.info(TAG + ": isFolderExists(): List Size =" + files.getItems().size());
        if (files.getItems().size() == 0) //if the size is zero, then the folder doesn't exist
            return "";
        else{

            //since google drive allows to have multiple folders with the same title (name)
            //we select the first file in the list to return
            // return files.getItems().get(0);
            folderpai=(files.getItems().get(0).getId());
            return folderpai;
        }
    }
    public void getDriveContents()
    {

        final String finalFolderpai = folderpai;

        mResultList = new ArrayList<File>();
        Drive.Files f1 = mService.files();
        Drive.Files.List request = null;
        try{
            do
            {
                try
                {
                    folderpai=getExistsFolder(mService, localConfig.getNomeven());
                    request = f1.list();
                    request.setQ("'"+folderpai+"' in parents and trashed=false");
                    FileList fileList = request.execute();

                    mResultList.addAll(fileList.getItems());
                    request.setPageToken(fileList.getNextPageToken());
                } catch (UserRecoverableAuthIOException e) {
                    startActivityForResult(e.getIntent(), REQUEST_AUTHORIZATION);
                } catch (IOException e) {
                    e.printStackTrace();
                    if (request != null)
                    {
                        request.setPageToken(null);
                    }
                }
            } while (request.getPageToken() !=null && request.getPageToken().length() > 0);

        }catch(Exception e){

        }

    }

    public void getDriveContents(String mod)
    {

        final String finalFolderpai = folderpai;

        mResultList = new ArrayList<File>();
        Drive.Files f1 = mService.files();
        Drive.Files.List request = null;
        try{
            do
            {
                try
                {
                    folderpai=getExistsFolder(mService, mod);
                    request = f1.list();
                    request.setQ("'"+folderpai+"' in parents and trashed=false");
                    FileList fileList = request.execute();

                    mResultList.addAll(fileList.getItems());
                    request.setPageToken(fileList.getNextPageToken());
                } catch (UserRecoverableAuthIOException e) {
                    startActivityForResult(e.getIntent(), REQUEST_AUTHORIZATION);
                } catch (IOException e) {
                    e.printStackTrace();
                    if (request != null)
                    {
                        request.setPageToken(null);
                    }
                }
            } while (request.getPageToken() !=null && request.getPageToken().length() > 0);

        }catch(Exception e){

        }

    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data)
    {
        switch (requestCode)
        {
            case REQUEST_ACCOUNT_PICKER:
                if (resultCode == RESULT_OK && data != null && data.getExtras() != null)
                {
                    String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        mCredential.setSelectedAccountName(accountName);
                        mService = getDriveService(mCredential);
                    }
                }
                break;
            case REQUEST_AUTHORIZATION:
                if (resultCode == Activity.RESULT_OK) {
                    //account already picked
                } else {
                    startActivityForResult(mCredential.newChooseAccountIntent(), REQUEST_ACCOUNT_PICKER);
                }
                break;
            case RESULT_STORE_FILE:
                mFileUri = data.getData();
                // Save the file to Google Drive

                break;
        }
    }


    public ArrayList<String> saveFileToDrive(final String paramString, String mod)
    {
        erro=false;
        ArrayList<String> erro = new ArrayList<String>(2);
        erro.add(0,"");
        erro.add(1,"");
        com.google.api.services.drive.model.File body = null;
        ContentResolver cR = null;
        FileContent mediaContent=null;
        try
        {
            String str2;
            String str1=paramString;
            if(!mod.equalsIgnoreCase("backup")&&(!mod.equalsIgnoreCase("backupgen"))){
                str1 = paramString + ".db";
                str2 = ConfigVendedor.getExternalStorageDirectory() + "/" + str1;
            }else {
                str2 = ConfigVendedor.getExternalStorageDirectoryBackupSeg() + "/" + str1;

            }

            mFileUri = Uri.parse(str2);

            cR = mContext.getContentResolver();
            // File's binary content
            java.io.File fileContent = new java.io.File(mFileUri.getPath());
             mediaContent = new FileContent(cR.getType(mFileUri), fileContent);

            // File's meta data.
             body = new com.google.api.services.drive.model.File();
            body.setTitle(fileContent.getName());
            body.setMimeType(cR.getType(mFileUri));

            if(mod.equalsIgnoreCase("backup")||(mod.equalsIgnoreCase("backupgen"))){
                criarPasta(localConfig.getNomeven());
                criarPasta("Atualizacoes");
                getExistsFolder(mService, localConfig.getNomeven());
                body.setParents(Arrays.asList(new ParentReference().setId(getExistsFolder(mService,localConfig.getNomeven()))));
            }


            com.google.api.services.drive.Drive.Files f1 = mService.files();
            com.google.api.services.drive.Drive.Files.Insert i1 = f1.insert(body, mediaContent);
            com.google.api.services.drive.model.File file = i1.execute();


        } catch (UserRecoverableAuthIOException e) {
            startActivityForResult(e.getIntent(), REQUEST_AUTHORIZATION);
            erro.set(1,e.toString());
            erro.set(0,e.toString());
        } catch (IOException e) {
            e.printStackTrace();

            showToast("Erro ao trasnferir , verifique sua internet " + e.toString());
            erro.set(1,e.toString());
            erro.set(0, "Erro ao trasnferir , verifique sua internet " );
        }
        //SEGUNDA TENTATIVA
        if(!erro.get(1).equalsIgnoreCase("")){
            erro.set(1,"");
            erro.set(0,"");
            try
            {
                java.io.File fil = new java.io.File(mFileUri.getPath());
                InputStream is = new FileInputStream(fil);
                InputStreamContent mediaContent2 = new InputStreamContent(cR.getType(mFileUri), is);
                mediaContent2.setLength(fil.length());
                File destFile = null;
                Drive.Files.Insert insert = mService.files().insert(body, mediaContent);
                MediaHttpUploader  uploader = insert.getMediaHttpUploader();
                uploader.setDirectUploadEnabled(false);
                destFile = insert.execute();
                String e="r";
            } catch (UserRecoverableAuthIOException e) {
                startActivityForResult(e.getIntent(), REQUEST_AUTHORIZATION);
                erro.set(1,e.toString());
                erro.set(0,e.toString());
            } catch (IOException e) {
                e.printStackTrace();
                showToast("Erro ao trasnferir , verifique sua internet " + e.toString());
                erro.set(1,e.toString());
                erro.set(0, "Erro ao trasnferir , verifique sua internet " );
            }

         }
        return erro;
    }
    public void showToast(final String toast) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public boolean downloadItemFromList(String arquivo,String diretorio)
    {
        boolean erro=true;
        mDLVal =arquivo;
        for(File tmp : mResultList)
        {
            if (String.valueOf(tmp.getModifiedDate()).equalsIgnoreCase(mDLVal)||tmp.getTitle().equalsIgnoreCase(mDLVal))
            {
                if (tmp.getDownloadUrl() != null && tmp.getDownloadUrl().length() >0)
                {
                    try
                    {
                        com.google.api.client.http.HttpResponse resp =
                                mService.getRequestFactory()
                                        .buildGetRequest(new GenericUrl(tmp.getDownloadUrl()))
                                        .execute();
                        InputStream iStream = resp.getContent();
                        try
                        {
                            final java.io.File file;
                            if(diretorio.equalsIgnoreCase("Atualizacoes")){
                                file = new java.io.File(ConfigVendedor.getExternalStorageDirectoryVs(),
                                        tmp.getTitle());
                            }else if(diretorio.equalsIgnoreCase("backup")){
                                file = new java.io.File(ConfigVendedor.getExternalStorageDirectoryBackupSeg(),
                                        tmp.getTitle());
                            }else{
                                file = new java.io.File(ConfigVendedor.getExternalStorageDirectory(),
                                        tmp.getTitle());
                            }
                            storeFile(file, iStream);
                            erro=false;
                        } finally {
                            iStream.close();
                        }
                    } catch (IOException e) {
                        erro=false;
                        e.printStackTrace();

                    }
                }
            }
        }
        return erro;

    }


    public boolean downloadItemFromList(String arquivo)
    {
        boolean erro=true;
        mDLVal =arquivo;
        for(File tmp : mResultList)
        {
            if (String.valueOf(tmp.getModifiedDate()).equalsIgnoreCase(mDLVal)||tmp.getTitle().equalsIgnoreCase(mDLVal))
            {
                if (tmp.getDownloadUrl() != null && tmp.getDownloadUrl().length() >0)
                {
                    try
                    {
                        com.google.api.client.http.HttpResponse resp =
                                mService.getRequestFactory()
                                        .buildGetRequest(new GenericUrl(tmp.getDownloadUrl()))
                                        .execute();
                        InputStream iStream = resp.getContent();
                        try
                        {
                            final java.io.File file = new java.io.File(ConfigVendedor.getExternalStorageDirectory(),
                                    tmp.getTitle());

                            storeFile(file, iStream);
                            erro=false;
                        } finally {
                            iStream.close();
                        }

                    } catch (IOException e) {
                        erro=false;
                        e.printStackTrace();

                    }
                }
            }
        }
        return erro;

    }

    private void storeFile(java.io.File file, InputStream iStream)
    {
        try
        {
            final OutputStream oStream = new FileOutputStream(file);
            try
            {
                try
                {
                    final byte[] buffer = new byte[1024];
                    int read;
                    while ((read = iStream.read(buffer)) != -1)
                    {
                        oStream.write(buffer, 0, read);
                    }
                    oStream.flush();
                } finally {
                    oStream.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static Drive getmService() {
        return mService;
    }

    public static void setmService(Drive mService) {
        GoogleDrive.mService = mService;
    }

    public GoogleAccountCredential getmCredential() {
        return mCredential;
    }

    public void setmCredential(GoogleAccountCredential mCredential) {
        this.mCredential = mCredential;
    }

    public List<File> getmResultList() {
        return mResultList;
    }

    public void setmResultList(List<File> mResultList) {
        this.mResultList = mResultList;
    }


}
