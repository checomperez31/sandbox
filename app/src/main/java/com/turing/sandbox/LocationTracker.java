package com.turing.sandbox;

import android.location.Location;

/**
 * Created by Sergio on 15/10/2017.
 */

public interface LocationTracker {
    public interface LocationUpdateListener{
        public void onUpdate(Location oldLoc, long oldTime, Location newLoc, long newTime);
    }

    public void start();
    public void start(LocationUpdateListener update);

    public void stop();

    public boolean hasLocation();

    public boolean hasPossiblyStaleLocation();

    public Location getLocation();

    public Location getPossiblyStaleLocation();

}
