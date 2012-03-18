package org.mt4j.util.logging;


public class MTLoggerFactory {
//	private static MTLoggerFactory instance;
	private static ILogger logger;
	
	private MTLoggerFactory(){}
	
//	public static MTLoggerFactory getInstance(){
//		if (instance == null){
//			instance = new MTLoggerFactory();
//		}
//		return instance;
//	}
	
	
	public static ILogger getLogger(String name) {
		if (logger != null){
			return logger.createNew(name);
		}else{
			throw new NoLoggerProvidedException();
		}
	}
	
	
	public static void setLoggerProvider(ILogger logger){
		MTLoggerFactory.logger = logger;
	}
	

}
