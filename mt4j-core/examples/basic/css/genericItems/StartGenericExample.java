package basic.css.genericItems;

import org.mt4j.MTApplication;


public class StartGenericExample extends MTApplication {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	public static void main(String[] args) {
		initialize();
	}
	
	
	@Override
	public void startUp() {
		addScene(new GenericExampleScene(this, "Integration  Test Scene"));
	}
}
