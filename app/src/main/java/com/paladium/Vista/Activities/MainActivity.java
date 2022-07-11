package com.paladium.Vista.Activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.paladium.Model.Firebase.BaseDeDatos;
import com.paladium.Presentador.PresentadorMainActivity;
import com.paladium.R;

public class MainActivity extends AppCompatActivity {


    private MeowBottomNavigation bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BaseDeDatos instanciarFirebase = new BaseDeDatos();

        PresentadorMainActivity presentadorMainActivity = new PresentadorMainActivity(this);
        bottomNavigation = findViewById(R.id.activity_main_BottomNavigation);
        presentadorMainActivity.crearBottomNavigation(bottomNavigation, getSupportFragmentManager());


    }

    //bloquear el boton retroceder
    @Override
    public void onBackPressed() {

    }


}