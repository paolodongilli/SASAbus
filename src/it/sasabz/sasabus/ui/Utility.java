package it.sasabz.sasabus.ui;

import com.actionbarsherlock.app.SherlockFragment;

import it.sasabz.android.sasabus.R;
import it.sasabz.sasabus.ui.news.CityNewsFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;

public class Utility {

	public static void getListViewSize(ListView myListView) {
        ListAdapter myListAdapter = myListView.getAdapter();
        if (myListAdapter == null) {
            //do nothing return null
            return;
        }
        //set listAdapter in loop for getting final size
        int totalHeight = 0;
        for (int size = 0; size < myListAdapter.getCount(); size++) {
            View listItem = myListAdapter.getView(size, null, myListView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
      //setting listview item in adapter
        ViewGroup.LayoutParams params = myListView.getLayoutParams();
        params.height = totalHeight + (myListView.getDividerHeight() * (myListAdapter.getCount() - 1));
        myListView.setLayoutParams(params);
        // print height of adapter on log
        Log.i("height of listItem:", String.valueOf(totalHeight));
    }
	
	
	public static String getTimeWithZero(int timeNumber) {
		String time = ""+timeNumber;
		if (timeNumber < 10){
			time = "0"+timeNumber;
		}
		return time;
	}
	
	public static int getDipsFromPixel(Context context, float pixels) {
        // Get the screen's density scale
        final float scale = context.getResources().getDisplayMetrics().density;
        // Convert the dps to pixels, based on density scale
        return (int) (pixels * scale + 0.5f);
    }
	
	public static void showNetworkErrorDialog(SherlockFragment fragment) {
		CustomDialog.Builder infoDialogBuilder = new CustomDialog.Builder(fragment);
		infoDialogBuilder.setTitle(fragment.getResources().getString(R.string.error));
		infoDialogBuilder.setMessage(fragment.getResources().getString(R.string.error));
		infoDialogBuilder.setNegativeButton(fragment.getResources().getString(android.R.string.ok), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		infoDialogBuilder.show();
	}
	
}