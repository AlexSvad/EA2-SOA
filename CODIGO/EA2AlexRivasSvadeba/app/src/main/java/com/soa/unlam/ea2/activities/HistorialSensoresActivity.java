package com.soa.unlam.ea2.activities;
import com.soa.unlam.R;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import java.util.ArrayList;

public class HistorialSensoresActivity extends AppCompatActivity {

    private SensoresAdapter adapter;
    private String tipoSensor;
    private TextView sensorTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial_sensores);

        Intent i = getIntent();
        tipoSensor = i.getStringExtra("tipoSensor");

        sensorTextView = findViewById(R.id.sensorText);
        sensorTextView.setText(tipoSensor);

        SharedPreferences sharedPref = getSharedPreferences(tipoSensor, Context.MODE_PRIVATE);

        ArrayList<String> listaMediciones = new ArrayList<>();
        for(Object o : sharedPref.getAll().values())
            listaMediciones.add(o.toString());

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SensoresAdapter(this, listaMediciones);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(HistorialSensoresActivity.this,SensoresActivity.class));
        finish();
    }
}