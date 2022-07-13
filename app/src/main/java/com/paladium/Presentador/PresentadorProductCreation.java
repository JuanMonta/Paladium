package com.paladium.Presentador;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.paladium.Model.Firebase.BaseDeDatos;
import com.paladium.Model.Utils.Utilidades;
import com.paladium.R;

import java.io.File;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

public class PresentadorProductCreation implements View.OnClickListener, InterfacePresenter_ProductCreation.onImagenCargada {
    private final String TAG = "PresenterProdCreation";
    private Context mContext;
    private TextInputEditText edCodBarras, edCantDisponible, edNombreProducto,
            edPrecioUnit, edCostoUnit, edDescripcion;

    private AutoCompleteTextView spCategoria;

    private InterfacePresenter_ProductCreation.onImagenCargada interfaceImagenCargada;
    private InterfacePresenter_ProductCreation.onProductoCargado interfaceProductoCargado;

    public PresentadorProductCreation(Context context, InterfacePresenter_ProductCreation.onProductoCargado interfaceProductoCargado) {
        this.mContext = context;
        this.interfaceProductoCargado = interfaceProductoCargado;
    }


    public void init(View view) {
        interfaceImagenCargada = this;
        edCodBarras = view.findViewById(R.id.product_creation_edCodBarras);
        edCantDisponible = view.findViewById(R.id.product_creation_edCantidadDisponible);
        edNombreProducto = view.findViewById(R.id.product_creation_edNombreProducto);
        edPrecioUnit = view.findViewById(R.id.product_creation_edPrecioUnitario);
        edCostoUnit = view.findViewById(R.id.product_creation_edCostoUnitario);
        edDescripcion = view.findViewById(R.id.product_creation_edDescripcionProducto);
        spCategoria = view.findViewById(R.id.product_creation_spCategoria);

    }

    /**
     * Mensaje ha mostrar cuando subidos un producto a firebase, informandonos si
     * se ha subido o no el producto.
     * @param tiutloDialog título que tendrá la alerta
     * @param mensaje mensaje de la alerta
     */
    private void productAlertDialog(String tiutloDialog, String mensaje) {
        AlertDialog.Builder ok_or_error_dialog = new AlertDialog.Builder(mContext);
        ok_or_error_dialog.setTitle(tiutloDialog);
        ok_or_error_dialog.setMessage(mensaje);
        ok_or_error_dialog.setCancelable(false);
        ok_or_error_dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        ok_or_error_dialog.show();
    }

    /**
     * Para registrar un producto en firebase, comenzando primeramente con la imagen del producto,
     * luego con los datos del producto.
     * @param rutaImagen Uri de la imagen en el dispositivo que será usada para subirse a firebase
     */
    public void registrarProducto(Uri rutaImagen) {
        //nombre de como se guardará la imagen en firebase
        String imagenNonbre = "";
        Calendar fecha = null;
        fecha = GregorianCalendar.getInstance();
        //si tiene foto seleccionada
        if (rutaImagen != null) {
            imagenNonbre = rutaImagen.getLastPathSegment() + fecha.getTimeInMillis();

            final StorageReference fotoRef = BaseDeDatos.getFireStorageReference().child(Utilidades.nodoStorageFotosProductos).child(imagenNonbre);
            UploadTask uploadTask = fotoRef.putFile(rutaImagen);
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
                    imagenCargada(uri, rutaImagen);
                }
            });

        } else {//si no ha seleccionado ninguna imagen
            guardarDatosProductosFireBase(null, rutaImagen);
        }

    }

    /**
     * Despues de que se haya subido la imagen del producto a firebase y obtenido su
     * respectivo link de descarga(si fuere el caso de tener imagen),
     * procedemos a guardar el resto de los datos del producto
     * @param downloadLinkImage
     * @param rutaImagen
     */
    private void guardarDatosProductosFireBase(Uri downloadLinkImage, Uri rutaImagen) {
        ProgressDialog progressDialog = new ProgressDialog(mContext);
        progressDialog.setMessage("Añadiendo producto...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        String nombre, descrip, codBarras;
        int cantidad;
        float precioUnit, costoUnit;

        codBarras = edCodBarras.getText().toString().trim();
        nombre = edNombreProducto.getText().toString().trim();
        cantidad = Integer.parseInt(edCantDisponible.getText().toString().trim());
        precioUnit = Float.parseFloat(edPrecioUnit.getText().toString().trim());
        costoUnit = Float.parseFloat(edCostoUnit.getText().toString().trim());
        descrip = edDescripcion.getText().toString().trim();

        Log.d(TAG, "Guardando datos");
        Map<String, Object> productos = new HashMap<>();
        productos.put(Utilidades.codBarrasProducto, codBarras);
        productos.put(Utilidades.nombreProducto, nombre);
        productos.put(Utilidades.cantProducto, cantidad);
        productos.put(Utilidades.precioProducto, precioUnit);
        productos.put(Utilidades.costoProducto, costoUnit);
        productos.put(Utilidades.descProducto, descrip);
        Log.d(TAG, "Guardando datos--->URL: " + downloadLinkImage == null ? "sin Ruta" : downloadLinkImage.toString());
        //si el link de descarga está vacío, significa que no escojió una imagen para subir a firebase,
        //entonces solo guardamos un vació
        productos.put(Utilidades.imagenProducto, downloadLinkImage == null ? "" : downloadLinkImage.toString());

        BaseDeDatos.getFireDatabase().child(Utilidades.nodoPadre).child(Utilidades.nodoProducto).push()
                .updateChildren(productos).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        //una vez subido el producto borramos la imagen que está guardad en el
                        //dispositivo
                        if (rutaImagen != null) {
                            Log.d(TAG, "Borrando Imagen, producto guardado: " + rutaImagen);
                            new File(rutaImagen.getPath()).delete();
                        }
                        //notificamos con la interfaz de que debe borrarse el Uri filePath que contiene
                        //la ruta de la imagen en el dispositivo una vez la hayamos subido a firebase
                        interfaceProductoCargado.productoCargado(true);
                        progressDialog.dismiss();
                        productAlertDialog("EXITO", "Se añadió el producto al registro.");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        productAlertDialog("ERROR", "Ocurrió un error al registrar el producto." +
                                "Intentelo nuevamente.");
                    }
                });
        Log.d(TAG, "Datos Guardados");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

        }
    }


    @Override
    public void imagenCargada(Uri downloadLinkImage, Uri rutaImagen) {
        Log.d(TAG, "Interface imagencargada: " + downloadLinkImage.toString());
        guardarDatosProductosFireBase(downloadLinkImage, rutaImagen);
    }
}
