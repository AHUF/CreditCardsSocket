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
import java.util.*;

import ccdblibA.*;

public class ConversationMangerA extends Thread{
    
    private crudA db;
    protected Socket mClient;
    
    protected BufferedReader mIn;
    protected ObjectOutputStream objOut;
    
    public ConversationMangerA(Socket client_socket){
        mClient = client_socket;
        try{
            mIn = new BufferedReader(new InputStreamReader(mClient.getInputStream()));
            objOut = new ObjectOutputStream(mClient.getOutputStream());
        }catch(IOException e){
            try{
                mClient.close();
            }catch(IOException e2){
                e2.printStackTrace();
            }
            e.printStackTrace();
            return;
        }catch(Exception e){
            e.printStackTrace();
        }
        //start the thread, this call the method run (a design pattern for threaded classes)
        this.start();
    }
    /*Provide the service. The server has to decide what the client wants it to do
        The following code enables the server to run Read, List, Update and Transact
    */
    
    @Override
    public void run(){
        String line;
        while(true){
            try{
                line = mIn.readLine();
                System.out.println(line);
                
                String cname, accno, sql, amt;
                String[] p = line.split(":");
                cname = p[0];
                if(cname.compareTo("login") == 0){
                    String uid = p[1];
                    String pass = p[2];
                    db = new crudA("CreditCardAccounts",uid,pass);
                    String login = "Fail";
                    if(db.IsConnected())
                        login = "OK";
                    objOut.writeObject(login);
                }
                if(cname.compareTo("update") == 0){
                    accno = p[1];
                    amt = p[2];
                    Integer code = new Integer(db.Update(accno, amt));
                    objOut.writeObject(code);
                }
                else if(cname.compareTo("transact") == 0){
                    accno = p[1];
                    amt = p[2];
                    String appcode = db.Charge(accno, amt);
                    objOut.writeObject(appcode);
                }
                else if(cname.compareTo("read") == 0){
                    sql = p[1];
                    List<String> l = db.Read(sql);
                    objOut.writeObject(l);
                }
                else if(cname.compareTo("list") == 0){
                    accno = p[1];
                    List<String> l = db.List(accno);
                    objOut.writeObject(l);
                }
                else if(cname.compareTo("quit") == 0){
                    objOut.writeObject("Closing connection...");
                    mClient.close();
                    break;
                }
            }catch(IOException e){
                e.printStackTrace();
            }
            System.out.flush();
        }
    }
}
