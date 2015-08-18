package org.mt4jx.input.inputProcessors.componentProcessors.Group3DProcessorNew.FingerTapGrouping;

import org.mt4j.components.MTComponent;
import org.mt4j.input.inputData.InputCursor;

public interface FingerTapTransitions {
	
	public void tapRelease(FingerTapSelection sel,InputCursor cursor,MTComponent comp);
	
	public void tapPress(FingerTapSelection sel,InputCursor cursor,MTComponent comp);
}
