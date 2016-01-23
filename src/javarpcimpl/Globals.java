/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javarpcimpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author ashu
 */
public class Globals {

    public static List<String> ipList = new ArrayList<String>();
    public static List<String> newIpList = new ArrayList<String>();
    public static HashMap<String, Integer> counter = new HashMap<String, Integer>();
    public static String selfIpAddress;
    public static final int TIME_OUT = 5;
    public static final int POLLING_TIME_OUT = 1;
    public static final int MAX_ATTEMPT = 3;
}
