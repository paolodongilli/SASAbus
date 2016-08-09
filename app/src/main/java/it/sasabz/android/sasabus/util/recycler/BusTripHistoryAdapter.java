package it.sasabz.android.sasabus.util.recycler;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import it.sasabz.android.sasabus.R;
import it.sasabz.android.sasabus.model.ExecutedTrip;
import it.sasabz.android.sasabus.provider.ApiUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author Alex Lardschneider
 */
public class BusTripHistoryAdapter extends RecyclerView.Adapter<BusTripHistoryAdapter.ViewHolder> {

    private final List<ExecutedTrip> mItems;
    private final SimpleDateFormat mDate = new SimpleDateFormat("dd.MM.yyyy", Locale.ITALY);

    public BusTripHistoryAdapter(List<ExecutedTrip> mItems) {
        this.mItems = mItems;
    }

    @Override
    public BusTripHistoryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_bus_trip_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mId.setText("#" + mItems.get(position).getTripId());
        holder.mLine.setText(mItems.get(position).getLine());
        holder.mDateTime.setText(mDate.format(new Date(mItems.get(position).getDate() * 1000)) + ' ' + ApiUtils.getTime(mItems.get(position).getDeparture()));
        holder.mFirstStop.setText(mItems.get(position).getFirstStop());
        holder.mLastStop.setText(mItems.get(position).getLastStop());
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.bus_trip_history_id) TextView mId;
        @BindView(R.id.bus_trip_history_line) TextView mLine;
        @BindView(R.id.bus_trip_history_date_time) TextView mDateTime;
        @BindView(R.id.bus_trip_history_first_stop) TextView mFirstStop;
        @BindView(R.id.bus_trip_history_last_stop) TextView mLastStop;

        public ViewHolder(View view) {
            super(view);

            ButterKnife.bind(this, view);
        }
    }
}