package advanced.mtShell;

import org.mt4j.MTApplication;


public class StartMTShell extends MTApplication {
	private static final long serialVersionUID = 1L;

	public static void main(String args[]){
		initialize();
	}
	
	@Override
	public void startUp(){
		this.addScene(new MTShellScene(this, "Multi-Touch Shell Scene"));
	}
	
}
