package com.paladium.Vista.Fragmentos;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.EditText;
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
import com.paladium.Presentador.InterfacePresenter_MainActivity;
import com.paladium.R;
import com.paladium.Vista.Adapters.CustomRVAdapter_Products_List;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Fragment_Inventario#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fragment_Inventario extends Fragment implements CustomRVAdapter_Products_List.ListItemClick {
    private final String TAG= "Fragment_Inventario";
    private RecyclerView customRecyclerView;
    private Producto producto;
    private ArrayList<Producto> listaProductos;
    private CustomRVAdapter_Products_List adapterProducts;
    private Toast toast;
    private Context mContext;

    private InterfacePresenter_MainActivity listener;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    public Fragment_Inventario() {
    }

    public void iniciarListener(InterfacePresenter_MainActivity listener) {
        this.listener = listener;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Fragment_Inventario.
     */
    // TODO: Rename and change types and number of parameters
    public static Fragment_Inventario newInstance(String param1, String param2) {
        Bundle args = new Bundle();
        Fragment_Inventario fragment = new Fragment_Inventario();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
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
        //------------------------------------------------------------------------------------------
        /*customRecyclerView = view.findViewById(R.id.fragment_inventario_recyclerV_CustomProducts);
        //customRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext());
        customRecyclerView.setLayoutManager(linearLayoutManager);

        listaProductos = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            producto = new Producto();
            producto.setNombre("Nombre: " + i);
            producto.setCantidad(i);
            producto.setPrecio(2.5 + i);
            listaProductos.add(producto);
        }

        adapterProducts = new CustomRVAdapter_Products_List(listaProductos, this);
        customRecyclerView.setAdapter(adapterProducts);
*/
        EditText edBuscar = view.findViewById(R.id.fragment_inventario_edBuscar);

        //adValueChange Listener escuha cuando un valor se ha cambiado en la base de datos en tiempo real,
        //si cambia en la BD, la Ui se actualiza automáticamente gracias a este método
        BaseDeDatos.getFireDatabase().child(Utilidades.nodoPadre).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<Producto> productosList = new ArrayList<>();
                for (DataSnapshot datos: dataSnapshot.getChildren()  ) {
                   // Log.d(TAG, "dataSnashot: "+datos.getKey());
                    Producto producto= datos.getValue(Producto.class);
                    producto.setProductoFirebaseKey(datos.getKey());
                    productosList.add(producto);
                }
                RecyclerView customRecycler = view.findViewById(R.id.fragment_inventario_recyclerV_CustomProducts);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext());
                customRecycler.setLayoutManager(linearLayoutManager);
                CustomRVAdapter_Products_List adapterProducts = new CustomRVAdapter_Products_List(productosList, Fragment_Inventario.this);
                customRecycler.setAdapter(adapterProducts);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }





    @Override
    public void onListenItemClick(int itemClicado) {
        String mensajeToast = "item # " + itemClicado + " clicado.";
        if (toast != null) {
            toast.cancel();
        }
        toast.makeText(mContext, mensajeToast, Toast.LENGTH_SHORT).show();
    }










    public void keyBoardisShowing(View view){
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
    }

}