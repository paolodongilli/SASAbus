/*
 * SASAbus - Android app for SASA bus open data
 *
 * OSMZipDownloadComplete.java
 *
 * Created: Mar 12, 2014 01:04:00 PM
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

package it.sasabz.sasabus.ui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class OSMZipDownloadComplete extends BroadcastReceiver
{
   MainActivity mainActivity;

   long         downloadId;
   File         zipFile;

   public OSMZipDownloadComplete(MainActivity context, long downloadId, File zipFile)
   {
      this.downloadId = downloadId;
      this.zipFile = zipFile;
      this.mainActivity = context;
   }

   @Override
   public void onReceive(Context context, Intent intent)
   {
      String action = intent.getAction();
      if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action))
      {
         long currDownloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0);
         if (currDownloadId == this.downloadId)
         {
            context.unregisterReceiver(this);
            new Thread(new Runnable()
            {
               @Override
               public void run()
               {
                  try
                  {
                     extractZipContent(OSMZipDownloadComplete.this.zipFile);
                  }
                  catch (IOException e)
                  {
                     OSMZipDownloadComplete.this.mainActivity.handleApplicationException(e);
                  }
               }
            }).start();

         }
      }
   }

   static void extractZipContent(File zipFile) throws IOException
   {
      FileInputStream fileInputStream = new FileInputStream(zipFile);
      ZipInputStream zipInputStream = new ZipInputStream(fileInputStream);

      ZipEntry zipEntry;

      int len;
      byte[] buf = new byte[100000];

      while ((zipEntry = zipInputStream.getNextEntry()) != null)
      {
         if (!zipEntry.isDirectory())
         {
            String name = zipEntry.getName();
            File tile = new File(zipFile.getParentFile(), name);
            tile.getParentFile().mkdirs();
            FileOutputStream fileOutputStream = new FileOutputStream(tile);

            while ((len = zipInputStream.read(buf)) > 0)
            {
               fileOutputStream.write(buf, 0, len);
            }

            fileOutputStream.close();
         }
      }

      zipInputStream.close();
      fileInputStream.close();
   }

}
