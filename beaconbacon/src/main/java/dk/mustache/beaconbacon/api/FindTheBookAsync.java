package dk.mustache.beaconbacon.api;

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


import android.os.AsyncTask;

import com.google.gson.JsonObject;

import java.io.IOException;

import dk.mustache.beaconbacon.data.BeaconBaconManager;
import dk.mustache.beaconbacon.interfaces.FindTheBookAsyncResponse;
import retrofit2.Call;
import retrofit2.Response;

//region AsyncTasks
public class FindTheBookAsync extends AsyncTask<String, Void, JsonObject> {
    public FindTheBookAsyncResponse delegate = null;

    @Override
    protected JsonObject doInBackground(String... strings) {

        //Generate the body for the API service with the request object from BeaconBaconManager
        JsonObject jsonObject = new JsonObject();
        JsonObject data = new JsonObject();
        jsonObject.addProperty("find_identifier", BeaconBaconManager.getInstance().getRequestObject().getFind_identifier());
        data.addProperty("Faust", BeaconBaconManager.getInstance().getRequestObject().getFaust_id());
        jsonObject.add("data", data);

        Call<JsonObject> call = ApiManager.getInstance().getApiService().findTheBook(strings[0], jsonObject);

        Response<JsonObject> response = null;
        try {
            response = call.execute();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return response != null ? response.body() : null;
    }

    @Override
    protected void onPostExecute(JsonObject result) {
        super.onPostExecute(result);

        delegate.findTheBookAsyncFinished(result);
    }
}
//endregion
