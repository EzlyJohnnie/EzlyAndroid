package com.ezly.ezly_android.UI.Address;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.ezly.ezly_android.Utils.Helper.LocationHerpler.EzlyAddress;
import com.ezly.ezly_android.Utils.Helper.LocationHerpler.LocationHelper;
import com.ezly.ezly_android.Utils.Config;
import com.mapbox.services.commons.ServicesException;
import com.mapbox.services.commons.models.Position;
import com.mapbox.services.geocoding.v5.GeocodingCriteria;
import com.mapbox.services.geocoding.v5.MapboxGeocoding;
import com.mapbox.services.geocoding.v5.models.CarmenFeature;

import java.io.IOException;
import java.util.List;


/**
 * Created by Johnnie on 13/02/17.
 */

public class GeocoderAdapter extends BaseAdapter implements Filterable {

    private GeocoderFilter geocoderFilter;
    private List<CarmenFeature> features;

    private LocationHelper locationHelper;

    public GeocoderAdapter(LocationHelper locationHelper) {
        this.locationHelper = locationHelper;
    }

    /*
     * Required by BaseAdapter
     */

    @Override
    public int getCount() {
        return features.size();
    }

    @Override
    public CarmenFeature getItem(int position) {
        return features.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /*
     * Get a View that displays the data at the specified position in the data set.
     */

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get view
        View view;
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            view = inflater.inflate(android.R.layout.simple_dropdown_item_1line, parent, false);
        } else {
            view = convertView;
        }

        // It always is a textview
        TextView text = (TextView) view;

        // Set the place name
        CarmenFeature feature = getItem(position);
        text.setText(feature.getPlaceName());

        return view;
    }

    /*
     * Required by Filterable
     */

    @Override
    public Filter getFilter() {
        if (geocoderFilter == null) {
            geocoderFilter = new GeocoderFilter();
        }

        return geocoderFilter;
    }

    private class GeocoderFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();

            // No constraint
            if (TextUtils.isEmpty(constraint)) {
                return results;
            }

            String searchText = constraint.toString();
//            if(!searchText.toLowerCase().contains("new zealand")){
//                searchText += " new zealand";
//            }

            //proximity location, user current location if has, middle NZ otherwise
            double lat;
            double lng;
            EzlyAddress lastKnowLocation = locationHelper.getLastKnownLocation();
            lat = lastKnowLocation == null ? -41.113110 : lastKnowLocation.getLocation().getLatitude();
            lng = lastKnowLocation == null ? 173.864501 : lastKnowLocation.getLocation().getLongitude();

            // The geocoder client
            MapboxGeocoding client = null;
            try {
                client = new MapboxGeocoding.Builder()
                        .setAccessToken(Config.MAPBOX_ACCESS_TOKEN)
                        .setLocation(searchText)
                        .setProximity(Position.fromCoordinates(lng, lat))
                        .setCountry("nz")
                        .setGeocodingType(GeocodingCriteria.TYPE_ADDRESS)
                        .build();

                features = client.executeCall().body().getFeatures();
                results.values = features;
                results.count = features.size();
            } catch (ServicesException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, Filter.FilterResults results) {
            if (results != null && results.count > 0) {
                features = (List<CarmenFeature>) results.values;
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }
    }
}