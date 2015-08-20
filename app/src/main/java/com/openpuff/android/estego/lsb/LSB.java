package com.openpuff.android.estego.lsb;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.util.Base64;

import com.openpuff.android.estego.audio.Audio;
import com.openpuff.android.estego.imagen.Imagen;

import java.io.IOException;

public class LSB {

    private static final String TEXT = "txt";
    private static final String PNG = "png";

    public LSB() {
    }

    public void ocultarMensaje(Bitmap bitmapPortador, @NonNull String texto, @NonNull String pass1, @NonNull String pass2, @NonNull String pass3) throws IOException {
        Imagen imagenPortador = new Imagen(bitmapPortador, TEXT, texto, pass1, pass2, pass3);
        imagenPortador.ocultar();
    }

    public void ocultarMensaje(Bitmap bitmapPortador, @NonNull Bitmap bitmapMensaje, @NonNull String pass1, @NonNull String pass2, @NonNull String pass3) throws IOException {
        byte[] datos = new Imagen(bitmapMensaje).read();
        String mensaje = Base64.encodeToString(datos, Base64.DEFAULT);
        Imagen imagenPortador = new Imagen(bitmapPortador, PNG, mensaje, pass1, pass2, pass3);
        imagenPortador.ocultar();
    }

    public void ocultarMensaje(String cancion, @NonNull String texto, @NonNull String pass1, @NonNull String pass2, @NonNull String pass3) throws IOException {
        Audio audio = new Audio(cancion, TEXT, texto, pass1, pass2, pass3);
        audio.ocultar();
    }

    public void ocultarMensaje(String cancion, @NonNull Bitmap bitmapMensaje, @NonNull String pass1, @NonNull String pass2, @NonNull String pass3) throws IOException {
        byte[] datos = new Imagen(bitmapMensaje).read();
        String mensaje = Base64.encodeToString(datos, Base64.DEFAULT);
        Audio audio = new Audio(cancion, PNG, mensaje, pass1, pass2, pass3);
        audio.ocultar();
    }


    @NonNull
    public String descubrirMensaje(@NonNull Bitmap bitmap, @NonNull String pass1, @NonNull String pass2, @NonNull String pass3) throws Exception {
        Imagen imagen = new Imagen(bitmap, pass1, pass2, pass3);
        return imagen.descubrir();
    }

    @NonNull
    public String descubrirMensaje(String cancion, @NonNull String pass1, @NonNull String pass2, @NonNull String pass3) throws Exception {
        Audio audio = new Audio(cancion, pass1, pass2, pass3);
        return audio.descubrir();
    }
}
