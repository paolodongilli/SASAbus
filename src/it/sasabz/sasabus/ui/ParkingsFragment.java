
package it.sasabz.sasabus.ui;

import it.sasabz.android.sasabus.R;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.actionbarsherlock.app.SherlockFragment;

public class ParkingsFragment extends SherlockFragment
{
   LinearLayout ll;

   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
   {
      final MainActivity mainActivity = (MainActivity) this.getActivity();
      this.ll = new LinearLayout(mainActivity);
      TextView searching = new TextView(mainActivity);
      searching.setText(mainActivity.getString(R.string.searching_connection));
      this.ll.addView(searching);
      return this.ll;

   }
}
