package com.paladium.Presentador;

import android.content.Context;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.paladium.R;
import com.paladium.Vista.Fragmentos.Fragment_Balance;
import com.paladium.Vista.Fragmentos.Fragment_Home;
import com.paladium.Vista.Fragmentos.Fragment_Inventario;

public class PresentadorMainActivity {

    private final Context mContext;
    private MeowBottomNavigation bottomNavigation;
    private final String TAG = "PresentadorMainActivity";

    private Fragment_Home fragment_home;
    private Fragment_Balance fragment_balance;
    private Fragment_Inventario fragment_inventario;

    public PresentadorMainActivity(Context mContext) {
        this.mContext = mContext;
        fragment_home = new Fragment_Home();
        fragment_balance = new Fragment_Balance();
        fragment_inventario = new Fragment_Inventario();
    }

    public void crearBottomNavigation(MeowBottomNavigation bottomNav, androidx.fragment.app.FragmentManager supportFragmentManager) {
        this.bottomNavigation = bottomNav;

        bottomNavigation.add(new MeowBottomNavigation.Model(1, R.drawable.store_24px));
        bottomNavigation.add(new MeowBottomNavigation.Model(2, R.drawable.account_balance_wallet_24px));
        bottomNavigation.add(new MeowBottomNavigation.Model(3, R.drawable.inventory_24px));

        bottomNavigation.setOnShowListener(new MeowBottomNavigation.ShowListener() {
            @Override
            public void onShowItem(MeowBottomNavigation.Model item) {
                Fragment fragment = null;

                switch (item.getId()) {
                    case 1:
                        fragment = new Fragment_Home();
                        break;
                    case 2:
                        fragment = new Fragment_Balance();
                        break;
                    case 3:
                        fragment = new Fragment_Inventario();
                        break;
                }
                //cargarFragmentos
                cargarFragmentos(fragment, supportFragmentManager);
            }
        });
        //agrega una burbuja con un número de notificaciones en el boton por su id
        bottomNavigation.setCount(1, "10");
        //setFragment inicial selected
        bottomNavigation.show(1, true);


        bottomNavigation.setOnClickMenuListener(new MeowBottomNavigation.ClickListener() {
            @Override
            public void onClickItem(MeowBottomNavigation.Model item) {

            }
        });

        bottomNavigation.setOnReselectListener(new MeowBottomNavigation.ReselectListener() {
            @Override
            public void onReselectItem(MeowBottomNavigation.Model item) {

            }
        });


    }

    public void materialBottomNavigation(BottomNavigationView bottomNavigationView, androidx.fragment.app.FragmentManager supportFragmentManager) {
        cargarFragmentos(fragment_home, supportFragmentManager);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.bottom_nav_item_home:
                        cargarFragmentos(fragment_home, supportFragmentManager);
                        break;
                    case R.id.bottom_nav_item_balance:
                        cargarFragmentos(fragment_balance, supportFragmentManager);
                        break;
                    case R.id.bottom_nav_item_inventario:
                        cargarFragmentos(fragment_inventario, supportFragmentManager);
                        break;
                }
                return true;
            }
        });
    }

    private void cargarFragmentos(Fragment fragment, FragmentManager supportFragmentManager) {

        supportFragmentManager
                .beginTransaction()
                .addToBackStack(null)
                .replace(R.id.fragmentContainer, fragment)
                .commit();
    }

}
