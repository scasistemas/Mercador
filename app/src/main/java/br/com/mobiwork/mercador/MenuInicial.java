package br.com.mobiwork.mercador;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import br.com.mobiwork.mercador.backup.ListBackupBaixados;
import br.com.mobiwork.mercador.cliente.AltClienteHistorico;
import br.com.mobiwork.mercador.cliente.CadCliente;
import br.com.mobiwork.mercador.cliente.GerenciarVisitas;
import br.com.mobiwork.mercador.cliente.LocalizaClientes;
import br.com.mobiwork.mercador.cliente.LocalizaRotas;
import br.com.mobiwork.mercador.creceber.GerenciaRestaure;
import br.com.mobiwork.mercador.dao.daoConfig;
import br.com.mobiwork.mercador.dao.daoCreateDB;
import br.com.mobiwork.mercador.dao.daoCreateDBEnvio;
import br.com.mobiwork.mercador.dao.daoCreateDBM;
import br.com.mobiwork.mercador.dao.daoPedido;
import br.com.mobiwork.mercador.gerdados.GerenciadorDeDados;
import br.com.mobiwork.mercador.informacoes.Informacoes;
import br.com.mobiwork.mercador.model.Config;
import br.com.mobiwork.mercador.pedido.LoteDePedidosEnviados;
import br.com.mobiwork.mercador.pedido.PedidosNEnviados;
import br.com.mobiwork.mercador.pedido.RelComis;
import br.com.mobiwork.mercador.pedido.RelPeso;
import br.com.mobiwork.mercador.pedido.RelVlrVenda;
import br.com.mobiwork.mercador.sinc.AtuVersao;
import br.com.mobiwork.mercador.sinc.Conexao;
import br.com.mobiwork.mercador.sinc.GoogleDriveAtu;
import br.com.mobiwork.mercador.sinc.ReceberInfoConfig;
import br.com.mobiwork.mercador.sinc.SincGoogleDriveUp;
import br.com.mobiwork.mercador.util.Alertas;
import br.com.mobiwork.mercador.util.AsyncResponse;
import br.com.mobiwork.mercador.util.BigDecimalRound;
import br.com.mobiwork.mercador.util.BkpBancoDeDados;
import br.com.mobiwork.mercador.util.ConfigVendedor;
import br.com.mobiwork.mercador.util.ExportDatabaseFileTask;
import br.com.mobiwork.mercador.util.TesteVelocidade;
import br.com.mobiwork.mercador.util.UtilCorrigirComissao;

public class MenuInicial extends  Activity implements OnClickListener,AsyncResponse
{

    private static String LogSync;
    private static String LogToUserTitle;
    private static String Log;
    private static SQLiteDatabase dbM;
    private static int vendid;

    public static Context mainContext;
    public int selecionarsincMenuPricipalID;
    //private TextView txSaldoTotal;
    private SQLiteDatabase db,dbEnvio;
    private Config config;
    private daoPedido dao;
    private  TextView txtVendedor;
    private Boolean b=false;
    Alertas a ;
    private String branch;
    private ImageButton brel,btajustes;

    /** Called when the activity is first created. */
    //testando a nova branch
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        a= new Alertas(this);
        txtVendedor = (TextView) findViewById (R.id.vendedor);
        daoCreateDBM daoDBM = new daoCreateDBM(this);
        dbM =  daoDBM.getWritableDatabase();
        brel=(ImageButton) findViewById(R.id.btRel);
        btajustes=(ImageButton) findViewById(R.id.btajustes);
        this.config = ConfigVendedor.getConfig(this.dbM);
        if (this.config.getVendid() != null && b == false && this.config.getAtu() != null) {
                if (this.config.getAtu().equalsIgnoreCase("1")) {
                    vendid = this.config.getVendid();
                    txtVendedor.setText(this.config.getNomeven());
                    if (Conexao.Conectado(this)) {
                        Intent localIntent1 = new Intent(this, AtuVersao.class);
                        localIntent1.putExtra("OP", "ATU_CARGA_TOTAL");
                        localIntent1.putExtra("tConn", "3g2gWifi");
                        startActivityForResult(localIntent1, 100);
                    } else {
                        a.AlertaSinc("SEM CONEXÃO COM INTERNET");
                    }
                }
        }

        //this.txSaldoTotal = ((TextView)findViewById(2131230770));
        this.db = new daoCreateDB(this).getWritableDatabase();
        int versao=db.getVersion();
        this.dbEnvio=new daoCreateDBEnvio(this).getWritableDatabase();
        this.dao = new daoPedido(this);
        if(config.getEmp()!=null){
            if(config.getEmp().equalsIgnoreCase("MVitoria")){
                double d = BigDecimalRound.Round(this.config.getSaldoVerba() + this.dao.saldoverbaGeral(this.db), 2);
                mainContext = this;
                txtVendedor.setText("R$ "+String.valueOf(d));
            }else if(config.getEmp().equalsIgnoreCase("boav")){
                txtVendedor.setText(this.config.getNomeven());
                btajustes.setVisibility(View.GONE);
                brel.setVisibility(View.GONE);
            }
            if(!config.getEmp().equalsIgnoreCase("geg")&&!config.getEmp().equalsIgnoreCase("lavrasatacado")&&!config.getEmp().equalsIgnoreCase("boav")&&!config.getEmp().equalsIgnoreCase("dten")||config.getEmp()==null){
                brel.setVisibility(View.GONE);

            }
        }
        View button1Click = findViewById(R.id.btAtividadesMenuPricipal);
        button1Click.setOnClickListener(this);
        button1Click = findViewById(R.id.btClientesMenuPricipal);
        button1Click.setOnClickListener(this);
        button1Click = findViewById(R.id.btPedidosMenuPricipal);
        button1Click.setOnClickListener(this);
        button1Click = findViewById(R.id.btSairMenuPricipal);
        button1Click.setOnClickListener(this);
        button1Click = findViewById(R.id.btSincMenuPricipal);
        button1Click.setOnClickListener(this);
        button1Click = findViewById(R.id.btBackup);
        button1Click.setOnClickListener(this);
        button1Click = findViewById(R.id.btExtraMenuPricipal);
        button1Click.setOnClickListener(this);
        button1Click = findViewById(R.id.btRel);
        button1Click.setOnClickListener(this);
        button1Click = findViewById(R.id.btajustes);
        button1Click.setOnClickListener(this);

//        new GeneratePassword().generateSenha(config);
    }

    public void onClick(View v) {
        switch(v.getId()){
            case R.id.btAtividadesMenuPricipal:
                //atividadesMenuPricipal();
                boolean entrou=false;
                if(config.getEmp()!=null){
                    if(config.getEmp().equalsIgnoreCase("vimilk")){
                        oplocaliza();
                        break;
                    }else{

                        Intent ix;
                        ix = new Intent(this,LocalizaClientes.class);
                        ix.putExtra("OP","ATENDER_CLIENTE");
                        this.startActivityForResult(ix,0);
                        entrou=true;
                        break;
                    }
                }
                if(!entrou){
                    Intent i;
                    i = new Intent(this,LocalizaClientes.class);
                    i.putExtra("OP","ATENDER_CLIENTE");
                    this.startActivityForResult(i,0);
                }
                break;
            case R.id.btClientesMenuPricipal:
                clientesMenuPricipal();
                break;
            case R.id.btPedidosMenuPricipal:
                pedidosMenuPricipal();
                break;
            case R.id.btSincMenuPricipal:
                sincMenuPricipal();
                break;
            case R.id.btBackup:
                runBackup();
                break;
            case R.id.btExtraMenuPricipal:
                extraMenuPricipal();
                break;
            case R.id.btRel:
                relMenuPricipal();
                break;
            case R.id.btajustes:
                selAjustes();
                break;

            case R.id.btSairMenuPricipal:
                /*OrdenarHashMap o = new OrdenarHashMap();
                o.ordenar();*/
                ExportDatabaseFileTask edtft=new ExportDatabaseFileTask(this, "br.com.mobiwork.mercador", "MercadoDB");
                edtft.execute(new String[0]);
                ExportDatabaseFileTask edtft2=new ExportDatabaseFileTask(this, "br.com.mobiwork.mercador", "MercadoDBM");
                edtft2.delegate=MenuInicial.this;
                edtft2.execute(new String[0]);
        }
    }




    public void showToast(final String toast) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), toast, Toast.LENGTH_SHORT).show();
            }
        });
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return false;
    }

    private void atividadesMenuPricipal() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.op_menu_principal)
                .setItems(R.array.op_menu_principalAtividades, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialoginterface, int i) {
                        selecionarAtividadeMenuPricipal(i);
                    }
                }).show();
    }

    private void clientesMenuPricipal() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.op_menu_principal)
                .setItems(R.array.op_menu_principalCliente, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialoginterface, int i) {
                        selecionarClientesMenuPricipal(i);
                    }
                }).show();
    }

    private void oplocaliza() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.op_menu_principal)
                .setItems(R.array.op_menu_principalLozalizar, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialoginterface, int i) {
                        selecionarAtividadeMenuPricipal(i);
                    }
                }).show();
    }


    private void pedidosMenuPricipal() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.op_menu_principal)
                .setItems(R.array.op_menu_principalPedidos, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialoginterface, int i) {
                        selecionarPedidosMenuPricipal(i);
                    }
                }).show();
    }

    protected void selecionarAtividadeMenuPricipal(int i) {
        Intent ix;
        switch (i) {
            case 0:
                ix = new Intent(this,LocalizaClientes.class);
                ix.putExtra("OP","ATENDER_CLIENTE");
                this.startActivityForResult(ix,0);
                break;
            case 2:
                ix = new Intent(this,GerenciarVisitas.class);
                ix.putExtra("OP","ATENDER_CLIENTE");
                this.startActivityForResult(ix,0);
                break;
            case 1:
                ix = new Intent(this,LocalizaRotas.class);
                ix.putExtra("OP","ATENDER_CLIENTE");
                this.startActivityForResult(ix,0);
                break;
        }
    }

    protected void selecionarClientesMenuPricipal(int i) {
        Intent ix;
        switch (i) {
            case 0:
                ix = new Intent(this,CadCliente.class);
                ix.putExtra("OP","CADASTRAR_CLIENTE");
                this.startActivityForResult(ix,102);
                break;
            case 1:
                ix = new Intent(this, LocalizaClientes.class);
                ix.putExtra("OP","CONSULTAR_CLIENTE");
                this.startActivityForResult(ix,0);
                break;
            case 2:
                ix = new Intent(this, LocalizaClientes.class);
                ix.putExtra("OP","CONSULTAR_CLIENTE_PEDIDOS");
                this.startActivityForResult(ix,0);
                break;
            case 3:
                ix = new Intent(this, AltClienteHistorico.class);
                this.startActivityForResult(ix,0);
                break;
        }
    }

    protected void selecionarPedidosMenuPricipal(int i) {
        Intent ix;
        switch (i) {
            case 0:
                ix = new Intent(this,PedidosNEnviados.class);
                ix.putExtra("vendid", vendid);
                startActivityForResult(ix, 101);
                break;
            case 1:
                ix = new Intent(this,LoteDePedidosEnviados.class);
                ix.putExtra("vendid", vendid);
                startActivityForResult(ix, 101);
                break;
        }
    }



    private void sincMenuPricipal()
    {
        new AlertDialog.Builder(this).setTitle(R.string.op_menu_principal).setItems(R.array.op_menu_principalSinc, new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
            {
                MenuInicial.this.selecionarsincMenuPricipalID = paramAnonymousInt;
                if (paramAnonymousInt <2)
                {
                    final int[] arrayOfInt = { 0 };
                    String[] arrayOfString = { "3G/2G/Wi-Fi(Internet)","Via rede", "Off-Line" };
                    AlertDialog.Builder localBuilder = new AlertDialog.Builder(MenuInicial.this);
                    localBuilder.setTitle("Sincronizar").setSingleChoiceItems(arrayOfString, 0, new DialogInterface.OnClickListener()
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
                            MenuInicial.this.selecionarsincMenuPricipal(MenuInicial.this.selecionarsincMenuPricipalID, arrayOfInt[0]);
                        }
                    });
                    localBuilder.create().show();
                    return;
                }
                MenuInicial.this.selecionarsincMenuPricipal(paramAnonymousInt, 0);
            }
        }).show();
    }

    private void extraMenuPricipal()
    {
        new AlertDialog.Builder(this).setTitle(R.string.op_menu_principal).setItems(R.array.op_menu_principalExtra, new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
            {
                MenuInicial.this.selecionarsincMenuPricipalID = paramAnonymousInt;

                MenuInicial.this.selecionarExtra(paramAnonymousInt, 0);
            }
        }).show();
    }
    private void selAjustes()
    {
        int menu=0;
        if(config.getEmp().equalsIgnoreCase("boav")) {
             menu = R.array.op_menu_precoRel;
        }
        if(config.getEmp()!=null) {
            new AlertDialog.Builder(this).setTitle(R.string.op_menu_principal).setItems(menu, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt) {
                    MenuInicial.this.selecionarsincMenuPricipalID = paramAnonymousInt;
                    MenuInicial.this.selecionarAjustes(paramAnonymousInt, 0);
                }
            }).show();
        }
    }

    private void relMenuPricipal()
    {
        int menu= R.array.op_menu_rel;
        if(config.getEmp()!=null) {
            if (config.getEmp().equalsIgnoreCase("geg")) {
                menu = R.array.op_menu_rel;
            }else{
                menu = R.array.op_menu_preco;
            }
            new AlertDialog.Builder(this).setTitle(R.string.op_menu_principal).setItems(menu, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt) {
                    MenuInicial.this.selecionarsincMenuPricipalID = paramAnonymousInt;

                    MenuInicial.this.selecionarRel(paramAnonymousInt, 0);
                }
            }).show();
        }
    }


    protected void runBackup() {

        final int[] arrayOfInt = { 0 };
        String[] arrayOfString = { "Restaurar Backup","Gerenciador de dados" };
        new AlertDialog.Builder(this).setTitle("Backup").setSingleChoiceItems(arrayOfString, 0, new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface paramDialogInterface, int paramInt)
            {
                arrayOfInt[0] = paramInt;
            }
        }).setPositiveButton("OK", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface paramDialogInterface, int paramInt)
            {
                escBackup(arrayOfInt[0]);
            }
        }).show();

    }
    public void escBackup(int param){
        Intent localIntent = new Intent(this, BkpBancoDeDados.class);


        if (param== 0){
            // localIntent.putExtra("tBkp", "Restaure");
            //  this.startActivity(localIntent);
            Intent intent = new Intent(this, ListBackupBaixados.class);
            this.startActivity(intent);
        }else if(param==1){
            localIntent= new Intent(this,GerenciadorDeDados.class);
            this.startActivityForResult(localIntent,99);
        }
        return;

    }

    protected void selecionarExtra(int paramInt1, int paramInt2)
    {
        switch (paramInt1)
        {
            default:
                return;


            case 0:
                startActivityForResult(new Intent(this, TesteVelocidade.class), 0);
                return;
            case 1:
                Intent localIntent= new Intent(this,GerenciaRestaure.class);
                localIntent.putExtra("vendid", vendid);
                localIntent.putExtra("tConn", "3g2gWifi");
                localIntent.putExtra("mod","Atualizacoes");
                this.startActivityForResult(localIntent,99);
                return;
            /*case 2:
                startActivity(new Intent(this, GerenciadorDeDados.class));
                return;*/

            case 2:
                startActivityForResult(new Intent(this, Informacoes.class), 0);
                return;
        }
    }
    protected void selecionarRel(int paramInt1, int paramInt2)
    {
        switch (paramInt1)
        {
            default:
                return;
            case 0:
                if(config.getEmp().equalsIgnoreCase("geg")){
                    startActivityForResult(new Intent(this, RelPeso.class), 0);
                }else if(config.getEmp().equalsIgnoreCase("lavrasatacado")){
                    startActivityForResult(new Intent(this, RelVlrVenda.class), 0);
                }else if(config.getEmp().equalsIgnoreCase("boav")||config.getEmp().equalsIgnoreCase("dten")){
                    startActivityForResult(new Intent(this, RelComis.class), 0);
                }
                return;

        }
    }

    protected void selecionarAjustes(int paramInt1, int paramInt2)
    {
        switch (paramInt1) {
            case 0:
                UtilCorrigirComissao u = new UtilCorrigirComissao(MenuInicial.this);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.CUPCAKE) {
                    u.execute(new String[0]);
                    return;
                }
        }
    }


    protected void selecionarsincMenuPricipal(int paramInt1, int paramInt2)
    {
        switch (paramInt1)
        {
            default:
                return;

            case 0:
                boolean ret=true;
                Intent localIntent2 = new Intent(this, SincGoogleDriveUp.class);
                localIntent2.putExtra("loteId", "");
                localIntent2.putExtra("mod","pedido");
                localIntent2.putExtra("pedidosEnviados", false);
                if (paramInt2 == 2){
                    localIntent2.putExtra("vendid", vendid);
                    localIntent2.putExtra("tConn", "offLine");
                }
                while (ret)
                { ret=false;
                    if(paramInt2==0){

                        localIntent2.putExtra("vendid", vendid);
                        localIntent2.putExtra("tConn", "3g2gWifi");

                    }
                    if(paramInt2==1){

                        localIntent2.putExtra("vendid", vendid);
                        localIntent2.putExtra("tConn", "rede");

                    }
                    startActivityForResult(localIntent2, 101);
                    return;
                }

            case 1:
                Intent localIntent1 = new Intent(this,GoogleDriveAtu.class);
                localIntent1.putExtra("OP", "ATU_CARGA_TOTAL");
                if (paramInt2 == 2) {
                    localIntent1.putExtra("tConn", "offLine");
                    startActivityForResult(localIntent1, 100);
                    return;
                }
                else if(paramInt2 == 1) {
                    localIntent1.putExtra("tConn", "rede");
                    startActivityForResult(localIntent1, 100);
                    return;
                }else  {
                    localIntent1.putExtra("tConn", "3g2gWifi");
                    startActivityForResult(localIntent1, 100);
                    return;
                }

            case 2:
                startActivityForResult(new Intent(this, ReceberInfoConfig.class), 0);
                return;
        }
    }


    public void drive() {
        Intent ix;
        ix = new Intent(this, GoogleDriveAtu.class);
        ix.putExtra("OP", "ATENDER_CLIENTE");
        this.startActivityForResult(ix, 0);


    }


    public static boolean verificaConexao(Context contexto){
        ConnectivityManager cm = (ConnectivityManager) contexto.getSystemService(Context.CONNECTIVITY_SERVICE);//Pego a conectividade do contexto o qual o metodo foi chamado

        NetworkInfo netInfo = cm.getActiveNetworkInfo();//Crio o objeto netInfo que recebe as  da NEtwork

        System.out.println("NETWORK INFO: "+netInfo.getSubtypeName());

        if ( (netInfo != null) && (netInfo.isConnectedOrConnecting()) && (netInfo.isAvailable()) ) //Se o objeto for nulo ou nao tem conectividade retorna false
            return true;
        else
            return false;
    }

    public static boolean Conectado(Context context) {
        try {
            ConnectivityManager cm = (ConnectivityManager)
                    context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnected()) {
                LogSync += "\nConectado a Internet 3G ";
                LogToUserTitle += "Conectado a Internet 3G ";
                Log = "Status de conexão 3G: "+cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnected();
                return true;
            } else if(cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected()){
                LogSync += "\nConectado a Internet WIFI ";
                LogToUserTitle += "Conectado a Internet WIFI ";
                Log = "Status de conexão Wifi: "+cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected();
                return true;
            } else {
                LogSync += "\nNão possui conexão com a internet ";
                LogToUserTitle += "Não possui conexão com a internet ";
                Log = "Status de conexão Wifi: "+cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected();
                Log = "Status de conexão 3G: "+cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnected();
                return false;
            }
        } catch (Exception e) {
            Log = e.getMessage();
            return false;
        }
    }

    @Override
    public void onBackPressed() {
        return;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String mensagem="";
        String titulo="";
        if((requestCode == 100 ||requestCode==101||requestCode==102) && resultCode==RESULT_OK) {
            if(requestCode==101){
                titulo=data.getExtras().getString("result");
                mensagem=data.getExtras().getString("result2");
            }else if(requestCode==100) {
                mensagem="Dados Sincronizados com Sucesso";
            }else{
                mensagem="Cliente Cadastrado com Sucesso";
            }
            a.AlertaSinc(titulo, mensagem);

        }

        if((requestCode == 100 ||requestCode==101) && resultCode==RESULT_CANCELED) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MenuInicial.this);

            if(requestCode==101){
                titulo=data.getExtras().getString("result");
                mensagem=data.getExtras().getString("result2");
            }else if(requestCode==100) {
                if(data!=null){
                    titulo= data.getExtras().getString("result");
                    mensagem= data.getExtras().getString("result2");
                }
            }else{
                mensagem="Erro ao Efetuar Cadastro";
            }
            if(!mensagem.equalsIgnoreCase("")){
                if(!mensagem.equalsIgnoreCase("Nenhuma Alteração")){
                    a.AlertaSinc(titulo, mensagem);
                }
            }
        }

        if(requestCode == 100 && resultCode==RESULT_FIRST_USER) {
            a.AlertaSinc("SEM NOVAS ATUALIZAÇÕES!");
        }

        this.config=ConfigVendedor.getConfig(this.db);
        if(this.config.getEmp()!=null){
            if(this.config.getEmp().equalsIgnoreCase("MVitoria")||this.config.getEmp().equalsIgnoreCase("salatiel123")||this.config.getEmp().equalsIgnoreCase("grm")){
                Config localConfig = new daoConfig(this).consultar(this.dbM);
                ConfigVendedor.setConfig(this.config);
                double d = BigDecimalRound.Round(localConfig.getSaldoVerba() + this.dao.saldoverbaGeral(this.db), 2);
                txtVendedor.setText("R$ "+String.valueOf(d));
            }else{
                txtVendedor.setText(this.config.getNomeven());
            }
        }
    }

    @Override
    public void processFinish(String output) {
        if (output.equals("EXIT")){
            this.finish();
        }
    }
}



