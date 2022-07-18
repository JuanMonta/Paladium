package com.paladium.Model.Utils;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;

import com.paladium.Model.Logica.Producto;

import java.util.ArrayList;
import java.util.Objects;

public class ProductoDiffCallBack extends DiffUtil.Callback {
    // https://stackoverflow.com/questions/43246905/highlight-changing-data-in-recyclerview
    // https://proandroiddev.com/diffutil-is-a-must-797502bc1149

    //https://devexperto.com/diffutil-recyclerview/
    //

    private ArrayList<Producto> mOldList;
    private ArrayList<Producto> mNewList;

    public ProductoDiffCallBack(ArrayList<Producto> mOldList, ArrayList<Producto> mNewList) {
        this.mOldList = mOldList;
        this.mNewList = mNewList;
    }

    @Override
    public int getOldListSize() {
        return mOldList != null ? mOldList.size() : 0;
    }

    @Override
    public int getNewListSize() {
        return mNewList != null ? mNewList.size() : 0;
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        //compruebo si el id del producto es elmismo
        return Objects.equals(mNewList.get(newItemPosition).getProductoFirebaseKey(), mOldList.get(oldItemPosition).getProductoFirebaseKey());
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        //comprueba si los datos contenidos en el producto son los mismos
        return mNewList.get(newItemPosition).equals(mOldList.get(oldItemPosition));
    }

    /**
     * para obtener cuales fueron los campos de los productos que se actualizaron entre la
     * antigua lista de productos y la nueva lista de productos
     * @param oldItemPosition
     * @param newItemPosition
     * @return
     */
    @Nullable
    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {
        //un bundle donde guardaremos por clave valor los datos que han cambiado
        Bundle diffBundle = new Bundle();

        if (mOldList !=null && mNewList !=null){
            //Log.d("PRODUCTDIFF", "TAMAÑOS DE LISTA-> OLD: "+mOldList.size()+"  NEW:"+mNewList.size());
            if ((newItemPosition+1) <= mOldList.size() && mOldList.size()>0){
                Producto newProduct = mNewList.get(newItemPosition);
                Producto oldProduct = mOldList.get(oldItemPosition);
                //las comparaciones solo guardaran los datos en el bundle solo si se cumplen las condiciones
                if (!Objects.equals(newProduct.getCodBarras(), oldProduct.getCodBarras())) {
                    diffBundle.putString(Utilidades.codBarrasProducto, newProduct.getCodBarras());
                }
                if (!Objects.equals(newProduct.getNombre(), oldProduct.getNombre())) {
                    diffBundle.putString(Utilidades.nombreProducto, newProduct.getNombre());
                }
                if (!Objects.equals(newProduct.getDescripcion(), oldProduct.getDescripcion())) {
                    diffBundle.putString(Utilidades.descProducto, newProduct.getDescripcion());
                }
                if (!Objects.equals(newProduct.getImagen(), oldProduct.getImagen())) {
                    diffBundle.putString(Utilidades.imagenProducto, newProduct.getImagen());
                }
                if (!Objects.equals(newProduct.getCategoria(), oldProduct.getCategoria())) {
                    diffBundle.putString(Utilidades.categoriaProducto, newProduct.getCategoria());
                }

                if (newProduct.getCantidad() != oldProduct.getCantidad()) {
                    diffBundle.putInt(Utilidades.cantProducto, newProduct.getCantidad());
                }

                if (newProduct.getPrecio() != oldProduct.getPrecio()) {
                    diffBundle.putFloat(Utilidades.precioProducto, newProduct.getPrecio());
                }
                if (newProduct.getCosto() != oldProduct.getCosto()) {
                    diffBundle.putFloat(Utilidades.costoProducto, newProduct.getCosto());
                }
            }
        }
        //si no se ha guardado nimgun dato en el buble, que devuelba null
        if (diffBundle.size() == 0) return null;


        return diffBundle;
    }

}
