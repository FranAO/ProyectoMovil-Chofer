package com.example.proyectomovil;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.cardview.widget.CardView;

public class MainActivity extends BaseNavigationActivity {
    CardView settingsButton;
    TextView tvNombreChofer, tvCodigoVehiculo, tvPlacaVehiculo, tvAsientosDispo;
    
    private SharedPreferences prefs;
    private static final String PREFS_NAME = "MiAppPrefs";
    private static final String LOGGED_IN_USER_EMAIL = "LOGGED_IN_USER_EMAIL";
    private String driverId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        settingsButton = findViewById(R.id.settingsCard);
        tvNombreChofer = findViewById(R.id.tvNombreChofer);
        tvCodigoVehiculo = findViewById(R.id.tvCodigoVehiculo);
        tvPlacaVehiculo = findViewById(R.id.tvPlacaVehiculo);
        tvAsientosDispo = findViewById(R.id.tvAsientosDispo);

        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        setupNavigation();
        cargarDatosDriver();

        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });
    }

    private void cargarDatosDriver() {
        String email = prefs.getString(LOGGED_IN_USER_EMAIL, null);
        if (email == null) {
            return;
        }

        new Thread(() -> {
            try {
                java.net.URL url = new java.net.URL("http://10.0.2.2:5090/api/driver/email/" + email);
                java.net.HttpURLConnection con = (java.net.HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");

                if (con.getResponseCode() == java.net.HttpURLConnection.HTTP_OK) {
                    java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.InputStreamReader(con.getInputStream()));
                    StringBuilder respuesta = new StringBuilder();
                    String linea;

                    while ((linea = reader.readLine()) != null) {
                        respuesta.append(linea);
                    }

                    org.json.JSONObject driver = new org.json.JSONObject(respuesta.toString());
                    driverId = driver.optString("id");
                    String firstName = driver.optString("firstName");
                    String lastName = driver.optString("lastName");
                    String assignedBusId = driver.optString("assignedBusId");

                    runOnUiThread(() -> {
                        tvNombreChofer.setText(firstName + " " + lastName);
                    });

                    if (assignedBusId != null && !assignedBusId.isEmpty() && !assignedBusId.equals("null")) {
                        cargarDatosBus(assignedBusId);
                    }
                    
                    if (driverId != null && !driverId.isEmpty() && !driverId.equals("null")) {
                        cargarTripActivo(driverId);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void cargarDatosBus(String busId) {
        new Thread(() -> {
            try {
                java.net.URL url = new java.net.URL("http://10.0.2.2:5090/api/bus/" + busId);
                java.net.HttpURLConnection con = (java.net.HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");

                if (con.getResponseCode() == java.net.HttpURLConnection.HTTP_OK) {
                    java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.InputStreamReader(con.getInputStream()));
                    StringBuilder respuesta = new StringBuilder();
                    String linea;

                    while ((linea = reader.readLine()) != null) {
                        respuesta.append(linea);
                    }

                    org.json.JSONObject bus = new org.json.JSONObject(respuesta.toString());
                    String busCode = bus.optString("busCode");
                    String plate = bus.optString("plate");

                    runOnUiThread(() -> {
                        tvCodigoVehiculo.setText(busCode);
                        tvPlacaVehiculo.setText(plate);
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void cargarTripActivo(String driverId) {
        new Thread(() -> {
            try {
                java.net.URL url = new java.net.URL("http://10.0.2.2:5090/api/trip/driver/" + driverId);
                java.net.HttpURLConnection con = (java.net.HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");

                if (con.getResponseCode() == java.net.HttpURLConnection.HTTP_OK) {
                    java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.InputStreamReader(con.getInputStream()));
                    StringBuilder respuesta = new StringBuilder();
                    String linea;

                    while ((linea = reader.readLine()) != null) {
                        respuesta.append(linea);
                    }

                    org.json.JSONArray trips = new org.json.JSONArray(respuesta.toString());
                    if (trips.length() > 0) {
                        org.json.JSONObject trip = trips.getJSONObject(0);
                        int occupiedSeats = trip.optInt("occupiedSeats");
                        int totalSeats = trip.optInt("totalSeats");

                        runOnUiThread(() -> {
                            tvAsientosDispo.setText(occupiedSeats + " / " + totalSeats);
                        });
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    @Override
    protected int getNavigationIndex() {
        return 0;
    }
}