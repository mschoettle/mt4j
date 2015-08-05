package org.mt4jx.input.inputProcessors.componentProcessors.Group3DProcessorNew.FingerTapGrouping;

import java.util.ArrayList;
import java.util.HashMap;

import org.mt4j.components.MTComponent;
import org.mt4j.input.inputData.InputCursor;

public interface FingerTapCursorMethods {
	public void tapPress(FingerTapSelectionManager selManager,MTComponent comp,InputCursor c);
	
	public void tapRelease(FingerTapSelectionManager selManager,MTComponent comp,InputCursor c);
}
