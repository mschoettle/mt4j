/***********************************************************************
 * mt4j Copyright (c) 2008 - 2009, C.Ruff, Fraunhofer-Gesellschaft All rights reserved.
 *  
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 ***********************************************************************/
package org.mt4j.components;

/**
 * The Enum StateChange.
 * @author Christopher Ruff
 */
public enum StateChange {
	
	/**  COMPONENT destroyed. */
	COMPONENT_DESTROYED,
	
	/**  REMOVED from parent. */
	REMOVED_FROM_PARENT,
	
	/**  ADDED to parent. */
	ADDED_TO_PARENT,
	
	/**  CHILD added. */
	CHILD_ADDED,
	
	/*
	//TODO add TRANSFORMED? global matrix changed? local matrix changed? scaled? rotated? translated * 
 * - boundsChanged (local transforms + setVertices/geometry)
 * - translated, rotated, scaled (or only localTransformChanged/boundsChanged?)
 * - parentRelativeBoundsChanged   Y>y>> 
 * - globalTransformChanged
 * - parentTransformsChanged
 * - 
 */
	;

}
