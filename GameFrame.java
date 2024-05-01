import javax.swing.JFrame;

public class GameFrame extends JFrame{

	GameFrame()
	{
		GamePanel Panel = new GamePanel();
		this.add(Panel);
		this.setTitle("SnakeGame");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.pack();
		this.setVisible(true);
		this.setLocationRelativeTo(null);;


	}
}

