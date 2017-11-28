package dk.mustache.beaconbacon.activities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import dk.mustache.beaconbacon.api.GetSpecificPlaceAsync;
import dk.mustache.beaconbacon.data.DataManager;
import dk.mustache.beaconbacon.datamodels.BBFloor;
import dk.mustache.beaconbacon.fragments.PlaceSelectionFragment;
import dk.mustache.beaconbacon.fragments.PoiSelectionFragment;
import dk.mustache.beaconbacon.R;
import dk.mustache.beaconbacon.api.ApiManager;
import dk.mustache.beaconbacon.api.GetMenuOverviewAsync;
import dk.mustache.beaconbacon.interfaces.MenuOverviewAsyncResponse;
import dk.mustache.beaconbacon.interfaces.SpecificPlaceAsyncResponse;
import dk.mustache.beaconbacon.datamodels.BBPlace;
import dk.mustache.beaconbacon.datamodels.BBPoiMenuItem;

import static dk.mustache.beaconbacon.BBApplication.PLACE_ID;

public class MapActivity extends AppCompatActivity implements View.OnClickListener, SpecificPlaceAsyncResponse, MenuOverviewAsyncResponse {
    public static final String TAG = "BeaconBacon";
    public static final String PLACE_SELECTION_FRAGMENT = "place_selection_fragment";
    public static final String POI_SELECTION_FRAGMENT = "poi_selection_fragment";

    private Toolbar toolbar;
    private ProgressBar progressBar;
    private TextView toolbarTitle;
    private TextView toolbarSubtitle;
    private ImageView arrowLeft;
    private ImageView arrowRight;

    public FloatingActionButton floatingActionButton;

    private PoiSelectionFragment poiSelectionFragment;
    private PlaceSelectionFragment placeSelectionFragment;

    private ImageView mapView;



    //region Android Lifecycle
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Set Layout
        setContentView(R.layout.activity_map);

        progressBar = findViewById(R.id.map_progress_bar);
        progressBar.setVisibility(View.VISIBLE);

        setupToolbar();

        floatingActionButton = findViewById(R.id.map_poi_fab);
        floatingActionButton.setOnClickListener(this);

        mapView = findViewById(R.id.map_view);

        //Determine if we already know which place to show
        if(getIntent().getStringExtra(PLACE_ID) != null) {
            //We got a place, let's show it right away

            progressBar.setVisibility(View.VISIBLE);

            //Initiate Fetch Specific Place
            GetSpecificPlaceAsync getSpecificPlaceAsync = new GetSpecificPlaceAsync();
            getSpecificPlaceAsync.delegate = this;
            ApiManager.getInstance().fetchSpecificPlaceAsync(getSpecificPlaceAsync, getIntent().getStringExtra(PLACE_ID));
        } else {
            final Handler handler = new Handler();
            final Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    //TODO Should remove this handler, AllPlaces must have been fetched before opening the library
                    if(DataManager.getInstance().getAllPlaces() != null && DataManager.getInstance().getAllPlaces().getData() != null) {
                        //We haven't gotten a place, let's prompt the user to select one
                        floatingActionButton.hide();
                        progressBar.setVisibility(View.GONE);

                        placeSelectionFragment = new PlaceSelectionFragment();
                        getSupportFragmentManager()
                                .beginTransaction()
                                .setCustomAnimations(R.anim.slide_in_bottom, R.anim.slide_out_bottom, R.anim.slide_in_bottom, R.anim.slide_out_bottom)
                                .replace(R.id.fragment_container, placeSelectionFragment, PLACE_SELECTION_FRAGMENT)
                                .addToBackStack(null)
                                .commit();
                    } else {
                        handler.postDelayed(this, 100);
                    }

                }
            };
            handler.postDelayed(runnable,100);
        }
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
                    .replace(R.id.fragment_container, placeSelectionFragment, PLACE_SELECTION_FRAGMENT)
                    .addToBackStack(null)
                    .commit();

        } else if (i == R.id.map_poi_fab) {
            floatingActionButton.hide();
            poiSelectionFragment = new PoiSelectionFragment();
            getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_bottom, R.anim.slide_out_bottom, R.anim.slide_in_bottom, R.anim.slide_out_bottom)
                    .replace(R.id.fragment_container, poiSelectionFragment, POI_SELECTION_FRAGMENT)
                    .addToBackStack(null)
                    .commit();

        } else if (i == R.id.bb_toolbar_arrow_left) {
            updateArrows(-1);

        } else if (i == R.id.bb_toolbar_arrow_right) {
            updateArrows(1);

        }
    }
    //endregion



    //region Setup
    private void setupToolbar() {
        toolbar = findViewById(R.id.bb_toolbar_regular);
        setSupportActionBar(toolbar);

        if(getSupportActionBar() != null)
            getSupportActionBar().setDisplayShowTitleEnabled(false);

        LinearLayout toolbarTitleLayout = findViewById(R.id.bb_toolbar_title_layout);
        toolbarTitleLayout.setOnClickListener(this);

        toolbarTitle = findViewById(R.id.bb_toolbar_title);
        toolbarSubtitle = findViewById(R.id.bb_toolbar_subtitle);

        arrowLeft = findViewById(R.id.bb_toolbar_arrow_left);
        arrowRight = findViewById(R.id.bb_toolbar_arrow_right);
        arrowLeft.setOnClickListener(this);
        arrowRight.setOnClickListener(this);
    }

    private void updateToolbar() {
        toolbarSubtitle.setText(DataManager.getInstance().getCurrentPlace().getName());
        if(DataManager.getInstance().getCurrentPlace().getFloors() != null && DataManager.getInstance().getCurrentPlace().getFloors().size() > 0)
            toolbarTitle.setText(DataManager.getInstance().getCurrentPlace().getFloors().get(0).getName());

        updateArrows(0);
    }
    //endregion



    //region Async Tasks Finished
    @Override
    public void specificPlaceAsyncFinished(JsonObject output) {
        Log.i(TAG, "Specific place fetched");

        JsonElement mJson = new JsonParser().parse(output.toString());
        BBPlace place = new Gson().fromJson(mJson, BBPlace.class);

        //Loop all places to find this one
        for (int i = 0; i < DataManager.getInstance().getAllPlaces().getData().size(); i++) {
            if (DataManager.getInstance().getAllPlaces().getData().get(i).getId() == place.getId()) {
                //Replace existing place with the fetched place
                DataManager.getInstance().getAllPlaces().getData().set(i, place);

                //Get the menu overview right away
                GetMenuOverviewAsync getMenuOverviewAsync = new GetMenuOverviewAsync();
                getMenuOverviewAsync.delegate = this;
                ApiManager.createInstance(this).fetchMenuOverviewAsync(getMenuOverviewAsync, String.valueOf(place.getId()));

                //Sort the Place's floors by Order
                Collections.sort(place.getFloors(), new Comparator<BBFloor>() {
                    @Override
                    public int compare(BBFloor floor1, BBFloor floor2) {
                        return floor1.getOrder() - floor2.getOrder();
                    }
                });

                //Set current Place and floor
                updateCurrentPlace(place);
                updateCurrentFloor(0);

                updateToolbar();

                if (place.getFloors() != null &&
                        place.getFloors().size() > 0 &&
                        !Objects.equals(place.getFloors().get(0).getImage(), "")) {
                        Log.i(TAG, "Loading image from url/cache");
                    Log.i(TAG, "Loading image from url/cache");

                    ApiManager.getInstance().getPicasso()
                            .load(place.getFloors().get(0).getImage())
                            .resize(1000, 1000)
                            .centerCrop()
                            .into(mapView, new com.squareup.picasso.Callback() {
                                @Override
                                public void onSuccess() {
                                    progressBar.setVisibility(View.GONE);
                                    Log.i(TAG, "Image successfully loaded");
                                }

                                @Override
                                public void onError() {
                                    Log.e(TAG, "An error occurred loading the image");
                                }
                            });
                } else {
                    mapView.setImageDrawable(null);
                    progressBar.setVisibility(View.GONE);
                    showAlert(getString(R.string.alert_title), getString(R.string.alert_message_library));
                }
            }
        }
    }

    @Override
    public void menuOverviewAsyncFinished(Bundle output) {
        int placeId = Integer.valueOf(output.getString("place_id"));

        Gson gson = new Gson();
        String jsonOutput = output.getString("json");
        Type listType = new TypeToken<List<BBPoiMenuItem>>(){}.getType();
        List<BBPoiMenuItem> menuItems = gson.fromJson(jsonOutput, listType);

        //Sort the Place's floors by Order
        if(menuItems != null) {
            Collections.sort(menuItems, new Comparator<BBPoiMenuItem>() {
                @Override
                public int compare(BBPoiMenuItem order1, BBPoiMenuItem order2) {
                    return order1.getOrder() - order2.getOrder();
                }
            });
        }

        for(int i=0; i<DataManager.getInstance().getAllPlaces().getData().size(); i++) {
            if(placeId == DataManager.getInstance().getAllPlaces().getData().get(i).getId()) {
                DataManager.getInstance().getAllPlaces().getData().get(i).setPoiMenuItem(menuItems);
            }
        }
    }
    //endregion



    //region Update Content
    public void setNewCurrentPlace(BBPlace newCurrentPlace) {
        Log.i(TAG, "Applying new place");

        toolbarSubtitle.setText(newCurrentPlace.getName());
        if(newCurrentPlace.getFloors() != null && newCurrentPlace.getFloors().size() != 0) {
            toolbarTitle.setText(newCurrentPlace.getFloors().get(0).getName());
        } else {
            toolbarTitle.setText("-");
        }

        updateArrows(0);

        progressBar.setVisibility(View.VISIBLE);

        getSupportFragmentManager().popBackStack();

        //Initiate Fetch Specific Place
        GetSpecificPlaceAsync getSpecificPlaceAsync = new GetSpecificPlaceAsync();
        getSpecificPlaceAsync.delegate = this;
        ApiManager.getInstance().fetchSpecificPlaceAsync(getSpecificPlaceAsync, String.valueOf(newCurrentPlace.getId()));
    }

    private void updateArrows(int direction) {
        if(DataManager.getInstance().getCurrentPlace().getFloors() != null && DataManager.getInstance().getCurrentPlace().getFloors().size() != 0) {

            int floorListSize = DataManager.getInstance().getCurrentPlace().getFloors().size();
            int currentFloor = DataManager.getInstance().getCurrentFloor();

            //Change the floor
            if (direction == 1 && currentFloor != floorListSize - 1) {
                //Update text
                toolbarSubtitle.setText(DataManager.getInstance().getCurrentPlace().getName());
                toolbarTitle.setText(DataManager.getInstance().getCurrentPlace().getFloors().get(currentFloor + direction).getName());

                updateCurrentFloor(currentFloor + direction);
            } else if (direction == -1 && currentFloor != 0) {
                //Update text
                toolbarSubtitle.setText(DataManager.getInstance().getCurrentPlace().getName());
                toolbarTitle.setText(DataManager.getInstance().getCurrentPlace().getFloors().get(currentFloor + direction).getName());

                updateCurrentFloor(currentFloor + direction);
            }

            //Update arrows
            if (currentFloor + direction == 0) {
                arrowLeft.setClickable(false);
                arrowLeft.setImageDrawable(getResources().getDrawable(R.drawable.ic_chevron_left_light));
            } else {
                arrowLeft.setClickable(true);
                arrowLeft.setImageDrawable(getResources().getDrawable(R.drawable.ic_chevron_left));
            }

            if (currentFloor + direction == floorListSize - 1) {
                arrowRight.setClickable(false);
                arrowRight.setImageDrawable(getResources().getDrawable(R.drawable.ic_chevron_right_light));
            } else {
                arrowRight.setClickable(true);
                arrowRight.setImageDrawable(getResources().getDrawable(R.drawable.ic_chevron_right));
            }
        } else {
            toolbarTitle.setText("-");
            arrowLeft.setImageDrawable(getResources().getDrawable(R.drawable.ic_chevron_left_light));
            arrowRight.setImageDrawable(getResources().getDrawable(R.drawable.ic_chevron_right_light));
        }
    }

    private void updateCurrentFloor(int newCurrentFloor) {
        progressBar.setVisibility(View.VISIBLE);

        DataManager.getInstance().setCurrentFloor(newCurrentFloor);
        BBPlace currentPlace = DataManager.getInstance().getCurrentPlace();

        if (currentPlace.getFloors() != null &&
                currentPlace.getFloors().size() > 0 &&
                !Objects.equals(currentPlace.getFloors().get(newCurrentFloor).getImage(), "")) {
            Log.i(TAG, "Loading image from url/cache");

            ApiManager.getInstance().getPicasso()
                    .load(currentPlace.getFloors().get(newCurrentFloor).getImage())
                    .resize(1000, 1000)
                    .centerCrop()
                    .into(mapView, new com.squareup.picasso.Callback() {
                        @Override
                        public void onSuccess() {
                            progressBar.setVisibility(View.GONE);
                            Log.i(TAG, "Image successfully loaded");
                        }

                        @Override
                        public void onError() {
                            Log.e(TAG, "An error occurred loading the image");
                        }
                    });
        } else {
            mapView.setImageDrawable(null);
        }

    }

    private void updateCurrentPlace(BBPlace newCurrentPlace) {
        progressBar.setVisibility(View.VISIBLE);
        DataManager.getInstance().setCurrentPlace(newCurrentPlace);
    }
    //endregion



    //region Misc
    private void showAlert(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(R.string.general_ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //If we want to do something when user dismisses dialog
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
    //endregion
}
