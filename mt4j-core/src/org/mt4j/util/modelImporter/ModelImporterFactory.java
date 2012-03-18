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
package org.mt4j.util.modelImporter;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import org.mt4j.components.visibleComponents.shapes.mesh.MTTriangleMesh;

import processing.core.PApplet;

/**
 * A factory for creating ModelImporter objects.
 * @author Christopher Ruff
 */
public abstract class ModelImporterFactory {
	
	/** The suffix to factory. */
	private static HashMap<String, Class<? extends ModelImporterFactory>> suffixToFactory;


	private static HashMap<String, Class<? extends ModelImporterFactory>> getMap(){
		if (suffixToFactory == null){
			suffixToFactory = new HashMap<String, Class<? extends ModelImporterFactory>>();
		}
		return suffixToFactory;
	}

	/**
	 * Gets the appropriate class for suffix.
	 * 
	 * @param fileSuffix the file suffix
	 * 
	 * @return the appropriate class for suffix
	 */
	private static Class<? extends ModelImporterFactory> getAppropriateClassForSuffix(String fileSuffix){
		return getMap().get(fileSuffix);
	}
	
	
	/**
	 * Registers a ModelImporterFactory with this class.
	 * The registered class has to extend the abstract ModelImporterFactory class!
	 * 
	 * @param fileSuffix the suffix of the files to be loaded by this factory (e.g. ".obj" or ".3ds")
	 * @param factory the factory to load the models with the specified suffix with. The factory has to be derived from ModelImporterFactory!
	 */
	public static void registerModelImporterFactory(String fileSuffix, Class<? extends ModelImporterFactory> factory){ 
		getMap().put(fileSuffix, factory);
	}
	
	
	/**
	 * Unregister a importer factory class for a file type.
	 * 
	 * @param factory the factory to load the models with the specified suffix with. The factory has to be derived from ModelImporterFactory!
	 */
	public static void unregisterModelImporterFactory(Class<? extends ModelImporterFactory> factory){ 
		Set<String> suffixesInHashMap = getMap().keySet();
		for (Iterator<String> iter = suffixesInHashMap.iterator(); iter.hasNext();) {
			String suffix = (String) iter.next();
			if (getMap().get(suffix).equals(factory)){
				getMap().remove(suffix);
			}
		}
	}
	
	
	/**
	 * Loads a model from a file and creates an array of meshes.
	 * <p>
	 * Some models store the texture information flipped on the y-Axis.
	 * This can be solved by setting <code>flipTextureY</code> to true.
	 * <br>Vertex normals will be created for the geometry according to the crease angle.
	 * <br>A crease angle of 180 will result in an all smoothed model, creating a vertex normal
	 * that is interpolated from all neighbor faces normals.
	 * <br>A crease angle of zero (0) will result in a flat shaded geometry. Only face normals will
	 * be used then.
	 * <br>A crease angle of 89 will create hard edges at 90 degree angle faces and smooth faces with
	 * less then 90 degree normals. This would be ideal to generate normals for cubes and models with
	 * many sharp 90 degree angles.
	 * <br>The best crease angle for a model has to be found by testing different crease angles.
	 * 
	 * @param pa the parent processing applet
	 * @param pathToModel the absolute path of the model file
	 * @param creaseAngle the crease angle, see method description for info
	 * @param flipTextureY flag whether or not to flip the texture vertical
	 * @param flipTextureX flag whether or not to flip the texture horizontal
	 * 
	 * @return the MT triangle mesh[]
	 * 
	 * @throws FileNotFoundException the file not found exception
	 * 
	 * an array filled with the meshes created during the loading process
	 */
	public static MTTriangleMesh[] loadModel(PApplet pa, String pathToModel, float creaseAngle, boolean flipTextureY, boolean flipTextureX){
		try {
			String suffix = getFileSuffix(pathToModel);
			ModelImporterFactory factory = getFactory(suffix);
			if (factory != null){
				return factory.loadModelImpl(pa, pathToModel, creaseAngle, flipTextureY, flipTextureX);	
			}
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} 
		return new MTTriangleMesh[]{};
	}
	
	

	/**
	 * Gets the factory.
	 * 
	 * @param fileSuffix the suffix of the file to be loaded by the factory (e.g. ".obj" or ".3ds")
	 * 
	 * @return the factory
	 * 
	 * @throws IllegalAccessException the illegal access exception
	 * @throws InstantiationException the instantiation exception
	 * @throws RuntimeException the runtime exception
	 * 
	 * the factory to load the model with if a appropriate factory was found
	 */
	private static ModelImporterFactory getFactory(String fileSuffix) throws IllegalAccessException, InstantiationException{
		Class<? extends ModelImporterFactory> modelFactoryClass = getAppropriateClassForSuffix(fileSuffix);
		//Get, create instance and return a appropriate factory object if found
		if (modelFactoryClass != null){
			try {
				ModelImporterFactory modelFactory = modelFactoryClass.newInstance();
				System.out.println("Found and created model factory for handling files: \"" + fileSuffix + "\"" + " Factory: " + modelFactory.getClass().getName());
				return modelFactory;
			}catch (InstantiationException e) {
				throw new InstantiationException("The ModelImporterFactory \"" + modelFactoryClass.getName() + "\" has to have a constructor without any parameters!");
			} catch (IllegalAccessException e) {
				throw new IllegalAccessException();
			} 
		}else{
			throw new RuntimeException("No appropriate factory class was found for handling files: \"" + fileSuffix + "\"");
		}
	}
	
	
	/**
	 * Gets the file suffix.
	 * 
	 * @param pathToFile the path to file
	 * 
	 * @return the file suffix
	 */
	private static String getFileSuffix(String pathToFile){
		int indexOfPoint = pathToFile.lastIndexOf(".");
		String suffix;
		if (indexOfPoint != -1){
			suffix = pathToFile.substring(indexOfPoint, pathToFile.length());
			suffix = suffix.toLowerCase();
		}else{
			suffix = "";
		}
		return suffix;
	}
	
	
	
	/**
	 * The model loading will put out debug information if set to true.
	 * 
	 * @param debug the debug
	 */
	public abstract void setDebug(boolean debug);
	
	
	/**
	 * Loads a model from a file and creates an array of meshes.
	 * <p>
	 * Some models store the texture information flipped on the y-Axis.
	 * This can be solved by setting <code>flipTextureY</code> to true.
	 * <br>Vertex normals will be created for the geometry according to the crease angle.
	 * <br>A crease angle of 180 will result in an all smoothed model, creating a vertex normal
	 * that is interpolated from all neighbor faces normals.
	 * <br>A crease angle of zero (0) will result in a flat shaded geometry. Only face normals will
	 * be used then.
	 * <br>A crease angle of 89 will create hard edges at 90 degree angle faces and smooth faces with
	 * less then 90 degree normals. This would be ideal to generate normals for cubes and models with
	 * many sharp 90 degree angles.
	 * <br>The best crease angle for a model has to be found by testing different crease angles.
	 * 
	 * @param pa the parent processing applet
	 * @param pathToModel the absolute path of the model file
	 * @param creaseAngle the crease angle, see method description for info
	 * @param flipTextureY flag whether or not to flip the texture vertical
	 * @param flipTextureX flag whether or not to flip the texture horizontal
	 * 
	 * @return the MT triangle mesh[]
	 * 
	 * @throws FileNotFoundException the file not found exception
	 * 
	 * an array filled with the meshes created during the loading process
	 */
	public abstract MTTriangleMesh[] loadModelImpl(PApplet pa, String pathToModel, float creaseAngle, boolean flipTextureY, boolean flipTextureX) throws FileNotFoundException;
	


}
