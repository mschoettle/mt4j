package basic.css.menus;

import org.mt4j.MTApplication;


public class StartMenuExample extends MTApplication {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	public static void main(String[] args) {
		initialize();
	}
	
	
	@Override
	public void startUp() {
		addScene(new MenuExampleScene(this, "Integration  Test Scene"));
	}
}
