package com.example.proyectomovil.UI;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import com.example.proyectomovil.R;
import com.mapbox.maps.MapView;
import com.mapbox.maps.Style;
import com.mapbox.geojson.Point;
import com.mapbox.maps.CameraOptions;
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions;
import com.mapbox.maps.extension.style.layers.properties.generated.IconAnchor;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class RouteActivity extends BaseNavigationActivity {
    private MapView mapView;
    private List<Point> stopPoints = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_route);

        setupNavigation();

        mapView = findViewById(R.id.mapView);
        
        mapView.getMapboxMap().loadStyle(Style.MAPBOX_STREETS, style -> {
            cargarRutaYParadas();
        });
    }

    @Override
    protected int getNavigationIndex() {
        return 1;
    }

    private void cargarRutaYParadas() {
        new Thread(() -> {
            try {
                SharedPreferences prefs = getSharedPreferences("MiAppPrefs", MODE_PRIVATE);
                String email = prefs.getString("LOGGED_IN_USER_EMAIL", "");
                
                if (email.isEmpty()) {
                    runOnUiThread(() -> {
                        android.widget.Toast.makeText(this, "Error: No se encontró el email del usuario", android.widget.Toast.LENGTH_SHORT).show();
                    });
                    return;
                }

                URL urlDriver = new URL("http://10.0.2.2:5090/api/driver/email/" + email);
                HttpURLConnection connDriver = (HttpURLConnection) urlDriver.openConnection();
                connDriver.setRequestMethod("GET");

                BufferedReader readerDriver = new BufferedReader(new InputStreamReader(connDriver.getInputStream()));
                StringBuilder responseDriver = new StringBuilder();
                String lineDriver;
                while ((lineDriver = readerDriver.readLine()) != null) {
                    responseDriver.append(lineDriver);
                }
                readerDriver.close();

                JSONObject driverJson = new JSONObject(responseDriver.toString());
                String routeId = driverJson.getString("RouteId");

                URL urlStops = new URL("http://10.0.2.2:5090/api/stop/route/" + routeId);
                HttpURLConnection connStops = (HttpURLConnection) urlStops.openConnection();
                connStops.setRequestMethod("GET");

                BufferedReader readerStops = new BufferedReader(new InputStreamReader(connStops.getInputStream()));
                StringBuilder responseStops = new StringBuilder();
                String lineStops;
                while ((lineStops = readerStops.readLine()) != null) {
                    responseStops.append(lineStops);
                }
                readerStops.close();

                JSONArray stopsArray = new JSONArray(responseStops.toString());
                
                stopPoints.clear();
                for (int i = 0; i < stopsArray.length(); i++) {
                    try {
                        JSONObject stopJson = stopsArray.getJSONObject(i);
                        double lat = stopJson.getDouble("Latitude");
                        double lng = stopJson.getDouble("Longitude");
                        stopPoints.add(Point.fromLngLat(lng, lat));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                
                runOnUiThread(() -> {
                    if (!stopPoints.isEmpty()) {
                        mapView.getMapboxMap().setCamera(
                                new CameraOptions.Builder()
                                        .center(stopPoints.get(0))
                                        .zoom(13.0)
                                        .build()
                        );
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mapView != null) {
            mapView.onStart();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mapView != null) {
            mapView.onStop();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mapView != null) {
            mapView.onDestroy();
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (mapView != null) {
            mapView.onLowMemory();
        }
    }
}
