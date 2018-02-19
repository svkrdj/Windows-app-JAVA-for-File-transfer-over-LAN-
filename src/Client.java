
import java.io.IOException;
import java.io.*;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.io.File;
import java.awt.event.ActionEvent;
import javax.swing.JProgressBar;

public class Client {

	private JFrame frame;
	private JTextField textField;
	private JTextField textField_1;
	public String path;
	private Socket socket = null;
	private  FileInputStream fis = null;
    private BufferedInputStream bis = null;
	private OutputStream os = null;
	private boolean isConnected = false;
	private ObjectOutputStream outputStream = null;
	private String sourceFilePath;
	private fileevent fileEvent = null;
	String ip;
	private JProgressBar progressBar;
	private JLabel lblNewLabel_1;
	private File file;
	private double len;

	/**
	 * Launch the application.
	 */
	public static void Yo() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Client window = new Client();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Client() {
		initialize();
	}
	public void Connect()
	{
		ip=textField.getText();
		Runnable task=new Runnable()  {
            @Override
            public void run() {
		while (!isConnected) {
			try {
			socket = new Socket(ip, 4445);
			outputStream = new ObjectOutputStream(socket.getOutputStream());
			
			isConnected = true;
			sendFileDetail();
			} catch (IOException e) {
			e.printStackTrace();
			}
			}
            }
	};
	Thread serverThread = new Thread(task);
    serverThread.start();
	}
	
	public void sendFileDetail()
	{
		fileEvent=new fileevent();
		String filename= sourceFilePath.substring(sourceFilePath.lastIndexOf("/") + 1, sourceFilePath.length());
		//String path = sourceFilePath.substring(0, sourceFilePath.lastIndexOf("/") + 1);
		//fileEvent.setDestinationDirectory(destinationPath);
		fileEvent.setFilename(filename);
		if(file.isFile())
		{
			try {
			
				fileEvent.setFileSize(len);
				fileEvent.setStatus("Success");
				
				}	
			
			catch (Exception e) {
				e.printStackTrace();
				fileEvent.setStatus("Error");
				}
				}
		else
		{
			    JOptionPane.showMessageDialog(null, "path specified is not pointing to a file");
				fileEvent.setStatus("Error");
		}
		try {
			outputStream.writeObject(fileEvent);
			sendFileData();
		}catch (IOException e) {
			e.printStackTrace();
			}

		}
	public void sendFileData() {
		
		
		progressBar = new JProgressBar(0,100);
		progressBar.setValue(0);    
		progressBar.setStringPainted(true); 
		progressBar.setBounds(45, 216, 324, 14);
		frame.getContentPane().add(progressBar);
		try {
	    os=socket.getOutputStream();
	    File myFile = new File (sourceFilePath);
	    byte [] mybytearray  = new byte [16000];
        fis = new FileInputStream(myFile);
        bis = new BufferedInputStream(fis);
        int count;
        int sent=0;
        while((count=bis.read(mybytearray))>0)
        {
        	
        	sent=sent+count;
            os.write(mybytearray,0,count);
        	//System.out.println(count+"  "+sent+" / "+len+"  "+sent/len);
            int per=(int )((sent/len)*100);
          //  System.out.println(per);
        	
        	progressBar.setValue(per);
        	progressBar.setString(per+"%");
        	
        }
        os.flush();
        bis.close();
        fis.close();
		JOptionPane.showMessageDialog(null, "Done...Going to exit");
		Thread.sleep(3000);
		System.exit(0);
		}
		catch(IOException e){
			e.printStackTrace();
	   } catch (InterruptedException e) {
			e.printStackTrace();
	   }
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JButton btnNewButton = new JButton("Send");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Connect();
			}
		});
		btnNewButton.setBounds(174, 158, 89, 23);
		frame.getContentPane().add(btnNewButton);
		
		JLabel lblNewLabel = new JLabel("Enter IP: ");
		lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 17));
		lblNewLabel.setBounds(45, 45, 83, 34);
		frame.getContentPane().add(lblNewLabel);
		
		textField = new JTextField();
		textField.setText("192.168.");
		textField.setBounds(136, 45, 233, 30);
		frame.getContentPane().add(textField);
		textField.setColumns(10);
		
		// JOptionPane.showMessageDialog(null, ip);
		
		JLabel lblS = new JLabel("Browse File:");
		lblS.setFont(new Font("Tahoma", Font.PLAIN, 17));
		lblS.setBounds(45, 101, 100, 21);
		frame.getContentPane().add(lblS);
		
		textField_1 = new JTextField();
		textField_1.setBounds(136, 96, 184, 28);
		frame.getContentPane().add(textField_1);
		textField_1.setColumns(10);
		
		JButton btnNewButton_1 = new JButton("Browse");
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
				int result = fileChooser.showOpenDialog(null);
			    if (result == JFileChooser.APPROVE_OPTION) {
				    File selectedFile = fileChooser.getSelectedFile();
				    sourceFilePath =selectedFile.getAbsolutePath();
				    char sp[]=sourceFilePath.toCharArray();
				    for(int i=0;i<sourceFilePath.length();i++)
				    {
				    	if(sp[i]=='\\')
				    	{
				    		sp[i]='/';
				    	}
				    			
				    }
				    sourceFilePath=String.valueOf(sp);
				   // JOptionPane.showMessageDialog(null, sourceFilePath);
				    textField_1.setText(sourceFilePath);
				    file=new File(sourceFilePath);
				    len =file.length();
				    System.out.println(len);
				    double ts=(double) ((len/1024.0)/1024.0);
				    ts=Double.parseDouble(new DecimalFormat("##.##").format(ts));
					lblNewLabel_1.setText("File Size: "+ts+" MB");
					String s=""+ts+" MB";
					System.out.println(s);
					}
			}
		});
		btnNewButton_1.setBounds(336, 101, 89, 23);
		frame.getContentPane().add(btnNewButton_1);
		
		lblNewLabel_1 = new JLabel("");
		lblNewLabel_1.setBounds(136, 133, 138, 14);
		frame.getContentPane().add(lblNewLabel_1);
		
		
	}
}
