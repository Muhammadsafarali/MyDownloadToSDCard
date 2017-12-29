package tom.mydownloadtosdcard.view;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.android.schedulers.AndroidSchedulers;

import tom.mydownloadtosdcard.presenter.MainPresenter;
import tom.mydownloadtosdcard.utils.ObserverNotifyResult;
import tom.mydownloadtosdcard.R;
import tom.mydownloadtosdcard.api.Facade;

public class MainActivity extends AppCompatActivity implements Observer, MainMvpView {

    private final static String LOG_TAG = "MainActivity";
    private TextView textLog;
    private HashMap<String, UsbDevice> deviceList;
    private PendingIntent mPermissionIntent;
//    private UsbManager mUsbManager;
//    private UsbDevice device;
    private UsbDeviceConnection mConnection;
    private String urlFile = "http://cloud.dubllik.ru/u/files/iusers/c4ca4238a0b923820dcc509a6f75849b/model_20171006_11-19-26.ply";
    private MainPresenter presenter;
    private RelativeLayout mainLayout;

    ListView _logsList;
    private List<String> _logs;
    private LogAdapter _adapter;

    private static final String ACTION_USB_PERMISSION =
            "com.android.example.USB_PERMISSION";

    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);

                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        if (device != null) {
                            textLog.setText(device.getDeviceName());
                        }
                    } else {
                        Log.e("TAG", "permission denied for device " + device);
                    }
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainLayout = findViewById(R.id.main_layout);
        _logsList = findViewById(R.id.list_threading_log);

        presenter = new MainPresenter();
        presenter.attachView(MainActivity.this);

        _setupLogger();

        this.textLog = findViewById(R.id.tvMyLog);

        Button btnGetUsbDevice = findViewById(R.id.btnGetUsbDevice);
        btnGetUsbDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getUsbDevice();
            }
        });

        Button btnDownloadFile = findViewById(R.id.btnDownloadFile);
        btnDownloadFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.startLongOperation();
            }
        });

        Button btnGetToken = findViewById(R.id.btnGetToken);
        btnGetToken.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Facade.getInstance().getToken();
            }
        });

//        mUsbManager = (UsbManager)getSystemService(Context.USB_SERVICE);
//
//        mPermissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
//        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
//        registerReceiver(mUsbReceiver, filter);

        mPermissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
        registerReceiver(mUsbReceiver, filter);

//        IBinder b = ServiceManager.getService(USB_SERVICE);
//        IUsbManager service = IUsbManager.Stub.asInterface(b);
//        service.grantDevicePermission(mDevice, uid);
    }


    // 2) UsbManager
    private void getUsbDevice() {
        presenter.mUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        this.deviceList = presenter.mUsbManager.getDeviceList();
        if (deviceList != null && deviceList.size() > 0) {
//            String resultText = new String();
//            for (String n :
//                    deviceList.keySet()) {
//                resultText += n;
//            }
//            textLog.setText(resultText);

            // Получить device
            Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
            while (deviceIterator.hasNext()) {
                presenter.device = deviceIterator.next();
                if (presenter.device != null) {
                    textLog.setText("Device: " + presenter.device.getDeviceName());
//                    mUsbManager.requestPermission(device, mPermissionIntent);
                }
            }
            presenter.mUsbManager.requestPermission(presenter.device, mPermissionIntent);
        } else {
            textLog.setText("deviceList: null");
        }
    }


    @Override
    public void update(Observable obj, Object arg) {
        if (obj instanceof Facade) {
            ObserverNotifyResult result = (ObserverNotifyResult) arg;
        }
    }


//    @Override
//    public void showMessage(String msg) {
//        Snackbar.make(mainLayout, msg, Snackbar.LENGTH_LONG).show();
//    }

    @Override
    public Context getContext() {
        return MainActivity.this;
    }

    public void _log(String logMsg) {

        if (_isCurrentlyOnMainThread()) {
            _logs.add(0, logMsg + " (main thread) ");
            _adapter.clear();
            _adapter.addAll(_logs);
        } else {
            _logs.add(0, logMsg + " (NOT main thread) ");

            // You can only do below stuff on main thread.
            new Handler(Looper.getMainLooper())
                    .post(
                            () -> {
                                _adapter.clear();
                                _adapter.addAll(_logs);
                            });
        }
    }


    private boolean _isCurrentlyOnMainThread() {
        return Looper.myLooper() == Looper.getMainLooper();
    }


    private void _setupLogger() {
        _logs = new ArrayList<>();
        _adapter = new LogAdapter(this, new ArrayList<String>());
        _logsList.setAdapter(_adapter);
    }

    private class LogAdapter extends ArrayAdapter<String> {
        public LogAdapter(Context context, List<String> logs) {
            super(context, R.layout.item_log, R.id.item_log, logs);
        }
    }
}
