package bluetooth_scan;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.os.Handler;

/**
 * Created by anish_khattar25 on 2/18/15.
 */
public class BluetoothScan {
    private BluetoothAdapter mBluetoothAdapter;
    private Handler mHandler;
    private static final long SCAN_PERIOD = 2000;
    private static final Object scanLock = new Object();


    public BluetoothScan(BluetoothAdapter adapter){
        mHandler = new Handler();


    }

}
