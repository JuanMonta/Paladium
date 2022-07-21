package com.paladium.Presentador.Interfaces;

import android.net.Uri;

public interface InterfacePresenter_ProductCreation {

    interface onImagenCargada {
        /**
         * Para notificar que la imagen del producto ha sido subido a Firebase,
         * con lo que luego se procederá a cargar los datos del producto en sí.
         */
        void imagenCargada(Uri downloadLinkImage, Uri rutaImagen);
    }


    interface onProductoCargado {
        /**
         * Cuando los datos del producto se hayan subido a firebase, entonces
         * borraremos el Uri filePath de la imagen que acabamos de subir para
         * dejar la variable limpia.
         */
        void productoCargado(boolean subido);
    }

    interface onSeleccionarMetodoScanSQBarCode {
        void seleccionMetodoScanQRBarcode(String metodoScan);
    }

}
