package com.izv.geolocalizacion;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ServicioIntent extends IntentService {

    private static final String ACCION_GEOCODE = "com.izv.geolocalizacion.action.GEOCODE";
    private static final String EXTRA_PARAM1_LOC = "com.izv.geolocalizacion.extra.LOCATION";
    public static final String BROADCAST_ACTION = "com.izv.geolocalizacion.geocode";

    public static void startActionGeocode(Context context, Location location) {
        Intent intent = new Intent(context, ServicioIntent.class);
        intent.setAction(ACCION_GEOCODE);
        intent.putExtra(EXTRA_PARAM1_LOC, location);
        context.startService(intent);
    }

    public ServicioIntent() {
        super("ServicioIntent");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // Es ejecutado en una hebra en segundo plano y de forma sincronizada
        if (intent != null) {
            final String action = intent.getAction();
            Bundle b = intent.getExtras();
            Location location = b.getParcelable(EXTRA_PARAM1_LOC);
            if(location != null){
                handleAccionGeocode(location);
            }
        }
    }

    private void handleAccionGeocode(Location location) {
        Geocoder geo = new Geocoder(this, Locale.getDefault());
        Intent intent = new Intent(BROADCAST_ACTION);
        try {
            List<Address> direcciones = geo.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            String localidad = "";
            String calle = "";
            if(direcciones.size() > 0){
                Date fecha = new Date();
                Address direccion = direcciones.get(0);
                localidad = direccion.getLocality();
                calle = direccion.getAddressLine(0);
                intent.putExtra("fecha", fecha);
                intent.putExtra("localizacion", location);
                intent.putExtra("localidad", localidad);
                intent.putExtra("calle", calle);
                //Localizacion localizacion = new Localizacion(fecha, location, localidad, calle);
                //intent.putExtra("localizacion", localizacion);
                sendBroadcast(intent);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
