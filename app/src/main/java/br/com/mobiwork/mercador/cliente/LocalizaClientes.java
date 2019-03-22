package br.com.mobiwork.mercador.cliente;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import br.com.mobiwork.mercador.R;
import br.com.mobiwork.mercador.dao.daoCliente;
import br.com.mobiwork.mercador.dao.daoConfig;
import br.com.mobiwork.mercador.dao.daoCreateDB;
import br.com.mobiwork.mercador.dao.daoCreateDBM;
import br.com.mobiwork.mercador.dao.daoCreceb;
import br.com.mobiwork.mercador.dao.daoPedido;
import br.com.mobiwork.mercador.dao.daoSetor;
import br.com.mobiwork.mercador.dao.daoVisitas;
import br.com.mobiwork.mercador.model.Cliente;
import br.com.mobiwork.mercador.model.Config;
import br.com.mobiwork.mercador.model.DataHoraPedido;
import br.com.mobiwork.mercador.model.Visitas;
import br.com.mobiwork.mercador.pedido.PedidoVenda;
import br.com.mobiwork.mercador.pedido.PedidosMesmoItem;
import br.com.mobiwork.mercador.util.Alertas;
import br.com.mobiwork.mercador.util.AtuTabela;
import br.com.mobiwork.mercador.util.ConfigVendedor;

public class LocalizaClientes extends Activity
{
    private EditText searchText;
    private SQLiteDatabase db,dbp;
    private Cursor cursor,cursor2;
    private ListAdapter adapter;
    private ListView clienteList;
    private boolean  isEditPedido;
    private String modo_op,clifant;
    private Config config;
    private daoCreceb dcre;
    private daoCliente daop;
    private Cliente cliente;
    Alertas a ;
    private Spinner cidade,spsetor;
    private boolean isSelection;
    private String idVisita;
    private daoPedido dp;
    private AtuTabela atu;




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.localizacliente);
        modo_op  = getIntent().getStringExtra("OP");
        cliente = new Cliente();
        a= new Alertas(this);
        daoCreateDBM dao = new daoCreateDBM(this);
        db =  dao.getWritableDatabase();
        atu= new AtuTabela();
        dcre= new daoCreceb(this);
        daop = new daoCliente(this);
        dp = new daoPedido(this);
        isSelection=false;
        cidade = (Spinner) findViewById(R.id.cidadesp);
        spsetor = (Spinner) findViewById(R.id.setoresp);
        this.config = ConfigVendedor.getConfig(this.db);
        idVisita="";
        if(config.getEmp()==null){
            this.config = new daoConfig(this).consultar(this.db);

        }
        if(config.getEmp()!=null){
            if(!config.getEmp().equalsIgnoreCase("geg")&&!config.getEmp().equalsIgnoreCase("lavrasatacado")&&!config.getEmp().equalsIgnoreCase("minasmilk")&&!config.getEmp().equalsIgnoreCase("salatiel")){
                if(config.getEmp().equalsIgnoreCase("boav")||config.getEmp().equalsIgnoreCase("dten")){
                    spsetor.setVisibility(View.VISIBLE);
                    popularComboSetor();
                }
                cidade.setVisibility(View.GONE);

            }else{
                popularComboCidade();
            }
        }
        final daoSetor ds = new daoSetor(this);
        searchText = (EditText) findViewById (R.id.searchText);
        searchText.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                if(!config.getEmp().equalsIgnoreCase("geg")&&!config.getEmp().equalsIgnoreCase("lavrasatacado")&&!config.getEmp().equalsIgnoreCase("minasmilk")){
                    if(config.getEmp().equalsIgnoreCase("boav")||config.getEmp().equalsIgnoreCase("dten")){
                        if(spsetor.getSelectedItem().toString().equalsIgnoreCase("Selecionar Setor")){
                            popularLista();
                        }else{
                            popularListaS(ds.seleSetor(db, spsetor.getSelectedItem().toString()));
                        }
                    }else{
                        popularLista();
                    }

                }else{
                    if(cidade.getSelectedItem().toString().equalsIgnoreCase("Selecionar Cidade")){
                        popularLista();
                    }else{
                        popularLista(cidade.getSelectedItem().toString());
                    }
                }


            }
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            }
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });
        clienteList = (ListView) findViewById (R.id.list);

        clienteList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public long ano;
            public long mes;
            public long dia;
            String aux, cli, codcli;
            Intent k;
            String z = "";

            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!isEditPedido) {


                    aux = cursor.getString(cursor.getColumnIndex("_id"));
                    codcli = cursor.getString(cursor.getColumnIndex("codCliente"));
                    clifant = cursor.getString(cursor.getColumnIndex("fantasia"));

                    final daoCreateDB dao = new daoCreateDB(getBaseContext());
                    dbp = dao.getWritableDatabase();
                    cliente = daop.consultarPorId(db, aux);

                    if (modo_op.equals("ATENDER_CLIENTE")) {

                        daop.acessoPedidoVenda(LocalizaClientes.this, config, cliente, dp, dbp, codcli, clifant, aux, db, dcre, daop, modo_op);
                    } else {
                        Intent i = null;
                        if (modo_op.equals("CONSULTAR_CLIENTE")) {
                            i = new Intent(LocalizaClientes.this, CadCliente.class);
                            i.putExtra("CLIENTE_ID", aux);
                            i.putExtra("OP", modo_op);
                            i.putExtra("idVisita", idVisita);
                        } else if (modo_op.equalsIgnoreCase("CONSULTAR_CLIENTE_PEDIDOS")) {

                            i = new Intent(LocalizaClientes.this, PedidosMesmoItem.class);
                            i.putExtra("CLIENTE_ID", cursor.getString(cursor.getColumnIndex("_id")));
                            i.putExtra("codcli", cursor.getString(cursor.getColumnIndex("codCliente")));
                            i.putExtra("codigo", cursor.getString(cursor.getColumnIndex("codigo")));
                            i.putExtra("mod_op", "iniciovenda");
                            i.putExtra("vlrreceb", "0");
                            i.putExtra("tolerancia", String.valueOf(config.getDiasTolerancia()));
                            i.putExtra("idVisita", idVisita);
                        }
                        //  k.putExtra("codCliente",codcli);
                        LocalizaClientes.this.startActivityForResult(i, 0);
                        if (modo_op.equals("ATENDER_CLIENTE")) {
                            LocalizaClientes.this.finish();
                        }

                    }

                }
            }
        });
        clienteList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           int pos, long id) {
                // TODO Auto-generated method stub
                opcoesClientes(cursor.getString(cursor.getColumnIndex("codCliente")));
                return true;
            }
        });
        popularLista();
    }

    private void opcoesClientes(final String cliente) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.op_adicionais)
                .setItems(R.array.op_menu_opcoes_clientes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialoginterface, int i) {
                        selecionar_op_clientes(i,cliente);
                    }
                }).show();
    }
    protected void selecionar_op_clientes(int i,String cliente) {
        Intent ix;
        switch (i) {
            case 0:
                daop.infoFinan(cliente,db);
                break;
        }
    }

    public void popularComboCidade(){

        cursor = daop.seleCidade(db);
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,R.layout.list_item,cursor,new String[] {"cidade"},
                new int[] {R.id.comboName});

        ArrayList cidades = new ArrayList();
        cidades.add("Selecionar Cidade");
        if(cursor.moveToFirst()){
            do{
                cidades.add(cursor.getString(cursor.getColumnIndex("cidade")));
            }while (cursor.moveToNext());
        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, cidades);
        ArrayAdapter<String> spinnerArrayAdapter = arrayAdapter;
        spinnerArrayAdapter.setDropDownViewResource( android.R.layout.simple_spinner_item);
        cidade.setAdapter(spinnerArrayAdapter);



        cidade.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent,
                                       View view, int position, long id) {
                if (isSelection){
                    popularLista(cidade.getSelectedItem().toString());
                }
                isSelection=true;
            }
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });



    }

    public void popularComboSetor(){

        final daoSetor ds = new daoSetor(this);
        final Cursor cursorset = ds.seleSetor(db);
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,R.layout.list_item,cursorset,new String[] {"setdescri"},
                new int[] {R.id.comboName});

        ArrayList setor = new ArrayList();
        setor.add("Selecionar Setor");
        if(cursorset!=null) {
            if (cursorset.moveToFirst()) {
                do {
                    setor.add(cursorset.getString(cursorset.getColumnIndex("setdescri")));
                } while (cursorset.moveToNext());
            }
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_spinner_item, setor);
            ArrayAdapter<String> spinnerArrayAdapter = arrayAdapter;
            spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
            spsetor.setAdapter(spinnerArrayAdapter);


            spsetor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                public void onItemSelected(AdapterView<?> parent,
                                           View view, int position, long id) {
                    if (isSelection) {
                        if (spsetor.getSelectedItem().toString().equalsIgnoreCase("Selecionar Setor")) {
                            popularLista();
                        } else {
                            popularListaS(ds.seleSetor(db, spsetor.getSelectedItem().toString()));
                        }
                    }
                    isSelection = true;
                }


                public void onNothingSelected(AdapterView<?> arg0) {
                }
            });
        }

    }



    public void irPedidoVenda(String aux1,String codcli1,String modo_op1,String vlrreceb){

        Visitas v = new Visitas();
        daoVisitas dv = new daoVisitas(getBaseContext());
        v.setCliente(codcli1);
        v.setNomeCli(clifant);
        v.setIdvendedor(config.getVendid());
        v.set_id(aux1);
        SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
        Date dataAtual = new Date(System.currentTimeMillis());
        v.setData(sd.format(dataAtual));
        v.setIdVisita(idVisita);
        dv.inserir(dbp, v, DataHoraPedido.criarDataHoraPedido());

        Intent k;
        k = new Intent(LocalizaClientes.this,PedidoVenda.class);
        k.putExtra("PEDIDO_ID", "");
        k.putExtra("CLIENTE_ID", aux1);
        k.putExtra("OP", modo_op);
        k.putExtra("codCliente",codcli1);
        k.putExtra("tolerancia",String.valueOf(config.getDiasTolerancia()));
        k.putExtra("idVisita",idVisita);
        k.putExtra("codigo",cursor.getString(cursor.getColumnIndex("codigo")));
        if(vlrreceb.equalsIgnoreCase("")){
            vlrreceb="0";
        }
        if (config.getEmp().equalsIgnoreCase("boav")) {
            k.putExtra("limite", daop.consultarPorIdLimite(db, aux1));
        }
        k.putExtra("vlrreceb",vlrreceb);
        LocalizaClientes.this.startActivityForResult(k,0);

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


    public void popularLista(String cidade) {
        String where="";
        if(!cidade.trim().equals("")) {
            cidade = cidade.trim();
            if (!cidade.equalsIgnoreCase("") && !cidade.equalsIgnoreCase("Selecionar Cidade")) {
                if (searchText != null) {
                    where = "and rtrim(cidade)='" + cidade + "'";
                } else {
                    where = "where rtrim(cidade)='" + cidade + "'";
                }
            }
        }else{
            if (!cidade.equalsIgnoreCase("") && !cidade.equalsIgnoreCase("Selecionar Cidade")) {
                if (searchText != null) {
                    where = "and cidade='" + cidade + "'";
                } else {
                    where = "where cidade='" + cidade + "'";
                }
            }
        }
        if(!cursor.isClosed()){
            cursor.close();
        }

        if(config.getEmp()!=null) {
            if(this.config.getEmp().equalsIgnoreCase("MVitoria")||this.config.getEmp().equalsIgnoreCase("salatiel")||this.config.getEmp().equalsIgnoreCase("minasmilk")||this.config.getEmp().equalsIgnoreCase("porto")
                    ||this.config.getEmp().equalsIgnoreCase("geg")||this.config.getEmp().equalsIgnoreCase("lavrasatacado")||this.config.getEmp().equalsIgnoreCase("jrc")
                    ||this.config.getEmp().equalsIgnoreCase("grm")||this.config.getEmp().equalsIgnoreCase("bomdestino")||this.config.getEmp().equalsIgnoreCase("pdoeletrica")||this.config.getEmp().equalsIgnoreCase("pdocarmopolis")||this.config.getEmp().equalsIgnoreCase("lcarm")||this.config.getEmp().equalsIgnoreCase("benditac")) {
                if (searchText != null) {
                    cursor = db.rawQuery("  SELECT codigo,_id,  razao ,CASE trim(clientes.fantasia)  WHEN '' THEN  razao else fantasia end as fantasia, endereco,codCliente,'-' FROM clientes WHERE (razao LIKE ? or codCliente like ?) " + where + " order by razao",
                            new String[]{searchText.getText().toString() + "%",searchText.getText().toString() + "%"});
                } else {
                    cursor = db.rawQuery("  SELECT codigo,_id,  razao ,CASE trim(clientes.fantasia)  WHEN '' THEN  razao else fantasia end as fantasia, endereco,codCliente,'-' FROM clientes " + where + " order by razao", null);
                }
                adapter = new SimpleCursorAdapter(this,R.layout.list_item,cursor,new String[] {"razao",  "endereco","codCliente","'-'"},
                        new int[] {R.id.firstName, R.id.title,R.id.codcli,R.id.traco});
            }else{
                if (searchText != null) {
                    cursor = db.rawQuery("  SELECT codigo,_id,  CASE trim(clientes.fantasia)  WHEN '' THEN  razao else fantasia end as fantasia ,razao, endereco,codCliente,'-' FROM clientes WHERE (fantasia LIKE ? or codCliente like ?) " + where + " order by fantasia",

                            new String[]{searchText.getText().toString() + "%",searchText.getText().toString() + "%"});
                } else {
                    cursor = db.rawQuery("  SELECT codigo,_id,  CASE trim(clientes.fantasia)  WHEN '' THEN  razao else fantasia end as fantasia ,razao, endereco,codCliente,'-' FROM clientes " + where + " order by fantasia", null);

                }
                adapter = new SimpleCursorAdapter(this,R.layout.list_item,cursor,new String[] {"fantasia",  "endereco","codCliente","'-'"},
                        new int[] {R.id.firstName, R.id.title,R.id.codcli,R.id.traco});
            }

        }
        clienteList.setAdapter(adapter);

    }

    public void popularListaS(String setor) {
        String where="";
        if(!setor.trim().equals("")) {
            setor = setor.trim();
            if (!setor.equalsIgnoreCase("") && !setor.equalsIgnoreCase("Selecionar Setor")) {
                if (searchText != null) {
                    where = "and rtrim(clisetor)='" + setor + "'";
                } else {
                    where = "where rtrim(clisetor)='" + setor + "'";
                }
            }
        }else{
            if (!setor.equalsIgnoreCase("") && !setor.equalsIgnoreCase("Selecionar Setor")) {
                if (searchText != null) {
                    where = "and clisetor='" + setor + "'";
                } else {
                    where = "where clisetor='" + setor + "'";
                }
            }
        }
        if(!cursor.isClosed()){
            cursor.close();
        }

        if(config.getEmp()!=null) {
            if(this.config.getEmp().equalsIgnoreCase("MVitoria")||this.config.getEmp().equalsIgnoreCase("salatiel")||this.config.getEmp().equalsIgnoreCase("minasmilk")||this.config.getEmp().equalsIgnoreCase("porto")
                    ||this.config.getEmp().equalsIgnoreCase("geg")||this.config.getEmp().equalsIgnoreCase("lavrasatacado")||this.config.getEmp().equalsIgnoreCase("jrc")
                    ||this.config.getEmp().equalsIgnoreCase("grm")||this.config.getEmp().equalsIgnoreCase("bomdestino")||this.config.getEmp().equalsIgnoreCase("pdoeletrica")||this.config.getEmp().equalsIgnoreCase("pdocarmopolis")||this.config.getEmp().equalsIgnoreCase("lcarm")||this.config.getEmp().equalsIgnoreCase("benditac")) {
                if (searchText != null) {
                    cursor = db.rawQuery("  SELECT codigo,_id,  razao ,CASE trim(clientes.fantasia)  WHEN '' THEN  razao else fantasia end as fantasia, endereco,codCliente,'-' FROM clientes WHERE (razao LIKE ? or codCliente like ?) " + where + " order by razao",

                            new String[]{searchText.getText().toString() + "%",searchText.getText().toString() + "%"});
                } else {
                    cursor = db.rawQuery("  SELECT codigo,_id,  razao ,CASE trim(clientes.fantasia)  WHEN '' THEN  razao else fantasia end as fantasia, endereco,codCliente,'-' FROM clientes " + where + " order by razao", null);

            }
                adapter = new SimpleCursorAdapter(this,R.layout.list_item,cursor,new String[] {"razao",  "endereco","codCliente","'-'"},
                        new int[] {R.id.firstName, R.id.title,R.id.codcli,R.id.traco});
            }else{
                if (searchText != null) {
                    cursor = db.rawQuery("  SELECT codigo,_id,  CASE trim(clientes.fantasia)  WHEN '' THEN  razao else fantasia end as fantasia ,razao, endereco,codCliente,'-' FROM clientes WHERE (fantasia LIKE ? or codCliente like ?) " + where + " order by fantasia",

                            new String[]{searchText.getText().toString() + "%",searchText.getText().toString() + "%"});
                } else {
                    cursor = db.rawQuery("  SELECT codigo,_id,  CASE trim(clientes.fantasia)  WHEN '' THEN  razao else fantasia end as fantasia ,razao, endereco,codCliente,'-' FROM clientes " + where + " order by fantasia", null);

                }
                if(config.getEmp().equalsIgnoreCase("boav")){
                    adapter = new SimpleCursorAdapter(this,R.layout.list_item_cli_boav,cursor,new String[] {"fantasia",  "endereco"},
                            new int[] {R.id.firstName, R.id.title});
                }else{
                    adapter = new SimpleCursorAdapter(this,R.layout.list_item,cursor,new String[] {"fantasia",  "endereco","codCliente","'-'"},
                            new int[] {R.id.firstName, R.id.title,R.id.codcli,R.id.traco});
                }
            }
        }
        clienteList.setAdapter(adapter);

    }


    public void popularLista() {
        if (searchText != null){
            if(config.getEmp()!=null){
                if(this.config.getEmp().equalsIgnoreCase("MVitoria")||this.config.getEmp().equalsIgnoreCase("salatiel")||this.config.getEmp().equalsIgnoreCase("minasmilk")||this.config.getEmp().equalsIgnoreCase("porto")
                        ||this.config.getEmp().equalsIgnoreCase("geg")||this.config.getEmp().equalsIgnoreCase("lavrasatacado")
                        ||this.config.getEmp().equalsIgnoreCase("jrc")||this.config.getEmp().equalsIgnoreCase("grm")||this.config.getEmp().equalsIgnoreCase("bomdestino")||this.config.getEmp().equalsIgnoreCase("pdoeletrica")
                        ||this.config.getEmp().equalsIgnoreCase("pdocarmopolis")||this.config.getEmp().equalsIgnoreCase("lcarm")||this.config.getEmp().equalsIgnoreCase("benditac")
                        ||this.config.getEmp().equalsIgnoreCase("marcio_o")){
                    if(config.getEmp().equalsIgnoreCase("jrc")) {
                        cursor = this.daop.pesqClientes(db, "%"+searchText.getText().toString()+"%", "razao", "codCliente");
                    }else{
                        cursor = this.daop.pesqClientes(db, searchText.getText().toString(), "razao", "codCliente");
                    }
                }else{
                    cursor=this.daop.pesqClientes(db, searchText.getText().toString(), "fantasia","codCliente");
                }
            }

            if(config.getEmp()!=null){
                if(this.config.getEmp().equalsIgnoreCase("MVitoria")||this.config.getEmp().equalsIgnoreCase("salatiel")||this.config.getEmp().equalsIgnoreCase("minasmilk")||this.config.getEmp().equalsIgnoreCase("porto")
                        ||this.config.getEmp().equalsIgnoreCase("geg")||this.config.getEmp().equalsIgnoreCase("lavrasatacado")||
                        this.config.getEmp().equalsIgnoreCase("jrc")||this.config.getEmp().equalsIgnoreCase("grm")||this.config.getEmp().equalsIgnoreCase("bomdestino")||this.config.getEmp().equalsIgnoreCase("pdoeletrica")||this.config.getEmp().equalsIgnoreCase("pdocarmopolis")||this.config.getEmp().equalsIgnoreCase("lcarm")){
                    adapter = new SimpleCursorAdapter(this,R.layout.list_item,cursor,new String[] {"razao",  "endereco","codCliente","'-'"},
                            new int[] {R.id.firstName, R.id.title,R.id.codcli,R.id.traco});
                }else{
                    if(config.getEmp().equalsIgnoreCase("boav")){
                        adapter = new SimpleCursorAdapter(this,R.layout.list_item_cli_boav,cursor,new String[] {"fantasia",  "endereco"},
                                new int[] {R.id.firstName, R.id.title});
                    }else{
                        adapter = new SimpleCursorAdapter(this,R.layout.list_item,cursor,new String[] {"fantasia",  "endereco","codCliente","'-'"},
                                new int[] {R.id.firstName, R.id.title,R.id.codcli,R.id.traco});
                    }


                }
                clienteList.setAdapter(adapter);
            }
        }
    }


    public void sair(View view) {
        LocalizaClientes.this.finish();
    }

    public void limpa(View view) {
        searchText.setText("");
    }

    @Override
    public void onBackPressed() {
        return;
    }
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        View v = getCurrentFocus();

        if (v != null &&
                (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_MOVE) &&
                v instanceof EditText &&
                !v.getClass().getName().startsWith("android.webkit.")) {
            int scrcoords[] = new int[2];
            v.getLocationOnScreen(scrcoords);
            float x = ev.getRawX() + v.getLeft() - scrcoords[0];
            float y = ev.getRawY() + v.getTop() - scrcoords[1];

            if (x < v.getLeft() || x > v.getRight() || y < v.getTop() || y > v.getBottom())
                hideKeyboard(this);
        }
        return super.dispatchTouchEvent(ev);
    }

    public static void hideKeyboard(Activity activity) {
        if (activity != null && activity.getWindow() != null && activity.getWindow().getDecorView() != null) {
            InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(activity.getWindow().getDecorView().getWindowToken(), 0);
        }
    }


}