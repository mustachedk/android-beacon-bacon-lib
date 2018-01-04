package dk.mustache.beaconbacon.activities;

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

import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;

import dk.mustache.beaconbacon.BBApplication;
import dk.mustache.beaconbacon.R;
import dk.mustache.beaconbacon.api.ApiManager;
import dk.mustache.beaconbacon.api.GetAllPlacesAsync;
import dk.mustache.beaconbacon.data.BeaconBaconManager;
import dk.mustache.beaconbacon.datamodels.AllPlaces;
import dk.mustache.beaconbacon.datamodels.BBConfigurationObject;
import dk.mustache.beaconbacon.datamodels.BBPlace;
import dk.mustache.beaconbacon.datamodels.BBRequestObject;
import dk.mustache.beaconbacon.interfaces.AllPlacesAsyncResponse;

import static dk.mustache.beaconbacon.BBApplication.FAUST_ID;
import static dk.mustache.beaconbacon.BBApplication.PLACE_ID;

public class DemoActivity extends AppCompatActivity implements View.OnClickListener, AllPlacesAsyncResponse {
    public static final String TAG = "BeaconBacon";

    private Button configNoStyle;
    private Button configStyle1;
    private Button configStyle2;

    private Button placeKbhBib;
    private Button placeValbyBib;
    private Button placeUnsupported;

    private String placeId = null;
//    private String faustId = "29715394"; // POI
    private String faustId = "24120236"; // Area


    //Test Environment
    private String baseUrl = "https://app.beaconbacon.io/api/v2/";
    //Production Environment
    //private String baseUrl = "https://wayfindingkkb.dk/api/v2/";

    private String apiKey = "$2y$10$xNbv82pkfvDT7t4I2cwkLu4csCtd75PIZ/G06LylcMnjwdj/vmJtm";

    GetAllPlacesAsync getAllPlacesAsync = new GetAllPlacesAsync();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Set Layout
        setContentView(R.layout.activity_demo);

        configNoStyle = findViewById(R.id.config_no_style);
        configStyle1 = findViewById(R.id.config_style_1);
        configStyle2 = findViewById(R.id.config_style_2);

        placeKbhBib = findViewById(R.id.place_kbh_bib);
        placeValbyBib = findViewById(R.id.place_valby);
        placeUnsupported = findViewById(R.id.place_unsupported);

        Button mapNoWayfinding = findViewById(R.id.map_no_wayfinding);
        Button mapWayfinding = findViewById(R.id.map_wayfinding);

        configNoStyle.setOnClickListener(this);
        configStyle1.setOnClickListener(this);
        configStyle2.setOnClickListener(this);

        placeKbhBib.setOnClickListener(this);
        placeValbyBib.setOnClickListener(this);
        placeUnsupported.setOnClickListener(this);

        mapNoWayfinding.setOnClickListener(this);
        mapWayfinding.setOnClickListener(this);

        BeaconBaconManager.createInstance(BBApplication.getContext());
    }

    private void initBeaconBacon() {
        //Instantiate our ApiManager
        ApiManager.createInstance(BBApplication.getContext());

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

        //Sort the Place's floors by Order
        if(allPlaces.getData() != null) {
            Collections.sort(allPlaces.getData(), new Comparator<BBPlace>() {
                @Override
                public int compare(BBPlace place1, BBPlace place2) {
                    return place1.getOrder() - place2.getOrder();
                }
            });
        }

        //Set all places in our BeaconBaconManager
        BeaconBaconManager.getInstance().setAllPlaces(allPlaces);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.config_no_style:
                setConfigBtnColor(configNoStyle);
                break;

            case R.id.config_style_1:
                setConfigBtnColor(configStyle1);
                applyConfiguration("Arial.ttf", android.R.color.holo_red_dark, baseUrl, apiKey);
                break;

            case R.id.config_style_2:
                setConfigBtnColor(configStyle2);
                applyConfiguration("Courier New.ttf", android.R.color.holo_green_dark, baseUrl, apiKey);
                break;

            case R.id.place_kbh_bib:
                setPlaceBtnColor(placeKbhBib);
                applyPlace("1");
                break;

            case R.id.place_valby:
                setPlaceBtnColor(placeValbyBib);
                applyPlace("2");
                break;

            case R.id.place_unsupported:
                setPlaceBtnColor(placeUnsupported);
                applyPlace("-1");
                break;

            case R.id.map_no_wayfinding:
                Intent intentNoWayfinding = new Intent(DemoActivity.this, BeaconBaconActivity.class);
                intentNoWayfinding.putExtra(PLACE_ID, placeId);
                startActivity(intentNoWayfinding);
                overridePendingTransition(R.anim.slide_in_bottom, R.anim.hold_anim);
                break;

            case R.id.map_wayfinding:
                BeaconBaconManager.getInstance().setRequestObject(new BBRequestObject("IMS", faustId, "En mand der hedder Ove", "SK", BitmapFactory.decodeResource(getResources(), R.drawable
                        .ic_book)));
                Intent intentWayfinding = new Intent(DemoActivity.this, BeaconBaconActivity.class);
                intentWayfinding.putExtra(PLACE_ID, placeId);
                intentWayfinding.putExtra(FAUST_ID, faustId);
                startActivity(intentWayfinding);
                overridePendingTransition(R.anim.slide_in_bottom, R.anim.hold_anim);
                break;
        }
    }

    private void setConfigBtnColor(Button button) {
        configNoStyle.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        configStyle1.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        configStyle2.setBackgroundColor(getResources().getColor(android.R.color.transparent));

        button.setBackgroundColor(getResources().getColor(R.color.mapBackground));
    }

    private void applyConfiguration(String fontName, int color, String baseUrl, String apiKey) {
        AssetManager assetManager = getApplicationContext().getAssets();
        Typeface typeface = Typeface.createFromAsset(assetManager, String.format(Locale.getDefault(), "fonts/%s", fontName));

        //Apply to a new Configuration Object
        BeaconBaconManager.getInstance().setConfigurationObject(new BBConfigurationObject(typeface, color, baseUrl, String.format("Bearer %s", apiKey)));

        initBeaconBacon();
    }

    private void setPlaceBtnColor(Button button) {
        placeKbhBib.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        placeValbyBib.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        placeUnsupported.setBackgroundColor(getResources().getColor(android.R.color.transparent));

        button.setBackgroundColor(getResources().getColor(R.color.mapBackground));
    }

    private void applyPlace(String placeId) {
        this.placeId = placeId;
    }
}
