package it.sasabz.sasabus.data.hafas.services;

import it.sasabz.sasabus.data.hafas.XMLConnectionRequest;
import it.sasabz.sasabus.ui.routing.OnlineShowFragment;

import java.util.Vector;

public class XMLForwardScroll extends XMLConnectionRequestList{

	Vector <XMLConnectionRequest> list;
	
	
	public XMLForwardScroll(Vector <XMLConnectionRequest> list, OnlineShowFragment activity)
	{
		this.activity = activity;
		this.list = list;
	}
	
	
	
	@Override
	protected Vector<XMLConnectionRequest> doInBackground(Void... params) {
		XMLConnectionRequest lastElement = list.lastElement();
		XMLConnectionRequest forward = scrollForward(lastElement);
		if(forward != null && !lastElement.getDeparture().getArrtime().equals(forward.getDeparture().getArrtime()) && 
				!lastElement.getArrival().getArrtime().equals(forward.getArrival().getArrtime()))
		{
			list.add(forward);
		}
		return list;
	}
	
	
	
}
