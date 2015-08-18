/***********************************************************************
 * mt4j Copyright (c) 2008 - 2010 Christopher Ruff, Fraunhofer-Gesellschaft All rights reserved.
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
package org.mt4j.test;

import java.awt.Rectangle;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;

import junit.framework.AssertionFailedError;
import junit.framework.TestCase;

import org.mt4j.AbstractMTApplication;
import org.mt4j.sceneManagement.Iscene;
import org.mt4j.test.testUtil.TestRunnable;

public abstract class AbstractWindowTestcase extends TestCase 
implements UncaughtExceptionHandler 
{
	private boolean startUpRun;
	private AbstractMTApplication app;
//	private List<AssertionFailedError> errors;
	private List<Throwable> errors;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		System.out.println("-> setUp()");
		
//		System.out.println("Default Uncaught ExceptionHandler: " + Thread.getDefaultUncaughtExceptionHandler());
		Thread.setDefaultUncaughtExceptionHandler(this);
		
		startUpRun = false;
		
		//Creating MTApplication without extending it 
		//because we have to extend TestCase
		JFrame f = new JFrame();
		f.setBounds(new Rectangle(800,600));
		f.setVisible(true);
		
		//Create a dummy application
		app = new TestDummyMTApplication();
		app.init();
		
		//Add it to the JFrame
		f.add(app);
		app.frame = f;
		
		//List for errors caught in other thread (e.g. the animation thread)
		errors = new ArrayList<Throwable>();
		
		while (!startUpRun){
			System.out.println("Sleeping test thread until MTApplication's startUp() executed...");
			try {
				Thread.sleep(350);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println("...Done.");
	}
		
	
	public void runTest(TestRunnable testRunnable){
		this.getMTApplication().invokeLater(testRunnable);
		while (!testRunnable.isCompleted()){
			System.out.println("Waiting for Processing thread to execute the test...");
			try {
				Thread.sleep(3500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println("...Done.");
	}
	
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		System.out.println("-> tearDown()");
		
		/*
		//Go through recieved AssertionFailedErrors and throw them 
		for (AssertionFailedError assertionFailedError : this.errors){
//			throwable.printStackTrace();
//			fail(throwable.getMessage());
			
//			AssertionFailedError err = new AssertionFailedError();
//			err.initCause(throwable);
//			throw err;
			
			throw assertionFailedError;
		}
		*/
		
		for (Throwable throwable : this.errors){
//			throwable.printStackTrace();
//			fail(throwable.getMessage());
			
			if (throwable instanceof AssertionFailedError){
				throw (AssertionFailedError)throwable;
			}
			else if (throwable instanceof RuntimeException){
				((RuntimeException)throwable).printStackTrace();
				throw (RuntimeException)throwable;
			} 
		}
		
//		final MTApplication appToDestroy = getMTApplication();
//		TestRunnable test = new TestRunnable() {
//			@Override
//			public void runMTTestCode() {
//				Iscene[] scenes = appToDestroy.getScenes();
//				for (int i = 0; i < scenes.length; i++) {
//					Iscene iscene = scenes[i];
//					if (!iscene.destroy()){
//						iscene.getCanvas().destroy();
//					}
//				}
////				appToDestroy.exit(); //probably calls System.exit() so further test wont get exectuted..
//			}
//		};
//		runTest(test);
		
		final AbstractMTApplication appToDestroy = getMTApplication();
		appToDestroy.invokeLater(new Runnable() {
			
			public void run() {
				Iscene[] scenes = appToDestroy.getScenes();
                for (Iscene iscene : scenes) {
                    if (!iscene.destroy()) {
                        iscene.getCanvas().destroy();
                    }
                }
			}
		});
//		getMTApplication().exit();
		
	}
	
	
	public void uncaughtException(Thread thread, final Throwable throwable) {
		System.out.println("A uncaught exception was thrown -> Thread: " + thread + " Throwable: " + throwable);
		if (throwable instanceof AssertionFailedError) {
			AssertionFailedError ae = (AssertionFailedError) throwable;
			ae.printStackTrace();
//			fail(ae.getMessage()); //doesent work here because its in the animation thread rather than in the junit main thread
//			fail();
			errors.add(ae);
//			app.exit();
//		}else if (throwable instanceof NullPointerException){
		}else{
			errors.add(throwable);
//			throwable.printStackTrace(); //TODO we also need to fail the test!
		}
	}
	
	private class TestDummyMTApplication extends AbstractMTApplication {
		private static final long serialVersionUID = 1L;
		@Override
		public void startUp() {
			inStartUp(this);
			startUpRun = true;
		}
	}
	
	public abstract void inStartUp(AbstractMTApplication app);
	
	
	public AbstractMTApplication getMTApplication(){
		return this.app;
	}

}
