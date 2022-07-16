package com.paladium.Vista.Activities;

import androidx.appcompat.app.AppCompatActivity;

import com.paladium.Presentador.PresentadorProductDescription;
import com.paladium.R;

import android.os.Bundle;
import android.view.View;

public class ProductDescription extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_description);

        View view = findViewById(android.R.id.content).getRootView();
        Bundle bundle = getIntent().getExtras();

        PresentadorProductDescription presentadorProductDescription = new PresentadorProductDescription(this,bundle, view);
    }
}