# Beacon-Bacon Android
## Getting started
1. Setup your own Beacon-Bacon Server and get an API Key

   (Read how to setup Places / Points of Interest / Beacons etc. [here](https://github.com/nosuchagency/beacon-bacon))
2. Import the beacon-bacon-android library into your project
  * Using gradle, add the library to your application's dependency list `implementation 'com.github.mustachedk:android-beacon-bacon-lib:1.0.21'`
3. Follow the instructions below to start using Beacon-Bacon


## How to use
You can see an example of using the library in the [Beacon-Bacon Android Demo Project](https://github.com/mustachedk/android-beacon-bacon-demo)

### Configure API and UI
```Java
//Create an instance of the BeaconBaconManager with the context of the BBApplication
BeaconBaconManager.createInstance(BBApplication.getContext());

//Set a Configuration Object to the BeaconBaconManager
/* Example (Font must be placed in your assets folder under /assets/fonts/)
applyConfiguration("<font_name_here.ttf", R.color.<your_color_here>, baseUrl, apiKey);
*/

private void applyConfiguration(String fontName, int color, String baseUrl, String apiKey) {
    AssetManager assetManager = getApplicationContext().getAssets();
    Typeface typeface = Typeface.createFromAsset(assetManager, String.format(Locale.getDefault(), "fonts/%s", fontName));

    //Apply to a new Configuration Object
    BeaconBaconManager.getInstance().setConfigurationObject(new BBConfigurationObject(typeface, color, baseUrl, String.format("Bearer %s", apiKey)));

    initBeaconBacon();
}
```

### Initialize BeaconBacon
```Java
//When the BeaconBaconManager has been instantiated and a Configuration Object has been set, initialize BeaconBacon and make a call to GetAllPlacesAsync
private void initBeaconBacon() {
    //Instantiate our ApiManager
    ApiManager.createInstance(BBApplication.getContext());

    GetAllPlacesAsync getAllPlacesAsync = new GetAllPlacesAsync();
    //Get the basics of All Places right away
    getAllPlacesAsync.delegate = this;
    ApiManager.getInstance().fetchAllPlacesAsync(getAllPlacesAsync);
}

//Listen for the result by implementing the AllPlacesAsyncResponse Interface
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
```

### Initiate map without way finding
```Java
//PLACE_ID is a String included in the BBApplication and can be statically imported.
//placeId is a String with the id of the place. Use -1 as default to have user select the place from a list.
Intent intentNoWayfinding = new Intent(<your_activity_name_here>.this, BeaconBaconActivity.class);
intentNoWayfinding.putExtra(PLACE_ID, placeId);
startActivity(intentNoWayfinding);
overridePendingTransition(R.anim.slide_in_bottom, R.anim.hold_anim);`
```

### Initiate way finding with an IMS Request
```Java
//Place id used as explained above
//FAUST_ID is a String included in the BBApplication and can be statically imported.
//faustId is a String with the faust id of the item to be found.
//First, set a new RequestObject to the BeaconBaconManager
BeaconBaconManager.getInstance().setRequestObject(new BBRequestObject("IMS", <faust_id_as_string>, <title>, <subtitle>, <bitmap>);

//Then open the BeaconBaconActivity applying both placeId and faustId
Intent intentWayfinding = new Intent(<your_activity_name_here>.this, BeaconBaconActivity.class);
intentWayfinding.putExtra(PLACE_ID, placeId);
intentWayfinding.putExtra(FAUST_ID, faustId);
startActivity(intentWayfinding);
overridePendingTransition(R.anim.slide_in_bottom, R.anim.hold_anim);
```

## Security Vulnerabilities
If you discover a security vulnerability within the Beacon Bacon Android library, please send an email to us at [developer@mustache.dk](mailto:developer@mustache.dk). All security vulnerabilities will be promptly addressed.


## Contributing
Beacon Bacon is still in early development, so we have not yet developed a Contribution Guide. Pull requests are always welcome.


## License
The Beacon Bacon Android Library is open-sourced software licensed under the [MIT license](https://opensource.org/licenses/MIT).
