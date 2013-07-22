/**
 *
 * Decompress.java
 * 
 * Created: Feb 3, 2011 9:01:58 PM
 * 
 * Copyright (C) 2011 Paolo Dongilli
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
package it.sasabz.sasabus.data;

import android.util.Log;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Decompress {
	private String zipFile;
	private String location;

	/**
	 * this constructor creates a object of the type Decompress
	 * @param zipFile is the name of the zip-file to decompress
	 * @param location is the location where the zip-file is located on the sd-card
	 */
	public Decompress(String zipFile, String location) {
		this.zipFile = zipFile;
		this.location = location;

		dirChecker("");
	}

	/**
	 * this method unzips the zip-file.
	 */
	public void unzip() {
		try {
			FileInputStream fin = new FileInputStream(zipFile);
			ZipInputStream zin = new ZipInputStream(fin);
			ZipEntry ze = null;
			byte[] buffer = new byte[1024];
			int length;
			while ((ze = zin.getNextEntry()) != null) {
				Log.v("Decompress", "Unzipping " + ze.getName());

				if (ze.isDirectory()) {
					dirChecker(ze.getName());
				} else {
					FileOutputStream fout = new FileOutputStream(location
							+ File.separator + ze.getName());
					while ((length = zin.read(buffer)) > 0) {
						fout.write(buffer, 0, length);
					}

					zin.closeEntry();
					fout.close();
				}
			}
			zin.close();
		} catch (Exception e) {
			Log.e("Decompress", "unzip", e);
		}
	}

	/**
	 * this method checks if the target directory exists, else
	 * the directory were created
	 * @param dir is the name of the directory to check/create
	 */
	private void dirChecker(String dir) {
		File f = new File(location + dir);

		if (!f.isDirectory()) {
			f.mkdirs();
		}
	}
}
