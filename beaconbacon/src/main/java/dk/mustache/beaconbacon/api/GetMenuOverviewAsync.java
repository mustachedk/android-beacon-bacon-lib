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
import android.os.Bundle;

import com.google.gson.JsonArray;

import java.io.IOException;

import dk.mustache.beaconbacon.api.ApiManager;
import dk.mustache.beaconbacon.interfaces.MenuOverviewAsyncResponse;
import retrofit2.Call;
import retrofit2.Response;

public class GetMenuOverviewAsync extends AsyncTask<String, Void, Bundle> {
    public MenuOverviewAsyncResponse delegate = null;

    @Override
    protected Bundle doInBackground(String... strings) {

        Call<JsonArray> call = ApiManager.getInstance().getApiService().getMenuOverview(strings[0]);

        Response<JsonArray> response = null;
        try {
            response = call.execute();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Bundle bundle = new Bundle();

        if(response != null && response.body() != null)
            bundle.putString("json", response.body().toString());

        bundle.putString("place_id", strings[0]);

        return bundle;
    }

    @Override
    protected void onPostExecute(Bundle result) {
        super.onPostExecute(result);

        delegate.menuOverviewAsyncFinished(result);
    }
}
//endregion
