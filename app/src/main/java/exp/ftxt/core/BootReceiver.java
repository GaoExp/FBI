package exp.ftxt.core;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import exp.ftxt.features.battery_bar.BatteryBarConfig;
import exp.ftxt.features.battery_stats.BatteryStatsConfig;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null || !Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) return;

        SharedPreferences prefs = context.getSharedPreferences("ftxt_prefs", Context.MODE_PRIVATE);

        BatteryStatsConfig.enabled = prefs.getBoolean("battery_enabled", false);
        BatteryBarConfig.enabled = prefs.getBoolean("batbar_enabled", false);

        boolean anyActive = BatteryStatsConfig.enabled
                || BatteryBarConfig.enabled;

        if (anyActive) {
            context.startForegroundService(new Intent(context, FloatingService.class));
        }
    }
}
