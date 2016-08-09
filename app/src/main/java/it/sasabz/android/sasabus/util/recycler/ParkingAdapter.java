package it.sasabz.android.sasabus.util.recycler;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import it.sasabz.android.sasabus.R;
import it.sasabz.android.sasabus.model.Parking;
import it.sasabz.android.sasabus.ui.parking.ParkingDetailActivity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author Alex Lardschneider
 */
public class ParkingAdapter extends RecyclerView.Adapter<ParkingAdapter.ViewHolder> {

    private final Context mContext;
    private final List<Parking> mItems;

    public ParkingAdapter(Context context, List<Parking> items) {
        mContext = context;
        mItems = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_parking, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        Parking item = mItems.get(position);

        String free = String.valueOf(item.getFreeSlots());

        if (free.equals("-1")) {
            viewHolder.slots.setVisibility(View.GONE);
        }

        viewHolder.name.setText(item.getName());
        viewHolder.slots.setMax(item.getTotalSlots());

        viewHolder.slots.setProgress(item.getTotalSlots() - item.getFreeSlots());
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.list_item_parking_card) CardView cardView;
        @BindView(R.id.parking_list_name) TextView name;
        @BindView(R.id.parking_list_slots) ProgressBar slots;

        ViewHolder(View view) {
            super(view);

            ButterKnife.bind(this, view);

            cardView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (position == RecyclerView.NO_POSITION) return;

            Parking item = mItems.get(position);

            Intent intent = new Intent(mContext, ParkingDetailActivity.class);
            intent.putExtra("name", item.getName());
            intent.putExtra("address", item.getAddress());
            intent.putExtra("phone", item.getPhone());
            intent.putExtra("lat", item.getLat());
            intent.putExtra("lon", item.getLng());
            intent.putExtra("currentFree", item.getFreeSlots());
            intent.putExtra("total", item.getTotalSlots());
            mContext.startActivity(intent);
        }
    }
}