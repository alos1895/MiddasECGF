package com.example.alos1.middasecgf;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class BienvenidaActivity extends AppCompatActivity {

    EditText eTNss;
    String numSs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bienvenida);

        Button comenzar = (Button)findViewById(R.id.btnStart);
        eTNss = (EditText)findViewById(R.id.etNumberSS);

        SharedPreferences pref=getSharedPreferences("datos", Context.MODE_PRIVATE);
        eTNss.setText(pref.getString("numSeguro",""));

        comenzar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Guardar Telefono
                SharedPreferences preferencias=getSharedPreferences("datos", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor=preferencias.edit();
                editor.putString("numSeguro", eTNss.getText().toString());
                editor.commit();
                numSs = eTNss.getText().toString();

                Intent intent = new Intent(getApplicationContext(), ConexionActivity.class);
                intent.putExtra("numSeguroSocial",numSs);
                startActivity(intent);
            }
        });

    }
}
