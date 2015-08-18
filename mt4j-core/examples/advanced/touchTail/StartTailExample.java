package advanced.touchTail;

import org.mt4j.MTApplication;


public class StartTailExample extends MTApplication{
	private static final long serialVersionUID = 1L;

	public static void main(String args[]){
		initialize();
	}
	
	@Override
	public void startUp(){
		this.addScene(new TouchTailScene(this, "Touch Tail Scene"));
	}
}
