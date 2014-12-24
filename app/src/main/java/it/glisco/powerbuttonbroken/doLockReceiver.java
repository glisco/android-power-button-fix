package it.glisco.powerbuttonbroken;

import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class doLockReceiver extends BroadcastReceiver {
    public doLockReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        DevicePolicyManager mDevicePolicyManager = (DevicePolicyManager) context.getSystemService(
                Context.DEVICE_POLICY_SERVICE);
        
        mDevicePolicyManager.lockNow();

    }
}
