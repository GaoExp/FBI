package exp.ftxt;

import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Debug;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.provider.Settings;
import android.widget.CheckBox;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SettingsActivity extends AppCompatActivity {

    private Switch overlaySwitch;
    private Switch notificationSwitch;
    private Switch batterySwitch;
    private Switch iconSwitch;

    private TextView memoryInfoText;
    private final Handler memoryHandler = new Handler(Looper.getMainLooper());
    private final Runnable memoryRunnable = new Runnable() {
        @Override
        public void run() {
            updateMemoryInfo();
            memoryHandler.postDelayed(this, 1000);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Konfigurasi");
        toolbar.setNavigationOnClickListener(v -> finish());

        overlaySwitch = findViewById(R.id.overlayPermissionSwitch);
        notificationSwitch = findViewById(R.id.notificationPermissionSwitch);
        batterySwitch = findViewById(R.id.batteryPermissionSwitch);
        iconSwitch = findViewById(R.id.iconSwitch);

        memoryInfoText = findViewById(R.id.memoryInfoText);
        findViewById(R.id.memoryExportButton).setOnClickListener(v -> exportMemorySnapshot());
        updateMemoryInfo();

        SharedPreferences prefs = getSharedPreferences("ftxt_prefs", MODE_PRIVATE);

        updatePermissionSwitches();

        overlaySwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            applySwitchTint(overlaySwitch, isChecked);
            if (isChecked && !Settings.canDrawOverlays(this)) {
                startActivity(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName())));
            }
        });

        notificationSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            applySwitchTint(notificationSwitch, isChecked);
            if (isChecked && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.POST_NOTIFICATIONS}, 100);
                }
            }
        });

        batterySwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            applySwitchTint(batterySwitch, isChecked);
            if (isChecked) {
                PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
                if (!pm.isIgnoringBatteryOptimizations(getPackageName())) {
                    startActivity(new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS,
                            Uri.parse("package:" + getPackageName())));
                }
            }
        });

        boolean useAltIcon = prefs.getBoolean("alt_icon", false);
        iconSwitch.setChecked(useAltIcon);
        applySwitchTint(iconSwitch, useAltIcon);

        iconSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            applySwitchTint(iconSwitch, isChecked);
            prefs.edit().putBoolean("alt_icon", isChecked).apply();
            setIcon(isChecked);
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        updatePermissionSwitches();
        startMemoryPolling();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopMemoryPolling();
    }

    private void applySwitchTint(Switch sw, boolean isChecked) {
        if (isChecked) {
            sw.setThumbTintList(ColorStateList.valueOf(Color.parseColor("#2196F3")));
            sw.setTrackTintList(ColorStateList.valueOf(Color.parseColor("#90CAF9")));
        } else {
            sw.setThumbTintList(ColorStateList.valueOf(Color.parseColor("#E53935")));
            sw.setTrackTintList(ColorStateList.valueOf(Color.parseColor("#EF9A9A")));
        }
    }

    private void setIcon(boolean useAlt) {
        PackageManager pm = getPackageManager();
        ComponentName def = new ComponentName(this, "exp.ftxt.MainActivityDefault");
        ComponentName alt = new ComponentName(this, "exp.ftxt.MainActivityAlt");
        int defState = useAlt ? PackageManager.COMPONENT_ENABLED_STATE_DISABLED : PackageManager.COMPONENT_ENABLED_STATE_ENABLED;
        int altState = useAlt ? PackageManager.COMPONENT_ENABLED_STATE_ENABLED : PackageManager.COMPONENT_ENABLED_STATE_DISABLED;
        pm.setComponentEnabledSetting(def, defState, PackageManager.DONT_KILL_APP);
        pm.setComponentEnabledSetting(alt, altState, PackageManager.DONT_KILL_APP);
    }

    private void updatePermissionSwitches() {
        boolean overlayOk = Settings.canDrawOverlays(this);
        overlaySwitch.setChecked(overlayOk);
        applySwitchTint(overlaySwitch, overlayOk);

        boolean notifOk;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notifOk = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED;
        } else {
            notifOk = true;
        }
        notificationSwitch.setChecked(notifOk);
        applySwitchTint(notificationSwitch, notifOk);

        PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
        boolean batteryOk = pm.isIgnoringBatteryOptimizations(getPackageName());
        batterySwitch.setChecked(batteryOk);
        applySwitchTint(batterySwitch, batteryOk);
    }

    private void startMemoryPolling() {
        memoryHandler.removeCallbacks(memoryRunnable);
        memoryHandler.postDelayed(memoryRunnable, 1000);
    }

    private void stopMemoryPolling() {
        memoryHandler.removeCallbacks(memoryRunnable);
    }

    private Debug.MemoryInfo readMemoryInfo() {
        Debug.MemoryInfo info = new Debug.MemoryInfo();
        Debug.getMemoryInfo(info);
        return info;
    }

    private String formatMb(int kb) {
        return String.format(Locale.US, "%.1f MB", kb / 1024f);
    }

    private int graphicsPssKb(Debug.MemoryInfo info) {
        String stat = info.getMemoryStat("summary.graphics");
        if (stat == null) return 0;
        try {
            int end = stat.indexOf(' ');
            if (end > 0) {
                return Integer.parseInt(stat.substring(0, end));
            }
            return Integer.parseInt(stat.trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private void updateMemoryInfo() {
        if (memoryInfoText == null) return;
        Debug.MemoryInfo info = readMemoryInfo();
        memoryInfoText.setText(
                "Java Heap   : " + formatMb(info.dalvikPss) + "\n" +
                "Native Heap : " + formatMb(info.nativePss) + "\n" +
                "Graphics    : " + formatMb(graphicsPssKb(info)) + "\n" +
                "Total Proses: " + formatMb(info.getTotalPss()));
    }

    private void exportMemorySnapshot() {
        Debug.MemoryInfo info = readMemoryInfo();
        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
                .format(new Date());
        String content =
                "FBI - Info Memori\n" +
                "Waktu: " + timestamp + "\n\n" +
                "Java Heap (Dalvik): " + formatMb(info.dalvikPss) + "\n" +
                "Native Heap       : " + formatMb(info.nativePss) + "\n" +
                "Graphics          : " + formatMb(graphicsPssKb(info)) + "\n" +
                "Total Proses (PSS): " + formatMb(info.getTotalPss()) + "\n";

        String fileName = "FBI_memori_" + System.currentTimeMillis() + ".txt";
        try {
            if (writeSnapshotToDownload(content, fileName)) {
                Toast.makeText(this, "Tersimpan: Download/" + fileName, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Gagal menyimpan snapshot", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Gagal menyimpan: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private boolean writeSnapshotToDownload(String content, String fileName) throws Exception {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContentValues values = new ContentValues();
            values.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
            values.put(MediaStore.MediaColumns.MIME_TYPE, "text/plain");
            values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);
            Uri uri = getContentResolver().insert(
                    MediaStore.Downloads.EXTERNAL_CONTENT_URI, values);
            if (uri == null) return false;
            try (OutputStream os = getContentResolver().openOutputStream(uri)) {
                if (os == null) return false;
                os.write(content.getBytes("UTF-8"));
            }
            return true;
        } else {
            File dir = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS);
            if (!dir.exists()) dir.mkdirs();
            File file = new File(dir, fileName);
            try (FileOutputStream fos = new FileOutputStream(file)) {
                fos.write(content.getBytes("UTF-8"));
            }
            return true;
        }
    }

}