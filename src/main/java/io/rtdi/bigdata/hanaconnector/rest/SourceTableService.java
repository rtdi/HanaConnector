package io.rtdi.bigdata.hanaconnector.rest;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Configuration;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import io.rtdi.bigdata.connector.connectorframework.WebAppController;
import io.rtdi.bigdata.connector.connectorframework.controller.ConnectionController;
import io.rtdi.bigdata.connector.connectorframework.controller.ConnectorController;
import io.rtdi.bigdata.connector.connectorframework.rest.JAXBErrorResponseBuilder;
import io.rtdi.bigdata.connector.connectorframework.rest.JAXBSuccessResponseBuilder;
import io.rtdi.bigdata.connector.connectorframework.servlet.ServletSecurityConstants;
import io.rtdi.bigdata.hanaconnector.HanaBrowse;
import io.rtdi.bigdata.hanaconnector.HanaConnectionProperties;
import io.rtdi.bigdata.hanaconnector.HanaTableMapping;
import io.rtdi.bigdata.hanaconnector.HanaBrowse.TableImport;

@Path("/")
public class SourceTableService {
	@Context
    private Configuration configuration;

	@Context 
	private ServletContext servletContext;

	public SourceTableService() {
	}
			
	@GET
	@Path("/connections/{connectionname}/sourcetables")
    @Produces(MediaType.APPLICATION_JSON)
	@RolesAllowed({ServletSecurityConstants.ROLE_VIEW})
    public Response getFiles(@PathParam("connectionname") String connectionname, @PathParam("name") String name) {
		try {
			ConnectorController connector = WebAppController.getConnectorOrFail(servletContext);
			ConnectionController connection = connector.getConnectionOrFail(connectionname);
			HanaBrowse browser = (HanaBrowse) connection.getBrowser();
			return Response.ok(browser.getHanaTables()).build();
		} catch (Exception e) {
			return JAXBErrorResponseBuilder.getJAXBResponse(e);
		}
	}

	@POST
	@Path("/connections/{connectionname}/sourcetables")
    @Produces(MediaType.APPLICATION_JSON)
	@RolesAllowed({ServletSecurityConstants.ROLE_CONFIG})
    public Response getFiles(@PathParam("connectionname") String connectionname, List<TableImport> data) {
		try {
			ConnectorController connector = WebAppController.getConnectorOrFail(servletContext);
			ConnectionController connection = connector.getConnectionOrFail(connectionname);
			HanaConnectionProperties props = (HanaConnectionProperties) connection.getConnectionProperties();
			String dbuser = props.getUsername();
			HanaBrowse browser = (HanaBrowse) connection.getBrowser();
			for (TableImport t : data) {
				HanaTableMapping entity = new HanaTableMapping(t.getHanaschemaname() + "_" + t.getHanatablename(), dbuser, t.getHanaschemaname(), t.getHanatablename(), browser.getConnection());
				entity.write(browser.getBusinessObjectDirectory());
			}
			return JAXBSuccessResponseBuilder.getJAXBResponse("Saved " + data.size() + " table schemas");
		} catch (Exception e) {
			return JAXBErrorResponseBuilder.getJAXBResponse(e);
		}
	}

	@GET
	@Path("/connections/{connectionname}/sourcetables/{definition}")
    @Produces(MediaType.APPLICATION_JSON)
	@RolesAllowed({ServletSecurityConstants.ROLE_VIEW})
    public Response getSchemaDefinition(
    		@PathParam("connectionname") String connectionname,
    		@PathParam("name") String name,
    		@PathParam("definition") String definition) {
		try {
			ConnectorController connector = WebAppController.getConnectorOrFail(servletContext);
			ConnectionController connection = connector.getConnectionOrFail(connectionname);
			HanaBrowse browser = (HanaBrowse) connection.getBrowser();
			HanaTableMapping o = browser.getBusinessObject(definition);
			return Response.ok(o).build();
		} catch (Exception e) {
			return JAXBErrorResponseBuilder.getJAXBResponse(e);
		}
	}

	@POST
	@Path("/connections/{connectionname}/sourcetables/{definition}")
    @Produces(MediaType.APPLICATION_JSON)
	@RolesAllowed({ServletSecurityConstants.ROLE_CONFIG})
    public Response setSchemaDefinition(@PathParam("connectionname") String connectionname, @PathParam("definition") String definition, HanaTableMapping data) {
		try {
			ConnectorController connector = WebAppController.getConnectorOrFail(servletContext);
			ConnectionController connection = connector.getConnectionOrFail(connectionname);
			HanaBrowse browser = (HanaBrowse) connection.getBrowser();
			data.write(browser.getBusinessObjectDirectory());
			return JAXBSuccessResponseBuilder.getJAXBResponse("Saved");
		} catch (Exception e) {
			return JAXBErrorResponseBuilder.getJAXBResponse(e);
		}
	}

}