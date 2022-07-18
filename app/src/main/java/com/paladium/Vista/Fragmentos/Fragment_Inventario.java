package com.paladium.Vista.Fragmentos;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.paladium.Model.Firebase.BaseDeDatos;
import com.paladium.Model.Logica.Producto;
import com.paladium.Model.Utils.Utilidades;
import com.paladium.Vista.Activities.ProductCreation;
import com.paladium.R;
import com.paladium.Vista.Activities.ProductDescription;
import com.paladium.Vista.Adapters.CustomRVAdapter_Products_List;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Fragment_Inventario#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fragment_Inventario extends Fragment implements CustomRVAdapter_Products_List.ListItemClick , View.OnClickListener{
    private final String TAG= "Fragment_Inventario";
    private ArrayList<Producto> listaProductos;
    private CustomRVAdapter_Products_List adapterProducts;
    private Toast toast;
    private Context mContext;
    private View mView;
    private RecyclerView customRecycler;
    private Parcelable recyclerViewState;
    private int findFirtsVisiblePositionRecyclerView;
    private int findFirstCompletelyVisibleItemPositionRecyclerView;
    private int findLastVisibleItemPositionRecyclerView;
    private int findLastCompletelyVisibleItemPositionRecyclerView;

    public Fragment_Inventario() {
    }


    public static Fragment_Inventario newInstance(String param1, String param2) {
        Fragment_Inventario fragment = new Fragment_Inventario();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_inventario, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mContext = view.getContext();
        mView = view.getRootView();
        //------------------------------------------------------------------------------------------
        customRecycler = mView.findViewById(R.id.fragment_inventario_recyclerV_CustomProducts);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        customRecycler.setLayoutManager(linearLayoutManager);
        //------------------------------------------------------------------------------------------
        adapterProducts = new CustomRVAdapter_Products_List( Fragment_Inventario.this, mContext);

        Button btCrearProducto = view.findViewById(R.id.fragment_inventario_btnCrearProducto);

        btCrearProducto.setOnClickListener(this);

        cargarDatosProductosRecycler();
    }

    private void cargarDatosProductosRecycler() {
        //adValueChange Listener escuha cuando un valor se ha cambiado en la base de datos en tiempo real,
        //si cambia en la BD, la Ui se actualiza automáticamente gracias a este método
        BaseDeDatos.getFireDatabaseIntanceReference().child(Utilidades.nodoPadre).child(Utilidades.nodoProducto).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                /*LinearLayoutManager linearLayout = (LinearLayoutManager) (customRecycler.getLayoutManager());
                findFirtsVisiblePositionRecyclerView = linearLayout.findFirstVisibleItemPosition();
                findFirstCompletelyVisibleItemPositionRecyclerView = linearLayout.findFirstCompletelyVisibleItemPosition();
                findLastVisibleItemPositionRecyclerView = linearLayout.findLastVisibleItemPosition();
                findLastCompletelyVisibleItemPositionRecyclerView = linearLayout.findLastCompletelyVisibleItemPosition();*/

                // https://stackoverflow.com/questions/28658579/refreshing-data-in-recyclerview-and-keeping-its-scroll-position
                // https://stackoverflow.com/questions/24989218/get-visible-items-in-recyclerview
                //para mantener el scrool o pisicion de los itemes que actualmente se ve en pantalla,
                //de esta manera cuando se inserte datos o se actuelicen, el reciclerview no vuelva al inicio.
                //Esto se utiliza más abajo, pero primero guardo la intancia antes de que los datos cambien.
                recyclerViewState = customRecycler.getLayoutManager().onSaveInstanceState();

                Log.d(TAG, "findFirtsVisiblePositionRecyclerView: "+findFirtsVisiblePositionRecyclerView);
                Log.d(TAG, "findFirstCompletelyVisibleItemPositionRecyclerView: "+findFirstCompletelyVisibleItemPositionRecyclerView);
                Log.d(TAG, "findLastVisibleItemPositionRecyclerView: "+findLastVisibleItemPositionRecyclerView);
                Log.d(TAG, "findLastCompletelyVisibleItemPositionRecyclerView: "+findLastCompletelyVisibleItemPositionRecyclerView);

                ArrayList<Producto> productosList = new ArrayList<>();
                for (DataSnapshot datos: dataSnapshot.getChildren()  ) {
                   // Log.d(TAG, "dataSnashot: "+datos.getKey());
                    Producto producto= datos.getValue(Producto.class);
                    producto.setProductoFirebaseKey(datos.getKey());
                    productosList.add(producto);
                }


                /*adapterProducts.notifyItemRangeChanged(findFirstCompletelyVisibleItemPositionRecyclerView, adapterProducts.getItemCount());*/
                adapterProducts.dataProductosChangeDiffCallUtil(productosList);
                customRecycler.setAdapter(adapterProducts);
                //notifico que los datos del adaptador han cambiado
                //luego colocaremos la insatncia guardada para recuperar el estado que tenía el
                //recyclerview antes de los nuevos datos
                //adapterProducts.notifyDataSetChanged();
                customRecycler.getLayoutManager().onRestoreInstanceState(recyclerViewState);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    @Override
    public void onListenItemClick(int itemClicado, Producto producto) {
        String mensajeToast = "item # " + itemClicado + " clicado.";
        Intent intent = new Intent(mView.getContext(), ProductDescription.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(Utilidades.bundleProduto, producto);
        intent.putExtras(bundle);
        startActivity(intent);

        if (toast != null) {
            toast.cancel();
        }
        toast.makeText(mView.getContext(), mensajeToast, Toast.LENGTH_SHORT).show();
    }



    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.fragment_inventario_btnCrearProducto:
                Intent crearProducto = new Intent(view.getContext(), ProductCreation.class);
                startActivity(crearProducto);
                break;
        }
    }



    /*public void keyBoardisShowing(View view){
        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                view.getWindowVisibleDisplayFrame(r);
                int screenHeight = view.getRootView().getHeight();
                //r.Bottom is the position above soft keypad or device key button.
                //if key pad is shown, the r.bottom is smaller than that before
                int keyPadHeigth = screenHeight - r.bottom;
                //0.15 ratio is perhaps enough to determine keypad height
                if (keyPadHeigth > screenHeight * 0.15) {
                    //keyboard is opened
                    listener.ocultarTeclado(false);
                } else {
                    //keyboard is closed
                    listener.ocultarTeclado(true);
                }
            }
        });
    }*/


}