package it.sasabz.android.sasabus.realm.user;

import io.realm.annotations.RealmModule;

/**
 * Indicates which {@link io.realm.RealmObject} belong to the user data realm database.
 * This module itself has no use.
 *
 * @author Alex Lardschneider
 */
@RealmModule(classes = {
        RecentRoute.class,
        FavoriteLine.class,
        FavoriteBusStop.class,
        PlannedTrip.class,
        Trip.class,
        Survey.class,
        FilterLine.class,
        TripToDelete.class
})
public class UserDataModule {
}