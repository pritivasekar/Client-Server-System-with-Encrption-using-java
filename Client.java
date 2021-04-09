import java.net.*;
import java.io.*;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;
 
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class Client extends Frame implements ActionListener, Runnable {

    Socket socket;
    Panel topControlsP, bottomControlsP, chatsP;
    List chatsL;
	List decryptchatsL;
    TextField ipTF, portTF, messageTF;
    Button connectB, sendB, exitB, showB, hideB;
    BufferedReader bufferedReader;
    BufferedWriter bufferedWriter;
    Thread th;
	private static SecretKeySpec secretKey;
    private static byte[] key;
	final String secret = "topsecret";

    public Client(String str) {
        super(str);
        //creating controls objects
        topControlsP = new Panel();
        ipTF = new TextField(15);
        portTF = new TextField(5);
        connectB = new Button("Connect");
        chatsL = new List();
		decryptchatsL = new List();
		decryptchatsL.setVisible(false);
        bottomControlsP = new Panel();
        messageTF = new TextField(20);
		chatsP = new Panel();
		chatsP.setLayout(new GridLayout(1,2));
		chatsP.add(chatsL);
		chatsP.add(decryptchatsL);
        sendB = new Button("Send");
		showB = new Button("Show text");
		hideB = new Button("Hide text");
        exitB = new Button("Exit");
        //adding controls to Panel
        topControlsP.add(ipTF);
        topControlsP.add(portTF);
        topControlsP.add(connectB);
        //adding controls to Panel
     // bottomControlsP.serLayout(new GridLayout(2,4,4,4));
        bottomControlsP.add(messageTF);
        bottomControlsP.add(sendB);
		bottomControlsP.add(showB);
		bottomControlsP.add(hideB);
        bottomControlsP.add(exitB);
        //adding to controls and Panel to Frame
        add(topControlsP, BorderLayout.NORTH);
        add(chatsP, BorderLayout.CENTER);
        add(bottomControlsP, BorderLayout.SOUTH);
        //adding Listeners on Connect, Send and Exit Button        
        connectB.addActionListener(this);
        sendB.addActionListener(this);
		showB.addActionListener(this);
		hideB.addActionListener(this);
        exitB.addActionListener(this);

        setSize(500, 500);//setting size of Frame/window
        setLocation(500, 0);//setting location of Frame/window on the screen
        setBackground(Color.PINK);//setting background for Frame/window
        setVisible(true);//setting it to visible state       
    }

    public void actionPerformed(ActionEvent event) {
        if (event.getSource().equals(exitB)) {
            System.exit(0);
        } else if (event.getSource().equals(connectB)) {
            try {
                socket = new Socket(ipTF.getText(), Integer.parseInt(portTF.getText()));
                bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                ipTF.setText("Connected");
                th = new Thread(this);
                th.start();
            } catch (IOException ioe) {
                ipTF.setText(ioe.getMessage());
            }
        } else if(event.getSource().equals(sendB)){
            try {
                if (bufferedWriter != null) {
                    String str = messageTF.getText();
					String encryptedString = encrypt(str,secret);
					bufferedWriter.write(encryptedString);
                    bufferedWriter.newLine();
                    bufferedWriter.flush();
                    chatsL.add("You: " + encryptedString);
					decryptchatsL.add("You: " + str);
                    messageTF.setText("");
                }
            } catch (IOException ioe) {
                ipTF.setText(ioe.getMessage());
            }
        }
		else if(event.getSource().equals(showB)){
				decryptchatsL.setVisible(true);
		}
		else {
				decryptchatsL.setVisible(false);
		}
    }

    public void run() {
        try {
            socket.setSoTimeout(1);
        } catch (Exception e) {
            ipTF.setText(e.getMessage());
        }
        while (true) {
            try {
				 String extn="";
                String message = bufferedReader.readLine();
				System.out.println(message);
				//chatsL.add(message);
				DataInputStream dis = new DataInputStream (socket.getInputStream());
				int flag=0,i;
            
                for(i=0;i<message.length();i++)
                {
                    
                    if(message.charAt(i)=='.' || flag==1)
                    {
                    flag=1;
                    extn+=message.charAt(i);
                    }
                }
				
                if(message == null) {
                    break;
                }
				
			else if(extn.equals(".java") || extn.equals(".txt"))
				{
					String str;
					FileWriter fstream = new FileWriter("Created_"+message);
                    PrintWriter out=new PrintWriter(fstream);
					do
                    {
                    //str=dis.readUTF().toString();
					str=bufferedReader.readLine();
					//String dstr = decrypt(str,secret);
                    System.out.println(str);
                    out.write(str+"\n");
                    out.flush();
					if(str==null)
						break;
                    }while(true);
        
                    chatsL.add("One File Received and Decrypted "+message);
                    out.close();
				}
				else
				{	
					chatsL.add("Server: " + message);
					String decryptedString = decrypt(message,secret);
					decryptchatsL.add("Server: "+decryptedString);
					
				}
            } catch (Exception e) {
                
            }            
        }
    }
	
	
	//AES CODE
 
    public static void setKey(String myKey) 
    {
        MessageDigest sha = null;
        try {
            key = myKey.getBytes("UTF-8");
            sha = MessageDigest.getInstance("SHA-1");
            key = sha.digest(key);
            key = Arrays.copyOf(key, 16); 
            secretKey = new SecretKeySpec(key, "AES");
        } 
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } 
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
 
    public static String encrypt(String strToEncrypt, String secret) 
    {
        try
        {
            setKey(secret);
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            return Base64.getEncoder().encodeToString(cipher.doFinal(strToEncrypt.getBytes("UTF-8")));
        } 
        catch (Exception e) 
        {
            System.out.println("Error while encrypting: " + e.toString());
        }
        return null;
    }
 
    public static String decrypt(String strToDecrypt, String secret) 
    {
        try
        {
            setKey(secret);
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            return new String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt)));
        } 
        catch (Exception e) 
        {
            System.out.println("Error while decrypting: " + e.toString());
        }
        return null;
    }
	
	
	//MAIN FUNCTION

    public static void main(String[] ar) {
        new Client("Client Program");
    }
}
