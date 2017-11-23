package dk.mustache.beaconbacon;

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

import dk.mustache.beaconbacon.data.ApiManager;
import dk.mustache.beaconbacon.data.AllPlacesAsyncResponse;
import dk.mustache.beaconbacon.data.GetAllPlacesAsync;
import dk.mustache.beaconbacon.data.GetSpecificPlaceAsync;
import dk.mustache.beaconbacon.data.SpecificPlaceAsyncResponse;
import dk.mustache.beaconbacon.datamodels.AllPlaces;
import dk.mustache.beaconbacon.datamodels.Place;

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
    private LinearLayout toolbarLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        toolbar = findViewById(R.id.bb_toolbar_regular);
        setSupportActionBar(toolbar);

        if(getSupportActionBar() != null)
            getSupportActionBar().setDisplayShowTitleEnabled(false);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;

        toolbarLayout = findViewById(R.id.bb_toolbar_layout);

        LinearLayout toolbarTitleLayout = findViewById(R.id.bb_toolbar_title_layout);
        toolbarTitleLayout.setOnClickListener(this);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) toolbarTitleLayout.getLayoutParams();
        layoutParams.width = width/4*2;
        toolbarTitleLayout.setLayoutParams(layoutParams);

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
        switch (view.getId()) {
            case R.id.bb_toolbar_title_layout:
                floatingActionButton.hide();
                placeSelectionFragment = new PlaceSelectionFragment();
                getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_bottom, R.anim.slide_out_bottom, R.anim.slide_in_bottom, R.anim.slide_out_bottom)
                        .replace(R.id.fragment_container, placeSelectionFragment, "place_selection_fragment")
                        .addToBackStack(null)
                        .commit();
                break;
            case R.id.map_poi_fab:
                floatingActionButton.hide();
                poiSelectionFragment = new PoiSelectionFragment();
                getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_bottom, R.anim.slide_out_bottom, R.anim.slide_in_bottom, R.anim.slide_out_bottom)
                        .replace(R.id.fragment_container, poiSelectionFragment, "poi_selection_fragment")
                        .addToBackStack(null)
                        .commit();
                break;
            case R.id.bb_toolbar_arrow_left:
                if(ApiManager.getInstance().getCurrentPlace().getFloors().size() != 0) {

                    if (ApiManager.getInstance().getCurrentFloor() != 0)
                        ApiManager.getInstance().setCurrentFloor(ApiManager.getInstance().getCurrentFloor()-1);

                    if (ApiManager.getInstance().getCurrentFloor() == 0)
                        arrowLeft.setColorFilter(R.color.colorArrowsEnd, PorterDuff.Mode.SRC_ATOP);
                    else
                        arrowLeft.setColorFilter(R.color.colorArrows, PorterDuff.Mode.SRC_ATOP);

                    if (ApiManager.getInstance().getCurrentFloor() == ApiManager.getInstance().getCurrentPlace().getFloors().size() - 1)
                        arrowRight.setColorFilter(R.color.colorArrowsEnd, PorterDuff.Mode.SRC_ATOP);
                    else
                        arrowRight.setColorFilter(R.color.colorArrows, PorterDuff.Mode.SRC_ATOP);

                    toolbarLayout.requestLayout();
                }
                break;
            case R.id.bb_toolbar_arrow_right:
                if(ApiManager.getInstance().getCurrentPlace().getFloors().size() != 0) {
                    if (ApiManager.getInstance().getCurrentFloor() != ApiManager.getInstance().getCurrentPlace().getFloors().size() - 1)
                        ApiManager.getInstance().setCurrentFloor(ApiManager.getInstance().getCurrentFloor()+1);

                    if (ApiManager.getInstance().getCurrentFloor() == 0)
                        arrowLeft.setColorFilter(R.color.colorArrowsEnd, PorterDuff.Mode.SRC_ATOP);
                    else
                        arrowLeft.setColorFilter(R.color.colorArrows, PorterDuff.Mode.SRC_ATOP);

                    if (ApiManager.getInstance().getCurrentFloor() == ApiManager.getInstance().getCurrentPlace().getFloors().size() - 1)
                        arrowRight.setColorFilter(R.color.colorArrowsEnd, PorterDuff.Mode.SRC_ATOP);
                    else
                        arrowRight.setColorFilter(R.color.colorArrows, PorterDuff.Mode.SRC_ATOP);

                    toolbarLayout.requestLayout();
                }
                break;
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
                if(i == 0) {
                    //TODO Where and how do we determine current place?
                    ApiManager.getInstance().setCurrentPlace(place);
                    ApiManager.getInstance().setCurrentFloor(0);
                    toolbarTitle.setText(ApiManager.getInstance().getCurrentPlace().getName());
                    toolbarSubtitle.setText(ApiManager.getInstance().getCurrentPlace().getFloors().get(0).getName());
                    arrowLeft.setColorFilter(R.color.colorArrowsEnd, PorterDuff.Mode.SRC_ATOP);
                }

                ApiManager.createInstance().getAllPlaces().getData().set(i, place);
            }
        }
    }

    public void setNewCurrentPlace(Place newCurrentPlace) {
        toolbarTitle.setText(newCurrentPlace.getName());
        if(newCurrentPlace.getFloors().size() != 0)
            toolbarSubtitle.setText(newCurrentPlace.getFloors().get(0).getName());
        else
            toolbarSubtitle.setText("-"); //TODO What should we present when only 1 floor is available?

        arrowLeft.setColorFilter(R.color.colorArrowsEnd, PorterDuff.Mode.SRC_ATOP);

        getSupportFragmentManager().popBackStack();

        //TODO Reload map with new place and floor
    }
}
