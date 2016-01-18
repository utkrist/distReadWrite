/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javarpcimpl;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;

/**
 *
 * @author ashu
 */
public class RPCClient {

    public synchronized void sendJoinReq(String ip) {

        try {
            if (!Globals.ipList.contains(ip)) { // If entered Ip is already in Nw
                XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
                XmlRpcClient client = new XmlRpcClient();
                config.setServerURL(new URL("http://" + ip + ":8082/"));
                client.setConfig(config);
                Object[] params = new Object[]{null};

                Object[] result;

                params[0] = Globals.ipList;
                System.out.println("executing hosts.join" + Globals.ipList);

                result = (Object[]) client.execute("Hosts.join", params);

                if (result != null) {
                    for (Object value : result) {
                        if (!Globals.ipList.contains((String) value)) {
                            Globals.ipList.add((String) value);
                        }
                    }
                    System.out.println("Join Successful: New network consists of " + Globals.ipList);
                    sendUpdate();
                } else {
                    System.out.println("Join Unsuccessful ");
                }
            } else {    //if Ip is already in the Network
                System.out.println(ip + " is already in Network");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public synchronized void sendUpdate() {

        try {
            for (String li : Globals.ipList) {
                if (!li.equals(Globals.selfIpAddress)) {
                    XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
                    XmlRpcClient client = new XmlRpcClient();
                    config.setServerURL(new URL("http://" + li + ":8082/"));
                    client.setConfig(config);
                    Object[] params = new Object[]{null};

                    Integer result;

                    params[0] = Globals.ipList;

                    result = (int) client.execute("Hosts.update", params);
                    if (result == 0) {
                        System.out.println("List updated successfully... ");
                    } else {
                        System.out.println("List not updated ------");
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public void sendSignOff(){
        
        for (Iterator<String> ite = Globals.ipList.iterator(); ite.hasNext();) {
            String localIp = ite.next();
            if (!localIp.equals(Globals.selfIpAddress)) {
                try {
                    XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
                    XmlRpcClient client = new XmlRpcClient();
                    config.setServerURL(new URL("http://" + localIp + ":8082/"));
                    client.setConfig(config);
                    Object[] params = new Object[]{null};

                    Integer result;

                    params[0] = Globals.selfIpAddress;

                    result = (int) client.execute("Hosts.signOut", params);
                                                        
                    if (result == 0) {                        
                        synchronized(Globals.ipList){
                            ite.remove();
                        }
                        System.out.println("Sign Off from " + localIp + " successful");
                    } else {
                        System.out.println("Sign Off from " + localIp + " Failed");
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }        
    }
}
