package dk.mustache.beaconbacon.activities;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Point;
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
import android.widget.FrameLayout;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import dk.mustache.beaconbacon.R;
import dk.mustache.beaconbacon.api.ApiManager;
import dk.mustache.beaconbacon.api.FindTheBookAsync;
import dk.mustache.beaconbacon.api.GetFloorImageAsync;
import dk.mustache.beaconbacon.api.GetIconImageAsync;
import dk.mustache.beaconbacon.api.GetMenuOverviewAsync;
import dk.mustache.beaconbacon.api.GetSpecificPlaceAsync;
import dk.mustache.beaconbacon.customviews.CustomMapView;
import dk.mustache.beaconbacon.customviews.CustomPoiView;
import dk.mustache.beaconbacon.data.DataManager;
import dk.mustache.beaconbacon.datamodels.BBFaustDataFloor;
import dk.mustache.beaconbacon.datamodels.BBFaustDataLocation;
import dk.mustache.beaconbacon.datamodels.BBFaustDataObject;
import dk.mustache.beaconbacon.datamodels.BBFloor;
import dk.mustache.beaconbacon.datamodels.BBPlace;
import dk.mustache.beaconbacon.datamodels.BBPoi;
import dk.mustache.beaconbacon.datamodels.BBPoiMenuItem;
import dk.mustache.beaconbacon.datamodels.BBResponseObject;
import dk.mustache.beaconbacon.enums.DisplayType;
import dk.mustache.beaconbacon.fragments.PlaceSelectionFragment;
import dk.mustache.beaconbacon.fragments.PoiSelectionFragment;
import dk.mustache.beaconbacon.interfaces.FindTheBookAsyncResponse;
import dk.mustache.beaconbacon.interfaces.FloorImageAsyncResponse;
import dk.mustache.beaconbacon.interfaces.IconImageAsyncResponse;
import dk.mustache.beaconbacon.interfaces.MenuOverviewAsyncResponse;
import dk.mustache.beaconbacon.interfaces.SpecificPlaceAsyncResponse;

import static dk.mustache.beaconbacon.BBApplication.FAUST_ID;
import static dk.mustache.beaconbacon.BBApplication.PLACE_ID;

public class MapActivity extends AppCompatActivity implements View.OnClickListener, SpecificPlaceAsyncResponse, MenuOverviewAsyncResponse, FindTheBookAsyncResponse, FloorImageAsyncResponse, IconImageAsyncResponse {
    public static final String TAG = "BeaconBacon";
    public static final String PLACE_SELECTION_FRAGMENT = "place_selection_fragment";
    public static final String POI_SELECTION_FRAGMENT = "poi_selection_fragment";

    private Toolbar toolbar;
    private ProgressBar progressBar;
    private TextView toolbarTitle;
    private TextView toolbarSubtitle;
    private ImageView arrowLeft;
    private ImageView arrowRight;

    public FloatingActionButton fabPoi;
    public FloatingActionButton fabFindTheBook;

    private PoiSelectionFragment poiSelectionFragment;
    private PlaceSelectionFragment placeSelectionFragment;

    private FrameLayout mapView;
    private CustomMapView customMapView;
    private List<BBPoi> selectedPois;

    private boolean isFindingBook;
    private boolean isFindingSpecificPlace;
    private boolean isFindingMenuForPlace;
    private boolean isFindingFloorImage;
    private boolean isFindingPoiIcons;

    private Bitmap currentFloorImage;


    //region Android Lifecycle
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Set Layout
        setContentView(R.layout.activity_map);
        mapView = findViewById(R.id.map_view_container);

        //Map View
        customMapView = new CustomMapView(this);
        customMapView.setAdjustViewBounds(true);
        customMapView.setFitsSystemWindows(true);
        mapView.addView(customMapView);

        progressBar = findViewById(R.id.map_progress_bar);
        progressBar.setVisibility(View.VISIBLE);

        setupToolbar();

        //FABs
        fabPoi = findViewById(R.id.map_poi_fab);
        fabPoi.setOnClickListener(this);
        fabFindTheBook = findViewById(R.id.map_ftb_fab);
        fabFindTheBook.setOnClickListener(this);


        //How was the MapActivity opened? (), (place_id) or (place_id, faust_id)
        String place_id = getIntent().getStringExtra(PLACE_ID);
        String faust_id = getIntent().getStringExtra(FAUST_ID);

        if(place_id != null) {
            progressBar.setVisibility(View.VISIBLE);

            if(faust_id != null) {
                //We have place_id and a faust_id
                if(DataManager.getInstance().getRequestObject() != null) {
                    fabFindTheBook.setVisibility(View.VISIBLE);
                    findABook(place_id, faust_id);
                } else {
                    Log.e("MapActivity", "Faust id provided, but no Request Object was set. Make a new BBRequestObject and set it to the DataManager.");
                }

                findSpecificPlace(place_id, true);
            } else {
                //We have a place id
                findSpecificPlace(place_id, false);
            }
        } else {
            //Open place selection
            openPlaceSelectionFragment();
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
            fabPoi.hide();
            fabFindTheBook.hide();
            placeSelectionFragment = new PlaceSelectionFragment();
            getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_bottom, R.anim.slide_out_bottom, R.anim.slide_in_bottom, R.anim.slide_out_bottom)
                    .replace(R.id.fragment_container, placeSelectionFragment, PLACE_SELECTION_FRAGMENT)
                    .addToBackStack(null)
                    .commit();

        } else if (i == R.id.map_poi_fab) {
            fabPoi.hide();
            fabFindTheBook.hide();
            poiSelectionFragment = new PoiSelectionFragment();
            getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_bottom, R.anim.slide_out_bottom, R.anim.slide_in_bottom, R.anim.slide_out_bottom)
                    .replace(R.id.fragment_container, poiSelectionFragment, POI_SELECTION_FRAGMENT)
                    .addToBackStack(null)
                    .commit();

        } else if (i == R.id.map_ftb_fab) {
            //TODO Scroll map to the Location of Book

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



    //region Place and Find The Book initialization
    private void openPlaceSelectionFragment() {
        final Handler handler = new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                //TODO Should remove this handler, AllPlaces must have been fetched before opening the library
                if(DataManager.getInstance().getAllPlaces() != null && DataManager.getInstance().getAllPlaces().getData() != null) {
                    //We haven't gotten a place, let's prompt the user to select one
                    fabPoi.hide();
                    fabFindTheBook.hide();
                    progressBar.setVisibility(View.GONE);

                    placeSelectionFragment = new PlaceSelectionFragment();
                    getSupportFragmentManager()
                            .beginTransaction()
                            .setCustomAnimations(R.anim.hold_anim, R.anim.slide_out_bottom, R.anim.hold_anim, R.anim.slide_out_bottom)
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

    private void findABook(String place_id, String faust_id) {
        isFindingBook = true;

        //A Request object was set, lets find the book
        FindTheBookAsync findTheBookAsync = new FindTheBookAsync();
        findTheBookAsync.delegate = this;
        ApiManager.getInstance().findTheBookAsync(findTheBookAsync, place_id);
    }

    private void findSpecificPlace(String place_id, boolean awaitFindBook) {
        isFindingSpecificPlace = true;
        isFindingBook = awaitFindBook;

        //Initiate Fetch Specific Place
        GetSpecificPlaceAsync getSpecificPlaceAsync = new GetSpecificPlaceAsync();
        getSpecificPlaceAsync.delegate = this;
        ApiManager.getInstance().fetchSpecificPlaceAsync(getSpecificPlaceAsync, place_id);
    }
    //endregion



    //region Async Tasks Finished
    @Override
    public void specificPlaceAsyncFinished(JsonObject output) {
        //Get the Place from JsonObject output
        JsonElement mJson = new JsonParser().parse(output.toString());
        BBPlace place = new Gson().fromJson(mJson, BBPlace.class);

        updatePlace(place);
        updateMenuOverview(place);
    }

    private void updateMenuOverview(BBPlace place) {
        isFindingMenuForPlace = true;

        //Get the menu overview right away
        GetMenuOverviewAsync getMenuOverviewAsync = new GetMenuOverviewAsync();
        getMenuOverviewAsync.delegate = this;
        ApiManager.createInstance(this).fetchMenuOverviewAsync(getMenuOverviewAsync, String.valueOf(place.getId()));
    }

    private void updatePlace(BBPlace place) {
        //Loop all places to find this one
        for (int i = 0; i < DataManager.getInstance().getAllPlaces().getData().size(); i++) {
            if (DataManager.getInstance().getAllPlaces().getData().get(i).getId() == place.getId()) {

                //Sort the Place's floors by Order
                Collections.sort(place.getFloors(), new Comparator<BBFloor>() {
                    @Override
                    public int compare(BBFloor floor1, BBFloor floor2) {
                        return floor1.getOrder() - floor2.getOrder();
                    }
                });

                //Replace existing place with the fetched place
                DataManager.getInstance().getAllPlaces().getData().set(i, place);

                //Set current Place and floor
                updateCurrentPlace(place);
                updateCurrentFloor(0);

                updateToolbar();

                if (place.getFloors() != null && place.getFloors().size() > 0 && !Objects.equals(place.getFloors().get(0).getImage(), "")) {
                    isFindingFloorImage = true;

                    GetFloorImageAsync getFloorImageAsync = new GetFloorImageAsync();
                    getFloorImageAsync.delegate = this;
                    ApiManager.getInstance().getFloorImage(getFloorImageAsync);
                } else {
                    customMapView.setPois(null);
                    customMapView.setFindTheBook(null, null);
                    customMapView.setImageBitmap(null);
                    progressBar.setVisibility(View.GONE);
                    showAlert(getString(R.string.alert_title), getString(R.string.alert_message_library));
                }
            }
        }
    }

    @Override
    public void floorImageAsyncFinished(Bitmap bitmap) {
        isFindingFloorImage = false;

        currentFloorImage = bitmap;

        updateMapView(true, false, null);
    }

    private void updateMapView(boolean updateFloor, boolean updatePois, List<CustomPoiView> pois) {
        customMapView
                .animate()
                .alpha(0)
                .setDuration(300)
                .start();

        if(updateFloor)
            customMapView.setImageBitmap(null);
        if(updatePois)
            customMapView.setPois(null);

        if(!isFindingFloorImage)
            customMapView.setImageBitmap(currentFloorImage);
        if(!isFindingPoiIcons)
            customMapView.setPois(pois);

        if(!isFindingFloorImage && !isFindingPoiIcons) {
            progressBar.setVisibility(View.GONE);

            customMapView.clearAnimation();
            customMapView.animate().alpha(1).setDuration(300).start();
            customMapView.invalidate();
        }
    }

    public void setSelectedPois(List<BBPoi> selectedPois) {
        this.selectedPois = selectedPois;

        isFindingPoiIcons = true;

        GetIconImageAsync getIconImageAsync = new GetIconImageAsync();
        getIconImageAsync.delegate = this;
        ApiManager.getInstance().getIconImage(getIconImageAsync, selectedPois);
    }

    @Override
    public void findTheBookAsyncFinished(JsonObject output) {
        JsonElement mJson = new JsonParser().parse(output.toString());
        BBResponseObject responseObject = new Gson().fromJson(mJson, BBResponseObject.class);

        //TODO Temp data
        List<BBFaustDataObject> list = new ArrayList<>();

        BBFaustDataObject tempDataObject = new BBFaustDataObject();

        BBFaustDataLocation tempDataLocation = new BBFaustDataLocation();
        tempDataLocation.setId(1620);
        tempDataLocation.setArea("");
        tempDataLocation.setType("point");
        tempDataLocation.setPosX(1132);
        tempDataLocation.setPosY(1258);

        BBFaustDataFloor tempDataFloor = new BBFaustDataFloor();
        tempDataFloor.setId(3);
        tempDataFloor.setMap_pixel_to_centimeter_ratio("0.4");

        tempDataObject.setFloor(tempDataFloor);
        tempDataObject.setLocation(tempDataLocation);



        BBFaustDataObject tempDataObject2 = new BBFaustDataObject();

        BBFaustDataLocation tempDataLocation2 = new BBFaustDataLocation();
        tempDataLocation2.setId(1620);
        tempDataLocation2.setArea("");
        tempDataLocation2.setType("point");
        tempDataLocation2.setPosX(1132);
        tempDataLocation2.setPosY(1258);

        BBFaustDataFloor tempDataFloor2 = new BBFaustDataFloor();
        tempDataFloor2.setId(3);
        tempDataFloor2.setMap_pixel_to_centimeter_ratio("0.4");

        tempDataObject2.setFloor(tempDataFloor2);
        tempDataObject2.setLocation(tempDataLocation2);



        list.add(tempDataObject);
        list.add(tempDataObject2);

        BBResponseObject bbResponseObject = responseObject;
        bbResponseObject.setData(list);
        //TODO end temp data

        int maxDistLocations = 200;

        if(bbResponseObject.getData() == null || bbResponseObject.getData().size() == 0) {
            bbResponseObject.setDisplayType(DisplayType.NONE);
        } else if(bbResponseObject.getData().size() == 1) {
            bbResponseObject.setDisplayType(DisplayType.SINGLE);
        } else {
            BBFaustDataObject dataObject1 = bbResponseObject.getData().get(0);
            BBFaustDataObject dataObject2 = bbResponseObject.getData().get(1);

            if (dataObject1.getFloor().getId() != dataObject2.getFloor().getId()) {
                bbResponseObject.setDisplayType(DisplayType.SINGLE);
                customMapView.setFindTheBook(DataManager.getInstance().getRequestObject().getImage(), bbResponseObject);
                return;
            } else {
                Point point1 = new Point(dataObject1.getLocation().getPosX(), dataObject1.getLocation().getPosY());
                Point point2 = new Point(dataObject2.getLocation().getPosX(), dataObject2.getLocation().getPosY());

                double distance = Math.sqrt(Math.pow(point2.x - point1.x, 2) + Math.pow(point2.y - point1.y, 2)) * Double.valueOf(bbResponseObject.getData().get(0).getFloor().getMap_pixel_to_centimeter_ratio());

                if (distance > 400) {
                    bbResponseObject.setDisplayType(DisplayType.SINGLE);
                    customMapView.setFindTheBook(DataManager.getInstance().getRequestObject().getImage(), bbResponseObject);
                    return;
                } else {
                    bbResponseObject.setDisplayType(DisplayType.CLUSTER);
                    bbResponseObject.setRadius((int) Math.max(maxDistLocations, distance) + 100);
                }
            }
        }
        /* Actual data
        if(responseObject.getData() == null || responseObject.getData().size() == 0) {
            responseObject.setDisplayType(DisplayType.NONE);
        } else if(responseObject.getData().size() == 1) {
            responseObject.setDisplayType(DisplayType.SINGLE);
        } else {
            BBFaustDataObject dataObject1 = responseObject.getData().get(0);
            BBFaustDataObject dataObject2 = responseObject.getData().get(1);

            if (dataObject1.getFloor().getId() != dataObject2.getFloor().getId()) {
                responseObject.setDisplayType(DisplayType.SINGLE);
                customMapView.setFindTheBook(DataManager.getInstance().getRequestObject().getImage(), responseObject);
                return;
            } else {
                Point point1 = new Point(dataObject1.getLocation().getPosX(), dataObject1.getLocation().getPosY());
                Point point2 = new Point(dataObject2.getLocation().getPosX(), dataObject2.getLocation().getPosY());

                double distance = Math.sqrt(Math.pow(point2.x - point1.x, 2) + Math.pow(point2.y - point1.y, 2)) * Double.valueOf(responseObject.getData().get(0).getFloor().getMap_pixel_to_centimeter_ratio());

                if (distance > 400) {
                    responseObject.setDisplayType(DisplayType.SINGLE);
                    customMapView.setFindTheBook(DataManager.getInstance().getRequestObject().getImage(), responseObject);
                    return;
                } else {
                    responseObject.setDisplayType(DisplayType.CLUSTER);
                    responseObject.setRadius((int) Math.max(maxDistLocations, distance) + 100);
                }
            }
        }
        */

        customMapView.setFindTheBook(DataManager.getInstance().getRequestObject().getImage(), responseObject);
    }

    @Override
    public void iconImageAsyncFinished(List<CustomPoiView> customPoiViews) {
        isFindingPoiIcons = false;
        updateMapView(false, true, customPoiViews);
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

        //Find the place and update the POI Menu items
        for(int i=0; i<DataManager.getInstance().getAllPlaces().getData().size(); i++) {
            if(placeId == DataManager.getInstance().getAllPlaces().getData().get(i).getId()) {
                DataManager.getInstance().getAllPlaces().getData().get(i).setPoiMenuItem(menuItems);
            }
        }
    }
    //endregion



    //region Update Content
    public void setNewCurrentPlace(BBPlace newCurrentPlace) {
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

            GetFloorImageAsync getFloorImageAsync = new GetFloorImageAsync();
            getFloorImageAsync.delegate = this;
            ApiManager.getInstance().getFloorImage(getFloorImageAsync);

            isFindingPoiIcons = true;

            GetIconImageAsync getIconImageAsync = new GetIconImageAsync();
            getIconImageAsync.delegate = this;
            ApiManager.getInstance().getIconImage(getIconImageAsync, selectedPois);
        } else {
            customMapView.setPois(null);
            customMapView.setFindTheBook(null, null);
            customMapView.setImageBitmap(null);
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
