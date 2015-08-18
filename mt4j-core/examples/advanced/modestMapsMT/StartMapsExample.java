package advanced.modestMapsMT;

import org.mt4j.MTApplication;

public class StartMapsExample extends MTApplication{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	public static void main(String args[]){
		initialize();
	}
	
	@Override
	public void startUp(){
		this.addScene(new MapsScene(this, "Map scene"));
	}
}
