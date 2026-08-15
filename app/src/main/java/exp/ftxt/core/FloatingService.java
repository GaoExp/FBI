package exp.ftxt.core;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.IBinder;

import android.view.WindowManager;

import java.util.ArrayList;
import java.util.List;

import exp.ftxt.R;
import exp.ftxt.features.battery_bar.BatteryBarConfig;
import exp.ftxt.features.battery_bar.BatteryBarModule;
import exp.ftxt.features.battery_stats.BatteryStatsConfig;
import exp.ftxt.features.battery_stats.BatteryStatsModule;
import exp.ftxt.shared.ui.OverlayModule;

public class FloatingService extends Service {

    private WindowManager windowManager;
    private WakeLockManager wakeLockManager;
    private SharedPreferences prefs;

    public static FloatingService instance;

    private BatteryStatsModule batteryStatsModule;
    private BatteryBarModule batteryBarModule;
    private final List<OverlayModule> allModules = new ArrayList<>();
    private BroadcastReceiver configChangeReceiver;
    private BroadcastReceiver screenReceiver;

    public BatteryStatsModule getBatteryStatsModule() { return batteryStatsModule; }
    public BatteryBarModule getBatteryBarModule() { return batteryBarModule; }

    public static BatteryStatsModule batteryStatsModule() {
        if (instance != null) instance.ensureBatteryStatsModule();
        return instance != null ? instance.batteryStatsModule : null;
    }
    public static BatteryBarModule batteryBarModule() {
        if (instance != null) instance.ensureBatteryBarModule();
        return instance != null ? instance.batteryBarModule : null;
    }

    private void ensureBatteryStatsModule() {
        if (batteryStatsModule == null) {
            batteryStatsModule = new BatteryStatsModule();
            allModules.add(batteryStatsModule);
            batteryStatsModule.init(windowManager, this, prefs);
        }
    }

    private void ensureBatteryBarModule() {
        if (batteryBarModule == null) {
            batteryBarModule = new BatteryBarModule();
            allModules.add(batteryBarModule);
            batteryBarModule.init(windowManager, this, prefs);
        }
    }

    private boolean isAnyModuleActive() {
        for (OverlayModule module : allModules) {
            if (module.isRunning()) return true;
        }
        return false;
    }

    private void registerConfigReceiver() {
        if (configChangeReceiver != null) return;
        configChangeReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent != null && Intent.ACTION_CONFIGURATION_CHANGED.equals(intent.getAction())) {
                    reloadAllPositions();
                }
            }
        };
        IntentFilter filter = new IntentFilter(Intent.ACTION_CONFIGURATION_CHANGED);
        registerReceiver(configChangeReceiver, filter);
    }

    private void unregisterConfigReceiver() {
        if (configChangeReceiver == null) return;
        unregisterReceiver(configChangeReceiver);
        configChangeReceiver = null;
    }

    private void registerScreenReceiver() {
        if (screenReceiver != null) return;
        screenReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent != null ? intent.getAction() : null;
                if (Intent.ACTION_SCREEN_ON.equals(action)) {
                    acquireWakeLockIfNeeded();
                } else if (Intent.ACTION_SCREEN_OFF.equals(action)) {
                    if (wakeLockManager != null) {
                        wakeLockManager.release();
                    }
                }
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(screenReceiver, filter);
    }

    private void unregisterScreenReceiver() {
        if (screenReceiver == null) return;
        unregisterReceiver(screenReceiver);
        screenReceiver = null;
    }

    private void acquireWakeLockIfNeeded() {
        if (isAnyModuleActive() && (wakeLockManager == null || !wakeLockManager.isHeld())) {
            if (wakeLockManager == null) {
                wakeLockManager = new WakeLockManager();
            }
            wakeLockManager.acquire(this);
        }
    }

    private void releaseWakeLockIfEmpty() {
        if (!isAnyModuleActive() && wakeLockManager != null && wakeLockManager.isHeld()) {
            wakeLockManager.release();
        }
    }

    private void stopSelfIfEmpty() {
        if (!isAnyModuleActive()) {
            stopSelf();
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;
        prefs = getSharedPreferences("ftxt_prefs", MODE_PRIVATE);

        NotificationHelper.createChannel(this);
        try {
            startForeground(NotificationHelper.NOTIFICATION_ID,
                    NotificationHelper.buildNotification(this));
            NotificationHelper.startIconCycling(this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

            if (BatteryStatsConfig.enabled) { ensureBatteryStatsModule(); batteryStatsModule.start(windowManager, this); }
            if (BatteryBarConfig.enabled) { ensureBatteryBarModule(); batteryBarModule.start(windowManager, this); }

            acquireWakeLockIfNeeded();
            if (isAnyModuleActive()) {
                registerConfigReceiver();
                registerScreenReceiver();
            }

        } catch (Exception e) {
            e.printStackTrace();
            stopSelf();
        }
    }

    public static void startModule(OverlayModule module) {
        if (instance != null && module != null) {
            module.start(instance.windowManager, instance);
            instance.acquireWakeLockIfNeeded();
            instance.registerConfigReceiver();
            instance.registerScreenReceiver();
        }
    }

    public static void stopModule(OverlayModule module) {
        if (instance != null && module != null) {
            module.stop();
            instance.releaseWakeLockIfEmpty();
            instance.unregisterConfigReceiver();
            instance.unregisterScreenReceiver();
            instance.stopSelfIfEmpty();
        }
    }

    public static void updateColorForModule(OverlayModule module, int color) {
        if (instance != null && module != null && module.isRunning()) {
            module.updateColor(color);
        }
    }

    public static void updateSizeForModule(OverlayModule module, float size) {
        if (instance != null && module != null && module.isRunning()) {
            module.updateSize(size);
        }
    }

    public static void updateLabelColorForModule(OverlayModule module, int color) {
        if (instance != null && module != null && module.isRunning()) {
            module.updateLabelColor(color);
        }
    }

    public static void updateSeparatorColorForModule(OverlayModule module, int color) {
        if (instance != null && module != null && module.isRunning()) {
            module.updateSeparatorColor(color);
        }
    }

    public static void updateShadowForModule(OverlayModule module) {
        if (instance != null && module != null && module.isRunning()) {
            module.updateShadow();
        }
    }

    public static void updateBackgroundForModule(OverlayModule module) {
        if (instance != null && module != null && module.isRunning()) {
            module.updateBackground();
        }
    }

    public static void updatePositionForModule(OverlayModule module) {
        if (instance != null && module != null && module.isRunning()) {
            module.updatePosition();
        }
    }

    public static void updateTouchFlagsForModule(OverlayModule module) {
        if (instance != null && module != null && module.isRunning()) {
            module.updateTouchFlags();
        }
    }

    public static void setOrientationSuffixForModule(OverlayModule module, String suffix) {
        if (instance != null && module != null) {
            module.setOrientationSuffix(suffix);
        }
    }

    public static int[] getCurrentPositionForModule(OverlayModule module) {
        if (instance != null && module != null) {
            return module.getCurrentPosition();
        }
        return null;
    }

    public static void restartModule(OverlayModule module) {
        if (instance != null && module != null && module.isRunning()) {
            module.stop();
            module.start(instance.windowManager, instance);
        }
    }

    public static void updateBatteryBarInPlace() {
        if (instance != null && instance.batteryBarModule != null && instance.batteryBarModule.isRunning()) {
            instance.batteryBarModule.applyAppearance();
            instance.batteryBarModule.reloadLayout();
            instance.batteryBarModule.updatePosition();
        }
    }

    public static void updateBatteryStatsInPlace() {
        if (instance != null && instance.batteryStatsModule != null && instance.batteryStatsModule.isRunning()) {
            instance.batteryStatsModule.refreshDisplay();
        }
    }

    private void reloadAllPositions() {
        for (OverlayModule module : allModules) {
            if (module.isRunning()) {
                module.reloadPosition();
            }
        }
    }

    public static void updateNotification() {
        if (instance != null) {
            NotificationHelper.updateNotification(instance);
        }
    }

    public static void stopAllModules() {
        if (instance == null) return;
        for (OverlayModule module : instance.allModules) {
            module.stop();
        }
        instance.releaseWakeLockIfEmpty();
        instance.unregisterConfigReceiver();
        instance.unregisterScreenReceiver();
        instance.stopSelfIfEmpty();
    }

    public static void hideAllOverlays() {
        if (instance == null) return;
        for (OverlayModule module : instance.allModules) {
            module.hide();
        }
    }

    public static void showAllOverlays() {
        if (instance == null) return;
        for (OverlayModule module : instance.allModules) {
            module.show();
        }
    }

    public static boolean areAllOverlaysHidden() {
        if (instance == null) return true;
        for (OverlayModule module : instance.allModules) {
            if (module.isRunning() && !module.isHidden()) return false;
        }
        return true;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        NotificationHelper.stopIconCycling();
        unregisterConfigReceiver();
        unregisterScreenReceiver();

        for (OverlayModule module : allModules) {
            module.stop();
        }
        allModules.clear();

        if (wakeLockManager != null) {
            wakeLockManager.release();
            wakeLockManager = null;
        }

        instance = null;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
