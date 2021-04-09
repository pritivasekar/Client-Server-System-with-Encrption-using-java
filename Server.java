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
import javax.swing.JFileChooser;
 
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;


public class Server extends Frame implements ActionListener, Runnable {

    int port = 4444;
    ServerSocket serverSocket;
    Socket socket;
    Label statusL;
    List chatsL;
    List decryptchatsL;
    Panel controlsP,chatsP;
    TextField messageTF;
    Button sendB, exitB, showB, hideB,sendf,receivef,efile,dfile,svoice,rvoice;
    BufferedReader bufferedReader;
    BufferedWriter bufferedWriter;
    private static SecretKeySpec secretKey;
    private static byte[] key;
    final String secret = "topsecret";
    public static final Color grey = new Color(0,0,139);
    String s;
     DataInputStream din;
    DataOutputStream dout;

    public Server(String m) {
        super(m);        
        //creating controls objects
        statusL = new Label("Status");
        statusL.setForeground(Color.WHITE);
        chatsL = new List();
        decryptchatsL = new List();
        decryptchatsL.setVisible(false);
        
        messageTF = new TextField(20);
        controlsP = new Panel();
		chatsP = new Panel();
		chatsP.setLayout(new GridLayout(1,2));
		chatsP.add(chatsL);
		chatsP.add(decryptchatsL);
        sendB = new Button("Send");
        showB = new Button("Show text");
        hideB = new Button("Hide text");
        exitB = new Button("Exit");
        //sfile=new Button("Select File");        
        sendf=new Button("Send File");
        receivef=new Button("Receive File");
        efile=new Button("Encrypt File");
        dfile=new Button("Decrypt File");
        svoice=new Button("Send voice");
        rvoice=new Button("Receive voice");
        Label temp1=new Label("");
        temp1.setVisible(false);
        Label temp2=new Label("");
        temp2.setVisible(false);

         //adding controls to Panel
        controlsP.setLayout(new GridLayout(2,4,4,4));        
        controlsP.add(messageTF);
        controlsP.add(sendB);
        controlsP.add(showB);
        controlsP.add(hideB);
        controlsP.add(temp1);	
        //controlsP.add(sfile);
        controlsP.add(sendf);
        //controlsP.add(efile);
        //controlsP.add(dfile);
        //controlsP.add(temp2);
        controlsP.add(svoice);
        //controlsP.add(rvoice);
        controlsP.add(exitB);
       
	
        //adding to controls and Panel to Frame
        add(statusL, BorderLayout.NORTH);
        add(chatsP, BorderLayout.CENTER);
        add(controlsP, BorderLayout.SOUTH);
        //adding Listeners on Send Button and Exit Button        
        sendB.addActionListener(this);
        showB.addActionListener(this);
        hideB.addActionListener(this);
        exitB.addActionListener(this);
        //sfile.addActionListener(this);
        dfile.addActionListener(this);
        efile.addActionListener(this);
        sendf.addActionListener(this); 
        svoice.addActionListener(this);
        rvoice.addActionListener(this);

        setSize(500, 500);//setting size of Frame/window
        setLocation(0, 0);//setting location of Frame/window on the screen
        setBackground(Color.ORANGE);//setting background for Frame/window
        setVisible(true);//setting it to visible state                
        
        Listen();
    }
    
    public void Listen() {
        try {
            serverSocket = new ServerSocket(port);            
            statusL.setText("Listening on ip:" + serverSocket.getInetAddress().getHostAddress() + " and port:" + port);
            socket = serverSocket.accept();
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            bufferedWriter.write("Connected successfully");
            bufferedWriter.newLine();
            bufferedWriter.flush();
            Thread th;
            th = new Thread(this);
            th.start();
        } catch (Exception e) {
            statusL.setText(e.getMessage());
        }
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(exitB)) {
            System.exit(0);
        } 
        else if(e.getSource().equals(sendB)){
	try {
		String str = messageTF.getText();
		String encryptedString = encrypt(str,secret);
                		bufferedWriter.write(encryptedString);
                		bufferedWriter.newLine();
                		bufferedWriter.flush();
		chatsL.add("You: " + encryptedString);
                		decryptchatsL.add("You: " + str);
                		messageTF.setText("");
            } catch (IOException ioe) {
                statusL.setText(ioe.getMessage());
            }
        }
        else if(e.getSource().equals(showB)){
	decryptchatsL.setVisible(true);
	}
               else if(e.getSource().equals(sendf))
	{
		FileDialog file=new FileDialog(this,"select file",FileDialog.LOAD);
		file.setVisible(true);
		s=file.getFile();
		
		chatsL.add("You Selected File : " + s);
        decryptchatsL.add("You Selected File : " + s);
						

		BufferedReader br=new BufferedReader(new InputStreamReader(System.in));

		 try{
			 
			bufferedWriter.write(s);
			bufferedWriter.newLine();
			bufferedWriter.flush();
			din=new DataInputStream(socket.getInputStream());
        	dout=new DataOutputStream(socket.getOutputStream());
            FileInputStream fstream = new FileInputStream(s);
              			// Get the object of DataInputStream
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader bcr = new BufferedReader(new InputStreamReader(in));
  
            //dout.writeUTF(s);
            System.out.println("Sending File " + s);
			chatsL.add("\n Sending File : "+s);
            String s1;
            chatsL.add("\n");
                    		while((s1=bcr.readLine())!=null)
                    		{
								System.out.println(s1);
								//String estr = encrypt(s1,secret);
                    			//System.out.println(""+s1);
                    			chatsL.add(s1+"\n");
                    			//dout.writeUTF(s1);
								bufferedWriter.write(s1);
								//System.out.println(estr);
                    			//dout.flush();
								bufferedWriter.flush();
                    			//Thread.currentThread().sleep(500);
				   
                    		}
			chatsL.add("\n sent ...") ;   
                }catch(Exception ex)
	{System.out.println("Enter Valid File Name");
	}
	}
       else if(e.getSource().equals(efile))
	{
	}
       else if(e.getSource().equals(dfile))
	{
	}
       else if(e.getSource().equals(svoice))
	{
	}
       else if(e.getSource().equals(rvoice))
	{
	}
        else {
	decryptchatsL.setVisible(false);
	}
    }

    public void run() {
        try {
            socket.setSoTimeout(1000);
        } catch (Exception e) {
        }
        statusL.setText("Client Connected");
        while (true) {
            try {           
                		String message = bufferedReader.readLine();
                		if(message == null) {
                    		serverSocket.close();
                    		break;
                		}
                		chatsL.add("Client: " + message);
		String decryptedString = decrypt(message,secret);
		decryptchatsL.add("Client: "+decryptedString);
            } catch (IOException ioe) {                
                
            }
        }                
        
        Listen();
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
        new Server("Server Program");
    }
}
