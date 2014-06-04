package it.sasabz.sasabus.ui;

import it.sasabz.android.sasabus.R;
import it.sasabz.sasabus.data.AndroidOpenDataLocalStorage;
import it.sasabz.sasabus.opendata.client.model.BusStation;
import it.sasabz.sasabus.opendata.client.model.BusStationList;
import it.sasabz.sasabus.opendata.client.model.BusStop;
import it.sasabz.sasabus.ui.busstop.NextBusFragment;
import it.sasabz.sasabus.ui.searchinputfield.DistanceBusStation;
import it.sasabz.sasabus.ui.searchinputfield.DistanceBusStationAdapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import bz.davide.dmweb.client.leaflet.DistanceCalculator;

import com.actionbarsherlock.app.SherlockFragment;

public class ParkingsNearestStationsFragment extends SherlockFragment {

	private ParkingData data = null;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		final MainActivity mainActivity = (MainActivity) this.getActivity();
		View ret = inflater.inflate(R.layout.fragment_parking_nearest_stations,
				container, false);

		final ListView listView = (ListView) ret.findViewById(R.id.listview_nearest_stations);
		((TextView)ret.findViewById(R.id.textview_parking)).setText(data.name + ": " + getString(R.string.nearest_bus_stations));
		DistanceBusStationAdapter adapter;
		final ArrayList<DistanceBusStation> nearestStations;
		try {
			nearestStations = getNearestStations(mainActivity
					.getOpenDataStorage());
			
			adapter = new DistanceBusStationAdapter(mainActivity,
					nearestStations);
			listView.setAdapter(adapter);
			listView.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					NextBusFragment fragmentToShow = (NextBusFragment) SherlockFragment.instantiate(
							ParkingsNearestStationsFragment.this.getActivity(),
							NextBusFragment.class.getName());
					fragmentToShow.setInitialBusStationName(mainActivity
							.getBusStationNameUsingAppLanguage(nearestStations
									.get(position).busStation));
					FragmentManager fragmentManager = ParkingsNearestStationsFragment.this
							.getActivity().getSupportFragmentManager();
					fragmentManager.beginTransaction()
							.add(R.id.content_frame, fragmentToShow)
							.addToBackStack(null).commit();
					Log.d(ParkingsNearestStationsFragment.class.getSimpleName(),
							"addedToBackStack");
				}
			});

			listView.setAdapter(adapter);
		} catch (IOException exxx) {
			mainActivity.handleApplicationException(exxx);
		}
		return ret;

	}

	public void setDate(ParkingData data) {
		this.data = data;
	}

	private ArrayList<DistanceBusStation> getNearestStations(
			AndroidOpenDataLocalStorage openDataLocalStorage)
			throws IOException {
		BusStationList busStationList = openDataLocalStorage.getBusStations();

		BusStation[] busStations = busStationList.getList();
		DistanceBusStation[] distanceBusStations = new DistanceBusStation[busStations.length];

		for (int i = 0; i < busStations.length; i++) {
			BusStation busStation = busStations[i];

			DistanceBusStation distanceBusStation = new DistanceBusStation();
			distanceBusStation.busStation = busStation;

			BusStop busStop;
			double d;
			for (int k = 0; k < busStation.getBusStops().length; k++) {
				busStop = busStation.getBusStops()[k];
				d = DistanceCalculator.distanceMeter(
						data.location.getLatitude(),
						data.location.getLongitude(), busStop.getLat(),
						busStop.getLon());
				if (k == 0 || d < distanceBusStation.distance) {
					distanceBusStation.distance = d;
				}
			}
			distanceBusStations[i] = distanceBusStation;
		}

		Arrays.sort(distanceBusStations, new Comparator<DistanceBusStation>() {
			@Override
			public int compare(DistanceBusStation d1, DistanceBusStation d2) {
				int diff = d1.distance.compareTo(d2.distance);
				if (diff == 0) {
					diff = d1.busStation.getORT_NAME().compareTo(
							d2.busStation.getORT_NAME());
				}
				return diff;
			}
		});

		final ArrayList<DistanceBusStation> nearest4Stations = new ArrayList<DistanceBusStation>();
		for (int i = 0; i < 4; i++) {

			nearest4Stations.add(distanceBusStations[i]);
		}
		return nearest4Stations;
	}
}
