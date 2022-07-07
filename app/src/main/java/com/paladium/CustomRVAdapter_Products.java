package com.paladium;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.paladium.Logica.Producto;

import java.util.List;

public class CustomRVAdapter_Products extends RecyclerView.Adapter<CustomRVAdapter_Products.ViewHolderListaProducts> {

    List<Producto> listaProducto;

    public interface ListItemClick{
        void onListenItemClick(int itemClicado);
    }

    final private ListItemClick onclickListener;

    public CustomRVAdapter_Products(List<Producto> listaProducto, ListItemClick listItemClick) {
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
            productoNombre.setText(""+producto.getNombre());
            productoCant.setText(""+producto.getCantidad());
            productoPrecio.setText(""+producto.getPrecio());
            Log.d("ADAPTER_PRODUCTOS", ""+producto.getNombre());
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            onclickListener.onListenItemClick(adapterPosition);
        }
    }
}
