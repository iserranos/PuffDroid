package com.openpuff.android;

import android.widget.Toast;

/**
 * Created by INIGO on 24/07/2015.
 */
class LSB {

    private Toast toast;
    //private final Context context;

    public LSB() {
        //this.toast = new Toast(context);
        //this.context = context;
    }



    void showAToast(String texto, int duracion) {
        try {
            toast.getView().isShown();
            toast.setText(texto);
        } catch (Exception e) {
            //toast = Toast.makeText(context, texto, duracion);
        }
        toast.show();
    }

}
