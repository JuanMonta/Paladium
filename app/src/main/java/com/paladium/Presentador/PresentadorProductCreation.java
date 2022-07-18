package com.paladium.Presentador;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.paladium.Model.Firebase.BaseDeDatos;
import com.paladium.Model.Logica.Producto;
import com.paladium.Model.Utils.Utilidades;
import com.paladium.Presentador.Customs.PresenterCustomDialogCrearCategoria;
import com.paladium.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class PresentadorProductCreation implements View.OnClickListener, InterfacePresenter_ProductCreation.onImagenCargada {
    private final String TAG = "PresenterProdCreation";
    private Context mContext;
    private Uri filePath;
    private TextInputEditText inputEdCodBarras, inputEdCantDisponible, inputEdNombreProducto,
            inputEdPrecioUnit, inputEdCostoUnit, inputEdDescripcion;
    private ImageButton imgBtnQuitarImagen, imgBtnCrearCategoria;
    private ImageView imgv_ImagenProducto;
    private AutoCompleteTextView autoCompletTextSpCategoria;
    private InterfacePresenter_ProductCreation.onImagenCargada interfaceImagenCargada;
    private InterfacePresenter_ProductCreation.onProductoCargado interfaceProductoCargado;
    private Bundle bundleProducto;
    private Producto producto;
    private ProgressDialog progressDialog;

    public PresentadorProductCreation(Context context, InterfacePresenter_ProductCreation.onProductoCargado interfaceProductoCargado
            , Bundle bundleProducto) {
        this.mContext = context;
        this.interfaceProductoCargado = interfaceProductoCargado;
        this.bundleProducto = bundleProducto;
        progressDialog = new ProgressDialog(mContext);
    }


    public void init(View view) {
        interfaceImagenCargada = this;
        inputEdCodBarras = view.findViewById(R.id.product_creation_edCodBarras);
        inputEdCantDisponible = view.findViewById(R.id.product_creation_edCantidadDisponible);
        inputEdNombreProducto = view.findViewById(R.id.product_creation_edNombreProducto);
        inputEdPrecioUnit = view.findViewById(R.id.product_creation_edPrecioUnitario);
        inputEdCostoUnit = view.findViewById(R.id.product_creation_edCostoUnitario);
        inputEdDescripcion = view.findViewById(R.id.product_creation_edDescripcionProducto);

        imgv_ImagenProducto = view.findViewById(R.id.product_creation_Imgv_Producto);

        imgBtnQuitarImagen = view.findViewById(R.id.product_creation_imgbQuitarImagen);
        imgBtnQuitarImagen.setOnClickListener(this);
        imgBtnCrearCategoria = view.findViewById(R.id.product_creation_imgbCrearCategoria);
        imgBtnCrearCategoria.setOnClickListener(this);

        autoCompletTextSpCategoria = view.findViewById(R.id.product_creation_spCategoria);

        //String[] categorias = mContext.getResources().getStringArray(R.array.array_producto_categoria_de_prueba);
        //ArrayAdapter arrayAdapter = new ArrayAdapter(mContext, R.layout.custom_text_view_dropdown_menu_producto_categoria, categorias);
        //autoCompletTextSpCategoria.setAdapter(arrayAdapter);

        cargarCategoriasProductos();

        //recibiré un bundle con la clase producto cuando se necesite editar los datos del producto
        if (bundleProducto != null) {
            producto = (Producto) bundleProducto.getSerializable(Utilidades.bundleProduto);
            cargarDatosProductosParaEdicion();

        }

    }

    private ProgressDialog productProgressDialog() {
        progressDialog.setCancelable(false);
        return progressDialog;
    }

    /**
     * Mensaje ha mostrar cuando subidos un producto a firebase, informandonos si
     * se ha subido o no el producto.
     *
     * @param tipoAlert título que tendrá la alerta
     * @param mensaje      mensaje de la alerta
     */
    private void productAlertDialog(String tipoAlert, String mensaje) {

        AlertDialog.Builder ok_or_error_dialog = new AlertDialog.Builder(mContext);
        if (tipoAlert.equals(Utilidades.alertDialog_EXITO)){
            ok_or_error_dialog.setTitle("ÉXITO");
        }else {
            ok_or_error_dialog.setTitle("ERROR");
        }
        ok_or_error_dialog.setMessage(mensaje);
        ok_or_error_dialog.setCancelable(false);
        ok_or_error_dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                //notificamos con la interfaz de que debe borrarse el Uri filePath que contiene
                //la ruta de la imagen en el dispositivo una vez la hayamos subido a firebase
                if (tipoAlert.equals(Utilidades.alertDialog_EXITO)){
                    limpiarCampos();
                    interfaceProductoCargado.productoCargado(true);
                }
            }
        });
        ok_or_error_dialog.show();
    }

    /**
     * Para registrar un producto en firebase, comenzando primeramente con la imagen del producto,
     * luego con los datos del producto.
     *
     * @param rutaImagen Uri de la imagen en el dispositivo que será usada para subirse a firebase
     */
    public void registrarProducto(Uri rutaImagen) {
        this.filePath = rutaImagen;
        //es una edicion
        if (bundleProducto != null) {
            actualizarDatos();
        } else {//es registro

            //nombre de como se guardará la imagen en firebase
            registrarProductoConOSinImagenFirebase();
        }

    }

    private void registrarProductoConOSinImagenFirebase() {
        if (productProgressDialog().isShowing()) {
            productProgressDialog().dismiss();
        } else {
            productProgressDialog().setMessage("Añadiendo producto...");
            productProgressDialog().show();
        }

        String imagenNonbre = "";
        Calendar fecha = null;
        fecha = GregorianCalendar.getInstance();
        //si tiene foto seleccionada
        if (this.filePath != null) {
            imagenNonbre = this.filePath.getLastPathSegment() + fecha.getTimeInMillis();

            final StorageReference fotoRef = BaseDeDatos.getFireStorageInstanceReference().child(Utilidades.nodoStorageFotosProductos).child(imagenNonbre);
            UploadTask uploadTask = fotoRef.putFile(this.filePath);
            Uri finalRutaImagen = this.filePath;
            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        Log.d(TAG, "SUBIDA DE IMAGEN FALLIDA");
                        throw new Exception();
                    }
                    Log.d(TAG, "SE SUBIO LA IMAGEN: ");
                    //retornamos el link de descarga se la imagen que acabamos de subir
                    return fotoRef.getDownloadUrl();
                }
            }).addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Log.d(TAG, "ONCOMPLETE: " + uri.toString());
                    Log.d(TAG, "ONCOMPLETE: " + uri.getPath());
                    Log.d(TAG, "ONCOMPLETE: " + uri.getScheme());
                    Log.d(TAG, "ONCOMPLETE: " + uri.getLastPathSegment());
                    //lanzo la interfaz para que llame a guardar los datos de los produtos.
                    //si llamo aquí el método, por alguna razó la aplicación se congela,
                    //pero si lo activo mediante esta interfaz funciona bien
                    imagenCargada(uri, finalRutaImagen);
                }
            });

        } else {//si no ha seleccionado ninguna imagen
            guardarDatosProductosFireBase(null, this.filePath);
        }
    }

    /**
     * Despues de que se haya subido la imagen del producto a firebase y obtenido su
     * respectivo link de descarga(si fuere el caso de tener imagen),
     * procedemos a guardar el resto de los datos del producto
     *
     * @param downloadLinkImage
     * @param rutaImagen
     */
    private void guardarDatosProductosFireBase(Uri downloadLinkImage, Uri rutaImagen) {
        if (productProgressDialog().isShowing()) {
            productProgressDialog().dismiss();
        } else {
            productProgressDialog().setMessage("Añadiendo producto...");
            productProgressDialog().show();
        }

        String nombre, descrip, codBarras, fotoProducto, categoria;
        int cantidad;
        float precioUnit, costoUnit;

        codBarras = inputEdCodBarras.getText().toString().trim();
        nombre = inputEdNombreProducto.getText().toString().trim();
        cantidad = inputEdCantDisponible.getText().toString().trim().isEmpty() ? 0 : Integer.parseInt(inputEdCantDisponible.getText().toString().trim());
        precioUnit = Float.parseFloat(inputEdPrecioUnit.getText().toString().trim());
        costoUnit = Float.parseFloat(inputEdCostoUnit.getText().toString().trim());
        descrip = inputEdDescripcion.getText().toString().trim();
        categoria = autoCompletTextSpCategoria.getText().toString().trim();

        Log.d(TAG, "Guardando datos");
        Map<String, Object> productos = new HashMap<>();
        productos.put(Utilidades.codBarrasProducto, codBarras);
        productos.put(Utilidades.nombreProducto, nombre);
        productos.put(Utilidades.cantProducto, cantidad);
        productos.put(Utilidades.precioProducto, precioUnit);
        productos.put(Utilidades.costoProducto, costoUnit);
        productos.put(Utilidades.categoriaProducto, categoria);
        productos.put(Utilidades.descProducto, descrip);

        if (!Uri.EMPTY.equals(downloadLinkImage)) {
            fotoProducto = "";
        } else {
            fotoProducto = downloadLinkImage.toString();
        }
        //si el link de descarga está vacío, significa que no escojió una imagen para subir a firebase,
        //entonces solo guardamos un vació
        productos.put(Utilidades.imagenProducto, fotoProducto);

        BaseDeDatos.getFireDatabaseIntanceReference().child(Utilidades.nodoPadre).child(Utilidades.nodoProducto).push()
                .updateChildren(productos).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        productProgressDialog().dismiss();
                        //notificamos dentro del alert dialog que el producto se registró,
                        //mediante la interfaz que se ha creado para dicho propósito
                        productAlertDialog("EXITO", "Se añadió el producto al registro.");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        productProgressDialog().dismiss();
                        productAlertDialog("ERROR", "Ocurrió un error al registrar el producto." +
                                "Intentelo nuevamente.");
                    }
                });
        Log.d(TAG, "Datos Guardados");
    }

    private void cargarDatosProductosParaEdicion() {

        Log.d(TAG, "PresentadorProductCreation --------------------------------- ");
        Log.d(TAG, "Nombre: " + producto.getNombre());
        Log.d(TAG, "Cantidad: " + producto.getCantidad());
        Log.d(TAG, "Precio: " + producto.getPrecio());
        Log.d(TAG, "Costo: " + producto.getCosto());
        Log.d(TAG, "Descrip: " + producto.getDescripcion());
        Log.d(TAG, "CobBar: " + producto.getCodBarras());
        Log.d(TAG, "ImagenURL: " + producto.getImagen());
        Log.d(TAG, "-----------------------------------------------------------");
        inputEdCodBarras.setText(producto.getCodBarras());
        inputEdCantDisponible.setText(String.valueOf(producto.getCantidad()));
        inputEdNombreProducto.setText(producto.getNombre());
        inputEdPrecioUnit.setText(String.valueOf(producto.getPrecio()));
        inputEdCostoUnit.setText(String.valueOf(producto.getCosto()));
        //categoria.setText(producto.getCategoria());
        inputEdDescripcion.setText(producto.getDescripcion());
        cargarImagenFirebase(producto.getImagen());

    }

    private void actualizarDatos() {
        if (this.filePath != null) {
            actualizarDatosProductoConImagen();
        } else {
            actualizarDatosProducto(null);
        }
    }

    private void actualizarDatosProductoConImagen() {
        Log.d(TAG, "Borrando imagen: " + Uri.parse(this.producto.getImagen()));

        if (productProgressDialog().isShowing()) {
            productProgressDialog().dismiss();
        } else {
            productProgressDialog().setMessage("Actualizando producto...");
            productProgressDialog().show();
        }

        String imagenNonbre = "";
        Calendar fecha = null;
        fecha = GregorianCalendar.getInstance();
        //si no tiene foto guardada aún el la bd
        if (this.producto.getImagen().isEmpty()) {
            imagenNonbre = this.filePath.getLastPathSegment() + fecha.getTimeInMillis();
        } else {
            //obtiene el nombre de la imagen guardada en el storage de firebase
            //si existe la imagen, entonces la actualizará
            imagenNonbre = FirebaseStorage.getInstance().getReferenceFromUrl(this.producto.getImagen()).getName();
        }

        final StorageReference fotoRef = BaseDeDatos.getFireStorageInstanceReference().child(Utilidades.nodoStorageFotosProductos).child(imagenNonbre);
        UploadTask uploadTask = fotoRef.putFile(this.filePath);
        Uri finalRutaImagen = this.filePath;
        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    Log.d(TAG, "SUBIDA DE IMAGEN FALLIDA");
                    throw new Exception();
                }
                Log.d(TAG, "SE SUBIO LA IMAGEN: ");
                //retornamos el link de descarga se la imagen que acabamos de subir
                return fotoRef.getDownloadUrl();
            }
        }).addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                   /* Log.d(TAG, "ONCOMPLETE: " + uri.toString());
                    Log.d(TAG, "ONCOMPLETE: " + uri.getPath());
                    Log.d(TAG, "ONCOMPLETE: " + uri.getScheme());
                    Log.d(TAG, "ONCOMPLETE: " + uri.getLastPathSegment());*/
                //lanzo la interfaz para que llame a guardar los datos de los produtos.
                //si llamo aquí el método, por alguna razó la aplicación se congela,
                //pero si lo activo mediante esta interfaz funciona bien
                imagenCargada(uri, finalRutaImagen);
            }
        });
    }

    private void actualizarDatosProducto(Uri rutaImagen) {
        if (productProgressDialog().isShowing()) {
            productProgressDialog().dismiss();
        } else {
            productProgressDialog().setMessage("Actualizando producto...");
            productProgressDialog().show();
        }

        String nombre, descrip, codBarras, categoria;
        int cantidad;
        float precioUnit, costoUnit;

        codBarras = inputEdCodBarras.getText().toString().trim();
        nombre = inputEdNombreProducto.getText().toString().trim();
        cantidad = inputEdCantDisponible.getText().toString().trim().isEmpty() ? 0 : Integer.parseInt(inputEdCantDisponible.getText().toString().trim());
        precioUnit = Float.parseFloat(inputEdPrecioUnit.getText().toString().trim());
        costoUnit = Float.parseFloat(inputEdCostoUnit.getText().toString().trim());
        descrip = inputEdDescripcion.getText().toString().trim();
        categoria = autoCompletTextSpCategoria.getText().toString().trim();

        Log.d(TAG, "Actualizando datos");
        Map<String, Object> productos = new HashMap<>();
        productos.put(Utilidades.codBarrasProducto, codBarras);
        productos.put(Utilidades.nombreProducto, nombre);
        productos.put(Utilidades.cantProducto, cantidad);
        productos.put(Utilidades.precioProducto, precioUnit);
        productos.put(Utilidades.costoProducto, costoUnit);
        productos.put(Utilidades.categoriaProducto, categoria);
        productos.put(Utilidades.descProducto, descrip);

        //Log.d(TAG, "Guardando datos--->URL: " + rutaImagen == null ? "sin Ruta" : rutaImagen.toString());
        //si el link de descarga está vacío, significa que no escojió una imagen para subir a firebase,
        if (rutaImagen != null) {
            productos.put(Utilidades.imagenProducto, rutaImagen.toString());
        }

        BaseDeDatos.getFireDatabaseIntanceReference().child(Utilidades.nodoPadre).child(Utilidades.nodoProducto).child(this.producto.getProductoFirebaseKey())
                .updateChildren(productos).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        //notificamos con la interfaz de que debe borrarse el Uri filePath que contiene
                        //la ruta de la imagen en el dispositivo una vez la hayamos subido a firebase
                        productProgressDialog().dismiss();
                        //dentro del alert llamo la interfaz que ayuda a verificar si el regsitro se logró
                        //mediante una interfaz
                        productAlertDialog(Utilidades.alertDialog_EXITO, "Se actualizó el registro.");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        productProgressDialog().dismiss();
                        productAlertDialog(Utilidades.alertDialog_ERROR, "Ocurrió un error al actualizar el producto." +
                                "Intentelo nuevamente.");
                    }
                });
        Log.d(TAG, "Datos Actualizados");
    }

    private void cargarImagenFirebase(String URLImagen) {
        //Log.d(TAG, "Cargando Imagen: " + URLImagen);
        if (!URLImagen.isEmpty()) {
            //Log.d("ADAPTER_PRODUCTOS", "Imagen no es vacia");
            Glide
                    .with(mContext.getApplicationContext())
                    .load(URLImagen)
                    .centerInside()
                    .fitCenter()
                    .listener(new RequestListener<Drawable>() {

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            //Log.d(TAG, "Imagen cargada");
                            //progressBarFoto.setVisibility(View.GONE);
                            return false;
                        }

                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            //Log.d(TAG, "Imagen error al cargar");
                            //progressBarFoto.setVisibility(View.GONE);
                            imgv_ImagenProducto.setImageResource(R.drawable.baseline_error_red_48dp);
                            return false;
                        }

                    })
                    .into(imgv_ImagenProducto);

        } else {
            Log.d(TAG, "Imagen URL vacia");
            //progressBarFoto.setVisibility(View.GONE);
            imgv_ImagenProducto.setImageResource(R.drawable.baseline_camera_alt_green_black_48dp);
        }
    }

    private void cargarCategoriasProductos(){
        BaseDeDatos.getFireDatabaseIntanceReference().child(Utilidades.nodoPadre).child(Utilidades.nodoCategoria).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<String> listaCateg = new ArrayList<>();

                for (DataSnapshot datos : dataSnapshot.getChildren()) {
                    // Log.d(TAG, "dataSnashot: "+datos.getKey());
                    Producto p = datos.getValue(Producto.class);
                        listaCateg.add(p.getCategoria());
                        Log.d(TAG, "PresentadorProductDescription --------------------------------- ");
                        Log.d(TAG, "Categoria: " + p.getCategoria());
                        Log.d(TAG, "-----------------------------------------------------------");
                }
                ArrayAdapter arrayAdapter = new ArrayAdapter(mContext, R.layout.custom_text_view_dropdown_menu_producto_categoria, listaCateg);
                autoCompletTextSpCategoria.setAdapter(arrayAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.product_creation_imgbQuitarImagen:
                cargarImagenFirebase(this.producto.getImagen());
                this.filePath = null;
                break;

            case R.id.product_creation_imgbCrearCategoria:
                new PresenterCustomDialogCrearCategoria(this.mContext);
                /*Dialog dialog = new Dialog(mContext);
                dialog.setContentView(R.layout.custom_dialog_crear_categoria);
                dialog.setCancelable(true);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();*/
                break;
        }
    }

    @Override
    public void imagenCargada(Uri downloadLinkImage, Uri rutaImagen) {
        Log.d(TAG, "Interface imagencargada: " + downloadLinkImage.toString());
        if (bundleProducto != null) {
            actualizarDatosProducto(downloadLinkImage);
        } else {
            guardarDatosProductosFireBase(downloadLinkImage, rutaImagen);
        }
    }

    private void limpiarCampos() {
        inputEdCodBarras.setText("");
        inputEdCantDisponible.setText("0");
        inputEdNombreProducto.setText("");
        inputEdPrecioUnit.setText("");
        inputEdCostoUnit.setText("");
        inputEdDescripcion.setText("");
        imgv_ImagenProducto.setImageResource(R.drawable.baseline_camera_alt_green_black_48dp);
        autoCompletTextSpCategoria.setText("Seleccione...");

    }

}
