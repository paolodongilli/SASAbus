/**
 *
 * MD5Utils.java
 * 
 * Created: Jun 15, 2011 12:22:35 AM
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
package it.sasabz.sasabus.data;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import android.util.Log;


public class MD5Utils {
	
	/**
	 * this method calculate the md5-checksum of a file
	 * @param file is the filename for calculating the md5-checksum
	 * @return the calculated md5-checksum, if an error occurs null
	 */
	public static String calculateMD5(File file) {
		try {
			InputStream fin = new FileInputStream(file);
			java.security.MessageDigest md5er = MessageDigest
					.getInstance("MD5");
			byte[] buffer = new byte[1024];
			int read;
			do {
				read = fin.read(buffer);
				if (read > 0)
					md5er.update(buffer, 0, read);
			} while (read != -1);
			fin.close();
			byte[] digest = md5er.digest();
			if (digest == null)
				return null;
			String strDigest = "";
			for (int i = 0; i < digest.length; i++) {
				strDigest += Integer.toString((digest[i] & 0xff) + 0x100, 16)
						.substring(1);
			}
			Log.i("FileRetriever", "md5: " + strDigest + " length: "
					+ strDigest.length());
			return strDigest;
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * this method extracts the md5-checksum from the file,
	 * in other word reads the first 32 characters from a file which
	 * is the md5-checksum precalculated 
	 * @param file is the name of the file to extract the md5-checksum
	 * @return the extracted checksum
	 */
	public static String extractMD5(File file) {
		FileInputStream fis = null;
		DataInputStream dis = null;
		BufferedReader br = null;
		String md5 = null;

		try {
			fis = new FileInputStream(file);
			dis = new DataInputStream(fis);
			br = new BufferedReader(new InputStreamReader(dis));
			String strLine;
			if ((strLine = br.readLine()) != null) {
				md5 = strLine.substring(0, 32);
			}
			dis.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}


		return md5;
	}
	
	/**
	 * This method checks if the md5-checksums in the md5 file is the same of
	 * the checksum calculated from the file
	 * @param file is the fale to calculated the checksum
	 * @param md5File is the md5-checksum calculated on the server
	 * @return a boolean if the checksums are equal
	 */
	public static boolean checksumOK(File file, File md5File) {
		String md5 = extractMD5(md5File);
		String calculatedMD5 = calculateMD5(file);
		if (md5 != null && calculatedMD5 != null)
			return md5.equals(calculatedMD5);
		else
			return false;
	}
}
