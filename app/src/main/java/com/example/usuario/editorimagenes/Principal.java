package com.example.usuario.editorimagenes;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.usuario.editorimagenes.util.Dialogo;
import com.example.usuario.editorimagenes.util.OnDialogoListener;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class Principal extends AppCompatActivity {

    private ImageView iv;
    private Button btnEscala, btnRotar, btnInvertir, btnColores;
    private Bitmap bitmap;
    private Bitmap bitmapNuevo;
    private String nombreFoto;
    private EditText etFoto;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_principal,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id){
            case R.id.mnGuardar:
                final OnDialogoListener odl = new OnDialogoListener() {
                    @Override
                    public void onPreShow(View v) {
                        etFoto = (EditText)v.findViewById(R.id.etNombreFoto);
                    }

                    @Override
                    public void onOkSelecter(View v) {
                        nombreFoto = etFoto.getText().toString();
                        if(nombreFoto.matches("")){
                            Toast.makeText(Principal.this,"El título es obligatorio",Toast.LENGTH_SHORT).show();
                        }else {
                            FileOutputStream salida;
                            try {
                                salida = new FileOutputStream(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/Editor de Fotos/" + nombreFoto + ".jpg");
                                bitmapNuevo.compress(Bitmap.CompressFormat.JPEG, 90, salida);
                            } catch (FileNotFoundException e) {
                            }
                            Toast.makeText(Principal.this, "Guardada", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelSelecter(View v) {
                        Toast.makeText(Principal.this,"Cancelado",Toast.LENGTH_SHORT).show();
                    }
                };
                Dialogo d = new Dialogo(Principal.this, R.layout.guardar_foto,odl);
                d.show();

                break;

            case R.id.mnDeshacer:
                bitmapNuevo = bitmap;
                iv.setImageBitmap(bitmap);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.principal);
        iv = (ImageView) findViewById(R.id.ivFoto);
        btnEscala = (Button)findViewById(R.id.btnEscalaGris);
        btnRotar = (Button)findViewById(R.id.btnRotar);
        btnInvertir = (Button)findViewById(R.id.btnInvert);
        btnColores  = (Button)findViewById(R.id.btnColores);

        Intent i = getIntent();
        Uri imgUri = (Uri) i.getParcelableExtra(Intent.EXTRA_STREAM);

        try {
            bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imgUri);
            if(bitmap != null)
                iv.setImageBitmap(bitmap);
        } catch (Exception e){
            Toast.makeText(this, "SELECCIONE UNA IMAGEN DESDE FUERA DE LA APLICACIÓN", Toast.LENGTH_LONG).show();
        }

        bitmapNuevo = bitmap;

        //ONCLICKS
        btnEscala.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bitmapNuevo = toEscalaDeGris(bitmapNuevo);
                iv.setImageBitmap(bitmapNuevo);
            }
        });

        btnRotar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bitmapNuevo = rotarBitmap(bitmapNuevo, 90);
                iv.setImageBitmap(bitmapNuevo);
            }
        });

        btnInvertir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bitmapNuevo = invertir(bitmapNuevo);
                iv.setImageBitmap(bitmapNuevo);
            }
        });

        btnColores.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bitmapNuevo = colores(bitmapNuevo);
                iv.setImageBitmap(bitmapNuevo);
            }
        });
    }

    /*************** MÉTODOS PARA EDITAR ********************************/

    public static Bitmap toEscalaDeGris(Bitmap bmpOriginal) {
        Bitmap bmpGrayscale = Bitmap.createBitmap(bmpOriginal.getWidth(),bmpOriginal.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bmpGrayscale);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(bmpOriginal, 0, 0, paint);
        return bmpGrayscale;
    }

    public static Bitmap rotarBitmap(Bitmap bmpOriginal, float angulo) {
        Matrix matriz = new Matrix();
        matriz.postRotate(angulo);
        return Bitmap.createBitmap(bmpOriginal, 0, 0,
                bmpOriginal.getWidth(), bmpOriginal.getHeight(), matriz, true);
    }


    public Bitmap colores(Bitmap bitmap){
        Bitmap bmp = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig());
        int pixel, red, green, blue, alpha;
        for (int i = 0; i < bitmap.getWidth(); i++) {
            for (int j = 0; j < bitmap.getHeight(); j++) {
                pixel = bitmap.getPixel(i, j);
                red = Color.red(pixel);
                green = Color.green(pixel);
                blue = Color.blue(pixel);
                alpha = Color.alpha(pixel);
                red = 50 + red;
                green = 50 + green;
                blue = 100 + blue;
                alpha = 100 + alpha;
                bmp.setPixel(i, j, Color.argb(alpha, red, green, blue));
            }
        }
        return bmp;
    }

    public Bitmap invertir(Bitmap b){
        float[] mirrorY = { -1, 0, 0, 0, 1, 0, 0, 0, 1 }; // Para voltear la imagen sobre el eje Y (reflejo en una pared)
        //float[] mirrorXY = { -1, 0, 0, 0, -1, 0, 0, 0, 1 }; // Para voltear la imagen sobre ambos ejes
        //float[] mirrorX = { 1, 0, 0, 0, -1, 0, 0, 0, 1 }; // Para voltear la imagen sobre el eje X (reflejo en el suelo)
        Matrix matrixMirror = new Matrix();
        matrixMirror.setValues(mirrorY);
        Matrix matrix = new Matrix();
        matrix.postConcat(matrixMirror);
        Bitmap mirrorBitmap = Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(), matrix, true); // Creamos un nuevo Bitmap aplicando la matriz creada anteriormente
        return mirrorBitmap;
    }
}