package dk.mustache.beaconbacon;

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

import android.app.Application;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import dk.mustache.beaconbacon.interfaces.AllPlacesAsyncResponse;
import dk.mustache.beaconbacon.api.ApiManager;
import dk.mustache.beaconbacon.data.DataManager;
import dk.mustache.beaconbacon.api.GetAllPlacesAsync;
import dk.mustache.beaconbacon.datamodels.AllPlaces;

public class BBApplication extends Application implements AllPlacesAsyncResponse {
    public static final String TAG = "BeaconBacon";
    public static final String PLACE_ID = "place_id";

    GetAllPlacesAsync getAllPlacesAsync = new GetAllPlacesAsync();

    @Override
    public void onCreate() {
        super.onCreate();

        //Instantiate our ApiManager
        ApiManager.createInstance(this);
        DataManager.createInstance(this);

        //Get the basics of All Places right away
        getAllPlacesAsync.delegate = this;
        ApiManager.getInstance().fetchAllPlacesAsync(getAllPlacesAsync);
    }

    @Override
    public void allPlacesAsyncFinished(JsonObject output) {
        Log.i(TAG, "All places fetched");

        //Map JsonObject output to the AllPlaces class
        JsonElement mJson =  new JsonParser().parse(output.toString());
        AllPlaces allPlaces = new Gson().fromJson(mJson, AllPlaces.class);

        //Set all places in our DataManager
        DataManager.getInstance().setAllPlaces(allPlaces);

        //THIS IS FOR SPECIFIC PLACE
//        for(int i=0; i<allPlaces.getData().size(); i++) {
//            GetSpecificPlaceAsync getSpecificPlaceAsync = new GetSpecificPlaceAsync();
//            getSpecificPlaceAsync.delegate = this;
//            ApiManager.createInstance(this).fetchSpecificPlaceAsync(getSpecificPlaceAsync, String.valueOf(allPlaces.getData().get(i).getId()));
//        }
//
//        if(placeSelectionFragment != null && placeSelectionFragment.adapter != null)
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    placeSelectionFragment.adapter.notifyDataSetChanged();
//                }
//            });
    }
}
