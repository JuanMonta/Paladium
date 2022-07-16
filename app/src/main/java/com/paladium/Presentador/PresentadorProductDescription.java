package com.paladium.Presentador;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.paladium.Model.Firebase.BaseDeDatos;
import com.paladium.Model.Logica.Producto;
import com.paladium.Model.Utils.Utilidades;
import com.paladium.R;

public class PresentadorProductDescription {
    private final String TAG = "PresentadorProductDescr";
    private Context mContext;
    private Bundle bundle;
    private View mView;
    private String idProducto = null;

    private TextView codBarras, nombre, cantidad, precio, costo, categoria, descripcion;
    private Button btnEditar, btnEliminar;

    public PresentadorProductDescription(Context mContext, Bundle bundle, View view) {
        this.mContext = mContext;
        this.bundle = bundle;
        this.mView = view;
        if (bundle.keySet() != null) {
            idProducto = bundle.getString(Utilidades.productoFirebaseKey);
        }
        init();
        cargarDatosProductos();
    }

    private void init() {
        codBarras = mView.findViewById(R.id.activity_product_descrip_tvCodBarras);
        nombre = mView.findViewById(R.id.activity_product_descrip_tvNombre);
        cantidad = mView.findViewById(R.id.activity_product_descrip_tvCantidad);
        precio = mView.findViewById(R.id.activity_product_descrip_tvPrecio);
        costo = mView.findViewById(R.id.activity_product_descrip_tvCosto);
        categoria = mView.findViewById(R.id.activity_product_descrip_tvCategoria);
        descripcion = mView.findViewById(R.id.activity_product_descrip_tvDescripcion);

        btnEditar = mView.findViewById(R.id.activity_product_descrip_btnEditar);
        btnEliminar = mView.findViewById(R.id.activity_product_descrip_btnEliminar);
    }

    private void cargarDatosProductos() {
        //adValueChange Listener escuha cuando un valor se ha cambiado en la base de datos en tiempo real,
        //si cambia en la BD, la Ui se actualiza automáticamente gracias a este método

        if (idProducto != null) {
            BaseDeDatos.getFireDatabase().child(Utilidades.nodoPadre).child(Utilidades.nodoProducto).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    Producto producto = new Producto();
                    for (DataSnapshot datos : dataSnapshot.getChildren()) {
                        // Log.d(TAG, "dataSnashot: "+datos.getKey());
                        if (datos.getKey().equals(idProducto)) {
                             producto = datos.getValue(Producto.class);
                            Log.d(TAG, "PresentadorProductDescription --------------------------------- ");
                            Log.d(TAG, "Nombre: " + producto.getNombre());
                            Log.d(TAG, "Cantidad: " + producto.getCantidad());
                            Log.d(TAG, "Precio: " + producto.getPrecio());
                            Log.d(TAG, "Costo: " + producto.getCosto());
                            Log.d(TAG, "Descrip: " + producto.getDescripcion());
                            Log.d(TAG, "CobBar: " + producto.getCodBarras());
                            Log.d(TAG, "ImagenURL: " + producto.getImagen());
                            Log.d(TAG, "-----------------------------------------------------------");
                        }
                    }
                    codBarras.setText(producto.getCodBarras());
                    cantidad.setText(String.valueOf(producto.getCantidad()));
                    nombre.setText(producto.getNombre());
                    precio.setText(String.valueOf(producto.getPrecio()));
                    costo.setText(String.valueOf(producto.getCosto()));
                    categoria.setText(producto.getCategoria());
                    descripcion.setText(producto.getDescripcion());

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }


}
