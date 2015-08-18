package org.mt4j.util.logging;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;

public class Log4jLogger implements ILogger {
	private Logger logger;
	
	public Log4jLogger(){	}
	
	private Log4jLogger(String name){
		this.logger = Logger.getLogger(name);
		//System.out.println("Created logger: " + logger);
		SimpleLayout l = new SimpleLayout();
		ConsoleAppender ca = new ConsoleAppender(l);
		logger.addAppender(ca);
	}

	public void setLevel(int level) {
		switch (level) {
		case OFF:
			this.logger.setLevel(Level.OFF); 
			break;
		case ALL:
			this.logger.setLevel(Level.ALL); 
			break;
		case INFO:
			this.logger.setLevel(Level.INFO); 
			break;
		case DEBUG:
			this.logger.setLevel(Level.DEBUG); 
			break;
		case WARN:
			this.logger.setLevel(Level.WARN); 
			break;
		case ERROR:
			this.logger.setLevel(Level.ERROR); 
			break;
		default:
			break;
		}
	}

	public void info(Object message) {
		this.logger.info(message);
	}

	public void debug(Object message) {
		this.logger.debug(message);
	}

	public void warn(Object message) {
		this.logger.warn(message);
	}

	public void error(Object message) {
		this.logger.error(message);
	}

//	public ILogger getLogger(String name) {
//		this.logger = Logger.getLogger(name);
////		Logger logger = Logger.getLogger(name);
//		System.out.println("Created logger: " + logger);
//		SimpleLayout l = new SimpleLayout();
//		ConsoleAppender ca = new ConsoleAppender(l);
//		logger.addAppender(ca);
////		return new Log4jLogger(logger);
//		return this;
//	}
	
	public ILogger createNew(String name) {
		return new Log4jLogger(name);
	}
	

	public int getLevel() {
		Level level = this.logger.getLevel();
		if (level.equals(Level.OFF)){
			return ILogger.OFF;
		}else if (level.equals(Level.ALL)){
			return ILogger.ALL;
		}else if (level.equals(Level.INFO)){
			return ILogger.INFO;
		}else if (level.equals(Level.DEBUG)){
			return ILogger.DEBUG;
		}else if (level.equals(Level.WARN)){
			return ILogger.WARN;
		}else if (level.equals(Level.ERROR)){
			return ILogger.ERROR;
		}else{
			return -1;
		}
	}
	

}
