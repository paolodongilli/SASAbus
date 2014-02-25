/*
 * SASAbus - Android app for SASA bus open data
 *
 * BusLineGroupException.java
 *
 * Created: Feb 20, 2014 15:00:00 AM
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

public class BusLineGroupException
{
   int      LI_NR;
   String[] areas;

   public BusLineGroupException(int lI_NR, String[] areas)
   {
      super();
      this.LI_NR = lI_NR;
      this.areas = areas;
   }

}
