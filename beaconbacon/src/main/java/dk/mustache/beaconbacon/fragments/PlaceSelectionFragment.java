package dk.mustache.beaconbacon.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import dk.mustache.beaconbacon.R;
import dk.mustache.beaconbacon.activities.BeaconBaconActivity;
import dk.mustache.beaconbacon.adapters.PlaceSelectionAdapter;
import dk.mustache.beaconbacon.data.BeaconBaconManager;
import dk.mustache.beaconbacon.datamodels.BBFloor;

public class PlaceSelectionFragment extends Fragment {
    private BBFloor currentFloor;

    //RecyclerView Setup
    private RecyclerView recyclerView;
    public RecyclerView.Adapter adapter;
    private LinearLayoutManager layoutManager;

    public PlaceSelectionFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_place_selection, container, false);

        //Toolbar Setup
        Toolbar toolbar = view.findViewById(R.id.fragment_place_toolbar);
        TextView toolbarTitle = view.findViewById(R.id.fragment_place_toolbar_title);

        if(BeaconBaconManager.getInstance().getConfigurationObject() != null && BeaconBaconManager.getInstance().getConfigurationObject().getTypeface() != null)
            toolbarTitle.setTypeface(BeaconBaconManager.getInstance().getConfigurationObject().getTypeface());

        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        if (((AppCompatActivity) getActivity()).getSupportActionBar() != null)
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);

        setHasOptionsMenu(true);

        //RecyclerView Setup
        recyclerView = view.findViewById(R.id.place_list);
        adapter = new PlaceSelectionAdapter(getActivity(), BeaconBaconManager.getInstance().getAllPlaces().getData(), BeaconBaconManager.getInstance().getCurrentPlace());
        recyclerView.setAdapter(adapter);
        layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        return view;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_close) {
            ((BeaconBaconActivity) getActivity()).fabPoi.show();

            if(BeaconBaconManager.getInstance().getRequestObject() != null)
                ((BeaconBaconActivity) getActivity()).fabFindTheBook.show();

            if(((BeaconBaconActivity) getActivity()).snackbar != null) {
                ((BeaconBaconActivity) getActivity()).snackbar.getView().setVisibility(View.VISIBLE);
                ((BeaconBaconActivity) getActivity()).snackbar.getView().animate()
                        .alpha(1)
                        .setDuration(300)
                        .start();
            }

            //If we have no place, just finish
            if(BeaconBaconManager.getInstance().getCurrentPlace() == null)
                getActivity().finish();
            else
                getActivity().getSupportFragmentManager().popBackStack();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
