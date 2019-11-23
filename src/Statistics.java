import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Color;
import java.io.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class Statistics extends JPanel
{
    private static JButton clearButton = new JButton("Clear");
    private static JButton closeButton = new JButton("Close");
    public static final int TABLE_HEIGHT = 500;
    public static final int TABLE_WIDTH = 500;
    private static boolean statsExist;
    private static FileWriter fileWriter;
    private static PrintWriter printWriter;
    private static String game;
    private static String[] columnNames = {"Name", "Status", "Score", "Time"};
    
    private static JFrame frame;
    private static JTable stats = new JTable();
    private static DefaultTableModel model;
    private static java.util.Map<String, Boolean> gameInit = new java.util.HashMap<>();

    public Statistics(String name)
    {
        gameInit.putIfAbsent(name, false);
        JPanel buttonPanel = new JPanel(new GridLayout(1, 0, 5, 5));
        if (!gameInit.get(name))
        {
            closeButton.addActionListener(new CloseListener());
            clearButton.addActionListener(new ClearListener());
            gameInit.put(name, true);
        }

        buttonPanel.add(closeButton);
        buttonPanel.add(clearButton);

        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(new Color(60, 60, 60));
        bottomPanel.add(buttonPanel);

        model = new DefaultTableModel(columnNames, 0);

        refreshStatistics();
        stats.setModel(model);

        JPanel topPanel = new JPanel(new GridLayout(1, 0, 5, 5));
        JLabel banner = new JLabel("Statistics - " + name);
        banner.setFont(new Font("Helvetica",Font.ITALIC+Font.BOLD, 30));
        banner.setForeground(Color.WHITE);
        topPanel.setBackground(new Color(60, 60, 60));
        topPanel.add(banner);

        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        setLayout(new BorderLayout(5, 5));
        add(new JScrollPane(stats), BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.PAGE_END);
        add(topPanel, BorderLayout.PAGE_START);
    }


    private static class CloseListener implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{
            frame.dispose();
		}

    }

    private static class ClearListener implements ActionListener
    {
        @Override
        public void actionPerformed(ActionEvent e)
        {
            frame.setVisible(false);
            java.io.File statistics = new java.io.File("res\\" + game + "_statistics.txt");

            try
            {
                if (!statistics.exists())
                {
                    statistics.getParentFile().mkdirs();
                    statistics.createNewFile();
                }

                statsExist = true;
                fileWriter = new FileWriter(statistics);
                printWriter = new PrintWriter(fileWriter);
                printWriter.close();
                display(game);
            }
            catch (IOException ie)
            {
                ie.printStackTrace();
            }
        }
    }

    public static void updateStatistics(GameStatus gs, String name)
    {
        int gameStatusFlag = gs.getGameStatusFlag();
        int gameScore = gs.getGameScore();
        int gameTime = gs.getGameTime();
        game = name.toLowerCase();
        statsExist = false;

        java.io.File statistics = new java.io.File("res\\" + game + "_statistics.txt");

        try
        {
            if (!statistics.exists())
            {
                statistics.getParentFile().mkdirs();
                statistics.createNewFile();
            }
            else
            {
                statsExist = true;
            }

            if (!statsExist)
            {
                fileWriter = new FileWriter(statistics);
                statsExist = true;
            }
            else
            {
                fileWriter = new FileWriter(statistics, true);
            }

            printWriter = new PrintWriter(fileWriter);
            printWriter.printf("%s:%d:%d:%d\n", "Bald Eagle", gameStatusFlag, gameScore, gameTime);

            printWriter.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private static void refreshStatistics()
    {
        java.io.File statistics = new java.io.File("res\\" + game + "_statistics.txt");
        try
        {
            BufferedReader br = new BufferedReader(new FileReader(statistics));
            String line;

            while ((line = br.readLine()) != null)
            {
                String[] data = line.split(":");
                switch (Integer.parseInt(data[1]))
                {
                    case 0:
                        data[1] = "loss";
                        break;
                    case 1:
                        data[1] = "forfeit";
                        break;
                    case 2:
                        data[2] = "win";
                        break;
                }
                model.addRow(data);
            }

            br.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void display(String name)
    {
        frame = new JFrame("Statistics");
        game = name.toLowerCase();
        Statistics mainPanel = new Statistics(game);
        frame.setSize(TABLE_WIDTH, TABLE_HEIGHT);

        mainPanel.setBackground(new Color(60, 60, 60));

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Container contentPane;
        contentPane = frame.getContentPane();
        contentPane.add(mainPanel);
        frame.pack();
        frame.setLocationByPlatform(true);
        frame.toFront();
        frame.repaint();
        frame.setVisible(true);
    }
}