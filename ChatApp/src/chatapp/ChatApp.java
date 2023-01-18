package chatapp;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import java.awt.Font;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.awt.event.ActionEvent;
import javax.swing.JTextPane;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

public class ChatApp {
	
	static int check = 0;
	private JFrame frame;
	private JTextField nameField;
	private JTextField messageField;
	

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ChatApp window = new ChatApp();
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
	public ChatApp() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame("Chat Application Java");
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JLayeredPane layeredPane = new JLayeredPane();
		layeredPane.setBounds(0, 0, 434, 261);
		frame.getContentPane().add(layeredPane);
		
		JPanel Login = new JPanel();
		Login.setVisible(true);
		Login.setLayout(null);
		Login.setBounds(0, 0, 434, 261);
		layeredPane.add(Login);
		
		nameField = new JTextField();
		nameField.setColumns(10);
		nameField.setBounds(130, 101, 173, 20);
		Login.add(nameField);
		
		JButton enterNameButton = new JButton("Set");
		
		enterNameButton.setBounds(175, 132, 89, 23);
		Login.add(enterNameButton);
		
		JLabel enterNameLabel = new JLabel("Enter Name");
		enterNameLabel.setHorizontalAlignment(SwingConstants.CENTER);
		enterNameLabel.setFont(new Font("Trebuchet MS", Font.BOLD, 23));
		enterNameLabel.setBounds(10, 45, 414, 45);
		Login.add(enterNameLabel);
		
		JPanel Chat = new JPanel();
		Chat.setVisible(false);
		Chat.setBounds(0, 0, 434, 261);
		layeredPane.add(Chat);
		Chat.setLayout(null);
		
		JButton sendButton = new JButton("Send");
		
		sendButton.setBounds(335, 230, 89, 23);
		Chat.add(sendButton);
		
		messageField = new JTextField();
		messageField.setBounds(10, 230, 310, 23);
		Chat.add(messageField);
		messageField.setColumns(10);
		
		JLabel nameLabel = new JLabel("Welcome: ");
		nameLabel.setBounds(10, 10, 300, 20);
		Chat.add(nameLabel);
		
		JTextPane messageBox = new JTextPane();
		messageBox.setEditable(true);
		messageBox.setBounds(0,0, 414, 190);
		
		JScrollPane scrollPane = new JScrollPane(messageBox, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		scrollPane.setBounds(10, 31, 414, 190);
		Chat.add(scrollPane);
		
		JButton refreshButton = new JButton("Refresh");
		refreshButton.setBounds(334, 7, 89, 23);
		Chat.add(refreshButton);
		
		enterNameButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Login.setVisible(false);
				Chat.setVisible(true);
				Connection conn;
				try {
					int id = 0;
					conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/chat-app", "root", "");
					String sqlCheckNameExist = " Select * from user where name = '"+nameField.getText()+"' ";
					Statement stmt1 = conn.createStatement();
					stmt1.execute(sqlCheckNameExist);
					ResultSet rs = stmt1.getResultSet();
					if (rs.isBeforeFirst() ) {
						nameLabel.setText("Welcome: " + nameField.getText());
					}else {
						String sqlNewName = "INSERT INTO `user`(`name`) VALUES ('"+nameField.getText()+"')";
						Statement stmt2 = conn.createStatement();
						stmt2.execute(sqlNewName);
						nameLabel.setText("Welcome: " + nameField.getText());
						JOptionPane.showMessageDialog(sendButton, "Name Added");
					}
					while(rs.next()) {
						id = rs.getInt("id");
					}
					String sqlRetrieveChat = "SELECT `id`, `message`, `userID` FROM `message` ";
					Statement stmt3 = conn.createStatement();
					stmt3.execute(sqlRetrieveChat);
					ResultSet rs2 = stmt3.getResultSet();
					
					while(rs2.next()) {
						messageBox.setText(messageBox.getText() + rs2.getString("message") + "\n");
					}
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});

		sendButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/chat-app", "root", "");
					
					String sqlCheckNameExist = " Select id from user where name = '"+nameField.getText()+"' ";
					int id = 0;
					Statement stmt1 = conn.createStatement();
					stmt1.execute(sqlCheckNameExist);
					ResultSet rs = stmt1.getResultSet();
					while(rs.next()) {
						id = rs.getInt("id");
					}
					String sqlSendMessage = " INSERT INTO `message`(`message`, `userID`) VALUES ('"+nameField.getText()+": "+messageField.getText()+"','"+id+"') ";
					
					if(check == 0) {
						messageBox.setText(messageBox.getText() + ""+nameField.getText()+": " + messageField.getText());
						check = 1;
					}else {
						messageBox.setText(messageBox.getText() + "\n"+nameField.getText()+": " + messageField.getText());
					}
					messageField.setText("");
					Statement stmt2 = conn.createStatement();
					stmt2.execute(sqlSendMessage);
					
					messageBox.setText("");
					
					String sqlRetrieveChat = "SELECT `id`, `message`, `userID` FROM `message` ";
					Statement stmt3 = conn.createStatement();
					stmt3.execute(sqlRetrieveChat);
					ResultSet rs2 = stmt3.getResultSet();
					
					while(rs2.next()) {
						messageBox.setText(messageBox.getText() + rs2.getString("message") + "\n");
					}
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		
		refreshButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/chat-app", "root", "");
					messageBox.setText("");
					
					String sqlRetrieveChat = "SELECT `id`, `message`, `userID` FROM `message` ";
					Statement stmt3 = conn.createStatement();
					stmt3.execute(sqlRetrieveChat);
					ResultSet rs2 = stmt3.getResultSet();
					
					while(rs2.next()) {
						messageBox.setText(messageBox.getText() + rs2.getString("message") + "\n");
					}
				}catch(Exception e2) {System.out.println(e2);}
			}
		});
	}
}
