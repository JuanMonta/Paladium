package com.paladium.Presentador.Customs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.paladium.Model.Utils.Utilidades;
import com.paladium.Presentador.InterfacePresenter_ProductCreation;
import com.paladium.R;

public class PresenterCustomDialog {

    private Context mContext;
    private Dialog customDialog;

    public interface ProductoBorrado {
        void onProductoBorrado(String codigo);
    }

    private ProductoBorrado interfazProductoBorrado;

    public interface ProductoBorradoFinish {
        void onProductoBorradoFinish();
    }


    public PresenterCustomDialog(Context mContext) {
        this.mContext = mContext;
        initDialog();
    }

    private void initDialog() {
        this.customDialog = new Dialog(mContext);
        this.customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.customDialog.setCancelable(false);
        this.customDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //this.customDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    public void dialogSuccess(String mensajeDialog, InterfacePresenter_ProductCreation.onProductoCargado interfaceProductoCargado) {
        this.customDialog.setContentView(R.layout.custom_dialog_success);
        //this.customDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        TextView mensaje = this.customDialog.findViewById(R.id.custom_dialog_success_tvMensaje);
        mensaje.setText(mensajeDialog);

        Button ok = this.customDialog.findViewById(R.id.custom_dialog_success_btnOk);
        ok.setOnClickListener(view -> {
            customDialog.dismiss();
            interfaceProductoCargado.productoCargado(true);
        });

        this.customDialog.show();

    }

    public void dialogSuccess(String mensajeDialog) {
        this.customDialog.setContentView(R.layout.custom_dialog_success);
        TextView mensaje = this.customDialog.findViewById(R.id.custom_dialog_success_tvMensaje);
        mensaje.setText(mensajeDialog);

        Button ok = this.customDialog.findViewById(R.id.custom_dialog_success_btnOk);
        ok.setOnClickListener(view -> customDialog.dismiss());
        this.customDialog.show();
    }

    public void dialogSuccess(String mensajeDialog, ProductoBorradoFinish borradoInterfaz) {
        this.customDialog.setContentView(R.layout.custom_dialog_success);
        TextView mensaje = this.customDialog.findViewById(R.id.custom_dialog_success_tvMensaje);
        mensaje.setText(mensajeDialog);

        Button ok = this.customDialog.findViewById(R.id.custom_dialog_success_btnOk);
        ok.setOnClickListener(view -> {
            customDialog.dismiss();
            borradoInterfaz.onProductoBorradoFinish();
        });

        this.customDialog.show();

    }


    public void dialogError(String mensajeError) {
        this.customDialog.setContentView(R.layout.custom_dialog_error);
        //this.customDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        TextView mensaje = this.customDialog.findViewById(R.id.custom_dialog_error_tvMensaje);
        mensaje.setText(mensajeError);

        Button ok = this.customDialog.findViewById(R.id.custom_dialog_error_btnOk);
        ok.setOnClickListener(view -> customDialog.dismiss());

        this.customDialog.show();
    }


    public void dialogAdvertencia(String mensajeAdvertencia, ProductoBorrado interfaz) {
        this.interfazProductoBorrado = interfaz;
        this.customDialog.setContentView(R.layout.custom_dialog_warning);
        //this.customDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        TextView mensaje = this.customDialog.findViewById(R.id.custom_dialog_warning_tvMensaje);
        mensaje.setText(mensajeAdvertencia);

        Button ok = this.customDialog.findViewById(R.id.custom_dialog_warning_btnOk);
        ok.setOnClickListener(view -> {
            customDialog.dismiss();
            interfazProductoBorrado.onProductoBorrado(Utilidades.alertDialog_OK);
        });

        Button cancel = this.customDialog.findViewById(R.id.custom_dialog_warning_btnCancel);
        cancel.setOnClickListener(view -> {
            customDialog.dismiss();
        });

        this.customDialog.show();

    }

    public void dialogAtención(String mensajeAtencion) {
        this.customDialog.setContentView(R.layout.custom_dialog_atention);
        //this.customDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        TextView mensaje = this.customDialog.findViewById(R.id.custom_dialog_atention_tvMensaje);
        mensaje.setText(mensajeAtencion);

        Button ok = this.customDialog.findViewById(R.id.custom_dialog_atention_btnOk);
        ok.setOnClickListener(view -> {
            customDialog.dismiss();
        });


        this.customDialog.show();

    }


}
