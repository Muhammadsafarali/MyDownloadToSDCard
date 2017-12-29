package tom.mydownloadtosdcard.presenter;

import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.util.Log;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import rx.Subscription;
import tom.mydownloadtosdcard.myApplication;
import tom.mydownloadtosdcard.view.MainMvpView;

/**
 * Created by 3dium on 15.12.2017.
 */

public class MainPresenter implements Presenter<MainMvpView> {

    private static final String LOG_TAG = "MainPresenter";

    public UsbManager mUsbManager;
    public UsbDevice device;
    public MainMvpView mainMvpView;
    private Subscription subscription;


    @Override
    public void attachView(MainMvpView view) {
        this.mainMvpView = view;
    }

    @Override
    public void detachView() {
        this.mainMvpView = null;
        if (subscription != null) subscription.unsubscribe();
    }

    public void writeToUsb() {

//        if (subscription != null) subscription.unsubscribe();
//        myApplication application = myApplication.get(mainMvpView.getContext());

        int Timeout = 100;
        boolean forceClaim = true;

        checkDeviceNull();

        if (device != null && mUsbManager != null) {
            int epIndex = 0;
            UsbInterface intf = device.getInterface(epIndex);
//            UsbEndpoint endpoint = intf.getEndpoint(0 );
            UsbDeviceConnection connection = mUsbManager.openDevice(device);

            MainPresenter.this.mainMvpView._log("Interface Count: " + device.getInterfaceCount());
            MainPresenter.this.mainMvpView._log("Using " + String.format("%04X:%04X", device.getVendorId(), device.getProductId()));


            if (connection.claimInterface(intf, forceClaim)) {
//                Log.e(LOG_TAG, "open SUCCESS");
                UsbEndpoint epIN = null;
                UsbEndpoint epOUT = null;
                MainPresenter.this.mainMvpView._log("usb open SUCCESS");
                MainPresenter.this.mainMvpView._log("EP: " + String.format("0x%02X", intf.getEndpoint(epIndex).getAddress()));

                for (int i = 0; i < intf.getEndpointCount(); i++) {
                    if (intf.getEndpoint(epIndex).getType() == UsbConstants.USB_ENDPOINT_XFER_BULK) {
                        MainPresenter.this.mainMvpView._log("Bulk Endpoint");

                        if (intf.getEndpoint(epIndex).getDirection() == UsbConstants.USB_DIR_IN) {
                            epIN = intf.getEndpoint(epIndex);
                            MainPresenter.this.mainMvpView._log("epIN");
                        } else {
                            epOUT = intf.getEndpoint(epIndex);
                            MainPresenter.this.mainMvpView._log("epOUT");
                        }
                    } else {
                        MainPresenter.this.mainMvpView._log("Not Bulk");
                    }
                }

                if (epOUT == null || epIN == null) {
                    throw new IllegalArgumentException("not all endpoints found");
                }
//                for (;;) {// this is the main loop for transferring
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    String get = "$fDump G" + "\n";
                    MainPresenter.this.mainMvpView._log("Sending: " + get);

                    byte[] by = get.getBytes();

                    // This is where it sends
                    MainPresenter.this.mainMvpView._log("out " + connection.bulkTransfer(epOUT, by, by.length, 500));

                    // This is where it is meant to receive
                    byte[] buffer = new byte[4096];

                    StringBuilder str = new StringBuilder();

                    if (connection.bulkTransfer(epIN, buffer, 4096, 500) >= 0) {
                        for (int i = 2; i < 4096; i++) {
                            if (buffer[i] != 0) {
                                str.append((char) buffer[i]);
                            } else {
                                MainPresenter.this.mainMvpView._log(str.toString());
                                break;
                            }
                        }

                    }
                    // this shows the complete string
                    MainPresenter.this.mainMvpView._log(str.toString());

//                    if (mStop) {
//                        mStopped = true;
//                        return;
//                    }
//                    MainPresenter.this.mainMvpView._log("sent " + counter);
//                    counter++;
//                    counter = (byte) (counter % 16);
//                }

//                byte[] init = {0x00,0x00,0x00,0x00,0x05,0x00,0x00,0x00,0x00,0x00,0x00,0x00};

                // 	length of data transferred (or zero) for success, or -1 for failure
//                int x = connection.bulkTransfer(endpoint,init, init.length, Timeout);
//                Log.e(LOG_TAG, "BulkTransfer returned " + x);

//                MainPresenter.this.mainMvpView._log("init.length: " + init.length);
//                MainPresenter.this.mainMvpView._log("BulkTransfer returned: " + x);
            } else {
                MainPresenter.this.mainMvpView._log("usb open FAIL");
                return;
            }
        }
    }

    private void checkDeviceNull() {
        if (device != null)
            MainPresenter.this.mainMvpView._log("device != null");
        else
            MainPresenter.this.mainMvpView._log("device == null");

        if (mUsbManager != null)
            MainPresenter.this.mainMvpView._log("mUsbManager != null");
        else
            MainPresenter.this.mainMvpView._log("mUsbManager == null");
    }

    public void test() {
        /*subscription = RxJavaCallAdapterFactory.create().
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(myApplication.getInstance().defaultSubscribeScheduler())
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {

                    }
                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(String testScheduler) {

                    }

                });*/
//        mainMvpView.showMessage("message");
    }

    public void startLongOperation() {
        DisposableObserver<Boolean> d = _getDisposableObserver();

        _getObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(d);
    }


    private Observable<Boolean> _getObservable() {
        return Observable.just(true)
                .map(
                        aBoolean -> {
                            MainPresenter.this.mainMvpView._log("Within Observable");
                            _doSomeLongOperation_thatBlocksCurrentThread();
                            return aBoolean;
                        });
    }

    private void _doSomeLongOperation_thatBlocksCurrentThread() {
//        MainPresenter.this.mainMvpView._log("performing long operation: " + str);

//        try {
//            Thread.sleep(3000);
//
//        } catch (InterruptedException e) {
//            Log.e("MainPresenter","Operation was interrupted");
//        }
        writeToUsb();
    }

    public DisposableObserver<Boolean> _getDisposableObserver() {
        return new DisposableObserver<Boolean>() {

            @Override
            public void onComplete() {
                MainPresenter.this.mainMvpView._log("On complete");
//                _progress.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onError(Throwable e) {
//               Log.e("MainPresenter", "Error in RxJava Demo concurrency: " + e.getMessage());
                MainPresenter.this.mainMvpView._log(String.format("Boo! Error %s", e.fillInStackTrace()));
//                _progress.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onNext(Boolean bool) {
                MainPresenter.this.mainMvpView._log(String.format("onNext with return value \"%b\"", bool));
            }
        };
    }

}
