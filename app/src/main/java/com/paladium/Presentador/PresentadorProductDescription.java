package com.paladium.Presentador;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.paladium.Model.Firebase.BaseDeDatos;
import com.paladium.Model.Logica.Producto;
import com.paladium.Model.Utils.Utilidades;
import com.paladium.R;
import com.paladium.Vista.Activities.ProductCreation;

import java.util.Objects;

public class PresentadorProductDescription implements View.OnClickListener {
    private final String TAG = "PresentadorProductDescr";
    private Context mContext;
    private View mView;
    private Producto producto = null;

    private TextView codBarras, nombre, cantidad, precio, costo, categoria, descripcion;
    private ImageView imgFotoProducto;
    private ProgressBar progressBarFoto;
    private Button btnEditar, btnEliminar;

    public PresentadorProductDescription(Context mContext, Bundle bundle, View view) {
        this.mContext = mContext;
        this.mView = view;
        if (bundle.keySet() != null) {
            producto = (Producto) bundle.getSerializable(Utilidades.bundleProduto);
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
        imgFotoProducto = mView.findViewById(R.id.activity_product_descrip_ImagenProducto);
        progressBarFoto = mView.findViewById(R.id.activity_product_descrip_ProgressBarFoto);

        btnEditar = mView.findViewById(R.id.activity_product_descrip_btnEditar);
        btnEditar.setOnClickListener(this);
        btnEliminar = mView.findViewById(R.id.activity_product_descrip_btnEliminar);
        btnEliminar.setOnClickListener(this);
    }

    private void cargarDatosProductos() {
        //adValueChange Listener escuha cuando un valor se ha cambiado en la base de datos en tiempo real,
        //si cambia en la BD, la Ui se actualiza automáticamente gracias a este método

        if (producto != null) {
            BaseDeDatos.getFireDatabaseIntanceReference()
                    .child(Utilidades.nodoPadre)
                    .child(Utilidades.nodoProducto)
                    .addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    Producto producto = new Producto();
                    for (DataSnapshot datos : dataSnapshot.getChildren()) {
                        // Log.d(TAG, "dataSnashot: "+datos.getKey());
                        if (Objects.equals(datos.getKey(), PresentadorProductDescription.this.producto.getProductoFirebaseKey())) {
                            producto = datos.getValue(Producto.class);
                            Log.d(TAG, "PresentadorProductDescription --------------------------------- ");
                            Log.d(TAG, "Nombre: " + producto.getNombre());
                            Log.d(TAG, "Cantidad: " + producto.getCantidad());
                            Log.d(TAG, "Precio: " + producto.getPrecio());
                            Log.d(TAG, "Costo: " + producto.getCosto());
                            Log.d(TAG, "Categoria: " + producto.getCategoria());
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
                    cargarImagenFirebase(producto.getImagen());

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    private void cargarImagenFirebase(String URLImagen) {
        //Log.d(TAG, "Cargando Imagen: " + URLImagen);
        if (!URLImagen.isEmpty()) {
            //Log.d("ADAPTER_PRODUCTOS", "Imagen no es vacia");
            Glide
                    .with(mContext)
                    .load(URLImagen)
                    .centerInside()
                    .fitCenter()
                    .listener(new RequestListener<Drawable>() {

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            //Log.d(TAG, "Imagen cargada");
                            progressBarFoto.setVisibility(View.GONE);
                            return false;
                        }

                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            //Log.d(TAG, "Imagen error al cargar");
                            progressBarFoto.setVisibility(View.GONE);
                            imgFotoProducto.setImageResource(R.drawable.baseline_error_red_48dp);
                            return false;
                        }

                    })
                    .into(imgFotoProducto);

        } else {
            Log.d(TAG, "Imagen URL vacia");
            progressBarFoto.setVisibility(View.GONE);
            imgFotoProducto.setImageResource(R.drawable.baseline_camera_alt_green_black_48dp);
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.activity_product_descrip_btnEditar:
                Intent intent = new Intent(mContext, ProductCreation.class);
                Bundle bundleProducto = new Bundle();
                bundleProducto.putSerializable(Utilidades.bundleProduto, this.producto);
                intent.putExtras(bundleProducto);
                mContext.startActivity(intent);
                break;
            case R.id.activity_product_descrip_btnEliminar:

                break;
        }
    }
}
