package it.sasabz.android.sasabus.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class ClusterMarker implements ClusterItem, Parcelable {

    private final LatLng position;
    private final String title;
    private final String snippet;

    public ClusterMarker(float lat, float lng, String title, String snippet) {
        position = new LatLng(lat, lng);
        this.title = title;
        this.snippet = snippet;
    }

    private ClusterMarker(Parcel in) {
        position = in.readParcelable(LatLng.class.getClassLoader());
        title = in.readString();
        snippet = in.readString();
    }

    @Override
    public LatLng getPosition() {
        return position;
    }

    public CharSequence getTitle() {
        return title;
    }

    public String getSnippet() {
        return snippet;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(position, 0);
        dest.writeString(title);
        dest.writeString(snippet);
    }

    public static final Parcelable.Creator<ClusterMarker> CREATOR = new Parcelable.Creator<ClusterMarker>() {

        @Override
        public ClusterMarker createFromParcel(Parcel in) {
            return new ClusterMarker(in);
        }

        @Override
        public ClusterMarker[] newArray(int size) {
            return new ClusterMarker[size];
        }
    };
}