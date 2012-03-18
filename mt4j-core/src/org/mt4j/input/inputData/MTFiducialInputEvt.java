/***********************************************************************
 * mt4j Copyright (c) 2008 - 2009 C.Ruff, Fraunhofer-Gesellschaft All rights reserved.
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
package org.mt4j.input.inputData;

import org.mt4j.input.inputSources.AbstractInputSource;

/**
 * The Class MTFiducialInputEvt.
 * @author Christopher Ruff
 */
public class MTFiducialInputEvt extends AbstractCursorInputEvt {
	
	/** The r_accel. */
	private float 
	angle, 
	x_speed, 
	y_speed,
	r_speed, 
	m_accel, 
	r_accel;
	private int fiducial_id;
	
	/**
	 * Instantiates a new mT fiducial input evt.
	 * 
	 * @param source the source
	 * @param positionX the position x
	 * @param positionY the position y
	 * @param id the id
	 * @param m the m
	 */
	public MTFiducialInputEvt(AbstractInputSource source, 
			float positionX,
			float positionY, 
			int id,
			InputCursor m,
			int fiducial_id
	) {
		this(source, positionX, positionY, id, m, fiducial_id, 0, 0, 0, 0, 0, 0);
	}
	
	/**
	 * Instantiates a new mT fiducial input evt.
	 * 
	 * @param source the source
	 * @param positionX the position x
	 * @param positionY the position y
	 * @param id the id
	 * @param m the m
	 * @param angle the angle
	 * @param x_speed the x_speed
	 * @param y_speed the y_speed
	 * @param r_speed the r_speed
	 * @param m_accel the m_accel
	 * @param r_accel the r_accel
	 */
	public MTFiducialInputEvt(AbstractInputSource source, 
			float positionX,
			float positionY, 
			int id,
			InputCursor m,
			int fiducial_id,
			float angle,
			float x_speed,
			float y_speed,
			float r_speed,
			float m_accel,
			float r_accel
	) {
		super(source, positionX, positionY, id, m);
		this.fiducial_id = fiducial_id;
		this.angle   = angle;
		this.x_speed = x_speed;
		this.y_speed = y_speed;
		this.r_speed = r_speed;
		this.m_accel = m_accel;
		this.r_accel = r_accel;
	}

	
	
	
	public int getFiducialId() {
		return fiducial_id;
	}

	/**
	 * Gets the angle.
	 * 
	 * @return the angle
	 */
	public float getAngle() {
		return angle;
	}

	/**
	 * Gets the x_speed.
	 * 
	 * @return the x_speed
	 */
	public float getX_speed() {
		return x_speed;
	}

	/**
	 * Gets the y_speed.
	 * 
	 * @return the y_speed
	 */
	public float getY_speed() {
		return y_speed;
	}

	/**
	 * Gets the r_speed.
	 * 
	 * @return the r_speed
	 */
	public float getR_speed() {
		return r_speed;
	}

	/**
	 * Gets the m_accel.
	 * 
	 * @return the m_accel
	 */
	public float getM_accel() {
		return m_accel;
	}

	/**
	 * Gets the r_accel.
	 * 
	 * @return the r_accel
	 */
	public float getR_accel() {
		return r_accel;
	}

	/* (non-Javadoc)
	 * @see org.mt4j.input.inputData.MTInputPositionEvt#clone()
	 */
	@Override
	public Object clone() throws CloneNotSupportedException {
		return new MTFiducialInputEvt((AbstractInputSource) this.getSource(), this.getX(), this.getY(), this.getId(), this.getCursor(), this.getFiducialId(), this.getAngle(), this.getX_speed(), this.getY_speed(), this.getR_speed(), this.getM_accel(), this.getR_accel() );
	}

//	/** The Constant FIDUCIAL_ADDED. */
//	public static final int FIDUCIAL_ADDED = 3;
//	
//	/** The Constant FIDUCIAL_UPDATED. */
//	public static final int FIDUCIAL_UPDATED = 4;
//	
//	/** The Constant FIDUCIAL_REMOVED. */
//	public static final int FIDUCIAL_REMOVED = 5;
	
	
}
