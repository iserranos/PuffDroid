package com.openpuff.android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

public class Main extends Activity implements OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inicio);

        ImageButton imagenOcultar = (ImageButton) findViewById(R.id.ImagenOcultar);
        ImageButton imagenDescubrir = (ImageButton) findViewById(R.id.ImagenDescubrir);
        TextView TextOcultar = (TextView) findViewById(R.id.TextOcultar);
        TextView TextDescubrir = (TextView) findViewById(R.id.TextDescubrir);

        imagenOcultar.setOnClickListener(this);
        imagenDescubrir.setOnClickListener(this);
        TextOcultar.setOnClickListener(this);
        TextDescubrir.setOnClickListener(this);
    }

    @Override
    public void onClick(@NonNull View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.ImagenOcultar:
            case R.id.TextOcultar:
                intent = new Intent(this, Oculta.class);
                startActivity(intent);
                break;

            case R.id.ImagenDescubrir:
            case R.id.TextDescubrir:
                intent = new Intent(this, Descubre.class);
                startActivity(intent);
                break;
        }
    }
}
