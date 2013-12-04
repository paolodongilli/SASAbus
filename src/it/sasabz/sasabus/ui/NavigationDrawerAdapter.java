package it.sasabz.sasabus.ui;

import java.util.List;

import it.sasabz.android.sasabus.R;
import it.sasabz.sasabus.ui.MainActivity.DrawerItem;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class NavigationDrawerAdapter extends ArrayAdapter<DrawerItem> {
	
	Context mContext;
	int mResource;

	public NavigationDrawerAdapter(Context context, int resource,
			List<DrawerItem> objects) {
		super(context, resource, objects);
		this.mContext = context;
		this.mResource = resource;
	}
	
	static class ViewHolder {
		final TextView textviewNavigationTitle;
		
		public ViewHolder(TextView textviewNavigationTitle) {
			this.textviewNavigationTitle = textviewNavigationTitle;
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		TextView textviewNavigationTitle;
		
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(mResource, parent, false);
			textviewNavigationTitle = (TextView) convertView.findViewById(R.id.textview_title);
			convertView.setTag(new ViewHolder(textviewNavigationTitle));
		} else {
			ViewHolder viewHolder = (ViewHolder) convertView.getTag();
			textviewNavigationTitle = viewHolder.textviewNavigationTitle;
		}
		
		DrawerItem drawerItem = getItem(position);
		textviewNavigationTitle.setText(drawerItem.navigationTitle);
		textviewNavigationTitle.setCompoundDrawablesWithIntrinsicBounds(drawerItem.navigationIcon, null, null, null);
		return convertView;
	}
}
