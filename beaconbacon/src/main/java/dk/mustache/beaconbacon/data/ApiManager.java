package dk.mustache.beaconbacon.data;

import java.io.IOException;

import retrofit2.Response;
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
    //Test Environment
    private String baseUrl = "https://app.beaconbacon.io/api/";
    private String apiKey = "$2y$10$xNbv82pkfvDT7t4I2cwkLu4csCtd75PIZ/G06LylcMnjwdj/vmJtm";
    private static final String apiVersion = "v2";

    private static ApiManager instance = new ApiManager();
    private ApiService apiService;

    private String allPlaces;

    public static ApiManager createInstance() {
        if (instance == null) {
            instance = new ApiManager();
        }

        return instance;
    }

    public static ApiManager getInstance() {
        return instance;
    }

    private ApiManager() {
        //Fetch the data using our ApiService
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl + apiVersion)
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

    public String getAllPlaces() {
        //We're already on a background thread, so just execute the call
        Response<String> response = null;
        try {
            return getApiService().getAllPlaces().execute().body();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void setAllPlaces(String allPlaces) {
        this.allPlaces = allPlaces;
    }
}
