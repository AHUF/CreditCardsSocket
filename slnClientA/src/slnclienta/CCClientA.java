/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package slnclienta;

/**
 *
 * @author Ann
 */
import java.net.*;
import java.io.*;
import java.util.*;

public class CCClientA {

    public static final int mPORT = 1574;
    private Socket mSocket;
    
    private BufferedReader mIn;
    private PrintStream mOut;
    private ObjectInputStream objIn;
    public String line = null;
    
    public CCClientA(){
        try{
            // create a socket that specifies an address + port
            mSocket = new Socket("localhost", mPORT);
            
            objIn = new ObjectInputStream(mSocket.getInputStream());
            mOut = new PrintStream(mSocket.getOutputStream());
        }catch(IOException e){
            System.err.println("Server not running");
        }
    }
    
    public void Run(){
        try{
            boolean quit = false;
            Scanner cin = new Scanner(System.in);
            PrintStream cout = System.out;
            cout.println("please enter your password");
            int count = 0;
            
            while(count < 3){
                String pass  = cin.nextLine();
                mOut.println("login:(Your USER NAME):" + pass);
                String resp = (String)objIn.readObject();
                if(resp.compareTo("OK") == 0)
                    break;
                else
                    count++;
                cout.println("incorrect password. please enter your password");
            }
            if(count == 3){
                cout.println("Your account has been locked. Please contact your DBA");
                //Terminates the currently running Java Virtual Machine. a nonzero status code indicates abnormal termination.
                System.exit(-1);
            }
            mOut.println("read:SELECT AccountNo, Balance, Limit, expirationDate, name FROM Account ORDER BY name");
            
            try{
                List<String> l = (List<String>) objIn.readObject();
                for(int i=0;i<l.size();i++)
                    System.out.println(l.get(i));
            }catch(IOException i){
                i.printStackTrace();
            }catch(ClassNotFoundException c){
                c.printStackTrace();
            }
            
            cout.println();
            cout.println("Enter U to update an account credit limit, T to verify a transaction, L to List transaction, Q to quit");
            cout.flush();
            String input = cin.nextLine();
            
            while(!quit){
                int c = input.charAt(0);
                switch(c){
                    case 'u':
                    case 'U':
                        cout.println("Enter Account No: ");
                        cout.flush();
                        String accno = cin.nextLine();
                        cout.println("Enter the new amount: ");
                        cout.flush();
                        String amt = cin.nextLine();
                        mOut.println("update:" + accno + ":" + amt);
                        try{
                            Integer line = (Integer)objIn.readObject();
                            System.out.println(line + " records updated");
                        }catch(IOException i){
                            i.printStackTrace();
                        }
                        break;
                    case 'T':
                    case 't':
                        cout.println("Enter Account No: ");
                        cout.flush();
                        accno = cin.nextLine();
                        cout.println("Enter the amount: ");
                        cout.flush();
                        amt = cin.nextLine();
                        
                        mOut.println("transact:" + accno + ":" + amt);
                        
                        try{
                            line = (String)objIn.readObject();
                            System.out.println("The approval code is " + line);
                        }catch(IOException i){
                            i.printStackTrace();
                        }
                        break;
                    case 'L':
                    case 'l':
                        cout.println("Enter Account No: ");
                        cout.flush();
                        accno = cin.nextLine();
                        mOut.println("list:" + accno);
                        try{
                            List<String> l = (List<String>)objIn.readObject();
                            for(int i=0;i<l.size();i++){
                                System.out.println(l.get(i));
                            }
                        }catch(IOException i){
                            i.printStackTrace();
                        }
                        break;
                    default:
                        quit = true;
                        mOut.println("quit:");
                        line = (String)objIn.readObject();
                        cout.println(line);
                        mSocket.close();
                }
                if(!quit){
                    cout.println("Enter U to update an account credit limit, T to verify a transaction, L to List transaction, Q to quit");
                    cout.flush();
                    input = cin.nextLine();
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {
        try{
            CCClientA c = new CCClientA();
            c.Run();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
}
