package com.example.koro_.stalertv2;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.app.Notification.Style;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.TextView;

import com.socrata.android.client.Callback;
import com.socrata.android.client.Consumer;
import com.socrata.android.client.Response;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import static com.socrata.android.soql.clauses.Expression.eq;

public class MainActivity extends AppCompatActivity {
    TextView mensaje1;
    TextView mensaje2;
    TextView mensaje3;
    List<Interrupciones> interrupciones;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mensaje1 = (TextView) findViewById(R.id.mensaje_id);
        mensaje2 = (TextView) findViewById(R.id.mensaje_id2);
        mensaje3 = (TextView) findViewById(R.id.mensaje_id3);



        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);
        } else {
            locationStart();
            Consulta("EL PESEBRE");//aqui comparar con la direccion de setLocation
        }

    }
    private void locationStart() {
        LocationManager mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Localizacion Local = new Localizacion();
        Local.setMainActivity(this);
        final boolean gpsEnabled = mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!gpsEnabled) {
            Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(settingsIntent);
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);
            return;
        }
        mlocManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0,Local);
        mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,Local);
        mensaje1.setText("Localizacion agregada");
        mensaje2.setText("");
    }
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 1000) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                locationStart();
                return;
            }
        }
    }

    public void setLocation(Location loc) {
        //Obtener la direccion de la calle a partir de la latitud y la longitud
        if (loc.getLatitude() != 0.0 && loc.getLongitude() != 0.0) {
            try {
                Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                List<Address> list = geocoder.getFromLocation(
                        loc.getLatitude(), loc.getLongitude(), 1);
                if (!list.isEmpty()) {
                    Address DirCalle = list.get(0);
                            mensaje2.setText("Mi direccion es: \n"
                            + DirCalle.getAddressLine(0)+"cod pos:"+DirCalle.getSubLocality());//mirar codigo postal mas aprox (sublocality)

                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
    /* Aqui empieza la Clase Localizacion */
    public class Localizacion implements LocationListener {
        MainActivity mainActivity;

        public MainActivity getMainActivity() {
            return mainActivity;
        }

        public void setMainActivity(MainActivity mainActivity) {
            this.mainActivity = mainActivity;
        }

        @Override
        public void onLocationChanged(Location loc) {
            // Este metodo se ejecuta cada vez que el GPS recibe nuevas coordenadas
            // debido a la deteccion de un cambio de ubicacion

            loc.getLatitude();
            loc.getLongitude();

            String Text = "Mi ubicacion actual es: " + "\n Lat = "
                    + loc.getLatitude() + "\n Long = " + loc.getLongitude();
            mensaje1.setText(Text);
            this.mainActivity.setLocation(loc);
        }

        @Override
        public void onProviderDisabled(String provider) {
            // Este metodo se ejecuta cuando el GPS es desactivado
            mensaje1.setText("GPS Desactivado");
        }

        @Override
        public void onProviderEnabled(String provider) {
            // Este metodo se ejecuta cuando el GPS es activado
            mensaje1.setText("GPS Activado");
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            switch (status) {
                case LocationProvider.AVAILABLE:
                    Log.d("debug", "LocationProvider.AVAILABLE");
                    break;
                case LocationProvider.OUT_OF_SERVICE:
                    Log.d("debug", "LocationProvider.OUT_OF_SERVICE");
                    break;
                case LocationProvider.TEMPORARILY_UNAVAILABLE:
                    Log.d("debug", "LocationProvider.TEMPORARILY_UNAVAILABLE");
                    break;
            }
        }
    }

    public void Consulta(String barrio){//aca hago la consulta por barrio, hay que hacerla por direccion

        Consumer consumer = new Consumer("https://www.datos.gov.co","SXkVB3AXh5ZYQj0F6gTo0cgaB");
        consumer.getObjects("p2d8-3i63",getInterrupcionesQuery(barrio.toUpperCase()),Interrupciones.class, new Callback<List<Interrupciones>>() {
            @Override
            public void onResults(Response<List<Interrupciones>> response) {
                interrupciones = response.getEntity();
                Notificacion("Barrio"+interrupciones.get(0).getBarrio()
                        +"\n inicio:"+interrupciones.get(0).getInicio()
                        +"\n fin:"+interrupciones.get(0).getFin()
                        +"\n servicio:"+interrupciones.get(0).getServicio()
                        +"\n direccion:"+interrupciones.get(0).getDireccion());
            }
        });
    }
    public String getInterrupcionesQuery(String direccion) {//este es el query, funcional modificar barrio por direccion!
        String query = "select %s where %s";
        String select = "barrio, inicio, fin, servicio, direccion";
        String where = "barrio like '%" + direccion + "%'";
        return String.format(query, select, where);
    }
    public void Notificacion(String datos){
        Intent i = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, i, PendingIntent.FLAG_ONE_SHOT);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setAutoCancel(true);
        mBuilder.setContentTitle("STAlert");
        mBuilder.setContentText("Interrupcion en tu zona!"+datos);
        mBuilder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.stalert));
        mBuilder.setSmallIcon(R.drawable.ic_stat_name);
        mBuilder.setContentIntent(pendingIntent);
        final NotificationCompat.BigTextStyle builder = new NotificationCompat.BigTextStyle();
        builder.bigText(datos);
        builder.setBigContentTitle("STAlert");
        mBuilder.setStyle(builder);
        mBuilder.setPriority(NotificationCompat.PRIORITY_MAX);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, builder.build());

    }

}





