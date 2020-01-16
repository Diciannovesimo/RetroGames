package com.nullpointerexception.retrogames.Components;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.graphics.drawable.shapes.Shape;

import androidx.annotation.NonNull;

/**
 *      ProfileImageGenerator
 *
 *      Viene utilizzato per recuperare o generare un disegno da utilizzare come immagine del profilo.
 */
public class ProfileImageGenerator
{
    /**   Contesto utilizzato per eseguire le funzionalità di questo oggetto  */
    private Context context;
    /**   Drawable caricato (o generato)  */
    private Drawable resource;

    /**   Interfaccia utilizzata per fornire un'implementazione del metodo di callback richiamato quando la risorsa è pronta.  */
    public interface OnImageGeneratedListener
    { void onImageGenerated(Drawable drawable); }

    @SuppressLint("CheckResult")
    public ProfileImageGenerator(@NonNull Context context)
    {
        this.context = context;
    }

    /**
     *      Genera un'immagine per il nome specificato.
     *
     *      @param name     Nome dal quale generare un'immagine
     *      @param onImageGeneratedListener   Implementazione del metodo di callback.
     */
    @SuppressLint("CheckResult")
    public void fetchImageOf(@NonNull String name, OnImageGeneratedListener onImageGeneratedListener)
    {
        resource = new ProfileImageLetter(name);

        if(onImageGeneratedListener != null)
            onImageGeneratedListener.onImageGenerated(resource);
    }

    public Context getContext()
    {
        return context;
    }

    public Drawable getResource()
    {
        return resource;
    }
}

/**
 *      ProfileImageLetter
 *
 *      Drawable generato da una determinata stringa
 */
class ProfileImageLetter extends ShapeDrawable
{
    /**   Lettera visualizzata in un disegno  */
    private char letter;

    /**
     *      Costruisce l'oggetto
     *
     *      @param name Nome dell'utente
     */
    ProfileImageLetter(String name)
    {
        if( name != null && ! name.isEmpty())
            this.letter = name.toUpperCase().charAt(0);
        setShape(new OvalShape());
        setIntrinsicWidth(512);
        setIntrinsicHeight(512);
        setColorFilter( generateColorFor(name) , PorterDuff.Mode.SRC_ATOP);
    }

    @Override
    protected void onDraw(Shape shape, Canvas canvas, Paint paint2)
    {
        super.onDraw(shape, canvas, paint2);

        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.FILL);
        paint.setTextSize(350);
        paint.setTextAlign(Paint.Align.CENTER);

        canvas.drawText( "" + letter , ((float) getBounds().width()/2),
                ((float) getBounds().height()/2) - ((paint.descent() + paint.ascent()) /2), paint);
    }

    /**
     *      Genera un colore in base alla stringa specificata.
     *
     *      @param name     Stringa da cui ottenere il colore.
     *      @return         Colore generato.
     */
    private int generateColorFor(String name)
    {
        if( name == null || name.isEmpty())
            return Color.TRANSPARENT;

        int color = Color.rgb(mod(name.hashCode(), 255),
                mod(name.hashCode() / 255, 255),
                mod(name.hashCode() / (255 * 255), 255));

        return color;
    }

    /**
     *      Calcolo di un numero equivalente da x mod y.

     *      @param x    Numero da cui calcolare il suo equivalente
     *      @param y    Modulo
     *      @return     Un numero equivalente di x mod y
     */
    private int mod(int x, int y)
    {
        while(x < 0)
            x += y;

        return x % y;
    }

}
