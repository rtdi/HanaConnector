package io.rtdi.bigdata.hanaconnector;

import java.io.File;
import java.util.List;

import io.rtdi.bigdata.connector.pipeline.foundation.exceptions.PropertiesException;
import io.rtdi.bigdata.connector.properties.ProducerProperties;

public class HanaProducerProperties extends ProducerProperties {

	private static final String PRODUCER_TOPICNAME = "producer.topic";
	private static final String PRODUCER_POLLINTERVAL = "producer.pollinterval";
	private static final String PRODUCER_SOURCE_SCHEMAS = "producer.source";

	public HanaProducerProperties(String name) throws PropertiesException {
		super(name);
		properties.addStringProperty(PRODUCER_TOPICNAME, "Target Topic", null, null, name, true);
		properties.addIntegerProperty(PRODUCER_POLLINTERVAL, "Poll interval", "Poll every n seconds", null, 60, true);
		properties.addMultiSchemaSelectorProperty(PRODUCER_SOURCE_SCHEMAS, "Source tables", "List of source tables to create data for", null, true);
	}

	public HanaProducerProperties(File dir, String name) throws PropertiesException {
		super(dir, name);
	}

	public String getTopicName() {
		return properties.getStringPropertyValue(PRODUCER_TOPICNAME);
	}
	
	public int getPollInterval() {
		return properties.getIntPropertyValue(PRODUCER_POLLINTERVAL);
	}
	
	public List<String> getSourceSchemas() throws PropertiesException {
		return properties.getMultiSchemaSelectorValue(PRODUCER_SOURCE_SCHEMAS);
	}
	
	public void setTopicName(String value) throws PropertiesException {
		properties.setProperty(PRODUCER_TOPICNAME, value);
	}

	public void setPollInterval(int value) throws PropertiesException {
		properties.setProperty(PRODUCER_POLLINTERVAL, value);
	}

	public void setSourceSchemas(List<String> value) throws PropertiesException {
		properties.setProperty(PRODUCER_SOURCE_SCHEMAS, value);
	}

}
