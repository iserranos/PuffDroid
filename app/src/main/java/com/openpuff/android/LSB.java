package com.openpuff.android;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.widget.Toast;

class LSB {

    private Toast toast;

    public LSB() {
    }

    protected boolean ocultarMensaje(Bitmap bitmapPortador, @NonNull String texto) {
        StringBuilder dato = Util.stringToBinary(texto);

        StringBuilder binario;
        int color = 0;
        String cadenaNueva;
        int i = 0, j = 0, contador = 0;

        for (; contador != dato.length() && bitmapPortador != null; i++, color = 0) {
            try {
                color += bitmapPortador.getPixel(j, i);
                binario = Util.stringToBinary(Integer.toString(color));
                cadenaNueva = binario.substring(0, binario.length() - 1);
                cadenaNueva += dato.charAt(contador);
                contador++;
                cadenaNueva = Util.binaryToString(cadenaNueva);
                color = Integer.parseInt(cadenaNueva);
                bitmapPortador.setPixel(j, i, color);
            } catch (Exception e) {
                j++;
                i = 0;
                //return false;
            }
        }
        return true;
    }

    protected String descubrirMensaje(@NonNull Bitmap bitmap) {
        int tamanioI;
        StringBuilder binario;
        int color = 0;
        String cadenaNueva;
        cadenaNueva = "";
        int i = 0, j = 0;
        String caracter = "";
        String mensaje = "";

        for (; i < bitmap.getWidth(); i++, color = 0) {
            color += bitmap.getPixel(0, i);
            binario = Util.stringToBinary(Integer.toString(color));
            cadenaNueva += binario.charAt(binario.length() - 1);
            if ((i + 1) % 8 == 0) {
                cadenaNueva = Util.binaryToString(cadenaNueva);
                if (cadenaNueva.charAt(cadenaNueva.length() - 1) == '-') {
                    break;
                } else {
                    caracter += cadenaNueva;
                    cadenaNueva = "";
                }
            }
        }

        try {
            tamanioI = Integer.parseInt(caracter);
        } catch (NumberFormatException ignored) {
            return "";
        }

        i++;
        color = 0;
        int contador = i;
        cadenaNueva = "";
        int length = (int) (Math.log10(tamanioI) + 2);
        tamanioI += length;
        tamanioI *= 8;

        for (; contador < tamanioI; i++, color = 0) {
            try {
                color += bitmap.getPixel(j, i);
                contador++;
                binario = Util.stringToBinary(Integer.toString(color));
                cadenaNueva += binario.charAt(binario.length() - 1);
                if ((i + 1) % 8 == 0) {
                    cadenaNueva = Util.binaryToString(cadenaNueva);
                    mensaje += cadenaNueva;
                    cadenaNueva = "";
                }
            } catch (Exception e) {
                j++;
                i = 0;
            }
        }
        return mensaje;
    }


    void showAToast(Context context, String texto, int duracion) {
        try {
            toast.getView().isShown();
            toast.setText(texto);
        } catch (Exception e) {
            toast = Toast.makeText(context, texto, duracion);
        }
        toast.show();
    }

}
