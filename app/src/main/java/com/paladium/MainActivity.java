package com.paladium;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.paladium.Logica.Producto;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements CustomRVAdapter_Products.ListItemClick{

    private DatabaseReference rootReference;
    private StorageReference storageReference;
    private RecyclerView customRecyclerView;
    private Producto producto;
    private ArrayList<Producto> listaProductos;
    private CustomRVAdapter_Products adapterProducts;
    private Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Firebase---------------------------------------------------------------------------------
        rootReference = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();
        //------------------------------------------------------------------------------------------
        customRecyclerView = findViewById(R.id.main_recyclerV_CustomProducts);
        customRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        customRecyclerView.setLayoutManager(linearLayoutManager);

        listaProductos = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            producto = new Producto();
            producto.setNombre("Nobre: "+i);
            producto.setCantidad(i);
            producto.setPrecio(2.5+i);
            listaProductos.add(producto);
        }

        adapterProducts = new CustomRVAdapter_Products(listaProductos, this);
        customRecyclerView.setAdapter(adapterProducts);

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