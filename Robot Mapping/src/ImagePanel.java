import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;


public class ImagePanel extends JPanel
{
	private BufferedImage image;
	private int width;
	private int height;
	
	public ImagePanel(int w, int h) throws IOException
	{
		Image temp = ImageIO.read(new File("new_hallway_map.jpg"));
		width = w;
		height = h;		
		
		//image = temp.getScaledInstance(800, 600, Image.SCALE_DEFAULT);
		image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics g= image.getGraphics();
		
		g.drawImage(temp, 0, 0, width, height, null);
		g.setColor(Color.YELLOW);
		g.fillOval(300, 300, 4, 4);
		g.dispose();		
		
		ImageIO.write(image, "png", new File("resizedImage.png"));
		//image = ImageIO.read(new File("resizedImage.png"));
	}
	
	protected void paintComponent(Graphics g)
	{				
		super.paintComponent(g);
		g.drawImage(image, 0, 0, width, height, null);		
	}
	
}
