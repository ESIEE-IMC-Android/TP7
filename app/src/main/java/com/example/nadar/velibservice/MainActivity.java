package com.example.nadar.velibservice;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nadar.velibservice.model.InfoStation;
import com.example.nadar.velibservice.model.ListeDesStationsVelib;
import com.example.nadar.velibservice.model.StationVelib;

import java.io.InputStream;
import java.net.URL;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    Intent intent;
    boolean started;
    ServiceStation timeService; 
    ListView list;

    private ListeDesStationsVelib stations;
    private ServiceConnection connection = new ServiceConnection() {

        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            Log.i("ServiceStation","onServiceConnected");
            timeService = ((ServiceStation.LocalBinder)service).getService();
        }

        public void onServiceDisconnected(ComponentName className){
            // triggered if the service is global and stopped
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);




        list= (ListView) findViewById(R.id.stationsList);

        try {
            this.stations = new ListeDesStationsVelib();
        } catch (Exception e) {
        }
        new ChargeurListeDesStations().execute("http://www.velib.paris.fr/service/carto");


        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = ((TextView) view).getText().toString();
                StationVelib st = stations.lireStation(item);
                new ChargeurInfoStation(st).execute();

            }
        });

    }


    private class ChargeurInfoStation extends AsyncTask<Void, Void, Boolean> {

        private StationVelib stationVelib;
        private InfoStation infoStation;
        public ChargeurInfoStation(StationVelib str) {
            this.stationVelib = str;
        }


        @Override
        protected Boolean doInBackground(Void... voids) {
            infoStation= new InfoStation(stationVelib.getNumber());
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            SharedPreferences sharedPreferences =getSharedPreferences("ahmad",0);
            sharedPreferences.edit().putString("slots","<"+infoStation.getAvailable()+","+infoStation.getFree()+">").apply();

        }
    }


    private class ChargeurListeDesStations extends AsyncTask<String, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(String... str) {
            try {

                URL url = new URL(str[0]);
                stations.chargerDepuisXML(url.openStream());
            } catch (Exception e) {
                e.printStackTrace();
                try {
                    InputStream is = MainActivity.this.getAssets().open("stations.xml");
                    stations.chargerDepuisXML(is);
                } catch (Exception e1) {
                    e1.printStackTrace();
                    return false;
                }
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            list.setAdapter(new ArrayAdapter<String>(MainActivity.this,android.R.layout.simple_list_item_1,stations.lesNomsDesStations()));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.item1) {
            if(isServiceStarted()) Toast.makeText(getApplicationContext(), "Service deja started!", Toast.LENGTH_SHORT).show();
            else{
                intent = new Intent (this, ServiceStation.class);
                intent.putExtra ( "pid", android.os.Process.myPid());
                bindService(intent, connection, Context.BIND_AUTO_CREATE);
                started = true;
            }
        }else{
            if(!isServiceStarted()) Toast.makeText(getApplicationContext(), "Service deja stopped!", Toast.LENGTH_SHORT).show();
            else{
                unbindService(connection);
                started = false;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean isServiceStarted () {
        boolean res = false ;
        ActivityManager am = (ActivityManager) getSystemService ( ACTIVITY_SERVICE );

        List<ActivityManager.RunningServiceInfo> services
                = am.getRunningServices (Integer.MAX_VALUE );
        for (ActivityManager.RunningServiceInfo r: services) {
            String name = getBaseContext().getPackageName();
            if (name.equals(r.service.getPackageName ())) {
                String nameService = getBaseContext().getPackageName() + ".ServiceStation" ;
                if (nameService.equals (r.service.getClassName ())) {
                    return true ;
                }
            }
        }
        return res;
    }


}