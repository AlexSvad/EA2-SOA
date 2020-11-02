package com.soa.unlam.ea2.activities;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.soa.unlam.R;
public class MainActivity extends AppCompatActivity {

    private TextView bateriaTextView;
    Button ir_login, ir_registrarse;

    private BroadcastReceiver bateriaReceptor = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int nivel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
            bateriaTextView.setText(String.valueOf(nivel)+"%");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        registerReceiver(this.bateriaReceptor,new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

        bateriaTextView = (TextView)findViewById(R.id.NivelBateriaTextView);
        ir_login=findViewById(R.id.ir_login);
        ir_registrarse=findViewById(R.id.ir_registrarse);

        ir_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish();
            }
        });

        ir_registrarse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, RegistrarActivity.class));
                finish();
            }
        });
    }
}
