import java.awt.Color;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Font;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;

public class MainMenu
{
    private static JLabel header = new JLabel("Solitaire Pack");
    private static JButton playGameButton = new JButton("Play Game");
    private static JButton viewStatisticsButton = new JButton("Statistics");
    private static JButton exitButton = new JButton("Exit");
    private static JList gameList = new JList();
    private static int selectedGame;
    public static final int TABLE_HEIGHT = Card.CARD_HEIGHT * 4;
    public static final int TABLE_WIDTH = (Card.CARD_WIDTH * 7) + 100;
    
    private static final JFrame frame = new JFrame("Bald Eagle Solitaire");
    protected static final JPanel table = new JPanel();
    
    
    private static class PlayGameListener implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{
            selectedGame = gameList.getSelectedIndex();
            frame.setVisible(false);
            frame.dispose();
            java.io.File dir;

            try
            {
                if (selectedGame == 0)
                {
                    BaldEagle.main(new String[0]);
                }
                else if (selectedGame == 1)
                {
                    dir = new java.io.File("/binarystar");
                    URL loadPath = dir.toURI().toURL();
                    URL[] classUrl = new URL[]{loadPath};
                    ClassLoader cl = new URLClassLoader(classUrl);
                }
                else if (selectedGame == 2)
                {
                    dir = new java.io.File("/bonanzacreek");
                    URL loadPath = dir.toURI().toURL();
                    URL[] classUrl = new URL[]{loadPath};
                    ClassLoader cl = new URLClassLoader(classUrl);
                }
                else if (selectedGame == 3)
                {
                    dir = new java.io.File("/betsyross");
                    URL loadPath = dir.toURI().toURL();
                    URL[] classUrl = new URL[]{loadPath};
                    ClassLoader cl = new URLClassLoader(classUrl);
                }
            }
            catch (MalformedURLException ue)
            {
                ue.printStackTrace();
            }
		}

    }

    private static class ViewStatisticsListener implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{
            String selectedGame = (String) gameList.getSelectedValue();
            Statistics.display(selectedGame.replaceAll(" ", "_"));
		}

    }

    private static class exitListener implements ActionListener
    {
        @Override
        public void actionPerformed(ActionEvent e)
        {
            frame.dispose();
        }
    }

    public static void initializeMenu()
    {
        playGameButton.setBounds(325, 260, 100, 30);
        playGameButton.addActionListener(new PlayGameListener());

        viewStatisticsButton.setBounds(325, 300, 100, 30);
        viewStatisticsButton.addActionListener(new ViewStatisticsListener());

        exitButton.setBounds(325, 340, 100, 30);
        exitButton.addActionListener(new exitListener());

        header.setFont(new Font("Helvetica",Font.ITALIC+Font.BOLD, 57));
        header.setForeground(Color.WHITE);
        header.setBounds(210, 0, 600, 100);

        String games[] = {"Bald Eagle", "Binary Star", "Bonanza Creek", "Betsy Ross"};
        gameList = new JList(games);
        gameList.setBounds(275, 160, 200, 75);

        table.add(gameList);
        table.add(header);
        table.add(playGameButton);
        table.add(viewStatisticsButton);
        table.add(exitButton);
    }

    public static void open()
    {
        frame.setVisible(true);
    }
    
    public static void main(String[] args)
	{
        Container contentPane;
		frame.setSize(TABLE_WIDTH, TABLE_HEIGHT);

		table.setLayout(null);
		table.setBackground(new Color(60, 60, 60));

		contentPane = frame.getContentPane();
		contentPane.add(table);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        initializeMenu();
        frame.setVisible(true);
    }
}