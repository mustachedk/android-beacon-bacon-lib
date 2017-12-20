package dk.mustache.beaconbacon.adapters;

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

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import dk.mustache.beaconbacon.R;
import dk.mustache.beaconbacon.activities.BeaconBaconActivity;
import dk.mustache.beaconbacon.data.BeaconBaconManager;
import dk.mustache.beaconbacon.datamodels.BBPlace;
import dk.mustache.beaconbacon.utils.CheckboxColorUtil;

public class PlaceSelectionAdapter extends RecyclerView.Adapter<PlaceSelectionAdapter.ViewHolder> {
    private Context context;
    private List<BBPlace> places;
    private BBPlace currentPlace;

    public PlaceSelectionAdapter(Context context, List<BBPlace> places, BBPlace currentPlace) {
        this.context = context;
        this.places = places;
        this.currentPlace = currentPlace;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_place_selection_item, parent, false);

        return new PlaceSelectionAdapter.ViewHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.placeText.setText(places.get(position).getName());

        if(currentPlace != null && places.get(position).getId() == currentPlace.getId())
            holder.checkBox.setChecked(true);
        else
            holder.checkBox.setChecked(false);

        if(position % 2 == 0)
            holder.itemView.setBackgroundColor(context.getResources().getColor(R.color.colorListItem));
        else
            holder.itemView.setBackgroundColor(Color.WHITE);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.checkBox.setChecked(!holder.checkBox.isChecked());
                currentPlace = places.get(holder.getAdapterPosition());
                BeaconBaconManager.getInstance().setCurrentPlace(places.get(holder.getAdapterPosition()));
                BeaconBaconManager.getInstance().setCurrentFloorIndex(0);
                BeaconBaconManager.getInstance().setCurrentFloorId(-1);
                notifyDataSetChanged();

                if(places != null) {
                    ((BeaconBaconActivity) context).setNewCurrentPlace(currentPlace);
                    ((BeaconBaconActivity) context).fabPoi.show();
                }

                if(BeaconBaconManager.getInstance().getRequestObject() != null)
                    ((BeaconBaconActivity) context).fabFindTheBook.show();

                if(BeaconBaconManager.getInstance().getRequestObject() != null)
                    ((BeaconBaconActivity) context).findABook(String.valueOf(currentPlace.getId()));

                if(((BeaconBaconActivity) context).snackbar != null) {
                    ((BeaconBaconActivity) context).snackbar.getView().setVisibility(View.VISIBLE);
                    ((BeaconBaconActivity) context).snackbar.getView().animate()
                            .alpha(1)
                            .setDuration(300)
                            .start();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return places.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView placeText;
        AppCompatCheckBox checkBox;

        private ViewHolder(View itemView, int viewType) {
            super(itemView);
            placeText = itemView.findViewById(R.id.place_selection_item_text);
            if(BeaconBaconManager.getInstance().getConfigurationObject() != null && BeaconBaconManager.getInstance().getConfigurationObject().getTypeface() != null)
                placeText.setTypeface(BeaconBaconManager.getInstance().getConfigurationObject().getTypeface());

            checkBox = itemView.findViewById(R.id.place_selection_item_checkbox);
            if(BeaconBaconManager.getInstance().getConfigurationObject() != null && BeaconBaconManager.getInstance().getConfigurationObject().getTintColor() != -1)
                CheckboxColorUtil.setAppCompatCheckBoxColors(checkBox, Color.TRANSPARENT, context.getResources().getColor(BeaconBaconManager.getInstance().getConfigurationObject().getTintColor()));
        }
    }
}
