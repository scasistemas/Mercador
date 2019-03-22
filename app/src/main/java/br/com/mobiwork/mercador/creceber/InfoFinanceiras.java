package br.com.mobiwork.mercador.creceber;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import br.com.mobiwork.mercador.R;
import br.com.mobiwork.mercador.dao.daoCreateDBM;
import br.com.mobiwork.mercador.model.Config;
import br.com.mobiwork.mercador.util.ConfigVendedor;

public class InfoFinanceiras extends Activity {
    private SQLiteDatabase db;
    private ListView loteList;
    private boolean  isEditPedido;
    private Cursor cursor;
    private ListAdapter adapter;
    private String clienteId;
    private Config config;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.lotedepedidosenviados);
        daoCreateDBM dao = new daoCreateDBM(this);
        db =  dao.getWritableDatabase();
        clienteId = getIntent().getStringExtra("CLIENTE_ID");

        loteList = (ListView) findViewById (R.id.list);
        this.config = ConfigVendedor.getConfig(this.db);
        popularLista();

    }


    public void popularLista() {

        if(config.getEmp().equalsIgnoreCase("vimilk")) {
            cursor = db.rawQuery("select _id,valor,vlrreceb,serie,parcela,cliente,cnpj,strftime('%d/%m/%Y',vencimento)as vencimento2,vencimento,strftime('%d/%m/%Y',emissao)as emissao2,emissao from creceb tb " +
                    " WHERE tb.cnpj = ?", new String[]{"" + clienteId});
            if (cursor.moveToFirst()) {
                String venc = cursor.getString(cursor.getColumnIndex("vencimento2"));
                if (cursor.getString(cursor.getColumnIndex("vencimento2")) == null) {
                    adapter = new SimpleCursorAdapter(this, R.layout.listacreceber, cursor, new String[]{"emissao2", "vencimento", "valor", "vlrreceb"},
                            new int[]{R.id.lbemissao,R.id.lbvencimento, R.id.valor, R.id.vlrreceb});
                } else {
                    adapter = new SimpleCursorAdapter(this, R.layout.listacreceber, cursor, new String[]{"emissao2", "vencimento2", "valor", "vlrreceb"},
                            new int[]{R.id.lbemissao,R.id.lbvencimento, R.id.valor, R.id.vlrreceb});
                }
                loteList.setAdapter(adapter);
            }
        }else if (config.getEmp().equalsIgnoreCase("jrc")){
            cursor = db.rawQuery("select _id,valor,vlrreceb,serie,parcela,cliente,cnpj,strftime('%d/%m/%Y',vencimento)as vencimento2,vencimento from creceb tb " +
                    " WHERE tb.cnpj = ?", new String[]{"" + clienteId});
            if (cursor.moveToFirst()) {
                String venc=cursor.getString(cursor.getColumnIndex("vencimento2"));
                if(cursor.getString(cursor.getColumnIndex("vencimento2"))==null){
                    adapter = new SimpleCursorAdapter(this,R.layout.listcrereceberemi,cursor,new String[] {"vencimento", "valor",  "vlrreceb"},
                            new int[] { R.id.lbemissao, R.id.valor, R.id.vlrreceb});
                }else{
                    adapter = new SimpleCursorAdapter(this,R.layout.listcrereceberemi,cursor,new String[] {"vencimento2", "valor",  "vlrreceb"},
                            new int[] { R.id.lbemissao, R.id.valor, R.id.vlrreceb});
                }
                loteList.setAdapter(adapter);
            }
        }else{
            cursor = db.rawQuery("select _id,valor,vlrreceb,serie,parcela,cliente,cnpj,strftime('%d/%m/%Y',vencimento)as vencimento2,vencimento from creceb tb " +
                    " WHERE tb.cnpj = ?", new String[]{"" + clienteId});
            if (cursor.moveToFirst()) {
                String venc=cursor.getString(cursor.getColumnIndex("vencimento2"));
                if(cursor.getString(cursor.getColumnIndex("vencimento2"))==null){
                    adapter = new SimpleCursorAdapter(this,R.layout.listcrerecebvenc,cursor,new String[] {"vencimento", "valor",  "vlrreceb"},
                            new int[] { R.id.lbvencimento, R.id.valor, R.id.vlrreceb});
                }else{
                    adapter = new SimpleCursorAdapter(this,R.layout.listcrerecebvenc,cursor,new String[] {"vencimento2", "valor",  "vlrreceb"},
                            new int[] { R.id.lbvencimento, R.id.valor, R.id.vlrreceb});
                }
                loteList.setAdapter(adapter);
            }
        }

    }


    public void sair(View view) {
        InfoFinanceiras.this.finish();
    }


}