package com.example.concesionario_sabado;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.database.sqlite.SQLiteDatabase;
import android.database.Cursor;

public class VentaActivity extends AppCompatActivity {

    EditText jetcodigo, jetidentificacion, jetplaca, jetfecha;
    TextView jtvnombre, jtvmarca, jtvmodelo;
    CheckBox jcbactivar;
    String identificacion, codigo, nombre, placa, modelo, marca, fecha;
    ClsOpenHelper admin=new ClsOpenHelper(this,"concecionario.db",null,1);
    long respuesta, respuesta2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_venta);
        //Ocultar la barra de titulo por defecto y asociar objetos Java con XML
        getSupportActionBar().hide();
        jetcodigo=findViewById(R.id.etcodigo);
        jetidentificacion=findViewById(R.id.etidentificacion);
        jetplaca=findViewById(R.id.etplaca);
        jetfecha=findViewById(R.id.etfecha);
        jtvmarca=findViewById(R.id.tvmarca);
        jtvnombre=findViewById(R.id.tvnombre);
        jtvmodelo=findViewById(R.id.tvmodelo);
        jcbactivar=findViewById(R.id.cbactivarv);
    }
    public void GuardarVe(View view) {
        codigo = jetcodigo.getText().toString();
        fecha = jetfecha.getText().toString();
        identificacion = jetidentificacion.getText().toString();
        nombre = jtvnombre.getText().toString();
        placa = jetplaca.getText().toString();
        modelo = jtvmodelo.getText().toString();
        marca = jtvmarca.getText().toString();
        if (identificacion.isEmpty() || nombre.isEmpty() || codigo.isEmpty() || fecha.isEmpty() || placa.isEmpty() || modelo.isEmpty() || marca.isEmpty()) {
            Toast.makeText(this, "Todos los datos son requeridos", Toast.LENGTH_SHORT).show();
            jetcodigo.requestFocus();
        } else {
            SQLiteDatabase db = admin.getReadableDatabase();
            Cursor buscarPlaca = db.rawQuery("SELECT activo FROM TblVehiculo WHERE placa = '" + placa + "'", null);
            if (buscarPlaca.moveToFirst()) {
                if (buscarPlaca.getString(0).equals("Si")) {
                    SQLiteDatabase dbW = admin.getWritableDatabase();
                    Cursor fila = dbW.rawQuery("SELECT * FROM TblVenta WHERE codigo = '" + codigo + "'", null);
                    if(!fila.moveToFirst()){
                        ContentValues registro = new ContentValues();
                        ContentValues registro2 = new ContentValues();
                        registro.put("codigo",codigo);
                        registro.put("fecha",fecha);
                        registro.put("identificacion",identificacion);
                        registro.put("placa",placa);
                        registro2.put("activo","No");
                        respuesta = dbW.insert("TblVenta", null, registro);
                        respuesta2 = dbW.update("TblVehiculo",registro2,"placa = '" + placa + "'",null);
                    }
                    else{
                        Toast.makeText(this, "El codigo de venta ya existe", Toast.LENGTH_SHORT).show();
                        respuesta = 0;
                    }

                    if (respuesta != 0){

                        Toast.makeText(this, "Registro guardado exitosamente ", Toast.LENGTH_SHORT).show();
                        LimpiarCampos();
                    }
                    dbW.close();
                }else{
                    Toast.makeText(this, "El vehiculo no esta disponible", Toast.LENGTH_SHORT).show();
                }

            }else{
                Toast.makeText(this, "El vehiculo no existe", Toast.LENGTH_SHORT).show();
            }
            db.close();
        }
    }
    public void ConsultarC(View view) {
        identificacion = jetidentificacion.getText().toString();
        if (!identificacion.isEmpty()){
            SQLiteDatabase db = admin.getReadableDatabase();
            Cursor fila = db.rawQuery("SELECT * FROM TblCliente WHERE identificacion = '" + identificacion + "'",null);
            if(fila.moveToFirst()){
                jtvnombre.setText(fila.getString(1));
            }
            db.close();
        }
        else{
            Toast.makeText(this, "Identificación es requerida para consulta", Toast.LENGTH_SHORT).show();
            jetidentificacion.requestFocus();
        }
    }
    public void ConsultarVh(View view) {
        placa = jetplaca.getText().toString();
        if (!placa.isEmpty()){
            SQLiteDatabase db = admin.getReadableDatabase();
            Cursor fila = db.rawQuery("SELECT * FROM TblVehiculo WHERE placa = '" + placa + "'",null);
            if(fila.moveToFirst()){
                jtvmodelo.setText(fila.getString(1));
                jtvmarca.setText(fila.getString(2));
            }
            else{
                Toast.makeText(this, "Vehiculo no hallado", Toast.LENGTH_SHORT).show();
            }
            db.close();
        }
        else{
            Toast.makeText(this, "Placa es requerida para consulta", Toast.LENGTH_SHORT).show();
            jetplaca.requestFocus();
        }
    }
    public void ConsultarVe(View view) {
        codigo = jetcodigo.getText().toString();
        if (!codigo.isEmpty()){
            SQLiteDatabase db = admin.getReadableDatabase();
            Cursor fila = db.rawQuery("SELECT TV.fecha, TV.activo, TC.identificacion, TC.nombre, TVH.placa, TVH.modelo, TVH.marca, TVH.activo FROM TblVenta TV INNER JOIN TblCliente TC ON TV.identificacion = TC.identificacion INNER JOIN TblVehiculo TVH ON TV.placa = TVH.placa WHERE TV.codigo = '" + codigo + "'",null);
            if(fila.moveToFirst()){
                jetfecha.setText(fila.getString(0));
                jetidentificacion.setText(fila.getString(2));
                jtvnombre.setText(fila.getString(3));
                jetplaca.setText(fila.getString(4));
                jtvmodelo.setText(fila.getString(5));
                jtvmarca.setText(fila.getString(6));
                if(fila.getString(1).equals("Si")){
                    jcbactivar.setChecked(true);
                }
                else {
                    jcbactivar.setChecked(false);
                }
            }
            else{
                Toast.makeText(this, "La venta no existe", Toast.LENGTH_SHORT).show();
            }
            db.close();
        }
        else{
            Toast.makeText(this, "El codigo es requerido para consulta", Toast.LENGTH_SHORT).show();
            jetcodigo.requestFocus();
        }
    }
    public void AnularVe(View view){
        codigo = jetcodigo.getText().toString();
        if(!codigo.isEmpty()){
            SQLiteDatabase db = admin.getReadableDatabase();
            Cursor plateSearch = db.rawQuery("SELECT * FROM TblVenta WHERE codigo = '" + codigo +"'",null);
            if(plateSearch.moveToFirst()){
                placa = plateSearch.getString(3);
                SQLiteDatabase dbw = admin.getWritableDatabase();
                ContentValues registroVenta = new ContentValues();
                ContentValues registroVehiculo = new ContentValues();
                registroVehiculo.put("activo","Si");
                registroVenta.put("activo","No");
                respuesta = dbw.update("TblVehiculo",registroVehiculo,"placa='" + placa + "'",null);
                respuesta2 = dbw.update("TblVenta",registroVenta,"codigo = '" + codigo + "'", null);
                dbw.close();
            }else{
                respuesta = 0;
                Toast.makeText(this, "El codigo de la venta no existe", Toast.LENGTH_SHORT).show();
            }

            if(respuesta > 0){
                Toast.makeText(this, "Registro anulado", Toast.LENGTH_SHORT).show();
                LimpiarCampos();
            }else{
                Toast.makeText(this, "Error anulando registro", Toast.LENGTH_SHORT).show();
            }

        }
        else{
            Toast.makeText(this, "Debe de ingresar el codigo de la venta", Toast.LENGTH_SHORT).show();
            jetcodigo.requestFocus();
        }
    }
    public void ActivarVe(View view){
        codigo = jetcodigo.getText().toString();
        if(!codigo.isEmpty()){
            SQLiteDatabase db = admin.getReadableDatabase();
            Cursor plateSearch = db.rawQuery("SELECT * FROM TblVenta WHERE codigo = '" + codigo +"'",null);
            if(plateSearch.moveToFirst()){
                placa = plateSearch.getString(3);
                SQLiteDatabase dbW = admin.getWritableDatabase();
                ContentValues registroVenta = new ContentValues();
                ContentValues registroVehiculo = new ContentValues();
                registroVehiculo.put("activo","No");
                registroVenta.put("activo","Si");
                respuesta = dbW.update("TblVehiculo",registroVehiculo,"placa='" + placa + "'",null);
                respuesta2 = dbW.update("TblVenta",registroVenta,"codigo = '" + codigo + "'", null);
                dbW.close();
            }else{
                respuesta = 0;
                Toast.makeText(this, "El codigo de la venta no existe", Toast.LENGTH_SHORT).show();
            }

            if(respuesta > 0){
                Toast.makeText(this, "Registro activad o", Toast.LENGTH_SHORT).show();
                LimpiarCampos();
            }else{
                Toast.makeText(this, "Error anulando registro", Toast.LENGTH_SHORT).show();
            }

        }
        else{
            Toast.makeText(this, "Debe de ingresar el codigo de la venta", Toast.LENGTH_SHORT).show();
            jetcodigo.requestFocus();
        }
    }
    public void Main(View View){
        Intent intMain = new Intent(this,MainActivity.class);
        startActivity(intMain);
    }
    public void CancelarVenta(View view){
        LimpiarCampos();
    }

    private void LimpiarCampos(){
        jetidentificacion.setText("");
        jetcodigo.setText("");
        jtvnombre.setText("");
        jtvmarca.setText("");
        jetplaca.setText("");
        jtvmodelo.setText("");
        jetfecha.setText("");
        jcbactivar.setChecked(false);
        jetcodigo.requestFocus();
    }
}
