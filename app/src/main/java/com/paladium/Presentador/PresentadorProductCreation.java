package com.paladium.Presentador;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.paladium.Model.Firebase.BaseDeDatos;
import com.paladium.Model.Logica.Producto;
import com.paladium.Model.Utils.Utilidades;
import com.paladium.Presentador.Customs.PresenterCustomDialog;
import com.paladium.Presentador.Customs.PresenterCustomDialogCrearCategoria;
import com.paladium.Presentador.Interfaces.InterfacePresenter_ProductCreation;
import com.paladium.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

public class PresentadorProductCreation implements View.OnClickListener, InterfacePresenter_ProductCreation.onImagenCargada
        {
    private final String TAG = "PresenterProdCreation";
    private Context mContext;
    private Uri filePath;
    private TextInputEditText inputEdCodBarras, inputEdCantDisponible, inputEdNombreProducto,
            inputEdPrecioUnit, inputEdCostoUnit, inputEdDescripcion;
    private TextInputLayout inputLayoutCodBarras, inputLayoutCantDisponible, inputLayoutNombreProducto,
            inputLayoutPrecioUnit, inputLayoutCostoUnit;

    private ImageButton imgBtnQuitarImagen, imgBtnCrearCategoria;
    private ImageView imgv_ImagenProducto;
    private AutoCompleteTextView autoCompletTextSpCategoria;
    private InterfacePresenter_ProductCreation.onImagenCargada interfaceImagenCargada;
    private InterfacePresenter_ProductCreation.onProductoCargado interfaceProductoCargado;
    private Bundle bundleProducto;
    private Producto producto;
    private ProgressDialog progressDialog;
    /**
     * pos[0] = codigoBarras; pos[1] = nombreProducto;
     */
    private ArrayList<String[]> listaCod_Barras_Nombre;
    private ArrayList<String[]> listaProductosRepetidos;

    public PresentadorProductCreation(Context context, InterfacePresenter_ProductCreation.onProductoCargado interfaceProductoCargado
            , Bundle bundleProducto) {
        this.mContext = context;
        this.interfaceProductoCargado = interfaceProductoCargado;
        this.bundleProducto = bundleProducto;
        progressDialog = new ProgressDialog(mContext.getApplicationContext());
    }


    public void init(View view) {
        interfaceImagenCargada = this;

        inputLayoutCodBarras = view.findViewById(R.id.product_creation_textInputLayout_CodBarras);
        inputEdCodBarras = view.findViewById(R.id.product_creation_edCodBarras);

        inputLayoutCantDisponible = view.findViewById(R.id.product_creation_textInputLayout_CantidadDisponible);
        inputEdCantDisponible = view.findViewById(R.id.product_creation_edCantidadDisponible);

        inputLayoutNombreProducto = view.findViewById(R.id.product_creation_textInputLayout_NombreProducto);
        inputEdNombreProducto = view.findViewById(R.id.product_creation_edNombreProducto);

        inputLayoutPrecioUnit = view.findViewById(R.id.product_creation_textInputLayout_PrecioUnitario);
        inputEdPrecioUnit = view.findViewById(R.id.product_creation_edPrecioUnitario);

        inputLayoutCostoUnit = view.findViewById(R.id.product_creation_textInputLayout_CostoUnitario);
        inputEdCostoUnit = view.findViewById(R.id.product_creation_edCostoUnitario);

        inputEdDescripcion = view.findViewById(R.id.product_creation_edDescripcionProducto);

        autoCompletTextSpCategoria = view.findViewById(R.id.product_creation_spCategoria);

        imgv_ImagenProducto = view.findViewById(R.id.product_creation_Imgv_Producto);

        imgBtnQuitarImagen = view.findViewById(R.id.product_creation_imgbQuitarImagen);
        imgBtnQuitarImagen.setOnClickListener(this);
        imgBtnCrearCategoria = view.findViewById(R.id.product_creation_imgbCrearCategoria);
        imgBtnCrearCategoria.setOnClickListener(this);


        //String[] categorias = mContext.getResources().getStringArray(R.array.array_producto_categoria_de_prueba);
        //ArrayAdapter arrayAdapter = new ArrayAdapter(mContext, R.layout.custom_text_view_dropdown_menu_producto_categoria, categorias);
        //autoCompletTextSpCategoria.setAdapter(arrayAdapter);

        cargarCategorias_CodBar_Nombre_Productos();
        textWatcherInputEditText();

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
     * @param mensaje   mensaje de la alerta
     */
    private void productAlertDialog(String tipoAlert, String mensaje) {

        PresenterCustomDialog dialog = new PresenterCustomDialog(this.mContext);

        if (tipoAlert.equals(Utilidades.alertDialog_EXITO)) {
            dialog.dialogSuccess(mensaje, interfaceProductoCargado);
            limpiarCampos();
            this.validarCamposInputTextWacher = 0;
            this.listaProductosRepetidos = null;
        }
        if (tipoAlert.equals(Utilidades.alertDialog_ACTUALIZADO)) {
            dialog.dialogSuccess(mensaje, interfaceProductoCargado);
        }
        if (tipoAlert.equals(Utilidades.alertDialog_ERROR)) {
            dialog.dialogError(mensaje);
        }
        if (tipoAlert.equals(Utilidades.alertDialog_ADVERTENCIA)) {
        }
        if (tipoAlert.equals(Utilidades.alertDialog_ATENCION)) {
        }
        if (tipoAlert.equals(Utilidades.alertDialog_PRODUCTO_REPETIDO)) {

        }
    }

    /**
     * Para registrar un producto en firebase, comenzando primeramente con la imagen del producto,
     * luego con los datos del producto.
     *
     * @param rutaImagen Uri de la imagen en el dispositivo que será usada para subirse a firebase
     */
    public void registrarProducto(Uri rutaImagen) {
        if (verificarCampos() == 0) {
            this.filePath = rutaImagen;
            //es una edicion
            if (bundleProducto != null) {
                actualizarDatos();
            } else {//es registro
                //nombre de como se guardará la imagen en firebase
                registrarProductoConOSinImagenFirebase();
            }
        }
    }

    private void registrarProductoConOSinImagenFirebase() {
        if (productProgressDialog().isShowing()) {
            productProgressDialog().dismiss();
        } else {
            productProgressDialog().setMessage(mContext.getString(R.string.progressdialog_crear_producto_mensaje));
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
            productProgressDialog().setMessage(mContext.getString(R.string.progressdialog_crear_producto_mensaje));
            productProgressDialog().show();
        }

        String nombre, descrip, codBarras, fotoProducto, categoria;
        int cantidad;
        float precioUnit, costoUnit;

        codBarras = inputEdCodBarras.getText().toString().trim().toUpperCase();
        nombre = inputEdNombreProducto.getText().toString().trim().toUpperCase();
        cantidad = inputEdCantDisponible.getText().toString().trim().isEmpty() ? 0 : Integer.parseInt(inputEdCantDisponible.getText().toString().trim());
        precioUnit = Float.parseFloat(inputEdPrecioUnit.getText().toString().trim());
        costoUnit = Float.parseFloat(inputEdCostoUnit.getText().toString().trim());
        descrip = inputEdDescripcion.getText().toString().trim();
        categoria = autoCompletTextSpCategoria.getText().toString().trim().toUpperCase();

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
            Log.d(TAG, "Guardando datos Uri no es Vacio");
            fotoProducto = downloadLinkImage.toString();
        } else {
            Log.d(TAG, "Guardando datos Uri está vacío");
            fotoProducto = "";
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
                        productAlertDialog(Utilidades.alertDialog_EXITO, mContext.getString(R.string.alertdialog_crear_producto_mensaje_EXITO));
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        productProgressDialog().dismiss();
                        productAlertDialog(Utilidades.alertDialog_ERROR, mContext.getString(R.string.alertdialog_crear_producto_mensaje_ERROR));
                    }
                });
        Log.d(TAG, "Datos Guardados");
    }

    private void cargarDatosProductosParaEdicion() {

       /* Log.d(TAG, "PresentadorProductCreation --------------------------------- ");
        Log.d(TAG, "Nombre: " + producto.getNombre());
        Log.d(TAG, "Cantidad: " + producto.getCantidad());
        Log.d(TAG, "Precio: " + producto.getPrecio());
        Log.d(TAG, "Costo: " + producto.getCosto());
        Log.d(TAG, "Categoria: " + producto.getCategoria());
        Log.d(TAG, "Descrip: " + producto.getDescripcion());
        Log.d(TAG, "CobBar: " + producto.getCodBarras());
        Log.d(TAG, "ImagenURL: " + producto.getImagen());
        Log.d(TAG, "-----------------------------------------------------------");*/
        inputEdCodBarras.setText(producto.getCodBarras());
        inputEdCantDisponible.setText(String.valueOf(producto.getCantidad()));
        inputEdNombreProducto.setText(producto.getNombre());
        inputEdPrecioUnit.setText(String.valueOf(producto.getPrecio()));
        inputEdCostoUnit.setText(String.valueOf(producto.getCosto()));
        //categoria.setText(producto.getCategoria());
        inputEdDescripcion.setText(producto.getDescripcion());
        autoCompletTextSpCategoria.setText(producto.getCategoria(), false);
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
            productProgressDialog().setMessage(mContext.getString(R.string.progressdialog_actualizar_producto_mensaje));
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
            //si existe la imagen, entonces la actualizará, caso contrario la crea
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
            productProgressDialog().setMessage(mContext.getString(R.string.progressdialog_actualizar_producto_mensaje));
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

        Log.d(TAG, "Actualizando datos INIT");
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
                        productAlertDialog(Utilidades.alertDialog_ACTUALIZADO, mContext.getString(R.string.alertdialog_actualizar_producto_mensaje_EXITO));
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        productProgressDialog().dismiss();
                        productAlertDialog(Utilidades.alertDialog_ERROR, mContext.getString(R.string.alertdialog_actualizar_producto_mensaje_ERROR));
                    }
                });
        Log.d(TAG, "Actualizando datos FIN");
    }

    private void cargarImagenFirebase(String URLImagen) {
        //Log.d(TAG, "Cargando Imagen: " + URLImagen);
        if (URLImagen != null && !URLImagen.isEmpty()) {
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
                            imgv_ImagenProducto.setImageResource(R.drawable.baseline_error_red_24dp);
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

    private void cargarCategorias_CodBar_Nombre_Productos() {
        BaseDeDatos.getFireDatabaseIntanceReference()
                .child(Utilidades.nodoPadre)
                .child(Utilidades.nodoCategoria)
                .addValueEventListener(new ValueEventListener() {
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


        BaseDeDatos.getFireDatabaseIntanceReference()
                .child(Utilidades.nodoPadre)
                .child(Utilidades.nodoProducto)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        listaCod_Barras_Nombre = new ArrayList<>();

                        for (DataSnapshot datos : dataSnapshot.getChildren()) {
                            // Log.d(TAG, "dataSnashot: "+datos.getKey());
                            Producto p = datos.getValue(Producto.class);
                            String[] data =
                                    {
                                            p != null ? p.getCodBarras() : "",
                                            p != null ? p.getNombre() : ""
                                    };
                            listaCod_Barras_Nombre.add(data);
                            /*Log.d(TAG, "Categoria: " + p.getCategoria());
                            Log.d(TAG, "PresentadorProductDescription --------------------------------- ");
                            Log.d(TAG, "-----------------------------------------------------------");*/
                        }
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

    @Override
    public void onGuardarProductorepetido(String guardarDeTodasFormas) {
        if (guardarDeTodasFormas.equals(Utilidades.alertDialog_PRODUCTO_REPETIDO_GUARDAR_DE_TODAS_FORMAS)){
            //borramos los validadores colocados en textWatcherInputEditText()
            //para que puedan pasar a guardar el producto de todas formas cuando existan agunas coincidencias
            //con otros productos.
            //Esta interfaz se llama en la Activity ProductCreation para llamar de nuevo al método
            //que guarda el producto
            this.validarCamposInputTextWacher = 0;
            this.listaProductosRepetidos = null;
        }
    }

    private void limpiarCampos() {
        //Log.d(TAG,"Limpiando campos");
        inputEdCodBarras.setText("");
        inputEdCantDisponible.setText("");
        inputEdNombreProducto.setText("");
        inputEdPrecioUnit.setText("");
        inputEdCostoUnit.setText("");
        inputEdDescripcion.setText("");
        imgv_ImagenProducto.setImageResource(R.drawable.baseline_camera_alt_green_black_48dp);
        //añadiendo false se evita que borre la lista de categorias cargadas anteriormente
        autoCompletTextSpCategoria.setText(mContext.getApplicationContext().getString(R.string.seleccione), false);

        inputEdCodBarras.setError(null);

        inputEdCantDisponible.setError(null);
        inputLayoutCantDisponible.setEndIconVisible(true);

        inputEdNombreProducto.setError(null);
        inputEdPrecioUnit.setError(null);
        inputEdCostoUnit.setError(null);
        inputEdDescripcion.setError(null);

        autoCompletTextSpCategoria.setText(mContext.getApplicationContext().getString(R.string.seleccione), false);
        autoCompletTextSpCategoria.setError(null);
    }

    private int verificarCampos() {

        int validar = 0;

        if (inputEdCantDisponible.getText().toString().length() == 0) {
            inputLayoutCantDisponible.setEndIconVisible(false);
            inputEdCantDisponible.setError(mContext.getString(R.string.producto_input_error_cantidad));
            validar++;
        }
        if (inputEdNombreProducto.getText().toString().length() == 0) {
            inputEdNombreProducto.setError(mContext.getString(R.string.producto_input_error_nombre));
            validar++;
        }
        if (inputEdPrecioUnit.getText().toString().length() == 0) {
            inputEdPrecioUnit.setError(mContext.getString(R.string.producto_input_error_precio));
            validar++;
        }
        if (inputEdCostoUnit.getText().toString().length() == 0) {
            inputEdCostoUnit.setError(mContext.getString(R.string.producto_input_error_costo));
            validar++;
        }

        if (autoCompletTextSpCategoria.getText().toString().trim().equals(mContext.getString(R.string.seleccione))) {
            autoCompletTextSpCategoria.setError(mContext.getString(R.string.producto_input_error_categoria));
            validar++;
        }

        return validar;
    }

    private int verificarCamposInputTextWatcher(){
        return this.validarCamposInputTextWacher;
    }

    int validarCamposInputTextWacher;

    private int textWatcherInputEditText() {

        inputEdCodBarras.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                inputEdCodBarras.setError(null);
                inputLayoutCodBarras.setEndIconVisible(true);
                if (s.toString().length() > 0) {

                    for (int i = 0; i < listaCod_Barras_Nombre.size(); i++) {
                        //cuando el buldle es nulo indico que no es una actualizacion de datos del producto
                        if (bundleProducto == null) {
                            if (s.toString().toUpperCase().trim().equals(listaCod_Barras_Nombre.get(i)[0].trim().toUpperCase())) {
                                inputLayoutCodBarras.setEndIconVisible(false);
                                inputEdCodBarras.setError(mContext.getString(R.string.producto_input_error_codBarras));
                                validarCamposInputTextWacher++;
                            }
                        }
                    }
                }
            }
        });

        inputEdCantDisponible.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int count, int after) {
                if (charSequence.length() == 0) {
                    inputLayoutCantDisponible.setEndIconVisible(false);
                    inputEdCantDisponible.setError((mContext.getString(R.string.producto_input_error_cantidad)));
                } else {
                    inputLayoutCantDisponible.setEndIconVisible(true);
                    inputEdCantDisponible.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        inputEdNombreProducto.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() == 0) {
                    inputLayoutNombreProducto.setEndIconVisible(false);
                    inputEdNombreProducto.setError((mContext.getString(R.string.producto_input_error_nombre)));
                } else {
                    inputLayoutNombreProducto.setEndIconVisible(true);
                    inputEdNombreProducto.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                //cuando el buldle es nulo indico que no es una actualizacion de datos del producto
                if (bundleProducto == null) {
                    comprobarProductosRepetidos(editable.toString());
                }
            }
        });

        inputEdPrecioUnit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() == 0) {
                    inputLayoutPrecioUnit.setEndIconVisible(false);
                    inputEdPrecioUnit.setError((mContext.getString(R.string.producto_input_error_precio)));
                } else {
                    inputLayoutPrecioUnit.setEndIconVisible(true);
                    inputEdPrecioUnit.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        inputEdCostoUnit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() == 0) {
                    inputLayoutCostoUnit.setEndIconVisible(false);
                    inputEdCostoUnit.setError((mContext.getString(R.string.producto_input_error_costo)));
                } else {
                    inputLayoutCostoUnit.setEndIconVisible(true);
                    inputEdCostoUnit.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        autoCompletTextSpCategoria.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() == 0) {
                    autoCompletTextSpCategoria.setError((mContext.getString(R.string.producto_input_error_categoria)));
                } else {
                    autoCompletTextSpCategoria.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        autoCompletTextSpCategoria.setOnItemClickListener((adapterViewParent, view, pos, l) -> Log.d(TAG, "selection: pos " + pos));

        return validarCamposInputTextWacher;
    }

    private void comprobarProductosRepetidos(String nombreProducto) {
        //para contar las coincidencias de los nombres de un producto
        //con el que es ingresado en el inputEd del nombre del producto
        int contarCoincidencias = 0;
        //máximo de item para la lista de nombres con coincidencias
        int MAX_ITEMS = 5;
        //lista para guardar los nombres de productos que ha coincidido con el texto ingresado
        //en el inputEd del nombre del producto
        ArrayList<String[]> listaCoincidencias = new ArrayList<>();
        //cuando haya más de 2 caracteres ingresados en el input Ed
        if (nombreProducto.trim().length() >= 3) {
            //guardo el textoingresado
            String nombreIngresadoEdText = nombreProducto.toString().trim();
            //separo las palabras que contenga, libre de espacios vacios
            String[] stringSplitDePalabras = nombreIngresadoEdText.trim().split("\\s+");
            //borro para reasignar la nueva palabra
            nombreIngresadoEdText = "";
            for (int i = 0; i < stringSplitDePalabras.length; i++) {
                //concateno todas las palabras que ahora no tienen espacios
                nombreIngresadoEdText = nombreIngresadoEdText + stringSplitDePalabras[i];
            }

            for (int i = 0; i < listaCod_Barras_Nombre.size(); i++) {

                Log.d(TAG, "-----------------------------------------------------------");
                //tomo el nombre del producto guardado en la lista
                String nombreEnLista = listaCod_Barras_Nombre.get(i)[1];
                //separo las palabras que contenga, libre de espacios vacios
                String[] stringSplitNombreLista = nombreEnLista.trim().split("\\s+");
                //borro para reasignar la nueva palabra
                nombreEnLista = "";
                for (int j = 0; j < stringSplitNombreLista.length; j++) {
                    //concateno todas las palabras que ahora no tienen espacios
                    nombreEnLista = nombreEnLista + stringSplitNombreLista[j];
                }
                //mientras el texto ingresado sea menor o igual al de la lista
                if (nombreIngresadoEdText.length() <= nombreEnLista.length()) {
                    Log.d(TAG, "nombreIngresadoEdText:" + nombreIngresadoEdText + " -- " + nombreIngresadoEdText.length() + " letras");
                    Log.d(TAG, "nombreEnLista #" + (i + 1) + ": " + nombreEnLista + " -- " + nombreEnLista.length() + " letras");
                    //recorro cada char del nombre del inputEd ingresado y lo comparo con el de la lista
                    for (int j = 0; j < nombreIngresadoEdText.length(); j++) {
                        Log.d(TAG, "nombreIngresadoEdText char: " + nombreIngresadoEdText.charAt(j));
                        char p1 = nombreIngresadoEdText.charAt(j);
                        char p2 = nombreEnLista.charAt(j);
                        if (String.valueOf(p1).equals(String.valueOf(p2))) {
                            //si encuentra una coincidencia que sume la variable de coincidencias
                            contarCoincidencias++;
                        }
                    }
                }
                //optengo el porcentaje de coincidencias encontradas con la palabra del nombre del producto
                float porcentajeCoincidencia = (float) contarCoincidencias / nombreEnLista.length();
                Log.d(TAG, "Numero de coincidencias palabra #" + (i + 1) + ": " + contarCoincidencias + " %: " + porcentajeCoincidencia);
                //mientras la lista sea menor al numero de items que como máximo debe contener
                if (listaCoincidencias.size() <= MAX_ITEMS) {
                    //si las coincidencias son iguales al 50% del nombre del producto,
                    //entonces que guarde el nombre del producto con el que tuvo esa mayor coincidencia
                    if (porcentajeCoincidencia >= 0.5) {
                        listaCoincidencias.add(listaCod_Barras_Nombre.get(i));
                    }
                }
                //devolvemos la variable a cero para que continue con la siguente palabra en la lista
                contarCoincidencias = 0;
            /*for (int j = 0; j < listaCoincidencias.size(); j++) {
                Log.d(TAG,"lista de coincidencias: "+listaCoincidencias.get(j)[1]);
            }*/
            }

        }

        if (listaCoincidencias.size() > 0) {
            listaProductosRepetidos = listaCoincidencias;
            validarCamposInputTextWacher++;
        }
    }


}
