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

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import dk.mustache.beaconbacon.BBApplication;
import dk.mustache.beaconbacon.R;
import dk.mustache.beaconbacon.api.ApiManager;
import dk.mustache.beaconbacon.api.FindTheBookAsync;
import dk.mustache.beaconbacon.api.GetAllPlacesAsync;
import dk.mustache.beaconbacon.api.GetFloorImageAsync;
import dk.mustache.beaconbacon.api.GetIconImageAsync;
import dk.mustache.beaconbacon.api.GetMenuOverviewAsync;
import dk.mustache.beaconbacon.api.GetSpecificPlaceAsync;
import dk.mustache.beaconbacon.customviews.CustomPoiView;
import dk.mustache.beaconbacon.customviews.CustomSnackbar;
import dk.mustache.beaconbacon.customviews.MapHolderView;
import dk.mustache.beaconbacon.customviews.PoiHolderView;
import dk.mustache.beaconbacon.data.BeaconBaconManager;
import dk.mustache.beaconbacon.datamodels.BBAllPlaces;
import dk.mustache.beaconbacon.datamodels.BBFaustDataObject;
import dk.mustache.beaconbacon.datamodels.BBFloor;
import dk.mustache.beaconbacon.datamodels.BBPlace;
import dk.mustache.beaconbacon.datamodels.BBPoi;
import dk.mustache.beaconbacon.datamodels.BBPoiMenuItem;
import dk.mustache.beaconbacon.datamodels.BBResponseObject;
import dk.mustache.beaconbacon.enums.DisplayType;
import dk.mustache.beaconbacon.fragments.FindTheBookFragment;
import dk.mustache.beaconbacon.fragments.PlaceSelectionFragment;
import dk.mustache.beaconbacon.fragments.PoiSelectionFragment;
import dk.mustache.beaconbacon.interfaces.AllPlacesAsyncResponse;
import dk.mustache.beaconbacon.interfaces.FindTheBookAsyncResponse;
import dk.mustache.beaconbacon.interfaces.FloorImageAsyncResponse;
import dk.mustache.beaconbacon.interfaces.IconImageAsyncResponse;
import dk.mustache.beaconbacon.interfaces.MenuOverviewAsyncResponse;
import dk.mustache.beaconbacon.interfaces.SpecificPlaceAsyncResponse;

import static android.graphics.Typeface.BOLD;
import static dk.mustache.beaconbacon.BBApplication.FAUST_ID;
import static dk.mustache.beaconbacon.BBApplication.PLACE_ID;
import static dk.mustache.beaconbacon.BBApplication.PLACE_SELECTION_FRAGMENT;
import static dk.mustache.beaconbacon.BBApplication.POI_SELECTION_FRAGMENT;

public class BeaconBaconActivity extends AppCompatActivity implements View.OnClickListener,
                                                                        SpecificPlaceAsyncResponse,
                                                                        MenuOverviewAsyncResponse,
                                                                        FindTheBookAsyncResponse,
                                                                        FloorImageAsyncResponse,
                                                                        IconImageAsyncResponse,
                                                                        AllPlacesAsyncResponse {

    //TODO This could possibly be done a little bit smarter
    public static int boxHeight = -1;

    //RootView is used to show the Snackbar
    private FrameLayout rootView;
    public CustomSnackbar snackbar;

    //ProgressBar indicating new loading of map
    private ProgressBar progressBar;

    //Toolbar elements
    private Toolbar toolbar;
    private TextView toolbarTitle;
    private TextView toolbarSubtitle;
    private ImageView arrowLeft;
    private ImageView arrowRight;

    //FABs
    public FloatingActionButton fabPoi;
    public FloatingActionButton fabFindTheBook;

    //Fragments
    private PoiSelectionFragment poiSelectionFragment;
    private PlaceSelectionFragment placeSelectionFragment;

    //Views
    private Bitmap currentFloorImage;
    private List<CustomPoiView> customPOIViewsList;
    private FrameLayout mapView;
    private MapHolderView mapHolderView;
    private PoiHolderView poiHolderView;

    //List of selected POIs for use in the POI Selection Fragment
    private List<BBPoi> selectedPois;

    //Booleans for AsyncTasks to update map synchronously
    private boolean isFindingBook;
    private boolean isFindingFloorImage;
    private boolean isFindingPoiIcons;
    private boolean bookWasFound;
    private boolean updateFindTheBook = true;
    private boolean isLocatingFindTheBookFloor;
    private boolean readyToUpdateFindTheBook;


    //region Android Lifecycle
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Set Layout and Root views
        setContentView(R.layout.activity_map);
        rootView = findViewById(R.id.root_view);
        mapView = findViewById(R.id.map_view_container);

        setupProgressBar();
        progressBar.setVisibility(View.VISIBLE);

        setupCustomViews();
        setupToolbar();
        setupFloatingActionButtons();

        init();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BeaconBaconManager.getInstance().setRequestObject(null);
        BeaconBaconManager.getInstance().setCurrentPlace(null);
        BeaconBaconManager.getInstance().setCurrentFloorIndex(-1);
        BeaconBaconManager.getInstance().setCurrentFloorId(-1);
    }

    @Override
    public void onBackPressed() {
        if(getSupportFragmentManager().getBackStackEntryCount() > 0) {
            Log.d("Backstack entry 0 name", getSupportFragmentManager().getBackStackEntryAt(0).getName());

            if (Objects.equals(getSupportFragmentManager().getBackStackEntryAt(0).getName(), PLACE_SELECTION_FRAGMENT))
                closePlaceSelectionFragment();
            else if (Objects.equals(getSupportFragmentManager().getBackStackEntryAt(0).getName(), POI_SELECTION_FRAGMENT))
                closePoiSelectionFragment(poiSelectionFragment.selectedPois);
        } else {
            super.onBackPressed();
        }

    }

    //endregion



    /**
     * This region sets up the general UI
     */
    //region Setup
    private void setupProgressBar() {
        //Setup Progress bar
        progressBar = findViewById(R.id.map_progress_bar);
        progressBar.setVisibility(View.VISIBLE);
    }

    private void setupCustomViews() {
        //Initialize custom views to Hold the Map and POIs
        poiHolderView = new PoiHolderView(this);

        mapHolderView = new MapHolderView(this);
        mapHolderView.poiHolderView = poiHolderView;

        mapView.addView(mapHolderView);
        mapView.addView(poiHolderView);
    }

    private void setupToolbar() {
        toolbar = findViewById(R.id.bb_toolbar_regular);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) getSupportActionBar().setDisplayShowTitleEnabled(false);

        LinearLayout toolbarTitleLayout = findViewById(R.id.bb_toolbar_title_layout);
        toolbarTitleLayout.setOnClickListener(this);

        toolbarTitle = findViewById(R.id.bb_toolbar_title);
        toolbarSubtitle = findViewById(R.id.bb_toolbar_subtitle);
        if (BeaconBaconManager.getInstance().getConfigurationObject() != null && BeaconBaconManager.getInstance().getConfigurationObject().getTypeface() != null) {
            toolbarTitle.setTypeface(BeaconBaconManager.getInstance().getConfigurationObject().getTypeface(), BOLD);
            toolbarSubtitle.setTypeface(BeaconBaconManager.getInstance().getConfigurationObject().getTypeface());
        }

        arrowLeft = findViewById(R.id.bb_toolbar_arrow_left);
        arrowRight = findViewById(R.id.bb_toolbar_arrow_right);
        arrowLeft.setOnClickListener(this);
        arrowRight.setOnClickListener(this);
    }

    private void setupFloatingActionButtons() {
        //FABs
        fabPoi = findViewById(R.id.map_poi_fab);
        fabPoi.setOnClickListener(this);
        fabPoi.setColorFilter(new PorterDuffColorFilter(getResources().getColor(R.color.colorAccent), PorterDuff.Mode.SRC_ATOP));

        fabFindTheBook = findViewById(R.id.map_ftb_fab);
        fabFindTheBook.setOnClickListener(this);
        fabFindTheBook.setColorFilter(new PorterDuffColorFilter(getResources().getColor(R.color.colorAccent), PorterDuff.Mode.SRC_ATOP));


        if (BeaconBaconManager.getInstance().getRequestObject() != null && BeaconBaconManager.getInstance().getRequestObject().getImage() != null) {
            Bitmap original = BeaconBaconManager.getInstance().getRequestObject().getImage();
            // Bitmap resized = Bitmap.createScaledBitmap(original, 100, 100, true);
            fabFindTheBook.setImageBitmap(original);
        }
    }
    //endregion



    /**
     * This method initializes the BeaconBaconActivity based on the input provided through the Intent.
     * Input can be either nothing, a place_id, or a place_id and faust_id.
     */
    private void init() {
        //Ensure we have all places
        if(BeaconBaconManager.getInstance().getAllPlaces() != null) {
            //Get place_id and faust_id if any
            String place_id = getIntent().getStringExtra(PLACE_ID);
            String faust_id = getIntent().getStringExtra(FAUST_ID);

            if (place_id != null) {
                //Loop all places to find this one and set toolbar titles
                for (int i = 0; i < BeaconBaconManager.getInstance().getAllPlaces().getData().size(); i++) {
                    if (Objects.equals(String.valueOf(BeaconBaconManager.getInstance().getAllPlaces().getData().get(i).getId()), place_id)) {
                        //We don't know the floor name until we've fetched a Specific Place, so we set the place name only initially
                        toolbarSubtitle.setText(BeaconBaconManager.getInstance().getAllPlaces().getData().get(i).getName());
                        break;
                    }
                }

                if (faust_id != null) {
                    //We have place_id and a faust_id
                    if (BeaconBaconManager.getInstance().getRequestObject() != null) {
                        Log.i("BeaconBaconActivity", "Faust id provided, finding the book for the user.");
                        findABook(place_id);
                    } else {
                        Log.e("BeaconBaconActivity", "Faust id provided, but no Request Object was set. Create a new BBRequestObject and set it to the BeaconBaconManager before opening the BeaconBaconActivity.");
                    }

                    Log.i("BeaconBaconActivity", "Place id was provided, finding the place for the user.");
                    findSpecificPlace(place_id, true);
                } else {
                    Log.i("BeaconBaconActivity", "No Faust id was provided, proceeding with Place id only.");
                    Log.i("BeaconBaconActivity", "Place id was provided, finding the place for the user.");

                    //We have a place id only
                    findSpecificPlace(place_id, false);
                }

            } else {
                Log.i("BeaconBaconActivity", "No place id was provided, sending user to place selection.");

                progressBar.setVisibility(View.GONE);

                //We have no place id, send user to place selection
                openPlaceSelectionFragment();
            }

        } else {
            //Call to get all places has not been made before opening BeaconBaconActivity, display error in console
            Log.e("BeaconBaconActivity", "Faust id provided, but no Request Object was set. Create a new BBRequestObject and set it to the BeaconBaconManager before opening the BeaconBaconActivity.");

            //Fetch all places
            GetAllPlacesAsync getAllPlacesAsync = new GetAllPlacesAsync();

            ApiManager.createInstance(BBApplication.getContext());

            getAllPlacesAsync.delegate = this;
            ApiManager.getInstance().fetchAllPlacesAsync(getAllPlacesAsync);
        }
    }



    /**
     * This region handles setting up menus and handles clicks on menu items and other UI elements
     */
    //region Menus & Clicks
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_close) {
            if (getSupportFragmentManager().getBackStackEntryCount() != 0) {
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
        //Cannot build library with switch statements, converted to if-else instead
        int viewId = view.getId();
        if (viewId == R.id.bb_toolbar_title_layout) {
            hideGuiElements();
            placeSelectionFragment = new PlaceSelectionFragment();
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_bottom,
                                        R.anim.slide_out_bottom,
                                        R.anim.slide_in_bottom,
                                        R.anim.slide_out_bottom)
                    .replace(R.id.fragment_container, placeSelectionFragment, PLACE_SELECTION_FRAGMENT)
                    .addToBackStack(PLACE_SELECTION_FRAGMENT)
                    .commit();

        } else if (viewId == R.id.map_poi_fab) {
            hideGuiElements();
            poiSelectionFragment = new PoiSelectionFragment();
            poiSelectionFragment.selectedPois = selectedPois;
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_bottom,
                            R.anim.slide_out_bottom,
                            R.anim.slide_in_bottom,
                            R.anim.slide_out_bottom)
                    .replace(R.id.fragment_container, poiSelectionFragment, POI_SELECTION_FRAGMENT)
                    .addToBackStack(POI_SELECTION_FRAGMENT)
                    .commit();

        } else if (viewId == R.id.map_ftb_fab) {
            for (int j = 0; j < BeaconBaconManager.getInstance().getCurrentPlace().getFloors().size(); j++) {
                if (BeaconBaconManager.getInstance().getResponseObject().getData().get(0).getFloor().getId() == BeaconBaconManager.getInstance().getCurrentPlace().getFloors().get(j).getId()) {
                    updateCurrentFloor(j, BeaconBaconManager.getInstance().getResponseObject().getData().get(0).getFloor().getId());
                    mapHolderView.scrollToBook();
                    break;
                }
            }

        } else if (viewId == R.id.bb_toolbar_arrow_left) {
            updateArrows(-1);

        } else if (viewId == R.id.bb_toolbar_arrow_right) {
            updateArrows(1);
        }
    }
    //endregion



    //region Find The Book
    /**
     * Initiates an AsyncTask to Find the book
     */
    public void findABook(String place_id) {
        isFindingBook = true;

        //A Request object was set, lets find the book
        FindTheBookAsync findTheBookAsync = new FindTheBookAsync();
        findTheBookAsync.delegate = this;
        ApiManager.getInstance().findTheBookAsync(findTheBookAsync, place_id);
    }

    /**
     * Hides all UI Elements (Snackbar and Fab) related to FindTheBook, and resets FindTheBook variables
     */
    private void hideFindTheBookElements() {
        fabFindTheBook.setVisibility(View.GONE);
        if (snackbar != null) {
            snackbar.getView().setVisibility(View.GONE);
            snackbar.dismiss();
        }

        BeaconBaconManager.getInstance().setRequestObject(null);
        mapHolderView.setFindTheBook(null, null);
        mapHolderView.findTheBookAreaObject = null;
    }

    /**
     * Handles the initialization of a custom Snackbar for FindTheBook
     */
    @SuppressLint("Range")
    private void showFindTheBookSnackbar() {
        snackbar = CustomSnackbar.make(rootView, CustomSnackbar.LENGTH_INDEFINITE);

        final Snackbar.SnackbarLayout snackbarView = (Snackbar.SnackbarLayout) snackbar.getView();
        if (BeaconBaconManager.getInstance().getConfigurationObject() != null && BeaconBaconManager.getInstance().getConfigurationObject().getTintColor() != -1)
            snackbarView.setBackgroundColor(getResources().getColor(BeaconBaconManager.getInstance().getConfigurationObject().getTintColor()));

        //Modify LayoutParams for Top-snackbar
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) snackbarView.getLayoutParams();
        params.gravity = Gravity.TOP;

        //Calculate Toolbar height
        TypedValue tv = new TypedValue();
        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true))
            params.topMargin = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());

        snackbarView.setLayoutParams(params);
        snackbar.setAction(getResources().getString(R.string.general_finish), new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BeaconBaconManager.getInstance().setRequestObject(null);
                mapHolderView.poiHolderView.findTheBookObject = null;
                mapHolderView.findTheBookAreaObject = null;
                updatePlace(BeaconBaconManager.getInstance().getCurrentPlace());
                hideFindTheBookElements();
            }
        });
        snackbar.show();
    }
    //endregion



    //region Place
    /**
     * Handles the initialization of a PlaceSelectionFragment
     */
    private void openPlaceSelectionFragment() {
        if (BeaconBaconManager.getInstance().getAllPlaces() != null && BeaconBaconManager.getInstance().getAllPlaces().getData() != null) {
            //We haven't gotten a place, let's prompt the user to select one
            hideGuiElements();
            progressBar.setVisibility(View.GONE);

            placeSelectionFragment = new PlaceSelectionFragment();
            getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.hold_anim, R.anim.slide_out_bottom, R.anim.hold_anim, R.anim.slide_out_bottom).replace(R.id.fragment_container, placeSelectionFragment, PLACE_SELECTION_FRAGMENT).addToBackStack(null).commit();

        } else {
            //Call to get all places has not been made display error in console
            Log.e("BeaconBaconActivity", "Faust id provided, but no Request Object was set. Create a new BBRequestObject and set it to the BeaconBaconManager before opening the BeaconBaconActivity.");

            //Fetch all places
            GetAllPlacesAsync getAllPlacesAsync = new GetAllPlacesAsync();

            ApiManager.createInstance(BBApplication.getContext());

            getAllPlacesAsync.delegate = this;
            ApiManager.getInstance().fetchAllPlacesAsync(getAllPlacesAsync);
        }
    }

    /**
     * Initiates an AsyncTask to find a specific place
     */
    private void findSpecificPlace(String place_id, boolean awaitFindBook) {
        isFindingBook = awaitFindBook;

        //Initiate Fetch Specific Place
        GetSpecificPlaceAsync getSpecificPlaceAsync = new GetSpecificPlaceAsync();
        getSpecificPlaceAsync.delegate = this;
        ApiManager.getInstance().fetchSpecificPlaceAsync(getSpecificPlaceAsync, place_id);
    }
    //endregion



    /**
     * This region handles actual updates to the UI
     */
    //region Update Content
    private void updateToolbar() {
        toolbarSubtitle.setText(BeaconBaconManager.getInstance().getCurrentPlace().getName());
        if (BeaconBaconManager.getInstance().getCurrentPlace().getFloors() != null && BeaconBaconManager.getInstance().getCurrentPlace().getFloors().size() > 0)
            toolbarTitle.setText(BeaconBaconManager.getInstance().getCurrentPlace().getFloors().get(0).getName());

        updateArrows(0);
    }

    private void updateToolbarTitle(BBPlace newCurrentPlace) {
        toolbarSubtitle.setText(newCurrentPlace.getName());
        if (newCurrentPlace.getFloors() != null && newCurrentPlace.getFloors().size() != 0)
            toolbarTitle.setText(newCurrentPlace.getFloors().get(BeaconBaconManager.getInstance().getCurrentFloorIndex()).getName());
        else
            toolbarTitle.setText("-");

        updateArrows(0);
    }

    private void updateMapView() {
        if (isFindingFloorImage || isFindingPoiIcons) {
            Log.i("BeaconBaconActivity", "We're still locating a Floor image or POI icons for this Place.");
            return;
        }

        try {
            mapHolderView.animate().alpha(0).withEndAction(new Runnable() {
                @Override
                public void run() {
                    final Handler handler = new Handler();
                    final Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            if (!isFindingBook && BeaconBaconManager.getInstance().getRequestObject() != null && !isFindingFloorImage) {
                                if (updateFindTheBook) {
                                    readyToUpdateFindTheBook = true;
                                    isLocatingFindTheBookFloor = true;

                                    for (int i = 0; i < BeaconBaconManager.getInstance().getCurrentPlace().getFloors().size(); i++) {
                                        if (BeaconBaconManager.getInstance().getResponseObject().getData().get(0).getFloor().getId() == BeaconBaconManager.getInstance().getCurrentPlace().getFloors().get(i).getId()) {
                                            updateCurrentFloor(i, BeaconBaconManager.getInstance().getResponseObject().getData().get(0).getFloor().getId());

                                            updateFindTheBook = false;

                                            //Return to avoid double update of MapHolderView
                                            return;
                                        }
                                    }
                                }
                            }

                            Log.i("BeaconBaconActivity", "Updating map layout.");

                            if (!isFindingFloorImage && !isFindingPoiIcons && !isFindingBook && !isLocatingFindTheBookFloor) {
                                progressBar.setVisibility(View.GONE);
                                fabPoi.setVisibility(View.VISIBLE);

                                if (BeaconBaconManager.getInstance().getRequestObject() != null && bookWasFound)
                                    fabFindTheBook.setVisibility(View.VISIBLE);

                                mapHolderView.invalidate();
                                mapHolderView.clearAnimation();
                                mapHolderView.animate().alpha(1).setDuration(200).start();



                                if (mapHolderView.poiHolderView != null) {
                                    mapHolderView.poiHolderView.floorWasSwitched();
                                }

                                int currentFloorIdx = BeaconBaconManager.getInstance().getCurrentFloorIndex();
                                BBFloor currentFloor = BeaconBaconManager.getInstance().getCurrentPlace().getFloors().get(currentFloorIdx);
                                mapView.setBackgroundColor(Color.parseColor(currentFloor.getMap_background_color()));

                                mapHolderView.setImageBitmap(currentFloorImage);

                                mapHolderView.setMapPois(customPOIViewsList);

                                if (readyToUpdateFindTheBook) {
                                    showFindTheBookSnackbar();

                                    //Should we display FTB Info?
                                    SharedPreferences sharedPref = getSharedPreferences("BeaconBacon_Preferences", Context.MODE_PRIVATE);
                                    if (!sharedPref.getBoolean("ftb_onboarding_info", false)) {
                                        FindTheBookFragment findTheBookFragment = new FindTheBookFragment();
                                        FragmentTransaction ft2 = getSupportFragmentManager().beginTransaction();
                                        ft2.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out);
                                        ft2.add(R.id.fragment_container, findTheBookFragment);
                                        ft2.addToBackStack(null);
                                        ft2.commit();
                                    }

                                    readyToUpdateFindTheBook = false;
                                }

                                if (!isLocatingFindTheBookFloor) {
                                    if (BeaconBaconManager.getInstance().getRequestObject() != null) {
                                        mapHolderView.setFindTheBook(BeaconBaconManager.getInstance().getRequestObject().getImage(), BeaconBaconManager.getInstance().getResponseObject());
                                    }

                                    updateToolbarTitle(BeaconBaconManager.getInstance().getCurrentPlace());
                                }

                            } else {
                                Log.i("BeaconBaconActivity", "We're still finding a map image for this floor or setting up POI icons, retrying in 100ms");
                                handler.postDelayed(this, 100);
                            }
                        }
                    };
                    handler.postDelayed(runnable, 0);
                }
            }).setDuration(200).start();

        } catch (Exception e) {
            Log.e("BBActivity - ErrorMap", e.getLocalizedMessage());
        }
    }

    /**
     * Get the menu overview for the provided place
     *
     * @param place BBPlace for which to find Menu Overview
     */
    private void updateMenuOverview(BBPlace place) {
        GetMenuOverviewAsync getMenuOverviewAsync = new GetMenuOverviewAsync();
        getMenuOverviewAsync.delegate = this;
        ApiManager.getInstance().fetchMenuOverviewAsync(getMenuOverviewAsync, String.valueOf(place.getId()));
    }

    /**
     * Update Current Place
     *
     * @param newCurrentPlace BBPlace to set as current
     */
    private void updateCurrentPlace(BBPlace newCurrentPlace) {
        progressBar.setVisibility(View.VISIBLE);
        BeaconBaconManager.getInstance().setCurrentPlace(newCurrentPlace);
    }

    /**
     * Update Place
     *
     * @param place BBPlace to update
     */
    private void updatePlace(BBPlace place) {
        if(selectedPois != null)
            selectedPois.clear();
        
        //Loop all places to find this one
        for (int i = 0; i < BeaconBaconManager.getInstance().getAllPlaces().getData().size(); i++) {
            if (BeaconBaconManager.getInstance().getAllPlaces().getData().get(i).getId() == place.getId()) {

                //Sort the Place's floors by Order
                Collections.sort(place.getFloors(), new Comparator<BBFloor>() {
                    @Override
                    public int compare(BBFloor floor1, BBFloor floor2) {
                        return floor1.getOrder() - floor2.getOrder();
                    }
                });

                //Replace existing place with the fetched place
                BeaconBaconManager.getInstance().getAllPlaces().getData().set(i, place);

                //Set current Place and floor
                updateCurrentPlace(place);
                updateCurrentFloor(0, BeaconBaconManager.getInstance().getCurrentPlace().getFloors().get(0).getId());
                updateToolbar();

                if (place.getFloors() != null && place.getFloors().size() > 0 && !Objects.equals(place.getFloors().get(0).getImage(), "")) {
                    //Moved method
                } else {
                    mapHolderView.setMapPois(null);
                    mapHolderView.setFindTheBook(null, null);
                    mapHolderView.setImageBitmap(null);
                    progressBar.setVisibility(View.GONE);
                    showAlert(getString(R.string.alert_title_general), getString(R.string.alert_message_library));
                }
            }
        }
    }

    /**
     * Update Floor
     *
     * @param newCurrentFloorIndex new floor index
     * @param newCurrentFloorId new floor id
     */
    private void updateCurrentFloor(int newCurrentFloorIndex, int newCurrentFloorId) {
        progressBar.setVisibility(View.VISIBLE);

        BeaconBaconManager.getInstance().setCurrentFloorIndex(newCurrentFloorIndex);
        BeaconBaconManager.getInstance().setCurrentFloorId(newCurrentFloorId);
        BBPlace currentPlace = BeaconBaconManager.getInstance().getCurrentPlace();

        if (currentPlace.getFloors() != null && currentPlace.getFloors().size() > 0 && !Objects.equals(currentPlace.getFloors().get(newCurrentFloorIndex).getImage(), "")) {
            // Set BEFORE Invoking Async Tasks.
            isFindingFloorImage = true;
            isFindingPoiIcons = true;

            GetFloorImageAsync getFloorImageAsync = new GetFloorImageAsync();
            getFloorImageAsync.delegate = this;
            ApiManager.getInstance().getFloorImage(getFloorImageAsync);

            GetIconImageAsync getIconImageAsync = new GetIconImageAsync();
            getIconImageAsync.delegate = this;
            ApiManager.getInstance().getIconImage(this, getIconImageAsync, selectedPois);

        } else {
            mapHolderView.setMapPois(null);
            mapHolderView.setFindTheBook(null, null);
            mapHolderView.setImageBitmap(null);
        }
    }

    /**
     * Updates the arrows in the Toolbar
     *
     * @param direction which the user pressed (1 for right, -1 for left)
     */
    private void updateArrows(int direction) {
        if (BeaconBaconManager.getInstance().getCurrentPlace() != null && BeaconBaconManager.getInstance().getCurrentPlace().getFloors() != null && BeaconBaconManager.getInstance().getCurrentPlace().getFloors().size() != 0) {

            int floorListSize = BeaconBaconManager.getInstance().getCurrentPlace().getFloors().size();
            int currentFloor = BeaconBaconManager.getInstance().getCurrentFloorIndex();

            //Change the floor
            if (direction == 1 && currentFloor != floorListSize - 1) {
                //Update text
                toolbarSubtitle.setText(BeaconBaconManager.getInstance().getCurrentPlace().getName());
                toolbarTitle.setText(BeaconBaconManager.getInstance().getCurrentPlace().getFloors().get(currentFloor + direction).getName());

                updateCurrentFloor(currentFloor + direction, BeaconBaconManager.getInstance().getCurrentPlace().getFloors().get(currentFloor + direction).getId());
            } else if (direction == -1 && currentFloor != 0) {
                //Update text
                toolbarSubtitle.setText(BeaconBaconManager.getInstance().getCurrentPlace().getName());
                toolbarTitle.setText(BeaconBaconManager.getInstance().getCurrentPlace().getFloors().get(currentFloor + direction).getName());

                updateCurrentFloor(currentFloor + direction, BeaconBaconManager.getInstance().getCurrentPlace().getFloors().get(currentFloor + direction).getId());
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
    //endregion



    /**
     * This region handles implementation of Interfaces exposed to the Async Tasks
     */
    //region Async Tasks Finished
    //region Images
    @Override
    public void floorImageAsyncFinished(Bitmap bitmap) {
        isFindingFloorImage = false;

        currentFloorImage = bitmap;

        if (!isFindingBook) {
            isLocatingFindTheBookFloor = false;
        }

        if(!isFindingPoiIcons)
            updateMapView();
    }

    @Override
    public void iconImageAsyncFinished(List<CustomPoiView> customPoiViews) {
        isFindingPoiIcons = false;

        customPOIViewsList = customPoiViews;

        if(!isFindingFloorImage)
            updateMapView();
    }
    //endregion

    @Override
    public void specificPlaceAsyncFinished(final JsonObject output) {
        Log.i("BeaconBaconActivity", "Place found, updating layout.");

        if (output != null) {
            final Handler handler = new Handler();
            final Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    if (!isFindingBook) {
                        //Get the Place from JsonObject output
                        JsonElement mJson = new JsonParser().parse(output.toString());
                        BBPlace place = new Gson().fromJson(mJson, BBPlace.class);

                        updatePlace(place);
                        updateMenuOverview(place);

                        if (BeaconBaconManager.getInstance().getRequestObject() != null && !bookWasFound) {
                            showAlert(getString(R.string.alert_title_find_book), String.format(getString(R.string.alert_message_find_book), BeaconBaconManager.getInstance().getRequestObject().getTitle(), BeaconBaconManager.getInstance().getCurrentPlace().getName()));
                            hideFindTheBookElements();
                        }

                    } else {
                        Log.i("BeaconBaconActivity", "We're currently locating an item for this place, retrying in 100ms");
                        handler.postDelayed(this, 100);
                    }
                }
            };
            handler.postDelayed(runnable, 0);
        } else {
            //No such place found, open place selection
            openPlaceSelectionFragment();
        }
    }

    @Override
    public void findTheBookAsyncFinished(JsonObject output) {
        if (output != null) {
            JsonElement mJson = new JsonParser().parse(output.toString());
            try {
                BBResponseObject responseObject = new Gson().fromJson(mJson, BBResponseObject.class);

                if (responseObject != null && responseObject.getStatus().equals("Found")) {
                    bookWasFound = true;

                    int maxDistLocations = 200;

                    if (responseObject.getData() == null || responseObject.getData().size() == 0) {
                        responseObject.setDisplayType(DisplayType.NONE);
                    } else if (responseObject.getData().size() == 1) {
                        responseObject.setDisplayType(DisplayType.SINGLE);
                    } else {
                        BBFaustDataObject dataObject1 = responseObject.getData().get(0);
                        BBFaustDataObject dataObject2 = responseObject.getData().get(1);

                        if (dataObject1.getFloor().getId() != dataObject2.getFloor().getId()) {
                            responseObject.setDisplayType(DisplayType.SINGLE);
                            BeaconBaconManager.getInstance().setResponseObject(responseObject);
                            return;
                        } else {
                            Point point1 = new Point(dataObject1.getLocation().getPosX(), dataObject1.getLocation().getPosY());
                            Point point2 = new Point(dataObject2.getLocation().getPosX(), dataObject2.getLocation().getPosY());

                            double distance = Math.sqrt(Math.pow(point2.x - point1.x, 2) + Math.pow(point2.y - point1.y, 2)) * Double.valueOf(responseObject.getData().get(0).getFloor().getMap_pixel_to_centimeter_ratio());

                            if (distance > 400) {
                                responseObject.setDisplayType(DisplayType.SINGLE);
                                BeaconBaconManager.getInstance().setResponseObject(responseObject);
                                return;
                            } else {
                                responseObject.setDisplayType(DisplayType.CLUSTER);
                                responseObject.setRadius((int) Math.max(maxDistLocations, distance) + 100);
                            }
                        }
                    }

                    BeaconBaconManager.getInstance().setResponseObject(responseObject);
                } else {
                    bookWasFound = false;
                    hideFindTheBookElements();
                }

            } catch (Exception e) {
                bookWasFound = false;
            }

        } else {
            bookWasFound = false;
        }

        isFindingBook = false;
    }

    @Override
    public void menuOverviewAsyncFinished(Bundle output) {
        int placeId = Integer.valueOf(output.getString("place_id"));

        Gson gson = new Gson();
        String jsonOutput = output.getString("json");
        Type listType = new TypeToken<List<BBPoiMenuItem>>() {
        }.getType();
        List<BBPoiMenuItem> menuItems = gson.fromJson(jsonOutput, listType);

        //Sort the Place's floors by Order
        if (menuItems != null) {
            Collections.sort(menuItems, new Comparator<BBPoiMenuItem>() {
                @Override
                public int compare(BBPoiMenuItem order1, BBPoiMenuItem order2) {
                    return order1.getOrder() - order2.getOrder();
                }
            });
        }

        //Find the place and update the POI Menu items
        for (int i = 0; i < BeaconBaconManager.getInstance().getAllPlaces().getData().size(); i++) {
            if (placeId == BeaconBaconManager.getInstance().getAllPlaces().getData().get(i).getId()) {
                BeaconBaconManager.getInstance().getAllPlaces().getData().get(i).setPoiMenuItem(menuItems);
            }
        }
    }

    @Override
    public void allPlacesAsyncFinished(JsonObject output) {
        Log.i(BBApplication.TAG, "All places fetched");

        //Map JsonObject output to the BBAllPlaces class
        JsonElement mJson =  new JsonParser().parse(output.toString());
        BBAllPlaces allPlaces = new Gson().fromJson(mJson, BBAllPlaces.class);

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
    //endregion



    /**
     * This region handles updates to the Activity received from Fragments (Set new Place, Set selected POIs)
     */
    //region Updates from Fragments
    public void setSelectedPois(List<BBPoi> selectedPois) {
        this.selectedPois = selectedPois;

        isFindingPoiIcons = true;
        GetIconImageAsync getIconImageAsync = new GetIconImageAsync();
        getIconImageAsync.delegate = this;
        ApiManager.getInstance().getIconImage(this, getIconImageAsync, selectedPois);
    }

    public void setNewCurrentPlace(BBPlace newCurrentPlace) {
        updateToolbarTitle(newCurrentPlace);

        progressBar.setVisibility(View.VISIBLE);

        getSupportFragmentManager().popBackStack();

        //Initiate Fetch Specific Place
        GetSpecificPlaceAsync getSpecificPlaceAsync = new GetSpecificPlaceAsync();
        getSpecificPlaceAsync.delegate = this;
        ApiManager.getInstance().fetchSpecificPlaceAsync(getSpecificPlaceAsync, String.valueOf(newCurrentPlace.getId()));
    }

    public void closePlaceSelectionFragment() {
        fabPoi.show();

        if(BeaconBaconManager.getInstance().getRequestObject() != null)
            fabFindTheBook.show();

        if(snackbar != null) {
            snackbar.getView().setVisibility(View.VISIBLE);
            snackbar.getView().animate()
                    .alpha(1)
                    .setDuration(300)
                    .start();
        }

        //If we have no place, just finish
        if(BeaconBaconManager.getInstance().getCurrentPlace() == null)
            finish();
        else
            getSupportFragmentManager().popBackStack();
    }

    public void closePoiSelectionFragment(List<BBPoi> selectedPois) {
        setSelectedPois(selectedPois);
        fabPoi.show();

        if(BeaconBaconManager.getInstance().getRequestObject() != null)
            fabFindTheBook.show();

        if(snackbar != null) {
            snackbar.getView().setVisibility(View.VISIBLE);
            snackbar.getView().animate()
                    .alpha(1)
                    .setDuration(300)
                    .start();
        }
        getSupportFragmentManager().popBackStack();
    }
    //endregion



    /**
     * This region handles everything else, right now, only displaying alerts and hiding UI elements
     */
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

    private void hideGuiElements() {
        fabPoi.hide();
        fabFindTheBook.hide();
        if (snackbar != null) {
            snackbar.getView().animate().alpha(0).withEndAction(new Runnable() {
                @Override
                public void run() {
                    snackbar.getView().setVisibility(View.GONE);
                }
            }).setDuration(300).start();
        }
    }
    //endregion
}
