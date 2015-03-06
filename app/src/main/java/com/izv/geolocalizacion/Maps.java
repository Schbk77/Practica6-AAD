package com.izv.geolocalizacion;

import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.db4o.Db4oEmbedded;
import com.db4o.ObjectContainer;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Maps extends ActionBarActivity implements OnMapReadyCallback {

    private List<Localizacion> localizaciones;
    private ObjectContainer bd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        // Cargar bd
        bd = Db4oEmbedded.openFile(Db4oEmbedded.newConfiguration(), getExternalFilesDir(null) + "/bd.db4o");
        List<Localizacion> locs = bd.query(Localizacion.class);
        localizaciones = new ArrayList<Localizacion>();
        for(Localizacion l : locs){
            localizaciones.add(l);
        }
        bd.close();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        if(localizaciones!=null){
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(localizaciones.get(localizaciones.size()-1).getLocalizacion().getLatitude(),localizaciones.get(0).getLocalizacion().getLongitude()), 13));
            for(int i=0; i<localizaciones.size(); i++){
                if(i+1 < localizaciones.size()){
                    Polyline line = googleMap.addPolyline(new PolylineOptions()
                            .add(new LatLng(localizaciones.get(i).getLocalizacion().getLatitude(), localizaciones.get(i).getLocalizacion().getLongitude()),
                                    new LatLng(localizaciones.get(i+1).getLocalizacion().getLatitude(), localizaciones.get(i+1).getLocalizacion().getLongitude()))
                            .width(5)
                            .color(Color.RED));
                }
            }
        }
    }
}
