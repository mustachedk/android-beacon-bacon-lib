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

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import dk.mustache.beaconbacon.R;
import dk.mustache.beaconbacon.datamodels.BBPoi;
import dk.mustache.beaconbacon.datamodels.Place;
import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

public class PoiSelectionAdapter extends BaseAdapter implements StickyListHeadersAdapter {
    private LayoutInflater inflater;
    private Place currentPlace;
    private List<BBPoi> pois = new ArrayList<>();
    private Context context;

    public PoiSelectionAdapter(Context context, Place currentPlace) {
        inflater = LayoutInflater.from(context);
        this.currentPlace = currentPlace;
        this.context = context;

        if(currentPlace != null && currentPlace.getFloors() != null &&
                currentPlace.getFloors().get(0).getLocations() != null &&
                currentPlace.getFloors().get(0).getLocations().size() != 0) {
            for (int i = 0; i < currentPlace.getFloors().get(0).getLocations().size(); i++) {

                if (currentPlace.getFloors().get(0).getLocations().get(i).getPoi() != null) {
                    pois.add(currentPlace.getFloors().get(0).getLocations().get(i).getPoi());
                }
            }
        }
    }

    @Override
    public int getCount() {
        if(pois != null)
            return pois.size();
        else
            return 0;
    }

    @Override
    public Object getItem(int position) {
        return pois.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;

        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.layout_poi_selection_item, parent, false);

            holder.layout = convertView.findViewById(R.id.poi_selection_item_layout);
            holder.image = convertView.findViewById(R.id.poi_selection_item_image);
            holder.checkBox = convertView.findViewById(R.id.poi_selection_item_checkbox);
            holder.text = convertView.findViewById(R.id.poi_selection_item_text);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if(position % 2 == 0)
            holder.layout.setBackgroundColor(context.getResources().getColor(R.color.colorListItem));
        else
            holder.layout.setBackgroundColor(Color.WHITE);

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.checkBox.setChecked(!holder.checkBox.isChecked());
            }
        });

        holder.text.setText(pois.get(position).getName());
        holder.checkBox.setChecked(false); //TODO Get state from active library

        if (pois.get(position).getIcon() != null) //If there's no image, don't load it in
            Picasso.with(context).load(pois.get(position).getIcon()).resize(100, 100).centerCrop().into(holder.image);

        return convertView;
    }

    @Override
    public View getHeaderView(int position, View convertView, ViewGroup parent) {
        HeaderViewHolder holder;

        if (convertView == null) {
            holder = new HeaderViewHolder();
            convertView = inflater.inflate(R.layout.layout_poi_selection_header_item, parent, false);
            holder.header = convertView.findViewById(R.id.header_item_text);
            convertView.setTag(holder);
        } else {
            holder = (HeaderViewHolder) convertView.getTag();
        }

        holder.header.setText(pois.get(position).getName());

        return convertView;
    }

    @Override
    public long getHeaderId(int position) {
        return pois.get(position).getId();
    }

    class HeaderViewHolder {
        TextView header;
    }

    class ViewHolder {
        LinearLayout layout;
        CircleImageView image;
        AppCompatCheckBox checkBox;
        TextView text;
    }
}
