package tom.mydownloadtosdcard.presenter;

/**
 * Created by 3dium on 15.12.2017.
 */

public interface Presenter<V> {

    void attachView(V viiw);
    void detachView();
}
