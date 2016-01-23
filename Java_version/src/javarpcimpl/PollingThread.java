/*
 * Copyright (C) 2016 ashu
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package javarpcimpl;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;

/**
 *
 * @author ashu
 */
public class PollingThread extends Thread {

    @Override
    public void run() {

//        HashMap<String, Integer> counter = new HashMap<String, Integer>();
        System.out.println("Starting POLLING THREAD");
        while (true) {
            List<String> tmp = new ArrayList<String>(Globals.ipList.size());
            tmp.clear();
            tmp.addAll(Globals.ipList);
            try {
                if (tmp.size() > 1) {
                    for (String ip : tmp) {
                        if (!ip.equals(Globals.selfIpAddress)) {

                            synchronized (Globals.counter) {
                                if (!Globals.counter.containsKey(ip)) {
                                    Globals.counter.put(ip, 0);
                                }
                            }
                            try {
                                XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
                                XmlRpcClient client = new XmlRpcClient();
                                config.setServerURL(new URL("http://" + ip + ":8082/"));
                                config.setConnectionTimeout(Globals.POLLING_TIME_OUT * 1000);
                                client.setConfig(config);
                                Object[] params = new Object[]{null};
                                params[0] = "";
                                Integer result;

                                synchronized (Globals.counter) {
                                    if (Globals.counter.get(ip) > 2) {
                                        pollFailed(ip);
                                        Globals.counter.remove(ip);
                                        continue;
                                    }
                                }

                                synchronized (Globals.counter) {
                                    Globals.counter.put(ip, Globals.counter.get(ip) + 1);
                                }
//                                System.out.println(" Before Polling Map " + Globals.counter);
                                result = (int) client.execute("Hosts.poll", params);
                                if (result == 1) {
                                    synchronized (Globals.counter) {
                                        Globals.counter.put(ip, 0);
                                    }
//                                    System.out.println("Poll response : " + ip);
                                }
                            } catch (MalformedURLException conn) {
                                System.out.println("Wrong URL entered");
                            } catch (XmlRpcException xmlEx) {
                                System.out.println("No Poll Response from " + ip);
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                    }
                }
                Thread.sleep(1000);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    /*
     Send request to every node to remove the Ip and removing the Ip from its own list
     */
    public int pollFailed(String remove_ip) {

        if (Globals.ipList.size() == 1) {
            return -1;
        }

        for (Iterator<String> ite = Globals.ipList.iterator(); ite.hasNext();) {
            String localIp = ite.next();
            if (!(localIp.equals(Globals.selfIpAddress) || localIp.equals(remove_ip))) {
                try {
                    XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
                    XmlRpcClient client = new XmlRpcClient();
                    config.setServerURL(new URL("http://" + localIp + ":8082/"));
                    config.setConnectionTimeout(Globals.TIME_OUT * 1000);
                    client.setConfig(config);
                    Object[] params = new Object[]{null};

                    Integer result;

                    params[0] = remove_ip;

                    result = (int) client.execute("Hosts.signOut", params);

                    if (result == 0) {
                        System.out.println("Sign Off from " + localIp + " successful");
                    } else {
                        System.out.println("Sign Off from " + localIp + " Failed");
                    }
                } catch (MalformedURLException urlEx) {
                    System.out.println("Wrong URL enetred");
                } catch (XmlRpcException conn) {
                    synchronized (Globals.ipList) {
                        ite.remove();
                    }
                    System.out.println("pollFailed operation Time out");
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }

        if (Globals.ipList.contains(remove_ip)) {
            synchronized (Globals.ipList) {
                if (Globals.ipList.remove(remove_ip)) {
                    System.out.println(remove_ip + " has left the Network" + Globals.ipList);
                    return 0;
                } else {
                    System.out.println("Unable to remove " + remove_ip);
                    return -1;
                }
            }
        }
        return 0;
    }
}
