package com.openpuff;

import android.os.Bundle;
import android.widget.ImageView;

public class Comparte extends Main {

    ImageView ImagenOriginal, ImagenCompartir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.comparte);
        ImagenOriginal = (ImageView) findViewById(R.id.ImagenOriginal);
        ImagenCompartir = (ImageView) findViewById(R.id.ImagenCompartir);
//        Bitmap bitmap = ((BitmapDrawable)ImagenOriginal.getDrawable()).getBitmap();
        //ImagenCompartir.setImageBitmap(((BitmapDrawable) ImagenOriginal.getDrawable()).getBitmap());
    }
}
