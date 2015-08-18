package org.mt4j.test.testUtil;


public abstract class TestRunnable implements Runnable{

	private boolean completed;
	
	public TestRunnable(){
		this.completed = false;
	}
	
	public void run(){
		try {
			runMTTestCode();
//			setCompleted();
		}
//		catch (Exception e) {
//			throw new AssertionFailedError();
//			System.out.println("-> Caught Exception: " + e);
//		}
		finally{
			setCompleted();
		}
	}
	
	abstract public void runMTTestCode();
	
	public void setCompleted(){
		this.completed = true;
	}
	
	public boolean isCompleted(){
		return this.completed;
	}
	
	
}
