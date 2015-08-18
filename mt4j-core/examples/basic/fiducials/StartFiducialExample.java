package basic.fiducials;

import org.mt4j.MTApplication;

public class StartFiducialExample extends MTApplication {
	private static final long serialVersionUID = 1L;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		initialize();
	}
	
	@Override
	public void startUp() {
		this.addScene(new FiducialScene(this, "Fiducial Scene"));
	}

}
