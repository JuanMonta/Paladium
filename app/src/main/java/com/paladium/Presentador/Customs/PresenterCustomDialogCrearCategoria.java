package com.paladium.Presentador.Customs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.paladium.Model.Firebase.BaseDeDatos;
import com.paladium.Model.Utils.Utilidades;
import com.paladium.R;

import java.util.HashMap;
import java.util.Map;

public class PresenterCustomDialogCrearCategoria {

    private Context mContext;
    private TextView mensaje;
    public PresenterCustomDialogCrearCategoria(Context mContext) {
        this.mContext = mContext;
        final Dialog dialogCategoria = new Dialog(mContext);
        dialogCategoria.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogCategoria.setCancelable(true);
        dialogCategoria.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogCategoria.setContentView(R.layout.custom_dialog_crear_categoria);

         mensaje = dialogCategoria.findViewById(R.id.custom_dialog_crear_categoria_tvMensaje);
        TextInputEditText inputEdNombreCategoria = dialogCategoria.findViewById(R.id.custom_dialog_crear_categoria_inputEdNombreCategoria);
        inputEdNombreCategoria.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                ocultarTextView();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        Button btnCrearCategoria = dialogCategoria.findViewById(R.id.custom_dialog_crear_categoria_btnCrearcategoria);
        btnCrearCategoria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Map<String, Object> categoria = new HashMap<>();
                categoria.put(Utilidades.categoriaProducto, inputEdNombreCategoria.getText().toString().trim());

                BaseDeDatos.getFireDatabaseIntanceReference()
                        .child(Utilidades.nodoPadre)
                        .child(Utilidades.nodoCategoria)
                        .push()
                        .updateChildren(categoria)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                            mensajeError(false);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                mensajeError(true);
                            }
                        });


            }
        });

        dialogCategoria.show();
    }

    private void ocultarTextView(){
        mensaje.setVisibility(View.INVISIBLE);
    }
    private void mensajeError(boolean error){
        if (error){
            mensaje.setVisibility(View.VISIBLE);
            mensaje.setTextColor(mContext.getResources().getColor(R.color.red));
            mensaje.setText("Error al crear, intente nuevamente");
        }else {
            mensaje.setVisibility(View.VISIBLE);
            mensaje.setTextColor(mContext.getResources().getColor(R.color.green_700));
            mensaje.setText("Categoria registrada");
        }
    }

}
