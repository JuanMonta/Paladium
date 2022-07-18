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
    public static final String codBarrasProducto = "CodBarras";
    public static final String nombreProducto = "Nombre";
    public static final String descProducto= "Descripcion";
    public static final String imagenProducto = "Imagen";
    public static final String categoriaProducto = "Categoria";
    public static final String cantProducto = "Cantidad";
    public static final String precioProducto = "Precio";
    public static final String costoProducto = "Costo";
    //la key no se guarda en firebase, solo se recupera
    public static final String productoFirebaseKey = "productoFirebaseKey";

    //key para recuperar la clase producto enviado mediante un bundle
    public static final String bundleProduto = "bundleProduto";

    //------------NOmbre de directorio para guardar imagenes de la app
    public static final String directorioName = "ImagenesCompressed";

    public static final String alertDialog_EXITO = "alertDialog_EXITO";
    public static final String alertDialog_ERROR = "alertDialog_ERROR";

    //----------para saber si la imagen de firebase ha sido descargada------------------------------
    public static final String imagenDescargada = "imagenDescargada";
    public static final String imagenErrorDescargar = "imagenErrorDescargar";
    public static final String imagenVacia = "imagenVacia";

}
