package com.paladium.Presentador.Customs;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.Writer;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.oned.Code128Writer;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.paladium.R;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Random;

public class PresenterCustomDialog_GenerarQRBarCode {
    private Context mContext;

    public PresenterCustomDialog_GenerarQRBarCode(Context mContext) {
        this.mContext = mContext;
    }

    public void dialogGenerar_BAR_Code( ArrayList<String[]> listaCod_Barras_Nombre, TextInputEditText inputEdCodBarras, BarcodeFormat formato, int ancho, int alto) {
        final ProgressDialog progressDialog = new ProgressDialog(mContext);
        progressDialog.setMessage("Generando código...");

        final Dialog customDialog = new Dialog(mContext);
        customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        customDialog.setCancelable(false);
        customDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        customDialog.setContentView(R.layout.custom_dialog_crear_barcode);

        TextView tv_titulo = customDialog.findViewById(R.id.custom_dialog_crear_barcode_tvTitulo);
        TextView tv_mensaje = customDialog.findViewById(R.id.custom_dialog_crear_barcode_tvMensaje);

        TextInputLayout inputLayout_QREscribirAlgo = customDialog.findViewById(R.id.custom_dialog_crear_barcode_inputLayout_QR_EscribirAlgo);
        TextInputEditText inputEd_QREscribirAlgo = customDialog.findViewById(R.id.custom_dialog_crear_barcode_inputEd_QR_EscribirAlgo);

        ImageView img_Codigo = customDialog.findViewById(R.id.custom_dialog_crear_barcode_ImgvCodigo);

        ImageButton imgBtn_exit = customDialog.findViewById(R.id.custom_dialog_crear_barcode_imgBtnCerrarDialog);
        imgBtn_exit.setOnClickListener(view -> {
            customDialog.dismiss();
        });

        Button btn_generarQR = customDialog.findViewById(R.id.custom_dialog_crear_barcode_btnGenerarQR);
        btn_generarQR.setOnClickListener(view -> {
            if (inputEd_QREscribirAlgo.getText().length()>0){
                generar_BAR_Code(inputEd_QREscribirAlgo.getText().toString().trim(), img_Codigo, formato, ancho, alto);
            }
        });

        Button btn_GuardarImagen = customDialog.findViewById(R.id.custom_dialog_crear_barcode_btnGuardarImagenCodigo);
        btn_GuardarImagen.setOnClickListener(view -> {

        });

        customDialog.show();
        progressDialog.show();

        if (formato == BarcodeFormat.QR_CODE) {
            tv_titulo.setText("Escriba algo para personalizar QR...");


        } else if (formato == BarcodeFormat.CODE_128) {
            tv_titulo.setText("Código generado:");
            inputLayout_QREscribirAlgo.setVisibility(View.GONE);
            btn_generarQR.setVisibility(View.GONE);


            boolean verficar = true;
            StringBuilder codigoGenerado;
            do {
                codigoGenerado = new StringBuilder();
                Random random = new Random();
                for (int i = 0; i < 12; i++) {
                    codigoGenerado.append(random.nextInt(10));
                }
                //Log.d(TAG, "codigo generado: "+codigoGenerado);
                for (int i = 0; i < listaCod_Barras_Nombre.size(); i++) {
                    verficar = listaCod_Barras_Nombre.get(i)[0].trim().equals(codigoGenerado.toString());
                }
            } while (verficar);
            inputEdCodBarras.setText(codigoGenerado.toString().trim());
            generar_BAR_Code(codigoGenerado.toString(), img_Codigo, formato, ancho, alto);
        }

        progressDialog.dismiss();
    }

    private void generar_BAR_Code(String codeData, ImageView imageView, BarcodeFormat formato, int ancho, int alto) {
        try {
            Hashtable<EncodeHintType, ErrorCorrectionLevel> hintMap = new Hashtable<EncodeHintType, ErrorCorrectionLevel>();
            hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);

            Writer codeWriter = null;
            if (formato == BarcodeFormat.QR_CODE) {
                codeWriter = new QRCodeWriter();
            } else if (formato == BarcodeFormat.CODE_128) {
                codeWriter = new Code128Writer();
            }

            if (codeWriter != null) {

                BitMatrix byteMatrix = codeWriter.encode(
                        codeData,
                        formato,
                        ancho,
                        alto,
                        hintMap
                );

                int width = byteMatrix.getWidth();
                int height = byteMatrix.getHeight();

                Bitmap imageBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

                for (int i = 0; i < width; i++) {
                    for (int j = 0; j < height; j++) {
                        imageBitmap.setPixel(i, j, byteMatrix.get(i, j) ? Color.BLACK : Color.WHITE);
                    }
                }
                if (formato == BarcodeFormat.QR_CODE) {
                    imageView.setImageBitmap(imageBitmap);
                } else {
                    generar_BAR_Code_Label(imageView, imageBitmap, codeData);
                }
            }
        } catch (WriterException e) {
            e.printStackTrace();
            //return null;
        }
    }

    private void generar_BAR_Code_Label(ImageView imageView, Bitmap bitmap2, String codeData) {

        int x = 100;
        int y = 285;
        Canvas canvas = null;
        canvas = new Canvas(bitmap2);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.WHITE);
        paint.setTextSize(45);


        Paint mpaint = new Paint();
        mpaint.setColor(Color.parseColor("#f9bf3a"));
        mpaint.setStyle(Paint.Style.FILL);
        float w = paint.measureText(codeData) / 2;
        float textSize = paint.getTextSize();
        Paint paint1 = new Paint(Paint.ANTI_ALIAS_FLAG);


        Paint p = new Paint();
        p.setAntiAlias(true);
        p.setColor(Color.WHITE);
        p.setStyle(Paint.Style.FILL);
        canvas.drawARGB(0, 0, 0, 0);
        canvas.drawRoundRect(new RectF(x - 13, y - (textSize + 3), x + paint.measureText(codeData) + 13, y + 23), 7, 7, p);
        canvas.drawRoundRect(new RectF(x - 10, y - (textSize), x + paint.measureText(codeData) + 10, y + 20), 7, 7, mpaint);
        canvas.drawText(codeData, x, y, paint);

        imageView.setImageBitmap(bitmap2);
    }
}
