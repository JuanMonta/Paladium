package com.paladium.Vista.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Toast;

import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;
import com.paladium.Model.Logica.Producto;
import com.paladium.Presentador.PresentadorMainActivity;
import com.paladium.R;
import com.paladium.Vista.Adapters.CustomRVAdapter_Products_List;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements CustomRVAdapter_Products_List.ListItemClick{

    private DatabaseReference rootReference;
    private StorageReference storageReference;
    private RecyclerView customRecyclerView;
    private Producto producto;
    private ArrayList<Producto> listaProductos;
    private CustomRVAdapter_Products_List adapterProducts;
    private Toast toast;

    private MeowBottomNavigation bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
       /* // Firebase---------------------------------------------------------------------------------
        rootReference = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();
        //------------------------------------------------------------------------------------------
        customRecyclerView = findViewById(R.id.main_recyclerV_CustomProducts);
        //customRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        customRecyclerView.setLayoutManager(linearLayoutManager);

        listaProductos = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            producto = new Producto();
            producto.setNombre("Nombre: "+i);
            producto.setCantidad(i);
            producto.setPrecio(2.5+i);
            listaProductos.add(producto);
        }

        adapterProducts = new CustomRVAdapter_Products_List(listaProductos, this);
        customRecyclerView.setAdapter(adapterProducts);*/


        PresentadorMainActivity presentadorMainActivity = new PresentadorMainActivity(this);
        bottomNavigation = findViewById(R.id.activity_main_BottomNavigation);
        presentadorMainActivity.crearBottomNavigation(bottomNavigation, getSupportFragmentManager());



    }


    @Override
    public void onListenItemClick(int itemClicado) {
        String mensajeToast = "item # "+itemClicado+" clicado.";
        if(toast !=null){
            toast.cancel();
        }
        toast.makeText(this, mensajeToast, Toast.LENGTH_SHORT).show();
    }
}