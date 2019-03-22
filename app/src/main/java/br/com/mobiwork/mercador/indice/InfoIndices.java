package br.com.mobiwork.mercador.indice;

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
import br.com.mobiwork.mercador.dao.daoindice_dev;
import br.com.mobiwork.mercador.model.Config;
import br.com.mobiwork.mercador.util.ConfigVendedor;

public class InfoIndices extends Activity {
    private SQLiteDatabase db;
    private ListView loteList;
    private Cursor cursor;
    private ListAdapter adapter;
    private String clienteId;
    private Config config;
    private daoindice_dev ddev;

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
        ddev= new daoindice_dev(this);
        popularLista();
    }

    public void popularLista() {

            cursor = ddev.consultarIndice_Dev(db,clienteId);
            if (cursor.moveToFirst()) {
                adapter = new SimpleCursorAdapter(this, R.layout.indice_dev, cursor, new String[]{"produto", "descricao", "venda", "devolucao","indice"},
                        new int[]{R.id.coditem,R.id.descricaoItem, R.id.txt_venda, R.id.txt_dev, R.id.txt_indice});
                loteList.setAdapter(adapter);
            }
    }

    public void sair(View view) {
        InfoIndices.this.finish();
    }

}