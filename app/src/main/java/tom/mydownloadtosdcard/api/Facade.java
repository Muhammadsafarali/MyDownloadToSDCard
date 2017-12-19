package tom.mydownloadtosdcard.api;

import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;

import java.util.Observable;
import java.util.Observer;

import tom.mydownloadtosdcard.api.Network.NetworkManager;
import tom.mydownloadtosdcard.myApplication;

/**
 * Created by 3dium on 14.12.2017.
 */

public class Facade extends Observable implements Observer {

    static final String LOG_TAG = "Facade";
    private static Facade instance;
    private static NetworkManager networkManager;

    public static Facade getInstance() {
        if (instance == null) {
            synchronized (Facade.class) {
                if (instance == null) {
                    instance = new Facade();
                    networkManager = new NetworkManager();
                    networkManager.addObserver(instance);
                }
            }
        }
        return instance;
    }

    public void downloadFile(int target, int request, String requestUri, UsbDevice device, UsbManager mUsbManager) {
        networkManager.downloadFile(target, request, requestUri, myApplication.getInstance(), device, mUsbManager);
    }

    public void getToken() {
        networkManager.getTokenVolley();
    }

    @Override
    public void update(Observable o, Object arg) {
        setChanged();
        notifyObservers(arg);
    }

}
