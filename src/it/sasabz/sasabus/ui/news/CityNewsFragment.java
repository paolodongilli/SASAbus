package it.sasabz.sasabus.ui.news;

import java.util.ArrayList;
import java.util.List;

import it.sasabz.android.sasabus.R;
import it.sasabz.sasabus.data.models.News;
import it.sasabz.sasabus.logic.DownloadNews;
import it.sasabz.sasabus.ui.CustomDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;

public class CityNewsFragment extends SherlockFragment {
	
	private static final String ARG_Position = "position";

	private int mPosition;
	private ListView mListView;
	private ListAdapter mListAdapter;
	private List<News> mNews;
	
	public static CityNewsFragment newInstance(int position) {
		CityNewsFragment cityFragment = new CityNewsFragment();
		
		Bundle args = new Bundle();
			args.putInt(ARG_Position, position);
		cityFragment.setArguments(args);
		
		return cityFragment;
		
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
//		if (savedInstanceState!= null) {
//			mPosition = savedInstanceState.getInt(ARG_Position);
//		}
		
		mPosition = getArguments().getInt(ARG_Position);

	}
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_city_news, null);
		mListView = (ListView) view.findViewById(R.id.listview_news);
		
		if (mListAdapter != null) {
			addAdapterToList();
		}
		
		return view;
	}
	
	public void setListAdapter(List<News> infos, SherlockFragmentActivity activity) {
		mListAdapter = new NewsAdapter (activity, 
				R.layout.listview_item_news, R.id.textview_busline, infos);
		mNews = infos;
	}
	
	
	
	public void addAdapterToList() {
		
		mListView.setAdapter(mListAdapter);
		
		//Add onClickListener for list
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				
				//Open new Dialog for an Info
				CustomDialog.Builder infoDialogBuilder = new CustomDialog.Builder(CityNewsFragment.this);
				infoDialogBuilder.setTitle(mNews.get(position).getTitle());
				infoDialogBuilder.setMessage(mNews.get(position).getMessage());
				infoDialogBuilder.setNegativeButton(getResources().getString(android.R.string.ok), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
				infoDialogBuilder.show();
				
//					AlertDialog.Builder builder = new AlertDialog.Builder(InfoActivity.this);
//					builder.setTitle("Title...");
//					builder.setMessage("Message...");
//					builder.setPositiveButton("OK", null);
//					builder.show();
				
			}
		});
		
	}
	
	
}
