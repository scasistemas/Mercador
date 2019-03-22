package br.com.mobiwork.mercador.util;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import br.com.mobiwork.mercador.R;
import br.com.mobiwork.mercador.dao.daoItemPedido;
import br.com.mobiwork.mercador.dao.daoMetasProd;
import br.com.mobiwork.mercador.dao.daoProdutos;

/**
 * Created by luisgustavo on 10/11/2017.
 */
public class BaseAdapterCursorMetasProd extends BaseAdapter {

    private Context context;
    private Cursor c;
    private int _id;
    private int tipo;
    ArrayList<Integer> p= new ArrayList<Integer>();
    private daoProdutos dp;
    private daoMetasProd dmp;
    private String datai,dataf;
    private daoItemPedido dip;
    String metcodigo;


    public BaseAdapterCursorMetasProd(Context context, Cursor produtos,String datai,String dataf,String metcodigo,int tipo){
        this.context = context;
        this.c = produtos;
        this.dp = new daoProdutos(context);
        this.dmp= new daoMetasProd(context);
        this.dip= new daoItemPedido(context);
        this.datai=datai;
        this.dataf=dataf;
        this.metcodigo=metcodigo;
        this.tipo=tipo;

    }


    @Override
    public int getCount() {
        if(c!=null) {
            return c.getCount();
        }else{
            return 0;
        }
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_itempesoprod, null);
        }
        if (c!=null) {
            TextView meta = (TextView) convertView.findViewById(R.id.lb_meta);
            TextView dif = (TextView) convertView.findViewById(R.id.lb_dif);
            TextView lb_titdif =(TextView) convertView.findViewById(R.id.lb_titdif);
            TextView lb_titmeta=(TextView) convertView.findViewById(R.id.lb_titmeta);
            TextView produto = (TextView) convertView.findViewById(R.id.lb_produto);
            TextView peso = (TextView) convertView.findViewById(R.id.lb_peso_venda);

            if (tipo>=1) {
                this.c.moveToPosition(position);
                this._id = Integer.parseInt(c.getString(c.getColumnIndex("metas_prod._id")));

                if (p.isEmpty()) {
                    this.p.add(position, _id);
                } else {
                    if (position == p.size()) {
                        this.p.add(position, _id);
                    }
                }

                String descriproduto = dp.consultarProdutopCod(c.getString(c.getColumnIndex("metproduto")));
                String codprduto = c.getString(c.getColumnIndex("metproduto"));
                produto.setText(c.getString(c.getColumnIndex("metproduto")) + "-" + descriproduto);

                double pedidos_tot = BigDecimalRound.Round(dip.consVendasPedData(datai, dataf, codprduto), 2);
                peso.setText(String.valueOf(pedidos_tot));

                if (tipo==1) {
                    double metar = dmp.selMetaProd(codprduto, metcodigo);
                    if (metar > 0 && !metcodigo.equals("")) {
                        meta.setText(String.valueOf(metar));
                        dif.setText(String.valueOf(BigDecimalRound.Round(pedidos_tot - metar, 2)));
                    } else {
                        meta.setText("Ñ definida");
                    }
                }else{
                    meta.setVisibility(View.GONE);
                    dif.setVisibility(View.GONE);
                    lb_titdif.setVisibility(View.GONE);
                    lb_titmeta.setVisibility(View.GONE);
                }
            }else{
                this.c.moveToPosition(position);
                this._id = Integer.parseInt(c.getString(c.getColumnIndex("itensPedido._id")));

                if (p.isEmpty()) {
                    this.p.add(position, _id);
                } else {
                    if (position == p.size()) {
                        this.p.add(position, _id);
                    }
                }

                String descriproduto = dp.consultarProdutopCod(c.getString(c.getColumnIndex("codPrduto")));
                String codprduto = c.getString(c.getColumnIndex("codPrduto"));
                produto.setText(c.getString(c.getColumnIndex("codPrduto")) + "-" + descriproduto);

                double peso_ped=c.getDouble(c.getColumnIndex("soma"));
                peso.setText(String.valueOf(BigDecimalRound.Round(peso_ped,2)));

                double metar = dmp.selMetaProd(codprduto, metcodigo);

                if (metar > 0 && !metcodigo.equals("")) {
                    meta.setText(String.valueOf(metar));
                    dif.setText(String.valueOf(c.getDouble(c.getColumnIndex("soma"))-metar));
                } else {
                    meta.setText("Ñ definida");
                }
            }
        }
        return convertView;
    }
}
