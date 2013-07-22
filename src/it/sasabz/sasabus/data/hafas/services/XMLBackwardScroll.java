package it.sasabz.sasabus.data.hafas.services;

import it.sasabz.sasabus.data.hafas.XMLConnectionRequest;
import it.sasabz.sasabus.ui.routing.OnlineShowFragment;

import java.util.Vector;

public class XMLBackwardScroll extends XMLConnectionRequestList{
Vector <XMLConnectionRequest> list;
	
	
	public XMLBackwardScroll(Vector <XMLConnectionRequest> list, OnlineShowFragment activity)
	{
		this.activity = activity;
		this.list = list;
	}
	
	
	
	@Override
	protected Vector<XMLConnectionRequest> doInBackground(Void... params) {
		XMLConnectionRequest firstElement = list.firstElement();
		XMLConnectionRequest backward = scrollBackward(firstElement);
		if(backward != null && !firstElement.getDeparture().getArrtime().equals(backward.getDeparture().getArrtime()) && 
				!firstElement.getArrival().getArrtime().equals(backward.getArrival().getArrtime()))
		{
			list.add(0, backward);
		}
		return list;
	}
}
