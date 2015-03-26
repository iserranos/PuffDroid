package com.openpuff;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

public class Main extends Activity implements OnClickListener {

    ImageButton ImagenOcultar, ImagenDescubrir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inicio);


        ImagenOcultar = (ImageButton) findViewById(R.id.ImagenOcultar);
        ImagenDescubrir = (ImageButton) findViewById(R.id.ImagenDescubrir);

        ImagenOcultar.setOnClickListener(this);
        ImagenDescubrir.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
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

            /*case R.id.ImagenContacto:

                intent = new Intent(this, Oculta.class);
                startActivity(intent);

                break;*/

            default:
                break;

        }
    }
}
