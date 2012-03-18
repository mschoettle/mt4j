package org.mt4j.util.logging;

public interface ILogger {
	public static final int OFF = 0;
	public static final int ALL = 1;
	public static final int INFO = 2;
	public static final int DEBUG = 3;
	public static final int WARN = 4;
	public static final int ERROR = 5;
	
	
	public void setLevel(int level);
	
	public int getLevel();
	
	public void info(Object msg);

	public void debug(Object msg);
	
	public void warn(Object msg);
	
	public void error(Object msg);

	public ILogger createNew(String name);
	
	//public void setOutputChannel(int channel);

}

