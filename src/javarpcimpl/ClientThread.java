/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javarpcimpl;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.Scanner;

/**
 *
 * @author ashu
 */
public class ClientThread extends Thread {

    private synchronized void computeSelfIp() {
        try {
            // compute Self Ip and update list
            Enumeration e = NetworkInterface.getNetworkInterfaces();
            while (e.hasMoreElements()) {
                NetworkInterface n = (NetworkInterface) e.nextElement();
                Enumeration ee = n.getInetAddresses();
                while (ee.hasMoreElements()) {
                    InetAddress i = (InetAddress) ee.nextElement();
                    if (i.isSiteLocalAddress()) {
                        Globals.selfIpAddress = i.getHostAddress();
//                        System.out.println(i.getHostAddress());
                        if (!Globals.ipList.contains(i.getHostAddress())) {
                            Globals.ipList.add(i.getHostAddress());
                        }
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            // TODO code application logic here
            computeSelfIp();

            BufferedReader input = new BufferedReader(new InputStreamReader(System.in));

           
            
            while (true) {

                System.out.println("\n Select Operation to be performed :");
                System.out.println("1. Join (Press Key 'j')");
                System.out.println("2. Sign Off (Press Key 'q')");
                System.out.println("3. Distributed R/W Operation (Press Key 'd')");

                switch (input.readLine().charAt(0)) {

                    case 'j':
                        joinRequest();
                        break;

                    case 'q':
                        signOff();
                        break;

                    case 'd':
                        break;

                    default:
                        System.out.println("Wrong Option Selected");
                        break;
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void joinRequest() {

        try {
            System.out.println("Enter the Ip Address of the node :");

            Scanner input = new Scanner(System.in);
            String ip = input.nextLine();

            RPCClient myClient = new RPCClient();
            myClient.sendJoinReq(ip);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void signOff() {
        RPCClient myClient = new RPCClient();
        if (myClient.sendSignOff() >= 0) {
            System.out.println("Sign off Operation completed");
        } else {
            System.out.println("No host in the network");
        }
    }
}
