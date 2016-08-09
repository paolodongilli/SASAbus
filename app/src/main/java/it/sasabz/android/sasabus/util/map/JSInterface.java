package it.sasabz.android.sasabus.util.map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.webkit.JavascriptInterface;

import java.io.File;

import it.sasabz.android.sasabus.Config;
import it.sasabz.android.sasabus.R;
import it.sasabz.android.sasabus.ui.bus.BusDetailActivity;
import it.sasabz.android.sasabus.ui.busstop.BusStopDetailActivity;
import it.sasabz.android.sasabus.ui.line.LineCourseActivity;
import it.sasabz.android.sasabus.ui.line.LineDetailActivity;
import it.sasabz.android.sasabus.ui.route.RouteMapPickerActivity;
import it.sasabz.android.sasabus.util.LogUtils;

import static it.sasabz.android.sasabus.ui.busstop.BusStopActivity.INTENT_DISPLAY_FAVORITES;

class JSInterface {

    private static final String TAG = "JSInterface";

    private Context context;

    private File rootFolder;

    JSInterface(Context context) {
        this.context = context;

        rootFolder = MapDownloadHelper.getRootFolder(context);

        LogUtils.e(TAG, rootFolder.getAbsolutePath());
    }

    @JavascriptInterface
    public String getMapTilesRootUrl() {
        return "file://" + rootFolder.getAbsolutePath();
    }

    @JavascriptInterface
    public void onVehicleClick(int vehicle) {
        Intent intent = new Intent(context, BusDetailActivity.class);
        intent.putExtra(Config.EXTRA_VEHICLE, vehicle);
        context.startActivity(intent);
    }

    @JavascriptInterface
    public void onLineClick(int lineId) {
        Intent intent = new Intent(context, LineDetailActivity.class);
        intent.putExtra(Config.EXTRA_LINE_ID, lineId);
        context.startActivity(intent);
    }

    @JavascriptInterface
    public void onLineCourseClick(int vehicle, int lineId, int busStop) {
        Intent intent = new Intent(context, LineCourseActivity.class);
        intent.putExtra(Config.EXTRA_VEHICLE, vehicle);
        intent.putExtra(Config.EXTRA_STATION_ID, new int[]{busStop});
        intent.putExtra(Config.EXTRA_LINE_ID, lineId);
        context.startActivity(intent);
    }

    @JavascriptInterface
    public String getDelayString(int delay) {
        return context.getString(R.string.bottom_sheet_delayed, delay);
    }

    @JavascriptInterface
    public String getBusDetailsString() {
        return context.getString(R.string.bus_details).toUpperCase();
    }

    @JavascriptInterface
    public String getLineDetailsString() {
        return context.getString(R.string.lines_detail).toUpperCase();
    }

    @JavascriptInterface
    public String getCourseDetailsString() {
        return context.getString(R.string.course_details).toUpperCase();
    }

    @JavascriptInterface
    public void onBusStopDetailsClick(int id) {
        Intent intent = new Intent(context, BusStopDetailActivity.class);
        intent.putExtra(Config.EXTRA_STATION_ID, id);
        ((Activity) context).startActivityForResult(intent, INTENT_DISPLAY_FAVORITES);
    }

    @JavascriptInterface
    public String getBusStopDetailsString() {
        return context.getString(R.string.station_details).toUpperCase();
    }

    @JavascriptInterface
    public void onBusStopSelectClick(int id) {
        ((RouteMapPickerActivity) context).selectBusStop(id);
    }

    @JavascriptInterface
    public String getSelectString() {
        return context.getString(R.string.station_select).toUpperCase();
    }
}
