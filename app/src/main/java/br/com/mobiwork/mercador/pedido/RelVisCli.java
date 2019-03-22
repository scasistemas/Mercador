package br.com.mobiwork.mercador.pedido;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import br.com.mobiwork.mercador.R;
import br.com.mobiwork.mercador.dao.daoConfig;
import br.com.mobiwork.mercador.dao.daoCreateDB;
import br.com.mobiwork.mercador.dao.daoCreateDBM;
import br.com.mobiwork.mercador.dao.daoVisitas;
import br.com.mobiwork.mercador.model.Config;
import br.com.mobiwork.mercador.model.Visitas;
import br.com.mobiwork.mercador.util.Alertas;

/**
 * Created by LuisGustavo on 25/11/14.
 */
public class RelVisCli extends Activity {

    private EditText searchText;
    private SQLiteDatabase db,dbconfig,dbped;
    private Cursor cursor,cursor2,cursor3;
    private ListAdapter adapter;
    private ListView clienteList;
    private boolean  isEditPedido;
    private String modo_op;
    private Config config;
    private Spinner cidade;
    private Alertas a;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.localizacliente);
        modo_op  = getIntent().getStringExtra("OP");


        cidade = (Spinner) findViewById(R.id.cidadesp);
        daoCreateDBM dao = new daoCreateDBM(this);
        db =  dao.getWritableDatabase();
        if(config==null){
            this.config = new daoConfig(this).consultar(this.db);
        }
        if(!config.getEmp().equalsIgnoreCase("geg")||config==null){
            cidade.setVisibility(View.GONE);
        }

        daoCreateDB daop = new daoCreateDB(this);
        dbped =  daop.getWritableDatabase();
        a = new Alertas(this);


        searchText = (EditText) findViewById (R.id.searchText);
        searchText.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                popularLista();
            }
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            }
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });
        clienteList = (ListView) findViewById (R.id.list);

        clienteList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
                if (!isEditPedido) {

                    final int[] arrayOfInt = { 0 };
                    String[] arrayOfString = { "Ver Pedido", "Excluir" };
                    final AlertDialog.Builder localBuilder = new AlertDialog.Builder(RelVisCli.this);
                    localBuilder.setTitle("Gerenciamento de Visitas  ").setSingleChoiceItems(arrayOfString, 0, new DialogInterface.OnClickListener()
                    {
                        public void onClick(DialogInterface paramAnonymous2DialogInterface, int paramAnonymous2Int)
                        {
                            arrayOfInt[0] = paramAnonymous2Int;
                        }
                    }).setPositiveButton("OK", new DialogInterface.OnClickListener()
                    {
                        public void onClick(DialogInterface paramAnonymous2DialogInterface, int paramAnonymous2Int)
                        {
                            paramAnonymous2DialogInterface.cancel();
                            String id=    cursor2.getString(cursor2.getColumnIndex("_id"));
                            if(arrayOfInt[0]==0){
                                selecionarAtividadeMenuPricipal(arrayOfInt[0],id);
                                localBuilder.create().cancel();
                            }else{
                                selecionarAtividadeMenuPricipal(arrayOfInt[0],id);
                            }

                        }
                    });
                    localBuilder.create().show();


                }
            }
        });


        popularLista();
    }
    protected void selecionarAtividadeMenuPricipal(int i,String id) {
        Intent ix;
        switch (i) {
            case 0:


                if(cursor2.getString(cursor2.getColumnIndex("_id"))==null){

                    AlertDialog alertDialog = new AlertDialog.Builder(this).create();
                    alertDialog.setTitle("Alerta");
                    alertDialog.setMessage("Não foi realizada nenhuma venda nesta visita");
                    alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });

                    alertDialog.show();

                }else{

                    Intent j = new Intent(RelVisCli.this,PedidosVisitas.class);
                    j.putExtra("idVisita", cursor2.getString(cursor2.getColumnIndex("_id")));
                    RelVisCli.this.startActivityForResult(j,0);
                }

                break;
            case 1:
                daoVisitas dp = new daoVisitas(this);
                Visitas v = new Visitas();
                v.setIdVisita(cursor2.getString(cursor2.getColumnIndex("_id")));
                v.setCliente(cursor2.getString(cursor2.getColumnIndex("cliente")));
                v.setIdvendedor(cursor2.getInt(cursor2.getColumnIndex("idvendedor")));
                v.set_id(id);
                a.AlertaSinc(dp.excluir(dbped,v));
                popularLista();
                break;
        }
    }

    private void startActivityForResult(int i, int requestCode) {
    }

    public void showToast(final String toast) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), toast, Toast.LENGTH_SHORT).show();
            }
        });
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 0) {
            searchText.setText("");
            popularLista();

        }
    }

    public void popularLista() {
        if (searchText != null){
            cursor2 = dbped.rawQuery("SELECT *,strftime('%d/%m/%Y',data) as data2 FROM histvis WHERE nomeCli LIKE ? order by nomeCli",
                    //cursor = db.rawQuery("SELECT _id,  razao , endereco FROM clientes WHERE razao LIKE ? order by razao",
                    //(case when fantasia is not null then fantasia else razao end)
                    //
                    new String[]{searchText.getText().toString() + "%"});
        }

        adapter = new SimpleCursorAdapter(this,R.layout.list_item,cursor2,new String[] {"nomeCli",  "data2","hora"},
                new int[] {R.id.firstName, R.id.title,R.id.lastName});
        clienteList.setAdapter(adapter);


    }



    public void sair(View view) {
        RelVisCli.this.finish();
    }

    public void limpa(View view) {
        searchText.setText("");
    }

    @Override
    public void onBackPressed() {
        return;
    }
}
