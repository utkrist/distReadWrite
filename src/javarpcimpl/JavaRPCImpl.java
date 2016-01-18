/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javarpcimpl;

/**
 *
 * @author ashu
 */
public class JavaRPCImpl {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            // TODO code application logic here
                            
                                          
        ServerThread servThreadInst = new ServerThread();
        servThreadInst.start();
        
        Thread.sleep(1000);
        
        ClientThread clientThreadInst = new ClientThread();
        clientThreadInst.start();
        
        } catch (Exception ex) {
           ex.printStackTrace();
        }
 
    }
    
}
