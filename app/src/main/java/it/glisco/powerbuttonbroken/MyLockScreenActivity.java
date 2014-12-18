package it.glisco.powerbuttonbroken;


import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class MyLockScreenActivity extends Activity implements OnClickListener {

    private static final int ADMIN_INTENT = 15;
    private static final String description = "Some Description About Your Admin";
    private DevicePolicyManager mDevicePolicyManager;
    private ComponentName mComponentName;
    private Button btnEnableAdmin;
    private Button btnDisableAdmin;
    private Button btnLock;
    private Button btnPowerOff;
    private Button btnStartService;
    private Button btnStopService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock);
        mDevicePolicyManager = (DevicePolicyManager)getSystemService(
                Context.DEVICE_POLICY_SERVICE);
        mComponentName = new ComponentName(this, MyAdminReceiver.class);
        btnEnableAdmin = (Button) findViewById(R.id.btnEnable);
        btnDisableAdmin = (Button) findViewById(R.id.btnDisable);
        btnLock = (Button) findViewById(R.id.btnLock);
        btnPowerOff = (Button) findViewById(R.id.btnPowerOff);

        btnStartService = (Button) findViewById(R.id.btnStartService);
        btnStopService = (Button) findViewById(R.id.btnStopService);



        btnEnableAdmin.setOnClickListener(this);
        btnDisableAdmin.setOnClickListener(this);
        btnLock.setOnClickListener(this);
        btnPowerOff.setOnClickListener(this);

    }

    public void startService(View v)
    {
        //startService(new Intent(this,LogService.class));
        btnStartService.setVisibility(View.GONE);
        btnStopService.setVisibility(View.VISIBLE);
        startService(new Intent(this,SensorService.class));
    }

    public void stopService(View v)
    {
        //stopService(new Intent(this,LogService.class));
        btnStartService.setVisibility(View.VISIBLE);
        btnStopService.setVisibility(View.GONE);
        stopService(new Intent(this,SensorService.class));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnEnable:
                Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
                intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mComponentName);
                intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,description);
                startActivityForResult(intent, ADMIN_INTENT);
                btnEnableAdmin.setVisibility(View.GONE);
                btnDisableAdmin.setVisibility(View.GONE);

                break;

            case R.id.btnDisable:
                mDevicePolicyManager.removeActiveAdmin(mComponentName);
                Toast.makeText(getApplicationContext(), "Admin registration removed", Toast.LENGTH_SHORT).show();
                btnEnableAdmin.setVisibility(View.VISIBLE);
                btnDisableAdmin.setVisibility(View.GONE);
                break;

            case R.id.btnLock:
                boolean isAdmin = mDevicePolicyManager.isAdminActive(mComponentName);
                if (isAdmin) {
                    mDevicePolicyManager.lockNow();
                }else{
                    Toast.makeText(getApplicationContext(), "Not Registered as admin", Toast.LENGTH_SHORT).show();
                    btnEnableAdmin.setVisibility(View.VISIBLE);
                    btnDisableAdmin.setVisibility(View.GONE);
                }
                break;

            case R.id.btnPowerOff:
                try {
                    Process proc = Runtime.getRuntime()
                            .exec(new String[]{ "su", "-c", "reboot -p" });
                    proc.waitFor();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                /*
                Intent i = new Intent("android.intent.action.ACTION_REQUEST_SHUTDOWN");
                i.putExtra("android.intent.extra.KEY_CONFIRM", true);
                startActivity(i);
                */
                break;

            case R.id.btnReboot:
                try {
                    Process proc = Runtime.getRuntime()
                            .exec(new String[]{ "su", "-c", "reboot" });
                    proc.waitFor();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                /*
                Intent i = new Intent("android.intent.action.ACTION_REQUEST_SHUTDOWN");
                i.putExtra("android.intent.extra.KEY_CONFIRM", true);
                startActivity(i);
                */
                break;

        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ADMIN_INTENT) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(getApplicationContext(), "Registered As Admin", Toast.LENGTH_SHORT).show();
                btnEnableAdmin.setVisibility(View.GONE);
                btnDisableAdmin.setVisibility(View.VISIBLE);
            }else{
                Toast.makeText(getApplicationContext(), "Failed to register as Admin", Toast.LENGTH_SHORT).show();
                btnEnableAdmin.setVisibility(View.VISIBLE);
                btnDisableAdmin.setVisibility(View.GONE);
            }
        }
    }

}