
package it.sasabz.sasabus.ui.busschedules;

import it.sasabz.sasabus.data.realtime.AsyncResponse;
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

      this.countDownLatch.await(3, TimeUnit.SECONDS);

      return this.response;
   }

   @Override
   public void onResponse(PositionsResponse t)
   {
      this.response = t;
      this.countDownLatch.countDown();
   }
}
