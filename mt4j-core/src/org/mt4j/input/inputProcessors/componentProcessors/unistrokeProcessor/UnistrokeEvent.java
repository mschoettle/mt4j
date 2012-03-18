package org.mt4j.input.inputProcessors.componentProcessors.unistrokeProcessor;


import org.mt4j.components.interfaces.IMTComponent3D;
import org.mt4j.components.visibleComponents.shapes.MTPolygon;
import org.mt4j.input.inputData.InputCursor;
import org.mt4j.input.inputProcessors.IInputProcessor;
import org.mt4j.input.inputProcessors.MTGestureEvent;
import org.mt4j.input.inputProcessors.componentProcessors.unistrokeProcessor.UnistrokeUtils.UnistrokeGesture;

public class UnistrokeEvent extends MTGestureEvent{
	private MTPolygon visualization;
	private UnistrokeGesture gesture;
	private InputCursor cursor;

	public UnistrokeEvent(IInputProcessor source, int id, IMTComponent3D targetComponent, MTPolygon visualization, UnistrokeGesture gesture, InputCursor inputCursor) {
		super(source, id, targetComponent);
		this.visualization = visualization;
		this.gesture = gesture;
		this.cursor = inputCursor;
	}

	public MTPolygon getVisualization() {
		return this.visualization;
	}
	
	public UnistrokeGesture getGesture(){
		return this.gesture;
	}

	public InputCursor getCursor() {
		return cursor;
	}


}
