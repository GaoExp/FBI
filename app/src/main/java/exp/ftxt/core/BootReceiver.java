package exp.ftxt.core;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import exp.ftxt.features.battery_bar.BatteryBarConfig;
import exp.ftxt.features.battery_stats.BatteryStatsConfig;
import exp.ftxt.features.memory_stats.MemoryConfig;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null || !Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) return;

        SharedPreferences prefs = context.getSharedPreferences("ftxt_prefs", Context.MODE_PRIVATE);

        BatteryStatsConfig.enabled = prefs.getBoolean("battery_enabled", false);
        BatteryBarConfig.enabled = prefs.getBoolean("batbar_enabled", false);
        MemoryConfig.enabled = prefs.getBoolean("mem_enabled", false);
        MemoryConfig.backgroundMonitor = prefs.getBoolean("mem_bg_monitor", false);

        boolean anyActive = BatteryStatsConfig.enabled
                || BatteryBarConfig.enabled
                || MemoryConfig.enabled
                || MemoryConfig.backgroundMonitor;

        if (anyActive) {
            context.startForegroundService(new Intent(context, FloatingService.class));
        }
    }
}
