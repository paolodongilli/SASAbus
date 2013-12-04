package it.sasabz.sasabus.ui;

import java.util.List;

import it.sasabz.android.sasabus.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Html;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockDialogFragment;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;

public class CustomDialog extends SherlockDialogFragment {
	
	Dialog dialog;
	
	static String TITLE = "title";
	static String MESSAGE = "message";
	static String POSITIVE = "positive";
	static String NEGATIVE = "negative";
	
	protected String title;
	protected String message;
	protected String positiveText;
	protected String negativeText;
	protected List<?> list;
	
	protected TextView textviewTitle;
	protected ScrollView scrollviewMessage;
	protected TextView textviewMessage;
	protected ListView listviewList;
	protected Button buttonNegative;
	protected Button buttonPositive;
	
	protected Adapter adapter;
	protected DialogInterface.OnClickListener positiveListener;
	protected DialogInterface.OnClickListener negativeListener;

	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		
		dialog = new Dialog(getSherlockActivity());
		
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		dialog.setContentView(R.layout.custom_dialog);
		
		WindowManager.LayoutParams wmlp = dialog.getWindow().getAttributes();
		wmlp.width = WindowManager.LayoutParams.FILL_PARENT;
		
		initializeViews();
		
		addTextToViewsAndRemoveUnnecessary();
		
		return dialog;
	}
	
	private void initializeViews(){
		textviewTitle = (TextView) dialog.findViewById(R.id.textview_title);
		scrollviewMessage = (ScrollView) dialog.findViewById(R.id.scrollview_message);
		textviewMessage = (TextView) dialog.findViewById(R.id.textview_message);
		listviewList = (ListView) dialog.findViewById(R.id.listview_list);
		buttonNegative = (Button) dialog.findViewById(R.id.button_negative);
		buttonPositive = (Button) dialog.findViewById(R.id.button_positive);
	}

	
	private void addTextToViewsAndRemoveUnnecessary() {
		
		if(title != null) {
			textviewTitle.setText(title);
		}
		
		
		if (message != null) {
			textviewMessage.setText(Html.fromHtml(message));
		} else {
			scrollviewMessage.setVisibility(View.GONE);
		}
		
		
		if (list != null) {
			listviewList.setAdapter((ListAdapter) adapter);
		} else {
			listviewList.setVisibility(View.GONE);
		}
		
		if (list != null) {
			
		} else {
			
		}
		
		
		if (negativeText != null) {
			buttonNegative.setText(negativeText);
			buttonNegative.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					negativeListener.onClick(getDialog(), 0);
				}
			});
		} else {
			buttonNegative.setVisibility(View.GONE);
		}
		
		
		if (positiveText != null) {
			buttonPositive.setText(positiveText);
			buttonPositive.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					positiveListener.onClick(getDialog(), 0);
				}
			});
		} else {
			buttonPositive.setVisibility(View.GONE);
		}
		
	}
	
	
	
	
	public static class Builder {
		
		CustomDialog customDialog;
		SherlockFragment fragment;
		
		
		public Builder(SherlockFragment fragment) {
			customDialog = new CustomDialog();
			this.fragment = fragment;
		}
		
		
		public void setTitle(String title) {
			customDialog.title = title;
		}
		
		public void setMessage(String message) {
			customDialog.message = message;
		}
		
		public void setList(List<?> list, Adapter adapter) {
			customDialog.list = list;
			customDialog.adapter = adapter;
		}
		
		public void setPositiveButton(String text, DialogInterface.OnClickListener listener) {
			customDialog.positiveText = text;
			customDialog.positiveListener = listener;
		}
		
		public void setNegativeButton(String text, DialogInterface.OnClickListener listener) {
			customDialog.negativeText = text;
			customDialog.negativeListener = listener;
		}
		
		
		public void show() {
			customDialog.show(fragment.getSherlockActivity().getSupportFragmentManager(), "info_dialog");
		}
		
	}
	
}