package com.example.coba.posindonesia.Activity;

import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.Fade;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.coba.posindonesia.Fragment.DetailBottomSheet;
import com.example.coba.posindonesia.Interfaces.RequestAPI;
import com.example.coba.posindonesia.Model.Resi;
import com.example.coba.posindonesia.R;
import com.example.coba.posindonesia.Session.SessionManager;
import com.example.coba.posindonesia.Url.BaseUrl;
import com.here.android.mpa.mapping.MapFragment;
import com.here.android.mpa.common.GeoCoordinate;
import com.here.android.mpa.mapping.Map;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.here.android.mpa.common.OnEngineInitListener;
import com.here.android.mpa.mapping.MapObject;

import org.w3c.dom.Text;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DetailResiActivity extends AppCompatActivity{

//    private RecyclerView recyclerView;
//    private RecyclerView.Adapter adapter;
//    private RecyclerView.LayoutManager layoutManager;
//    private ArrayList<String> dataSet;

//    @Override
//    public void onMapReady(Map map) {
//        map.setCenter(new GeoCoordinate(37.7397, -121.4252, 0.0), Map.Animation.NONE);
//        map.setZoomLevel((map.getMaxZoomLevel() + map.getMinZoomLevel()) / 2);
//    }

    private Map map = null;
    private MapFragment mapFragment = null;
    private MapObject mapObject;
    BaseUrl baseUrl = new BaseUrl();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_resi);
        final OnMapReadyCallback onMapReadyCallback = null;
        TextView noDetailResi = this.findViewById(R.id.noDetailResi);
        Log.i("SRRRR", getIntent().getStringExtra("NoResi"));
        noDetailResi.setText(getIntent().getStringExtra("NoResi"));

        getDetailResi(getIntent().getStringExtra("NoResi"),"107.573117","-6.9032739");

        Fade fade = new Fade();
        View decor = getWindow().getDecorView();
        fade.excludeTarget(decor.findViewById(R.id.action_bar_container), true);
        fade.excludeTarget(android.R.id.statusBarBackground, true);
        fade.excludeTarget(android.R.id.navigationBarBackground, true);

        getWindow().setEnterTransition(fade);
        getWindow().setExitTransition(fade);

        getWindow().setAllowReturnTransitionOverlap(false);

//        detailResiBtn.findViewById(R.id.detailResiBtn);
//        detailResiBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                bottom = new DetailBottomSheet();
//                bottom.show(getSupportFragmentManager(), bottom.getTag());
//            }
//        });

        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.mapfragment);
        mapFragment.init(new OnEngineInitListener() {
            @Override
            public void onEngineInitializationCompleted(OnEngineInitListener.Error error) {
                if (error == OnEngineInitListener.Error.NONE) {
                    map = mapFragment.getMap();
                    map.setCenter(new GeoCoordinate(-6.874230, 107.616400), Map.Animation.NONE);
                    map.setZoomLevel(map.getMaxZoomLevel());
                    

                }else{
                    Log.i("HERE ERR", error.getDetails());
                }
            }
        });

        Button button = (Button) findViewById(R.id.detailResiBtn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSheetDialogFragment bottom = new DetailBottomSheet();
                bottom.show(getSupportFragmentManager(),bottom.getTag());
            }
        });

//        FloatingActionButton refresh_button = (FloatingActionButton) findViewById(R.id.refresh_button);
//        refresh_button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //do here
//            }
//        });

    }






//        ///adapter///
//        dataSet = new ArrayList<>();
//        setDataSet();
//        recyclerView = new RecyclerView(DetailResiActivity.this);
//        recyclerView.findViewById(R.id.detil_recycleview);
//        recyclerView.setHasFixedSize(true);
//
//        layoutManager = new LinearLayoutManager(this);
//        recyclerView.setLayoutManager(layoutManager);
//
//        adapter = new RecyclerViewAdapter(dataSet);
//        recyclerView.setAdapter(adapter);
//        ////


//    private void setDataSet(){
//        dataSet.add("HALO SATU DUA TIGA");
//        dataSet.add("hehehe");
//        dataSet.add("hehehe");
//        dataSet.add("hehehe");
//        dataSet.add("hehehe");
//        dataSet.add("hehehe");
//        dataSet.add("hehehe");
//        dataSet.add("hehehe");
//        dataSet.add("hehehe");
//    }


//GET URUTAN PAKET

    protected void getDetailResi(final String noResi, String lon, String lat){
        Retrofit retrofit = new Retrofit.Builder().baseUrl(baseUrl.getUrl()).addConverterFactory(GsonConverterFactory.create()).build();
        RequestAPI requestAPI = retrofit.create(RequestAPI.class);

        Call<Resi> resiCall = requestAPI.getResi(noResi, lon, lat);
        resiCall.enqueue(new Callback<Resi>() {
            @Override
            public void onResponse(Call<Resi> call, Response<Resi> response) {
                Log.i("succ", response.message());

                TextView etaResi = findViewById(R.id.etaPackage);
                
                View dsV = new DetailBottomSheet().getView();
                SessionManager sessionManager = new SessionManager(DetailResiActivity.this);
                String summ = response.body().getMessage().getSum_packet_delivered() + "/" + response.body().getMessage().getTotal_packet();
                String eta = response.body().getMessage().getEstimation_time();
                int maxLength = (eta.length() < 3)?eta.length():3;
                String cutString = eta.substring(0, maxLength) + " Minutes";
                etaResi.setText(cutString);
                sessionManager.setSummary(summ);
                sessionManager.setYours(response.body().getMessage().getYour_packet());
                sessionManager.setResi(noResi);
                sessionManager.setLatitude(response.body().getMessage().getLatitude());
                sessionManager.setLongitude(response.body().getMessage().getLongitude());
            }

            @Override
            public void onFailure(Call<Resi> call, Throwable t) {
                Log.i("errr", t.getMessage());
            }
        });

    }

}