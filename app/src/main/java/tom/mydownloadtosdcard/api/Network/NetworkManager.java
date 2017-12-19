package tom.mydownloadtosdcard.api.Network;

import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;

import tom.mydownloadtosdcard.utils.Constant;
import tom.mydownloadtosdcard.utils.ErrorHandler;
import tom.mydownloadtosdcard.utils.ObserverNotifyResult;
import tom.mydownloadtosdcard.utils.SignInResult;
import tom.mydownloadtosdcard.myApplication;

/**
 * Created by 3dium on 14.12.2017.
 */

public class NetworkManager extends Observable {

    private static final String LOG_TAG = "NetworkManager";

    public void downloadFile(final int target, final int type_request, final String requestUrl, Context context, final UsbDevice device, final UsbManager mUsbManager) {

//        Model model = Facade.getInstance().getModelById(model_id);
//        String requestUrl = model.getPath();
        InputStreamVolleyRequest request = new InputStreamVolleyRequest(Request.Method.GET, requestUrl,
                new Response.Listener<byte[]>() {
                    @Override
                    public void onResponse(byte[] response) {
                        try {
                            if (response != null) {
                                int count;
                                try {

                                    InputStream inputStream = new ByteArrayInputStream(response);

//                                    String pth = model.getName().replace("-", "_");
//                                    File file = getExternalDir(context, pth);

                                    // Сохранить путь до файла
                                    byte data[] = new byte[1024];
                                    int Timeout = 0;
                                    boolean forceClaim = true;

                                    if (device != null && mUsbManager != null) {
                                        UsbInterface intf = device.getInterface(0);
                                        UsbEndpoint endpoint = intf.getEndpoint(2 );
                                        UsbDeviceConnection connection = mUsbManager.openDevice(device);
                                        connection.claimInterface(intf, forceClaim);

                                        while ((count = inputStream.read(data)) != -1) {
                                            connection.bulkTransfer(endpoint, data, data.length, Timeout);
                                        }

                                        connection.close();
                                    }
//                                        stopLoading(target, type_request, model_id);

                                }
                                catch (IOException ex) {
                                    ex.printStackTrace();
                                    sendError(target, type_request, "Не удается загрузить модель", Constant.StatusCode_Error_Download_File);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            sendError(target, type_request, "Не удается загрузить модель", Constant.StatusCode_Error_Download_File);
                        }
                    }
                } ,new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                try {
//                    handleError(error, target, type_request);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

        }, null);

//        startLoad(target, type_request);
        RequestQueue mRequestQueue = Volley.newRequestQueue(context, new HurlStack());
        mRequestQueue.add(request);
    }

    public void getTokenVolley() {

        final String basic = "Basic cC1jcnVzYWRlckB5YW5kZXgucnU6MGYxMDAzZWMwNTc5YmQ0ZmIwMTBkY2JjYTQ0M2Y3Y2Q=";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, Constant.TOKEN, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                SignInResult result = Routes.Factory.getGson().fromJson(response.toString(), SignInResult.class);

                if (result != null) {
                    if (!result.isError()) {
                        Log.e(LOG_TAG, result.getToken());
                        //                        AuthUtils.setToken(result.getToken());
//                        stopLoading(target, type_request, null);
                    }
                    else {
//                        sendError(target, type_request, result.getDescription(), Constant.StatusCode_Error);
                        Log.e(LOG_TAG, result.getDescription());
                    }
                } else {
//                    sendError(target, type_request, "Json Object Is Null", Constant.StatusCode_Error);

                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
//                try {
//                    handleError(error, target, type_request);
//                } catch (Exception ex) {
//                    ex.printStackTrace();
//                    sendError(target, type_request, "Error", Constant.StatusCode_Error);
//                }
            }

        }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put(Constant.HEADER_AUTH, basic);
                headers.put(Constant.HEADER_APPN, Constant.APP_IDENTIFIER);
                return headers;
            }
        };

        VolleySingleton.getInstance(myApplication.getInstance().getApplicationContext()).addToRequestQueue(jsonObjectRequest);
    }

    private void handleError(VolleyError error, int target, final int type_request) {
        NetworkResponse networkResponse = error.networkResponse;

        String message = ErrorHandler.getVolleyError(error);
        if (message == null)
            message = "Тайм-Аут Соединения. Пожалуйста, проверьте ваше интернет-соединение";

        int statusCode = Constant.StatusCode_Unauthorized;
        if (type_request == Constant.RequestDownloadModel)
            statusCode = Constant.StatusCode_Error_Download_File;

        if (networkResponse != null)
            statusCode = networkResponse.statusCode;

        sendError(target, type_request, message, statusCode);
    }

    private void sendError(int target, int request, String msg, int status_code) {
        ObserverNotifyResult notifyResult = new ObserverNotifyResult();
        notifyResult.setTarget(target);
        notifyResult.setLoading(false);
        notifyResult.setRequest(request);
        notifyResult.setDescription(msg);
        notifyResult.setError(true);
        notifyResult.setStatus_code(status_code);

        setChanged();
        notifyObservers(notifyResult);
    }

    private void startLoad(int target, int request) {
        ObserverNotifyResult notifyResult = new ObserverNotifyResult();
        notifyResult.setError(false);
        notifyResult.setTarget(target);
        notifyResult.setRequest(request);
        notifyResult.setLoading(true);

        setChanged();
        notifyObservers(notifyResult);
    }

    private void stopLoading(int target, int request, Integer modelId) {
        ObserverNotifyResult notifyResult = new ObserverNotifyResult();
        notifyResult.setModelId(modelId);
        notifyResult.setRequest(request);
        notifyResult.setTarget(target);
        notifyResult.setLoading(false);
        notifyResult.setError(false);
        notifyResult.setStatus_code(Constant.StatusCode_OK);

        setChanged();
        notifyObservers(notifyResult);
    }

    private String getErrorMessage(int id) {
        return myApplication.getInstance().getResources().getString(id);
    }

}
