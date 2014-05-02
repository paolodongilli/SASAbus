
package it.sasabz.sasabus.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import com.actionbarsherlock.app.SherlockFragment;

public class AboutFragment extends SherlockFragment
{
   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
   {
      final MainActivity mainActivity = (MainActivity) this.getActivity();
      WebView webView = new WebView(mainActivity);
      webView.loadUrl("file:///android_asset/about.html");
      return webView;
   }
}
