package com.soa.unlam.ea2.activities;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Calendar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.soa.unlam.R;
import com.soa.unlam.ea2.api.EventRequest;
import com.soa.unlam.ea2.api.EventResponse;
import com.soa.unlam.ea2.api.RetrofitInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SensoresActivity extends Activity implements SensorEventListener {

    private SensorManager mSensorManager;
    private TextView      acelerometro;
    private TextView      proximity;
    private TextView      luminosidad;
    private TextView      magnetic;
    private TextView      detecta;
    private Integer idThreadEvent=0;
    private static final String TAG = "SensoresActivity";
    private static final String TIPO_ACELEROMETRO = "Acelerómetro";
    private static final String TIPO_PROXIMIDAD = "Proximidad";
    private static final String TIPO_LUMINOSIDAD = "Luminosidad";
    private static final String TIPO_MAGNETROMETRO = "Magnetrómetro";
    String tokenSensores;
    private long contAcelerometro = 0, contProximidad = 0, contLuminosidad = 0, contMagnetico = 0;

    Button ir_historialSensoresAcelerometro,ir_historialSensoresProximidad,ir_historialSensoresLuminosidad,ir_historialSensoresMagnetic;

    DecimalFormat         dosdecimales = new DecimalFormat("###.###");

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensores);

        Intent i = getIntent();
        tokenSensores = i.getStringExtra("tokenUsuario");

        // Defino los botones
        ir_historialSensoresAcelerometro=findViewById(R.id.btn_aceler);
        ir_historialSensoresProximidad=findViewById(R.id.btn_proximidad);
        ir_historialSensoresLuminosidad=findViewById(R.id.btn_lumin);
        ir_historialSensoresMagnetic=findViewById(R.id.btn_magnet);

        ir_historialSensoresAcelerometro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(SensoresActivity.this, HistorialSensoresActivity.class);
                i.putExtra("tipoSensor", TIPO_ACELEROMETRO);
                i.putExtra("tokenUsuario", tokenSensores);
                startActivity(i);
                finish();
            }
        });

        ir_historialSensoresProximidad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(SensoresActivity.this, HistorialSensoresActivity.class);
                i.putExtra("tipoSensor", TIPO_PROXIMIDAD);
                i.putExtra("tokenUsuario", tokenSensores);
                startActivity(i);
                finish();
            }
        });

        ir_historialSensoresLuminosidad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(SensoresActivity.this, HistorialSensoresActivity.class);
                i.putExtra("tipoSensor", TIPO_LUMINOSIDAD);
                i.putExtra("tokenUsuario", tokenSensores);
                startActivity(i);
                finish();
            }
        });

        ir_historialSensoresMagnetic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(SensoresActivity.this, HistorialSensoresActivity.class);
                i.putExtra("tipoSensor", TIPO_MAGNETROMETRO);
                i.putExtra("tokenUsuario", tokenSensores);
                startActivity(i);
                finish();
            }
        });

        // Defino los TXT para representar los datos de los sensores
        acelerometro  = findViewById(R.id.acelerometro);
        proximity     = findViewById(R.id.proximity);
        luminosidad   = findViewById(R.id.luminosidad);
        magnetic      = findViewById(R.id.magnetic);
        detecta       = findViewById(R.id.detecta);

        // Accedemos al servicio de sensores
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

    }
    // Método para iniciar el acceso a los sensores
    protected void Ini_Sensores()
    {
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),   SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY),       SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT),           SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),  SensorManager.SENSOR_DELAY_NORMAL);
    }

    // Método para para la escucha de los sensores
    private void Parar_Sensores()
    {
        mSensorManager.unregisterListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER));
        mSensorManager.unregisterListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY));
        mSensorManager.unregisterListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT));
        mSensorManager.unregisterListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD));
    }

    // Método que escucha el cambio de sensibilidad de los sensores
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy)
    {

    }

    // Método que escucha el cambio de los sensores
    @Override
    public void onSensorChanged(SensorEvent event)
    {
        String txt = "", tipoSensor = "";

        // Cada sensor puede lanzar un thread que pase por aqui
        // Para asegurarnos ante los accesos simultáneos se syncroniza

        synchronized (this)
        {
            switch(event.sensor.getType())
            {
                case Sensor.TYPE_ACCELEROMETER :
                    tipoSensor = TIPO_ACELEROMETRO;
                    txt += "Sensor Acelerómetro:\n";
                    txt += "x: " + dosdecimales.format(event.values[0]) + " m/seg2 \n";
                    txt += "y: " + dosdecimales.format(event.values[1]) + " m/seg2 \n";
                    txt += "z: " + dosdecimales.format(event.values[2]) + " m/seg2 \n";
                    acelerometro.setText(txt);

                    if ((event.values[0] > 25) || (event.values[1] > 25) || (event.values[2] > 25))
                    {
                        detecta.setBackgroundColor(Color.parseColor("#cf091c"));
                        detecta.setText("Vibracion Detectada");
                    }
                    break;

                case Sensor.TYPE_PROXIMITY :
                    tipoSensor = TIPO_PROXIMIDAD;
                    txt += "Proximidad:\n";
                    txt += event.values[0] + "\n";

                    proximity.setText(txt);

                    // Si detecta 0 lo represento
                    if( event.values[0] == 0 )
                    {
                        detecta.setBackgroundColor(Color.parseColor("#cf091c"));
                        detecta.setText("Proximidad Detectada");
                    }
                    break;

                case Sensor.TYPE_LIGHT :
                    tipoSensor = TIPO_LUMINOSIDAD;
                    txt += "Luminosidad:\n";
                    txt += event.values[0] + " Lux \n";

                    luminosidad.setText(txt);
                    break;

                case Sensor.TYPE_MAGNETIC_FIELD :
                    tipoSensor = TIPO_MAGNETROMETRO;
                    txt += "Campo Magnetico:\n";
                    txt += event.values[0] + " uT" + "\n";

                    magnetic.setText(txt);
                    break;
            }

            SharedPreferences sharedPreferencSensor = getSharedPreferences(tipoSensor, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferencSensor.edit();
            String tiempo = Calendar.getInstance().getTime().toString();
            editor.putString(tiempo,tiempo+":\n"+txt);
            editor.apply();
        }
    }

    @Override
    protected void onStop()
    {

        Parar_Sensores();

        super.onStop();
    }

    @Override
    protected void onDestroy()
    {
        Parar_Sensores();

        super.onDestroy();
    }

    @Override
    protected void onPause()
    {
        Parar_Sensores();

        super.onPause();
    }

    @Override
    protected void onRestart()
    {
        Ini_Sensores();

        super.onRestart();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        ejecutarThreadEvento();
        Ini_Sensores();
    }

    private void  ejecutarThreadEvento() {
        SensoresActivity.ThreadAsynctask hiloEvent=new SensoresActivity.ThreadAsynctask();
        hiloEvent.execute("id Thread");
    }
    private class ThreadAsynctask extends AsyncTask<String, Integer, String>{
        @Override
        protected void onPreExecute() {
            if(!hayInternet()) Toast.makeText(getApplicationContext(),"No estás conectado a internet",Toast.LENGTH_SHORT).show();
        }

        @Override
        protected String doInBackground(String... params) {
            idThreadEvent++;
            publishProgress(idThreadEvent);
            registrarEvent();
            String mensaje =params[0]+idThreadEvent;
            return mensaje;
        }

        @Override
        protected void onPostExecute(String result) {
        }

        @Override
        protected void onProgressUpdate(Integer ...values) {
            //Toast.makeText(getApplicationContext(),"Ejecutando Thread Numero:"+values[0].toString(),Toast.LENGTH_SHORT).show();
        }

    }

    private void registrarEvent() {

        if(!hayInternet()) return;

            Log.e(TAG,"Tu token es:" + tokenSensores);

            EventRequest request = new EventRequest();
            request.setEnv("PROD");
            request.setType_events("SensorEvent");
            request.setDescription("Inicio de Actividad de los Sensores");

            Retrofit retrofit  = new Retrofit.Builder()
                    .baseUrl(getString(R.string.retrofit_server))
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            RetrofitInterface apiInterface = retrofit.create(RetrofitInterface.class);
            Call<EventResponse> call = apiInterface.regEvent(request, "Bearer "+tokenSensores);
            call.enqueue(new Callback<EventResponse>() {
                @Override
                public void onResponse(Call<EventResponse> call, Response<EventResponse> response) {
                    if(response.isSuccessful()){
                        Toast.makeText(SensoresActivity.this, "Se registró correctamente el evento: "+response.body().getEvent().getType_events(), Toast.LENGTH_LONG).show();
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
                    Toast.makeText(SensoresActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                    Log.e(TAG,t.getMessage());
                }
            });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(SensoresActivity.this,MainActivity.class));
        finish();
    }

    public boolean hayInternet() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}