import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JComboBox;

public class MainMenu
{
    private static JLabel header = new JLabel("Bald Eagle Solitaire");
    private static JButton playGameButton = new JButton("Play Game");
    public static final int TABLE_HEIGHT = Card.CARD_HEIGHT * 4;
    public static final int TABLE_WIDTH = (Card.CARD_WIDTH * 7) + 100;
    
    private static final JFrame frame = new JFrame("Bald Eagle Solitaire");
    protected static final JPanel table = new JPanel();
    
    private static class PlayGameListener implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{
            BaldEagle.main(new String[0]);
		}

    }
    public static void initializeMenu()
    {
        playGameButton.setBounds(245, 20, 100, 100);
        playGameButton.addActionListener(new PlayGameListener());
        table.add(playGameButton);
    }
    
    public static void main(String[] args)
	{
        Container contentPane;

		frame.setSize(TABLE_WIDTH, TABLE_HEIGHT);

		table.setLayout(null);
		table.setBackground(new Color(0, 180, 0));

		contentPane = frame.getContentPane();
		contentPane.add(table);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        initializeMenu();
    }
}