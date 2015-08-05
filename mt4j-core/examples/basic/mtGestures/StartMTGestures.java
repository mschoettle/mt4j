package basic.mtGestures;

import org.mt4j.MTApplication;

public class StartMTGestures extends MTApplication {
	private static final long serialVersionUID = 1L;
	
	public static void main(String[] args) {
		initialize();
	}
	@Override
	public void startUp() {
		addScene(new MTGesturesExampleScene(this, "Multi-touch Gestures Example Scene"));
	}
}
