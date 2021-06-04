
package app.nehc.batterytool;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;


public class TestActivity extends AppCompatActivity {

    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        BatteryManager batteryManager = (BatteryManager) getSystemService(BATTERY_SERVICE);

        TextView tv_1 = findViewById(R.id.tv_1);
        tv_1.setText("出厂容量" + getBatteryCapacity(this) + "");
        TextView tv_2 = findViewById(R.id.tv_2);
        tv_2.setText(""+batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_ENERGY_COUNTER));
    }

    public String getBatteryCapacity(Context context) {
        Object mPowerProfile;
        double batteryCapacity = 0;
        final String POWER_PROFILE_CLASS = "com.android.internal.os.PowerProfile";

        try {
            mPowerProfile = Class.forName(POWER_PROFILE_CLASS)
                    .getConstructor(Context.class)
                    .newInstance(context);

            batteryCapacity = (double) Class.forName(POWER_PROFILE_CLASS)
                    .getMethod("getBatteryCapacity")
                    .invoke(mPowerProfile);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return String.valueOf(batteryCapacity + " mAh");
    }
}