package dk.mustache.beaconbacon;

import android.content.pm.ApplicationInfo;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import dk.mustache.beaconbacon.customviews.CustomToolbar;
import dk.mustache.beaconbacon.data.ApiManager;
import dk.mustache.beaconbacon.data.AllPlacesAsyncResponse;
import dk.mustache.beaconbacon.data.GetAllPlacesAsync;
import dk.mustache.beaconbacon.data.GetSpecificPlaceAsync;
import dk.mustache.beaconbacon.data.SpecificPlaceAsyncResponse;
import dk.mustache.beaconbacon.datamodels.AllPlaces;
import dk.mustache.beaconbacon.datamodels.Place;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MapActivity extends AppCompatActivity implements View.OnClickListener, AllPlacesAsyncResponse, SpecificPlaceAsyncResponse {
    GetAllPlacesAsync getAllPlacesAsync = new GetAllPlacesAsync();

    private Toolbar toolbar;
    public FloatingActionButton floatingActionButton;
    private PoiSelectionFragment poiSelectionFragment;
    private PlaceSelectionFragment placeSelectionFragment;
    private TextView toolbarTitle;
    private TextView toolbarSubtitle;
    private ImageView arrowLeft;
    private ImageView arrowRight;

    private ImageView mapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        toolbar = findViewById(R.id.bb_toolbar_regular);
        setSupportActionBar(toolbar);

        if(getSupportActionBar() != null)
            getSupportActionBar().setDisplayShowTitleEnabled(false);

        LinearLayout toolbarTitleLayout = findViewById(R.id.bb_toolbar_title_layout);
        toolbarTitleLayout.setOnClickListener(this);

        toolbarTitle = findViewById(R.id.bb_toolbar_title);
        toolbarSubtitle = findViewById(R.id.bb_toolbar_subtitle);

        floatingActionButton = findViewById(R.id.map_poi_fab);
        floatingActionButton.setOnClickListener(this);

        arrowLeft = findViewById(R.id.bb_toolbar_arrow_left);
        arrowLeft.setOnClickListener(this);
        arrowRight = findViewById(R.id.bb_toolbar_arrow_right);
        arrowRight.setOnClickListener(this);

        getAllPlacesAsync.delegate = this;
        ApiManager.createInstance().fetchAllPlacesAsync(getAllPlacesAsync);

        mapView = findViewById(R.id.map_view);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_close) {
            if(getSupportFragmentManager().getBackStackEntryCount() != 0) {
                //Let the fragment consume the event
                return false;
            } else {
                //Dismiss activity
                finish();
                overridePendingTransition(0, R.anim.slide_out_bottom);
                return true;
            }
        }

        return false;
    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.bb_toolbar_title_layout) {
            floatingActionButton.hide();
            placeSelectionFragment = new PlaceSelectionFragment();
            getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_bottom, R.anim.slide_out_bottom, R.anim.slide_in_bottom, R.anim.slide_out_bottom)
                    .replace(R.id.fragment_container, placeSelectionFragment, "place_selection_fragment")
                    .addToBackStack(null)
                    .commit();

        } else if (i == R.id.map_poi_fab) {
            floatingActionButton.hide();
            poiSelectionFragment = new PoiSelectionFragment();
            getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_bottom, R.anim.slide_out_bottom, R.anim.slide_in_bottom, R.anim.slide_out_bottom)
                    .replace(R.id.fragment_container, poiSelectionFragment, "poi_selection_fragment")
                    .addToBackStack(null)
                    .commit();

        } else if (i == R.id.bb_toolbar_arrow_left) {
            updateArrows(-1);

        } else if (i == R.id.bb_toolbar_arrow_right) {
            updateArrows(1);

        }
    }

    //TODO Apply order of Floors to the mix - currently 1. sal comes before Stuen on Museum 6 i.e.
    private void updateArrows(int direction) {
        if(ApiManager.getInstance().getCurrentPlace().getFloors() != null && ApiManager.getInstance().getCurrentPlace().getFloors().size() != 0) {
            //Change the floor
            if (direction == 1 && ApiManager.getInstance().getCurrentFloor() != ApiManager.getInstance().getCurrentPlace().getFloors().size() - 1)
                ApiManager.getInstance().setCurrentFloor(ApiManager.getInstance().getCurrentFloor() + direction);
            else if (direction == -1 && ApiManager.getInstance().getCurrentFloor() != 0)
                ApiManager.getInstance().setCurrentFloor(ApiManager.getInstance().getCurrentFloor() + direction);

            //Update text
            toolbarSubtitle.setText(ApiManager.getInstance().getCurrentPlace().getName());
            if(ApiManager.getInstance().getCurrentPlace().getFloors() != null &&
                    ApiManager.getInstance().getCurrentPlace().getFloors().size() != 0)
                toolbarTitle.setText(ApiManager.getInstance().getCurrentPlace().getFloors().get(ApiManager.getInstance().getCurrentFloor()).getName());
            else
                toolbarTitle.setText("-"); //TODO What should we present when only 1 floor is available?

            //Update arrows
            if (ApiManager.getInstance().getCurrentFloor() == 0)
                arrowLeft.setImageDrawable(getResources().getDrawable(R.drawable.ic_chevron_left_light));
            else
                arrowLeft.setImageDrawable(getResources().getDrawable(R.drawable.ic_chevron_left));

            if (ApiManager.getInstance().getCurrentFloor() == ApiManager.getInstance().getCurrentPlace().getFloors().size() - 1)
                arrowRight.setImageDrawable(getResources().getDrawable(R.drawable.ic_chevron_right_light));
            else
                arrowRight.setImageDrawable(getResources().getDrawable(R.drawable.ic_chevron_right));
        } else {
            arrowLeft.setImageDrawable(getResources().getDrawable(R.drawable.ic_chevron_left_light));
            arrowRight.setImageDrawable(getResources().getDrawable(R.drawable.ic_chevron_right_light));
        }
    }

    public void allPlacesAsyncFinished(JsonObject output) {
        Log.d("All Places Json", String.valueOf(output));

        JsonParser parser = new JsonParser();
        JsonElement mJson =  parser.parse(output.toString());
        Gson gson = new Gson();
        AllPlaces allPlaces = gson.fromJson(mJson, AllPlaces.class);

        ApiManager.createInstance().setAllPlaces(allPlaces);

        for(int i=0; i<allPlaces.getData().size(); i++) {
            GetSpecificPlaceAsync getSpecificPlaceAsync = new GetSpecificPlaceAsync();
            getSpecificPlaceAsync.delegate = this;
            ApiManager.createInstance().fetchSpecificPlaceAsync(getSpecificPlaceAsync, String.valueOf(allPlaces.getData().get(i).getId()));
        }

        if(placeSelectionFragment != null && placeSelectionFragment.adapter != null)
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    placeSelectionFragment.adapter.notifyDataSetChanged();
                }
            });
    }

    @Override
    public void specificPlaceAsyncFinished(JsonObject output) {
        Log.d("Specific Place Json", String.valueOf(output));

        JsonParser parser = new JsonParser();
        JsonElement mJson =  parser.parse(output.toString());
        Gson gson = new Gson();
        Place place = gson.fromJson(mJson, Place.class);

        for(int i=0; i<ApiManager.createInstance().getAllPlaces().getData().size(); i++) {
            if (ApiManager.createInstance().getAllPlaces().getData().get(i).getId() == place.getId()) {

                ApiManager.getInstance().getAllPlaces().getData().set(i, place);

                if(i == 0) {
                    //TODO Where and how do we determine current place?
                    ApiManager.getInstance().setCurrentPlace(place);
                    ApiManager.getInstance().setCurrentFloor(0);
                    toolbarSubtitle.setText(ApiManager.getInstance().getCurrentPlace().getName());
                    toolbarTitle.setText(ApiManager.getInstance().getCurrentPlace().getFloors().get(0).getName());

                    updateArrows(0);



                    //----- Picasso testing
                    OkHttpClient client = new OkHttpClient.Builder()
                            .addInterceptor(new Interceptor() {
                                @Override
                                public Response intercept(Chain chain) throws IOException {
                                    Request newRequest = chain.request().newBuilder()
                                            .addHeader("Authorization", "Bearer $2y$10$xNbv82pkfvDT7t4I2cwkLu4csCtd75PIZ/G06LylcMnjwdj/vmJtm")
                                            .build();

                                    return chain.proceed(newRequest);
                                }
                            })
                            .build();

                    Picasso picasso = new Picasso.Builder(this)
                            .downloader(new OkHttp3Downloader(client))
                            .build();
                    //-------Picasso testing

//
//
//                    //In progress
//                    //TODO Seems we need further access to get image through picasso
//                    OkHttpClient client = new OkHttpClient.Builder()
//                            .addInterceptor(new Interceptor() {
//                                @Override
//                                public Response intercept(Chain chain) throws IOException {
//                                    Request newRequest = chain.request().newBuilder()
//                                            .addHeader("Authorization", "Bearer $2y$10$xNbv82pkfvDT7t4I2cwkLu4csCtd75PIZ/G06LylcMnjwdj/vmJtm")
//                                            .build();
//                                    return chain.proceed(newRequest);%s
//                                }
//                            })
//                            .build();
//
//                    Picasso picasso = new Picasso.Builder(this)
//                            .downloader(new OkHttp3Downloader(client))
//                            .build();

                    if(place.getFloors() != null) {
                        //Usually Picasso.with(context), but possibly not when using okhttp3downloader
                        picasso.load(place.getFloors().get(0).getImage())
                                .resize(Integer.valueOf(place.getFloors().get(0).getMap_width_in_pixels() != null ? place.getFloors().get(0).getMap_width_in_pixels() : "0"),
                                        Integer.valueOf(place.getFloors().get(0).getMap_height_int_pixels() != null ? place.getFloors().get(0).getMap_height_int_pixels() : "0"))
                                .centerCrop()
                                .into(mapView, new com.squareup.picasso.Callback() {
                                    @Override
                                    public void onSuccess() {
                                        Log.d("Picasso", "success");
                                    }

                                    @Override
                                    public void onError() {
                                        Log.d("Picasso", "error");
                                    }
                                });
                    }
                }
            }
        }
    }

    public void setNewCurrentPlace(Place newCurrentPlace) {
        Log.d("new Place, id", newCurrentPlace.getId() + "");
        Log.d("New place, floors", newCurrentPlace.getFloors() != null ? newCurrentPlace.getFloors().size() + "" : "0");

        toolbarSubtitle.setText(newCurrentPlace.getName());
        if(newCurrentPlace.getFloors() != null && newCurrentPlace.getFloors().size() != 0)
            toolbarTitle.setText(newCurrentPlace.getFloors().get(0).getName());
        else
            toolbarTitle.setText("-"); //TODO What should we present when only 1 floor is available?

        updateArrows(0);

        getSupportFragmentManager().popBackStack();

        //TODO Reload map with new place and floor
    }
}
