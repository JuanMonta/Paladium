package com.paladium.Model.Utils;

public class Utilidades {

    //NOmbre de los nodos usados en firebase
    public static final String nodoPadre = "Productos";
    public static final String nodoProducto = "Producto";
    public static final String nodoCategoria = "Categoria";
    public static final String nodoStorageFotosProductos = "FotosProductos";

    //nombre de los campos del producto guardado en Firebase
    //estos nombres en string estan escritos esxactamente como sus variables
    //de la clase Prodcutos, y con ese nombre de variables de la clase Producto, se han
    //de guardar en firebase, puesto que Firebase toma las variables como un nombre
    //para los campos.
    public static final String nombreProducto = "Nombre";
    public static final String descProducto= "Descripcion";
    public static final String cantProducto = "Cantidad";
    public static final String codBarrasProducto = "CodBarras";
    public static final String precioProducto = "Precio";
    public static final String costoProducto = "Costo";
    public static final String imagenProducto = "Imagen";


    //------------NOmbre de directorio para guardar imagenes de la app
    public static final String directorioName = "ImagenesCompressed";

}
