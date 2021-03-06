package com.paladium.Presentador;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
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

    private static Context mContext;
    private MeowBottomNavigation bottomNavigation;
    private final String TAG = "PresentadorMainActivity";

    private Fragment currentFragment;
    private Fragment_Home fragment_home;
    private Fragment_Balance fragment_balance;
    private Fragment_Inventario fragment_inventario;
    private static ProgressDialog dialog;

    public PresentadorMainActivity(Context mContext) {
        PresentadorMainActivity.mContext = mContext;
        fragment_home = new Fragment_Home();
        fragment_balance = new Fragment_Balance();
        fragment_inventario = new Fragment_Inventario();
        dialog = new ProgressDialog(mContext);

    }
    //llamar al recolector de basura para liberar meoria
    //System.gc();

    public static ProgressDialog progresBarMainActivity() {
        dialog.setMessage(mContext.getString(R.string.progressdialog_CARGANDO));
        dialog.setCancelable(false);
        return dialog;
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
        if (getCurrentFragmento() == null) {
           beginFragmentTransacction(fragment, supportFragmentManager);
        }
        if (getCurrentFragmento() != fragment){
            beginFragmentTransacction(fragment, supportFragmentManager);
        }
    }

    private void beginFragmentTransacction(Fragment fragment, FragmentManager supportFragmentManager){
        progresBarMainActivity().show();

        new android.os.Handler().post(new Runnable() {
            @Override
            public void run() {

                supportFragmentManager
                        .beginTransaction()
                        .addToBackStack(null)
                        .replace(R.id.fragmentContainer, fragment)
                        .commit();

                setCurrentFragmento(fragment);
            }
        });


    }

    private void setCurrentFragmento(Fragment fragmento) {
        this.currentFragment = fragmento;
    }

    private Fragment getCurrentFragmento() {
        return this.currentFragment;
    }

}
