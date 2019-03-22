package br.com.mobiwork.mercador.pedido;

import android.app.AlertDialog;
import android.app.TabActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TextView;

import com.exina.android.calendar.CalendarActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import br.com.mobiwork.mercador.R;
import br.com.mobiwork.mercador.creceber.InfoFinanceiras;
import br.com.mobiwork.mercador.dao.CReceberPedidoDao;
import br.com.mobiwork.mercador.dao.PedidoRollBack;
import br.com.mobiwork.mercador.dao.daoCliente;
import br.com.mobiwork.mercador.dao.daoCondicaoDePgto;
import br.com.mobiwork.mercador.dao.daoConfig;
import br.com.mobiwork.mercador.dao.daoCreateDB;
import br.com.mobiwork.mercador.dao.daoCreateDBM;
import br.com.mobiwork.mercador.dao.daoCreceb;
import br.com.mobiwork.mercador.dao.daoFormaPgto;
import br.com.mobiwork.mercador.dao.daoItemPedido;
import br.com.mobiwork.mercador.dao.daoPedido;
import br.com.mobiwork.mercador.dao.daoindice_dev;
import br.com.mobiwork.mercador.indice.InfoIndices;
import br.com.mobiwork.mercador.model.CReceberPedido;
import br.com.mobiwork.mercador.model.Cliente;
import br.com.mobiwork.mercador.model.Config;
import br.com.mobiwork.mercador.model.DataHoraPedido;
import br.com.mobiwork.mercador.model.Indice_dev;
import br.com.mobiwork.mercador.model.ItemPedido;
import br.com.mobiwork.mercador.model.Pedido;
import br.com.mobiwork.mercador.util.Alertas;
import br.com.mobiwork.mercador.util.BigDecimalRound;
import br.com.mobiwork.mercador.util.ConfigVendedor;


public class PedidoVenda extends TabActivity implements  View.OnClickListener  { //, View.OnKeyListener

    public static final int SWIPE_MIN_DISTANCE = 60;
    public static final int SWIPE_MAX_OFF_PATH = 250;
    public static final int SWIPE_THRESHOLD_VELOCITY = 200;
    private GestureDetector gestureDetector;
    View.OnTouchListener gestureListener;

    TabHost tabHost;
    private Button btTabCondVenda,btTabItens,btTabObsFin;
    private double limiteini;

    private Cursor cursor,cursorItens, cursorCondDePgto,cursorop,cursorformapgto;
    private SimpleCursorAdapter adapter;
    private SQLiteDatabase  dbM, db;
    private Pedido pedido;
    private boolean  isEditPedido, isEditItem, isEditPedidoItem, incluirPedido, isEditCReceberPedido, isEditCRP, isSelection, isConfirmaPedido;
    private Spinner condDePgto, formaDePgto, operacao ;
    private int condDePgtoId, formaDePgtoId, operacaoId, tbPrecoId, nrvenc, condDePgtoSelection;
    private DataHoraPedido dataHoraMS;
    private Cliente cliente;
    private String pedidoId, clienteId;
    private Config config;

    private ItemPedido itensPedido;
    private ListView produtosList, CReceberList;
    private double valorDescAcres,percDescAcres,vrSubTotal,vrSubTotal_st, vrTotal,vrTotalisped,graurisco, vrCReceberPedido, vrreceberaberto;
    private Integer  idTipoDescAcresPed;
    private EditText rec;
    private long mAnoVencto,mMesVencto,mDiaVencto;
    private TextView mBtLimiteCredito,mObs, limitecreditoTx,limitetottx,txrec, txVrTotal, txVrSubTotal, txvalorDescAcres,txtdesc,txt_inddev;
    private String codcli;
    private double vlrreceb,limite,receb;
    Alertas a ;
    daoCliente dao;
    String data;
    private String codigo,codproduto,recebimento;
    private Double limcredped;
    private int tolerancia,ndup;
    private String idVisita;
    private daoCreceb dcre;
    private Button alterarLimite,tit_indidev;
    private int param;
    private double recebido;
    private daoPedido d ;
    double vrtot;
    private boolean statot;
    boolean ver ;
    int cont;
    private daoCondicaoDePgto dcpgto;
    private daoFormaPgto dformp;
    private TextView txtlimitec,txt_limiteminimo;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pedidovenda);
        cont=0;
        codcli= getIntent().getStringExtra("codCliente");
        clienteId = getIntent().getStringExtra("CLIENTE_ID");
        pedidoId  = getIntent().getStringExtra("PEDIDO_ID");
        clienteId=clienteId.trim();
        vlrreceb= getIntent().getDoubleExtra("vlrreceb", 0.0);
        tolerancia=getIntent().getIntExtra("tolerancia", 0);
        idVisita=getIntent().getStringExtra("idVisita");
        limite= getIntent().getDoubleExtra("limite", 0.0);
        ndup= getIntent().getIntExtra("ndup", 0);
        receb= getIntent().getDoubleExtra("receb", 0.0);
        param= getIntent().getIntExtra("sit", 0);
        recebimento="";
        d= new daoPedido(this);
        statot=false;
        dcpgto = new daoCondicaoDePgto(this);
        dformp= new daoFormaPgto(this);

       /* if(!(Thread.getDefaultUncaughtExceptionHandler() instanceof CustomExceptionHandler)) {
            Thread.setDefaultUncaughtExceptionHandler(new CustomExceptionHandler(this
                    ));
        }*/
        itensPedido = new ItemPedido();
        itensPedido.setIdPedido(pedidoId);
        vrTotalisped=0;
        SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
        Date dataAtual = new Date(System.currentTimeMillis());
        data = sd.format(dataAtual);
        txrec=(TextView) findViewById(R.id.txrec);
        CReceberList = (ListView) findViewById (R.id.listCReceber);
        mBtLimiteCredito = (TextView) findViewById(R.id.btlimitecredito);
        txtdesc = (TextView) findViewById(R.id.txtdesc);
        mBtLimiteCredito.setOnClickListener(this);
        rec=(EditText)findViewById(R.id.recebido);
        alterarLimite=(Button)findViewById(R.id.alterar);
        limitecreditoTx = (TextView) findViewById(R.id.limitecredito);
        txtlimitec=(TextView) findViewById(R.id.txtlimitec);
        txt_limiteminimo=(TextView) findViewById(R.id.txt_limiteminimo);
        txVrTotal = (TextView) findViewById (R.id.vrTotalPedido);
        txVrSubTotal = (TextView) findViewById (R.id.subTotalPedido);
        txvalorDescAcres  = (TextView) findViewById (R.id.desconto);
        txvalorDescAcres  = (TextView) findViewById (R.id.desconto);
        txt_inddev   = (TextView) findViewById (R.id.txt_inddev);
        tit_indidev    = (Button) findViewById (R.id.tit_indidev);
        produtosList = (ListView) findViewById (R.id.list);
        formaDePgto = (Spinner) findViewById(R.id.formapag);
        limiteini=0;




        this.config = ConfigVendedor.getConfig(dbM);
        if(config.getEmp().equalsIgnoreCase("lavrasatacado")){
            codigo= getIntent().getStringExtra("codigo");
        }
        if(!config.getEmp().equalsIgnoreCase("jrc")){
            rec.setVisibility(View.GONE);
            alterarLimite.setVisibility(View.GONE);
        }

        if(config.getEmp().equalsIgnoreCase("jrc")){
           if(param==0){
               rec.setText(String.valueOf(receb));
               recebido=vlrreceb;
           }else{
               rec.setText(String.valueOf(receb));
               recebido=receb;
           }
        }

        mObs  = (TextView) findViewById(R.id.obs);
        a= new Alertas(this);
        daoCreateDBM daoDBM = new daoCreateDBM(this);
        dbM =  daoDBM.getWritableDatabase();

        daoCreateDB daoDB = new daoCreateDB(this);
        db =  daoDB.getWritableDatabase();
        pedido = new Pedido();

        if(this.config.getEmp()==null){
            this.config = new daoConfig(this).consultar(this.dbM);
        }
        dcre= new daoCreceb(this);

        nrvenc = 0;
        isEditPedido = false;
        isConfirmaPedido = true;
        incluirPedido = true;
        isSelection = false;
        vrCReceberPedido = 0;

        dao = new daoCliente(this);
        //AQUI ELE PEGA TODOS OS DADOS DO CLIENTE
        cliente = dao.consultarPorId(dbM,clienteId);
        if(cliente.getCodigo()==null){
            cliente=dao.consultarPorIdPrecadastro(db,clienteId);
            if(cliente.getCodigo()!=null){
                dao.transfpre(dbM,cliente);
                cliente = dao.consultarPorId(dbM,clienteId);
            }
        }
        if(config.getEmp().equalsIgnoreCase("vimilk")){
            txtlimitec.setText("Valor minimo p/ Venda");
            limitecreditoTx.setText(String.valueOf(cliente.getClivlrminvenda()));
        }
        if(config.getEmp().equalsIgnoreCase("boav")){
            txt_limiteminimo.setVisibility(View.VISIBLE);
            txt_limiteminimo.setText("Valor minimo p/ Venda: R$ "+String.valueOf(cliente.getClivlrminvenda()));
        }



        if(cliente.get_id()!=null) {
            condDePgtoId = cliente.getCondDePgto();
            formaDePgtoId = cliente.getFormaDePgto();
            tbPrecoId = cliente.getTbPrecoCliente();
            graurisco = cliente.getGraurisco();
        }
        operacaoId = -1;

        if (!pedidoId.equals("")){
            Cursor c = db.rawQuery("SELECT * FROM pedidos tb " +
                    " WHERE tb._id = ?" ,  new String[]{""+pedidoId});
            if (c.moveToFirst()) {
                isEditPedido = true;
                incluirPedido = false;
                isConfirmaPedido = false;
                this.condDePgtoId = c.getInt(c.getColumnIndex("condDePgto"));
                formaDePgtoId = c.getInt(c.getColumnIndex("formaDePgto"));
                operacaoId = c.getInt(c.getColumnIndex("operacao"));
                tbPrecoId = c.getInt(c.getColumnIndex("tbPrecoCliente"));
                vrTotalisped=c.getDouble(c.getColumnIndex("vrTotal"));
                vrtot=vrTotalisped;
                codcli=cliente.getCodCliente();
                pedido.setPedido(c);
                dataHoraMS = pedido.getDataHoraMS();
                mAnoVencto = c.getInt(c.getColumnIndex("anoVencto"));
                mMesVencto = c.getInt(c.getColumnIndex("mesVencto"));
                mDiaVencto = c.getInt(c.getColumnIndex("diaVencto"));
                mObs.setText(c.getString(c.getColumnIndex("obs")));
                Log.e("msg01", String.valueOf(c.getInt(c.getColumnIndex("idTipoDescAcresPed"))));
                //jrclimite
                if(config.getEmp().equalsIgnoreCase("jrc")){
                    recebimento=c.getString(c.getColumnIndex("recebimento"));
                    rec.setText(c.getString(c.getColumnIndex("recebimento")));
                    limitecreditoTx.setText(String.valueOf(cliente.getLimitetemp()));
                    limcredped=Double.parseDouble(dao.consultarLimite(dbM, cliente.getCodCliente()));
                }

                valorDescAcres = c.getDouble(c.getColumnIndex("valorDescAcres"));
                idTipoDescAcresPed = c.getInt(c.getColumnIndex("idTipoDescAcresPed"));
                percDescAcres=c.getDouble(c.getColumnIndex("percDescAcres"));
                PedidoRollBack.criarPedidoRollBack(db, c, pedidoId);
                cursorItens = db.rawQuery("SELECT *  FROM itensPedido tb " +
                        " WHERE tb.idPedido = ? " ,  new String[]{ ""+pedidoId});
                PedidoRollBack.setItemPedido(cursorItens);
            }
        }


        //    cursor = dbM.rawQuery("SELECT  sum(vlrreceb) as vlrreceb FROM creceb tb " +
        //  " WHERE tb.cnpj = ? " ,  new String[]{""+cliente.getCodCliente()});
        cursor = dbM.rawQuery("select valor from creceb where cliente='" + codcli + "'", null);


        isEditCReceberPedido = false;
        condDePgto = (Spinner) findViewById(R.id.condpag);
        operacao = (Spinner) findViewById(R.id.operacao);


        operacao.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent,
                                       View view, int position, long id) {
              //  if (isSelection){
                    operacaoId = Integer.parseInt(String.valueOf(operacao.getSelectedItemId()));
                    String op=cursorop.getString(cursorop.getColumnIndex("descricao")).trim();
                    if(op.equalsIgnoreCase("BONIFICACAO")||(config.getEmp().equalsIgnoreCase("vimilk")&&(operacaoId==3||operacaoId==4||operacaoId==6))) {
                        alterar_cond_forma("LIVRE DE DEBITO","LIVRE DE DEBITO");
                    }
                    if((config.getEmp().equalsIgnoreCase("vimilk")&&(operacaoId==2)) && condDePgtoId==999){
                        alterar_cond_forma("A VISTA","BRADESCO");
                    }
                }
            //}
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });

        condDePgto.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent,
                                       View view, int position, long id) {
               if (condDePgtoId!=Integer.parseInt(cursorCondDePgto.getString(cursorCondDePgto.getColumnIndex("_id"))))
                {
                        condDePgtoId = Integer.parseInt(cursorCondDePgto.getString(cursorCondDePgto.getColumnIndex("_id")));
                        if (config.getEmp().equalsIgnoreCase("lavrasatacado")) {
                            acresaprazo();
                        }
                        if (config.getEmp().equalsIgnoreCase("Mvitoria") || config.getEmp().equalsIgnoreCase("boav") && pedidoId != null) {
                            if (condDePgtoId != 0) {
                                daoItemPedido dip = new daoItemPedido(PedidoVenda.this);
                                ArrayList<ItemPedido> it;
                                if (config.getEmp().equals("boav")) {
                                    it = dip.pesqPrecoOriginal(pedidoId);
                                } else {
                                    it = dip.pesqPrecoAbaixo(pedidoId);
                                }
                                if (it.size() > 0) {
                                    verica_altera_preco_vista(it, pedidoId);
                                }
                            }
                        }
                        if (config.getEmp().equalsIgnoreCase("vimilk")) {
                            switch (condDePgtoId) {
                                case 999:
                                    alterar_operacao_forma("TROCA", "LIVRE DE DEBITO");
                                    break;
                                case 1:
                                    alterar_operacao_forma("***FATURA", "DINHEIRO");
                                    break;
                                default:
                                    alterar_operacao_forma("***FATURA", "BRADESCO");
                                    break;
                            }
                        }
                    isSelection = true;
                }
            }

            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });

        formaDePgto.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent,
                                       View view, int position, long id) {
            //    if (isSelection) {
                    formaDePgtoId = Integer.parseInt(String.valueOf(formaDePgto.getSelectedItemId()));
                    String forma = cursorformapgto.getString(cursorformapgto.getColumnIndex("descricao")).trim();
                    if (config.getEmp().equalsIgnoreCase("vimilk")) {
                        switch (formaDePgtoId){
                            case 8:
                                alterar_operacao_cond("TROCA", "LIVRE DE DEBITO");
                                break;
                            case 1:
                                alterar_operacao_cond("***FATURA", "A VISTA");
                                break;
                            default:
                                alterar_operacao_cond("***FATURA","7 DIAS");
                                break;
                        }

                    }

                }
           // }

            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });




        dataHoraMS = DataHoraPedido.criarDataHoraPedido();

        if (cursor.moveToFirst()) {

            TextView  btlimitecreditoTx = (TextView) findViewById(R.id.btlimitecredito);
            vrreceberaberto = dcre.consultarsomaCreceb(dbM,cliente);
            if (vrreceberaberto > 0){
                vrreceberaberto = BigDecimalRound.Round(vrreceberaberto, 2);
                btlimitecreditoTx.setText( String.valueOf(vrreceberaberto));
                txrec.setText(String.valueOf("Recebido de: "+vrreceberaberto));

                if (!isEditPedido){
                    //jrclimite
                    if(!config.getEmp().equalsIgnoreCase("jrc")) {
                        Intent ix = new Intent(PedidoVenda.this, InfoFinanceiras.class);
                        ix.putExtra("CLIENTE_ID", cliente.getCodCliente());
                        PedidoVenda.this.startActivityForResult(ix, 0);
                    }
                }
            }
        }
        updateDisplay();
        Double conv;
        //jrclimite
        if(config.getEmp().equalsIgnoreCase("jrc")){
            if(isEditPedido){
                conv=BigDecimalRound.Round(limcredped);
                limitecreditoTx.setText(String.valueOf(conv));
               // limitetottx.setText(String.valueOf(Double.parseDouble(dao.consultarLimitetotal(dbM,cliente.getCodCliente()))-dcre.consultarsomaCreceb(dbM,cliente)+dao.consultarrecebimentos(db,cliente.getCodigo())));
            }else{
                if(vrreceberaberto==vlrreceb&&vlrreceb!=0){
                    conv=BigDecimalRound.Round(Double.parseDouble(dao.consultarLimitetotal(dbM, codcli)));
                    limitecreditoTx.setText(String.valueOf(conv));
                } else if(vrreceberaberto==vlrreceb){
                    conv=BigDecimalRound.Round(Double.parseDouble(dao.consultarLimite(dbM, codcli)));
                    limitecreditoTx.setText(String.valueOf(conv));
                } else{
                   double conv2=BigDecimalRound.Round(Double.parseDouble(dao.consultarLimitetotal(dbM, codcli)));
                   if(vlrreceb>conv2){
                       conv=BigDecimalRound.Round(conv2);
                       limitecreditoTx.setText(String.valueOf(conv));
                   }else{
                       conv=BigDecimalRound.Round(vlrreceb);
                       limitecreditoTx.setText(String.valueOf(conv));
                   }

                }
            }
        }



        pedido.setIdvendedor(this.config.getVendid());

        TextView  clienteTx = (TextView) findViewById(R.id.cliente);
        if(cliente.getRazao()!=null) {
            clienteTx.setText(cliente.getRazao().trim());
        }
        clienteTx.setEnabled(false);
        pedido.setCliente(cliente.getCodigo());
        condDePgto = (Spinner) findViewById(R.id.condpag);
        popularComboOperacao();
        popularComboFormaDePgto();


        popularCReceberLista() ;


        produtosList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!isEditItem) {
                    editarExcluirItem();
                   // isEditItem = true;
                }
            }
        });
        popularLista();
        if (this.config.getEmp().equalsIgnoreCase("geg")){
            double valor_tot=0;
            if (!txVrTotal.getText().toString().equals("")) {
                valor_tot=Double.parseDouble(txVrTotal.getText().toString());
            }
            popularComboCondDePgto(valor_tot);
        }else{
            popularComboCondDePgto();
        }
        btTabCondVenda = (Button) findViewById(R.id.tabCondVenda);
        btTabItens = (Button) findViewById(R.id.tabItens);
        btTabObsFin = (Button) findViewById(R.id.tabObsFin);
        btTabCondVenda.setOnClickListener(this);
        btTabItens.setOnClickListener(this);
        btTabObsFin.setOnClickListener(this);
        btTabCondVenda.setEnabled(false);

        tabHost = getTabHost();
        tabHost.addTab(tabHost.newTabSpec("tab1").setIndicator(new TextView(this)).setContent(R.id.layout_tab_principal));
        tabHost.addTab(tabHost.newTabSpec("tab2").setIndicator(new TextView(this)).setContent(R.id.layout_tab_endereco));
        tabHost.addTab(tabHost.newTabSpec("tab2").setIndicator(new TextView(this)).setContent(R.id.layout_tab_condvenda));
        tabHost.setCurrentTab(0);
        cursor = dbM.rawQuery("select valor from creceb where cliente='" + codcli + "'", null);
        cont = cursor.getCount();
        //jrclimite
        if(config.getEmp().equalsIgnoreCase("jrc")) {
              ver = dao.verifsit(dbM, cliente.getTolerancia(), data, cont, cliente.getNdup(), dcre.consultarsomaCreceb(dbM, cliente), Double.parseDouble(dao.consultarLimitetotal(dbM, cliente.getCodCliente())), cliente.getCodCliente());
              alterarLimite(0, 0);
              limiteini = Double.parseDouble(dao.consultarLimite(dbM,codcli));
        }
        swipe();
        if(config.getEmp().equalsIgnoreCase("boav")){
            limitecreditoTx.setText(String.valueOf(limite));
            txtdesc.setText("");
            if(isEditPedido) {
                calccomissao();
            }
        }
        if(config.getEmp().equalsIgnoreCase("vimilk")){
            txtdesc.setText("Valor do ST");

        }
        if (config.getEmp().equalsIgnoreCase("vimilk")){
            calculo_indice_dev();
        }else{
            txt_inddev.setVisibility(View.GONE);
            tit_indidev.setVisibility(View.GONE);
        }
    }

    public void calculo_indice_dev(){
        daoindice_dev d = new daoindice_dev(this);
        txt_inddev.setText(String.valueOf(d.consultarIndice_Dev_porcliente(codcli)));
    }

    public void ir_indice(View v){
        Intent ix = new Intent(PedidoVenda.this,InfoIndices.class);
        ix.putExtra("CLIENTE_ID", codcli);
        PedidoVenda.this.startActivityForResult(ix, 0);
    }

    public void alterar_operacao_forma(String operacao_param,String forma_param){
        alterar_operacao(operacao_param);
        alterar_forma(forma_param);
        isSelection = true;
    }
    public void alterar_operacao_cond(String operacao_param,String condicao_param){
        alterar_operacao(operacao_param);
        alterar_condicao(condicao_param);
        isSelection = true;
    }


    public void alterar_cond_forma(String condicao_param,String forma_param){
        alterar_condicao(condicao_param);
        alterar_forma(forma_param);
        isSelection = true;
    }

    public void alterar_operacao(String operacao_param){
        int p=0;
        boolean altera_operacao=true;
        if (operacao_param.equals("TROCA") && operacaoId!=2){
             altera_operacao=false;
        }

        if (altera_operacao) {
            if (cursorop.moveToFirst()) {
                do {
                    String cdp = cursorop.getString(cursorop.getColumnIndex("descricao")).trim();
                    if (cdp.equalsIgnoreCase(operacao_param)) {
                        operacao.setSelection(p);
                        operacaoId = Integer.parseInt(cursorop.getString(cursorop.getColumnIndex("_id")));
                        break;
                    }
                    p++;
                } while (cursorop.moveToNext());
            }
        }
    }

    public void alterar_condicao(String condicao_param){
        boolean altera_condicao=true;

        if(condicao_param.equals("A VISTA") || condicao_param.equals("7 DIAS")  && condDePgtoId!=999){
            altera_condicao=false;
        }
        if(condicao_param.equals("7 DIAS")  && condDePgtoId==1 || condicao_param.equals("A VISTA")  && condDePgtoId!=1){
            altera_condicao=true;
        }

        if(altera_condicao) {
            int p = 0;
            if (cursorCondDePgto.moveToFirst()) {
                do {
                    String cdp = cursorCondDePgto.getString(cursorCondDePgto.getColumnIndex("descricao")).trim();
                    if (cdp.equalsIgnoreCase(condicao_param)) {
                        condDePgto.setSelection(p);
                        condDePgtoId = Integer.parseInt(cursorCondDePgto.getString(cursorCondDePgto.getColumnIndex("_id")));
                        break;
                    }
                    p++;
                } while (cursorCondDePgto.moveToNext());
            }
        }
    }

    public void alterar_forma(String forma_param){
        boolean altera_forma=false;

        if (forma_param.equals("LIVRE DE DEBITO")&& formaDePgtoId!=8) {
            altera_forma=true;
        }
        if(forma_param.equals("BRADESCO") || forma_param.equals("DINHEIRO") && formaDePgtoId==8){
            altera_forma=true;
        }
        if(forma_param.equals("BRADESCO") && formaDePgtoId==1 ||forma_param.equals("DINHEIRO") && formaDePgtoId!=1){
            altera_forma=true;
        }
        if(altera_forma) {
            int p = 0;
            if (cursorformapgto.moveToFirst()) {
                do {
                    String cdp = cursorformapgto.getString(cursorformapgto.getColumnIndex("descricao")).trim();
                    if (cdp.equalsIgnoreCase(forma_param)) {
                        formaDePgto.setSelection(p);
                        formaDePgtoId = Integer.parseInt(cursorformapgto.getString(cursorformapgto.getColumnIndex("_id")));
                        break;
                    }
                    p++;
                } while (cursorformapgto.moveToNext());
            }
        }
    }





    public void swipe(){
        gestureDetector = new GestureDetector(new MyGestureDetector());
        gestureListener = new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        };
        tabHost.setOnClickListener(PedidoVenda.this);
        tabHost.setOnTouchListener(gestureListener);
        produtosList.setOnTouchListener(gestureListener);
        CReceberList.setOnTouchListener(gestureListener);


    }
    public void passarvlr1(View v) {
        rec.setText(String.valueOf(vrreceberaberto));
    }


    public boolean verreceb(){
        String id  =dao.consultapedrrec(db, cliente.getCodigo());
        double r=0;
        if(!rec.getText().toString().equalsIgnoreCase("")){
            r=Double.parseDouble(rec.getText().toString());
        }
        if(id.equalsIgnoreCase("")){
            return false;
        }
        else if(r>=0) {
            if (!id.equalsIgnoreCase(pedidoId) ) {
                return true;
            } else {
              return false;
            }
        }else{
          return false;
        }
    }

    public void alterarlim(View v){

        if(!verreceb()&&cont>0){
            alterarLimite(1, 1);
        }else{
            if(!verreceb()){
                a.Alerta("Ja existe recebimento em outro pedido!");
            }else{
                a.Alerta("Cllente sem duplicata em aberto");
            }

        }
    }



    public void alterarLimite(int alerta,int alerta2){

        boolean atulimite=false;
        daoPedido d = new daoPedido(this);
        if(dao.recebOutroPed(db,cliente.getCodigo(),pedidoId)) {
          rec.setText("0.00");
          if (alerta2 == 1) {
                a.AlertaSinc("Já existe recebimento para este cliente em outro pedido !");
          }
        }

        if(cliente.getNdup()>0||cliente.getLimiteDeCredito()>0||cliente.getTolerancia()>0) {
            if (ver) {
                if (!rec.getText().toString().equalsIgnoreCase("")) {
                    SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd ");
                    Date dataAtual = new Date(System.currentTimeMillis());
                    String data = sd.format(dataAtual);
                    double valoremaberto = dcre.consultarsomaCreceb(dbM, cliente);
                    if (!recebimento.equalsIgnoreCase("")) {
                        receb = Double.parseDouble(String.valueOf(recebimento));
                    }

                    if(cliente.getLimiteDeCredito()>0){
                        atulimite=true;
                    }
                    if (Double.parseDouble(rec.getText().toString()) > BigDecimalRound.Round(valoremaberto, 2) && ver) {
                        if (alerta2 == 1) {
                            a.AlertaSinc("Recebimento acima do valor pendente !");
                        }

                    } else if (Double.parseDouble(String.valueOf(rec.getText())) > valoremaberto && !ver) {
                        if (alerta2 == 1) {
                            a.AlertaSinc("Recebimento acima do valor pendente !");
                        }
                    } else if(atulimite) {
                        double total2 = 0;
                        if (ver) {
                            double conv = 0;
                            double conv2 = BigDecimalRound.Round(Double.parseDouble(dao.consultarLimitetotal(dbM, codcli)));
                            vlrreceb = total2;
                            if (vlrreceb > conv2) {
                                conv = BigDecimalRound.Round(conv2);
                                limitecreditoTx.setText(String.valueOf(conv));
                                vlrreceb = conv;
                                total2 = conv;
                            } else {
                                if (alerta == 0 && !isEditPedido) {
                                    if (BigDecimalRound.Round(dao.consultarLimiteTemp(dbM, codcli)) > 0) {
                                        conv = BigDecimalRound.Round(dao.consultarLimiteTemp(dbM, codcli));
                                    } else if (Double.parseDouble(rec.getText().toString()) == dcre.consultarsomaCreceb(dbM, cliente) && BigDecimalRound.Round(Double.parseDouble(dao.consultarLimitetotal(dbM, cliente.getCodCliente()))) > 0) {
                                        conv = BigDecimalRound.Round(Double.parseDouble(dao.consultarLimitetotal(dbM, codcli)));
                                        statot = true;
                                    } else {
                                        conv = recebido;
                                    }
                                    limitecreditoTx.setText(String.valueOf(conv));
                                    total2 = conv;
                                } else if (alerta == 1 && !isEditPedido) {
                                    if ((Double.parseDouble(rec.getText().toString()) == dcre.consultarsomaCreceb(dbM, cliente) || Double.parseDouble(rec.getText().toString()) >= BigDecimalRound.Round(Double.parseDouble(dao.consultarLimitetotal(dbM, cliente.getCodCliente())))) && BigDecimalRound.Round(Double.parseDouble(dao.consultarLimitetotal(dbM, cliente.getCodCliente()))) > 0) {
                                        conv = BigDecimalRound.Round(Double.parseDouble(dao.consultarLimitetotal(dbM, cliente.getCodCliente())) - vrTotal);
                                        statot = true;
                                    } else {
                                        double difreceb = 0;
                                        double diftotal = 0;
                                        if (recebimento.equalsIgnoreCase("")) {
                                            recebimento = String.valueOf(receb);
                                        }
                                        if (statot) {

                                            dao.upLimite(dbM, codcli, Double.parseDouble(rec.getText().toString()) - vrTotal);
                                            difreceb = 0;
                                            statot = false;
                                        } else {
                                            difreceb = Double.parseDouble(rec.getText().toString()) - Double.parseDouble(recebimento);
                                        }
                                        if (vrtot != vrTotal) {
                                            diftotal = vrTotal - vrtot;
                                        }

                                        double c = BigDecimalRound.Round(Double.parseDouble(dao.consultarLimite(dbM, cliente.getCodCliente())));
                                        conv = BigDecimalRound.Round(Double.parseDouble(dao.consultarLimite(dbM, cliente.getCodCliente())) + difreceb - diftotal);
                                    }
                                    recebimento = rec.getText().toString();
                                    vrtot = Double.parseDouble(txVrTotal.getText().toString());
                                    limitecreditoTx.setText(String.valueOf(conv));
                                    total2 = conv;
                                } else if (alerta == 0 && isEditPedido) {
                                    if (Double.parseDouble(dao.consultarLimite(dbM, cliente.getCodCliente())) > dcre.consultarsomaCreceb(dbM, cliente)) {
                                        statot = true;
                                    }
                                    conv = BigDecimalRound.Round(Double.parseDouble(dao.consultarLimite(dbM, cliente.getCodCliente())));
                                    limitecreditoTx.setText(String.valueOf(conv));
                                    total2 = conv;
                                } else if (alerta == 1 && isEditPedido) {
                                    double difreceb = 0;

                                    if (Double.parseDouble(rec.getText().toString()) == dcre.consultarsomaCreceb(dbM, cliente)) {
                                        conv = BigDecimalRound.Round(Double.parseDouble(dao.consultarLimitetotal(dbM, codcli)) - vrTotal);
                                        statot = true;
                                    } else {
                                        if (statot) {
                                            dao.upLimite(dbM, codcli, Double.parseDouble(rec.getText().toString()) - vrTotal);
                                            difreceb = 0;
                                            statot = false;
                                        } else {
                                            difreceb = Double.parseDouble(rec.getText().toString()) - receb;
                                        }
                                        double diftotal = 0;
                                        double receb = 0;
                                        if (!recebimento.equalsIgnoreCase("")) {
                                            receb = Double.parseDouble(recebimento);
                                        }
                                        if (vrtot != vrTotal) {
                                            diftotal = vrTotal - vrtot;
                                        }

                                        double c = BigDecimalRound.Round(Double.parseDouble(dao.consultarLimite(dbM, cliente.getCodCliente())));
                                        conv = BigDecimalRound.Round(Double.parseDouble(dao.consultarLimite(dbM, cliente.getCodCliente())) + difreceb - diftotal);


                                    }
                                    limitecreditoTx.setText(String.valueOf(conv));
                                    total2 = conv;
                                    recebimento = rec.getText().toString();
                                    vrtot = Double.parseDouble(txVrTotal.getText().toString());

                                }

                            }

                        }
                        GravarPedido(1);
                        try {

                            if (!ver) {
                                double conv = 0;
                                double conv2 = BigDecimalRound.Round(Double.parseDouble(dao.consultarLimitetotal(dbM, codcli)));
                                vlrreceb = total2;
                                if (vlrreceb > conv2) {
                                    conv = BigDecimalRound.Round(conv2);
                                    limitecreditoTx.setText(String.valueOf(conv));
                                    vlrreceb = conv;
                                    total2 = conv;
                                } else {
                                    if (alerta == 0 && !isEditPedido) {
                                        if (BigDecimalRound.Round(dao.consultarLimiteTemp(dbM, codcli)) > 0) {
                                            conv = BigDecimalRound.Round(dao.consultarLimiteTemp(dbM, codcli));
                                        } else {
                                            conv = BigDecimalRound.Round(Double.parseDouble(dao.consultarLimitetotal(dbM, codcli)) + recebido - dcre.consultarsomaCreceb(dbM, cliente));
                                        }
                                        limitecreditoTx.setText(String.valueOf(conv));
                                        total2 = conv;
                                    } else if (alerta == 1 && !isEditPedido) {
                                        double diftotal = 0;
                                        double receb = 0;
                                        if (!recebimento.equalsIgnoreCase("")) {
                                            receb = Double.parseDouble(recebimento);
                                        }
                                        if (vrtot != vrTotal) {
                                            diftotal = vrTotal - vrtot;
                                        }
                                        if (!d.consultarexisterecebimentos(db, cliente.getCodigo()).equalsIgnoreCase("") && !d.consultarexisterecebimentos(db, cliente.getCodigo()).equalsIgnoreCase(pedidoId)) {
                                            conv = BigDecimalRound.Round(Double.parseDouble(dao.consultarLimite(dbM, cliente.getCodCliente())) - diftotal);
                                        } else if (!d.consultarexisteped(db, cliente.getCodigo()).equalsIgnoreCase("") && !d.consultarexisteped(db, cliente.getCodigo()).equalsIgnoreCase(pedidoId)) {
                                            conv = BigDecimalRound.Round(Double.parseDouble(dao.consultarLimite(dbM, cliente.getCodCliente())) - diftotal + Double.parseDouble(rec.getText().toString()));
                                        } else if (Double.parseDouble(rec.getText().toString()) == dcre.consultarsomaCreceb(dbM, cliente)) {
                                            conv = BigDecimalRound.Round(Double.parseDouble(dao.consultarLimitetotal(dbM, cliente.getCodCliente())) - vrTotal);
                                            statot = true;
                                        } else {
                                            if (statot) {
                                                dao.upLimite(dbM, codcli, Double.parseDouble(rec.getText().toString()));
                                                statot = false;
                                            }

                                            conv = BigDecimalRound.Round(Double.parseDouble(dao.consultarLimitetotal(dbM, cliente.getCodCliente())) + Double.parseDouble(rec.getText().toString()) - dcre.consultarsomaCreceb(dbM, cliente) - vrTotal);
                                        }
                                        recebimento = rec.getText().toString();
                                        vrtot = vrTotal;
                                        limitecreditoTx.setText(String.valueOf(conv));
                                        total2 = conv;
                                    } else if (alerta == 0 && isEditPedido) {
                                        conv = BigDecimalRound.Round(Double.parseDouble(dao.consultarLimite(dbM, cliente.getCodCliente())));
                                        limitecreditoTx.setText(String.valueOf(conv));
                                        total2 = conv;
                                    } else if (alerta == 1 && isEditPedido) {
                                        double diftotal = 0;
                                        double receb = 0;
                                        if (!recebimento.equalsIgnoreCase("")) {
                                            receb = Double.parseDouble(recebimento);
                                        }
                                        if (vrtot != vrTotal) {
                                            diftotal = vrTotal - vrtot;
                                        }
                                        if (d.consultarexisterecebimentos(db, cliente.getCodigo()).equalsIgnoreCase("") && !d.consultarexisterecebimentos(db, cliente.getCodigo()).equalsIgnoreCase(pedidoId)) {
                                            conv = BigDecimalRound.Round(Double.parseDouble(dao.consultarLimite(dbM, cliente.getCodCliente())) - diftotal);
                                        } else if (d.consultarqtdped(db, cliente.getCodigo()) > 1 && verreceb()) {
                                            conv = BigDecimalRound.Round(Double.parseDouble(dao.consultarLimite(dbM, cliente.getCodCliente())) - diftotal + Double.parseDouble(rec.getText().toString()));
                                        } else if (Double.parseDouble(rec.getText().toString()) == dcre.consultarsomaCreceb(dbM, cliente)) {
                                            conv = BigDecimalRound.Round(Double.parseDouble(dao.consultarLimitetotal(dbM, codcli)) - vrTotal);
                                            statot = true;
                                        } else {
                                            double difreceb = 0;
                                            if (statot) {
                                                dao.upLimite(dbM, codcli, Double.parseDouble(rec.getText().toString()));
                                                difreceb = 0;
                                                statot = false;
                                            } else {
                                                difreceb = Double.parseDouble(rec.getText().toString()) - receb;
                                            }


                                            double c = BigDecimalRound.Round(Double.parseDouble(dao.consultarLimite(dbM, cliente.getCodCliente())));
                                            conv = BigDecimalRound.Round(Double.parseDouble(dao.consultarLimite(dbM, cliente.getCodCliente())) + difreceb - diftotal);


                                        }
                                        limitecreditoTx.setText(String.valueOf(conv));
                                        total2 = conv;
                                        recebimento = rec.getText().toString();
                                        vrtot = vrTotal;

                                    }


                                }

                            }


                            //   }
                            if (total2 < 0) {
                                if (alerta2 == 1) {
                                    a.AlertaSinc("ATENÇÃO,limite negativo por favor corrija");
                                }
                            }
                            dao.upLimite(dbM, codcli, total2);
                            if (alerta2 == 1) {
                                a.AlertaSinc("Alterado com Sucesso!");
                            }
                        } catch (Exception e) {
                            if (alerta2 == 1) {

                                a.AlertaSinc(e.getMessage());
                            }

                        }


                    }

                } else {
                    if (alerta2 == 1) {
                        a.Alerta("É preciso preencher o campo de recebimento");
                    }

                }

            } else {// SE EXISTE REGRA , MAS ESTA COM AS PENDENCIAS EM DIA
                double dif = 0;
                double totvendas = 0;
                double totrecebimento = 0;
                boolean statusreceb = true;
                recebimento = rec.getText().toString();
                if (!recebimento.equalsIgnoreCase("")) {
                    receb = Double.parseDouble(String.valueOf(recebimento));
                }
                Cursor cliped = dao.consultarpedfetuadosOutroPed(db, cliente.getCodigo(), pedidoId);
                if (cliped.moveToFirst()) {
                    totvendas = cliped.getDouble(cliped.getColumnIndex("vrtotal"));
                    totrecebimento = cliped.getDouble(cliped.getColumnIndex("recebimento"));
                }
                if (BigDecimalRound.Round(dao.consultarLimiteTemp(dbM, codcli)) == 0.00 || BigDecimalRound.Round(dao.consultarLimiteTemp(dbM, codcli)) == 0.01) {//PRIMEIRA VENDA
                    double totc = dcre.consultarsomaCreceb(dbM, cliente);
                    double totlimit = Double.valueOf(dao.consultarLimite(dbM, codcli));

                    if (receb > 0) {
                        double as = Double.parseDouble(dao.consultarLimitetotal(dbM, codcli));
                        double b = (dcre.consultarsomaCreceb(dbM, cliente));
                        double c = receb;
                        dif = BigDecimalRound.Round(Double.parseDouble(dao.consultarLimitetotal(dbM, codcli)) - (dcre.consultarsomaCreceb(dbM, cliente) - receb) - vrTotal - totvendas, 2);//NO CASO DE FAZER ALGUM PAGAMENTO
                        dao.upLimite(dbM, codcli, dif);
                        limitecreditoTx.setText(String.valueOf(dif));
                        if (alerta2 == 1) {
                            a.AlertaSinc("Alterado com Sucesso!");
                        }
                    } else if (totc > 0 && totlimit > 0) {
                        dif = BigDecimalRound.Round(totlimit - totc - totvendas, 2);
                        dao.upLimite(dbM, codcli, dif);
                        limitecreditoTx.setText(String.valueOf(dif));
                        if (alerta2 == 1) {
                            a.AlertaSinc("Alterado com Sucesso!");
                        }
                    }
                } else {
                    if (!rec.getText().toString().equalsIgnoreCase("")) {
                        SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd ");
                        Date dataAtual = new Date(System.currentTimeMillis());
                        String data = sd.format(dataAtual);

                        double valoremaberto = dcre.consultarsomaCreceb(dbM, cliente);
                        if (Double.parseDouble(rec.getText().toString()) > valoremaberto && ver) {
                            if (alerta2 == 1) {
                                a.AlertaSinc("Recebimento acima do valor pendente !");
                                statusreceb = false;
                            }

                        } else if (Double.parseDouble(String.valueOf(rec.getText())) > valoremaberto && !ver) {
                            if (alerta2 == 1) {
                                a.AlertaSinc("Recebimento acima do valor pendente !");
                                statusreceb = false;
                            }
                        }
                        //isEditPedido
                        if (statusreceb) {
                            if (Double.parseDouble(rec.getText().toString()) == dcre.consultarsomaCreceb(dbM, cliente)) {
                                dif = BigDecimalRound.Round(Double.parseDouble(dao.consultarLimitetotal(dbM, cliente.getCodCliente())) - vrTotal, 2);//NO CASO DE FAZER PAGAMENTO TOTAL
                                dao.upLimite(dbM, codcli, dif);
                                limitecreditoTx.setText(String.valueOf(dif));
                                if (alerta2 == 1) {
                                    a.AlertaSinc("Alterado com Sucesso!");
                                }
                            } else {
                                if (receb > 0) {
                                    double as = Double.parseDouble(dao.consultarLimitetotal(dbM, codcli));
                                    double b = (dcre.consultarsomaCreceb(dbM, cliente));
                                    double c = receb;
                                    dif = BigDecimalRound.Round(Double.parseDouble(dao.consultarLimitetotal(dbM, codcli)) - (dcre.consultarsomaCreceb(dbM, cliente) - receb) - vrTotal - totvendas, 2);//NO CASO DE FAZER ALGUM PAGAMENTO
                                    dao.upLimite(dbM, codcli, dif);
                                    limitecreditoTx.setText(String.valueOf(dif));
                                    if (alerta2 == 1) {
                                        a.AlertaSinc("Alterado com Sucesso!");
                                    }
                                } else {

                                    dif = BigDecimalRound.Round(Double.parseDouble(dao.consultarLimitetotal(dbM, codcli)) - dcre.consultarsomaCreceb(dbM, cliente) - vrTotal - totvendas + totrecebimento, 2);//NO CASO DE NÃO FAZER PAGAMENTO

                                    dao.upLimite(dbM, codcli, dif);
                                    limitecreditoTx.setText(String.valueOf(dif));
                                    if (alerta2 == 1) {
                                        a.AlertaSinc("Alterado com Sucesso!");
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }else{
            limitecreditoTx.setText(String.valueOf(vlrreceb));
        }
    }


    @Override
    public void onClick(View v) {
        if (v == btTabCondVenda) {
            tabHost.setCurrentTab(0);
            btTabCondVenda.setEnabled(false);
            btTabItens.setEnabled(true);
            btTabObsFin.setEnabled(true);
        } else if (v == btTabItens)  {
            tabHost.setCurrentTab(1);
            btTabCondVenda.setEnabled(true);
            btTabItens.setEnabled(false);
            btTabObsFin.setEnabled(true);
        } else if (v == btTabObsFin)  {
            tabHost.setCurrentTab(2);
            btTabCondVenda.setEnabled(true);
            btTabItens.setEnabled(true);
            btTabObsFin.setEnabled(false);
        }
    }

    public void popularComboOperacao(){
        cursorop = dbM.rawQuery("SELECT _id, descricao FROM operacao ", null);
        adapter = new SimpleCursorAdapter(this,R.layout.list_item,cursorop,new String[] {"descricao"},
                new int[] {R.id.comboName});


        operacao.setAdapter(adapter);

        if (operacaoId != -1){
            int x=0;
            if (cursorop.moveToFirst()) {
                do {
                    if (operacaoId == cursorop.getInt(cursorop.getColumnIndex("_id"))) {
                        operacao.setSelection(x);
                        break;
                    }
                    x=x+1;
                } while (cursorop.moveToNext());
            }
        }

    }

    public void popularComboCondDePgto(){
        String where_temp_c="";
        if(config.getEmp().equalsIgnoreCase("vimilk")){
            where_temp_c=dcpgto.consultarPorPorCliente(cliente.getClicondimerc());
        }
        cursorCondDePgto = dbM.rawQuery("SELECT _id, descricao, nrvenc FROM condicaoDePgto "+where_temp_c , null);

        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,R.layout.list_item,cursorCondDePgto,new String[] {"descricao"},
                new int[] {R.id.comboName});
        condDePgto.setAdapter(adapter);
        int x=0;
        if (cursorCondDePgto.moveToFirst()) {
            if (this.condDePgtoId == 0){

                condDePgtoId = cursorCondDePgto.getInt(cursorCondDePgto.getColumnIndex("_id"));
            }
            do {
                int cod=cursorCondDePgto.getInt(cursorCondDePgto.getColumnIndex("_id"));
                if (this.condDePgtoId == cursorCondDePgto.getInt(cursorCondDePgto.getColumnIndex("_id"))) {
                    condDePgto.setSelection(x);
                    nrvenc = cursorCondDePgto.getInt(cursorCondDePgto.getColumnIndex("nrvenc"));
                    break;
                }
                x=x+1;
            } while (cursorCondDePgto.moveToNext());
        }
        acresaprazo();

    }


    public  void  acresaprazo(){
        if(config.getEmp().equalsIgnoreCase("lavrasatacado")) {
            daoItemPedido dip = new daoItemPedido(this);

            SQLiteDatabase dbM2;
            daoCreateDBM daoDBM2 = new daoCreateDBM(this);
            dbM2 =  daoDBM2.getWritableDatabase();

            SQLiteDatabase db2;
            daoCreateDB daoDB2 = new daoCreateDB(this);
            db2 =  daoDB2.getWritableDatabase();
            String tempcli;
            tempcli=codcli;
            if(tempcli==null){
                tempcli=codigo;
                if(tempcli==null){
                    tempcli=clienteId;
                }
            }
            dip.acrecimoaprazo(db2, dbM2, tempcli, pedidoId, cursorCondDePgto.getString(cursorCondDePgto.getColumnIndex("nrvenc")));
            dbM2.close();
            db2.close();
            daoDB2.close();
            daoDBM2.close();

            popularLista();
        }
    }

    public void popularComboCondDePgto(double valor){
        String where="";
        if(valor<600){
            where ="where qtdparcela<=1";
        }else if(valor<1000){
            where=" where qtdparcela<=2";
        }else if(valor<1500){
            where=" where qtdparcela<=3";
        }else if(valor>1500){
            where="where qtdparcela<=4";
        }
        cursorCondDePgto = dbM.rawQuery("SELECT _id, descricao, nrvenc FROM condicaoDePgto " +where  , null);
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,R.layout.list_item,cursorCondDePgto,new String[] {"descricao"},
                new int[] {R.id.comboName});

        condDePgto = (Spinner) findViewById(R.id.condpag);
        condDePgto.setAdapter(adapter);

        int x=0;
        if (cursorCondDePgto.moveToFirst()) {
            if (condDePgtoId == 0){
                condDePgtoId = cursorCondDePgto.getInt(cursorCondDePgto.getColumnIndex("_id"));
            }

            do {
                //onde ta o erro
                int cond=cursorCondDePgto.getInt(cursorCondDePgto.getColumnIndex("_id"));
                if (condDePgtoId == cursorCondDePgto.getInt(cursorCondDePgto.getColumnIndex("_id"))) {
                    condDePgto.setSelection(x);
                    nrvenc = cursorCondDePgto.getInt(cursorCondDePgto.getColumnIndex("nrvenc"));
                    break;
                }
                x=x+1;
            } while (cursorCondDePgto.moveToNext());
        }

        condDePgto.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent,
                                       View view, int position, long id) {
                if (isSelection){
                    condDePgtoSelection = position;
                    nrvenc = cursorCondDePgto.getInt(cursorCondDePgto.getColumnIndex("nrvenc"));
                    condDePgtoId = Integer.parseInt(String.valueOf(condDePgto.getSelectedItemId()));
                    vrCReceberPedido = 0;
                    criarParcelas(nrvenc);

                }
                isSelection = true;
            }

            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });
    }

    public void popularComboFormaDePgto(){
        String where_temp="";
        if(config.getEmp().equalsIgnoreCase("vimilk")){
            where_temp=dformp.consultarPorPorCliente(cliente.getCliformmerc());
        }
        cursorformapgto = dbM.rawQuery("SELECT _id, descricao FROM formaDePgto "+where_temp   , null);
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,R.layout.list_item,cursorformapgto,new String[] {"descricao"},
                new int[] {R.id.comboName});


        formaDePgto.setAdapter(adapter);


        int x=0;
        if (cursorformapgto.moveToFirst()) {
            do {
                if (formaDePgtoId == cursorformapgto.getInt(cursorformapgto.getColumnIndex("_id"))) {
                    formaDePgto.setSelection(x);
                    break;
                }
                x=x+1;
            } while (cursorformapgto.moveToNext());
        }

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        isEditItem = false;

       if(config.getEmp().equalsIgnoreCase("pdoeletrica")||config.getEmp().equalsIgnoreCase("pdocarmopolis")||config.getEmp().equalsIgnoreCase("cser")) {
            Cursor cursor2 = d.consuldesc(db, pedidoId);
            try {
                popularLista();
                if (cursor2.moveToFirst()) {
                    if (cursor2.getDouble(cursor2.getColumnIndex("valorDescAcres")) > 0) {

                        idTipoDescAcresPed=cursor2.getInt(cursor2.getColumnIndex("idTipoDescAcresPed"));
                        valorDescAcres=cursor2.getDouble(cursor2.getColumnIndex("valorDescAcres"));
                        percDescAcres=cursor2.getDouble(cursor2.getColumnIndex("percDescAcres"));
                        vrTotal = BigDecimalRound.Round(vrSubTotal - cursor2.getDouble(cursor2.getColumnIndex("valorDescAcres")),2);
                        txVrTotal.setText(String.valueOf(vrTotal));
                        txvalorDescAcres.setText(String.valueOf(cursor2.getDouble(cursor2.getColumnIndex("valorDescAcres"))));
                        Pedido p= new Pedido();
                        p.setIdTipoDescAcresPed(idTipoDescAcresPed);
                        p.setPercDescAcres(percDescAcres);
                        p.setValorDescAcres(valorDescAcres);
                        d.updDatadesc(db,p);
                    }
                }
            }catch(Exception e){
                String erro=e.getMessage();
            }
        }

        acresaprazo();
        if(requestCode == 100 && resultCode==RESULT_OK) {

            int year = data.getIntExtra("year", 0);   // get number of year
            int month = data.getIntExtra("month", 0); // get number of month 0..11
            int day = data.getIntExtra("day", 0);     // get number of day 0..31        // format date and display on screen
            mAnoVencto = year;
            mMesVencto = month;
            mDiaVencto = day;
            isEditCRP = false;
            vrCReceberPedido = 0;
            criarParcelas(0);
        }

        if(requestCode == 101 && resultCode==RESULT_OK) {

            int ano = data.getIntExtra("ano", 0);
            int mes = data.getIntExtra("mes", 0);
            int dia = data.getIntExtra("dia", 0);
            mAnoVencto = ano;
            mMesVencto = mes;
            mDiaVencto = dia;
            condDePgtoId = data.getIntExtra("condDePgtoId", 0);
            mObs.setText(data.getStringExtra("obsPedido"));
            GravarPedido(1);
            //jrclimite
            if(config.getEmp().equalsIgnoreCase("jrc")){
                alterarLimite(1,0);
            }
            PedidoVenda.this.finish();
        }
//entrou
        if(requestCode == 1 && resultCode==RESULT_OK) {

            pedidoId  = data.getExtras().getString("PEDIDO_ID");
            popularLista();
            if (this.config.getEmp().equalsIgnoreCase("geg")){
                popularComboCondDePgto(vrTotal);
            }else{
                popularComboCondDePgto();
            }

            tabHost.setCurrentTab(1);
            btTabCondVenda.setEnabled(true);
            btTabItens.setEnabled(false);
            btTabObsFin.setEnabled(true);
            //jrclimite
            if(config.getEmp().equalsIgnoreCase("jrc")){
                alterarLimite(1,0);
            }
        }
        if(requestCode == 2 && resultCode==RESULT_OK) {

            valorDescAcres = data.getExtras().getDouble("valorDescAcres");
            percDescAcres = data.getExtras().getDouble("percDescAcres");
            idTipoDescAcresPed = data.getExtras().getInt("idTipoDescAcresPed");
            if (idTipoDescAcresPed == 0){
                valorDescAcres = (vrSubTotal/100) * percDescAcres;
                valorDescAcres = BigDecimalRound.Round(valorDescAcres,2);
                vrTotal = vrSubTotal - valorDescAcres;
            }else {
                valorDescAcres = BigDecimalRound.Round(valorDescAcres,2);
                vrTotal = vrSubTotal - valorDescAcres;
            }

            vrTotal = BigDecimalRound.Round(vrTotal,2);
            txVrTotal.setText(String.valueOf(vrTotal));
            txvalorDescAcres.setText(String.valueOf(valorDescAcres));
            criarParcelas(nrvenc);
            //jrclimite
            if(config.getEmp().equalsIgnoreCase("jrc")){
                alterarLimite(1,0);
            }
        }


        if(requestCode == 0 && resultCode==RESULT_OK) {

            if (verificaPedidoNaoEnviado()){
                isEditPedidoItem = data.getExtras().getBoolean("isEditPedidoItem");
                itensPedido.setPedidoItemId(data.getExtras().getString("pedidoItemId"));
                itensPedido.setCodPrduto(data.getExtras().getString("codproduto"));
                itensPedido.setCodUn(data.getExtras().getString("codUn"));
                itensPedido.setIdPedido(data.getExtras().getString("pedidoId"));
                itensPedido.setDescricao(data.getExtras().getString("descricao"));
                itensPedido.setPreco(data.getExtras().getDouble("vrUnit"));
                itensPedido.setPrecoMax(data.getExtras().getDouble("precoMax"));
                itensPedido.setPrecoMin(data.getExtras().getDouble("precoMin"));
                itensPedido.setCustoGer(data.getExtras().getDouble("custoGer"));
                itensPedido.setQuantidade(data.getExtras().getDouble("quantidade"));
                itensPedido.setPrecoTb(data.getExtras().getDouble("precoTb"));
                itensPedido.setValorDescAcresPromo(data.getExtras().getDouble("valorDescAcresPromo"));
                itensPedido.setIdPrecoPromo(data.getExtras().getInt("idPrecoPromo"));
                itensPedido.setIdDescontoQtd(data.getExtras().getInt("idDescontoQtd"));
                itensPedido.setTipoDescAcresPromo(data.getExtras().getInt("idTipoDescAcresPromo"));
                itensPedido.setComissao(data.getExtras().getDouble("comissao"));
                itensPedido.setPreco_st(data.getExtras().getDouble("preco_st"));


                daoItemPedido daoItem = new daoItemPedido(this);
                if (isEditPedidoItem){
                    daoItem.atualizar(db,itensPedido);
                }else {
                    daoItem.Inserir(db,itensPedido);

                }
                popularLista();
                if (this.config.getEmp().equalsIgnoreCase("geg")){
                    popularComboCondDePgto(vrTotal);
                }else{
                    popularComboCondDePgto();
                }
                //jrclimite
                if(config.getEmp().equalsIgnoreCase("jrc")){
                    alterarLimite(1,0);
                }

            }
        }
        if(config.getEmp().equalsIgnoreCase("boav")) {
            calccomissao();
        }
    }

    private void criarParcelas(int diasAd) {
        if (vrCReceberPedido != vrTotal){
            vrCReceberPedido = vrTotal;
            CReceberPedido creceb = new CReceberPedido();
            creceb.set_id(pedidoId);
            creceb.setAnoVencto(mAnoVencto);
            creceb.setCodCliente(clienteId);
            creceb.setDiaVencto(mDiaVencto);
            creceb.setMesVencto(mMesVencto);

            pedido.setMesVencto(mMesVencto);
            pedido.setAnoVencto(mAnoVencto);
            pedido.setDiaVencto(mDiaVencto);

            creceb.setParcela("01");
            creceb.setValor(vrCReceberPedido);
            CReceberPedidoDao dao = new CReceberPedidoDao(this);
            if (dao.InserirAtualizar(db,creceb,isEditCReceberPedido,diasAd)){
                popularCReceberLista();
            }
        }
    }

    public void popularCReceberLista() {
        cursor = db.rawQuery("SELECT  * FROM cRecebPedido tb " +
                " WHERE tb._id = ? " ,  new String[]{""+pedidoId});


        if (cursor.moveToFirst()) {
            CReceberPedido creceb = new CReceberPedido();
            creceb.setCreceb(cursor);

            mAnoVencto = creceb.getAnoVencto(pedido.getAnoVencto());
            mDiaVencto = creceb.getDiaVencto(pedido.getDiaVencto());
            mMesVencto = creceb.getMesVencto(pedido.getMesVencto());


            vrCReceberPedido = creceb.getValor();
            isEditCReceberPedido = true;
            adapter = new SimpleCursorAdapter(this,R.layout.creceberpedido,cursor,new String[] {"vencimento", "valor"},
                    new int[] { R.id.vencimento, R.id.valor});

            CReceberList.setAdapter(adapter);

            CReceberList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (!isEditCRP) {
                        startActivityForResult(new Intent(Intent.ACTION_PICK).setDataAndType(null, CalendarActivity.MIME_TYPE), 100);
                        isEditCRP = true;
                    }
                }
            });

        }

    }

    public void popularLista() {
        //calcular aqui

        if(config.getEmp().equalsIgnoreCase("vimilk")){
            cursorItens = db.rawQuery("SELECT *,round((preco * comissao)/100,2) as vlrcomissao,round(((preco * comissao)/100),2)*quantidade as total  FROM itensPedido tb " +
                    " WHERE tb.idPedido = ? ", new String[]{"" + pedidoId});
        }else{
            cursorItens = db.rawQuery("SELECT *,round((preco * comissao)/100,2) as vlrcomissao,round(((preco * comissao)/100),2)*quantidade as total  FROM itensPedido tb " +
                    " WHERE tb.idPedido = ? ", new String[]{"" + pedidoId});

        }

        vrSubTotal = 0;
        vrSubTotal_st=0;
        if(config.getEmp().equalsIgnoreCase("pdoeletrica")||config.getEmp().equalsIgnoreCase("pdocarmopolis")){
            if (cursorItens.moveToFirst()) {
                    do {
                         if (cursorItens.getDouble(cursorItens.getColumnIndex("comprimento")) > 0) {
                            vrSubTotal = vrSubTotal + ((cursorItens.getDouble(cursorItens.getColumnIndex("quantidade")) * cursorItens.getDouble(cursorItens.getColumnIndex("comprimento"))) * cursorItens.getDouble(cursorItens.getColumnIndex("preco")));
                        } else {
                            vrSubTotal = vrSubTotal + (cursorItens.getDouble(cursorItens.getColumnIndex("preco")) * cursorItens.getDouble(cursorItens.getColumnIndex("quantidade")));
                        }
                    }while (cursorItens.moveToNext()) ;
            }
        }else {
            if (cursorItens.moveToFirst()) {
                do {
                    vrSubTotal = vrSubTotal + (cursorItens.getDouble(cursorItens.getColumnIndex("preco")) * cursorItens.getDouble(cursorItens.getColumnIndex("quantidade")));
                    if(config.getEmp().equalsIgnoreCase("vimilk")) {
                        double teste=cursorItens.getDouble(cursorItens.getColumnIndex("preco_st"));
                        vrSubTotal_st = vrSubTotal_st + (cursorItens.getDouble(cursorItens.getColumnIndex("preco_st")));
                    }
                } while (cursorItens.moveToNext());
            }
        }
        vrTotal = vrSubTotal - valorDescAcres;
        valorDescAcres = BigDecimalRound.Round(valorDescAcres,2);
        vrTotal = BigDecimalRound.Round(vrTotal,2);
        vrSubTotal = BigDecimalRound.Round(vrSubTotal,2);
        txVrTotal.setText(String.valueOf(vrTotal));
        txVrSubTotal.setText(String.valueOf(vrSubTotal));
        if(config.getEmp().equalsIgnoreCase("vimilk")){
            txvalorDescAcres.setText(String.valueOf(BigDecimalRound.Round(vrSubTotal_st,2)));
            txVrTotal.setText(String.valueOf(BigDecimalRound.Round(vrSubTotal+vrSubTotal_st,2)));
        }else{
            txvalorDescAcres.setText(String.valueOf(valorDescAcres));
        }
        criarParcelas(nrvenc);
        if(config.getEmp().equalsIgnoreCase("boav")){
            adapter = new SimpleCursorAdapter(this,R.layout.itempedido_detal
                    ,cursorItens,new String[] {"descricao", "quantidade",  "preco","vlrcomissao","total"},
                    new int[] { R.id.descricao, R.id.quantidade, R.id.preco,R.id.detal,R.id.detal2});
        }else if(config.getEmp().equalsIgnoreCase("vimilk")){
            adapter = new SimpleCursorAdapter(this,R.layout.item_ped_st
                    ,cursorItens,new String[] {"descricao", "quantidade",  "preco","preco_st"},
                    new int[] { R.id.descricao, R.id.quantidade, R.id.preco,R.id.detal});

        }else{
            adapter = new SimpleCursorAdapter(this,R.layout.itempedido,cursorItens,new String[] {"descricao", "quantidade",  "preco",},
                    new int[] { R.id.descricao, R.id.quantidade, R.id.preco});
        }

        produtosList.setAdapter(adapter);
        //

    }

    public void opcoesDoMenu(View v) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.op_menu_principal)
                .setItems(R.array.op_menu_pedido, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialoginterface, int i) {
                        selecionarMenuCliente(i);
                    }
                }).show();
    }
    protected void selecionarMenuCliente(int i) {
        switch (i) {
            case 0:
                if (verificaPedidoNaoEnviado()){
                    confirmaPedido();
                }
                break;
            case 1:
                if (verificaPedidoNaoEnviado()){
                    incluirItemPedido();
                }
                break;
            case 2:
                if (verificaPedidoNaoEnviado()){
                    excluirPedido();
                }
                break;
            case 3:
                sair();
                break;
            case 4:
                Intent ix = new Intent(PedidoVenda.this,InfoFinanceiras.class);
                ix.putExtra("CLIENTE_ID", cliente.getCodCliente());
                PedidoVenda.this.startActivityForResult(ix, 0);
                break;


        }
    }

    public void incluirItemPedido() {

        GravarPedido(1);

        Intent ix = new Intent(PedidoVenda.this,IncluirItensDoPedido.class);
        ix.putExtra("PEDIDO_ID",pedido.get_id());
        ix.putExtra("tbPrecoId", tbPrecoId);
        ix.putExtra("idFiltroProduto","");
        ix.putExtra("valorDescAcres",pedido.getValorDescAcres());
        ix.putExtra("graurisco",graurisco);
        ix.putExtra("condDePgtoId",condDePgtoId);
        ix.putExtra("cliente_id", codcli);
        ix.putExtra("vrtotal",String.valueOf(txVrTotal.getText()));
        ix.putExtra("lisped",vrTotalisped);
        if(config.getEmp().equalsIgnoreCase("lavrasatacado")){
            ix.putExtra("codigo",codigo);
        }
        if(cursorCondDePgto.moveToFirst()) {
            ix.putExtra("nrvenc", cursorCondDePgto.getInt(cursorCondDePgto.getColumnIndex("nrvenc")));
        }
        if(isEditPedido){
            ix.putExtra("isedit",1);
        }else{
            ix.putExtra("isedit",0);
        }
        ix.putExtra("limite",String.valueOf(limitecreditoTx.getText()));

        PedidoVenda.this.startActivityForResult(ix,1);
    }

    public void aplicaDescAcres() {
        Intent i = new Intent(PedidoVenda.this,AplicaDescAcresPed.class);
        i.putExtra("idTipoDescAcresPed",idTipoDescAcresPed);
        i.putExtra("valorDescAcres",valorDescAcres);
        i.putExtra("percDescAcres", percDescAcres);
        i.putExtra("vrSubTotal",vrSubTotal);
        PedidoVenda.this.startActivityForResult(i, 2);
    }

    public void GravarPedido(int confirmado) {
        pedido.setOperacao(Integer.parseInt(String.valueOf(operacao.getSelectedItemId())));
        pedido.setTbPrecoCliente(cliente.getTbPrecoCliente());
        //   condDePgtoSelection=condDePgto.getSelectedItemPosition()+1;
        pedido.setCondDePgto(condDePgtoId);
        pedido.setFormaDePgto(Integer.parseInt(String.valueOf(formaDePgto.getSelectedItemId())));
        pedido.setOperacao(Integer.parseInt(String.valueOf(operacao.getSelectedItemId())));
        pedido.setObs(mObs.getText().toString());
        pedido.setIdDliente(cliente.getCodigo());
        pedido.setNomeCliente(cliente.getRazao());
        pedido.setCnpjcpf(cliente.getCnpjcpf());
        pedido.setCidade(cliente.getCidade());
        pedido.setConfirmado(confirmado);
        pedido.setIdTipoDescAcresPed(idTipoDescAcresPed);
        pedido.setValorDescAcres(valorDescAcres);
        pedido.setPercDescAcres(percDescAcres);

        pedido.setVrTotal(vrTotal);
        pedido.setMesVencto(mMesVencto);
        pedido.setAnoVencto(mAnoVencto);
        pedido.setDiaVencto(mDiaVencto);
        pedido.setId_visita(idVisita);
        pedido.setObs(mObs.getText().toString());
        //jrclimite
        if(config.getEmp().equalsIgnoreCase("jrc")){
            if(!rec.getText().toString().equalsIgnoreCase("")){
                pedido.setRecebimento(Double.valueOf(rec.getText().toString()));
            }
            if(!limitecreditoTx.getText().toString().equalsIgnoreCase("")){
                pedido.setLimcredped(Double.parseDouble(String.valueOf(limitecreditoTx.getText())));
            }


            if(isEditPedido){
         //       dao.upLimite(dbM,codcli,Double.parseDouble(dao.consultarLimite(dbM, codcli)) + (vrTotalisped-vrTotal));
            }
        }
        daoPedido dao = new daoPedido(this);
        daoCreateDBM daom = new daoCreateDBM(this);
        dbM =  daom.getWritableDatabase();
        this.config = ConfigVendedor.getConfig(dbM);

        if(this.config.getEmp().equalsIgnoreCase("MVitoria")||this.config.getEmp().equalsIgnoreCase("grm")){
            double sdverba=dao.buscarSaldoVerba(db,pedidoId);
            pedido.setSaldoVerba(sdverba);
        }
        if (incluirPedido){
            //insere pedido

            pedido = dao.Inserir(db,pedido,dataHoraMS);
            pedidoId=pedido.get_id();
            incluirPedido = false;
        } else{
            dao.atualizar(db,pedido,dataHoraMS);
            pedidoId=pedido.get_id();
        }
    }

    private void updateDisplay() {
        /*mVencto.setText(
            new StringBuilder()
            		.append(mDiaVencto).append("-")
                    .append(mMesVencto).append("-")
                    .append(mAnoVencto).append(" "));*/
    }

    public void excluirPedido() {
        new AlertDialog.Builder(this)
                .setTitle("Exclusão")
                .setItems(R.array.op_alerta_sim_nao, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialoginterface, int i) {
                        if (i == 0) {
                            selecionarAlertaSim();
                        }
                    }
                }).show();
    }

    protected void selecionarAlertaSim() {
        daoPedido daop = new daoPedido(this);
        //jrclimite
        if(!config.getEmp().equalsIgnoreCase("jrc")){
            daop.excluir(db,pedido);
            Intent result = new Intent(this, PedidoVenda.class);
            String mensagem="";
            setResult(1000,result);
            PedidoVenda.this.finish();
        }
        //jrclimite
        if(config.getEmp().equalsIgnoreCase("jrc")){

            cursor = dbM.rawQuery("select valor from creceb where cliente='" + codcli + "'", null);
            int cont = cursor.getCount();
            boolean ver = dao.verifsit(dbM, cliente.getTolerancia(), data, cont, cliente.getNdup(), dcre.consultarsomaCreceb(dbM, cliente), Double.parseDouble(dao.consultarLimite(dbM, cliente.getCodCliente())), cliente.getCodCliente());
            double a = Double.valueOf(dao.consultarLimite(dbM, codcli));
            double b=Double.parseDouble(dao.consultarLimitetotal(dbM, cliente.getCodCliente()));
            double c=dcre.consultarsomaCreceb(dbM, cliente);
            double total=0;
            boolean verr =verreceb();
            double r=0;
            int conta=d.consultarqtdped(db, cliente.getCodigo());
            if (dao.consultarLimiteTemp(dbM, cliente.getCodCliente())>0) {
                    if (d.consultarqtdped(db, cliente.getCodigo()) == 1) {
                        total = 0;
                    }else if (d.consultarqtdped(db, cliente.getCodigo()) > 1 && verreceb()) {
                        total = Double.parseDouble(dao.consultarLimite(dbM, cliente.getCodCliente())) + vrTotal;
                    }else if(d.consultarqtdped(db, cliente.getCodigo())>1&&!verreceb()){

                        if(!rec.getText().toString().equalsIgnoreCase("")){
                            r=Double.parseDouble(rec.getText().toString());
                        }
                        total=Double.parseDouble(dao.consultarLimite(dbM, cliente.getCodCliente()))+vrTotal-r;
                    }
                }
                if (total == 0.01) {
                    total = 0;
                }
                if(Double.parseDouble(dao.consultarLimitetotal(dbM,cliente.getCodigo()))<r && total<0){
                    total = 0;
                }

                if(total>=0) {
                    dao.upLimitexclu(dbM, codcli, total);
                    daop.excluir(db, pedido);
                    Intent result = new Intent(this, PedidoVenda.class);
                    String mensagem = "";
                    setResult(1000, result);
                    PedidoVenda.this.finish();
                }else{
                     Alertas al = new Alertas(this);
                     al.AlertaSinc("Limite negativo com a alteração !");
                 }


        }
    }

    public void editarExcluirItem() {
        new AlertDialog.Builder(this)
                .setItems(R.array.op_alerta_editar_excluir_item, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialoginterface, int i) {
                        if (i == 0) {
                            selecionarAlertaEditarItem();
                        } else {
                            selecionarAlertaExcluirItem();
                        }
                    }
                }).show();
    }

    protected void selecionarAlertaExcluirItem() {
        isEditItem = false;
        if (verificaPedidoNaoEnviado()){
            itensPedido.setItemPedido(cursorItens);
            daoItemPedido dao = new daoItemPedido(this);
            dao.excluir(db, itensPedido);
            popularLista();
            if (this.config.getEmp().equalsIgnoreCase("geg")){
                popularComboCondDePgto(vrTotal);
            }else{
                popularComboCondDePgto();
            }
            if(config.getEmp().equalsIgnoreCase("jrc")){
                alterarLimite(1,0);
            }


        }
    }


    protected void selecionarAlertaEditarItem() {
        itensPedido.setItemPedido(cursorItens);
        isEditPedidoItem = true;
        Intent i = new Intent(PedidoVenda.this,IncluirItem.class);
        i.putExtra("DESCRI", cursorItens.getString(cursorItens.getColumnIndex("descricao")));
        i.putExtra("CODPRODUTO", itensPedido.getCodPrduto());
        i.putExtra("codUn", itensPedido.getCodUn());
        i.putExtra("tbPrecoId",tbPrecoId);
        i.putExtra("pedidoItemId",itensPedido.getPedidoItemId());
        i.putExtra("quantidade",itensPedido.getQuantidade());
        i.putExtra("precoTb",itensPedido.getPrecoTb());
        i.putExtra("precoMax",itensPedido.getPrecoMax());
        i.putExtra("precoMin",itensPedido.getPrecoMin());
        i.putExtra("custoGer",itensPedido.getCustoGer());
        i.putExtra("vrUnit",BigDecimalRound.Round(itensPedido.getPreco(), 2));
        i.putExtra("pedidoId",itensPedido.getIdPedido());
        i.putExtra("isEditPedidoItem",isEditPedidoItem);
        i.putExtra("condDePgtoId",condDePgtoId);
        i.putExtra("idPrecoPromo", itensPedido.getIdPrecoPromo());
        i.putExtra("valorDescAcresPromo", itensPedido.getValorDescAcresPromo());
        i.putExtra("idDescontoQtd", itensPedido.getIdDescontoQtd());
        i.putExtra("idTipoDescAcresPromo", itensPedido.getTipoDescAcresPromo());
        i.putExtra("limite", String.valueOf(limitecreditoTx.getText()));
        i.putExtra("vrtotal",String.valueOf(txVrTotal.getText()));
        i.putExtra("lisped",vrTotalisped);
        i.putExtra("clienteid",clienteId);
        i.putExtra("comprimento",itensPedido.getComprimento());
        i.putExtra("quantcalc",itensPedido.getQuantcalc());
        if(config.getEmp().equalsIgnoreCase("lavrasatacado")){
            i.putExtra("codigo",codigo);
        }
        i.putExtra("codcli",codcli);
        i.putExtra("nrvenc",cursorCondDePgto.getInt(cursorCondDePgto.getColumnIndex("nrvenc")));
        if(isEditPedido){
            i.putExtra("isedit",1);
        }else{
            i.putExtra("isedit",0);
        }

        PedidoVenda.this.startActivityForResult(i,0);
    }

    public void sair() {
        if (pedido.getLoteEnvio() == null){
            alertaSair();
        } else {
            PedidoVenda.this.finish();
        }
    }

    public void confirmaPedido() {

        boolean limite = false;
        //jrclimite
        if (config.getEmp().equalsIgnoreCase("jrc")) {
            alterarLimite(1,0);
            if(Double.parseDouble(dao.consultarLimite(dbM, cliente.getCodCliente()))<0){
                 limite=true;
            }
            if(cliente.getNdup()==0&&cliente.getLimiteDeCredito()==0&&cliente.getTolerancia()==0) {
                limite=false;
            }
        }

        if(config.getEmp().equalsIgnoreCase("boav")){
            if(this.limite>0) {
                int teste=Integer.parseInt(String.valueOf(formaDePgto.getSelectedItemId()));
                if (condDePgtoId==1 && Integer.parseInt(String.valueOf(formaDePgto.getSelectedItemId()))==3) {
                   limite=false;
                }else{
                    if (vrTotal > this.limite) {
                        limite = true;
                        a.Alerta("Atençao! Valor da venda é maior que o limite permitido: R$ " + this.limite);
                    }
                }
            }
        }
        if(config.getEmp().equalsIgnoreCase("vimilk")) {
            if (cliente.getClianalise() == 2) {
                if (condDePgtoId != 1) {
                    a.Alerta("Cliente sob analise, vendas permitidas somente a vista !");
                    limite = true;
                }
            }

        }
        if(config.getEmp().equalsIgnoreCase("vimilk")|| config.getEmp().equalsIgnoreCase("boav")) {
            if (vrTotal < cliente.getClivlrminvenda() && operacaoId <= 2) {
                a.Alerta("Atençao! Venda com valor abaixo do limite: R$ " + cliente.getClivlrminvenda());
                limite = true;
            }
        }

            if (limite == false) {
                if (isConfirmaPedido||config.getEmp().equalsIgnoreCase("pdoeletrica")||config.getEmp().equalsIgnoreCase("pdocarmopolis")) {
                    GravarPedido(1);
                    Intent i = new Intent(PedidoVenda.this, ConfirmaPedido.class);
                    i.putExtra("clienteId", clienteId);
                    i.putExtra("condDePgtoId", condDePgtoId);
                    i.putExtra("pedidoId", pedidoId);
                    i.putExtra("obs", mObs.getText().toString());
                    i.putExtra("codcli", codcli);
                    i.putExtra("vrTotal", vrTotal);
                    i.putExtra("subtotal", vrSubTotal);
                    i.putExtra("st", vrSubTotal_st);
                    i.putExtra("codigo", cliente.getCodigo());
                    i.putExtra("vlrreceb", vlrreceb);
                    i.putExtra("limcredped", String.valueOf(limitecreditoTx.getText()));
                    i.putExtra("idVisita", idVisita);
                    daoPedido dao = new daoPedido(this);
                    if (this.config.getEmp().equalsIgnoreCase("MVitoria") || this.config.getEmp().equalsIgnoreCase("grm")) {
                        double sdverba = dao.buscarSaldoVerba(db, pedidoId);
                        i.putExtra("saldoverba", sdverba);
                    }

                    Long ano = mAnoVencto;
                    Long mes = mMesVencto;
                    Long dia = mDiaVencto;

                    i.putExtra("ano", ano.intValue());
                    i.putExtra("dia", dia.intValue());
                    i.putExtra("mes", mes.intValue());
                    PedidoVenda.this.startActivityForResult(i, 101);
                } else {
                    daoPedido dp = new daoPedido(this);
                    daoItemPedido dip = new daoItemPedido(this);
                    dp.upComissao(db, pedidoId, dip.calcTotalComissao(db, pedidoId));
                    GravarPedido(1);
                    PedidoVenda.this.finish();
                }
            }
        }

    public void alertaSair() {
        new AlertDialog.Builder(this)
                .setTitle("Deseja Sair do Pedido sem Salvar?")
                .setItems(R.array.op_alerta_sim_nao, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialoginterface, int i) {
                        if (i == 0) {
                            cancelarPedido();
                        }
                    }
                }).show();
    }
    public void cancelarPedido() {
        if (isEditPedido){
            PedidoRollBack.rollBackpedido(this,dataHoraMS);
            Intent result = new Intent(this, PedidoVenda.class);
            String mensagem="";
            setResult(1000,result);
            dao.upLimite(dbM,codcli,limiteini);

        } else {
            selecionarAlertaSim();
        }
        PedidoVenda.this.finish();
    }

    private boolean verificaPedidoNaoEnviado() {
        if (pedido.getLoteEnvio() != null){
            new AlertDialog.Builder(this)
                    .setTitle("Pedido já Foi enviado")
                    .setNegativeButton("Ok", null)
                    .show();
            return false;
        } else {
            return true;
        }
    }

   class MyGestureDetector extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            try {
                float x1=e1.getX();
                float x2=e2.getX();
                if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
                    return false;
                // right to left swipe
                if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    int i = tabHost.getCurrentTab();
                    tabHost.setCurrentTab(i + 1);
                } else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    int i = tabHost.getCurrentTab();
                    tabHost.setCurrentTab(i - 1);
                }
                botswip();
            } catch (Exception e) {
                // nothing
            }
            return false;
        }
    }
    public void botswip(){
        if(tabHost.getCurrentTab()==0){
            btTabCondVenda.setEnabled(false);
            btTabItens.setEnabled(true);
            btTabObsFin.setEnabled(true);
        }else if(tabHost.getCurrentTab()==1){
            btTabCondVenda.setEnabled(true);
            btTabItens.setEnabled(false);
            btTabObsFin.setEnabled(true);
        }else if(tabHost.getCurrentTab()==2){
            btTabCondVenda.setEnabled(true);
            btTabItens.setEnabled(true);
            btTabObsFin.setEnabled(false);
        }
    }

    @Override
    public void onBackPressed() {
        return;
    }
    public void calccomissao(){
        daoItemPedido dp = new daoItemPedido(this);
        txvalorDescAcres.setText(String.valueOf( dp.calcTotalComissao(db, pedidoId)));
    }

    @Override
    protected void onStop()
    {
        Log.e("CiclodeVida", "Parei");
        super.onStop();
    }

    public void verica_altera_preco_vista(final ArrayList<ItemPedido> it, final String pedidoId) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        TextView textView = new TextView(this);
        textView.setText("Existe(m) item(s) com o preço a vista. Deseja realmente mudar a condição de pagamento? Essa operação mudará o preço destes itens.");
        builder.setCustomTitle(textView);
        builder.setItems(R.array.op_alerta_sim_nao, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialoginterface, int i) {
                if (i == 0) {
                    daoItemPedido dip = new daoItemPedido(PedidoVenda.this);
                    dip.altera_preco_a_vista(it, pedidoId);
                    popularLista();
                } else {
                    condDePgto.setSelection(0);
                }
            }
        }).show();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        Log.e("CiclodeVida", "Método OnDestroy()");
    }

}

