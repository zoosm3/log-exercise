package hoge;

import java.util.Map;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.ThreadContext;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.impl.Log4jLogEvent;
import org.apache.logging.log4j.message.Message;

public class CustomLogEvent extends Log4jLogEvent implements LogEvent {
	private String userId;
	private String applicationName;
	private static final long serialVersionUID = 1L;

	public CustomLogEvent(String loggerName, Marker marker, String loggerFQCN, Level level, Message message,
			Throwable t, Map<String, String> mdc, ThreadContext.ContextStack ndc, 
			String threadName,
			StackTraceElement location, long timestamp) {
		super(loggerName, marker, loggerFQCN, level, message, t, mdc, ndc, threadName, location, timestamp);
	} 
}