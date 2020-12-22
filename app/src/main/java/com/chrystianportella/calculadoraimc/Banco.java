package com.chrystianportella.calculadoraimc;

import android.annotation.TargetApi;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Banco {
    private static final String BANCO = "banco.db";
    private static final String TABELA = "imc";
    private static final String ALTURA = "altura";
    private static final String PESO = "peso";
    private static final String DATA = "data";
    SQLiteDatabase _db;
    Context _ctx;

    public Banco() {
    }

    public Banco(Context ctx) {
        _ctx = ctx;
        CriarBanco();
    }

    public void CriarBanco() {
        _db = _ctx.openOrCreateDatabase(BANCO, Context.MODE_PRIVATE, null);
        _db.execSQL("CREATE TABLE IF NOT EXISTS imc(ID INTEGER PRIMARY KEY AUTOINCREMENT, " + ALTURA + " REAL, " + PESO + " REAL, " + DATA + " TEXT);");
    }


    public boolean InsertSQL(double peso, double altura) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        Calendar calendar = Calendar.getInstance();
        String data = dateFormat.format(calendar.getTime());
        try {
            _db = _ctx.openOrCreateDatabase(BANCO, Context.MODE_PRIVATE, null);
            _db.execSQL("INSERT INTO " + TABELA + " (" + ALTURA + "," + PESO + "," + DATA + ") VALUES (" + altura + "," + peso + ", '" + data + "') ");
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public ArrayList<IMC> getAll() {
        ArrayList<IMC> list = new ArrayList<IMC>();
        Cursor res = _db.rawQuery("SELECT * FROM " + TABELA, new String[]{});
        res.moveToFirst();
        while (res.isAfterLast() == false) {
            IMC imc = new IMC();
            imc.Altura = Double.parseDouble(res.getString(res.getColumnIndex(ALTURA)));
            imc.Peso = Double.parseDouble(res.getString(res.getColumnIndex(PESO)));
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
            Calendar calendar = Calendar.getInstance();
            String data= res.getString(res.getColumnIndex(DATA));
            try {
                Date d = dateFormat.parse(data);
                calendar.setTime(d);
                imc.Data = calendar;
                list.add(imc);
            }catch (Exception e){
                return list;
            }
            res.moveToNext();
        }
        return list;
    }
}
