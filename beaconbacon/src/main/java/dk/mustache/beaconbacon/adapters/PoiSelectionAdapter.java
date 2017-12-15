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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import dk.mustache.beaconbacon.R;
import dk.mustache.beaconbacon.api.ApiManager;
import dk.mustache.beaconbacon.customviews.AreaView;
import dk.mustache.beaconbacon.data.BeaconBaconManager;
import dk.mustache.beaconbacon.datamodels.BBPoi;
import dk.mustache.beaconbacon.datamodels.BBPoiMenuItem;
import dk.mustache.beaconbacon.utils.CheckboxColorUtil;
import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

import static android.graphics.Color.parseColor;

public class PoiSelectionAdapter extends BaseAdapter implements StickyListHeadersAdapter {
    private LayoutInflater inflater;
    private List<BBPoiMenuItem> poiMenuItems = new ArrayList<>();
    private List<BBPoi> selectedPois;
    private Context context;
    private addSelectedPoisInterface addSelectedPoisInterface;

    public interface addSelectedPoisInterface {
        void addSelectedPoi(BBPoi poi);
        void removeSelectedPoi(BBPoi poi);
    }

    public PoiSelectionAdapter(Context context, List<BBPoiMenuItem> poiMenuItems, List<BBPoi> selectedPois, addSelectedPoisInterface addSelectedPoisInterface) {
        this.addSelectedPoisInterface = addSelectedPoisInterface;
        this.selectedPois = selectedPois;
        inflater = LayoutInflater.from(context);
        this.poiMenuItems = poiMenuItems;
        this.context = context;
    }

    @Override
    public int getCount() {
        if(poiMenuItems != null)
            return poiMenuItems.size();
        else
            return 0;
    }

    @Override
    public Object getItem(int position) {
        return poiMenuItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if(poiMenuItems.get(position).getPoi() != null) {
            final ViewHolder holder;

            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.layout_poi_selection_item, parent, false);

            holder.layout = convertView.findViewById(R.id.poi_selection_item_layout);
            holder.image = convertView.findViewById(R.id.poi_selection_item_image);

            holder.checkBox = convertView.findViewById(R.id.poi_selection_item_checkbox);
            if(BeaconBaconManager.getInstance().getConfigurationObject() != null && BeaconBaconManager.getInstance().getConfigurationObject().getTintColor() != -1)
                CheckboxColorUtil.setAppCompatCheckBoxColors(holder.checkBox, Color.TRANSPARENT, context.getResources().getColor(BeaconBaconManager.getInstance().getConfigurationObject().getTintColor()));

            holder.text = convertView.findViewById(R.id.poi_selection_item_text);
            if(BeaconBaconManager.getInstance().getConfigurationObject() != null && BeaconBaconManager.getInstance().getConfigurationObject().getTypeface() != null)
                holder.text.setTypeface(BeaconBaconManager.getInstance().getConfigurationObject().getTypeface());

            holder.areaView = convertView.findViewById(R.id.poi_selection_item_image_area);

            if (position % 2 == 0)
                holder.layout.setBackgroundColor(context.getResources().getColor(R.color.colorListItem));
            else
                holder.layout.setBackgroundColor(Color.WHITE);

            holder.layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder.checkBox.setChecked(!holder.checkBox.isChecked());
                    if(holder.checkBox.isChecked()) {
                        addSelectedPoisInterface.addSelectedPoi(poiMenuItems.get(position).getPoi());
                    } else {
                        addSelectedPoisInterface.removeSelectedPoi(poiMenuItems.get(position).getPoi());
                    }
                }
            });

            holder.text.setText(poiMenuItems.get(position).getPoi().getName());

            if(selectedPois != null) {
                for(BBPoi poi : selectedPois) {
                    if(poi.getId() == poiMenuItems.get(position).getPoi().getId()) {
                        holder.checkBox.setChecked(true);
                        break;
                    } else {
                        holder.checkBox.setChecked(false);
                    }
                }
            } else {
                holder.checkBox.setChecked(false);
            }

            if(!Objects.equals(poiMenuItems.get(position).getPoi().getIcon(), "")) {
                ApiManager.getInstance().getPicasso().load(poiMenuItems.get(position).getPoi().getIcon())
                        .resize(200,200)
                        .centerCrop()
                        .into(holder.image);
            } else if(Objects.equals(poiMenuItems.get(position).getPoi().getType(), "area")) {
                holder.areaView.setCircleColor(parseColor(poiMenuItems.get(position).getPoi().getColor()));
            }

            return convertView;
        } else {
            return inflater.inflate(R.layout.layout_poi_selection_item_empty, parent, false);
        }
    }

    @Override
    public View getHeaderView(int position, View convertView, ViewGroup parent) {
        if(poiMenuItems.get(position).getPoi() == null) {
            HeaderViewHolder holder;

            holder = new HeaderViewHolder();
            convertView = inflater.inflate(R.layout.layout_poi_selection_header_item, parent, false);
            holder.header = convertView.findViewById(R.id.header_item_text);
            convertView.setTag(holder);

            holder.header.setText(poiMenuItems.get(position).getTitle());
            if(BeaconBaconManager.getInstance().getConfigurationObject() != null && BeaconBaconManager.getInstance().getConfigurationObject().getTypeface() != null)
                holder.header.setTypeface(BeaconBaconManager.getInstance().getConfigurationObject().getTypeface());

            return convertView;
        } else {
            return inflater.inflate(R.layout.layout_poi_selection_item_empty, parent, false);
        }
    }

    @Override
    public long getHeaderId(int position) {
        return poiMenuItems.get(position).getId();
    }

    class HeaderViewHolder {
        TextView header;
    }

    class ViewHolder {
        LinearLayout layout;
        CircleImageView image;
        AppCompatCheckBox checkBox;
        TextView text;
        AreaView areaView;
    }
}
