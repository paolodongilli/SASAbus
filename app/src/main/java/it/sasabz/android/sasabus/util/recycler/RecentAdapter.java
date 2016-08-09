package it.sasabz.android.sasabus.util.recycler;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import it.sasabz.android.sasabus.Config;
import it.sasabz.android.sasabus.R;
import it.sasabz.android.sasabus.model.route.RouteRecent;
import it.sasabz.android.sasabus.ui.busstop.BusStopDetailActivity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author Alex Lardschneider
 */
public class RecentAdapter extends RecyclerView.Adapter<RecentAdapter.ViewHolder> {

    private final List<RouteRecent> mItems;
    private final RecyclerView mRecyclerView;
    private final Context mContext;
    private final SparseBooleanArray mSelectedItemsIds;

    public RecentAdapter(Context context, RecyclerView recyclerView, List<RouteRecent> items) {
        mItems = items;
        mRecyclerView = recyclerView;
        mContext = context;

        mSelectedItemsIds = new SparseBooleanArray();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_route_recent, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int i) {
        RouteRecent item = mItems.get(i);

        holder.departure.setText(item.getOriginName() + " (" + item.getOriginMunic() + ')');
        holder.arrival.setText(item.getDestinationName() + " (" + item.getDestinationMunic() + ')');
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public void toggleSelection(View view, int position) {
        selectView(view, position, !mSelectedItemsIds.get(position));
    }

    public void removeSelection() {
        mSelectedItemsIds.clear();

        for (int i = 0; i < mItems.size(); i++) {
            View view = mRecyclerView.getLayoutManager().findViewByPosition(i);

            if (view != null) {
                view.setSelected(false);
                view.setBackgroundColor(ContextCompat.getColor(mContext, R.color.card_background));
            }
        }

        notifyDataSetChanged();
    }

    private void selectView(View view, int position, boolean value) {
        if (value) {
            mSelectedItemsIds.put(position, true);
            view.setBackgroundColor(ContextCompat.getColor(mContext, R.color.black_400));
        } else {
            mSelectedItemsIds.delete(position);
            view.setBackgroundColor(ContextCompat.getColor(mContext, R.color.card_background));
        }
    }

    public int getSelectedCount() {
        return mSelectedItemsIds.size();
    }

    public SparseBooleanArray getSelectedIds() {
        return mSelectedItemsIds;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.list_route_recent_departure) TextView departure;
        @BindView(R.id.list_route_recent_arrival) TextView arrival;

        ViewHolder(View view) {
            super(view);

            ButterKnife.bind(this, view);
        }

        @Override
        public void onClick(View v) {
            int position = getLayoutPosition();
            if (position == RecyclerView.NO_POSITION) return;

            RouteRecent item = mItems.get(position);

            Intent intent = new Intent(v.getContext(), BusStopDetailActivity.class);
            intent.putExtra(Config.EXTRA_STATION_ID, item.getId());
            v.getContext().startActivity(intent);
        }
    }
}