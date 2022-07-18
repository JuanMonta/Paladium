package com.paladium.Vista.Activities;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;
import com.paladium.Model.Utils.ImageCompression;
import com.paladium.Model.Utils.Utilidades;
import com.paladium.Presentador.InterfacePresenter_ProductCreation;
import com.paladium.Presentador.PresentadorProductCreation;
import com.paladium.R;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;


import java.io.File;
import java.io.IOException;

public class ProductCreation extends AppCompatActivity implements View.OnClickListener, InterfacePresenter_ProductCreation.onProductoCargado {

    private String TAG = "ProductCreation";
    private final int ESCOJER_FOTO = 1;
    private final int TOMAR_FOTO = 2;

    PresentadorProductCreation productCreation;
    Bundle bundleProducto;
    private Button btnRegistrarProducto;
    private ImageButton imgbtnSeleccionarImagenProducto, imgbTomarFoto;
    private ImageView imgv_ImagenProducto;
    private TextInputLayout inputLayoutScanQRBarCode;
    private TextInputEditText inputEdScanQRBarCode;
    private Uri filePath;
    private String rutaImagen;
    private ActivityResultLauncher<Intent> activityResultLauncherEscojerFoto;
    private ActivityResultLauncher<Intent> activityResultLauncherTomarFoto;
    private ActivityResultLauncher<ScanOptions> activityResultLauncherScanQRBarCode;
    private ActivityResultLauncher<Intent> activityResultLauncherCropImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_creation);
        filePath = null;
        View view = findViewById(android.R.id.content).getRootView();
        bundleProducto = getIntent().getExtras();
        productCreation = new PresentadorProductCreation(this, this, bundleProducto);
        productCreation.init(view);
        inputEdScanQRBarCode = findViewById(R.id.product_creation_edCodBarras);
        inputLayoutScanQRBarCode = findViewById(R.id.product_creation_LayoutedCodBarras);
        inputLayoutScanQRBarCode.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scanQRBarCode();
            }
        });

        btnRegistrarProducto = view.findViewById(R.id.product_creation_btnGuardarProducto);
        btnRegistrarProducto.setOnClickListener(this);
        if (bundleProducto !=null){
            btnRegistrarProducto.setText("GUARDAR EDICIÓN");
        }

        imgbtnSeleccionarImagenProducto = view.findViewById(R.id.product_creation_imgbtnSeleccionarImagenProducto);
        imgbtnSeleccionarImagenProducto.setOnClickListener(this);
        imgbTomarFoto = view.findViewById(R.id.product_creation_imgbTomarFotoProducto);
        imgbTomarFoto.setOnClickListener(this);

        imgv_ImagenProducto = findViewById(R.id.product_creation_Imgv_Producto);

        ActivityResultLauncherEscojerFoto();
        ActivityResultLauncherTomarFoto();
        ActivityResultLauncherScanQRBarcode();
        ActivityResultLauncherCropImage();
    }


    private void seleccionarImagen() {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        Intent chooser = Intent.createChooser(intent, "Seleccione una imagen");
        activityResultLauncherEscojerFoto.launch(chooser);
    }

    private void tomarFoto() {
        Intent intent = new Intent();
        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);

        File imagenGuardada = null;
        try {
            Log.d(TAG, "LLamando imagen en el dispositivo: ");
            imagenGuardada = guardarImagenEnDispositivo();
        } catch (IOException io) {
            Log.d(TAG, "No se pudo guarda la imagen en el dispositivo: " + io.fillInStackTrace());
        }

        if (imagenGuardada != null) {
            //se debe asignar un provider en el Manifest
            Uri fotoUri = FileProvider.getUriForFile(this, "com.paladium" + ".fileprovider", imagenGuardada);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fotoUri);
            activityResultLauncherTomarFoto.launch(intent);
        }

    }

    private void ActivityResultLauncherEscojerFoto() {
        activityResultLauncherEscojerFoto = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {

                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    //si el path no es nulo, entonces que borre cualquier archivo creado anteriormente,
                    //puesto que está seleccionando una nueva imagen
                    if (filePath != null) {
                        new File(filePath.getPath()).delete();
                    }
                    //obtenemos el resultado que contiene la imagen escojida de la galería
                    filePath = result.getData().getData();
                     /*try {
                        Bitmap imagen = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                        imgv_ImagenProducto.setImageBitmap(imagen);
                    } catch (IOException e) {

                    }*/
                    Log.d(TAG, "imagen escojida -> filePath: " + filePath.toString());
                    //obtenemos la dirección real de la imagen dentro del dispositivo
                    String imageRealPath = realImagePath(filePath);
                    Log.d(TAG, "imagen escojida -> realImagePath: " + imageRealPath);
                    //enviamos la dirección de la imagen, la cual será tomada, comprimida y guardada
                    //su compresión dentro del dispositivo.
                    //Nos devuelve la ruta de la imagen comprimida
                    String compressImagePath = new ImageCompression(getApplicationContext()).compressImage(imageRealPath);
                    //transformamos la ruta de la imagen comprimida a un path Uri con la cual la subimos a firebase
                    filePath = Uri.fromFile(new File(compressImagePath));
                    Log.d(TAG, "imagen escojida -> filePath UriFromFile: " + filePath);
                    Log.d(TAG, "imagen escojida -> compressImagePath:   " + compressImagePath);
                    Log.d(TAG, "imagen escojida -> compressImageUri:" + Uri.parse(compressImagePath));
                    cropImage(filePath);
                    /*Bitmap imagen = BitmapFactory.decodeFile(compressImagePath);
                    imgv_ImagenProducto.setImageBitmap(imagen);*/
                }
            }
        });

    }

    private void ActivityResultLauncherTomarFoto() {
        activityResultLauncherTomarFoto = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == RESULT_OK) {
                   /* Bundle extras = result.getData().getExtras();
                    Bitmap imagen = (Bitmap) extras.get("data");
                    imgv_ImagenProducto.setImageBitmap(imagen);*/

                    Log.d(TAG, "imagen tomada foto -> filePath Strg: " + filePath.toString());
                    //enviamos la dirección de la imagen, la cual será tomada, comprimida y guardada
                    //su compresión dentro del dispositivo.
                    //Nos devuelve la ruta de la imagen comprimida
                    String compressImagePath = new ImageCompression(getApplicationContext()).compressImage(filePath.getPath());
                    //borramos la imagen anteriormente guardada en el path y guardamos la direccion de la nueva imagen,
                    //la cual es la imagen comprimida
                    borrarAchivoDelFilePath(filePath);
                    //transformamos la ruta de la imagen comprimida a un path Uri con la cual la subimos a firebase
                    filePath = Uri.fromFile(new File(compressImagePath));
                    Log.d(TAG, "imagen tomada foto -> nuevo filePath: " + filePath.toString());
                    Log.d(TAG, "imagen tomada foto -> compressImagePath:        " + compressImagePath);
                    Log.d(TAG, "imagen tomada foto -> compressImageUri:" + Uri.parse(compressImagePath));
                    Log.d(TAG, "imagen tomada foto -> EnviandoUri a CropImage()");
                    //llamamos al inten con la Activity de la librería que permitirá recortar la imagen
                    cropImage(filePath);
                    /*Bitmap imagen = BitmapFactory.decodeFile(compressImagePath);
                    imgv_ImagenProducto.setImageBitmap(imagen);*/
                }
            }
        });
    }

    private void ActivityResultLauncherCropImage(){
        activityResultLauncherCropImage = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                Log.d(TAG, "imagen Crop Launcher -> Entrando en Result Launcher");
                if (result.getResultCode() == RESULT_OK){
                    Log.d(TAG, "imagen Crop Launcher -> ResultLauncher Ok");
                    CropImage.ActivityResult resultImage = CropImage.getActivityResult(result.getData());
                    //borramos el archivo que tenga guardado anteriormente el filePath
                        borrarAchivoDelFilePath(filePath);
                    //devuel la direccion de la imagen recortada, la cual ha sido guardada en caché del dispostivo
                    filePath = resultImage.getUri();
                    Log.d(TAG, "imagen Crop Launcher -> Colocando a Image View el Uri: "+resultImage.getUri());
                    imgv_ImagenProducto.setImageURI(resultImage.getUri());
                    //Toast.makeText(ProductCreation.this, "Image Update Successfully!!!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private File guardarImagenEnDispositivo() throws IOException {
        //si el path no es nulo, entonces que borre cualquier archivo creado anteriormente,
        //puesto que está tomando una nueva imagen con la cámara
        if (filePath != null) {
            new File(filePath.getPath()).delete();
        }

        String nombreImagen = "paladium_";
        File directorio = getExternalFilesDir(Utilidades.directorioName);
        File imagen = File.createTempFile(nombreImagen, ".jpg", directorio);

        rutaImagen = imagen.getAbsolutePath();
        filePath = Uri.fromFile(imagen);
        Log.d(TAG, "Guardando imagen en el dispositivo: rutaImagen -> \t" + rutaImagen);
        Log.d(TAG, "Guardando imagen en el dispositivo: filePathUri -> \t" + filePath);
        Log.d(TAG, "Guardando imagen en el dispositivo: filePathUri.getPath -> \t" + filePath.getPath());
        Log.d(TAG, "Guardando imagen en el dispositivo: filePathUri.toString -> \t" + filePath.toString());
        return imagen;
    }

    private String realImagePath(Uri uri) {
        String realPath = "";
        String wholeID = DocumentsContract.getDocumentId(uri);
        // Split at colon, use second item in the array
        String id = wholeID.split(":")[1];
        String[] column = {MediaStore.Images.Media.DATA};
        // where id is equal to
        String sel = MediaStore.Images.Media._ID + "=?";
        Cursor cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, column, sel, new String[]{id}, null);
        int columnIndex = 0;
        if (cursor != null) {
            columnIndex = cursor.getColumnIndex(column[0]);
            if (cursor.moveToFirst()) {
                realPath = cursor.getString(columnIndex);
            }
            cursor.close();
        }
        return realPath;
    }

    private void cropImage (Uri imagenARecortar){
        // https://github.com/ArthurHub/Android-Image-Cropper

        Log.d(TAG, "imagen tomada foto -> Creando Intent");
        Intent intent = CropImage.activity(imagenARecortar)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setMultiTouchEnabled(true)
                .getIntent(this);
        Log.d(TAG, "imagen tomada foto -> Colocando y lanzando inten Intent");
        activityResultLauncherCropImage.launch(intent);
    }

    private void borrarAchivoDelFilePath(Uri filePath){
        if (filePath !=null){
            new File(filePath.getPath()).delete();
        }
    }

    private void scanQRBarCode() {
        //pagina principal https://github.com/journeyapps/zxing-android-embedded#older-sdk-versions
        //scan options     https://github-com.translate.goog/journeyapps/zxing-android-embedded/blob/master/zxing-android-embedded/src/com/journeyapps/barcodescanner/ScanOptions.java?_x_tr_sl=en&_x_tr_tl=es&_x_tr_hl=es&_x_tr_pto=op
        //vista incustada pag principal     https://github-com.translate.goog/journeyapps/zxing-android-embedded/blob/master/EMBEDDING.md?_x_tr_sl=en&_x_tr_tl=es&_x_tr_hl=es
        //zxing activity main    https://github-com.translate.goog/journeyapps/zxing-android-embedded/blob/master/sample/src/main/java/example/zxing/MainActivity.java?_x_tr_sl=en&_x_tr_tl=es&_x_tr_hl=es
        // continuos capture activity  https://github-com.translate.goog/journeyapps/zxing-android-embedded/blob/master/sample/src/main/java/example/zxing/ContinuousCaptureActivity.java?_x_tr_sl=en&_x_tr_tl=es&_x_tr_hl=es&_x_tr_pto=op
        ScanOptions options = new ScanOptions();
        options.setDesiredBarcodeFormats(ScanOptions.ONE_D_CODE_TYPES);
        options.setPrompt("Escanea un Código de Barras o QR.\n\n"
                + "Para utilizar el flash use los botones de control de volumen:\n"
                + " -Subir Volumen: Encender flash\n"
                + " -Bajar Volumen: Apagar   flash\n");
        options.setCameraId(0);  // Use a specific camera of the device
        options.setBeepEnabled(false);
        options.setBarcodeImageEnabled(true);
        options.setBeepEnabled(true);
        //options.setTorchEnabled(true);
        options.setOrientationLocked(false);
        activityResultLauncherScanQRBarCode.launch(options);
    }

    private void ActivityResultLauncherScanQRBarcode() {
        // Register the launcher and result handler
        activityResultLauncherScanQRBarCode = registerForActivityResult(new ScanContract(),
                result -> {
                    if (result.getContents() != null) {
                        inputEdScanQRBarCode.setTextInputLayoutFocusedRectEnabled(false);
                        inputEdScanQRBarCode.setText(result.getContents());
                    } else {
                        Toast.makeText(this, "Scaneo Cancelado", Toast.LENGTH_LONG).show();
                    }
                });

    }

    private void borrarTodasLasImagenEnCarpeTasCompressedYCache(){

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.product_creation_imgbtnSeleccionarImagenProducto:
                seleccionarImagen();
                break;

            case R.id.product_creation_imgbTomarFotoProducto:
                tomarFoto();
                break;

            case R.id.product_creation_btnGuardarProducto:
                productCreation.registrarProducto(filePath);
                break;
        }
    }

    @Override
    public void productoCargado(boolean subido) {
        //cuando se haya subido los datos del producto a firebase, borramos el Uri filePath
        //que contenía la dirección de la imagen en el dispositivo
        if (subido) {
            //una vez subido el producto borramos la imagen que está guardad en el
            //dispositivo
                Log.d(TAG, "Borrando Imagen, producto guardado: " + filePath);
                borrarAchivoDelFilePath(filePath);
            this.filePath = null;
        }
        if (bundleProducto !=null){
            //onBackPressed();
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Toast.makeText(this, "onResume() ProductCreation", Toast.LENGTH_SHORT).show();
/*        AutoCompleteTextView autoCompletTextSpCategoria = findViewById(R.id.product_creation_spCategoria);
        String[] categorias = getResources().getStringArray(R.array.array_producto_categoria_de_prueba);
        ArrayAdapter arrayAdapter = new ArrayAdapter(this, R.layout.custom_text_view_dropdown_menu_producto_categoria, categorias);
        autoCompletTextSpCategoria.setAdapter(arrayAdapter);*/
    }

    @Override
    protected void onPause() {
        super.onPause();
        //Toast.makeText(this, "onPause() ProductCreation", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStop() {
        super.onStop();
        //Toast.makeText(this, "onStop() ProductCreation", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Toast.makeText(this, "onDestroy() ProductCreation", Toast.LENGTH_SHORT).show();
    }




}