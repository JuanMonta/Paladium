package com.paladium.Vista.Activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.paladium.Presentador.Customs.PresenterCustomDialog;
import com.paladium.Presentador.PresentadorMainActivity;
import com.paladium.Presentador.PresentadorProductDescription;
import com.paladium.R;

import android.os.Bundle;
import android.view.View;

public class ProductDescription extends AppCompatActivity implements PresenterCustomDialog.ProductoBorradoFinish {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_description);

        View view = findViewById(android.R.id.content).getRootView();
        Bundle bundle = getIntent().getExtras();

        PresentadorProductDescription presentadorProductDescription = new PresentadorProductDescription(ProductDescription.this,bundle, view, this);


    }

    //cuando se borra un producto se carga esta interfaz
    @Override
    public void onProductoBorradoFinish() {
        finish();
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }
}