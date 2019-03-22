package br.com.mobiwork.mercador.pedido;


import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.*;

import br.com.mobiwork.mercador.dao.daoCreateDBM;
import br.com.mobiwork.mercador.R;


public class AplicaDescAcresPed extends Activity implements  View.OnClickListener {

    private EditText edTxValor;
    private SQLiteDatabase db;
    private Cursor cursorTbTipoDescAcresPromo;
    private SimpleCursorAdapter adapter;
    private Spinner tipodescacresped;
    private Integer idTipoDescAcresPed;
    private double valorDescAcres,percDescAcres, vrSubTotal;
    private boolean isSelection;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.setContentView(R.layout.aplicadescacresped);
        daoCreateDBM dao = new daoCreateDBM(this);
        db =  dao.getWritableDatabase();
        Intent i = getIntent();
        isSelection = false;
        idTipoDescAcresPed = i.getIntExtra("idTipoDescAcresPed",0);
        valorDescAcres = getIntent().getDoubleExtra("valorDescAcres",0);
        percDescAcres = getIntent().getDoubleExtra("percDescAcres",0);
        vrSubTotal = getIntent().getDoubleExtra("vrSubTotal",0);
        edTxValor = (EditText) findViewById (R.id.valor);
        View.OnTouchListener otl = new View.OnTouchListener() {
            public boolean onTouch (View v, MotionEvent event) {
                return true;
            }
        };
        edTxValor.setOnTouchListener(otl);
        if (idTipoDescAcresPed == 0){
            if (percDescAcres > 0){
                edTxValor.setText(String.valueOf(percDescAcres));
            }
            valorDescAcres = 0;
        }else {
            if (valorDescAcres > 0){
                edTxValor.setText(String.valueOf(valorDescAcres));
            }
            percDescAcres = 0;
        }
        tipodescacresped = (Spinner) findViewById(R.id.tipodescacrespromo);
        configBt();
        popularCombo();
    }

    public void popularCombo(){

        cursorTbTipoDescAcresPromo = db.rawQuery("SELECT _id, descricao FROM tbTipoDescAcresPed tb " , null);

        adapter = new SimpleCursorAdapter(this,R.layout.list_item,cursorTbTipoDescAcresPromo,new String[] {"descricao"},
                new int[] {R.id.comboName});

        tipodescacresped.setAdapter(adapter);
        tipodescacresped.setSelection(idTipoDescAcresPed);
        tipodescacresped.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent,
                                       View view, int position, long id) {
                if (isSelection){
                    valorDescAcres = 0;
                    percDescAcres = 0;
                    edTxValor.setText("");
                }
                isSelection = true;
            }

            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });
    }


    public void onClick(View v) {
        String keyInfo = (String)v.getTag();

        if (keyInfo.equals("back"))
            isBack();
        else if (keyInfo.equals("limpa"))
            edTxValor.setText("");
        else if (keyInfo.equals("cancel")){
            Intent result = new Intent(this, AplicaDescAcresPed.class);
            setResult(RESULT_CANCELED, result);
            AplicaDescAcresPed.this.finish();
        }
        else if (keyInfo.equals("done")){
            Intent result = new Intent(this, AplicaDescAcresPed.class);
            result.putExtra("valorDescAcres",valorDescAcres);
            result.putExtra("percDescAcres",percDescAcres);
            idTipoDescAcresPed = Integer.parseInt(String.valueOf(tipodescacresped.getSelectedItemId()));
            result.putExtra("idTipoDescAcresPed", idTipoDescAcresPed);
            setResult(RESULT_OK, result);
            AplicaDescAcresPed.this.finish();
        }
        else if (!keyInfo.equals("")){
            edTxValor.append(keyInfo);
            if (!edTxValor.getText().toString().equals("")){
                idTipoDescAcresPed = Integer.parseInt(String.valueOf(tipodescacresped.getSelectedItemId()));
                if (idTipoDescAcresPed == 0){
                    percDescAcres = Double.parseDouble(edTxValor.getText().toString());
                    valorDescAcres = 0;
                }else {
                    valorDescAcres = Double.parseDouble(edTxValor.getText().toString());
                    percDescAcres = 0;
                }
            }
        }

    }

    private void isBack() {
        CharSequence cc = edTxValor.getText();
        if (cc != null && cc.length() > 0)
        {
            edTxValor.setText("");
            edTxValor.append(cc.subSequence(0, cc.length() - 1));
        }
    }

    public void configBt(){
        Button bt = (Button) findViewById(R.id.x1);
        bt.setOnClickListener(this);
        bt = (Button) findViewById(R.id.x2);
        bt.setOnClickListener(this);
        bt = (Button) findViewById(R.id.x3);
        bt.setOnClickListener(this);
        bt = (Button) findViewById(R.id.x4);
        bt.setOnClickListener(this);
        bt = (Button) findViewById(R.id.x5);
        bt.setOnClickListener(this);
        bt = (Button) findViewById(R.id.x6);
        bt.setOnClickListener(this);
        bt = (Button) findViewById(R.id.x7);
        bt.setOnClickListener(this);
        bt = (Button) findViewById(R.id.x8);
        bt.setOnClickListener(this);
        bt = (Button) findViewById(R.id.x9);
        bt.setOnClickListener(this);
        bt = (Button) findViewById(R.id.x0);
        bt.setOnClickListener(this);
        bt = (Button) findViewById(R.id.xLimpa);
        bt.setOnClickListener(this);
        bt = (Button) findViewById(R.id.xDoneBt);
        bt.setOnClickListener(this);
        bt = (Button) findViewById(R.id.xCancelBt);
        bt.setOnClickListener(this);
        bt = (Button) findViewById(R.id.xPonto);
        bt.setOnClickListener(this);

    }
    @Override
    public void onBackPressed() {
        return;
    }

}