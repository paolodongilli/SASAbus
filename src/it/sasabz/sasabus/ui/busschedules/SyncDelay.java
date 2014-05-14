/*
 * SASAbus - Android app for SASA bus open data
 *
 * SyncDelay.java
 *
 * Created: May 9, 2014 02:14:00 PM
 *
 * Copyright (C) 2011-2014 Paolo Dongilli, Markus Windegger, Davide Montesin
 *
 * This file is part of SASAbus.
 *
 * SASAbus is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SASAbus is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with SASAbus.  If not, see <http://www.gnu.org/licenses/>.
 */

package it.sasabz.sasabus.ui.busschedules;

import it.sasabz.sasabus.data.realtime.AsyncResponse;
import it.sasabz.sasabus.data.realtime.Feature;
import it.sasabz.sasabus.data.realtime.PositionsResponse;
import it.sasabz.sasabus.data.realtime.SASAbusRealtimeDataClient;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import bz.davide.dmxmljson.json.HTTPAsyncJSONDownloader;
import bz.davide.dmxmljson.json.OrgJSONParser;

public class SyncDelay implements AsyncResponse<PositionsResponse>
{
   SASAbusRealtimeDataClient realtimeDataClient = new SASAbusRealtimeDataClient("http://sasatest.r3-gis.com/",
                                                                                new HTTPAsyncJSONDownloader(),
                                                                                new OrgJSONParser());

   PositionsResponse         response;
   CountDownLatch            countDownLatch;

   public PositionsResponse delay(String[] lineVariants) throws IOException, InterruptedException
   {

      if (lineVariants.length == 0)
      {
         this.response = new PositionsResponse();
         this.response.setFeatures(new Feature[0]);
         return this.response;
      }

      String parameter = "";
      for (int i = 0; i < lineVariants.length; i++)
      {
         if (i > 0)
         {
            parameter += ",";
         }
         parameter += lineVariants[i];
      }

      System.out.println("DELAY REQ: " + parameter);

      this.response = null;
      this.countDownLatch = new CountDownLatch(1);
      this.realtimeDataClient.positions(parameter, this);

      if (!this.countDownLatch.await(3, TimeUnit.SECONDS))
      {
         // In case of timeout
         this.response = new PositionsResponse();
         this.response.setFeatures(new Feature[0]);
      }

      return this.response;
   }

   @Override
   public void onResponse(PositionsResponse t)
   {
      this.response = t;
      this.countDownLatch.countDown();
   }
}
