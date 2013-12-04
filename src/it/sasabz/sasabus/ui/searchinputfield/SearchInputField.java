package it.sasabz.sasabus.ui.searchinputfield;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.actionbarsherlock.app.SherlockFragmentActivity;

import it.sasabz.android.sasabus.R;
import it.sasabz.sasabus.data.models.BusStop;
import it.sasabz.sasabus.data.models.News;
import it.sasabz.sasabus.ui.CustomDialog;
import it.sasabz.sasabus.ui.CustomDialog.Builder;
import it.sasabz.sasabus.ui.news.NewsFragment;
import it.sasabz.sasabus.ui.news.NewsAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

public class SearchInputField extends RelativeLayout {

	private Context context;
	
	private ViewGroup viewgroupInputfield;
	private AutoCompleteTextView autocompletetextviewInputfield;
	
	private ViewGroup viewgroupMore;
	private ImageButton imageButtonMore;
	
	private ViewGroup viewgroupButtons;
	private ImageButton imagebuttonNearby;
	private ImageButton imagebuttonMap;
	private ImageButton imagebuttonFavorites;
	
	
	public SearchInputField(Context context, AttributeSet attrs) {
		super(context, attrs);
		inflate(context, R.layout.search_inputfield, this);
		this.context = context;
		
		initializeViews();
		init(context, attrs);
		addOnClickListenerForMoreButton();
	}
	
	private void init(Context context, AttributeSet attrs) {
		TypedArray array = context.getTheme().obtainStyledAttributes
				(attrs, R.styleable.SearchInputField, 0, 0);
		try {
			autocompletetextviewInputfield.setHint
				(array.getString(R.styleable.SearchInputField_android_hint));
		} finally {
			array.recycle();
		}
	}
	
	private void initializeViews() {
		viewgroupInputfield = (ViewGroup) 
				findViewById(R.id.linearlayout_autocompletetextview);
		autocompletetextviewInputfield = (AutoCompleteTextView)
				findViewById(R.id.autocompletetextview_busstop);
		
		viewgroupMore = (ViewGroup) findViewById(R.id.linearlayout_more);
		imageButtonMore = (ImageButton) viewgroupMore
				.findViewById(R.id.imagebutton_more);
		
		viewgroupButtons = (ViewGroup) findViewById(R.id.linearlayout_buttons);
		imagebuttonNearby = (ImageButton) viewgroupButtons.findViewById(R.id.imagebutton_nearby);
		imagebuttonMap = (ImageButton) viewgroupButtons.findViewById(R.id.imagebutton_map);
		imagebuttonFavorites = (ImageButton) viewgroupButtons.findViewById(R.id.imagebutton_favorites);
	}
	
	
	//More button
	private void addOnClickListenerForMoreButton() {
		imageButtonMore.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				openCloseSearchButtons(v);
			}
		});
	}
	
	private LayoutParams paramsButtons;
	private LayoutParams paramsInputfield;
	
	private void openCloseSearchButtons(View v) {
		paramsButtons = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		paramsInputfield = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		
		if (!imageButtonMore.isSelected()) {
			paramsButtons.addRule(RelativeLayout.LEFT_OF, viewgroupMore.getId());
			viewgroupButtons.setLayoutParams(paramsButtons);
			
			paramsInputfield.addRule(RelativeLayout.LEFT_OF, viewgroupButtons.getId());
			viewgroupInputfield.setLayoutParams(paramsInputfield);
			
			imageButtonMore.setSelected(true);
		} else {
			paramsButtons.addRule(RelativeLayout.RIGHT_OF, viewgroupMore.getId());
			viewgroupButtons.setLayoutParams(paramsButtons);
			
			paramsInputfield.addRule(RelativeLayout.LEFT_OF, viewgroupMore.getId());
			viewgroupInputfield.setLayoutParams(paramsInputfield);
			
			imageButtonMore.setSelected(false);
		}
		
	}
	
	
	//Search buttons (nearby, map, favorites)
	public void addOnClickListenerForSearchButtons(final SherlockFragmentActivity activity) {
		addOnClickListenerForBusstopsNearbyButton(activity);
		
		//bus stops from map
		imagebuttonMap.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO open the open streetmap
				
			}
		});
		
		addOnClickListenerForFavoriteBusstopsButton(activity);
		
	}
	
	
	private void addOnClickListenerForBusstopsNearbyButton(final SherlockFragmentActivity activity) {
		imagebuttonNearby.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				List<String> busstopsNearby = Arrays.asList("Test 1", "Test 2", "Test 3");
				Adapter adapter = new BusStopsNearbyAdapter(context, 
						R.layout.listview_item_busstop_nearby, 
						R.id.textview_busstop, busstopsNearby);
				
//				CustomDialog.Builder busstopsNearbyDialogBuilder = new CustomDialog.Builder(activity);
//				busstopsNearbyDialogBuilder.setTitle(activity.getResources().getString(R.string.bus_stops_nearby));
//				busstopsNearbyDialogBuilder.setList(busstopsNearby, adapter);
//				busstopsNearbyDialogBuilder.setNegativeButton(getResources().getString(android.R.string.cancel), new DialogInterface.OnClickListener() {
//					@Override
//					public void onClick(DialogInterface dialog, int which) {
//						dialog.dismiss();
//					}
//				});
//				busstopsNearbyDialogBuilder.show();
			}
		});
	}
	
	private void addOnClickListenerForFavoriteBusstopsButton(final SherlockFragmentActivity activity) {
		imagebuttonFavorites.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				List<String> favoriteBusstops = Arrays.asList("Favorite Busstop 1", "Favorite Busstop 2", "Favorite Busstop 3");
				Adapter adapter = new FavoriteBusStopsAdapter(context, 
						R.layout.listview_item_favorite_busstops, 
						R.id.textview_busstop, favoriteBusstops);
				
//				CustomDialog.Builder favoriteBusstopsDialogBuilder = new CustomDialog.Builder(activity);
//				favoriteBusstopsDialogBuilder.setTitle(activity.getResources().getString(R.string.favorites_button));
//				favoriteBusstopsDialogBuilder.setList(favoriteBusstops, adapter);
//				favoriteBusstopsDialogBuilder.setNegativeButton(getResources().getString(android.R.string.cancel), new DialogInterface.OnClickListener() {
//					@Override
//					public void onClick(DialogInterface dialog, int which) {
//						dialog.dismiss();
//					}
//				});
//				favoriteBusstopsDialogBuilder.show();
			}
		});
	}
	
	
	public String getHint() {
		return autocompletetextviewInputfield.getHint().toString();
	}
	
	public void setHint(String hint) {
		autocompletetextviewInputfield.setHint(hint); 
	}
	
	public String getText() {
		return autocompletetextviewInputfield.getText().toString();
	}
	
	public void addAdapterToAutocompletetextview(Context context, List<BusStop> list) {
		ArrayAdapter<BusStop> adapter = new ArrayAdapter<BusStop>(context, 
				android.R.layout.simple_dropdown_item_1line, list);
		autocompletetextviewInputfield.setAdapter(adapter);
	}
	
}