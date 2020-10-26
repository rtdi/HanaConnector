package io.rtdi.bigdata.hanaconnector.servlet;

import javax.servlet.annotation.WebServlet;

import io.rtdi.bigdata.connector.connectorframework.servlet.UI5ServletAbstract;

@WebServlet("/ui5/EditSchema")
public class AddTables extends UI5ServletAbstract {
	private static final long serialVersionUID = 1L;

    public AddTables() {
        super("Add Tables", "AddTables");
    }

}
