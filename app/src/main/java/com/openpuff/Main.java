package com.openpuff;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

public class Main extends Activity implements OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inicio);

        ImageButton imagenOcultar = (ImageButton) findViewById(R.id.ImagenOcultar);
        ImageButton imagenDescubrir = (ImageButton) findViewById(R.id.ImagenDescubrir);

        imagenOcultar.setOnClickListener(this);
        imagenDescubrir.setOnClickListener(this);
    }

    @Override
    public void onClick(@NonNull View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.ImagenOcultar:
                intent = new Intent(this, Oculta.class);
                startActivity(intent);
                break;

            case R.id.ImagenDescubrir:
                intent = new Intent(this, Descubre.class);
                startActivity(intent);
                break;

            default:
                break;

        }
    }
}
