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
import java.awt.Font; 
import java.awt.Color; 
import java.util.Timer;
import java.util.TimerTask;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JTextArea;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.ImageIcon;
import javax.imageio.ImageIO;


public class Statistics
{
    private static JLabel header = new JLabel("Bald Eagle Solitaire - Statistics");
    private static JButton closeButton = new JButton("Close");
    public static final int TABLE_HEIGHT = 500;
    public static final int TABLE_WIDTH = 300;
    
    private static final JFrame frame = new JFrame("Bald Eagle Solitaire");
    protected static final JPanel table = new JPanel();
    

    private static class CloseListener implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{
            frame.dispose();
		}

    }

    public static void initializeMenu()
    {
        closeButton.setBounds(10, TABLE_HEIGHT-75, 100, 30);
        closeButton.addActionListener(new CloseListener());
        
        JLabel banner = new JLabel("Statistics");
        banner.setFont(new Font("Helvetica",Font.ITALIC+Font.BOLD, 30));
        banner.setForeground(Color.WHITE);
        banner.setBounds(10, 10, 200, 30);

        // reading statistics file to display in window
        // to be finished
        /* JTextArea text = new JTextArea();
        FileReader fr = new FileReader(new File("src\\statistics\\SampleStatistics.txt"));
        BufferedReader br = new BufferedReader(fr);
        String line = br.readLine();
        while(line != null){
            text.append(line + "\n");
            line = br.readLine();
        } */
        
        table.add(banner);
        table.add(closeButton);
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
        frame.setVisible(true);

        initializeMenu();
    }
}