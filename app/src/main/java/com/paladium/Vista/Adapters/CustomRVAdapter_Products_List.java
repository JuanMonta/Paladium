package com.paladium.Vista.Adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.paladium.Model.Logica.Producto;
import com.paladium.R;

import java.util.List;

public class CustomRVAdapter_Products_List extends RecyclerView.Adapter<CustomRVAdapter_Products_List.ViewHolderListaProducts> {

    List<Producto> listaProducto;

    public interface ListItemClick{
        void onListenItemClick(int itemClicado);
    }

    final private ListItemClick onclickListener;

    public CustomRVAdapter_Products_List(List<Producto> listaProducto, ListItemClick listItemClick) {
        this.listaProducto = listaProducto;
        onclickListener = listItemClick;
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

    public class ViewHolderListaProducts extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView productoNombre, productoPrecio, productoDesc, productoCant;
        ImageView productoFoto;

        public ViewHolderListaProducts(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);

            productoNombre = itemView.findViewById(R.id.custom_list_productos_tvNobre);
            productoPrecio = itemView.findViewById(R.id.custom_list_productos_tvPrecio);
            productoCant = itemView.findViewById(R.id.custom_list_productos_tvCantidad);
            productoFoto = itemView.findViewById(R.id.custom_list_productos_igvFoto);
        }


        void asignarDatos(Producto producto) {
            productoNombre.setText(producto.getNombre() == null ? "Nombre no disponible" : producto.getNombre());
            productoCant.setText(producto.getCantidad() >0 ? ""+producto.getCantidad(): "Producto no disponible");
            productoPrecio.setText(producto.getPrecio() >0 ? ""+producto.getPrecio(): "Precio no disponible");

            Log.d("ADAPTER_PRODUCTOS", "Product Key: "+producto.getProductoFirebaseKey());
            Log.d("ADAPTER_PRODUCTOS", "Nombre: "+producto.getNombre());
            Log.d("ADAPTER_PRODUCTOS", "Cantidad: "+producto.getCantidad());
            Log.d("ADAPTER_PRODUCTOS", "Precio: "+producto.getPrecio());
            Log.d("ADAPTER_PRODUCTOS", "Costo: "+producto.getCosto());
            Log.d("ADAPTER_PRODUCTOS", "Descrip: "+producto.getDescripcion());
            Log.d("ADAPTER_PRODUCTOS", "CobBar: "+producto.getCodBarras());
            Log.d("ADAPTER_PRODUCTOS", "ImagenURL: "+producto.getImagen());
            Log.d("ADAPTER_PRODUCTOS", "---------------------------------------");
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            onclickListener.onListenItemClick(adapterPosition);
        }
    }
}
