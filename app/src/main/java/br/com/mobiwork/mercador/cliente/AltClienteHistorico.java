package br.com.mobiwork.mercador.cliente;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import br.com.mobiwork.mercador.R;
import br.com.mobiwork.mercador.dao.daoCreateDBM;
import br.com.mobiwork.mercador.dao.daoPrecadexist;
import br.com.mobiwork.mercador.model.Config;
import br.com.mobiwork.mercador.util.ConfigVendedor;

/**
 * Created by LuisGustavo on 06/01/2017.
 */
public class AltClienteHistorico extends Activity{

    private ListAdapter adapter;
    private ListView clienteList;
    private daoPrecadexist daodpcadexist;
    private Config config;
    private SQLiteDatabase db;
    private Cursor clientes;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.localizacliente);
        daodpcadexist= new daoPrecadexist(this);
        clienteList = (ListView) findViewById (R.id.list);
        daoCreateDBM dao = new daoCreateDBM(this);
        db =  dao.getWritableDatabase();
        this.config = ConfigVendedor.getConfig(this.db);
        popularLista();
    }

    public void popularLista() {
        if (this.config.getEmp().equalsIgnoreCase("MVitoria") || this.config.getEmp().equalsIgnoreCase("salatiel")
                || this.config.getEmp().equalsIgnoreCase("minasmilk") || this.config.getEmp().equalsIgnoreCase("porto")
                || this.config.getEmp().equalsIgnoreCase("geg") || this.config.getEmp().equalsIgnoreCase("lavrasatacado")
                || this.config.getEmp().equalsIgnoreCase("jrc") || this.config.getEmp().equalsIgnoreCase("grm")
                || this.config.getEmp().equalsIgnoreCase("bomdestino") || this.config.getEmp().equalsIgnoreCase("pdoeletrica")
                || this.config.getEmp().equalsIgnoreCase("pdocarmopolis") || this.config.getEmp().equalsIgnoreCase("lcarm")) {
            clientes=daodpcadexist.pesqPreCadExistente("",true);
        }else{
            clientes=daodpcadexist.pesqPreCadExistente("",false);
        }
        adapter = new SimpleCursorAdapter(this,R.layout.list_item,clientes,new String[] {"fantasia",  "endereco","codCliente","'-'"},
                new int[] {R.id.firstName, R.id.title,R.id.codcli,R.id.traco});
        clienteList.setAdapter(adapter);
    }
}
