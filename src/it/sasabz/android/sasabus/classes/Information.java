package it.sasabz.android.sasabus.classes;

public class Information {
	private String titel_de;
	
	private String titel_it;
	
	private String nachricht_de;
	
	private String nachricht_it;
	
	private String bacino;
	
	
	public String getBacino() {
		return bacino;
	}


	public void setBacino(String bacino) {
		this.bacino = bacino;
	}


	public Information(String titel_de, String titel_it, String nachricht_de, String nachricht_it)
	{
		this.titel_de = titel_de;
		this.titel_it = titel_it;
		this.nachricht_de = nachricht_de;
		this.nachricht_it = nachricht_it;
	}


	public String getTitel_de() {
		return titel_de;
	}


	public void setTitel_de(String titel_de) {
		this.titel_de = titel_de;
	}


	public String getTitel_it() {
		return titel_it;
	}


	public void setTitel_it(String titel_it) {
		this.titel_it = titel_it;
	}


	public String getNachricht_de() {
		return nachricht_de;
	}


	public void setNachricht_de(String nachricht_de) {
		this.nachricht_de = nachricht_de;
	}


	public String getNachricht_it() {
		return nachricht_it;
	}


	public void setNachricht_it(String nachricht_it) {
		this.nachricht_it = nachricht_it;
	}
	
	public void getNachricht()
	{
		
	}
	
	public void getTitel()
	{
		
	}
	
	
	
}
