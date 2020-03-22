package com.hackiu.covid_19;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.hackiu.covid_19.Watson.MainActivity;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;

import java.util.ArrayList;
import java.util.List;

import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconOffset;

public class MenuActivity extends AppCompatActivity {

    TextView call;
    LottieAnimationView location, notification;
    ImageView bot;
    private MapView mapView;
    private static final String SOURCE_ID = "SOURCE_ID";
    private static final String ICON_ID = "ICON_ID";
    private static final String LAYER_ID = "LAYER_ID";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, "pk.eyJ1IjoiYWJlbGhlcmVkaWEiLCJhIjoiY2s4MG04aDlyMDJpNTNrcjJ6Y2huazVmZSJ9.7STtjX9SG4brvDGwHChAjQ");
        setContentView(R.layout.activity_menu);
        mapView = findViewById(R.id.mapView);

        location = findViewById(R.id.location);
        location.setProgress(98);

        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                location.playAnimation();
            }
        });

        bot = findViewById(R.id.bot);
        bot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MenuActivity.this, MainActivity.class);
                startActivity(i);
            }
        });

        notification = findViewById(R.id.notification);
        notification.setProgress(100);

        notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notification.playAnimation();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        notification.pauseAnimation();
                    }
                },1350);
            }
        });

        call = findViewById(R.id.call);

        LayoutInflater layoutInflater = LayoutInflater.from(MenuActivity.this);
        final View view = layoutInflater.inflate(R.layout.loading_dialog, null);

        final AlertDialog alertDialog = new AlertDialog.Builder(MenuActivity.this)
                .setView(view).create();
        alertDialog.show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                alertDialog.dismiss();
                alertDialog.setCancelable(false);

            }
        },2500);

        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", "113", null));
                startActivity(intent);
            }
        });


        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull MapboxMap mapboxMap) {
                List<Feature> symbolLayerIconFeatureList = new ArrayList<>();
                symbolLayerIconFeatureList.add(Feature.fromGeometry(
                        Point.fromLngLat(-76.928882, -12.043768)));
                symbolLayerIconFeatureList.add(Feature.fromGeometry(
                        Point.fromLngLat(-76.952216, -12.155684)));
                mapboxMap.setStyle(new Style.Builder().fromUri("mapbox://styles/mapbox/cjf4m44iw0uza2spb3q0a7s41")

                        .withImage(ICON_ID, BitmapFactory.decodeResource(
                                MenuActivity.this.getResources(), R.drawable.red_marker))

                        .withSource(new GeoJsonSource(SOURCE_ID,
                                FeatureCollection.fromFeatures(symbolLayerIconFeatureList)))
                        .withLayer(new SymbolLayer(LAYER_ID, SOURCE_ID)
                                .withProperties(PropertyFactory.iconImage(ICON_ID),
                                        iconAllowOverlap(true),
                                        iconOffset(new Float[] {0f, -9f}))
                        ), new Style.OnStyleLoaded() {
                    @Override
                    public void onStyleLoaded(@NonNull Style style) {
                    }
                });
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }
}
