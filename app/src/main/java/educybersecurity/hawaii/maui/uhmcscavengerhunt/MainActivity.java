package educybersecurity.hawaii.maui.uhmcscavengerhunt;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.utils.UrlBeaconUrlCompressor;
import org.w3c.dom.Text;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Collection;

public class MainActivity extends AppCompatActivity implements BeaconConsumer, RangeNotifier {
    private static final int REQUEST_ENABLE_BT = 1;
    private int currentStop;
    private static double radius = 1.1;
    private static final String[] hexURLs = {
            "http://phy.net/qwEwku?t!pxZ", // 218
            "http://phy.net/FbMr9i?tVceh", // 219
            "http://phy.net/9sdWgF?s00H0", // 220
            "http://phy.net/iTkB6J?uNOXp", // 210
            "http://phy.net/jTHh29?tAsU8", // 109
            "http://www.example.com/folder/file6.ext/"
    };
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;
    private BeaconManager mBeaconManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(R.string.title);
        TextView info = (TextView) findViewById(R.id.info);
        TextView next = (TextView) findViewById(R.id.next);
        // Creates a file for keeping track of steps, only happens once also initializes currentStop
        FileWriter fw;
        FileReader fr;
        File file = new File(getFilesDir().getPath().toString() + "/prefs.txt");

        try {
            // Writes 0 at file creation (step 0) once
            file.createNewFile();
            fw = new FileWriter(file, true);
            fw.write(0);
            fw.flush();
            fw.close();

            // Reads from file and initializes currentStop and sets up interface if user is not within first step
            fr = new FileReader(file);
            currentStop = fr.read();
            fr.close();


        } catch (Exception e) {
            e.printStackTrace();
        }

        switch(currentStop) {
            case 0:
                info.setText(R.string.step0_info);
                next.setText(R.string.step0_next);
                break;
            case 1:
                info.setText(R.string.step1_info);
                next.setText(R.string.step1_next);
                break;
            case 2:
                info.setText(R.string.step2_info);
                next.setText(R.string.step2_next);
                break;
            case 3:
                info.setText(R.string.step3_info);
                next.setText(R.string.step3_info);
                break;
            case 4:
                info.setText(R.string.step4_info);
                next.setText(R.string.step4_next);
                break;
            case 5:
                info.setText(R.string.step5_info);
                next.setText(R.string.step5_next);
                break;
        }

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
        if (btAdapter != null && !btAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == RESULT_OK) Log.d("Beacons", "Bluetooth enabled.");
            else Log.d("Beacons", "Bluetooth not enabled!");
        }
    }

    @Override
    public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
        // I/O stuff
        FileWriter fw;
        FileReader fr;
        File file = new File(getFilesDir().getPath().toString() + "/prefs.txt");




        for (Beacon beacon : beacons) {
            //Byte[] url=new Byte[]
            //Log.d("BeaconToString", UrlBeaconUrlCompressor.uncompress(beacon.getId1().toByteArray()));
            //Log.d("whatthefuck",UrlBeaconUrlCompressor.uncompress(UrlBeaconUrlCompressor.compress("http://www.something.net/folder/file.ext")));

            try {

                fr = new FileReader(file);
                currentStop = fr.read();
                fr.close();
                Log.d("Step", Integer.toString(currentStop));
            } catch (Exception e) {
            }

            Log.d("bkon","Current URL: "+UrlBeaconUrlCompressor.uncompress(beacon.getId1().toByteArray()));
            Log.d("CurrentStop", Integer.toString(currentStop));
            String url = UrlBeaconUrlCompressor.uncompress(beacon.getId1().toByteArray());
            getIntent().putExtra("test",url);

//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    String s = getIntent().getStringExtra("test");
//                    Toast.makeText(getApplicationContext(),s , Toast.LENGTH_SHORT).show();
//                }
//            });
            try {
                if (new String(UrlBeaconUrlCompressor.compress(hexURLs[currentStop]), "UTF-8").equals(new String(beacon.getId1().toByteArray(), "UTF-8"))) {
                    //NEXT STOP DETECTED

                    if (beacon.getDistance() <= radius) {

                        //USER HAS VISITED THE LOCATION

                        switch (currentStop) {
                            case 0:
                                // update UI to step 1
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        TextView info = (TextView) findViewById(R.id.info);
                                        TextView next = (TextView) findViewById(R.id.next);
                                        info.setText(R.string.step1_info);
                                        next.setText(R.string.step1_next);
                                    }
                                });


                                // Increases currentStop by 1 and writes it to file
                                currentStop++;
                                fw = new FileWriter(file, false);
                                fw.write(currentStop);
                                fw.flush();
                                fw.close();
                                Log.d("Step", Integer.toString(currentStop));
                                break;
                            case 1:
                                // update UI to step 2
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        TextView info = (TextView) findViewById(R.id.info);
                                        TextView next = (TextView) findViewById(R.id.next);
                                        info.setText(R.string.step2_info);
                                        next.setText(R.string.step2_next);
                                    }
                                });

                                // Increases currentStop by 1 and writes it to file
                                currentStop++;
                                fw = new FileWriter(file, false);
                                fw.write(currentStop);
                                fw.flush();
                                fw.close();
                                Log.d("Step", Integer.toString(currentStop));
                                break;
                            case 2:
                                // update UI to step 3
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        TextView info = (TextView) findViewById(R.id.info);
                                        TextView next = (TextView) findViewById(R.id.next);
                                        info.setText(R.string.step3_info);
                                        next.setText(R.string.step3_next);
                                    }
                                });

                                // Increases currentStop by 1 and writes it to file
                                currentStop++;
                                fw = new FileWriter(file, false);
                                fw.write(currentStop);
                                fw.flush();
                                fw.close();
                                Log.d("Step", Integer.toString(currentStop));
                                break;
                            case 3:
                                // update UI to step 4
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        TextView info = (TextView) findViewById(R.id.info);
                                        TextView next = (TextView) findViewById(R.id.next);
                                        info.setText(R.string.step4_info);
                                        next.setText(R.string.step4_next);
                                    }
                                });

                                // Increases currentStop by 1 and writes it to file
                                currentStop++;
                                fw = new FileWriter(file, false);
                                fw.write(currentStop);
                                fw.flush();
                                fw.close();
                                Log.d("Step", Integer.toString(currentStop));
                                radius = 1.5;
                                break;
                            case 4:
                                // update UI to step 5
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        TextView info = (TextView) findViewById(R.id.info);
                                        TextView next = (TextView) findViewById(R.id.next);
                                        info.setText(R.string.step5_info);
                                        next.setText(R.string.step5_next);
                                    }
                                });

                                // Increases currentStop by 1 and writes it to file
                                currentStop++;
                                fw = new FileWriter(file, false);
                                fw.write(currentStop);
                                fw.flush();
                                fw.close();
                                Log.d("Step", Integer.toString(currentStop));
                                break;
                        }
                    }
                }
            } catch (Exception e) {
            }
        }

    }

    public void reset(View v) {
        FileWriter fw;
        File file = new File(getFilesDir().getPath().toString() + "/prefs.txt");

        try {
            fw = new FileWriter(file, false);
            fw.write(0);
            fw.flush();
            fw.close();
        } catch (Exception e) {}

        currentStop = 0;
        TextView info = (TextView) findViewById(R.id.info);
        TextView next = (TextView) findViewById(R.id.next);

        switch(currentStop) {
            case 0:
                info.setText(R.string.step0_info);
                next.setText(R.string.step0_next);
                break;
            case 1:
                info.setText(R.string.step1_info);
                next.setText(R.string.step1_next);
                break;
            case 2:
                info.setText(R.string.step2_info);
                next.setText(R.string.step2_next);
                break;
            case 3:
                info.setText(R.string.step3_info);
                next.setText(R.string.step3_next);
                break;
            case 4:
                info.setText(R.string.step4_info);
                next.setText(R.string.step4_next);
                break;
            case 5:
                info.setText(R.string.step5_info);
                next.setText(R.string.step5_next);
                break;
        }
    }
    public void plusOne(View v) {
        FileWriter fw;
        File file = new File(getFilesDir().getPath().toString() + "/prefs.txt");
        currentStop++;
        try {
            fw = new FileWriter(file, false);
            fw.write(currentStop);
            fw.flush();
            fw.close();
        } catch (Exception e) {}

        TextView info = (TextView) findViewById(R.id.info);
        TextView next = (TextView) findViewById(R.id.next);

        switch(currentStop) {
            case 0:
                info.setText(R.string.step0_info);
                next.setText(R.string.step0_next);
                break;
            case 1:
                info.setText(R.string.step1_info);
                next.setText(R.string.step1_next);
                break;
            case 2:
                info.setText(R.string.step2_info);
                next.setText(R.string.step2_next);
                break;
            case 3:
                info.setText(R.string.step3_info);
                next.setText(R.string.step3_next);
                break;
            case 4:
                info.setText(R.string.step4_info);
                next.setText(R.string.step4_next);
                break;
            case 5:
                info.setText(R.string.step5_info);
                next.setText(R.string.step5_next);
                break;
        }
    }

    public void onBeaconServiceConnect() {
        Log.d("Beacons", "How about here?");
        Region region = new Region("all-beacons-region", null, null, null);
        try {
            mBeaconManager.startRangingBeaconsInRegion(region);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        mBeaconManager.setRangeNotifier(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        mBeaconManager = BeaconManager.getInstanceForApplication(this.getApplicationContext());
        // Detect the main Eddystone-UID frame:
        mBeaconManager.getBeaconParsers().add(new BeaconParser().
                setBeaconLayout("s:0-1=feaa,m:2-2=10,p:3-3:-41,i:4-20v"));
        mBeaconManager.bind(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        mBeaconManager.unbind(this);
    }


    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://educybersecurity.hawaii.maui.uhmcscavengerhunt/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://educybersecurity.hawaii.maui.uhmcscavengerhunt/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }
}
