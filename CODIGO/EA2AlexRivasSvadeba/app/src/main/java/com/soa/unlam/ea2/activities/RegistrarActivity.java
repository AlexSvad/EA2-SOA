package com.soa.unlam.ea2.activities;
import android.content.Context;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.soa.unlam.R;
import com.soa.unlam.ea2.api.EventRequest;
import com.soa.unlam.ea2.api.EventResponse;
import com.soa.unlam.ea2.api.RetrofitInterface;
import com.soa.unlam.ea2.api.RegistrarRequest;
import com.soa.unlam.ea2.api.RegistrarResponse;
import java.io.IOException;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RegistrarActivity extends AppCompatActivity {

    private Integer idThread=0;
    private EditText _et_nombre,_et_apellido,_et_dni,_et_email,_et_password,_et_comision;
    ProgressBar progressBar1;
    Button btn_ir_inicioSesion,crear_cuenta;
    private  String tokenRegistrar;
    private static final String TAG = "RegistrarActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrarse);

        _et_nombre = findViewById(R.id.et_nombre);
        _et_apellido = findViewById(R.id.et_apellido);
        _et_dni = findViewById(R.id.et_dni);
        _et_email = findViewById(R.id.et_email);
        _et_password = findViewById(R.id.et_password);
        _et_comision = findViewById(R.id.et_comision);

        progressBar1 = findViewById(R.id.progressBar1);

        btn_ir_inicioSesion=findViewById(R.id.btn_ir_inicioSesion);
        btn_ir_inicioSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegistrarActivity.this, LoginActivity.class));
                finish();
            }
        });

        crear_cuenta=findViewById(R.id.crear_cuenta);
        crear_cuenta.setOnClickListener(view -> ejecutarThread());
    }

    private void  ejecutarThread() {
        ThreadAsynctask hilo=new ThreadAsynctask();
        hilo.execute("id Thread");
    }
    private class ThreadAsynctask extends AsyncTask<String, Integer, String>{
        @Override
        protected void onPreExecute() {
            progressBar1.setVisibility(View.VISIBLE);
            crear_cuenta.setEnabled(false);

            if(!hayInternet()) Toast.makeText(getApplicationContext(),"No estás conectado a internet",Toast.LENGTH_SHORT).show();

            String _nombre = _et_nombre.getText().toString().trim();
            String _apellido = _et_apellido.getText().toString().trim();
            String _dni = _et_dni.getText().toString().trim();
            String _email = _et_email.getText().toString().trim();
            String _password = _et_password.getText().toString().trim();
            String _comision = _et_comision.getText().toString().trim();

            if (_nombre.isEmpty()){
                _et_nombre.setError("El nombre es obligatorio");
                _et_nombre.requestFocus();
                return;
            }else if (_apellido.isEmpty()){
                _et_apellido.setError("El apellido es obligatorio");
                _et_apellido.requestFocus();
                return;
            }else if (_dni.isEmpty()){
                _et_dni.setError("El DNI es obligatorio");
                _et_dni.requestFocus();
                return;
            }else if (_email.isEmpty()){
                _et_email.setError("El correo electrónico es obligatorio");
                _et_email.requestFocus();
                return;
            } else if (!_email.contains("@")){
                _et_email.setError("El correo electrónico debe ser válido");
                _et_email.requestFocus();
                return;
            }else if (_password.isEmpty()){
                _et_password.setError("La contraseña es obligatoria");
                _et_password.requestFocus();
                return;
            }else if (_password.length()<8){
                _et_password.setError("La contraseña debe ser de 8 caracteres como mínimo");
                _et_password.requestFocus();
                return;
            }else if (_comision.isEmpty()){
                _et_comision.setError("La contraseña debe ser de 8 caracteres como mínimo");
                _et_comision.requestFocus();
                return;
            }
        }

        @Override
        protected String doInBackground(String... params) {
            idThread++;
            registrarse();
            String mensaje =params[0]+idThread;
            return mensaje;
        }

        @Override
        protected void onPostExecute(String result) {
            progressBar1.setVisibility(View.INVISIBLE);
            crear_cuenta.setEnabled(true);
        }

        @Override
        protected void onProgressUpdate(Integer ...values) {
            //Toast.makeText(getApplicationContext(),"Ejecutando Thread Numero:"+values[0].toString(),Toast.LENGTH_SHORT).show();
        }
    }

    private void registrarse() {

        if(!hayInternet()) return;

        String _nombre = _et_nombre.getText().toString().trim();
        String _apellido = _et_apellido.getText().toString().trim();
        String _dni = _et_dni.getText().toString().trim();
        String _email = _et_email.getText().toString().trim();
        String _password = _et_password.getText().toString().trim();
        String _comision = _et_comision.getText().toString().trim();

        if (_nombre.isEmpty()){
            return;
        }else if (_apellido.isEmpty()){
            return;
        }else if (_dni.isEmpty()){
            return;
        }else if (_email.isEmpty()){
            return;
        } else if (!_email.contains("@")){
            return;
        }else if (_password.isEmpty()){
            return;
        }else if (_password.length()<8){
            return;
        }else if (_comision.isEmpty()){
            return;
        } else {

            RegistrarRequest request = new RegistrarRequest();
            request.setEnv("PROD");
            request.setName(_nombre);
            request.setLastname(_apellido);
            request.setDni(Long.parseLong(_dni));
            request.setEmail(_email);
            request.setPassword(_password);
            request.setCommission(Long.parseLong(_comision));

            Retrofit retrofit = new Retrofit.Builder()
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(getString(R.string.retrofit_server))
                    .build();

            RetrofitInterface retrofitInterface = retrofit.create(RetrofitInterface.class);

            Call<RegistrarResponse> call = retrofitInterface.register(request);
            call.enqueue(new Callback<RegistrarResponse>() {
                @Override
                public void onResponse(Call<RegistrarResponse> call, Response<RegistrarResponse> response) {
                    if(response.isSuccessful()){
                        tokenRegistrar = response.body().getToken();
                        registrarEvento();
                        Toast.makeText(RegistrarActivity.this,"Te registraste "+_nombre+" "+_apellido+"con el DNI " +_dni+" y el email "+_email+" en la comisión "+_comision, Toast.LENGTH_LONG).show();
                        Intent i = new Intent(RegistrarActivity.this, SensoresActivity.class);
                        i.putExtra("tokenUsuario", tokenRegistrar);
                        startActivity(i);
                    }else {
                        int code = response.code();
                        String cad = "ERROR";
                        try {
                            cad = response.errorBody().string();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        if (code == 401) {
                                Toast.makeText(getApplicationContext(),"ERROR:"+code+" Prueba con otro correo electrónico",Toast.LENGTH_LONG).show();
                            } else if (code >= 400 && code < 500) {
                                Toast.makeText(getApplicationContext(),"ERROR:"+code+" "+cad.substring(37,cad.lastIndexOf('"')),Toast.LENGTH_LONG).show();
                        } else if (code >= 500 && code < 600) {
                                Toast.makeText(getApplicationContext(),"ERROR:"+code+" Error en el servidor",Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getApplicationContext(),"ERROR:"+code,Toast.LENGTH_SHORT).show();
                            }
                        Log.e(TAG,cad);
                    }
                }
                @Override
                public void onFailure(Call<RegistrarResponse> call, Throwable t) {
                    Toast.makeText(RegistrarActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                    Log.e(TAG,t.getMessage());
                }
            });
        }
    }

    void registrarEvento()
    {
        EventRequest request = new EventRequest();
        request.setEnv("PROD");
        request.setType_events("RegistroExitoso");
        request.setDescription("Se registró correctamente el usuario");

        Retrofit retrofit  = new Retrofit.Builder()
                .baseUrl(getString(R.string.retrofit_server))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RetrofitInterface apiInterface = retrofit.create(RetrofitInterface.class);
        Call<EventResponse> call = apiInterface.regEvent(request, "Bearer "+tokenRegistrar);
        call.enqueue(new Callback<EventResponse>() {
            @Override
            public void onResponse(Call<EventResponse> call, Response<EventResponse> response) {
                if(response.isSuccessful()){
                    Toast.makeText(RegistrarActivity.this, "Se registró correctamente el evento: "+response.body().getEvent().getType_events(), Toast.LENGTH_LONG).show();
                    Log.e(TAG,"Success:"+response.body().getSuccess()+"\nEnv:"+response.body().getEnv()+"\nType Events:"+response.body().getEvent().getType_events()+
                            "\nDescription:"+response.body().getEvent().getDescription());
                }else {
                    int code = response.code();
                    String cad = "ERROR";
                    try {
                        cad = response.errorBody().string();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (code == 401) {
                        Toast.makeText(getApplicationContext(),"ERROR:"+code+" No se pudo registrar el evento",Toast.LENGTH_LONG).show();
                    } else if (code >= 400 && code < 500) {
                        Toast.makeText(getApplicationContext(),"ERROR:"+code+" "+cad.substring(37,cad.lastIndexOf('"')),Toast.LENGTH_LONG).show();
                    } else if (code >= 500 && code < 600) {
                        Toast.makeText(getApplicationContext(),"ERROR:"+code+" Error en el servidor",Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(),"ERROR:"+code,Toast.LENGTH_SHORT).show();
                    }
                    Log.e(TAG,cad);
                }
            }
            @Override
            public void onFailure(Call<EventResponse> call, Throwable t) {
                Toast.makeText(RegistrarActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e(TAG,t.getMessage());
            }
        });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(RegistrarActivity.this,MainActivity.class));
        finish();
    }

    public boolean hayInternet() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}



