/**
 *
 * SplashScreen.java
 * 
 * Created: Feb 3, 2011 09:04:06 AM
 * 
 * Copyright (C) 2011 Paolo Dongilli and Markus Windegger
 *
 * This file is part of SasaBus.

 * SasaBus is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SasaBus is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with SasaBus.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */

package it.sasabz.android.sasabus;

import it.sasabz.android.sasabus.R;
import it.sasabz.android.sasabus.R.layout;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;

public class SplashScreen extends Activity {
    protected boolean active = true;
    protected int splashTime = 1000;
    
    public Context getThis()
    {
    	return this;
    }
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        
        
        // thread for displaying the SplashScreen
        Thread splashTread = new Thread() {
        	
            @Override
            public void run() {
                try {
                    int waited = 0;
                    while(active && (waited < splashTime)) {
                        sleep(100);
                        if(active) {
                            waited += 100;
                        }
                    }
                } catch(InterruptedException e) {
                    // do nothing
                } finally {
                    finish();

                    // Run next activity
                	Intent checkDatabase = new Intent(getThis(), HomeActivity.class);
                	startActivity(checkDatabase);
                }
            }
        };
        splashTread.start();

        
    
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            active = false;
        }
        return true;
    }
}