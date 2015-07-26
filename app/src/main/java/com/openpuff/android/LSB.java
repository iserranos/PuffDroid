package com.openpuff.android;

import android.support.annotation.NonNull;
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

    @NonNull
    String binaryToString(@NonNull String input) {
        String output = "";
        for (int i = 0; i <= input.length() - 8; i += 8) {
            int k = Integer.parseInt(input.substring(i, i + 8), 2);
            output += (char) k;
        }
        return output;
    }

    @NonNull
    StringBuilder stringToBinary(@NonNull String textoOculto) {
        byte[] bytes = textoOculto.getBytes();
        StringBuilder binary = new StringBuilder();
        for (byte b : bytes) {
            int val = b;
            for (int i = 0; i < 8; i++) {
                binary.append((val & 128) == 0 ? 0 : 1);
                val <<= 1;
            }
        }
        return binary;
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
