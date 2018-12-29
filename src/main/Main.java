import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicProgressBarUI;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class Main {

    private JLabel dayHeader, situationName, situationMessenger, economyDifference, militaryDifference,
            happinessDifference, religionDifference;
    private JProgressBar economyBar, militaryBar, religionBar, happinessBar;
    private JTextArea situationText;
    private JButton button1, button2;
    private JFrame frame;
    private int width, height;
    private GameLoop gameLoop;
    private boolean fullScreen;

    public static void main(String[] args) {
        (new Main()).createPanel();
    }

    private void createPanel() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        width = (int) screenSize.getWidth();
        height = (int) screenSize.getHeight();

        requestFullScreen();
    }

    private void requestFullScreen() {
        createNewFrame();
        frame.setBounds((int)(width*0.5)-250, (int)(height*0.5)-250, 500,  500);

        JLabel label = new JLabel("Full Screen Mode");
        JButton yes = new JButton("Yes (recommended)");
        JButton no = new JButton("No");

        styleLabel(label, Color.WHITE, 25);
        styleButton(yes, 20);
        styleButton(no, 20);

        frame.add(label);
        frame.add(yes);
        frame.add(no);

        label.setBounds(150, 30, 300, 100);
        yes.setBounds(100, 130, 300, 100);
        no.setBounds(100, 260, 300, 100);

        yes.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fullScreen = true;
                launchScreen();
            }
        });
        no.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fullScreen = false;
                launchScreen();
            }
        });
    }

    private void createNewFrame() {
        JFrame oldFrame = frame;

        frame = new JFrame();
        frame.getContentPane().setBackground(Color.BLACK);

        if (oldFrame != null) {
            if (!fullScreen) {
                oldFrame.setContentPane(frame.getContentPane());
                frame = oldFrame;
            } else {
                oldFrame.dispose();
                frame.setUndecorated(true);
                fullScreen = false;
            }
        }

        frame.setLayout(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("The Throne");
        frame.setIconImages(getIcons());
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setVisible(true);
    }

    private JTextArea createTextArea() {
        JTextArea text = new JTextArea(5, 20);
        text.setWrapStyleWord(true);
        text.setLineWrap(true);
        text.setOpaque(false);
        text.setEditable(false);
        text.setFocusable(false);
        text.setBackground(UIManager.getColor("Label.background"));
        text.setFont(UIManager.getFont("Label.font"));
        text.setBorder(UIManager.getBorder("Label.border"));
        return text;
    }

    private void instructionsScreen() {
        createNewFrame();

        JLabel instructionsHeader = new JLabel();
        JTextArea instructionsText = createTextArea();
        JButton playButton = new JButton("Play");

        frame.add(instructionsHeader);
        frame.add(instructionsText);
        frame.add(playButton);

        playButton.setVisible(false);

        styleButton(playButton, 25);
        styleLabel(instructionsHeader, Color.WHITE, 30);
        styleTextArea(instructionsText, Color.WHITE, 20);

        instructionsHeader.setBounds((int)(width*0.44), (int)(height*0.03), 500, 100);
        instructionsText.setBounds((int)(width*0.32), (int)(height*0.19), (int)(width*0.4), 400);
        playButton.setBounds((int)(width*0.3), (int)(height*0.75), (int)(width*0.4), (int)(height*0.12));

        new Thread() {
            @Override
            public void run() {
                try {
                    ScreenUtils.type("Instructions", 80, null, instructionsHeader);
                    sleep(2000);
                    instructionsText.setText("> You start out with 50 points in each resource category.\n\n"
                            + "> You are presented with a situation where you have 2 options to respond.\n\n"
                            + "> The option you pick will have predetermined consequences and impact your resources.\n\n"
                            + "> You are presented with situations until you lose the game by having one resource dropping below 0.\n\n"
                            + "> When you lose the amount of days you survived will pop up, that is your score.");
                    playButton.setVisible(true);
                    playButton.setFocusable(true);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }.start();

        playButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                requestName();
            }
        });
    }

    private void styleTextField(JTextField field, Color color, int size) {
        Font font = new Font(field.getFont().getName(), Font.PLAIN, size);
        field.setForeground(color);
        field.setFont(font);
        field.setBorder(BorderFactory.createLineBorder(Color.WHITE));
        Border border = field.getBorder();
        Border margin = new EmptyBorder(10,60,10,60);
        field.setBorder(new CompoundBorder(border, margin));
    }

    private void requestName() {
        createNewFrame();

        JLabel label = new JLabel();
        JTextField field = new JTextField();

        label.setVisible(true);
        field.setVisible(false);

        frame.add(label);
        frame.add(field);

        styleLabel(label, Color.WHITE, 25);
        styleTextField(field, Color.BLACK, 25);

        label.setBounds((int)(width*0.40), (int)(height*0.32), 500, 100);
        field.setBounds((int)(width*0.32), (int)(height*0.44), (int)(width*0.4), (int)(height*0.08));

        new Thread() {
            @Override
            public void run() {
                try {
                    ScreenUtils.type("What is your majesty's name?", 40, null, label);
                    sleep(2500);
                    field.setVisible(true);
                    frame.addKeyListener(new KeyAdapter() {
                        public void keyPressed(KeyEvent event) {
                            System.exit(0);
                        }
                    });
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }.start();

        field.setAction(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent event) {
                if (field.getText().replace(" ", "").equals("")) {
                    // DENY
                } else {
                    startGame(field.getText());
                }
            }
        });
    }

    public void deathScreen(String string) {
        createNewFrame();

        JLabel gameOver = new JLabel();
        JTextArea reason = createTextArea();
        JLabel pressAnyKey = new JLabel("Press any key to continue...");

        pressAnyKey.setVisible(false);

        frame.add(gameOver);
        frame.add(reason);
        frame.add(pressAnyKey);

        styleLabel(gameOver, Color.WHITE, 50);
        styleTextArea(reason, Color.WHITE, 25);
        styleLabel(pressAnyKey, Color.WHITE, 25);

        gameOver.setBounds((int)(width*0.44), (int)(height*0.32), 500, 100);
        reason.setBounds((int)(width*0.35), (int)(height*0.42), (int)(width*0.4), (int)(height*0.12));
        pressAnyKey.setBounds((int)(width*0.35), (int)(height*0.7), (int)(width*0.4), (int)(height*0.12));

        new Thread() {
            @Override
            public void run() {
                try {
                    ScreenUtils.type("Game Over", 100, null, gameOver);
                    sleep(2000);
                    int delay = 50;
                    ScreenUtils.type(string, delay, reason, null);
                    sleep(string.length()*delay + 800);
                    pressAnyKey.setVisible(true);
                    frame.addKeyListener(new KeyAdapter() {
                        public void keyPressed(KeyEvent event) {
                            System.exit(0);
                        }
                    });
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }.start();
    }

    private void launchScreen() {
        createNewFrame();

        JLabel titleScreen = new JLabel();
        JLabel crownImage = new JLabel();
        JLabel githubLogo = new JLabel();
        JButton playButton = new JButton("Play");
        JButton instructionsButton = new JButton("Instructions");

        BufferedImage image = null;
        try {
            image = ImageIO.read(this.getClass().getResourceAsStream("images/crown.png"));
            crownImage.setIcon(new ImageIcon(image));
            image = ImageIO.read(this.getClass().getResourceAsStream("images/github.png"));
            githubLogo.setIcon(new ImageIcon(image));
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        frame.add(titleScreen);
        frame.add(playButton);
        frame.add(instructionsButton);
        frame.add(crownImage);
        frame.add(githubLogo);

        styleButton(instructionsButton, 25);
        styleButton(playButton, 25);

        instructionsButton.setVisible(false);
        playButton.setVisible(false);
        crownImage.setVisible(false);
        githubLogo.setVisible(false);

        crownImage.setBounds((int)(width*0.43), 0, 512, 512/2);
        githubLogo.setBounds((int)(width*0.44)-74, (int)(height*0.35)+16, 64, 64);
        titleScreen.setBounds((int)(width*0.44), (int)(height*0.35), 500, 100);
        instructionsButton.setBounds((int)(width*0.3), (int)(height*0.5), (int)(width*0.4), (int)(height*0.12));
        playButton.setBounds((int)(width*0.3), (int)(height*0.65), (int)(width*0.4), (int)(height*0.12));

        styleLabel(titleScreen, Color.WHITE, 30);

        instructionsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                instructionsScreen();
            }
        });
        playButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                requestName();
            }
        });

        new Thread() {
            @Override
            public void run() {
                try {
                    sleep(1000);

                    ScreenUtils.type("The Throne", 100, null, titleScreen);
                    sleep(2000);
                    ScreenUtils.delete(100, null, titleScreen);
                    sleep(1200);

                    sleep(1000);
                    ScreenUtils.type("A game by Aditya Bandekar", 80, null, titleScreen);
                    sleep(3000);
                    ScreenUtils.delete(100, null, titleScreen);
                    sleep(2200);


                    sleep(1000);
                    githubLogo.setVisible(true);
                    sleep(400);
                    ScreenUtils.type("lookcook.github.io", 100, null, titleScreen);
                    sleep(2800);
                    ScreenUtils.delete(100, null, titleScreen);
                    sleep(2000);
                    githubLogo.setVisible(false);

                    sleep(800);
                    titleScreen.setText("The Throne");
                    crownImage.setVisible(true);
                    instructionsButton.setVisible(true);
                    playButton.setVisible(true);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }.start();
    }

    private void styleBar(JProgressBar bar, int size) {
        Font font = new Font(bar.getFont().getName(), Font.PLAIN, size);
        bar.setFont(font);
        bar.setForeground(Color.GREEN);
        bar.setBackground(Color.RED);
        bar.setStringPainted(true);
        bar.setUI(new BasicProgressBarUI() {
            protected Color getSelectionBackground() { return Color.WHITE; }
            protected Color getSelectionForeground() { return Color.WHITE; }
        });
        bar.setBorder(null);
    }

    public void startGame(String selectedName) {
        createNewFrame();

        dayHeader = new JLabel();
        JLabel resourceHeader = new JLabel("Resources");
        JLabel economyLabel = new JLabel("ECONOMY");
        JLabel militaryLabel = new JLabel("MILITARY");
        JLabel religionLabel = new JLabel("RELIGION");
        JLabel happinessLabel = new JLabel("HAPPINESS");
        situationName = new JLabel();
        situationMessenger = new JLabel();
        button1 = new JButton();
        button2 = new JButton();
        button1.setVisible(false);
        button2.setVisible(false);

        economyBar = new JProgressBar();
        militaryBar = new JProgressBar();
        happinessBar = new JProgressBar();
        religionBar = new JProgressBar();
        economyDifference = new JLabel();
        militaryDifference = new JLabel();
        happinessDifference = new JLabel();
        religionDifference = new JLabel();

        situationText = createTextArea();

        button1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gameLoop.play(1);
            }
        });
        button2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gameLoop.play(2);
            }
        });

        frame.getContentPane().add(button1);
        frame.getContentPane().add(button2);
        frame.getContentPane().add(dayHeader);
        frame.getContentPane().add(resourceHeader);
        frame.getContentPane().add(economyLabel);
        frame.getContentPane().add(militaryLabel);
        frame.getContentPane().add(religionLabel);
        frame.getContentPane().add(happinessLabel);
        frame.getContentPane().add(situationName);
        frame.getContentPane().add(situationMessenger);
        frame.getContentPane().add(situationText);
        frame.getContentPane().add(economyBar);
        frame.getContentPane().add(militaryBar);
        frame.getContentPane().add(happinessBar);
        frame.getContentPane().add(religionBar);
        frame.getContentPane().add(economyDifference);
        frame.getContentPane().add(happinessDifference);
        frame.getContentPane().add(militaryDifference);
        frame.getContentPane().add(religionDifference);

        int x = (int)(width*0.3);
        int y = (int)(height*0.65);
        int buttonWidth = (int) (width*0.4);
        button1.setBounds(x,y,buttonWidth,70);
        button2.setBounds(x,y+100, buttonWidth,70);
        styleButton(button1, 18);
        styleButton(button2, 18);

        dayHeader.setBounds((int)(width*0.45), 0, 100, 80);
        styleLabel(dayHeader, Color.WHITE, 25);
        resourceHeader.setBounds((int)(width*0.45), (int)(height*0.05), 400, 80);
        styleLabel(resourceHeader, Color.WHITE, 25);

        economyBar.setBounds((int)(width*0.52), (int)(height*0.14), 150, 18);
        styleBar(economyBar, 16);
        militaryBar.setBounds((int)(width*0.52), (int)(height*0.17), 150, 18);
        styleBar(militaryBar, 16);
        religionBar.setBounds((int)(width*0.52), (int)(height*0.2), 150, 18);
        styleBar(religionBar, 16);
        happinessBar.setBounds((int)(width*0.52), (int)(height*0.23), 150, 18);
        styleBar(happinessBar, 16);

        economyDifference.setBounds((int)(width*0.52 + 170), (int)(height*0.14), 400, 18);
        styleLabel(economyDifference, Color.WHITE, 18);
        militaryDifference.setBounds((int)(width*0.52 + 170), (int)(height*0.17), 400, 18);
        styleLabel(militaryDifference, Color.WHITE, 18);
        religionDifference.setBounds((int)(width*0.52 + 170), (int)(height*0.2), 400, 18);
        styleLabel(religionDifference, Color.WHITE, 18);
        happinessDifference.setBounds((int)(width*0.52 + 170), (int)(height*0.23), 400, 18);
        styleLabel(happinessDifference, Color.WHITE, 18);

        economyLabel.setBounds((int)(width*0.45), (int)(height*0.14), 400, 18);
        styleLabel(economyLabel, Color.WHITE, 16);
        militaryLabel.setBounds((int)(width*0.45), (int)(height*0.17), 400, 18);
        styleLabel(militaryLabel, Color.WHITE, 16);
        religionLabel.setBounds((int)(width*0.45), (int)(height*0.2), 400, 18);
        styleLabel(religionLabel, Color.WHITE, 16);
        happinessLabel.setBounds((int)(width*0.45), (int)(height*0.23), 400, 18);
        styleLabel(happinessLabel, Color.WHITE, 16);

        situationName.setBounds((int)(width*0.3), (int)(height*0.3), (int)(width*0.4), 80);
        styleLabel(situationName, Color.WHITE, 18);
        situationMessenger.setBounds((int)(width*0.3), (int)(height*0.33), (int)(width*0.4), 80);
        styleLabel(situationMessenger, Color.WHITE, 16);

        situationText.setBounds((int)(width*0.3), (int)(height*0.4), (int)(width*0.4), 300);
        styleTextArea(situationText, Color.WHITE, 16);


        gameLoop = new GameLoop(this, selectedName);
    }

    private void styleLabel(JLabel label, Color color, int size) {
        Font font = new Font(label.getFont().getName(), Font.PLAIN, size);
        label.setForeground(color);
        label.setFont(font);
    }

    private void styleTextArea(JTextArea textArea, Color color, int size) {
        Font font = new Font(textArea.getFont().getName(), Font.PLAIN, size);
        textArea.setForeground(color);
        textArea.setFont(font);
    }

    private void styleButton(JButton button, int size) {
        Font font = new Font(button.getFont().getName(), Font.PLAIN, size);
        Color redOrange = new Color(255, 83, 73);
        Color darkBlue = new Color(0, 51, 103);
        button.setBackground(redOrange);
        button.setBorder(null);
        button.setFocusPainted(false);
        button.setForeground(Color.WHITE);
        button.setFont(font);

        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                button.setBackground(darkBlue);
            }

            public void mouseExited(MouseEvent evt) {
                button.setBackground(redOrange);
            }
        });
    }

    private void updateResources(int current, int old, JProgressBar bar, JLabel label) {
        if (current > old) {
            label.setForeground(Color.GREEN);
            label.setText("+" + (current - old));
            label.setVisible(true);
        } else if (current < old) {
            label.setForeground(Color.RED);
            label.setText((current - old) + "");
            label.setVisible(true);
        } else {
            label.setVisible(false);
        }
        bar.setValue(current);
    }

    public void presentSituation(Situation situation, int[] resources,
                                 int[] oldResources, HashMap<String, String> placeholders, int day) {
        String[] arrows = {" ", " ", " ", " "};
        for (int i = 0; i < 4; i++) {
            if (oldResources[i] > resources[i]) {
                arrows[i] = "-" + Math.abs(oldResources[i]-resources[i]);
            } else if (oldResources[i] < resources[i]) {
                arrows[i] = "+" + Math.abs(oldResources[i]-resources[i]);
            }
        }

        dayHeader.setText("Day: " + day);

        updateResources(resources[Resource.ECONOMY.id()], oldResources[Resource.ECONOMY.id()],
                economyBar, economyDifference);
        updateResources(resources[Resource.MILITARY.id()], oldResources[Resource.MILITARY.id()],
                militaryBar, militaryDifference);
        updateResources(resources[Resource.HAPPINESS.id()], oldResources[Resource.HAPPINESS.id()],
                happinessBar, happinessDifference);
        updateResources(resources[Resource.RELIGION.id()], oldResources[Resource.RELIGION.id()],
                religionBar, religionDifference);


        situationName.setText(ScreenUtils.fillPlaceholders(situation.name, placeholders));
        situationMessenger.setText(ScreenUtils.fillPlaceholders(situation.messenger, placeholders));
        situationText.setText("");

        button1.setVisible(false);
        button2.setVisible(false);
        button1.setText(situation.option1);
        button2.setText(situation.option2);
        new Thread() {
            @Override
            public void run() {
                try {
                    int delay = 30;
                    String string = ScreenUtils.fillPlaceholders(situation.text, placeholders);
                    ScreenUtils.type(string, delay, situationText, null);
                    sleep(string.length()*30 + 200);
                    button1.setVisible(true);
                    button2.setVisible(true);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }.start();
    }

    private ArrayList<BufferedImage> getIcons() {
        ArrayList<BufferedImage> images = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            InputStream stream = this.getClass().getResourceAsStream("icons/icon" + i + ".png");
            BufferedImage image = null;
            try {
                image = ImageIO.read(stream);
                images.add(image);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return images;
    }
}
