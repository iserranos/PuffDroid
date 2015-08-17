package com.openpuff.android;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

class Util {

    private static Toast toast;

    @NonNull
    static String binaryToString(@NonNull String input) {
        String output = "";
        for (int i = 0; i <= input.length() - 8; i += 8) {
            int k = Integer.parseInt(input.substring(i, i + 8), 2);
            output += (char) k;
        }
        return output;
    }

    @NonNull
    static StringBuilder stringToBinary(@NonNull String textoOculto) {
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

    public static void showAToast(@NonNull Context context, String texto, int duracion) {
        try {
            toast.getView().isShown();
            toast.setText(texto);
        } catch (Exception e) {
            toast = Toast.makeText(context, texto, duracion);
        }
        toast.show();
    }

    static void hideKeyboard(@Nullable View view, @NonNull InputMethodManager inputManager) {
        // Check if no view has focus:
        if (view != null) {
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
}
