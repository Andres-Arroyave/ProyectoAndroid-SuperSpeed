package com.example.concesionario_sabado;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.annotation.Nullable;

public class ClsOpenHelper extends SQLiteOpenHelper {
    public ClsOpenHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table TblCliente(identificacion" +
        " text primary key,nombre text not null,correo " +
        "text not null,activo text default 'Si')");
        db.execSQL("create table TblVehiculo(placa text " +
        "primary key,modelo text not null,marca text " +
        "not null,activo text default 'Si')");
        db.execSQL("create table TblVenta(codigo text " +
        "primary key,fecha text not null,identificacion " +
        "text not null,placa text not null,activo " +
        "text default 'Si',constraint pk_venta foreign key " +
        "(identificacion) references TbLCliente(identificacion)," +
        "foreign key(placa) references TblVehiculo(placa))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE TblCliente");
        db.execSQL("DROP TABLE TblVehiculo");
        db.execSQL("DROP TABLE TblVenta");
        {
            onCreate(db);
        }
    }
}
