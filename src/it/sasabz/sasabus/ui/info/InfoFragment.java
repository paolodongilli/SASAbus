package it.sasabz.sasabus.ui.info;

import it.sasabz.android.sasabus.R;
import it.sasabz.sasabus.data.models.DBObject;
import it.sasabz.sasabus.data.models.Information;
import it.sasabz.sasabus.data.orm.InformationList;
import it.sasabz.sasabus.logic.Utility;
import it.sasabz.sasabus.ui.MainTabActivity;
import it.sasabz.sasabus.ui.SASAbus;
import it.sasabz.sasabus.ui.adapter.MyListAdapter;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockListFragment;

public class InfoFragment extends SherlockListFragment {

	/** The List to store the retrieved informations (de/it) from the server */
	private ArrayList<DBObject> list = null;

	/** Progress Dialog to show during retrieving the information */
	private ProgressDialog progressDialog = null;

	
	/** Called with the view of the Fragment is first created. */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.standard_listview_layout,
				container, false);
		TextView titel = (TextView) view.findViewById(R.id.untertitel);
		titel.setText(R.string.menu_infos);
		return view;
	}

	/**
	 * Called when the Fragment is about to start interacting with the user.
	 */
	@Override
	public void onResume() {
		super.onResume();
		
//		 If the phone has an internet connectivity, then the data is
//		 retrieved, otherwise a dialog informs the user of the
//		 "powered off connection"
		 
		if (Utility.hasNetworkConnection(getSherlockActivity()))
			fillData();
		else
		{
			AlertDialog.Builder builder = new AlertDialog.Builder(
					getSherlockActivity());
			builder.setCancelable(true);
			builder.setMessage(R.string.no_network_connection);
			builder.setTitle(R.string.error_title);
			builder.setNeutralButton(android.R.string.cancel,
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.cancel();
							dialog.dismiss();
						}
					});
			builder.create().show();
		}
	}

	/**
	 * This Method is reacting on an interaction from the user. If the user taps
	 * on a list element, so a dialog is showing the entire message of this
	 * information.
	 */
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		AlertDialog.Builder builder = new AlertDialog.Builder(
				getSherlockActivity());
		builder.setPositiveButton(android.R.string.ok,
				new Dialog.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialogInterface, int i) {
						dialogInterface.dismiss();
					}
				});
		Information information = (Information) list.get(position);
		builder.setTitle(Html.fromHtml(information.getTitel()));
		builder.setMessage(Html.fromHtml("<pre>" + information.getNachricht()
				+ "</pre>"));
		builder.create().show();
	}

	/**
	 * this method calls the information-retriever which retrieves the
	 * information from the server of SASA SpA-AG. The progress dialog will be
	 * created and will be shown
	 */
	public void fillData() {
		SharedPreferences shared = PreferenceManager
				.getDefaultSharedPreferences(getSherlockActivity());
		int infocity = Integer.parseInt(shared.getString("infos", "0"));

//		progressDialog = new ProgressDialog(getSherlockActivity());
//
//		progressDialog.setMessage(getResources().getText(R.string.waiting));
//		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//		progressDialog.setCancelable(false);
//		progressDialog.show();

		InformationList info = new InformationList(getSherlockActivity());
		info.execute(Integer.valueOf(infocity));
	}

	/**
	 * this is the method which fills the information containing list
	 * @param list	is an array list of informations
	 */
	public void fillList(ArrayList<DBObject> list) {
		this.list = list;
		MyListAdapter infos = new MyListAdapter(SASAbus.getContext(),
				R.id.text, R.layout.news_row, list);
		setListAdapter(infos);
		progressDialog.dismiss();
	}
	
	
}