import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;

public class View extends JFrame {

    CGLS cgls;

    int width = 0;
    int height = 0;

    JPanel topPanel = new JPanel();
    JPanel mainPanel = new JPanel();
    JPanel textPanel = new JPanel();

    JSlider tolSlider;
    JSlider kSlider;
    JScrollPane outputWindow;
    Controller c;
    Output o;

    int imageCounter = 0;
    int currentImage = 0;


    public View(CGLS cgls, String title, Output o, int width, int height) {
        this.cgls = cgls;
        this.o = o;
        this.width = width;
        this.height = height;

        this.setLayout(new BorderLayout());

    }

    public void setController(Controller c) {
        this.c = c;
    }

    public void createGUI() {

        this.add(this.topPanel, BorderLayout.NORTH);
        this.topPanel.setLayout(new GridLayout(1, 6));

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new GridLayout(1, 2));
        this.add(contentPanel, BorderLayout.CENTER);


        JPanel sliderPanel = new JPanel();
        this.add(sliderPanel, BorderLayout.SOUTH);
        sliderPanel.setLayout(new GridLayout(1, 1));

        kSlider = new JSlider();
        refreshSlider(1, 1, this.imageCounter, kSlider);

        ChangeListener kChangeListener = new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                JSlider slider = (JSlider) e.getSource();
                if (slider.getValue() != currentImage) {
                    refreshImage(cgls.outputPath + slider.getValue() + "." + cgls.file_Extension);
                    o.print("Show Image " + slider.getValue());
                }

            }
        };
        kSlider.addChangeListener(kChangeListener);
        sliderPanel.add(kSlider);

        contentPanel.add(this.mainPanel, BorderLayout.WEST);
        this.mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));

        textPanel.setBackground(Color.BLACK);
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.PAGE_AXIS));
        outputWindow = new JScrollPane(textPanel);
        contentPanel.add(outputWindow, BorderLayout.EAST);

        JButton createI = new JButton(Controller.INPUT);
        createI.setActionCommand(Controller.INPUT);
        createI.addActionListener(c);
        topPanel.add(createI);

        JButton createC = new JButton(Controller.CGLS);
        createC.setActionCommand(Controller.CGLS);
        createC.addActionListener(c);
        topPanel.add(createC);

        JButton createS = new JButton(Controller.STOP);
        createS.setActionCommand(Controller.STOP);
        createS.addActionListener(c);
        topPanel.add(createS);

        JButton createInv = new JButton(Controller.INVERTED);
        createInv.setActionCommand(Controller.INVERTED);
        createInv.addActionListener(c);
        topPanel.add(createInv);

        JLabel toleranceLabel = new JLabel("Toleranz (10^):", SwingConstants.RIGHT);
        toleranceLabel.setLabelFor(tolSlider);
        topPanel.add(toleranceLabel);

        tolSlider = new JSlider();
        refreshSlider(new Double(Math.log10(cgls.tolerance)).intValue(),  -10,2, tolSlider);
        ChangeListener tolChangeListener = new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                JSlider slider = (JSlider) e.getSource();
                double value = Math.pow(10, slider.getValue());
                if(value != cgls.tolerance){
                    cgls.setTolerance(value);
                    o.print("tolerance set on " +  value);
                }
            }
        };
        tolSlider.addChangeListener(tolChangeListener);
        topPanel.add(tolSlider);
    }

    public void refreshSlider(int value, int minimum, int maximum, JSlider slider) {
        slider.setMinimum(minimum);
        slider.setMaximum(maximum);
        slider.setMajorTickSpacing(5);
        slider.setMinorTickSpacing(1);
        slider.createStandardLabels(Math.max(1, maximum/5));
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        slider.setValue(value);

        this.setVisible(true);
    }

    public void displayImage(String path) {

        imageCounter++;
        currentImage = imageCounter;
        refreshImage(path);

        refreshSlider(imageCounter, 1,imageCounter, kSlider);


        this.mainPanel.repaint();
        this.mainPanel.revalidate();
        this.getContentPane().repaint();
        this.setVisible(true);
    }

    public void refreshImage(String path) {
        this.mainPanel.removeAll();

        JPanel jp = new JPanel();

        ImageIcon imageIcon = new ImageIcon(path);
        Image image = imageIcon.getImage(); // transform it
        Image newimg = image.getScaledInstance(imageIcon.getIconWidth() * 5, imageIcon.getIconHeight() * 5, java.awt.Image.SCALE_SMOOTH); // scale it the smooth way
        imageIcon = new ImageIcon(newimg);  // transform it back

        JLabel pictureLabel = new JLabel(imageIcon);
        jp.add(pictureLabel);

        this.mainPanel.add(jp);
        this.setVisible(true);

    }

    public void write(String s) {
        JLabel text = new JLabel();
        text.setText(s);
        text.setForeground(Color.WHITE);
        this.textPanel.add(text);


        this.textPanel.revalidate();
        int height = (int)this.textPanel.getPreferredSize().getHeight();
        Rectangle rect = new Rectangle(0,height,10,10);
        this.textPanel.scrollRectToVisible(rect);



//        int offset = outputWindow.getVerticalScrollBar().getMaximum();
//        int offset = 0;
//        outputWindow.getVerticalScrollBar().setValue(offset);

        this.setVisible(true);
    }

    public void finish() {

        this.setSize(this.width, this.height);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setVisible(true);
    }

}
