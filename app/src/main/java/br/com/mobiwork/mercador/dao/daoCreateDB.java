package br.com.mobiwork.mercador.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import br.com.mobiwork.mercador.R;
import br.com.mobiwork.mercador.util.AtuTabela;

public class daoCreateDB extends SQLiteOpenHelper {
    private static final String NOME_BD = "MercadoDB";
    private static final int VERSAO_BD =20;
    private static final String LOG_TAG = "MercadoDB";
    private  Context contexto;
    private AtuTabela atu;

    public daoCreateDB(Context context) {
        super(context, NOME_BD, null, VERSAO_BD);
        this.contexto = context;
        atu = new AtuTabela();
    }


    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.beginTransaction();

        try
        {
            ExecutarComandosSQL(db, contexto.getString(R.string.tabela_pedidos).split("\n"));
            ExecutarComandosSQL(db, contexto.getString(R.string.tabela_itensPedido).split("\n"));
            ExecutarComandosSQL(db, contexto.getString(R.string.tabela_precadastro).split("\n"));
            ExecutarComandosSQL(db, contexto.getString(R.string.tabela_loteEnvio).split("\n"));
            ExecutarComandosSQL(db, contexto.getString(R.string.tabela_cRecebPedido).split("\n"));
            ExecutarComandosSQL(db, contexto.getString(R.string.tabela_cRecebpago).split("\n"));
            ExecutarComandosSQL(db, contexto.getString(R.string.tabela_precadexist).split("\n"));
            ExecutarComandosSQL(db, contexto.getString(R.string.tabela_visitas).split("\n"));
            ExecutarComandosSQL(db, contexto.getString(R.string.tabela_histVis).split("\n"));
            ExecutarComandosSQL(db, contexto.getString(R.string.tabela_configserv).split("\n"));
            db.setTransactionSuccessful();
        }
        catch (SQLException e)
        {
            Log.e("Erro ao criar as tabelas e testar os dados", e.toString());
        }
        finally
        {
            db.endTransaction();
        }

        /*for (int i = 2; i <= VERSAO_BD; i++) {
             atualizaDBSQL(db,0,i);
        }*/
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        atualizaDBSQL(db, oldVersion, newVersion);

    }



    private void ExecutarComandosSQL(SQLiteDatabase paramSQLiteDatabase, String[] paramArrayOfString)
    {
        int i = paramArrayOfString.length;
        for (int j = 0; j < i; j++)
        {
            String str = paramArrayOfString[j];
            if (str.trim().length() > 0) {
                try {
                    paramSQLiteDatabase.execSQL(str);
                }catch (Exception e){

                }
            }
        }
    }




    private void atualizaDBSQL(SQLiteDatabase paramSQLiteDatabase, int paramInt1, int paramInt2)
    {
        Log.w("MercadoDB", "Atualizando a base de dados da versão " + paramInt1 + " para " + paramInt2 + ", que destruirá todos os dados antigos");
        paramSQLiteDatabase.beginTransaction();

        try
        {
            if(paramInt1<6&&paramInt2>=6){
                ExecutarComandosSQL(paramSQLiteDatabase, contexto.getString(R.string.tabela_precadexist).split("\n"));
                ExecutarComandosSQL(paramSQLiteDatabase, contexto.getString(R.string.tabela_visitas).split("\n"));
                if(!atu.verificar(paramSQLiteDatabase,"pedidos","recebimento")){
                    ExecutarComandosSQL(paramSQLiteDatabase, contexto.getString(R.string.atualiza_bd_vs2_12).split("\n"));
                }
                if(!atu.verificar(paramSQLiteDatabase,"pedidos","limcredped")){
                    ExecutarComandosSQL(paramSQLiteDatabase, contexto.getString(R.string.atualiza_bd_vs2_13).split("\n"));
                }
                if(!atu.verificar(paramSQLiteDatabase,"pedidos","saldoverba")){
                    ExecutarComandosSQL(paramSQLiteDatabase, contexto.getString(R.string.atualiza_bd_vs2_7).split("\n"));
                }


            }
            if(paramInt1<7&&paramInt2>=7){
                ExecutarComandosSQL(paramSQLiteDatabase, contexto.getString(R.string.tabela_histVis).split("\n"));
                ExecutarComandosSQL(paramSQLiteDatabase, contexto.getString(R.string.tabela_configserv).split("\n"));
               /* if(!atu.verificar(paramSQLiteDatabase,"pedidos","peso")){
                    ExecutarComandosSQL(paramSQLiteDatabase, contexto.getString(R.string.atualiza_bd_vs2_13).split("\n"));
                }*/
                if(!atu.verificar(paramSQLiteDatabase,"pedidos","pesotot")){
                    ExecutarComandosSQL(paramSQLiteDatabase, contexto.getString(R.string.atualiza_bd_vs2_14).split("\n"));
                }
                if(!atu.verificar(paramSQLiteDatabase,"pedidos","data")){
                    ExecutarComandosSQL(paramSQLiteDatabase, contexto.getString(R.string.atualiza_bd_vs2_16).split("\n"));
                }
                if(!atu.verificar(paramSQLiteDatabase,"itensPedido","pesoliq")){
                    ExecutarComandosSQL(paramSQLiteDatabase, contexto.getString(R.string.atualiza_bd_vs2_15).split("\n"));
                }
                if(!atu.verificar(paramSQLiteDatabase,"pedidos","id_visita")){
                    ExecutarComandosSQL(paramSQLiteDatabase, contexto.getString(R.string.atualiza_bd_vs2_17).split("\n"));
                }
                if(!atu.verificar(paramSQLiteDatabase,"visitas","idVisita")){
                    ExecutarComandosSQL(paramSQLiteDatabase, contexto.getString(R.string.atualiza_bd_vs2_18).split("\n"));
                }
            }
            if(paramInt1<8&&paramInt2>=8){
                if(!atu.verificar(paramSQLiteDatabase,"itensPedido","largura")){
                    ExecutarComandosSQL(paramSQLiteDatabase, contexto.getString(R.string.atualiza_bd_vs2_19).split("\n"));
                }
                if(!atu.verificar(paramSQLiteDatabase,"itensPedido","espessura")){
                    ExecutarComandosSQL(paramSQLiteDatabase, contexto.getString(R.string.atualiza_bd_vs2_20).split("\n"));
                }
                if(!atu.verificar(paramSQLiteDatabase,"itensPedido","comprimento")){
                    ExecutarComandosSQL(paramSQLiteDatabase, contexto.getString(R.string.atualiza_bd_vs2_21).split("\n"));
                }
                if(!atu.verificar(paramSQLiteDatabase,"itensPedido","quantcalc")){
                    ExecutarComandosSQL(paramSQLiteDatabase, contexto.getString(R.string.atualiza_bd_vs2_22).split("\n"));
                }
            }
            if(paramInt1<11&&paramInt2>=11){
                if(!atu.verificar(paramSQLiteDatabase,"itensPedido","comissao")){
                    ExecutarComandosSQL(paramSQLiteDatabase, contexto.getString(R.string.atualiza_bd_vs2_23).split("\n"));
                }

            }
            if(paramInt1<12&&paramInt2>=12){
                if(!atu.verificar(paramSQLiteDatabase,"pedidos","comistot")){
                    ExecutarComandosSQL(paramSQLiteDatabase, contexto.getString(R.string.atualiza_bd_vs2_24).split("\n"));
                }

            }
            if(paramInt1<14&&paramInt2>=14){
                if(!atu.verificar(paramSQLiteDatabase,"itensPedido","preco_st")){
                    ExecutarComandosSQL(paramSQLiteDatabase, contexto.getString(R.string.atualiza_bd_vs2_25).split("\n"));
                }

            }
            if(paramInt1<15&&paramInt2>=15){
                if(!atu.verificar(paramSQLiteDatabase,"precadastro","cod_vendedor")){
                    ExecutarComandosSQL(paramSQLiteDatabase, contexto.getString(R.string.atualiza_bd_vs2_26).split("\n"));
                }
                if(!atu.verificar(paramSQLiteDatabase,"precadastro","clianalise")){
                    ExecutarComandosSQL(paramSQLiteDatabase, contexto.getString(R.string.atualiza_bd_vs2_27).split("\n"));
                }
            }

            if(paramInt1<16&&paramInt2>=16){
                if(!atu.verificar(paramSQLiteDatabase,"precadastro","data")){
                    ExecutarComandosSQL(paramSQLiteDatabase, contexto.getString(R.string.atualiza_bd_vs2_28).split("\n"));
                }
            }
            if(paramInt1<17&&paramInt2>=17) {
                if (!atu.verificar(paramSQLiteDatabase, "precadastro", "clipervisita")) {
                    ExecutarComandosSQL(paramSQLiteDatabase, contexto.getString(R.string.atualiza_bd_vs2_29).split("\n"));
                }
                if (!atu.verificar(paramSQLiteDatabase, "precadastro", "clicondimerc")) {
                    ExecutarComandosSQL(paramSQLiteDatabase, contexto.getString(R.string.atualiza_bd_vs2_30).split("\n"));
                }
                if (!atu.verificar(paramSQLiteDatabase, "precadastro", "cliformmerc")) {
                    ExecutarComandosSQL(paramSQLiteDatabase, contexto.getString(R.string.atualiza_bd_vs2_31).split("\n"));
                }
                if (!atu.verificar(paramSQLiteDatabase, "precadastro", "clirotamerc")) {
                    ExecutarComandosSQL(paramSQLiteDatabase, contexto.getString(R.string.atualiza_bd_vs2_32).split("\n"));
                }
            }
            if(paramInt1<19&&paramInt2>=19) {
                if (!atu.verificar(paramSQLiteDatabase, "precadastro", "numero")) {
                    ExecutarComandosSQL(paramSQLiteDatabase, contexto.getString(R.string.atualiza_bd_vs2_33).split("\n"));
                }
            }
            if (!atu.verificar(paramSQLiteDatabase, "precadastro", "data")) {
                ExecutarComandosSQL(paramSQLiteDatabase, contexto.getString(R.string.atualiza_bd_vs2_35).split("\n"));
            }
            paramSQLiteDatabase.setTransactionSuccessful();

            return;
        }
        catch (SQLException localSQLException)
        {
            Log.e("Erro ao atualizar as tabelas e testar os dados", localSQLException.toString());
            throw localSQLException;
        }
        finally
        {
            paramSQLiteDatabase.endTransaction();
        }

    }

    public long insert(SQLiteDatabase db,String table, String nullColumnHack, ContentValues values) {
        return db.insert(table, nullColumnHack, values);
    }

    public static int update(SQLiteDatabase db,String table, ContentValues values, String whereClause, String[] whereArgs){
        return db.update(table, values, whereClause, whereArgs);
    }

    public static int delete(SQLiteDatabase db,String table, String whereClause, String[] whereArgs){
        return db.delete(table, whereClause, whereArgs);
    }

}

