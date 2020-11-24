package com.spacehowen.binchecker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    private final static String Url ="https://lookup.binlist.net/";
    EditText NumberT; //numero ingresado
    Button BtnBuscar ; //
    TextView Sistema,Tipo,Marca,Pais,Moneda,Banco,Bandera;
    String numero;
    ProgressBar Cargando;
    CardView CardConte;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //ids
        CardConte = findViewById(R.id.card1);
        NumberT =  findViewById(R.id.txtNumber); //numero de tarjeta a ingresar
        BtnBuscar =  findViewById(R.id.btnBuscar); //boton para buscar
        Sistema =  findViewById(R.id.txtSistema); //Sistema de pago
        Tipo =  findViewById(R.id.txttipo); // Tipo de tarj Credito - debido
        Marca =  findViewById(R.id.txtMarca); // Marca comercial
        Pais =  findViewById(R.id.txtPais); // Pais
        Moneda =  findViewById(R.id.txtMoneda); // Moneda
        Bandera = findViewById(R.id.txtBandera); //Bandera
        Banco =  findViewById(R.id.txtBanco);
        Cargando =  findViewById(R.id.ProgressTarjeta); //Progress bar
        //Este boton busca la tarjeta
        BtnBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(NumberT.getText().toString().length()>5){
                    numero=NumberT.getText().toString();
                    Limpiar();
                    Buscar ObjBuscar = new Buscar();
                    ObjBuscar.execute(NumberT.getText().toString().trim());
                }else{
                    Toast.makeText(MainActivity.this, "Ingrese minimo 06 digitos", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    //
    private void Limpiar() {
        Sistema.setText("");
        Tipo.setText("");
        Marca.setText("");
        Pais.setText("");
        Moneda.setText("");
        Bandera.setText("");
        Banco.setText("");
    }
    //btns main
    public void btnGithub(View view) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/spacehowen/bin-checker"));
        startActivity(intent);
    }

    public void btnWeb(View view) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://spacehowen.com/"));
        startActivity(intent);
    }

    //
    class Buscar extends AsyncTask<String,Void,String> {
        protected void onPreExecute() {
            super.onPreExecute();
            Cargando.setVisibility(View.VISIBLE);
            CardConte.setVisibility(View.INVISIBLE);
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(NumberT.getWindowToken(), 0);
        }
        protected String doInBackground(String... params) {
            HashMap<String,String> data = new HashMap<>();
            data.put("number",params[0]);
            clsSearch ObjSearch = new clsSearch();
            String result = ObjSearch.sendPostRequest(Url+numero,data);
            return result;
        }
        protected void onPostExecute(String Resultado) {
            super.onPostExecute(Resultado);
            if (Resultado.equals("404")){
                Toast.makeText(MainActivity.this, "El Número ingresado no existe", Toast.LENGTH_SHORT).show();
                Cargando.setVisibility(View.INVISIBLE);
            }else{
                String Json = Resultado;
                JSONObject reader = null;
                JSONObject ReadCountry = null;
                JSONObject ReadBank = null;
                try {
                    reader = new JSONObject(Json);
                    Sistema.setText(reader.getString("scheme"));
                    Tipo.setText(reader.getString("type"));
                    Marca.setText(reader.getString("brand"));

                    String Country =  reader.getString("country"); //
                    //Lectura del objeto Country
                    ReadCountry = new JSONObject((Country));

                    Pais.setText(ReadCountry.getString("name")); //obtiene el nombre del pais
                    Moneda.setText(ReadCountry.getString("currency")); //obtiene el nombre del pais
                    Bandera.setText( ReadCountry.getString("emoji")); //obtiene la bandera  del pais

                    //nombre del banco
                    if (TextUtils.isEmpty(reader.getString("bank"))){
                        Banco.setText("SIN INFO");
                    }else{
                        String bank =  reader.getString("bank"); //
                        ReadBank = new JSONObject((bank));
                        Banco.setText(ReadBank.getString("name"));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Cargando.setVisibility(View.INVISIBLE);
                }
                Cargando.setVisibility(View.INVISIBLE);
                CardConte.setVisibility(View.VISIBLE);
            }
        }
    }
}