package it.glisco.powerbuttonbroken;

import android.app.AlertDialog;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

public class doPowerOffReceiver extends BroadcastReceiver {
    public doPowerOffReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {


        new AlertDialog.Builder(context)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(R.string.quit)
                .setMessage(R.string.really_quit)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        try {
                            Process proc = Runtime.getRuntime()
                                    .exec(new String[]{ "su", "-c", "reboot -p" });
                            proc.waitFor();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }


                    }

                })
                .setNegativeButton(R.string.no, null)
                .show();
        
        

    }
}
