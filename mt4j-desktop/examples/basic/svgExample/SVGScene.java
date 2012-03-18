package basic.svgExample;

import org.mt4j.AbstractMTApplication;
import org.mt4j.components.visibleComponents.widgets.MTSvg;
import org.mt4j.input.inputProcessors.globalProcessors.CursorTracer;
import org.mt4j.sceneManagement.AbstractScene;
import org.mt4j.util.MTColor;
import org.mt4j.util.math.Vector3D;


public class SVGScene extends AbstractScene {
	
//	private String svgPath = System.getProperty("user.dir")+File.separator + "examples"+File.separator +"basic"+ File.separator + "svgExample"+ File.separator + "data" + File.separator;
	private String svgPath =  "basic" + AbstractMTApplication.separator + "svgExample" + AbstractMTApplication.separator + "data" + AbstractMTApplication.separator;

	public SVGScene(AbstractMTApplication mtApplication, String name) {
		super(mtApplication, name);
		
		this.setClearColor(new MTColor(255, 255, 255, 255));
		//Show touches
		this.registerGlobalInputProcessor(new CursorTracer(mtApplication, this));
		
		MTSvg svg = new MTSvg(mtApplication, svgPath + "windmill.svg");
		svg.setPositionGlobal(new Vector3D(mtApplication.width/2, mtApplication.height/2,0));
		this.getCanvas().addChild(svg);
		
		MTSvg butterFly = new MTSvg(mtApplication, svgPath + "butterfly.svg");
		butterFly.setPositionGlobal(new Vector3D(300, 100,0));
		this.getCanvas().addChild(butterFly);
		
		this.getCanvas().addChild(new MTSvg(mtApplication, svgPath + "primitives.svg"));
	}

	
	public void onEnter() {}
	
	public void onLeave() {}

}
