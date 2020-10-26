package io.rtdi.bigdata.hanaconnector;

import io.rtdi.bigdata.connector.pipeline.foundation.exceptions.PropertiesException;
import io.rtdi.bigdata.connector.properties.ConnectionProperties;

public class HanaConnectionProperties extends ConnectionProperties {

	private static final String JDBCURL = "hana.jdbcurl";
	private static final String USERNAME = "hana.username";
	private static final String PASSWORD = "hana.password";

	public HanaConnectionProperties(String name) {
		super(name);
		properties.addStringProperty(JDBCURL, "JDBC URL", "The JDBC URL to use for connecting to the Hana system", "sap-icon://target-group", "jdbc:sap://localhost:3xx15/yy", true);
		properties.addStringProperty(USERNAME, "Username", "Hana database username", "sap-icon://target-group", null, true);
		properties.addPasswordProperty(PASSWORD, "Password", "Password", "sap-icon://target-group", null, true);
	}

	public String getJDBCURL() {
		return properties.getStringPropertyValue(JDBCURL);
	}
	
	public String getUsername() {
		return properties.getStringPropertyValue(USERNAME);
	}
	
	public String getPassword() {
		return properties.getPasswordPropertyValue(PASSWORD);
	}
	
	public void setJDBCURL(String value) throws PropertiesException {
		properties.setProperty(JDBCURL, value);
	}

	public void setUsername(String value) throws PropertiesException {
		properties.setProperty(USERNAME, value);
	}

	public void setPassword(String value) throws PropertiesException {
		properties.setProperty(PASSWORD, value);
	}

}
