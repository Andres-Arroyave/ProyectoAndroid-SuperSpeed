package com.example.concesionario_sabado;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class VehiculoActivity extends AppCompatActivity {

    EditText jetplaca, jetmodelo, jetmarca;
    CheckBox jcbactivov;
    String placa, modelo, marca;

    ClsOpenHelper admin=new ClsOpenHelper(this,"concecionario.db",null,1);
    long respuesta;
    byte sw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehiculo);
        //Ocultar la barra de titulo por defecto y asociar objetos Java con XML
        getSupportActionBar().hide();
        jetplaca=findViewById(R.id.etplaca);
        jetmodelo=findViewById(R.id.etmodelo);
        jetmarca=findViewById(R.id.etmarca);
        jcbactivov=findViewById(R.id.cbactivov);
        sw=0;
    }

    public void GuardarV(View view){
        placa=jetplaca.getText().toString();
        modelo=jetmodelo.getText().toString();
        marca=jetmarca.getText().toString();
        if (placa.isEmpty() || modelo.isEmpty() || marca.isEmpty())
        {
            Toast.makeText(this, "Todos los datos son requeridos", Toast.LENGTH_SHORT).show();
            jetplaca.requestFocus();
        }
        else
        {
            SQLiteDatabase db=admin.getWritableDatabase();
            ContentValues registro=new ContentValues();
            registro.put("placa",placa);
            registro.put("modelo",modelo);
            registro.put("marca",marca);
            if  (sw==0)
                respuesta=db.insert("TblVehiculo",null,registro);
            else {
                respuesta = db.update("tblvehiculo", registro, "placa='" + placa + "'", null);
                sw=0;
            }
            if(respuesta==0)
            {
                Toast.makeText(this, "Error guardando registro", Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(this, "El registro fue guardado", Toast.LENGTH_SHORT).show();
                limpiar_campos();
            }
            db.close();
        }
    }//Fin de de metodo guardar

    public void ConsultarV(View view) {
        //Validando que se haya digitado una placa
        placa=jetplaca.getText().toString();
        if(!placa.isEmpty()){
            SQLiteDatabase db=admin.getReadableDatabase();
            Cursor fila=db.rawQuery(" select * from TblVehiculo where placa='"+placa+"'",null);
            if(fila.moveToNext()){
                sw=1;
                jetmodelo.setText(fila.getString(1));
                jetmarca.setText(fila.getString(2));
                if (fila.getString(3).equals("Si"))
                    jcbactivov.setChecked(true);
                else
                    jcbactivov.setChecked(false);
            }else{
                Toast.makeText(this, "Registro no hallado", Toast.LENGTH_SHORT).show();
            }
            db.close();
        }
        else{
            Toast.makeText(this, "La placa es requerida para consultar", Toast.LENGTH_SHORT).show();
            jetplaca.requestFocus();
        }
    }

    public void CancelarV(View view){
        limpiar_campos();
    }

    private void limpiar_campos()
    {
        jetplaca.setText("");
        jetmodelo.setText("");
        jetmarca.setText("");
        jcbactivov.setChecked(false);
        jetplaca.requestFocus();
        sw=0;

    }
    public void AnularV(View view){
        if (sw == 1){
            sw=0;
            SQLiteDatabase db=admin.getWritableDatabase();
            ContentValues registro=new ContentValues();
            registro.put("activo","No");
            respuesta=db.update("TblVehiculo",registro,"placa='"+placa+"'",null);
            if (respuesta>0)
            {
                Toast.makeText(this, "Registro anulado", Toast.LENGTH_SHORT).show();
                limpiar_campos();
            }
            else
            {
                Toast.makeText(this, "Error anulando registro", Toast.LENGTH_SHORT).show();
            }

            db.close();

        }
        else{
            Toast.makeText(this, "Primero debe consultar", Toast.LENGTH_SHORT).show();
            jetplaca.requestFocus();
        }
    }

    public void Regresar(View view){
        Intent intmain=new Intent(this,MainActivity.class);
        startActivity(intmain);
    }

    public void ActivarV(View view){
        if (sw == 1){
            sw=0;
            SQLiteDatabase db=admin.getWritableDatabase();
            ContentValues registro=new ContentValues();
            registro.put("activo","Si");
            respuesta=db.update("TblVehiculo",registro,"placa='"+placa+"'",null);
            if (respuesta>0)
            {
                Toast.makeText(this, "Registro activado", Toast.LENGTH_SHORT).show();
                jcbactivov.setChecked(true);
            }
            else
            {
                Toast.makeText(this, "Error activando registro", Toast.LENGTH_SHORT).show();
            }
            db.close();
        }
        else{
            Toast.makeText(this, "Primero debe consultar", Toast.LENGTH_SHORT).show();
            jetplaca.requestFocus();
        }
    }
}