/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javarpcimpl;

import java.util.List;

/**
 *
 * @author ashu
 */
public class Hosts {

    public Object[] join(List<String> hostList) {
//        System.out.println("New request recieved " + hostList);

            for (String hostList1 : hostList) {
                boolean match = false;
                for (String ipList : Globals.ipList) {
                    if (hostList1.equalsIgnoreCase(ipList)) {
                        match = true;
                    }
                }
                if (match == false) {
                    synchronized (Globals.ipList) {
                        Globals.ipList.add(hostList1);
                        System.out.println(hostList1 + " has joined the Network..." + Globals.ipList);
                    }                                        
                }
            }
            return Globals.ipList.toArray();
        
    }

    public int update(List<String> hostList) {

        if (hostList.isEmpty()) {
            return -1;
        } else {
            for (String hostList1 : hostList) {
                boolean match = false;
                for (String ipList : Globals.ipList) {
                    if (hostList1.equalsIgnoreCase(ipList)) {
                        match = true;
                    }
                }
                if (match == false) {
                    System.out.println(hostList1 + " has joined the Network..." + Globals.ipList);
                    synchronized (Globals.ipList) {
                        Globals.ipList.add(hostList1);
                    }
                }
            }
            return 0;
        }
    }
    
    public int signOut(String ip){
        
        if(Globals.ipList.contains(ip)){
            synchronized(Globals.ipList){
                if(Globals.ipList.remove(ip)){
                    System.out.println(ip + " has left the Network" + Globals.ipList);
                    return 0;
                }else{                    
                    System.out.println("Unable to Sign Out " + ip); 
                    return -1;
                }
            }
        }else{
            return -1;
        }                   
    }
}
