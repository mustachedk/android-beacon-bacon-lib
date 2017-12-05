package dk.mustache.beaconbacon.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

/** ApiService.java

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

public interface ApiService {
    @Headers({
            "Authorization: Bearer $2y$10$xNbv82pkfvDT7t4I2cwkLu4csCtd75PIZ/G06LylcMnjwdj/vmJtm",
            "Accept: application/json"
    })
    @GET("place")
    Call<JsonObject> getAllPlaces();

    @Headers({
            "Authorization: Bearer $2y$10$xNbv82pkfvDT7t4I2cwkLu4csCtd75PIZ/G06LylcMnjwdj/vmJtm",
            "Accept: application/json"
    })
    @GET("place/{place_id}")
    Call<JsonObject> getSpecificPlace(@Path("place_id") String place_id);

    @Headers({
            "Authorization: Bearer $2y$10$xNbv82pkfvDT7t4I2cwkLu4csCtd75PIZ/G06LylcMnjwdj/vmJtm",
            "Accept: application/json",
    })
    @GET("place/{place_id}/menu")
    Call<JsonArray> getMenuOverview(@Path("place_id") String place_id);

    @Headers({
            "Authorization: Bearer $2y$10$xNbv82pkfvDT7t4I2cwkLu4csCtd75PIZ/G06LylcMnjwdj/vmJtm",
            "Accept: application/json"
    })
    @POST("place/{place_id}/find")
    Call<JsonObject> findTheBook(@Path("place_id") String place_id, @Body JsonObject body);
}
