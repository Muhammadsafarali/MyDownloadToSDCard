package tom.mydownloadtosdcard;

import android.app.Application;
import android.content.Context;

import rx.Scheduler;
import rx.schedulers.Schedulers;

/**
 * Created by 3dium on 14.12.2017.
 */

public class myApplication extends Application {

    private static myApplication instance;
    private Scheduler defaultSubscribeScheduler;

    public static myApplication getInstance() {
        return instance;
    }

    public static myApplication get(Context context) {
        return (myApplication) context.getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public Scheduler defaultSubscribeScheduler() {
        if (defaultSubscribeScheduler == null) {
            defaultSubscribeScheduler = Schedulers.io();
        }
        return defaultSubscribeScheduler;
    }

}
