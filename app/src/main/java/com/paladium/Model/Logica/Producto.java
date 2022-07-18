package com.paladium.Model.Logica;

import java.io.Serializable;

public class Producto implements Serializable {

    // Variables que darán el nombre a cada uno de los campos de los productos
    //en firebase, no se deben inicializar

    private String CodBarras;
    private String Nombre;
    private String Descripcion;
    private String Imagen;
    private String Categoria;
    private int Cantidad;
    private float Precio;
    private float Costo;
    //la key solo se recupera de firebase, no se la guarda en un campo del la Bd
    private String productoFirebaseKey;

    public Producto() {
    }

    public String getNombre() {
        return Nombre;
    }

    public void setNombre(String nombre) {
        Nombre = nombre;
    }

    public String getDescripcion() {
        return Descripcion;
    }

    public void setDescripcion(String descripcion) {
        Descripcion = descripcion;
    }

    public int getCantidad() {
        return Cantidad;
    }

    public void setCantidad(int cantidad) {
        Cantidad = cantidad;
    }

    public String getCodBarras() {
        return CodBarras;
    }

    public void setCodBarras(String codBarras) {
        CodBarras = codBarras;
    }

    public float getPrecio() {
        return Precio;
    }

    public void setPrecio(float precio) {
        Precio = precio;
    }

    public float getCosto() {
        return Costo;
    }

    public void setCosto(float costo) {
        Costo = costo;
    }

    public String getImagen() {
        return Imagen;
    }

    public void setImagen(String imagen) {
        Imagen = imagen;
    }

    public String getCategoria() {
        return Categoria;
    }

    public void setCategoria(String categoria) {
        Categoria = categoria;
    }

    public String getProductoFirebaseKey() {
        return productoFirebaseKey;
    }

    public void setProductoFirebaseKey(String productoFirebaseKey) {
        this.productoFirebaseKey = productoFirebaseKey;
    }
}
