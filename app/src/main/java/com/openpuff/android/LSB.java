package com.openpuff.android;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.ByteArrayOutputStream;

class LSB {

    @NonNull
    private final Seguridad seguridad;

    public LSB() {
        this.seguridad = new Seguridad();
    }

    @Nullable
    Bitmap ocultarMensaje(Bitmap bitmapPortador, @NonNull String texto, @NonNull String pass1, @NonNull String pass2, @NonNull String pass3) {
        String textoFinal = seguridad.encrypt(texto.trim(), pass1.trim(), pass2.trim(), pass3.trim());
        StringBuilder dato = Util.stringToBinary(textoFinal.length() + "-" + textoFinal);
        return ocultarEnBitmap(bitmapPortador, dato);
    }

    @Nullable
    Bitmap ocultarMensaje(Bitmap bitmapPortador, @NonNull Bitmap bitmapMensaje, @NonNull String pass1, @NonNull String pass2, @NonNull String pass3) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmapMensaje.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        String texto = new String(byteArray);

        String textoFinal = seguridad.encrypt(texto.trim(), pass1.trim(), pass2.trim(), pass3.trim());
        StringBuilder dato = Util.stringToBinary(textoFinal.length() + "-" + textoFinal);

        return ocultarEnBitmap(bitmapPortador, dato);
    }

    @Nullable
    private Bitmap ocultarEnBitmap(@Nullable Bitmap bitmapPortador, @NonNull StringBuilder dato) {
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
                //return null;
            }
        }
        return bitmapPortador;
    }

    String ocultarMensaje(String cancion, @NonNull String texto, @NonNull String pass1, @NonNull String pass2, @NonNull String pass3) {
        String textoFinal = seguridad.encrypt(texto.trim(), pass1.trim(), pass2.trim(), pass3.trim());
        StringBuilder dato = Util.stringToBinary(textoFinal.length() + "-" + textoFinal);

        return ocultarEnCancion(cancion, dato);
    }

    String ocultarMensaje(String cancion, @NonNull Bitmap bitmapMensaje, @NonNull String pass1, @NonNull String pass2, @NonNull String pass3) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmapMensaje.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        String texto = new String(byteArray);

        String textoFinal = seguridad.encrypt(texto.trim(), pass1.trim(), pass2.trim(), pass3.trim());
        StringBuilder dato = Util.stringToBinary(textoFinal.length() + "-" + textoFinal);

        return ocultarEnCancion(cancion, dato);
    }

    private String ocultarEnCancion(String cancion, StringBuilder mensaje) {
        return cancion;
    }

    @NonNull
    String descubrirMensaje(@NonNull Bitmap bitmap, @NonNull String pass1, @NonNull String pass2, @NonNull String pass3) {
        String texto = descubrirDeBitmap(bitmap);
        return seguridad.decrypt(texto.trim(), pass1.trim(), pass2.trim(), pass3.trim());
    }

    @NonNull
    public String descubrirMensaje(String cancion, @NonNull String pass1, @NonNull String pass2, @NonNull String pass3) {
        String texto = descubrirDeCancion(cancion);
        return seguridad.decrypt(texto.trim(), pass1.trim(), pass2.trim(), pass3.trim());
    }

    private String descubrirDeBitmap(@NonNull Bitmap bitmap) {
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

    private String descubrirDeCancion(String cancion) {
        return cancion;
    }

}
