package com.paladium.Presentador.Customs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;

import com.paladium.R;

public class PresenterCustomDialog {

    private Context mContext;
    private Dialog  customDialog;

    public PresenterCustomDialog(Context mContext) {
        this.mContext = mContext;
    }

    public void dialogSuccess(){
        this.customDialog = new Dialog(mContext);
        this.customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.customDialog.setCancelable(false);
        this.customDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        this.customDialog.setContentView(R.layout.custom_dialog_success);
        //this.customDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        Button ok = this.customDialog.findViewById(R.id.custom_dialog_success_btnOk);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customDialog.dismiss();
            }
        });

        this.customDialog.show();

    }
}
