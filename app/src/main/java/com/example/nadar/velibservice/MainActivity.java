package com.example.nadar.velibservice;

import android.app.ActivityManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    Intent intent;
    boolean started;
    ServiceStation timeService;

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

        Button getCount = (Button) findViewById(R.id.getCount);
        final EditText countText = (EditText) findViewById(R.id.countText);

        getCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(started)
                    countText.setText("count: " + timeService.getCount());
                else
                    countText.setText("service non démarré");

            }
        });
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