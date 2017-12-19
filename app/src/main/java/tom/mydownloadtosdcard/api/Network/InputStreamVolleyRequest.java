package tom.mydownloadtosdcard.api.Network;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by 3dium on 14.12.2017.
 */

public class InputStreamVolleyRequest extends Request<byte[]> {

    private final Response.Listener<byte[]> mListener;
    private Map<String, String> mParams;
    public Map<String, String> rHeaders;

    public InputStreamVolleyRequest(int method, String url, Response.Listener<byte[]> listener, Response.ErrorListener errorListener, HashMap<String, String> params) {
        super(method, url, errorListener);

        setShouldCache(false);
        mListener = listener;
        mParams = params;
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return mParams;
    }

    @Override
    protected void deliverResponse(byte[] response) {
        mListener.onResponse(response);
    }

    @Override
    protected Response<byte[]> parseNetworkResponse(NetworkResponse response) {
        rHeaders = response.headers;
        return Response.success(response.data, HttpHeaderParser.parseCacheHeaders(response));
    }

}
