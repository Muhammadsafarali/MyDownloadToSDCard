package tom.mydownloadtosdcard.utils;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;

/**
 * Created by 3dium on 14.12.2017.
 */

public class ErrorHandler {

    public static String getVolleyError(VolleyError error) {
        String message = null;

        if (error instanceof AuthFailureError) {
            message = "Неверный логин или пароль";
        } else
        if (error instanceof NetworkError) {
            message = "Не могу подключиться к Интернету...пожалуйста, проверьте ваше соединение";
        } else
        if (error instanceof ServerError) {
            message = "Сервер не отвечает. Пожалуйста, попробуйте снова через некоторое время";
        } else
        if (error instanceof ParseError) {
            message = "Ошибка чтения ответа от сервера. Пожалуйста, попробуйте снова через некоторое время";
        } else
        if (error instanceof NoConnectionError) {
            message = "Не могу подключиться к Интернету...пожалуйста, проверьте ваше соединение";
        } else
        if (error instanceof TimeoutError) {
            message = "Тайм-Аут Соединения. Пожалуйста, проверьте ваше интернет-соединение";
        }
        return message;
    }


}
