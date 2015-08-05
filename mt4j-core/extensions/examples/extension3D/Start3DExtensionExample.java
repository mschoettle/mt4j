package examples.extension3D;

import org.mt4j.MTApplication;
import org.mt4j.sceneManagement.Iscene;


public class Start3DExtensionExample extends MTApplication{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Iscene scene;

	public static void main(String args[]){		
		initialize();
	}
	
	/* (non-Javadoc)
	 * @see org.mt4j.MTApplication#startUp()
	 */
	/* (non-Javadoc)
	 * @see org.mt4j.MTApplication#startUp()
	 */
	/* (non-Javadoc)
	 * @see org.mt4j.MTApplication#startUp()
	 */
	/* (non-Javadoc)
	 * @see org.mt4j.MTApplication#startUp()
	 */
	/* (non-Javadoc)
	 * @see org.mt4j.MTApplication#startUp()
	 */
	/* (non-Javadoc)
	 * @see org.mt4j.MTApplication#startUp()
	 */
	/* (non-Javadoc)
	 * @see org.mt4j.MTApplication#startUp()
	 */
	/* (non-Javadoc)
	 * @see org.mt4j.MTApplication#startUp()
	 */
	/* (non-Javadoc)
	 * @see org.mt4j.MTApplication#startUp()
	 */
	/* (non-Javadoc)
	 * @see org.mt4j.MTApplication#startUp()
	 */
	/* (non-Javadoc)j
	 * @see org.mt4j.MTApplication#startUp()
	 */
	@Override
	public void startUp(){
		scene = new Extension3DScene(this, "3D Model scene");
		this.addScene(scene);
	}

	@Override
	public void draw() {
		super.draw();        
	}
	
}
