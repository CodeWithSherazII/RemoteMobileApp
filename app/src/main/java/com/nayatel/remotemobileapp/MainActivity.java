package com.nayatel.remotemobileapp;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.nayatel.remotemobileapp.client.SocketClient;

public class MainActivity extends AppCompatActivity {
    private EditText ipEditText;
    private TextView statusTextView;
    private SocketClient socketClient;
    private boolean isConnected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ipEditText = findViewById(R.id.ipEditText);
        statusTextView = findViewById(R.id.statusTextView);
        socketClient = new SocketClient();

        findViewById(R.id.connectButton).setOnClickListener(v -> {
            String ip = ipEditText.getText().toString().trim();
            if (!ip.isEmpty()) {
                new Thread(() -> {
                    boolean success = socketClient.connectToServer(ip, 8888);
                    Log.d("remote", "Activity, socketStatus " + success);
                    runOnUiThread(() -> {
                        if (success) {
                            isConnected = true;
                            statusTextView.setText("Connected to TV");
                        } else {
                            statusTextView.setText("Connection failed");
                        }
                    });
                }).start();
            }
        });

        setupButton(R.id.volumeUpButton, "VOLUME_UP");
        setupButton(R.id.volumeDownButton, "VOLUME_DOWN");

        setupButton(R.id.dpadUpButton, "DPAD_UP");
        setupButton(R.id.dpadDownButton, "DPAD_DOWN");
        setupButton(R.id.dpadLeftButton, "DPAD_LEFT");
        setupButton(R.id.dpadRightButton, "DPAD_RIGHT");
        setupButton(R.id.dpadCenterButton, "DPAD_CENTER");

        setupButton(R.id.homeButton, "HOME");
        setupButton(R.id.backButton, "BACK");


    }

    private void setupButton(int buttonId, String command) {
        findViewById(buttonId).setOnClickListener(v -> {
            try {
                if (isConnected) {
                    new Thread(() -> {
                        try {
                            socketClient.sendCommand(command);
                            Log.d("remote", "Sent: " + command);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }).start();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        socketClient.closeConnection();
    }
}
