package com.openpuff.android.estego;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.openpuff.android.estego.audio.AudioSampleReader;
import com.openpuff.android.estego.audio.AudioSampleWriter;
import com.openpuff.android.utils.Util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.UnsupportedAudioFileException;

public class LSB {

    @NonNull
    private final Seguridad seguridad;

    public LSB() {
        this.seguridad = new Seguridad();
    }

    //takes in data for one channel and turns it into two channels
    private static double[] interleaveSamples(double[] mono) {
        double[] interleavedSamples = new double[mono.length * 2];
        int interleavedSamplesCounter = 0;
        for (double aMono : mono) {
            interleavedSamples[interleavedSamplesCounter] = aMono;
            interleavedSamplesCounter++;
            interleavedSamples[interleavedSamplesCounter] = aMono;
            interleavedSamplesCounter++;
        }
        return interleavedSamples;
    }

    //adds the data to the specified part of the array out
    private static void appendOutput(double[] in, int startIndex, double[] out) {
        for (double anIn : in) {
            out[startIndex] = anIn;
            startIndex++;
        }
    }

    private static String constructMessage(String[] messageInBinary) {
        String message = "";
        for (String aMessageInBinary : messageInBinary) {
            int byteAsInt = byteToInt(aMessageInBinary);
            if (byteAsInt != -1) {
                message = message + String.valueOf((char) byteAsInt);
            }
        }

        return message;
    }

    private static int byteToInt(String byteAsString) {
        if (byteAsString == null) {
            return -1;
        }
        int byteAsInt = Integer.parseInt(byteAsString);
        int intValue = 0;
        for (int i = 1; i < 9; i++) {
            if ((numberOfPlaces((int) (byteAsInt % Math.pow(10, i))) == i) && (byteAsInt % Math.pow(10, i) != 0)) {
                intValue += Math.pow(2, (i - 1));
            }
        }
        return intValue;
    }

    private static int numberOfPlaces(int num) {
        int toReturn;
        if (num > 9999999) {
            toReturn = 8;
        } else if (num > 999999) {
            toReturn = 7;
        } else if (num > 99999) {
            toReturn = 6;
        } else if (num > 9999) {
            toReturn = 5;
        } else if (num > 999) {
            toReturn = 4;
        } else if (num > 99) {
            toReturn = 3;
        } else if (num > 9) {
            toReturn = 2;
        } else {
            toReturn = 1;
        }
        return toReturn;
    }

    @Nullable
    public Bitmap ocultarMensaje(Bitmap bitmapPortador, @NonNull String texto, @NonNull String pass1, @NonNull String pass2, @NonNull String pass3) {
        String textoFinal = seguridad.encrypt(texto.trim(), pass1.trim(), pass2.trim(), pass3.trim());
        StringBuilder dato = Util.stringToBinary(textoFinal.length() + "-" + textoFinal);
        return ocultarEnBitmap(bitmapPortador, dato);
    }

    @Nullable
    public Bitmap ocultarMensaje(Bitmap bitmapPortador, @NonNull Bitmap bitmapMensaje, @NonNull String pass1, @NonNull String pass2, @NonNull String pass3) {
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

    public String ocultarMensaje(String cancion, @NonNull String texto, @NonNull String pass1, @NonNull String pass2, @NonNull String pass3) {
        String textoFinal = seguridad.encrypt(texto.trim(), pass1.trim(), pass2.trim(), pass3.trim());
        StringBuilder dato = Util.stringToBinary(textoFinal.length() + "-" + textoFinal);

        return ocultarEnCancion(cancion, dato);
    }

    public String ocultarMensaje(String cancion, @NonNull Bitmap bitmapMensaje, @NonNull String pass1, @NonNull String pass2, @NonNull String pass3) {
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
    public String descubrirMensaje(@NonNull Bitmap bitmap, @NonNull String pass1, @NonNull String pass2, @NonNull String pass3) {
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

    public void encodeMessage(File audioFile, String message, String outPath) { //change outPath to File
        ASCII phrase = new ASCII(message);
        int[] messageAsBits = phrase.getBinaryBitArray();
        int currentBit = 0;
        try {
            AudioSampleReader sampleReader = new AudioSampleReader(audioFile);
            int bytesRead = 0;
            int nbChannels = sampleReader.getFormat().getChannels();
            int totalBytes = (int) sampleReader.getSampleCount() * nbChannels;
            double[] out = new double[totalBytes];
            int bytesToRead = 4096 * 2; //some aribituary number thats 2^n
            double[] audioData = new double[totalBytes];
            sampleReader.getInterleavedSamples(0, totalBytes, audioData);

            if (totalBytes / bytesToRead < messageAsBits.length) {
                throw new RuntimeException("The audio file is too short for the message to fit!");
            }

            while (bytesRead < totalBytes && currentBit < messageAsBits.length) {
                if (totalBytes - bytesRead < bytesToRead) {
                    bytesToRead = totalBytes - bytesRead;
                }

                //System.out.println("Reading data.");
                //take a portion of the data
                double[] samples = new double[bytesToRead];
                System.arraycopy(audioData, bytesRead, samples, 0, samples.length);
                bytesRead += bytesToRead;
                double[] channelOne = new double[samples.length / 2];
                sampleReader.getChannelSamples(0, samples, channelOne);

                //System.out.println("Taking the FFT.");
                //take the FFT
                double[][] freqMag = Util.getMag(channelOne, (int) sampleReader.getFormat().getFrameRate());

                channelOne = Util.correctDataLength(channelOne);
                Util[] complexData = new Util[channelOne.length];
                for (int i = 0; i < channelOne.length; i++) {
                    complexData[i] = new Util(channelOne[i], 0);
                }
                Util[] complexMags = Util.fft(complexData);
                double[] freqs = Util.getFreqs(complexData.length, (int) sampleReader.getFormat().getFrameRate());

                //pick the fundamentalAmp
                double fundamentalAmp = 0;
                for (double[] aFreqMag : freqMag) {
                    if (Math.abs(aFreqMag[1]) > fundamentalAmp) {
                        fundamentalAmp = aFreqMag[1];
                    }
                }
                boolean isRest = false;
                if (fundamentalAmp < .01) {
                    isRest = true;
                }

                //System.out.println("Writing the 1 or 0");
                //decide if the overtone should be changed and if so, change it. don't write if its a rest
                if (messageAsBits[currentBit] == 1 && !isRest) {
                    //edit the data thats going to be ifft'd
                    for (int i = 0; i < freqs.length; i++) {
                        if (Math.abs(Math.abs(freqs[i]) - 20000) < 5) { //lets try changing a set freq
                            complexMags[i] = new Util(.01 * channelOne.length, 0);
                        }
                    }

                    //take the IFFT
                    Util[] ifft = Util.ifft(complexMags);

                    //change ifft data from complex to real. put in fft class?
                    double[] ifftReal = new double[ifft.length];
                    for (int i = 0; i < ifftReal.length; i++) {
                        ifftReal[i] = ifft[i].re();
                    }

                    appendOutput(interleaveSamples(ifftReal), bytesRead - bytesToRead, out); //add to the array thats going to be written out
                    currentBit++;
                } else if (messageAsBits[currentBit] == 0 && !isRest) {
                    //add a 0 to the message
                    appendOutput(samples, bytesRead - bytesToRead, out);
                    currentBit++;
                } else if (isRest) {
                    appendOutput(samples, bytesRead - bytesToRead, out); //add on the rest so it doesn't sound weird, but don't make it seem like any bits are being written.
                }
            }

            //writing out the leftover part of the audio file (which doesn't have any encoded btis in it)
            if (bytesRead < totalBytes) {
                double[] leftoverData = new double[totalBytes - bytesRead];
                //take a portion of the data
                System.arraycopy(audioData, bytesRead, leftoverData, 0, leftoverData.length);
                appendOutput(leftoverData, bytesRead, out);
            }

            File outFile = new File(outPath);
            AudioSampleWriter audioWriter = new AudioSampleWriter(outFile,
                    sampleReader.getFormat(), AudioFileFormat.Type.WAVE);
            audioWriter.write(out);
            audioWriter.close();
        } catch (UnsupportedAudioFileException | IOException e) {
            e.printStackTrace();
        }
    }

    public String decodeMessage(File audioFile) {
        String hiddenMessage = "";
        try {
            AudioSampleReader sampleReader = new AudioSampleReader(audioFile);
            int bytesRead = 0;
            int nbChannels = sampleReader.getFormat().getChannels();
            int totalBytes = (int) sampleReader.getSampleCount() * nbChannels;
            int bytesToRead = 4096 * 2; //some aribituary number thats 2^n
            String[] messageAsBytes = new String[totalBytes / bytesToRead];
            int currentCharIndex = 0;
            int bitsSaved = 0;

            double[] audioData = new double[totalBytes];
            sampleReader.getInterleavedSamples(0, totalBytes, audioData);
            while (bytesRead < totalBytes) {
                if (totalBytes - bytesRead < bytesToRead) {
                    bytesToRead = totalBytes - bytesRead;
                }

                //read in the data
                double[] samples = new double[bytesToRead];
                System.arraycopy(audioData, bytesRead, samples, 0, samples.length);
                bytesRead += bytesToRead;
                double[] channelOne = new double[samples.length / 2];
                sampleReader.getChannelSamples(0, samples, channelOne);

                //take the FFT
                channelOne = Util.correctDataLength(channelOne);
                double[][] freqMag = Util.getMag(channelOne, (int) sampleReader.getFormat().getFrameRate());

                //pick the fundamentalAmp
                double fundamentalAmp = 0;
                for (double[] aFreqMag : freqMag) {
                    if (Math.abs(aFreqMag[1]) > fundamentalAmp) {
                        fundamentalAmp = aFreqMag[1];
                    }
                }
                boolean isRest = false;
                if (fundamentalAmp < .01) {
                    isRest = true;
                }

                //get the amplitude of freq 20000
                double ampToTest = 0;
                if (!isRest) {
                    for (double[] aFreqMag : freqMag) { //you don't have to start from 0..
                        if (Math.abs(Math.abs(aFreqMag[0]) - 20000) < 5) {
                            ampToTest = aFreqMag[1];
                        }
                    }
                }

                if (!isRest) {
                    //compare the overtones to see if there should be a 1 or 0
                    //int overtoneToTest = overtones.length;
                    //if (Math.abs(overtones[overtoneToTest-1][1]-expectedOvertones[overtoneToTest-1])>.0049) {
                    if (ampToTest > .009) { //just test a certain freq
                        //checking if something is null..
                        if (messageAsBytes[currentCharIndex] == null) {
                            messageAsBytes[currentCharIndex] = "1";
                        } else {
                            messageAsBytes[currentCharIndex] = "1" + messageAsBytes[currentCharIndex]; //adding a 1
                        }
                    } else {
                        if (messageAsBytes[currentCharIndex] == null) {
                            messageAsBytes[currentCharIndex] = "0";
                        } else {
                            messageAsBytes[currentCharIndex] = "0" + messageAsBytes[currentCharIndex]; //adding a 0
                        }
                    }
                    bitsSaved++;
                    if (bitsSaved % 8 == 0) {
                        if (messageAsBytes[currentCharIndex].equals("00000000")) { //if null
                            System.out.println("The message is over.");
                            break; //the message is done
                        }
                        currentCharIndex++;
                    }
                }
            }

            hiddenMessage = constructMessage(messageAsBytes);
        } catch (UnsupportedAudioFileException | IOException e) {
            e.printStackTrace();
        }
        return hiddenMessage;
    }

}
