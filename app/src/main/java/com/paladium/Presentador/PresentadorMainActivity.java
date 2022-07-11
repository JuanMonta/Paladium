package com.paladium.Presentador;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.paladium.R;
import com.paladium.Vista.Fragmentos.Fragment_Balance;
import com.paladium.Vista.Fragmentos.Fragment_Home;
import com.paladium.Vista.Fragmentos.Fragment_Inventario;

public class PresentadorMainActivity implements InterfacePresenter_MainActivity {

    private final Context mContext;
    private MeowBottomNavigation bottomNavigation;

    public PresentadorMainActivity(Context mContext) {
        this.mContext = mContext;
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
                        ((Fragment_Inventario) fragment).iniciarListener(PresentadorMainActivity.this);
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

    private void cargarFragmentos(Fragment fragment, FragmentManager supportFragmentManager) {
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();
    }


    @Override
    public void ocultarTeclado(boolean ocultar) {

    }
}
