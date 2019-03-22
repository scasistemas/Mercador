package br.com.mobiwork.mercador.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import br.com.mobiwork.mercador.model.ItemPedido;
import br.com.mobiwork.mercador.util.BigDecimalRound;

public class daoItemPedido extends daoCreateDB {
    private Context ctx;

    public daoItemPedido(Context context) {
        super(context);
        ctx = context;
    }

    public void Inserir(SQLiteDatabase db, ItemPedido itensPedido) {
        ContentValues values = new ContentValues();
        values.put("idPedido", itensPedido.getIdPedido());
        values.put("descricao", itensPedido.getDescricao());
        values.put("codPrduto", itensPedido.getCodPrduto());
        values.put("quantidade", itensPedido.getQuantidade());
        values.put("loteEnvio", itensPedido.getLoteEnvio());
        values.put("preco", itensPedido.getPreco());
        values.put("precoMax", itensPedido.getPreco());
        values.put("precoMin", itensPedido.getPrecoMin());
        values.put("custoGer", itensPedido.getCustoGer());
        values.put("valorDescAcresPromo", itensPedido.getValorDescAcresPromo());
        values.put("precoTb", itensPedido.getPrecoTb());
        values.put("qtdeconvertida", itensPedido.getQtdeconvertida());
        values.put("valorDescAcresPromo", itensPedido.getValorDescAcresPromo());
        values.put("idPrecoPromo", itensPedido.getIdPrecoPromo());
        values.put("idDescontoQtd", itensPedido.getIdDescontoQtd());
        values.put("tipoDescAcresPromo", itensPedido.getTipoDescAcresPromo());
        values.put("codUn", itensPedido.getCodUn());
        values.put("pesoliq", itensPedido.getPesoliq());
        values.put("espessura", itensPedido.getEspessura());
        values.put("comprimento", itensPedido.getComprimento());
        values.put("largura", itensPedido.getLargura());
        values.put("quantcalc", itensPedido.getQuantcalc());
        values.put("comissao",itensPedido.getComissao());
        values.put("preco_st",itensPedido.getPreco_st());
        db.insert("itensPedido", "", values);
    }

    public static boolean atualizar(SQLiteDatabase db, ItemPedido itensPedido) {
        ContentValues values = new ContentValues();
        values.put("idPedido", itensPedido.getIdPedido());
        values.put("codPrduto", itensPedido.getCodPrduto());
        values.put("descricao", itensPedido.getDescricao());
        values.put("loteEnvio", itensPedido.getLoteEnvio());
        values.put("precoMax", itensPedido.getPreco());
        values.put("precoMin", itensPedido.getPrecoMin());
        values.put("custoGer", itensPedido.getCustoGer());
        values.put("valorDescAcresPromo", itensPedido.getValorDescAcresPromo());
        values.put("idPrecoPromo", itensPedido.getIdPrecoPromo());
        values.put("idDescontoQtd", itensPedido.getIdDescontoQtd());
        values.put("tipoDescAcresPromo", itensPedido.getTipoDescAcresPromo());
        values.put("quantidade", itensPedido.getQuantidade());
        values.put("preco", itensPedido.getPreco());
        values.put("precoTb", itensPedido.getPrecoTb());
        values.put("qtdeconvertida", itensPedido.getQtdeconvertida());
        values.put("codUn", itensPedido.getCodUn());
        values.put("pesoliq", itensPedido.getPesoliq());
        values.put("espessura", itensPedido.getEspessura());
        values.put("comprimento", itensPedido.getComprimento());
        values.put("largura", itensPedido.getLargura());
        values.put("quantcalc", itensPedido.getQuantcalc());
        values.put("comissao", itensPedido.getComissao());
        values.put("preco_st", itensPedido.getPreco_st());


        return db.update("itensPedido", values, "_id = " + itensPedido.getPedidoItemId(), null) > 0;
    }

    public static boolean excluir(SQLiteDatabase db, ItemPedido item) {
        return delete(db, "itensPedido", "_id = " + item.getPedidoItemId(), null) > 0;
    }

    public static boolean excluir(SQLiteDatabase db, String pedidoid) {
        return delete(db, "itensPedido", "_id = " + pedidoid, null) > 0;
    }





    public Double precoaprazo(SQLiteDatabase db, SQLiteDatabase dbM, String codcli, String pedidoId, int nrvenc, String codpr) {

        int tbpreco = 0;
        daoCliente dc = new daoCliente(ctx);
        double valorpr = 0;


        Cursor cliente = dc.consultarPorIdRestC(dbM, codcli);

        if (cliente.moveToFirst()) {
            tbpreco = cliente.getInt(cliente.getColumnIndex("tbPrecoCliente"));
            if (tbpreco == 0) {
                tbpreco = 1;
            }
        }
            Cursor vrproduto = dbM.rawQuery("SELECT *  FROM produtoInfo tb " +
                    " WHERE tb.codPrduto = ? and tb._idTabela = ? ", new String[]{"" + codpr, "" + tbpreco});
            if (vrproduto.moveToFirst()) {
                valorpr = vrproduto.getDouble(vrproduto.getColumnIndex("precotb"));
            }
            if (nrvenc!=1) {
                valorpr = valorpr + (valorpr * 2.5 / 100);
            }

        return valorpr;
    }




    public void acrecimoaprazo(SQLiteDatabase db,SQLiteDatabase dbM,String codcli,String pedidoId,String nrvenc){

        int tbpreco=0;
        daoCliente dc =new daoCliente(ctx);


        Cursor cliente=dc.consultarporCodCur(dbM, codcli);

        if(cliente.moveToFirst()){
            tbpreco=cliente.getInt(cliente.getColumnIndex("tbPrecoCliente"));
            if(tbpreco==0){
                tbpreco=1;
            }
        }



        Cursor cursorItens = db.rawQuery("SELECT *  FROM itensPedido tb " +
                " WHERE tb.idPedido = ? " ,  new String[]{ ""+pedidoId});

        if(cursorItens.moveToFirst()){
            do{
                double valor=0;
                double valorpr=0;
                double valortb=0;
                double c=0;
                double quantidade=cursorItens.getDouble(cursorItens.getColumnIndex("quantidade"));
                String codproduto=cursorItens.getString(cursorItens.getColumnIndex("codPrduto"));
                Cursor vrproduto  = dbM.rawQuery("SELECT *  FROM produtoInfo tb " +
                        " WHERE tb.codPrduto = ? and tb._idTabela = ? ", new String[]{"" + codproduto, "" + tbpreco});
                if(vrproduto.moveToFirst()){
                    valorpr=cursorItens.getDouble(cursorItens.getColumnIndex("precoMax"));
                    valor=cursorItens.getDouble(cursorItens.getColumnIndex("preco"));
                    valortb=vrproduto.getDouble(cursorItens.getColumnIndex("precoTb"));
                    c= BigDecimalRound.Round(valortb + (valortb * 2.5 / 100), 2);
                }
                if(!nrvenc.equalsIgnoreCase("1")) {
                    if(valor==valortb) {
                            valorpr = valor + (valor * 2.5 / 100);
                            String sqlup=("UPDATE itensPedido SET preco = "+valorpr+" WHERE idPedido = '"+pedidoId+"' and codPrduto='"+codproduto+"'");
                            db.execSQL(sqlup);

                    }
                }else{
                    if(valor!=valorpr){
                        String sqlup=("UPDATE itensPedido SET preco = "+valorpr+" WHERE idPedido = '"+pedidoId+"' and codPrduto='"+codproduto+"'");
                        db.execSQL(sqlup);
                    }else if(valorpr==BigDecimalRound.Round(valortb + (valortb * 2.5 / 100),2)){
                        String sqlup=("UPDATE itensPedido SET preco = "+valortb+" WHERE idPedido = '"+pedidoId+"' and codPrduto='"+codproduto+"'");
                        db.execSQL(sqlup);
                    }
                }


            }while (cursorItens.moveToNext());
        }
    }
    public void upPreco_st(SQLiteDatabase db,String pedidoId,double preco_st,String codproduto){
        try {
            String sqlup = ("UPDATE itensPedido SET preco_st = " + preco_st + " WHERE idPedido = '" + pedidoId + "' and codPrduto='" + codproduto + "'");
            db.execSQL(sqlup);
        }catch(Exception e){

        }
    }



    public void comissao(SQLiteDatabase db,double comissao,String id){
        double valor=0;
        String sqlup=("UPDATE pedidos SET comissao = "+comissao+" WHERE _id = '"+id+"'");
        db.execSQL(sqlup);
    }

    public double calcTotalComissao(SQLiteDatabase db,String pedido){
           Cursor cursor = db.rawQuery("select sum(round(((preco * comissao)/100),2)*quantidade) as comissaoTotal from itensPedido where idPedido='"+pedido+"'", null);
        if(cursor.moveToFirst()){
            return BigDecimalRound.Round(cursor.getDouble(cursor.getColumnIndex("comissaoTotal")),2);
        }else{
            return 0;
        }
    }

    public double calcTotalComissaoTotalNEnv(SQLiteDatabase db){
        Cursor cursor = db.rawQuery("select sum(round(((preco * comissao)/100),2)*quantidade) as comissaoTotal from itensPedido where  loteEnvio is null", null);
        if(cursor.moveToFirst()){
            return BigDecimalRound.Round(cursor.getDouble(cursor.getColumnIndex("comissaoTotal")), 2);
        }else{
            return 0;
        }
    }
    public double calcTotalComissaoTotalporlote(SQLiteDatabase db,String lote){
        Cursor cursor = db.rawQuery("select sum(round(((preco * comissao)/100),2)*quantidade) as comissaoTotal from itensPedido where  loteEnvio ='"+lote+"'", null);
        if(cursor.moveToFirst()){
            return BigDecimalRound.Round(cursor.getDouble(cursor.getColumnIndex("comissaoTotal")), 2);
        }else{
            return 0;
        }
    }
    public Cursor pesqporid(SQLiteDatabase db,String pedidoid){
        Cursor localCursor=null;
        localCursor = db.rawQuery("SELECT *  FROM itensPedido where idPedido='"+pedidoid+"'", null);
        return localCursor;
    }



    public ArrayList<String> consultarUltimaVenda(SQLiteDatabase db,String cliente,String produto) {
        Cursor cursor=null;
        String erro="";
        ArrayList<String> result = new ArrayList<>();
        SimpleDateFormat sd = new SimpleDateFormat("dd/MM/yyyy ");
        try {
            cursor = db.rawQuery("select distinct pedidos._id,idpedido,pedidos.cliente,pedidos.data,codprduto,descricao,preco from itensPedido inner join pedidos on itenspedido.idPedido=pedidos._id where trim(cliente)='"+cliente.trim()+"' \n" +
                    "and codPrduto='"+produto+"' and pedidos.data= (select max(pedidos.data)  from itensPedido inner join pedidos on itenspedido.idPedido=pedidos._id where trim(cliente)='"+cliente.trim()+"' \n" +
                    "and codPrduto='"+produto+"' ) ", null);
            if(cursor.moveToFirst()){

                String dia = "",mes="",ano="",datafim;
                int cont=0;
                String str = cursor.getString(cursor.getColumnIndex("data"));
                for(int i=0;i<str.length();i++){
                    if(String.valueOf(str.charAt(i)).equals("-")){
                      cont++;
                    }else {
                        if (cont == 2) {
                            dia = dia + String.valueOf(str.charAt(i));
                        }
                        if (cont == 1) {
                            mes = mes + String.valueOf(str.charAt(i));
                        }
                        if (cont == 0) {
                            ano = ano + String.valueOf(str.charAt(i));
                        }
                    }
                }
                datafim=dia+"/"+String.format("%02d",Integer.parseInt(mes))+"/"+ano;
                result.add(0,String.valueOf(datafim));
                result.add(1,String.valueOf(cursor.getDouble(cursor.getColumnIndex("preco"))));
                return result;
            }

        }catch (Exception e){
            erro=e.getMessage();
            return result;
        }
        return result;
    }

    public ArrayList<ItemPedido> pesqPrecoAbaixo(String pedido){ //pesquisar se existe pre�o abaixo do de venda
        SQLiteDatabase db=this.getReadableDatabase();
        ArrayList<ItemPedido> ait = new ArrayList<ItemPedido>();
        Cursor cursor = db.rawQuery("select codPrduto,precoTb,preco from itensPedido where idPedido='" + pedido + "'", null);
        if(cursor.moveToFirst()){
            do {
                double precoTb=cursor.getDouble(cursor.getColumnIndex("precoTb"));
                double preco=cursor.getDouble(cursor.getColumnIndex("preco"));
                if (preco<precoTb){
                    ItemPedido it = new ItemPedido();
                    it.setCodPrduto(cursor.getString(cursor.getColumnIndex("codPrduto")));
                    it.setPrecoTb(cursor.getDouble(cursor.getColumnIndex("precoTb")));
                    it.setPreco(cursor.getDouble(cursor.getColumnIndex("preco")));
                    ait.add(it);
                }
            }while(cursor.moveToNext());
        }
        db.close();
        return ait;
    }

    public ArrayList<ItemPedido> pesqPrecoOriginal(String pedido){ //pesquisar se existe pre�o abaixo do de venda
        SQLiteDatabase db=this.getReadableDatabase();
        ArrayList<ItemPedido> ait = new ArrayList<ItemPedido>();
        Cursor cursor = db.rawQuery("select codPrduto,comissao,preco from itensPedido where idPedido='" + pedido + "'", null);
        if(cursor.moveToFirst()){
            do {
                daoProdutoInfo dp = new daoProdutoInfo(ctx);
                double precoTb=dp.consultarPrecoPorComiss(cursor.getString(cursor.getColumnIndex("codPrduto")), cursor.getDouble(cursor.getColumnIndex("comissao")));
                double preco=cursor.getDouble(cursor.getColumnIndex("preco"));
                if (preco<precoTb){
                    ItemPedido it = new ItemPedido();
                    it.setCodPrduto(cursor.getString(cursor.getColumnIndex("codPrduto")));
                    it.setPrecoTb(precoTb);
                    it.setPreco(cursor.getDouble(cursor.getColumnIndex("preco")));
                    ait.add(it);
                }
            }while(cursor.moveToNext());
        }
        db.close();
        return ait;
    }



    public void altera_preco_a_vista(ArrayList<ItemPedido> it,String pedido){
        SQLiteDatabase db=this.getWritableDatabase();
        for(int i=0;i<it.size();i++) {
            String sqlup = ("UPDATE itensPedido SET preco = " + it.get(i).getPrecoTb() + " WHERE idPedido = '" + pedido + "' AND codprduto = '" + it.get(i).getCodPrduto() + "'");
            db.execSQL(sqlup);
        }
        db.close();
     //   pesqporid(pedido);
    }

    public double consVendasPedData(String datai,String dataf,String metproduto){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor localCursor=null;
        double total=0;
        String where1="";
        String where2="";
        if (!datai.equals("")&&!dataf.equals("")) {
             where1 = "where pedidos.data>='" + datai + "' and  pedidos.data<='" + dataf + "'  ";
             if (!metproduto.equals("")) {
                 where2 = "and itensPedido.codPrduto='"+metproduto+"'";
             }
        }
        try {
            localCursor = db.rawQuery("select sum(itensPedido.quantidade*itensPedido.pesoliq) as soma from itensPedido inner join " +
                    "pedidos on pedidos._id=itensPedido.idPedido  " + where1 + " " + where2 , null);
            if (localCursor.moveToFirst()){
                total=localCursor.getDouble(localCursor.getColumnIndex("soma"));
            }
        }catch(Exception e){
            String message=e.getMessage();
        }
        db.close();
        localCursor.close();
        return total;
    }

    public Cursor consVendasPedDataGeral(SQLiteDatabase db,String datai,String dataf,String metcodigo){
        Cursor localCursor=null;
        daoMetasProd dmp = new daoMetasProd(ctx);
        String where1="";
        String where2="";
        if (!datai.equals("")&&!dataf.equals("")) {
            where1 = "where pedidos.data>='" + datai + "' and  pedidos.data<='" + dataf + "'  ";
            if (!metcodigo.equals("")) {
                where2 = "and "+dmp.listarMetasProd(datai, dataf);
            }
        }
        try {
            localCursor = db.rawQuery("select itensPedido._id,idPedido,data,itensPedido.codprduto,sum(itensPedido.quantidade*itensPedido.pesoliq) as soma from itensPedido inner join " +
                    "pedidos on pedidos._id=itensPedido.idPedido  " + where1 + " " + where2 + "  group by (codprduto)", null);
        }catch(Exception e){
            String message=e.getMessage();
        }
        return localCursor;
    }

    public double constotalpesoprod(SQLiteDatabase db,String datai,String dataf,String metcodigo){
        Cursor localCursor=null;
        daoMetasProd dmp = new daoMetasProd(ctx);
        String where1="";
        String where2="";
        if (!datai.equals("")&&!dataf.equals("")) {
            where1 = "where pedidos.data>='" + datai + "' and  pedidos.data<='" + dataf + "'  ";
            if (!metcodigo.equals("")) {
                String result=dmp.listarMetasProd(datai, dataf);
                if (!result.equals("")) {
                    where2 = "and " + result;
                }
            }
        }
        try {
            localCursor = db.rawQuery("select sum(itensPedido.quantidade*itensPedido.pesoliq) as soma from itensPedido inner join " +
                    "pedidos on pedidos._id=itensPedido.idPedido  " + where1 + " " + where2 , null);
        }catch(Exception e){
            String message=e.getMessage();
            return 0.00;
        }

        if (localCursor.moveToFirst()){
            return localCursor.getDouble(localCursor.getColumnIndex("soma"));
        }else{
            return 0.00;
        }
    }

}
