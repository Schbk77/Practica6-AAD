package com.izv.geolocalizacion;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.db4o.Db4oEmbedded;
import com.db4o.ObjectContainer;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class Principal extends ActionBarActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private GoogleApiClient cliente;
    private Location ultimaLocalizacion;
    private LocationRequest peticionLocalizaciones;
    private boolean conectado = false;
    private ObjectContainer bd;

    // On...

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);
        // Conectar con Google Play Services
        conectar();
        // Registrar Broadcast Receiver
        registerReceiver(receptor, new IntentFilter(ServicioIntent.BROADCAST_ACTION));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_principal, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return mostrarMapa();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cliente.disconnect();
        conectado = false;
        unregisterReceiver(receptor);
        stopService(new Intent(this, ServicioIntent.class));
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocationServices.FusedLocationApi.removeLocationUpdates
                (cliente, this);
        cliente.disconnect();
        conectado = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(conectado){
            LocationServices.FusedLocationApi.
                    requestLocationUpdates(cliente, peticionLocalizaciones, this);
        }else{
            conectar();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        cliente.connect();

    }

    @Override
    protected void onStop() {
        super.onStop();
        cliente.disconnect();
        conectado = false;
    }

    // Métodos Auxiliares

    private void actualizarUltimaLocalizacion(Localizacion localizacion) {
        TextView tvFecha = (TextView)findViewById(R.id.tvFecha);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/M/yyyy");
        String date = sdf.format(localizacion.getFecha());
        tvFecha.setText("Fecha: " + date);
        TextView tvCoordenadas = (TextView)findViewById(R.id.tvCoordenadas);
        tvCoordenadas.setText("Coordenadas: "+ localizacion.getLocalizacion().getLatitude()+","+localizacion.getLocalizacion().getLongitude());
        TextView tvLocalidad = (TextView)findViewById(R.id.tvLocalidad);
        tvLocalidad.setText("Localidad:" + localizacion.getLocalidad());
        TextView tvCalle = (TextView)findViewById(R.id.tvCalle);
        tvCalle.setText("Calle:" + localizacion.getCalle());
    }

    private void conectar(){
        if(GooglePlayServicesUtil.isGooglePlayServicesAvailable(this) ==
                ConnectionResult.SUCCESS) {
            cliente = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
            cliente.connect();
            Log.v("CONECTADO", "true");
        } else {
            // Si no esta disponible hay que tratar de instalar GooglePlayServices...
        }
    }

    private boolean mostrarMapa() {
        Intent i = new Intent(Principal.this, Maps.class);
        startActivity(i);
        // Mandar lista a la actividad Maps y mostrar lineas
        return true;
    }

    // Broadcast Receiver

    private BroadcastReceiver receptor = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle b = intent.getExtras();
            Date fecha = (Date) b.getSerializable("fecha");
            Location location = b.getParcelable("localizacion");
            String localidad = b.getString("localidad");
            String calle = b.getString("calle");
            Localizacion localizacion = new Localizacion(fecha, location, localidad, calle);
            // Cargar bd
            bd = Db4oEmbedded.openFile(Db4oEmbedded.newConfiguration(), getExternalFilesDir(null) + "/bd.db4o");
            bd.store(localizacion);
            bd.commit();
            bd.close();
            // Actualizar Ultima Localizacion ...
            actualizarUltimaLocalizacion(localizacion);

        }
    };

    // GoogleApiClient.ConnectionCallbacks

    @Override
    public void onConnected(Bundle bundle) {
        conectado = true;
        if(cliente.isConnected()){
            ultimaLocalizacion = LocationServices.FusedLocationApi.getLastLocation(cliente);
            if (ultimaLocalizacion != null) {
                // hacer algo
            }
            peticionLocalizaciones = new LocationRequest();
            peticionLocalizaciones.setInterval(900000);  // localizaciones cada 15min
            peticionLocalizaciones.setFastestInterval(900000);    //nunca mas rapido de 15min
            peticionLocalizaciones.setPriority(
                    LocationRequest.PRIORITY_HIGH_ACCURACY);
            LocationServices.FusedLocationApi.requestLocationUpdates(cliente,
                    peticionLocalizaciones, this);  // la clase procesa las peticiones en el onLocationChanged*/
        } else {
            cliente.connect();
        }

    }

    @Override
    public void onConnectionSuspended(int i) {
        cliente.connect();
    }

    // LocationListener

    @Override
    public void onLocationChanged(Location location) {
        ServicioIntent.startActionGeocode(this, location);
    }

    // GoogleApiClient.OnConnectionFailedListener

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        cliente.connect();
    }
}

