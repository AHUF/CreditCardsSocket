/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package slnservera;

/**
 *
 * @author Ann
 */
import java.io.*;
import java.net.*;

public class CCServerA {

    public final static int mPORT = 1574;
    protected ServerSocket mListenSocket;
    public CCServerA() throws IOException{
        try{
            mListenSocket  = new ServerSocket(mPORT);
        }catch(IOException e){
            throw e;
        }
        System.out.println("Server: listening on port " + mPORT);
    }
    
    /* The body of the server thread.Loop forever, listening for and
        accepting connections from clients. For each connection,
        create a ConversationManager object to handle communication through the
        new Socket.
    */
    public void Listen(){
        try{
            while(true){
                // Create a socket to hear back from the client
                Socket client_socket = mListenSocket.accept();
                ConversationMangerA c = new ConversationMangerA(client_socket);
            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }
    
    //start the server
    public static void main(String[] args) {
        try{
            CCServerA s = new CCServerA();
            s.Listen();
        }catch(IOException e){
            e.printStackTrace();
        }
    }
    
}
