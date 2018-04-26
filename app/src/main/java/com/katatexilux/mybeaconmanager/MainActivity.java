package com.katatexilux.mybeaconmanager;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.widget.ImageView;
import android.widget.TextView;

import com.estimote.coresdk.observation.region.RegionUtils;
import com.estimote.coresdk.common.requirements.SystemRequirementsChecker;
import com.estimote.coresdk.observation.region.beacon.BeaconRegion;
import com.estimote.coresdk.recognition.packets.Beacon;
import com.estimote.coresdk.service.BeaconManager;

import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private BeaconManager beaconManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final TextView textView_Titolo = (TextView) findViewById(R.id.textTitolo);
        final TextView textView_Descr = (TextView) findViewById(R.id.textDescr);
        final ImageView imageDescr = (ImageView) findViewById(R.id.imageView2);

        beaconManager = new BeaconManager(getApplicationContext());

        // Set the monitoring settings.
        beaconManager.setBackgroundScanPeriod(1000, 0);

        // Set the exit expiration to it's min (between 1000 and 60000 in ms).
        beaconManager.setRegionExitExpiration(1000);

        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {

            final BeaconRegion region1 = new BeaconRegion(
                    "ice", // <=============================
                    UUID.fromString("B9407F30-F5F8-466E-AFF9-25556B57FE6D"),
                    30, 20);
            final BeaconRegion region2 = new BeaconRegion(
                    "mint", // <=============================
                    UUID.fromString("B9407F30-F5F8-466E-AFF9-25556B57FE6D"),
                    20, 10);
            final BeaconRegion region3 = new BeaconRegion(
                    "blueberry", // <=============================
                    UUID.fromString("B9407F30-F5F8-466E-AFF9-25556B57FE6D"),
                    10, 1);

            public void onServiceReady() {
                /*beaconManager.startMonitoring(new BeaconRegion(
                        "monitored region",
                        UUID.fromString("B9407F30-F5F8-466E-AFF9-25556B57FE6D"),
                        30, 20));
                 */
                //PER TROVARE LA DISTANZA
                // double distance = RegionUtils.computeAccuracy(Beacons);
                beaconManager.startMonitoring(region1);
                beaconManager.startMonitoring(region2);
                beaconManager.startMonitoring(region3);
            }
        });

        beaconManager.setRangingListener(new BeaconManager.BeaconRangingListener() {
            @Override
            public void onBeaconsDiscovered(BeaconRegion region, List<Beacon> list) {
                if (!list.isEmpty()) {
                    Beacon uniqueBeacon = list.get(0);
                    String uuid = uniqueBeacon.getProximityUUID().toString();
                    int major = uniqueBeacon.getMajor();
                    int minor = uniqueBeacon.getMinor();
                    double distance = RegionUtils.computeAccuracy(uniqueBeacon);
                    textView_Descr.setText("UUID: " + uuid + " MAJOR: " + major + " MINOR: " + minor + " DISTANZA; " + distance);

                }
            }
        });

        beaconManager.setMonitoringListener(new BeaconManager.BeaconMonitoringListener() {

            @Override
            public void onEnteredRegion(BeaconRegion region, List<Beacon> beacons) {
                if (region.getIdentifier().equals("ice")) {
                    showNotification(
                            "You are in the ICE region",
                            "Good job dude!");
                    //aggiorna elementi nella activity

                    textView_Titolo.setText("BEACON ICE");
                    imageDescr.setColorFilter(0xffA4D8DE);
                    textView_Descr.setText("This is the ICE region This is the ICE region This is the ICE region This is the ICE region This is the ICE region This is the ICE region This is the ICE region");


                } else if (region.getIdentifier().equals("mint")) {
                    showNotification(
                            "You are in the MINT region",
                            "You can explode!");
                    //aggiorna elementi nella activity
                    textView_Titolo.setText("BEACON MINT");
                    imageDescr.setColorFilter(0xffACE98D);
                    textView_Descr.setText("This is the MINT region This is the MINT region This is the MINT region This is the MINT region This is the MINT region This is the MINT region This is the MINT region");
                } else if (region.getIdentifier().equals("blueberry")) {
                    showNotification(
                            "You are in the BLUEBERRY region",
                            "Go away from here");
                    //aggiorna elementi nella activity
                    textView_Titolo.setText("BEACON BLUEBERRY");
                    imageDescr.setColorFilter(0xffC858FF);
                    textView_Descr.setText("This is the BLUEBERRY region This is the BLUEBERRY regionThis is the BLUEBERRY region This is the BLUEBERRY region This is the BLUEBERRY region This is the BLUEBERRY region ");
                }

            }
            @Override
            public void onExitedRegion(BeaconRegion region) {
                showNotification(
                        "I don't know where you are!",
                        "Damned");
                textView_Titolo.setText("Nothing to show");
                imageDescr.setColorFilter(0xffFFFFFF);
                textView_Descr.setText("I cannot find any area, i'm sorry bro!");
            }
        });
    }

    //Notifica

    public void showNotification(String title, String message) {
        Intent notifyIntent = new Intent(this, MainActivity.class);
        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivities(this, 0,
                new Intent[] { notifyIntent }, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification = new Notification.Builder(this)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .build();
        notification.defaults |= Notification.DEFAULT_SOUND;
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, notification);
    }



    @Override
    protected void onResume() {
        super.onResume();

        SystemRequirementsChecker.checkWithDefaultDialogs(this);
    }


    }

