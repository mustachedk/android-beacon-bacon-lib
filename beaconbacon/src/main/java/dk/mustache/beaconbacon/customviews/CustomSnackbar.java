package dk.mustache.beaconbacon.customviews;

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

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.support.annotation.NonNull;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.v4.view.ViewCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import dk.mustache.beaconbacon.R;
import dk.mustache.beaconbacon.data.BeaconBaconManager;

public class CustomSnackbar extends BaseTransientBottomBar<CustomSnackbar> {
    /**
     * Constructor for the transient bottom bar.
     *
     * @param parent The parent for this transient bottom bar.
     * @param content The content view for this transient bottom bar.
     * @param callback The content view callback for this transient bottom bar.
     */
    private CustomSnackbar(ViewGroup parent, View content, ContentViewCallback callback) {
        super(parent, content, callback);
    }

    public static CustomSnackbar make(@NonNull ViewGroup parent, @Duration int duration) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final View content = inflater.inflate(R.layout.find_book_snackbar, parent, false);
        final ContentViewCallback viewCallback = new ContentViewCallback(content);
        final CustomSnackbar customSnackbar = new CustomSnackbar(parent, content, viewCallback);
        customSnackbar.setDuration(duration);

        ImageView imageView = content.findViewById(R.id.snack_image);
        imageView.setImageBitmap(BeaconBaconManager.getInstance().getRequestObject().getImage());
        imageView.setColorFilter(new PorterDuffColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP));

        TextView textHeader = content.findViewById(R.id.snack_text_header);
        textHeader.setText(BeaconBaconManager.getInstance().getRequestObject().getTitle());
        textHeader.setTextColor(Color.WHITE);
        if(BeaconBaconManager.getInstance().getConfigurationObject() != null && BeaconBaconManager.getInstance().getConfigurationObject().getTypeface() != null)
            textHeader.setTypeface(BeaconBaconManager.getInstance().getConfigurationObject().getTypeface());

        TextView textSubheader = content.findViewById(R.id.snack_text_subheader);
        textSubheader.setText(BeaconBaconManager.getInstance().getRequestObject().getSubtitle());
        textSubheader.setTextColor(Color.WHITE);
        if(BeaconBaconManager.getInstance().getConfigurationObject() != null && BeaconBaconManager.getInstance().getConfigurationObject().getTypeface() != null)
            textSubheader.setTypeface(BeaconBaconManager.getInstance().getConfigurationObject().getTypeface());

        customSnackbar.getView().setElevation(0.0f);

        return customSnackbar;
    }

    public CustomSnackbar setAction(CharSequence text, final View.OnClickListener listener) {
        Button actionView = getView().findViewById(R.id.snack_action);
        actionView.setText(text);
        if(BeaconBaconManager.getInstance().getConfigurationObject() != null && BeaconBaconManager.getInstance().getConfigurationObject().getTypeface() != null)
            actionView.setTypeface(BeaconBaconManager.getInstance().getConfigurationObject().getTypeface());
        actionView.setVisibility(View.VISIBLE);
        actionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onClick(view);
            }
        });
        return this;
    }

    private static class ContentViewCallback implements BaseTransientBottomBar.ContentViewCallback {

        private View content;

        public ContentViewCallback(View content) {
            this.content = content;
        }

        @Override
        public void animateContentIn(int delay, int duration) {
            content.setScaleY(0f);
            ViewCompat.animate(content).scaleY(1f).setDuration(duration).setStartDelay(delay);
        }

        @Override
        public void animateContentOut(int delay, int duration) {
            content.setScaleY(1.0f);
            ViewCompat.animate(content).scaleY(0f).setDuration(duration).setStartDelay(delay);
        }
    }
}
