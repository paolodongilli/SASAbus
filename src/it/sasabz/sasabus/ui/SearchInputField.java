package it.sasabz.sasabus.ui;

import it.sasabz.android.sasabus.R;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

public class SearchInputField extends RelativeLayout {

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
		
		initializeViews();
		init(context, attrs);
		addOnClickListenerForMoreButtons(this);
		
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
	
	
	//More buttons
		private void addOnClickListenerForMoreButtons(View view) {
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
		
		
		public String getHint() {
			return autocompletetextviewInputfield.getHint().toString();
		}
		
		public void setHint(String hint) {
			autocompletetextviewInputfield.setHint(hint); 
		}
		
		public String getText() {
			return autocompletetextviewInputfield.getText().toString();
		}
	
}