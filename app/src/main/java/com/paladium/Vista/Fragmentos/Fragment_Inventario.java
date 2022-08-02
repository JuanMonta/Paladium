package com.paladium.Vista.Fragmentos;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.paladium.Model.Firebase.BaseDeDatos;
import com.paladium.Model.Logica.Producto;
import com.paladium.Model.Utils.Utilidades;
import com.paladium.Presentador.Adapters.CustomRVAdapter_Products_List;
import com.paladium.Presentador.PresentadorMainActivity;
import com.paladium.R;
import com.paladium.Vista.Activities.ProductCreation;
import com.paladium.Vista.Activities.ProductDescription;

import java.util.ArrayList;
import java.util.List;

public class Fragment_Inventario extends Fragment implements CustomRVAdapter_Products_List.ListItemClick, View.OnClickListener {
    private final String TAG = "Fragment_Inventario";
    private CustomRVAdapter_Products_List customAdapterProducts;
    private Toast toast;
    private Context mContext;
    private View mView;
    private RecyclerView customRecycler;
    private Parcelable recyclerViewState;

    private ArrayList<Producto> productosListaPrincipal;
    private ArrayList<Producto> productosListaFiltradaPorCategoria;
    /**
     * Para guarda los ids de los botones que se agregan al toggleButtomGroup de manera
     * programática y que fungen como las categorías de los productos.
     */
    private List<Integer> listaIdsToggleButtoms;

    private TextView tvNumeroRegistros, tvNumeroRegistrosCostoTotal, tvNumeroRegistrosPrecioTotal,tvNumeroRegistrosUtilidadTotal;
    private TextInputEditText inputEdBuscar;
    private MaterialButtonToggleGroup buttonToggleGroup_Categorias;
    private LinearLayout linearLayoutCategoriaContainer;
    /**
     * Para validar para cuando un producto es borrado desde la bd, en el recyclerView
     * no haga el efecto de Blink cuando carga la old y new list de los productos de la Bd.
     * Usado mediante childEventListener en cargarDatosProductosRecycler.
     */
    private boolean validarCalculoDiferenciasListasProductos;
    /**
     * Para validar cuando se está buscando un producto con el inputEd de busqueda,
     * cuando se escribe cambia a true, y cuando se borra el texto cambia a false,
     * sirve tambien para evitar que cuando haya un nuevo dato que se haya agregado o borrado en
     * firebase, no se cargue de nuevo completa la lista en el recyclerview de los productos.
     */
    private boolean buscando;
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
        this.mContext = view.getContext();
        this.mView = view.getRootView();
        this.validarCalculoDiferenciasListasProductos = true;
        //------------------------------------------------------------------------------------------
        this.tvNumeroRegistros = mView.findViewById(R.id.fragment_inventario_tvNumeroRegistros);
        this.tvNumeroRegistrosCostoTotal = mView.findViewById(R.id.fragment_inventario_tvNumeroRegistrosCostoTotal);
        this.tvNumeroRegistrosPrecioTotal = mView.findViewById(R.id.fragment_inventario_tvNumeroRegistrosPrecioTotal);
        this.tvNumeroRegistrosUtilidadTotal = mView.findViewById(R.id.fragment_inventario_tvNumeroRegistrosUtilidadTotal);
        //------------------------------------------------------------------------------------------
        this.inputEdBuscar = mView.findViewById(R.id.fragment_inventario_inputEdBuscar);
        //------------------------------------------------------------------------------------------
        buttonToggleGroup_Categorias = mView.findViewById(R.id.fragment_inventario_ButtonToggleGroup_Categorias);
        linearLayoutCategoriaContainer = mView.findViewById(R.id.fragment_inventario_CategoriasContainer);
        //buttonToggleGroupListner();
        //------------------------------------------------------------------------------------------
        this.customRecycler = mView.findViewById(R.id.fragment_inventario_recyclerV_CustomProducts);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        this.customRecycler.setLayoutManager(linearLayoutManager);
        //------------------------------------------------------------------------------------------
        this.customAdapterProducts = new CustomRVAdapter_Products_List(Fragment_Inventario.this, mContext);
        this.customAdapterProducts.setValidarCalculoDiferenciasListasProductos(this.validarCalculoDiferenciasListasProductos);

        Button btCrearProducto = view.findViewById(R.id.fragment_inventario_btnCrearProducto);
        btCrearProducto.setOnClickListener(this);

        addTextWatcherInputEditText();

        cargarDatosProductosRecycler();

        PresentadorMainActivity.progresBarMainActivity().dismiss();
    }


    private void cargarDatosProductosRecycler() {
        //CARGAR PRODUCTOS-------------------------------------------------------------------------

        //adValueChange Listener escuha cuando un valor se ha cambiado en la base de datos en tiempo real,
        //si cambia en la BD, la Ui se actualiza automáticamente gracias a este método
        DatabaseReference dbProducto = BaseDeDatos.getFireDatabaseIntanceReference().child(Utilidades.nodoPadre).child(Utilidades.nodoProducto);

        ChildEventListener childEventListenerProductos = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                validarCalculoDiferenciasListasProductos = false;
                //Log.d(TAG,"onChildAdded");
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                //Log.d(TAG,"onChildChanged");
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                //Log.d(TAG,"onChildRemoved");
                //cuando un child (que pertenece en este caso a los childs de producto) es eliminado,
                //entonces cambio el boolean a falso para que en el adapatador de los productos del
                //recycler view no cargue l efecto de blink que se colocó para identificar datos
                //que han cambiado.
                validarCalculoDiferenciasListasProductos = false;
                //Log.d(TAG, " childEventListenerProductos: " + validarCalculoDiferenciasListasProductos);
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                //Log.d(TAG,"onChildMoved");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //Log.d(TAG,"onCancelled");
            }
        };

        ValueEventListener valueEventListenerCargarProductos = new ValueEventListener() {
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

                /*Log.d(TAG, "findFirtsVisiblePositionRecyclerView: "+findFirtsVisiblePositionRecyclerView);
                Log.d(TAG, "findFirstCompletelyVisibleItemPositionRecyclerView: "+findFirstCompletelyVisibleItemPositionRecyclerView);
                Log.d(TAG, "findLastVisibleItemPositionRecyclerView: "+findLastVisibleItemPositionRecyclerView);
                Log.d(TAG, "findLastCompletelyVisibleItemPositionRecyclerView: "+findLastCompletelyVisibleItemPositionRecyclerView);*/

                productosListaPrincipal = new ArrayList<>();
                for (DataSnapshot datos : dataSnapshot.getChildren()) {
                    // Log.d(TAG, "dataSnashot: "+datos.getKey());
                    Producto producto = datos.getValue(Producto.class);
                    producto.setProductoFirebaseKey(datos.getKey());
                    productosListaPrincipal.add(producto);
                }
                //si no se está buscando un producto con el inputEd, entoces que agrege la lista
                if (!buscando) {
                    crearRecyclerListaProductos(productosListaPrincipal);
                } else {
                    //tomo el id del boton que está seleccionado del toggleButtonGroup
                    int buttomSelected = buttonToggleGroup_Categorias.getCheckedButtonId();
                    if (listaIdsToggleButtoms != null && listaIdsToggleButtoms.size() > 0) {
                        //recorro toda la lista hasta encontrar el id del boton que está seleccionado
                        for (int i = 0; i < listaIdsToggleButtoms.size(); i++) {
                            if (buttomSelected == listaIdsToggleButtoms.get(i)) {
                                MaterialButton button = (MaterialButton) mView.findViewById(buttomSelected);
                                filtrarProductosPorCategoria(button.getText().toString().trim());
                                break;
                            }
                        }
                    }

                    //si está buscando, entonces que actualice la lista de coincidencias de busqueda
                    //del producto
                    buscarProducto(inputEdBuscar.getText().toString().trim());
                }

                float sumaCostos = 0, sumaPrecios=0, utilidadTotal=0;

                for (int i = 0; i < productosListaPrincipal.size(); i++) {
                    sumaCostos += productosListaPrincipal.get(i).getCosto() * productosListaPrincipal.get(i).getCantidad();
                    sumaPrecios += productosListaPrincipal.get(i).getPrecio() * productosListaPrincipal.get(i).getCantidad();
                }
                utilidadTotal = sumaPrecios-sumaCostos;
                tvNumeroRegistros.setText("" + productosListaPrincipal.size());
                tvNumeroRegistrosCostoTotal.setText("$" + Math.round(sumaCostos * 100.0) / 100.0);
                tvNumeroRegistrosPrecioTotal.setText("$" + Math.round(sumaPrecios * 100.0) / 100.0);
                tvNumeroRegistrosUtilidadTotal.setText("$" + Math.round( utilidadTotal * 100.0) / 100.0);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        dbProducto.addChildEventListener(childEventListenerProductos);
        //dentro de la carga de datos, mandamos a que ordene alfabeticamente por el nombre del producto
        dbProducto.orderByChild(Utilidades.nombreProducto).addValueEventListener(valueEventListenerCargarProductos);



        //CARGAR CATEGORIAS-------------------------------------------------------------------------
        DatabaseReference dbCategoria = BaseDeDatos.getFireDatabaseIntanceReference().child(Utilidades.nodoPadre).child(Utilidades.nodoCategoria);

        ChildEventListener childEventListenerCategorias = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        ValueEventListener valueEventListenerCargarCategoriasProductos = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Producto> listaCategoriasProducto = new ArrayList<>();
                for (DataSnapshot datos : snapshot.getChildren()) {
                    Producto producto = datos.getValue(Producto.class);
                    producto.setCategoriaFirebaseKey(datos.getKey());

                    listaCategoriasProducto.add(producto);
                }
                crearToggleButtonsCategorias(listaCategoriasProducto);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        dbCategoria.addChildEventListener(childEventListenerCategorias);

        dbCategoria.orderByChild(Utilidades.nodoCategoria).addValueEventListener(valueEventListenerCargarCategoriasProductos);
    }


    @Override
    public void onListenItemClick(int itemClicado, Producto producto) {
        PresentadorMainActivity.progresBarMainActivity().show();
        String mensajeToast = "item # " + itemClicado + " clicado.";
        Intent intent = new Intent(mView.getContext(), ProductDescription.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(Utilidades.bundleProduto, producto);
        intent.putExtras(bundle);
        startActivity(intent);

        if (toast != null) {
            toast.cancel();
        }
        //toast.makeText(mView.getContext(), mensajeToast, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fragment_inventario_btnCrearProducto:
                PresentadorMainActivity.progresBarMainActivity().show();
                new android.os.Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        Intent crearProducto = new Intent(view.getContext(), ProductCreation.class);
                        startActivity(crearProducto);
                    }
                });

                break;
        }
    }

    private void crearRecyclerListaProductos(ArrayList<Producto> listaDeProductos) {

        Log.d(TAG, "valor validarDiff: " + validarCalculoDiferenciasListasProductos);
        customAdapterProducts.setValidarCalculoDiferenciasListasProductos(validarCalculoDiferenciasListasProductos);
        /*adapterProducts.notifyItemRangeChanged(findFirstCompletelyVisibleItemPositionRecyclerView, adapterProducts.getItemCount());*/
        customAdapterProducts.dataProductosChangeDiffCallUtil(listaDeProductos);
        customRecycler.setAdapter(customAdapterProducts);
        //notifico que los datos del adaptador han cambiado
        //luego colocaremos la insatncia guardada para recuperar el estado que tenía el
        //recyclerview antes de los nuevos datos
        //adapterProducts.notifyDataSetChanged();
        customRecycler.getLayoutManager().onRestoreInstanceState(recyclerViewState);
        validarCalculoDiferenciasListasProductos = true;
        Log.d(TAG, "colocando validar calculo en true del recycler: " + validarCalculoDiferenciasListasProductos);
    }

    private void crearToggleButtonsCategorias(ArrayList<Producto> listaCategoriasProducto) {
        //para guardar los ids de los botones que agregamos aquí
        this.listaIdsToggleButtoms = new ArrayList<>();
        //si hay un cambio en las categorias, remuevo todos los botones para agregar nuevamente
        this.buttonToggleGroup_Categorias.removeAllViews();
        //primero agrego el boton principal para luego agregar los botones con el nombre de las categorias
        MaterialButton todo = (MaterialButton) getLayoutInflater().inflate(R.layout.custom_boton_categorias, null);
        todo.setText(R.string.btn_categoria_TODO);
        todo.setGravity(Gravity.CENTER);
        todo.setOnClickListener(v -> {
            filtrarProductosPorCategoria(Utilidades.categoriaTodo);
            buscarProducto(inputEdBuscar.getText().toString().trim());
        });
        this.buttonToggleGroup_Categorias.addView(todo);
        this.listaIdsToggleButtoms.add(todo.getId());

        for (int i = 0; i < listaCategoriasProducto.size(); i++) {
            //MaterialButton boton = new MaterialButton(mContext,null, com.google.android.material.R.style.Widget_MaterialComponents_Button_OutlinedButton);
            MaterialButton boton = (MaterialButton) getLayoutInflater().inflate(R.layout.custom_boton_categorias, null);
            boton.setText(listaCategoriasProducto.get(i).getCategoria());
            //boton.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));
            boton.setGravity(Gravity.CENTER);
            boton.setCheckable(true);
            boton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //con cada onClickListener agregado para cada botón puedo independientemete
                    //interactuar con ese onclick, así tomo el texto para filtrar las categorias
                    MaterialButton b = (MaterialButton) v;
                    filtrarProductosPorCategoria(b.getText().toString());
                    buscarProducto(inputEdBuscar.getText().toString().trim());
                    //Log.d(TAG, "Boton click "+b.getText());
                }
            });
            this.buttonToggleGroup_Categorias.addView(boton);
            this.listaIdsToggleButtoms.add(boton.getId());
        }

    }

    private void filtrarProductosPorCategoria(String categoria) {
        //en esta lista se guarda los productos filtrados
        this.productosListaFiltradaPorCategoria = new ArrayList<>();
        //cuando se selecciona el botón que agrega denuevo toda la lista original
        if (categoria.equals(Utilidades.categoriaTodo)) {
            this.productosListaFiltradaPorCategoria = this.productosListaPrincipal;
        } else {
            //si el nombre de la categoria seleccionada coincide con la lista de productos, se agrega.
            //Log.d(TAG,"categoria seleccionada: "+categoria);
            for (int i = 0; i < this.productosListaPrincipal.size(); i++) {
                if (this.productosListaPrincipal.get(i).getCategoria().equals(categoria)) {
                    //Log.d(TAG,"producto agregado: "+this.productosListaPrincipal.get(i).getNombre());
                    this.productosListaFiltradaPorCategoria.add(productosListaPrincipal.get(i));
                }
            }
        }
        //para que no haga comparaciones de listas al filtrar
        this.validarCalculoDiferenciasListasProductos = false;
        //creo el recycler view con los datos filtrados
        crearRecyclerListaProductos(this.productosListaFiltradaPorCategoria);
    }

    private void buscarProducto(String buscar) {
        //valido que durante una busqueda no verifique diferencias en datos de lista
        validarCalculoDiferenciasListasProductos = false;
        //para mostrar en el recycler los productos encontrados mediante la búsqueda
        ArrayList<Producto> listaCoincidencias = new ArrayList();
        //según que tipo de lista se usará para realizar la busqueda de productos
        ArrayList<Producto> listaAUsar;
        //si existe la lista de productos que haya sido filtrada por categorías
        if (this.productosListaFiltradaPorCategoria != null && this.productosListaFiltradaPorCategoria.size() > 0) {
            listaAUsar = this.productosListaFiltradaPorCategoria;
        } else {//si no, la lista donde buscar será la original
            listaAUsar = this.productosListaPrincipal;
        }
        //si la busquena no es vacía
        if (!buscar.isEmpty()) {
            for (int i = 0; i < listaAUsar.size(); i++) {
                if (listaAUsar.get(i).getNombre().trim().contains(buscar)) {
                    listaCoincidencias.add(listaAUsar.get(i));
                }
            }
        } else {//caso contrario, será la lista de búsqueda que corresponda
            listaCoincidencias = listaAUsar;
        }
        crearRecyclerListaProductos(listaCoincidencias);
    }

    private void addTextWatcherInputEditText() {
        this.inputEdBuscar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().trim().length() > 0) {
                    //para que valide que no cargue en el recycler view una nueva lista que provenga
                    //de firebase
                    buscando = true;

                    buscarProducto(s.toString().trim());
                } else {
                    //para que vuelva a cargar datos que provengan de firebase
                    buscando = false;
                    buscarProducto("");
                }
            }
        });
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