package com.paladium.Presentador.Adapters;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.paladium.Model.Logica.Producto;
import com.paladium.Model.Utils.ProductoDiffCallBack;
import com.paladium.Model.Utils.Utilidades;
import com.paladium.R;

import java.util.ArrayList;

public class CustomRVAdapter_Products_List extends RecyclerView.Adapter<CustomRVAdapter_Products_List.ViewHolderListaProducts> {
    private final String TAG = "ADAPTER_PRODUCTOS";
    private ArrayList<Producto> listaProducto;
    //para calcular y actualizar los datos del recycler view
    //servirá para saber que datos han cambiado
    private ProductoDiffCallBack diffCallBack;
    private Context mContext;
    //para validar si debo o nó verificar diferencias en la old y new list
    private boolean validarCalculoDiferenciasListasProductos;

    public interface ListItemClick {
        void onListenItemClick(int itemClicado, Producto producto);
    }
    //llamado en Fragmen_Inventario, y enviado desde aquí en el holder hacia el Fragment_Inventario
    final private ListItemClick onclickListener;

    //necesito conservar la lista anterior, por lo que no lo llamo en el constructor
    public CustomRVAdapter_Products_List(ListItemClick listItemClick, Context context) {
        //this.listaProducto = listaProducto;
        this.onclickListener = listItemClick;
        this.mContext = context;
    }

    @NonNull
    @Override
    public ViewHolderListaProducts onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_list_productos, parent, false);
        return new ViewHolderListaProducts(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderListaProducts holder, int position) {
        holder.asignarDatos(listaProducto.get(position));
    }


    @Override
    public int getItemCount() {
        return this.listaProducto.size();
    }


    /**
     * Para calclular la diferencias entre lista de datos del producto antigua creada y actual enviada.
     * Tambien notifica al adaptador que debe actualizar el recycler view.
     *
     * @param newProductList la nueva lista de productos que será puesta en el recycler view
     */
    public void dataProductosChangeDiffCallUtil(ArrayList<Producto> newProductList) {
       /* Log.d("ADAPTER_PRODUCTOS", "--LISTA ANTIGUA ------------");
        if (listaProducto != null) {
            for (int i = 0; i < listaProducto.size(); i++) {
                Log.d("ADAPTER_PRODUCTOS", "Product Key: " + listaProducto.get(i).getProductoFirebaseKey());
                Log.d("ADAPTER_PRODUCTOS", "Nombre: " + listaProducto.get(i).getNombre());
                Log.d("ADAPTER_PRODUCTOS", "Cantidad: " + listaProducto.get(i).getCantidad());
                Log.d("ADAPTER_PRODUCTOS", "Precio: " + listaProducto.get(i).getPrecio());
                Log.d("ADAPTER_PRODUCTOS", "Costo: " + listaProducto.get(i).getCosto());
                Log.d("ADAPTER_PRODUCTOS", "Descrip: " + listaProducto.get(i).getDescripcion());
                Log.d("ADAPTER_PRODUCTOS", "CobBar: " + listaProducto.get(i).getCodBarras());
                Log.d("ADAPTER_PRODUCTOS", "ImagenURL: " + listaProducto.get(i).getImagen());
                Log.d("ADAPTER_PRODUCTOS", "---------------------------------------");
            }
        }*/
        //calcula las diferencias entre la lista existente (old) y la nueva que recibimos (new),
        //se envian las respectivas listas, las cuales tambien nos servirán en el ViewHolderListaProducts
        //para obtener cuales fueron los datos que cambiaron
        this.diffCallBack = new ProductoDiffCallBack();
        this.diffCallBack.setmOldList(this.listaProducto);
        this.diffCallBack.setmNewList(newProductList);
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(this.diffCallBack);
        //notificamos al recyclerView Adapter que debe actualizarse
        diffResult.dispatchUpdatesTo(this);
        //actualizamos la lista existente por la nueva
        this.listaProducto = newProductList;


       /* Log.d("ADAPTER_PRODUCTOS", "--LISTA NUEVA   ------------");
        for (int i = 0; i < newProductList.size(); i++) {
            Log.d("ADAPTER_PRODUCTOS", "Product Key: " + newProductList.get(i).getProductoFirebaseKey());
            Log.d("ADAPTER_PRODUCTOS", "Nombre: " + newProductList.get(i).getNombre());
            Log.d("ADAPTER_PRODUCTOS", "Cantidad: " + newProductList.get(i).getCantidad());
            Log.d("ADAPTER_PRODUCTOS", "Precio: " + newProductList.get(i).getPrecio());
            Log.d("ADAPTER_PRODUCTOS", "Costo: " + newProductList.get(i).getCosto());
            Log.d("ADAPTER_PRODUCTOS", "Descrip: " + newProductList.get(i).getDescripcion());
            Log.d("ADAPTER_PRODUCTOS", "CobBar: " + newProductList.get(i).getCodBarras());
            Log.d("ADAPTER_PRODUCTOS", "ImagenURL: " + newProductList.get(i).getImagen());
            Log.d("ADAPTER_PRODUCTOS", "---------------------------------------");
        }*/

    }

    public void setValidarCalculoDiferenciasListasProductos(boolean validacion) {
        this.validarCalculoDiferenciasListasProductos = validacion;
    }

    private boolean getValidarCalculoDiferenciasListasProductos() {
        return this.validarCalculoDiferenciasListasProductos;
    }

    public class ViewHolderListaProducts extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView productoNombre, productoPrecio, productoDesc, productoCant;
        private ImageView productoFoto;
        private ProgressBar progressBarFoto;
        private Producto producto;

        public ViewHolderListaProducts(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            this.productoNombre = itemView.findViewById(R.id.custom_list_productos_tvNobre);
            this.productoPrecio = itemView.findViewById(R.id.custom_list_productos_tvPrecio);
            this.productoCant = itemView.findViewById(R.id.custom_list_productos_tvCantidad);
            this.productoFoto = itemView.findViewById(R.id.custom_list_productos_igvFoto);
            this.progressBarFoto = itemView.findViewById(R.id.custom_list_productos_ProgressBarImage);
        }


        void asignarDatos(Producto producto) {
            this.producto = producto;
            this.productoNombre.setText(producto.getNombre() == null ? "Nombre no disponible" : producto.getNombre());
            this.productoCant.setText(producto.getCantidad() > 0 ? "" + producto.getCantidad() : "Producto no disponible");
            this.productoPrecio.setText(producto.getPrecio() > 0 ? "" + producto.getPrecio() : "Precio no disponible");
            cargarImagenFirebase(producto.getImagen());
            //con esto valido si debe empezaar a buscar diferencias, cuando es false he validado
            //que es cuadno un producto ha sido eliminado un producto
            //(verificar Fragment_Inventario.cargarDatosProductosRecycler)
            if (getValidarCalculoDiferenciasListasProductos()) {
                //muestra las diferencias que hay entre la lista anterior y la nueva que se ha cargado.
                //los datos de las difrencias se mostraran cuando el sistema propio de recyclerview cargue
                //los items respectivos del producto, es decir, si en el momento que se actualiza, el item que ha sido
                //actualizado no esta algo visible en pantalla, entonces no caragará este item, pues así funciona
                //el recycler view, optimiza las lista, solo muestra los items cuando en verdad se verán en pantalla.
                Bundle diferencias = (Bundle) diffCallBack.getChangePayload(getAdapterPosition(), getAdapterPosition());
                if (diferencias != null) {
                    for (String bundleKey : diferencias.keySet()) {

                        if (bundleKey.equals(Utilidades.nombreProducto)) {
                            Log.d("ADAPTER_PRODUCTOS", "Nombre a cambiado");
                            blinkEffect(this.productoNombre);
                            //actualizamos el campo de la lista old para evitar que el efecto blink
                            //que se ha creado para las vistas cuando cambian o actulizen de valores
                            //no se lance continuamente cada vez que se se ponga en pantalla el item
                            //de la lista con dicho view que contiene el cambio, por ello actualizamos
                            //el datos que hemos visto que cambió
                            diffCallBack.getmOldList().get(getAdapterPosition()).setNombre(producto.getNombre());
                        }
                        if (bundleKey.equals(Utilidades.cantProducto)) {
                            Log.d("ADAPTER_PRODUCTOS", "Cantidad a cambiado");
                            blinkEffect(this.productoCant);
                            diffCallBack.getmOldList().get(getAdapterPosition()).setCantidad(producto.getCantidad());
                        }
                        if (bundleKey.equals(Utilidades.precioProducto)) {
                            Log.d("ADAPTER_PRODUCTOS", "Precio a cambiado");
                            blinkEffect(this.productoPrecio);
                            diffCallBack.getmOldList().get(getAdapterPosition()).setPrecio(producto.getPrecio());
                        }

                    }
                }
            }
            /*Log.d("ADAPTER_PRODUCTOS", "Product Key: "+producto.getProductoFirebaseKey());
            Log.d("ADAPTER_PRODUCTOS", "Nombre: "+producto.getNombre());
            Log.d("ADAPTER_PRODUCTOS", "Cantidad: "+producto.getCantidad());
            Log.d("ADAPTER_PRODUCTOS", "Precio: "+producto.getPrecio());
            Log.d("ADAPTER_PRODUCTOS", "Costo: "+producto.getCosto());
            Log.d("ADAPTER_PRODUCTOS", "Descrip: "+producto.getDescripcion());
            Log.d("ADAPTER_PRODUCTOS", "CobBar: "+producto.getCodBarras());
            Log.d("ADAPTER_PRODUCTOS", "ImagenURL: "+producto.getImagen());
            Log.d("ADAPTER_PRODUCTOS", "---------------------------------------");*/
        }

        public void cargarImagenFirebase(String URLImagen) {
            //Log.d("ADAPTER_PRODUCTOS", "Cargando Imagen: " + URLImagen);
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
                                //Log.d("ADAPTER_PRODUCTOS", "Imagen cargada");
                                progressBarFoto.setVisibility(View.GONE);
                                return false;
                            }

                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                //Log.d("ADAPTER_PRODUCTOS", "Imagen error al cargar");
                                progressBarFoto.setVisibility(View.GONE);
                                productoFoto.setImageResource(R.drawable.baseline_error_red_24dp);
                                return false;
                            }

                        })
                        .into(productoFoto);

            } else {
                //Log.d("ADAPTER_PRODUCTOS", "Imagen URL vacia");
                progressBarFoto.setVisibility(View.GONE);
                productoFoto.setImageResource(R.drawable.baseline_camera_alt_green_black_48dp);
            }
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            onclickListener.onListenItemClick(adapterPosition, producto);
        }

        private void blinkEffect(View view) {
            ObjectAnimator animation = ObjectAnimator.ofInt(view, "backgroundColor", Color.TRANSPARENT, Color.RED, Color.TRANSPARENT);
            animation.setDuration(3000);
            animation.setEvaluator(new ArgbEvaluator());
            animation.setRepeatMode(ValueAnimator.REVERSE);
            animation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {

                }
            });
            animation.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {

                }
            });

            animation.start();


        }

    }
}

