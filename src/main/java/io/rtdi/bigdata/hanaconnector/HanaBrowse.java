package io.rtdi.bigdata.hanaconnector;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilderException;

import io.rtdi.bigdata.connector.connectorframework.BrowsingService;
import io.rtdi.bigdata.connector.connectorframework.controller.ConnectionController;
import io.rtdi.bigdata.connector.connectorframework.entity.TableEntry;
import io.rtdi.bigdata.connector.connectorframework.exceptions.ConnectorRuntimeException;
import io.rtdi.bigdata.connector.pipeline.foundation.utils.FileNameEncoder;

public class HanaBrowse extends BrowsingService<HanaConnectionProperties> {
	
	private File bopath;
	private Connection conn;

	public HanaBrowse(ConnectionController controller) throws IOException {
		super(controller);
		bopath = new File(controller.getDirectory(), "BusinessObjects");
	}

	@Override
	public void open() throws IOException {
		conn = HanaConnectorFactory.getDatabaseConnection(getConnectionProperties());
	}

	@Override
	public void close() {
		if (conn != null) {
			try {
				conn.close();
			} catch (SQLException e) {
			}
			conn = null;
		}
	}

	@Override
	public List<TableEntry> getRemoteSchemaNames() throws IOException {
		if (bopath.isDirectory()) {
			File[] files = bopath.listFiles();
			List<TableEntry> ret = new ArrayList<>();
			if (files != null) {
				for (File f : files) {
					if (f.getName().endsWith(".json") && f.isFile()) {
						String name = FileNameEncoder.decodeName(f.getName());
						ret.add(new TableEntry(name.substring(0, name.length()-5))); // remove the .json ending
					}
				}
			}
			return ret;
		} else {
			return null;
		}
	}

	@Override
	public Schema getRemoteSchemaOrFail(String name) throws IOException {
		HanaTableMapping n1 = HanaTableMapping.readDefinition(null, name, null, bopath);
		try {
			return n1.getAvroSchema();
		} catch (SchemaBuilderException e) {
			throw new ConnectorRuntimeException("Schema cannot be parsed", e, null, null);
		}
	}

	public HanaTableMapping getBusinessObject(String name) throws IOException {
		HanaTableMapping n1 = HanaTableMapping.readDefinition(null, name, null, bopath);
		return n1;
	}

	public File getBusinessObjectDirectory() {
		return bopath;
	}
	
	public List<TableImport> getHanaTables() throws ConnectorRuntimeException {
		String sql = "SELECT schema_name, table_name FROM tables \r\n"
				+ "WHERE schema_name IN (SELECT schema_name FROM EFFECTIVE_PRIVILEGES WHERE user_name = CURRENT_USER AND PRIVILEGE = 'TRIGGER' AND object_type = 'SCHEMA')\r\n"
				+ "OR (schema_name, table_name) IN (SELECT schema_name, object_name FROM EFFECTIVE_PRIVILEGES WHERE user_name = CURRENT_USER AND PRIVILEGE = 'TRIGGER' AND object_type = 'TABLE')\r\n"
				+ "OR schema_name = CURRENT_USER\r\n"
				+ "order by 1,2";
		try (PreparedStatement stmt = conn.prepareStatement(sql);) {
			ResultSet rs = stmt.executeQuery();
			List<TableImport> sortedlist = new ArrayList<>();
			while (rs.next()) {
				String schemaname = rs.getString(1);
				String tablename = rs.getString(2);
				sortedlist.add(new TableImport(schemaname, tablename));
			}
			return sortedlist;
		} catch (SQLException e) {
			throw new ConnectorRuntimeException("Reading all tables of the TABLES view failed", e, 
					"Execute the sql as Hana user \"" + getConnectionProperties().getUsername() + "\"", sql);
		}
	}
	
	public Connection getConnection() {
		return conn;
	}
	
	public static class TableImport {
		private String hanatable;
		private String hanaschema;
		private boolean imported;
		private String mappingname;
		
		public TableImport() {
			super();
		}

		public TableImport(String hanaschema, String hanatable) {
			super();
			this.hanatable = hanatable;
			this.hanaschema = hanaschema;
			this.mappingname = hanaschema + "_" + hanatable;
		}

		public String getHanatablename() {
			return hanatable;
		}
		public void setHanatablename(String tablename) {
			this.hanatable = tablename;
		}
		public boolean isImported() {
			return imported;
		}
		public void setImported(boolean imported) {
			this.imported = imported;
		}

		public String getHanaschemaname() {
			return hanaschema;
		}

		public void setHanaschemaname(String schemaname) {
			this.hanaschema = schemaname;
		}

		public String getMappingname() {
			return mappingname;
		}

		public void setMappingname(String mappingname) {
			this.mappingname = mappingname;
		}
	}

	@Override
	public void validate() throws IOException {
		close();
		open();
		String sql = "select top 1 table_name from TABLES";
		try (PreparedStatement stmt = conn.prepareStatement(sql);) {
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				return;
			} else {
				throw new ConnectorRuntimeException("No entries found in the Hana TABLES view - missing permissions?", null, 
						"Execute the sql as Hana user \"" + getConnectionProperties().getUsername() + "\"", sql);
			}
		} catch (SQLException e) {
			throw new ConnectorRuntimeException("Reading the Hana TABLES view failed", e, 
					"Execute the sql as Hana user \"" + getConnectionProperties().getUsername() + "\"", sql);
		} finally {
			close();
		}
		
	}

	@Override
	public void deleteRemoteSchemaOrFail(String remotename) throws IOException {
		File file = new File(bopath, remotename + ".json");
		java.nio.file.Files.delete(file.toPath());
	}
}
