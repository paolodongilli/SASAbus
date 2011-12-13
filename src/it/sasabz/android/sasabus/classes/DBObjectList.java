/**
 *
 * DBObjectList.java
 * 
 * Created: Dez 13, 2011 16:01:03 PM
 * 
 * Copyright (C) 2011 Markus Windegger
 * 
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

package it.sasabz.android.sasabus.classes;

import java.util.Iterator;
import java.util.Vector;

import android.text.StaticLayout;



public abstract class DBObjectList {

	protected static Vector <DBObject> list = null;
	
	
	/**
	 * This static method allows you to add an element which is an instance of DBObject to the local vector 
	 * @param object to add to the vector
	 * @return a vector of the new list
	 * @throws Exception occours when get getList returns an error (which means, that the getList method isn't hardcoded yet)
	 */
	public static Vector <DBObject> add(DBObject object) throws Exception
	{
		Vector <DBObject> list = getList();
		list.add(object);
		DBObjectList.list = list;
		return DBObjectList.list;
	}
	
	/**
	 * This static method allows you to delete an object from the vector
	 * @param identifyer is the integer of the object which identifies the object in the database
	 * @return a vector of the new list
	 * @throws Exception occours when getList returns an error
	 */
	public static Vector <DBObject> remove(int identifyer) throws Exception
	{
		Vector <DBObject> list = getList();
		Iterator<DBObject> iterator = list.iterator();
		//Searching the object in the vector an then removing
		boolean found = false;
		while(iterator.hasNext() && !found)
		{
			DBObject listelement = iterator.next();
			if (listelement.getId() == identifyer)
			{
				found = true;
				list.remove(listelement);
			}
		}
		DBObjectList.list = list;
		return DBObjectList.list;
	}
	
	
	/**
	 * This static method returns an object of the vector, which has the id equal to identifyer
	 * @param identifyer is the id of the searched object
	 * @return the object from the vector where the id = identifyer
	 * @throws Exception occours when getList returns an error
	 */
	public static DBObject getById(int identifyer) throws Exception
	{
		DBObject ret = null;
		Vector <DBObject> list = getList();
		Iterator <DBObject> iterator = list.iterator();
		//Searching the object in the Vector
		while (iterator.hasNext() && ret == null)
		{
			DBObject listelement = iterator.next();
			if(listelement.getId() == identifyer)
			{
				ret = listelement;
			}
		}
		return ret;
	}
	
	
	
	 /**                                                                                                                                                                                                          
	   * This function returns a vector of all the objects momentanly avaiable in the database                                                                                                                     
	   * @return a vector of objects if all goes right, alternativ it returns a MyError                                                                                                                              
	   */
	  public static  Vector <DBObject> getList() throws Exception
	  {
	    throw new Exception("The manually abstract method getList is not been hardcoded in the child class");
	  }


	
}
