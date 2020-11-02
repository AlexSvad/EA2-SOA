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
import com.soa.unlam.ea2.api.LoginRequest;
import com.soa.unlam.ea2.api.LoginResponse;
import java.io.IOException;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity {

    Button btn_ir_crearCuenta,iniciar_sesion;
    private EditText _et_email,_et_password;
    private Integer idThread=0;
    private static final String TAG = "LoginActivity";
    ProgressBar progressBar1;
    private  String tokenLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        _et_email = findViewById(R.id.et_email_login);
        _et_password = findViewById(R.id.et_password_login);
        progressBar1 = findViewById(R.id.progressBarLogin);

        btn_ir_crearCuenta=findViewById(R.id.btn_ir_crearCuenta);
        btn_ir_crearCuenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, RegistrarActivity.class));
                finish();
            }
        });

        iniciar_sesion=findViewById(R.id.iniciar_sesion);
        iniciar_sesion.setOnClickListener(view -> ejecutarLoginThread());
    }

    private void  ejecutarLoginThread() {
        LoginActivity.ThreadAsynctask LoginHilo=new LoginActivity.ThreadAsynctask();
        LoginHilo.execute("id LoginThread");
    }
    private class ThreadAsynctask extends AsyncTask<String, Integer, String>{
        @Override
        protected void onPreExecute() {
            progressBar1.setVisibility(View.VISIBLE);
            iniciar_sesion.setEnabled(false);

            if(!hayInternet()) Toast.makeText(getApplicationContext(),"No estás conectado a internet",Toast.LENGTH_SHORT).show();

            String _email = _et_email.getText().toString().trim();
            String _password = _et_password.getText().toString().trim();

            if (_email.isEmpty()){
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
            }
        }

        @Override
        protected String doInBackground(String... params) {
            idThread++;
            publishProgress(idThread);
            login();
            String mensaje =params[0]+idThread;
            return mensaje;
        }

        @Override
        protected void onPostExecute(String result) {
            progressBar1.setVisibility(View.INVISIBLE);
            iniciar_sesion.setEnabled(true);
        }

        @Override
        protected void onProgressUpdate(Integer ...values) {
            //Toast.makeText(getApplicationContext(),"Ejecutando Thread Numero:"+values[0].toString(),Toast.LENGTH_SHORT).show();
        }

    }

    private void login() {

        if(!hayInternet()) return;

        String _email = _et_email.getText().toString().trim();
        String _password = _et_password.getText().toString().trim();

        if (_email.isEmpty()){
            return;
        } else if (!_email.contains("@")){
            return;
        }else if (_password.isEmpty()){
            return;
        }else if (_password.length()<8){
            return;
        }else {

                LoginRequest request = new LoginRequest();
                request.setEmail(_email);
                request.setPassword(_password);

                Retrofit retrofit = new Retrofit.Builder()
                        .addConverterFactory(GsonConverterFactory.create())
                        .baseUrl(getString(R.string.retrofit_server))
                        .build();

                RetrofitInterface retrofitInterface = retrofit.create(RetrofitInterface.class);

                Call<LoginResponse> call = retrofitInterface.login(request);
                call.enqueue(new Callback<LoginResponse>() {
                    @Override
                    public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                        if(response.isSuccessful()){
                            tokenLogin = response.body().getToken();
                            registrarEvento();
                            Toast.makeText(LoginActivity.this,"Te logueaste "+_email, Toast.LENGTH_LONG).show();
                            Intent i = new Intent(LoginActivity.this, SensoresActivity.class);
                            i.putExtra("tokenUsuario", tokenLogin);
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
                    public void onFailure(Call<LoginResponse> call, Throwable t) {
                        Toast.makeText(LoginActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                        Log.e(TAG,t.getMessage());
                    }
                });
            }
    }

    void registrarEvento()
    {
        EventRequest request = new EventRequest();
        request.setEnv("PROD");
        request.setType_events("LoginExitoso");
        request.setDescription("Se logueó correctamente el usuario");

        Retrofit retrofit  = new Retrofit.Builder()
                .baseUrl(getString(R.string.retrofit_server))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RetrofitInterface apiInterface = retrofit.create(RetrofitInterface.class);
        Call<EventResponse> call = apiInterface.regEvent(request, "Bearer "+tokenLogin);
        call.enqueue(new Callback<EventResponse>() {
            @Override
            public void onResponse(Call<EventResponse> call, Response<EventResponse> response) {
                if(response.isSuccessful()){
                    Toast.makeText(LoginActivity.this, "Se registró correctamente el evento: "+response.body().getEvent().getType_events(), Toast.LENGTH_LONG).show();
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
                Toast.makeText(LoginActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e(TAG,t.getMessage());
            }
        });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(LoginActivity.this,MainActivity.class));
        finish();
    }

    public boolean hayInternet() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}
