package com.example.alos1.middasecgf;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class BienvenidaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bienvenida);

        Button comenzar = (Button)findViewById(R.id.btnComenzar);

        comenzar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ListSong = new Intent(getApplicationContext(), ConexionActivity.class);
                startActivity(ListSong);
            }
        });
    }
}
