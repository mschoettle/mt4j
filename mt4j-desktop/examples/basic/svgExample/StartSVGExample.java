package basic.svgExample;

import org.mt4j.MTApplication;

public class StartSVGExample extends MTApplication{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	public static void main(String args[]){
		initialize();
	}
	
	@Override
	public void startUp(){
		this.addScene(new SVGScene(this, "Svg scene"));
	}
}
