package br.com.mobiwork.mercador.util;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import br.com.mobiwork.mercador.R;


/**
 * Created by LuisGustavo on 18/05/2015.
 */
public class BaseAdapterCheck extends BaseAdapter {

    private Context context;
    private Cursor c,c2;
    private SQLiteDatabase db;
    DateFormat formatter;
    Date d ;
    private String _id;
    ArrayList<String> p= new ArrayList<String>();

    String sit,filtro;
    ListView le ;
    List<HashMap<String, String>> fillMaps = new ArrayList<HashMap<String, String>>();
   // ArrayList<String> checkedValue;
    List<HashMap<String, String>> checkedValue;

    public BaseAdapterCheck(Context context,  List<HashMap<String, String>> fillMaps2,ListView l,String filtror){
        this.context = context;
        //this.c = rotas;
        this.fillMaps=fillMaps2;
        formatter= new SimpleDateFormat("dd/MM/yyyy");
        d = new Date();
        le=l;
        this.filtro=filtror;
        checkedValue = new ArrayList<HashMap<String, String>>();

    }

    public List<HashMap<String, String>> getCheckedValue() {
        return checkedValue;
    }



    @Override
    public int getCount() {
        return fillMaps.size();
    }

    @Override
    public Object getItem(int position) {
        if(filtro.equalsIgnoreCase("restaure")){
            return position;
        }else{
            return this.p.get(position);

        }
    }


    @Override
    public long getItemId(int position) {
        return Long.parseLong(this.p.get(position));

    }

    public View getView(final int position, View convertView, ViewGroup parent) {


        View layout;
        Activity aa = (Activity)context;
        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.listcrereceberemi, null);
        }
        else{
            convertView = convertView;
        }

        boolean r=false;


            if (p.isEmpty()) {
                this._id = fillMaps.get(position).get("_id").substring(0,6)+fillMaps.get(position).get("_id").substring(8,9);
                this.p.add(position, _id);

            } else {
                if (position == p.size()) {
                    this._id = fillMaps.get(position).get("_id").substring(0,6)+fillMaps.get(position).get("_id").substring(8,9);
                    this.p.add(position, _id);
                    ;
                }
            }



        final TextView vencimento = (TextView) convertView.findViewById(R.id.lbemissao);
        vencimento.setText(fillMaps.get(position).get("emissao2"));

        final TextView modelo = (TextView) convertView.findViewById(R.id.valor);
        modelo.setText(fillMaps.get(position).get("valor"));

        TextView data = (TextView) convertView.findViewById(R.id.vlrreceb);
        data.setText(fillMaps.get(position).get("vlrreceb"));


        final CheckBox cb = (CheckBox) convertView.findViewById(R.id.checkBox1);
        cb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (cb.isChecked()) {
                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put("_id", p.get(position));
                    map.put("valor", modelo.getText().toString());
                    checkedValue.add(map);
                }else {
                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put("_id", p.get(position));
                    map.put("valor", modelo.getText().toString());
                    checkedValue.remove(map);
                }

            }
        });
        boolean check=false;
        for (int i=0;i<checkedValue.size();i++){
            if(p.get(position).equalsIgnoreCase(checkedValue.get(i).get("_id"))){
                check=true;
            }
        }
        cb.setChecked(check);




        return convertView;
    }

}
