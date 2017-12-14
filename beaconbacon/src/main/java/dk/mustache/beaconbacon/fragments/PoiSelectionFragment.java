package dk.mustache.beaconbacon.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import dk.mustache.beaconbacon.R;
import dk.mustache.beaconbacon.activities.BeaconBaconActivity;
import dk.mustache.beaconbacon.adapters.PoiSelectionAdapter;
import dk.mustache.beaconbacon.data.BeaconBaconManager;
import dk.mustache.beaconbacon.datamodels.BBPoi;
import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

public class PoiSelectionFragment extends Fragment implements PoiSelectionAdapter.addSelectedPoisInterface {
    //RecyclerView Setup
    private StickyListHeadersListView stickyListHeadersListView;
    private StickyListHeadersAdapter stickyListHeadersAdapter;
    public List<BBPoi> selectedPois;

    public PoiSelectionFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_poi_selection, container, false);

        Toolbar toolbar = view.findViewById(R.id.fragment_poi_toolbar);
        TextView toolbarTitle = view.findViewById(R.id.fragment_poi_toolbar_title);

        if(BeaconBaconManager.getInstance().getConfigurationObject() != null && BeaconBaconManager.getInstance().getConfigurationObject().getTypeface() != null)
            toolbarTitle.setTypeface(BeaconBaconManager.getInstance().getConfigurationObject().getTypeface());

        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        if(((AppCompatActivity) getActivity()).getSupportActionBar() != null)
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);

        setHasOptionsMenu(true);


        //RecyclerView Setup
        stickyListHeadersListView = view.findViewById(R.id.poi_list);
        stickyListHeadersAdapter = new PoiSelectionAdapter(getActivity(), BeaconBaconManager.getInstance().getCurrentPlace().getPoiMenuItem(), selectedPois, this);
        stickyListHeadersListView.setAdapter(stickyListHeadersAdapter);

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
            ((BeaconBaconActivity) getActivity()).setSelectedPois(selectedPois);
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
            getActivity().getSupportFragmentManager().popBackStack();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void addSelectedPoi(BBPoi poi) {
        if(selectedPois == null)
            selectedPois = new ArrayList<>();

        selectedPois.add(poi);
    }

    @Override
    public void removeSelectedPoi(BBPoi poi) {
        selectedPois.remove(poi);
    }
}
