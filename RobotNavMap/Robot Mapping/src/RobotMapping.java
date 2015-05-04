import javax.swing.*;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
	
public class RobotMapping
{
	public RobotMapping() throws IOException
	{		
		ImagePanel imagePanel = new ImagePanel(1300, 700);
		JFrame frame = new JFrame("Robot Mapping");
		
		ImageIcon image = new ImageIcon("resizedImage.png");
		JScrollPane jsp = new JScrollPane(new JLabel(image));
		
		jsp.getVerticalScrollBar().setUnitIncrement(100);
		
		frame.getContentPane().add(jsp);
		
		
//		JScrollPane scrollPane = new JScrollPane(imagePanel);
//		scrollPane.setAutoscrolls(true);
//		scrollPane.setPreferredSize(new Dimension(400, 300));
//		
//		frame.getContentPane().add(scrollPane, BorderLayout.CENTER);

		frame.setSize(1300, 700);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH); 
		frame.setVisible(true);
	}
	
	public static void main(String[] args) throws IOException
	{
		RobotMapping map = new RobotMapping();
	}
}
