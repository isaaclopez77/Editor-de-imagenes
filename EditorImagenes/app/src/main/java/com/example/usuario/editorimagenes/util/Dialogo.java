package com.example.usuario.editorimagenes.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;

public class Dialogo {
    private Context contexto;
    private int layout;
    private AlertDialog.Builder dialogo;
    private LayoutInflater inflater;
    private View vista;
    private OnDialogoListener odl;

    public Dialogo(Context contexto, int layout, OnDialogoListener odl){
        this.contexto=contexto;
        this.layout=layout;
        this.odl=odl;
        dialogo=new AlertDialog.Builder(this.contexto);
        inflater = LayoutInflater.from(this.contexto);
        vista = inflater.inflate(layout, null);
        dialogo.setView(vista);
        dialogo.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Dialogo.this.odl.onOkSelecter(vista);
                    }
                });
        dialogo.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                Dialogo.this.odl.onCancelSelecter(vista);
            }
        });
    }
    public void show(){
        odl.onPreShow(vista);
        dialogo.show();
    }
    public void setTitulo(String titulo){
        dialogo.setTitle(titulo);
    }
}