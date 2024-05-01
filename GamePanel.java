import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import java.util.Random;



public class GamePanel extends JPanel implements ActionListener{

	static final int SCREEN_WIDTH = 600;
	static final int SCREEN_HEIGHT = 600;
	static final int UNIT_SIZE = 30;
	static final int GAME_UNITS = (SCREEN_WIDTH*SCREEN_HEIGHT)/UNIT_SIZE;
	static int DELAY = 80;
	final int x[] = new int[GAME_UNITS];
	final int y[] = new int[GAME_UNITS];
	int bodyParts=1;
	int applesEaten = 0;
	int appleX;
	int appleY;
	char direction = 'R'; //U,R,D,L
	boolean running = false;
	boolean gridLines = false;
	boolean rainbow = false;
	boolean started = false;
	Timer timer;
	Random random;
	String jumpSound;
	JumpSoundEffect jse = new JumpSoundEffect();


	GamePanel()
	{
		random = new Random();
		this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
		this.setBackground(Color.black);
		this.setFocusable(true);
		this.addKeyListener(new MyKeyAdapter());
		jumpSound = ".//res//jump.wav";
		startGame();

	}

	public void startGame()
	{
		newApple();
		running = true;
		timer = new Timer(DELAY, this);
		timer.start();
	}

	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		draw(g);
	}

	public void draw(Graphics g)
	{
		g.setColor(Color.DARK_GRAY);


		if(running)
		{



			if(started)
			{
				if (gridLines)
					for(int i = 0; i < SCREEN_HEIGHT/UNIT_SIZE; i++) 
					{
						g.drawLine(i*UNIT_SIZE, 0, i*UNIT_SIZE, SCREEN_HEIGHT);
						g.drawLine(0, i*UNIT_SIZE, SCREEN_WIDTH, i*UNIT_SIZE);
					}

				g.setColor(Color.red);
				g.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE);

				for(int i = 0; i < bodyParts; i++)
				{
					if (i == 0) 
					{
						if (rainbow)
							g.setColor(Color.WHITE);
						else 
							g.setColor(Color.green);
						g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
					}
					else
					{
						if (rainbow)
							g.setColor(new Color(random.nextInt(255),random.nextInt(255),random.nextInt(255)));
						else
							g.setColor(new Color(45,180,0));

						g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
					}
				}
				g.setColor(Color.LIGHT_GRAY);
				g.setFont(new Font ("", Font.BOLD, 40));
				FontMetrics metrics = getFontMetrics(g.getFont());
				g.drawString("Score: " + applesEaten,
						(SCREEN_WIDTH - metrics.stringWidth("Score: " + applesEaten))/2,
						g.getFont().getSize());



			}
			else
			{
				g.setColor(Color.green);
				g.setFont(new Font ("", Font.BOLD, 80));
				FontMetrics metrics = getFontMetrics(g.getFont());
				g.drawString("SNAKE",(SCREEN_WIDTH - metrics.stringWidth("SNAKE"))/2, g.getFont().getSize()+50);
				g.setFont(new Font ("", Font.BOLD, 30));
				g.setColor(Color.gray);
				metrics = getFontMetrics(g.getFont());
				g.drawString("Arrow keys or WASD to move",(SCREEN_WIDTH - metrics.stringWidth("Arrow keys or WASD to move"))/2, g.getFont().getSize()+150);
				g.drawString("Press enter to start/restart",(SCREEN_WIDTH - metrics.stringWidth("Press enter to start/restart"))/2, g.getFont().getSize()+200);




			}
		}
		else
			gameOver(g);

	}

	public void newApple()
	{
		appleX = random.nextInt((int)(SCREEN_WIDTH/UNIT_SIZE))*UNIT_SIZE;
		appleY = random.nextInt((int)(SCREEN_HEIGHT/UNIT_SIZE))*UNIT_SIZE;
	}

	public void move()
	{
		if(started)
		{
			for(int i = bodyParts; i > 0; i--)
			{
				x[i] = x[i-1];
				y[i] = y[i-1];
			}
			switch(direction)
			{
			case 'U' : 
				y[0] = y[0] - UNIT_SIZE;
				break;
			case 'D' : 
				y[0] = y[0] + UNIT_SIZE;
				break;
			case 'L' : 
				x[0] = x[0] - UNIT_SIZE;
				break;
			case 'R' : 
				x[0] = x[0] + UNIT_SIZE;
				break;
			}
		}
	}

	public void checkApple()
	{
		if((x[0] == appleX) && (y[0] == appleY))
		{
			bodyParts++;
			applesEaten++;
			if(DELAY > 20)
				DELAY-=3;
			newApple();
		}
	}	

	public void checkCollisions()
	{
		if(started)
		{
			//if collides with body
			for(int i = bodyParts; i > 0; i--)
				if((x[0] == x[i]) & (y[0] == y[i]))
					running = false;

			//if collides with border
			//left
			if (x[0] < 0)
				running = false;
			//right
			if (x[0] >  SCREEN_WIDTH-UNIT_SIZE)
				running = false;
			//top
			if (y[0] < 0)
				running = false;
			//bottom
			if (y[0] > SCREEN_HEIGHT-UNIT_SIZE)
				running = false;




			if(!running)
				timer.stop();
		}
	}

	public void gameOver(Graphics g)
	{
		g.setColor(Color.white);
		g.setFont(new Font ("", Font.BOLD, 75));
		FontMetrics metrics = getFontMetrics(g.getFont());
		g.drawString("Game Over",
				(SCREEN_WIDTH - metrics.stringWidth("Game Over"))/2,
				SCREEN_HEIGHT/2);

		g.setColor(Color.LIGHT_GRAY);
		g.setFont(new Font ("", Font.BOLD, 40));
		metrics = getFontMetrics(g.getFont());
		g.drawString("Score: " + applesEaten,
				(SCREEN_WIDTH - metrics.stringWidth("Score: " + applesEaten))/2,
				g.getFont().getSize());


	}	


	@Override
	public void actionPerformed(ActionEvent e) {

		if (running)
		{
			move();
			checkApple();
			checkCollisions();

		}
		repaint();
	}

	public class MyKeyAdapter extends KeyAdapter
	{
		@Override
		public void keyPressed(KeyEvent e)
		{
			switch(e.getKeyCode()) 
			{
			case KeyEvent.VK_LEFT:
				if (direction!= 'R')
					direction = 'L';
				break;
			case KeyEvent.VK_A:
				if (direction!= 'R')
					direction = 'L';
				break;
			case KeyEvent.VK_RIGHT:
				if (direction!= 'L')
					direction = 'R';
				break;
			case KeyEvent.VK_D:
				if (direction!= 'L')
					direction = 'R';
				break;
			case KeyEvent.VK_UP:
				if (direction!= 'D')
					direction = 'U';
				break;
			case KeyEvent.VK_W:
				if (direction!= 'D')
					direction = 'U';
				break;
			case KeyEvent.VK_DOWN:
				if (direction!= 'U')
					direction = 'D';
				break;
			case KeyEvent.VK_S:
				if (direction!= 'U')
					direction = 'D';
				break;
			case KeyEvent.VK_G:
				gridLines = !gridLines;
				break;
			case KeyEvent.VK_R:
				rainbow = !rainbow;
				break;
			case KeyEvent.VK_ENTER:
					started = true;
				
			}
		}
	}




	//ignore this. I wanted to scare the shit out of my cousin
	public class JumpSoundEffect
	{
		Clip clip;

		public void setFile(String jumpSoundEffect)
		{
			try
			{
				File file = new File(jumpSoundEffect);
				AudioInputStream sound = AudioSystem.getAudioInputStream(file);
				clip = AudioSystem.getClip();
				clip.open(sound);
			}
			catch(Exception e)
			{	
				/**/
			}
		}

		public void playSound()
		{
			clip.setFramePosition(0);
			clip.start();
		}
	}

	public class ImageViewer
	{

		public ImageViewer()
		{
			SwingUtilities.invokeLater(new Runnable()
			{
				public void run() 
				{
					JFrame frame = new JFrame("Image Viewer");
					frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
					BufferedImage img = null;

					try 
					{
						img = ImageIO.read(getClass().getResource("/image.png"));
					}
					catch(Exception e)
					{
						e.printStackTrace();
						System.exit(1);
					}

					ImageIcon imgIcon = new ImageIcon(img);
					JLabel lbl = new JLabel();
					lbl.setIcon(imgIcon);
					frame.getContentPane().add(lbl, BorderLayout.CENTER);
					frame.pack();
					frame.setLocationRelativeTo(null);
					frame.setVisible(true);
				}

			});
		}		

	}
}






























