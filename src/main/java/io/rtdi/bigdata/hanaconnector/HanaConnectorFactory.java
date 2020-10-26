package io.rtdi.bigdata.hanaconnector;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import io.rtdi.bigdata.connector.connectorframework.BrowsingService;
import io.rtdi.bigdata.connector.connectorframework.ConnectorFactory;
import io.rtdi.bigdata.connector.connectorframework.IConnectorFactoryProducer;
import io.rtdi.bigdata.connector.connectorframework.Producer;
import io.rtdi.bigdata.connector.connectorframework.controller.ConnectionController;
import io.rtdi.bigdata.connector.connectorframework.controller.ProducerInstanceController;
import io.rtdi.bigdata.connector.connectorframework.exceptions.ConnectorCallerException;
import io.rtdi.bigdata.connector.pipeline.foundation.exceptions.PropertiesException;

public class HanaConnectorFactory extends ConnectorFactory<HanaConnectionProperties> 
implements IConnectorFactoryProducer<HanaConnectionProperties, HanaProducerProperties> {

	public HanaConnectorFactory() {
		super("S4Connector");
	}

	@Override
	public Producer<HanaConnectionProperties, HanaProducerProperties> createProducer(ProducerInstanceController instance) throws IOException {
		return new HanaProducer(instance);
	}

	@Override
	public HanaConnectionProperties createConnectionProperties(String name) throws PropertiesException {
		return new HanaConnectionProperties(name);
	}

	@Override
	public HanaProducerProperties createProducerProperties(String name) throws PropertiesException {
		return new HanaProducerProperties(name);
	}

	@Override
	public BrowsingService<HanaConnectionProperties> createBrowsingService(ConnectionController controller) throws IOException {
		return new HanaBrowse(controller);
	}

	@Override
	public boolean supportsBrowsing() {
		return true;
	}

	static Connection getDatabaseConnection(HanaConnectionProperties props) throws ConnectorCallerException {
		try {
			return getDatabaseConnection(props.getJDBCURL(), props.getUsername(), props.getPassword());
		} catch (SQLException e) {
			throw new ConnectorCallerException("Failed to establish a database connection", e, null, props.getJDBCURL());
		}
	}
	
	static Connection getDatabaseConnection(String jdbcurl, String user, String passwd) throws SQLException {
        try {
            Class.forName("com.sap.db.jdbc.Driver");
            Connection conn = DriverManager.getConnection(jdbcurl, user, passwd);
            conn.setAutoCommit(false);
            return conn;
        } catch (ClassNotFoundException e) {
            throw new SQLException("No Hana JDBC driver library found");
        }
	}

}
