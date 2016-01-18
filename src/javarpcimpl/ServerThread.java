/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javarpcimpl;

import org.apache.xmlrpc.server.PropertyHandlerMapping;
import org.apache.xmlrpc.server.XmlRpcServer;
import org.apache.xmlrpc.server.XmlRpcServerConfigImpl;
import org.apache.xmlrpc.webserver.WebServer;

/**
 *
 * @author ashu
 */
public class ServerThread extends Thread {

    private static int port = 8082;

    public ServerThread() {
    }

    @Override
    public void run() {

        try {
            WebServer webServer = new WebServer(port);

            XmlRpcServer xmlRpcServer = webServer.getXmlRpcServer();

            PropertyHandlerMapping phm = new PropertyHandlerMapping();

            phm.addHandler("Hosts",javarpcimpl.Hosts.class);            

            xmlRpcServer.setHandlerMapping(phm);

            XmlRpcServerConfigImpl serverConfig
                    = (XmlRpcServerConfigImpl) xmlRpcServer.getConfig();
            serverConfig.setEnabledForExtensions(true);
            serverConfig.setContentLengthOptional(false);

            System.out.println("XMl RPC SERVER at " + port + " Running....");
            webServer.start();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
