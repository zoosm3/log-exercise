package hoge;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.Deflater;

import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractOutputStreamAppender;
import org.apache.logging.log4j.core.appender.rolling.DefaultRolloverStrategy;
import org.apache.logging.log4j.core.appender.rolling.RollingFileManager;
import org.apache.logging.log4j.core.appender.rolling.RolloverStrategy;
import org.apache.logging.log4j.core.appender.rolling.TriggeringPolicy;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginConfiguration;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.impl.Log4jLogEvent;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.apache.logging.log4j.core.net.Advertiser;
import org.apache.logging.log4j.core.util.Booleans;
import org.apache.logging.log4j.core.util.Integers;

@Plugin(name = "MyRollingFile", category = "Core", elementType = "appender", printObject = true)
public class MyRollingFileAppender extends AbstractOutputStreamAppender<RollingFileManager> {
	private static final int DEFAULT_BUFFER_SIZE = 8192;
	private static final long serialVersionUID = 1L;
	private final String fileName;
	private final String filePattern;
	private Object advertisement;
	private final Advertiser advertiser;

	private MyRollingFileAppender(final String name, final Layout<? extends Serializable> layout, final Filter filter,
			final RollingFileManager manager, final String fileName, final String filePattern,
			final boolean ignoreExceptions, final boolean immediateFlush, final Advertiser advertiser) {
		super(name, layout, filter, ignoreExceptions, immediateFlush, manager);
		if (advertiser != null) {
			final Map<String, String> configuration = new HashMap<String, String>(layout.getContentFormat());
			configuration.put("contentType", layout.getContentType());
			configuration.put("name", name);
			advertisement = advertiser.advertise(configuration);
		}
		this.fileName = fileName;
		this.filePattern = filePattern;
		this.advertiser = advertiser;
	}

	@Override
	public void append(final LogEvent logEvent) {
		getManager().checkRollover(logEvent);
		System.out.println("[Test]" + logEvent.getMessage().getFormattedMessage().replaceAll("\n",""));
		
		super.append(logEvent);
	}

	@PluginFactory
	public static MyRollingFileAppender createAppender(@PluginAttribute("fileName") final String fileName,
			@PluginAttribute("filePattern") final String filePattern, @PluginAttribute("append") final String append,
			@PluginAttribute("name") final String name, @PluginAttribute("bufferedIO") final String bufferedIO,
			@PluginAttribute("bufferSize") final String bufferSizeStr,
			@PluginAttribute("immediateFlush") final String immediateFlush,
			@PluginElement("Policy") final TriggeringPolicy policy,
			@PluginElement("Strategy") RolloverStrategy strategy,
			@PluginElement("Layout") Layout<? extends Serializable> layout,
			@PluginElement("Filter") final Filter filter, @PluginAttribute("ignoreExceptions") final String ignore,
			@PluginAttribute("advertise") final String advertise,
			@PluginAttribute("advertiseURI") final String advertiseURI,
			@PluginConfiguration final Configuration config) {
		final boolean isAppend = Booleans.parseBoolean(append, true);
		final boolean ignoreExceptions = Booleans.parseBoolean(ignore, true);
		final boolean isBuffered = Booleans.parseBoolean(bufferedIO, true);
		final boolean isFlush = Booleans.parseBoolean(immediateFlush, true);
		final boolean isAdvertise = Boolean.parseBoolean(advertise);
		final int bufferSize = Integers.parseInt(bufferSizeStr, DEFAULT_BUFFER_SIZE);
		if (!isBuffered && bufferSize > 0) {
			LOGGER.warn("The bufferSize is set to {} but bufferedIO is not true: {}", bufferSize, bufferedIO);
		}
		if (name == null) {
			LOGGER.error("No name provided for FileAppender");
			return null;
		}
		if (fileName == null) {
			LOGGER.error("No filename was provided for FileAppender with name " + name);
			return null;
		}
		if (filePattern == null) {
			LOGGER.error("No filename pattern provided for FileAppender with name " + name);
			return null;
		}
		if (policy == null) {
			LOGGER.error("A TriggeringPolicy must be provided");
			return null;
		}
		if (strategy == null) {
			strategy = DefaultRolloverStrategy.createStrategy(null, null, null,
					String.valueOf(Deflater.DEFAULT_COMPRESSION), config);
		}
		if (layout == null) {
			layout = PatternLayout.createDefaultLayout();
		}
		final RollingFileManager manager = RollingFileManager.getFileManager(fileName, filePattern, isAppend,
				isBuffered, policy, strategy, advertiseURI, layout, bufferSize);
		if (manager == null) {
			return null;
		}
		return new MyRollingFileAppender(name, layout, filter, manager, fileName, filePattern, ignoreExceptions,
				isFlush, isAdvertise ? config.getAdvertiser() : null);
	}
}
