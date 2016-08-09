package it.sasabz.android.sasabus.util.recycler;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import it.sasabz.android.sasabus.R;
import it.sasabz.android.sasabus.model.CircleLine;
import it.sasabz.android.sasabus.model.line.Lines;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author Alex Lardschneider
 */
public class CircleLinesAdapter extends RecyclerView.Adapter<CircleLinesAdapter.ViewHolder> {

    private final Context mContext;
    private final List<CircleLine> mItems;

    public CircleLinesAdapter(Context context, List<CircleLine> items) {
        mContext = context;
        mItems = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.list_item_planned_trips_lines, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        CircleLine item = mItems.get(position);

        viewHolder.frameLayout.setSelected(item.isSelected());
        viewHolder.text.setText(Lines.lidToName(item.getId()));

        if (item.isSelected()) {
            viewHolder.text.setTextColor(ContextCompat.getColor(mContext, R.color.white));
        } else {
            viewHolder.text.setTextColor(Color
                    .parseColor('#' + Lines.lineColors[position + 2]));
        }

        Drawable drawable = viewHolder.frameLayout.getBackground();
        drawable.setColorFilter(Color
                .parseColor('#' + Lines.lineColors[position + 2]), PorterDuff.Mode.ADD);
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public static final class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.list_item_planned_data_lines) FrameLayout frameLayout;
        @BindView(R.id.list_item_planned_data_lines_text) TextView text;

        private ViewHolder(View view) {
            super(view);

            ButterKnife.bind(this, view);
        }
    }
}