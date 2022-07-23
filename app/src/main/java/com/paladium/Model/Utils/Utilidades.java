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

    // para los alertdialog customs utilizados para borrar, guardar o actualizar un produco---------
    public static final String alertDialog_OK = "alertDialog_OK";
    public static final String alertDialog_CANCEL = "alertDialog_CANCEL";

    public static final String alertDialog_EXITO = "alertDialog_EXITO";
    public static final String alertDialog_ERROR = "alertDialog_ERROR";
    public static final String alertDialog_ATENCION = "alertDialog_ATENCION";
    public static final String alertDialog_ACTUALIZADO = "alertDialog_ACTUALIZADO";
    public static final String alertDialog_ADVERTENCIA = "alertDialog_ADVERTENCIA";
    public static final String alertDialog_PRODUCTO_REPETIDO = "alertDialog_PRODUCTO_REPETIDO";
    public static final String alertDialog_PRODUCTO_REPETIDO_GUARDAR_DE_TODAS_FORMAS = "alertDialog_PRODUCTO_REPETIDO_GUARDAR_DE_TODAS_FORMAS";

    public static final String metodoScanQRBarCode_Galeria = "metodoScanQRBarCode_Galeria";
    public static final String metodoScanQRBarCode_Camara = "metodoScanQRBarCode_Camara";



}
