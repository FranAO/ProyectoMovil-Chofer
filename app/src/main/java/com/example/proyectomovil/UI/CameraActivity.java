package com.example.proyectomovil.UI;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.proyectomovil.R;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CameraActivity extends BaseNavigationActivity {
    private static final int CAMERA_PERMISSION_CODE = 100;
    private PreviewView previewView;
    private TextView tvResultado;
    private ExecutorService cameraExecutor;
    private BarcodeScanner scanner;
    private boolean isScanning = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_camera);

        setupNavigation();

        previewView = findViewById(R.id.previewView);
        tvResultado = findViewById(R.id.tvResultado);

        BarcodeScannerOptions options = new BarcodeScannerOptions.Builder()
                .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
                .build();
        scanner = BarcodeScanning.getClient(options);

        cameraExecutor = Executors.newSingleThreadExecutor();

        if (checkCameraPermission()) {
            startCamera();
        } else {
            requestCameraPermission();
        }
    }

    @Override
    protected int getNavigationIndex() {
        return 3;
    }

    private boolean checkCameraPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCamera();
            } else {
                Toast.makeText(this, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();

                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(previewView.getSurfaceProvider());

                CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;

                ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build();

                imageAnalysis.setAnalyzer(cameraExecutor, this::analyzeImage);

                cameraProvider.unbindAll();
                Camera camera = cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalysis);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void analyzeImage(ImageProxy imageProxy) {
        if (!isScanning) {
            imageProxy.close();
            return;
        }

        InputImage image = InputImage.fromMediaImage(imageProxy.getImage(), imageProxy.getImageInfo().getRotationDegrees());

        scanner.process(image)
                .addOnSuccessListener(barcodes -> {
                    for (Barcode barcode : barcodes) {
                        String qrContent = barcode.getRawValue();
                        if (qrContent != null && !qrContent.isEmpty()) {
                            isScanning = false;
                            runOnUiThread(() -> {
                                tvResultado.setText("QR detectado: " + qrContent);
                                validarTicket(qrContent);
                            });
                            break;
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    e.printStackTrace();
                })
                .addOnCompleteListener(task -> imageProxy.close());
    }

    private void validarTicket(String ticketId) {
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
                String driverId = driverJson.getString("Id");

                URL urlTrip = new URL("http://10.0.2.2:5090/api/trip/driver/" + driverId);
                HttpURLConnection connTrip = (HttpURLConnection) urlTrip.openConnection();
                connTrip.setRequestMethod("GET");

                BufferedReader readerTrip = new BufferedReader(new InputStreamReader(connTrip.getInputStream()));
                StringBuilder responseTrip = new StringBuilder();
                String lineTrip;
                while ((lineTrip = readerTrip.readLine()) != null) {
                    responseTrip.append(lineTrip);
                }
                readerTrip.close();

                org.json.JSONArray tripsArray = new org.json.JSONArray(responseTrip.toString());
                if (tripsArray.length() > 0) {
                    JSONObject tripActivo = tripsArray.getJSONObject(0);
                    String tripId = tripActivo.getString("Id");

                    URL urlValidate = new URL("http://10.0.2.2:5090/api/passengerintrip/validate?ticketId=" + ticketId + "&tripId=" + tripId);
                    HttpURLConnection connValidate = (HttpURLConnection) urlValidate.openConnection();
                    connValidate.setRequestMethod("POST");
                    connValidate.setRequestProperty("Content-Type", "application/json");

                    int responseCode = connValidate.getResponseCode();

                    if (responseCode == 200) {
                        BufferedReader readerValidate = new BufferedReader(new InputStreamReader(connValidate.getInputStream()));
                        StringBuilder responseValidate = new StringBuilder();
                        String lineValidate;
                        while ((lineValidate = readerValidate.readLine()) != null) {
                            responseValidate.append(lineValidate);
                        }
                        readerValidate.close();

                        runOnUiThread(() -> {
                            tvResultado.setText("Ticket validado correctamente");
                            Toast.makeText(this, "Pasajero abordado", Toast.LENGTH_LONG).show();
                        });
                    } else {
                        runOnUiThread(() -> {
                            tvResultado.setText("Error: Ticket no válido");
                            Toast.makeText(this, "Ticket no válido", Toast.LENGTH_LONG).show();
                        });
                    }

                    Thread.sleep(3000);
                    runOnUiThread(() -> {
                        tvResultado.setText("Escanea un código QR");
                        isScanning = true;
                    });
                }

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    tvResultado.setText("Error al validar ticket");
                    Toast.makeText(this, "Error de conexión", Toast.LENGTH_SHORT).show();
                    isScanning = true;
                });
            }
        }).start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cameraExecutor.shutdown();
        scanner.close();
    }
}