package arkuni.util.hostfile;

import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class HostFileChangerPerl {
	private JButton runBtn;
	private JTextArea runResult;
	private JFrame frame;
	private JScrollPane scrollpane;
	private JPanel jpanel;
	public void createFrame() {
		frame = new JFrame("Host file Change");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(600,300);
		frame.setVisible(true);
		frame.setLayout(new FlowLayout());
		
		
		runResult = new JTextArea(10,40);
		scrollpane = new JScrollPane(runResult);
		scrollpane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		runBtn = new JButton("RUN");
		//runBtn.setBorderPainted(false);
		//runBtn.setFocusPainted(false);
		runBtn.setContentAreaFilled(false);
		runBtn.addActionListener(new ButtonListener());
		
		jpanel = new JPanel();
		jpanel.add(runBtn);
		jpanel.add(scrollpane);
		Container contentPane = frame.getContentPane();
		contentPane.add(jpanel);
		

	}
	class ButtonListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			StringBuilder sb = new StringBuilder();
			String s;
			try {
				Process oProcess = new ProcessBuilder("C:\\Perl\\bin\\perl.exe", "D:\\MyData\\desktop\\perl\\change_host.pl").start();
				BufferedReader stdOut   = new BufferedReader(new InputStreamReader(oProcess.getInputStream()));
				
				while ((s =   stdOut.readLine()) != null) {
					sb.append(s);
					sb.append("\n");
				}
				
				runResult.setDragEnabled(true);
				runResult.setText(sb.toString());

			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			
		}
		
	}
	//class ButtonListener implements 
	public static void main(String[] args) {
		
		HostFileChangerPerl host = new HostFileChangerPerl();
		host.createFrame();
		
	}
}
