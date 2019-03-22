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

/**
 * Created by LuisGustavo on 26/05/14.
 */
public class BaseAdapterProdutos extends BaseAdapter {

    private Context context;
    private Cursor c;
    private int _id;
    ArrayList<Integer> p= new ArrayList<Integer>();

    public BaseAdapterProdutos(Context context, Cursor produtos){
        this.context = context;
        this.c = produtos;
    }


    @Override
    public int getCount() {
        return c.getCount();
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
            convertView = inflater.inflate(R.layout.listaproduto, null);
        }

        this.c.moveToPosition(position);
        this._id=Integer.parseInt(c.getString(c.getColumnIndex("fazenda._id")));

        if(p.isEmpty()){
            this.p.add(position, _id);
        }else{
            if(position==p.size()){
                this.p.add(position,_id);
            }
        }

        TextView codigo = (TextView) convertView.findViewById(R.id.coditem);
        codigo.setText(c.getString(c.getColumnIndex("codPrduto")));

        TextView marca = (TextView) convertView.findViewById(R.id.descricaoItem);
        marca.setText(c.getString(c.getColumnIndex("descricao")));

        if(c.getInt(c.getColumnIndex("m_nota"))==1){
            convertView.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.button_shape_blue));
        }else{
            convertView.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.button_shape_black));
        }
        return convertView;
    }
}
