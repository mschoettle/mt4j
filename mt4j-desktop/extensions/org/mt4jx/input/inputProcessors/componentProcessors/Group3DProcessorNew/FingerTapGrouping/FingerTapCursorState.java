package org.mt4jx.input.inputProcessors.componentProcessors.Group3DProcessorNew.FingerTapGrouping;

import org.mt4j.components.MTComponent;
import org.mt4j.input.inputData.InputCursor;

public enum FingerTapCursorState implements FingerTapCursorMethods {

	OBJECTWITHNOTAP
	{

		public void tapPress(FingerTapSelectionManager selManager,MTComponent comp,InputCursor c) {
			
			selManager.setLockedCursorForComponent(comp, c);
			selManager.setCursorStateForComponent(comp, OBJECTWITHLOCKEDCURSOR);
		}

		public void tapRelease(FingerTapSelectionManager selManager,MTComponent comp,InputCursor c) {
			//cannot be			
		}

	
	},
	
	OBJECTWITHLOCKEDCURSOR
	{

		public void tapPress(FingerTapSelectionManager selManager,MTComponent comp,InputCursor c) {
			selManager.addUnUsedCursorsForComponent(comp, c);
			selManager.setCursorStateForComponent(comp, OBJECTWITHONEUNUSEDCURSOR);
		}

		public void tapRelease(FingerTapSelectionManager selManager,MTComponent comp,InputCursor c) {
			if(selManager.getLockedCursorForComponent(comp)==c)
			{
				selManager.setLockedCursorForComponent(comp, null);
				selManager.setCursorStateForComponent(comp, OBJECTWITHNOTAP);
			}else
			{
				//should not happen
			}
		}		
	},
	
	OBJECTWITHONEUNUSEDCURSOR
	{

		public void tapPress(FingerTapSelectionManager selManager,
				MTComponent comp, InputCursor c) {
			selManager.addUnUsedCursorsForComponent(comp, c);
			selManager.setCursorStateForComponent(comp, OBJECTWITHMANYUNUSEDCURSORS);
			
		}

		public void tapRelease(FingerTapSelectionManager selManager,
				MTComponent comp, InputCursor c) {
			if(selManager.getLockedCursorForComponent(comp)==c)
			{
				selManager.setLockedCursorForComponent(comp, selManager.getUnUsedCursorsForComponent(comp).get(0));
				selManager.removeUnUsedCursorsForComponent(comp, selManager.getUnUsedCursorsForComponent(comp).get(0));
				selManager.setCursorStateForComponent(comp, OBJECTWITHLOCKEDCURSOR);
			}else
			{
				selManager.removeUnUsedCursorsForComponent(comp, c);
				selManager.setCursorStateForComponent(comp, OBJECTWITHLOCKEDCURSOR);				
			}
			
		}
		
	},
	
	OBJECTWITHMANYUNUSEDCURSORS
	{

		public void tapPress(FingerTapSelectionManager selManager,
				MTComponent comp, InputCursor c) {
			selManager.addUnUsedCursorsForComponent(comp, c);			
		}

		public void tapRelease(FingerTapSelectionManager selManager,
				MTComponent comp, InputCursor c) {
			selManager.removeUnUsedCursorsForComponent(comp, c);
			if(selManager.getUnUsedCursorsForComponent(comp).size()==1)
			{
				selManager.setCursorStateForComponent(comp, OBJECTWITHONEUNUSEDCURSOR);		
			}			
		}
		
	}
	
	
}
