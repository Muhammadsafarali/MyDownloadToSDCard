package tom.mydownloadtosdcard.api.Network;

import android.database.Observable;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;

import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Url;


/**
 * Created by 3dium on 14.12.2017.
 */

public interface Routes {

    @GET
    Observable<ResponseBody> downloadFileDynamicUrlSync(@Url String fileUrl);


    class Factory {
        public static Gson getGson() {
            return GSON;
        }

        private static final Gson GSON = new GsonBuilder()
                .setDateFormat("dd.MM.yyyy HH:mm:ss")
                .create();
    }
}
