package dk.mustache.beaconbacon.api;

import android.content.Context;
import android.support.annotation.NonNull;

import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.List;

import dk.mustache.beaconbacon.datamodels.BBPoi;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/* CLASS NAME GOES HERE

Copyright (c) 2017 Mustache ApS

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
*/

public class ApiManager {
    private static ApiManager instance;
    private Context context;

    //Test Environment
    private static final String BASE_URL = "https://app.beaconbacon.io/api/v2/";
    private static final String API_KEY = "Bearer $2y$10$xNbv82pkfvDT7t4I2cwkLu4csCtd75PIZ/G06LylcMnjwdj/vmJtm";
    private static final String AUTHORIZATION = "Authorization";

    private ApiService apiService;
    private Picasso picasso;



    //region ApiManager setup
    public static ApiManager createInstance(Context context) {
        if (instance == null) {
            instance = new ApiManager(context);
        }

        return instance;
    }

    public static ApiManager getInstance() {
        return instance;
    }

    private ApiManager(Context context) {
        this.context = context;

        setupApiService();
        setupPicassoClient();
    }
    //endregion



    //region ApiService and Picasso Setup
    private void setupApiService() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        setApiService(retrofit.create(ApiService.class));
    }

    public ApiService getApiService() {
        return apiService;
    }

    public void setApiService(ApiService apiService) {
        this.apiService = apiService;
    }

    private void setupPicassoClient() {
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(@NonNull Chain chain) throws IOException {
                        Request newRequest = chain.request().newBuilder()
                                .addHeader(AUTHORIZATION, API_KEY)
                                .build();

                        return chain.proceed(newRequest);
                    }
                })
                .build();

        OkHttp3Downloader okHttp3Downloader = new OkHttp3Downloader(client);

        setPicasso(new Picasso.Builder(context)
                .downloader(okHttp3Downloader)
                .build());
    }

    public Picasso getPicasso() {
        return picasso;
    }

    public void setPicasso(Picasso picasso) {
        this.picasso = picasso;
    }
    //endregion



    //region Async Tasks
    public void fetchAllPlacesAsync(GetAllPlacesAsync getAllPlacesAsync) {
        getAllPlacesAsync.execute();
    }

    public void fetchSpecificPlaceAsync(GetSpecificPlaceAsync getSpecificPlaceAsync, String placeId) {
        getSpecificPlaceAsync.execute(placeId, null, null);
    }

    public void fetchMenuOverviewAsync(GetMenuOverviewAsync getMenuOverviewAsync, String placeId) {
        getMenuOverviewAsync.execute(placeId, null, null);
    }

    public void findTheBookAsync(FindTheBookAsync findTheBookAsync, String placeId) {
        findTheBookAsync.execute(placeId, null, null);
    }

    public void getFloorImage(GetFloorImageAsync getFloorImageAsync) {
        getFloorImageAsync.execute(null, null, null);
    }

    public void getIconImage(GetIconImageAsync getIconImageAsync, List<BBPoi> selectedPois) {
        getIconImageAsync.execute(selectedPois, null, null);
    }
    //endregion
}
