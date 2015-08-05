package advanced.models3D;

import org.mt4j.MTApplication;


public class StartModelExample extends MTApplication{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	public static void main(String args[]){
		initialize();
	}
	
	@Override
	public void startUp(){
		this.addScene(new Models3DScene(this, "3D Model scene"));
	}
}
