package tom.mydownloadtosdcard.presenter;

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

    public void writeToUsb(UsbDevice device, UsbManager mUsbManager) {

        if (subscription != null) subscription.unsubscribe();
        myApplication application = myApplication.get(mainMvpView.getContext());

        int Timeout = 100;
        boolean forceClaim = true;

        if (device != null && mUsbManager != null) {
            UsbInterface intf = device.getInterface(0);
            UsbEndpoint endpoint = intf.getEndpoint(2 );
            UsbDeviceConnection connection = mUsbManager.openDevice(device);
            if (connection != null && connection.claimInterface(intf, forceClaim)) {
                Log.e(LOG_TAG, "open SUCCESS");

                byte[] init = {0x00,0x00,0x00,0x00,0x05,0x00,0x00,0x00,0x00,0x00,0x00,0x00};
                int x = connection.bulkTransfer(endpoint,init, init.length, Timeout);
                Log.e(LOG_TAG, "BulkTransfer returned " + x);
            }
        }
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
        mainMvpView.showMessage("message");
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
        MainPresenter.this.mainMvpView._log("performing long operation");

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            Log.e("MainPresenter","Operation was interrupted");
        }
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
               Log.e("MainPresenter", "Error in RxJava Demo concurrency: " + e.getMessage());
                MainPresenter.this.mainMvpView._log(String.format("Boo! Error %s", e.getMessage()));
//                _progress.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onNext(Boolean bool) {
                MainPresenter.this.mainMvpView._log(String.format("onNext with return value \"%b\"", bool));
            }
        };
    }

}
