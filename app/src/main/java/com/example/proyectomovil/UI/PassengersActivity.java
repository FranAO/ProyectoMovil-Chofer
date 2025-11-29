package com.example.proyectomovil.UI;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyectomovil.Adapters.PassengerAdapter;
import com.example.proyectomovil.Models.Passenger;
import com.example.proyectomovil.R;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PassengersActivity extends BaseNavigationActivity {
    private RecyclerView recyclerViewPasajeros;
    private PassengerAdapter adapter;
    private List<Passenger> listaPasajeros;
    private Map<String, String> ticketStatusMap = new HashMap<>();
    private static final int SCAN_QR_REQUEST = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_passengers);

        setupNavigation();

        recyclerViewPasajeros = findViewById(R.id.recyclerViewPasajeros);
        recyclerViewPasajeros.setLayoutManager(new LinearLayoutManager(this));

        listaPasajeros = new ArrayList<>();
        adapter = new PassengerAdapter(listaPasajeros, this::onScanQRClicked);
        recyclerViewPasajeros.setAdapter(adapter);

        cargarPasajeros();
    }

    @Override
    protected int getNavigationIndex() {
        return 2;
    }

    private void onScanQRClicked(Passenger passenger) {
        Intent intent = new Intent(this, CameraActivity.class);
        intent.putExtra("PASSENGER_ID", passenger.getId());
        intent.putExtra("PASSENGER_NAME", passenger.getName());
        startActivityForResult(intent, SCAN_QR_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (requestCode == SCAN_QR_REQUEST && resultCode == RESULT_OK && data != null) {
            String qrData = data.getStringExtra("QR_DATA");
            String passengerId = data.getStringExtra("PASSENGER_ID");
            String passengerName = data.getStringExtra("PASSENGER_NAME");
            
            if (qrData != null) {
                mostrarDialogoConfirmacion(qrData, passengerId, passengerName);
            }
        }
    }

    private void mostrarDialogoConfirmacion(String ticketId, String passengerId, String passengerName) {
        new AlertDialog.Builder(this)
                .setTitle("Verificar Ticket")
                .setMessage("¿Confirmar el ticket de " + passengerName + "?")
                .setPositiveButton("Confirmar", (dialog, which) -> {
                    actualizarEstadoTicket(ticketId, passengerId, "used", true);
                })
                .setNegativeButton("Cancelar", (dialog, which) -> {
                    actualizarEstadoTicket(ticketId, passengerId, "cancelled", false);
                })
                .show();
    }

    private void actualizarEstadoTicket(String ticketId, String passengerId, String status, boolean confirmed) {
        new Thread(() -> {
            try {
                URL url = new URL("http://10.0.2.2:5090/api/ticket/" + ticketId);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("PUT");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);

                JSONObject updateData = new JSONObject();
                updateData.put("Status", status);

                OutputStream os = conn.getOutputStream();
                os.write(updateData.toString().getBytes());
                os.flush();
                os.close();

                int responseCode = conn.getResponseCode();
                
                runOnUiThread(() -> {
                    if (responseCode == 200) {
                        ticketStatusMap.put(passengerId, confirmed ? "confirmed" : "cancelled");
                        adapter.updateTicketStatus(passengerId, confirmed ? "confirmed" : "cancelled");
                        Toast.makeText(this, confirmed ? "Ticket confirmado" : "Ticket cancelado", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Error al actualizar ticket", Toast.LENGTH_SHORT).show();
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }

    private void cargarPasajeros() {
        new Thread(() -> {
            try {
                SharedPreferences prefs = getSharedPreferences("MiAppPrefs", MODE_PRIVATE);
                String email = prefs.getString("LOGGED_IN_USER_EMAIL", "");
                
                if (email.isEmpty()) {
                    runOnUiThread(() -> {
                        Toast.makeText(this, "Error: No se encontró el email del usuario", Toast.LENGTH_SHORT).show();
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
                String driverId = driverJson.has("Id") ? driverJson.getString("Id") : driverJson.getString("id");

                // Obtener el viaje activo del chofer
                URL urlTrips = new URL("http://10.0.2.2:5090/api/trip");
                HttpURLConnection connTrips = (HttpURLConnection) urlTrips.openConnection();
                connTrips.setRequestMethod("GET");

                BufferedReader readerTrips = new BufferedReader(new InputStreamReader(connTrips.getInputStream()));
                StringBuilder responseTrips = new StringBuilder();
                String lineTrips;
                while ((lineTrips = readerTrips.readLine()) != null) {
                    responseTrips.append(lineTrips);
                }
                readerTrips.close();

                JSONArray tripsArray = new JSONArray(responseTrips.toString());
                String activeTripId = null;

                // Buscar viaje activo del chofer
                for (int i = 0; i < tripsArray.length(); i++) {
                    JSONObject tripJson = tripsArray.getJSONObject(i);
                    String tripDriverId = tripJson.has("DriverId") ? tripJson.getString("DriverId") : tripJson.getString("driverId");
                    String tripStatus = tripJson.has("Status") ? tripJson.getString("Status") : tripJson.getString("status");
                    
                    if (tripDriverId.equals(driverId) && tripStatus.equals("in_progress")) {
                        activeTripId = tripJson.has("Id") ? tripJson.getString("Id") : tripJson.getString("id");
                        
                        // Obtener tickets del viaje
                        if (tripJson.has("TicketIds") && !tripJson.isNull("TicketIds")) {
                            JSONArray ticketIds = tripJson.getJSONArray("TicketIds");
                            Map<String, Passenger> passengersMap = new HashMap<>();

                            for (int j = 0; j < ticketIds.length(); j++) {
                                String ticketId = ticketIds.getString(j);

                                // Obtener información del ticket
                                URL urlTicket = new URL("http://10.0.2.2:5090/api/ticket/" + ticketId);
                                HttpURLConnection connTicket = (HttpURLConnection) urlTicket.openConnection();
                                connTicket.setRequestMethod("GET");

                                BufferedReader readerTicket = new BufferedReader(new InputStreamReader(connTicket.getInputStream()));
                                StringBuilder responseTicket = new StringBuilder();
                                String lineTicket;
                                while ((lineTicket = readerTicket.readLine()) != null) {
                                    responseTicket.append(lineTicket);
                                }
                                readerTicket.close();

                                JSONObject ticketJson = new JSONObject(responseTicket.toString());
                                String studentId = ticketJson.getString("StudentId");
                        
                                if (!passengersMap.containsKey(studentId)) {
                                    URL urlStudent = new URL("http://10.0.2.2:5090/api/student/" + studentId);
                                    HttpURLConnection connStudent = (HttpURLConnection) urlStudent.openConnection();
                                    connStudent.setRequestMethod("GET");

                                    BufferedReader readerStudent = new BufferedReader(new InputStreamReader(connStudent.getInputStream()));
                                    StringBuilder responseStudent = new StringBuilder();
                                    String lineStudent;
                                    while ((lineStudent = readerStudent.readLine()) != null) {
                                        responseStudent.append(lineStudent);
                                    }
                                    readerStudent.close();

                                    JSONObject studentJson = new JSONObject(responseStudent.toString());
                                    String fullName = studentJson.getString("FirstName") + " " + studentJson.getString("LastName");
                                    
                                    Passenger passenger = new Passenger(studentId, fullName, "pending");
                                    passengersMap.put(studentId, passenger);
                                }
                            }

                            listaPasajeros.clear();
                            listaPasajeros.addAll(passengersMap.values());

                            runOnUiThread(() -> adapter.notifyDataSetChanged());
                            return;
                        }
                    }
                }

                // Si no hay viaje activo
                runOnUiThread(() -> {
                    Toast.makeText(this, "No hay viaje activo en este momento", Toast.LENGTH_SHORT).show();
                });

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    Toast.makeText(this, "Error al cargar pasajeros: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }
}
