
/**
 * 
 * The MainFrame sets up the GUI of application.
 * 
 * It is responsible for the following two things 
 * (a) MainFrame sets up the GUI with a Frame containing two panels embedded in it.
 * (b) MainFrame Instantiates ReadImage class and then reads the ColorCode.txt and Intensity.txt 
 * to sort the images based on ColorCode and Intensity respectively using the Manhattan distance metric.
 * 
 * @author Divya Kamath
 */

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Image;
import java.awt.Toolkit;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JFrame;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Map.Entry;

public class MainFrame extends JFrame implements ActionListener {

    // The main frame contains two panels, rightPanel to display the selected image
    // and
    // leftPanel to display the images in order

    private JPanel rightPanel;
    private Buttons colorButton;
    private Buttons intensityButton;
    private Buttons colorIntensityButton;
    private Buttons reset;
    private Buttons relevance;
    private JLabel selectedImage;
    private JScrollPane scroll = null;
    private ImageIcon defaultImage;
    private JPanel imageOrder = null;
    private JCheckBox checkBox;
    private Toolkit tk;

    private Set<Integer> relevanceSet;
    // buttonHashMap stores the image number as key and button with images as value
    private HashMap<Integer, JPanel> buttonHashMap;

    // buttonOrder stores the order in which the images are to be displayed
    private int[] buttonOrder;
    private int[] imageSize;
    private int picNo = 0;

    // intensityMatrix and colorCodeMatrix stores the intensity and colorCode
    // histogram values of all the images
    private int[][] intensityMatrix;
    private int[][] colorCodeMatrix;
    private double[][] normalizedFeatureMartix;
    private double[] weight;
    private CalculateUtil calculate;

    /**
     * constructor
     * 
     * pre: none
     * 
     * post: Following 3 things are set up
     * (a) Sets up the default GUI
     * (b) Instantiates ReadImage object to create ColorCode.txt and Intensity.txt
     * (c) reads the Intensity.txt and ColorCode.txt files
     * 
     */

    MainFrame() {
        tk = this.getToolkit();
        relevanceSet = new HashSet<Integer>();
        // HEADING START
        JLabel heading = new JLabel();
        heading.setText("CONTENT BASED IMAGE RETRIEVAL SYSTEM");
        heading.setBounds(225, 0, 575, 100);
        heading.setForeground(new Color(0xe1f5fe));
        heading.setFont(new Font("Lora", Font.PLAIN, 25));
        // HEADING END

        // LEFT PANEL START
        JPanel leftPanel = new JPanel();
        leftPanel.setBounds(35, 80, 450, 550);
        leftPanel.setBackground(new Color(0x39796b));
        leftPanel.setLayout(null);

        reset = new Buttons("RESET", 15, 500, 205);
        relevance = new Buttons("RELEVANCE", 230, 500, 205);
        colorButton = new Buttons("COLOR CODE SORT", 15, 350, 420);
        intensityButton = new Buttons("INTENSITY SORT", 15, 400, 420);
        colorIntensityButton = new Buttons("COLOR CODE & INTENSITY SORT", 15, 450, 420);

        reset.addActionListener(new ResetButtonHandler());
        relevance.addActionListener(new RelevanceButtonHandler());
        colorButton.addActionListener(new ColorButtonHandler());
        intensityButton.addActionListener(new IntensityButtonHandler());
        colorIntensityButton.addActionListener(new ColorIntensityButtonHandler());

        // Attribution for the image layout.png
        // Layout.png icon is made by Lafs from www.flaticon.com

        java.net.URL defaultImageUrl = this.getClass().getResource("layout.png");
        defaultImage = new ImageIcon(tk.getImage(defaultImageUrl));
        Image image = defaultImage.getImage();
        Image newing = image.getScaledInstance(300, 300, java.awt.Image.SCALE_SMOOTH);
        defaultImage = new ImageIcon(newing);

        selectedImage = new JLabel();
        selectedImage.setBounds(75, 25, 300, 300);

        leftPanel.add(selectedImage);
        leftPanel.add(reset);
        leftPanel.add(relevance);
        leftPanel.add(colorButton);
        leftPanel.add(intensityButton);
        leftPanel.add(colorIntensityButton);
        // LEFT PANEL ENDS

        // RIGHT PANEL STARTS
        rightPanel = new JPanel();
        rightPanel.setBackground(new Color(0x39796b));
        rightPanel.setBounds(500, 80, 500, 550);

        imageSize = new int[101];
        buttonOrder = new int[101];
        buttonOrder[0] = 0;
        buttonHashMap = new HashMap<Integer, JPanel>();

        for (int i = 1; i <= 100; i++) {
            java.net.URL ImageUrl = this.getClass().getResource("images/" + i + ".jpg");
            ImageIcon icon = new ImageIcon(tk.getImage(ImageUrl));
            if (icon != null) {
                imageSize[i] = icon.getIconWidth() * icon.getIconHeight();
                Image imageIcon = icon.getImage();
                Image newingIcon = imageIcon.getScaledInstance(100, 100, java.awt.Image.SCALE_SMOOTH);
                icon = new ImageIcon(newingIcon);

                JButton imgButton = new JButton(icon);
                imgButton.setToolTipText(i + ".jpg");
                imgButton.setPreferredSize(new Dimension(100, 75));
                imgButton.addActionListener(new IconButtonHandler(i, icon));
                imgButton.setDisabledIcon(icon);

                checkBox = new JCheckBox("Relevant");
                checkBox.setSelected(false);
                checkBox.setFocusable(false);
                checkBox.setName(i + "");
                checkBox.addActionListener(new CheckBoxHandler(i, checkBox));

                JPanel imgSet = new JPanel();
                imgSet.add(imgButton);
                imgSet.add(checkBox);
                imgSet.setPreferredSize(new Dimension(100, 110));

                buttonHashMap.put(i, imgSet);
                buttonOrder[i] = i;
            }
        }
        imageOder();

        // RIGHT PANEL ENDS

        // MAIN FRAME START
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(1024, 1024);
        this.setResizable(false);
        this.getContentPane().setBackground(new Color(0x263238));
        this.setLocationRelativeTo(null);

        // this.setTitle("Reading images Please wait");
        // this.setVisible(true);
        // new ReadImage();

        this.setTitle("Content-Based Image Retrieval System");

        this.add(heading);
        this.add(leftPanel);
        this.add(rightPanel);

        this.setLayout(null);
        this.setLocationRelativeTo(null);
        this.setVisible(true);

        defaultSetting();
        // MAIN FRAME END

        intensityMatrix = new int[101][26];
        colorCodeMatrix = new int[101][65];
        normalizedFeatureMartix = new double[101][90];
        weight = new double[normalizedFeatureMartix[0].length];
        readIntensityFile();
        readColorCodeFile();

        calculate = new CalculateUtil();
        calculate.calculateGaussianNormalization(normalizedFeatureMartix, intensityMatrix,
                colorCodeMatrix, imageSize);

    }

    /**
     * ImageOrder method is responsible to diplay the images in the rightPanel.
     * It displays the images as per the index order in buttonOrder array
     * 
     * pre: rightPanel, buttonHashMap and buttonOrder are instantiated
     * 
     * post: Adds scroll to rightPanel. Scroll is populated with imageOrder that
     * holds all the image in desired order.
     */

    private void imageOder() {

        if (imageOrder == null) {
            imageOrder = new JPanel(new GridLayout(25, 4, 0, 0));
            imageOrder.setBounds(525, 100, 500, 550);
        } else {
            imageOrder.removeAll();
            imageOrder.revalidate();
            imageOrder.repaint();
        }
        HideRelevanceIcon();
        for (int i = 1; i < 101; i++) {
            imageOrder.add(buttonHashMap.get(buttonOrder[i]));
        }

        if (scroll == null) {
            scroll = new JScrollPane(imageOrder, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                    JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            scroll.setEnabled(true);
            scroll.setPreferredSize(new Dimension(500, 550));
            scroll.getVerticalScrollBar().setUnitIncrement(550); // skips to next 20 images
            rightPanel.add(scroll);
        }
        scroll.getVerticalScrollBar().setValue(0);
    }

    /**
     * defaultSetting method sets the GUI to its default setting
     * 
     * pre: none
     * 
     * post: It makes the following 3 changes
     * (a) selectedImage icon is set to default image
     * (b) selectedImage mouse hover message is set to "Select an image from right"
     * (c) disables the intensityButton, colorButton and reset button
     * 
     */
    private void defaultSetting() {
        selectedImage.setIcon(defaultImage);
        selectedImage.setToolTipText("Select an image from right");

        reset.setEnabled(false);
        relevance.setEnabled(false);
        colorButton.setEnabled(false);
        intensityButton.setEnabled(false);
        colorIntensityButton.setEnabled(false);

    }

    /**
     * IconButtonHandler implements an ActionListener for each iconButton.
     * 
     * pre: none
     * 
     * post: When an iconButton is clicked, it sets two values
     * (a) it sets the selectedImage to the image on the button
     * (b) it sets picNo to the image number selected
     * 
     */

    private class IconButtonHandler implements ActionListener {
        int pNo;
        // ImageIcon imgButton;

        IconButtonHandler(int i, ImageIcon img) {
            pNo = i;
            // imgButton = img;
        }

        public void actionPerformed(ActionEvent e) {
            java.net.URL selectedImgUrl = this.getClass().getResource("images/" + pNo + ".jpg");
            selectedImage.setIcon(new ImageIcon(
                    tk.getImage(selectedImgUrl).getScaledInstance(300, 300, java.awt.Image.SCALE_SMOOTH)));

            selectedImage.setToolTipText(pNo + ".jpg");

            picNo = pNo;

            colorIntensityButton.setEnabled(true);
            intensityButton.setEnabled(true);
            colorButton.setEnabled(true);
            reset.setEnabled(true);

            if (relevanceSet.size() > 0) {
                relevanceSet.clear();
            }
            for (Entry<Integer, JPanel> entry : buttonHashMap.entrySet()) {
                JPanel imgPanel = entry.getValue();
                JCheckBox box = (JCheckBox) imgPanel.getComponent(1);
                box.setSelected(false);
            }
        }

    }

    /**
     * CheckBoxHandler implements an ActionListener for each checkBox.
     * 
     * pre: none
     * 
     * post: When an checkBox is selected, it is added to relevance set
     * If deselected, it is removed from the set.
     * 
     */
    private class CheckBoxHandler implements ActionListener {
        int pNo;
        JCheckBox box;

        CheckBoxHandler(int i, JCheckBox checkBox) {
            pNo = i;
            box = checkBox;
        }

        public void actionPerformed(ActionEvent e) {
            if (box.isSelected()) {
                relevanceSet.add(pNo);
            }
            if (!box.isSelected()) {
                relevanceSet.remove(pNo);
            }
        }

    }

    /**
     * ResetButtonHandler implements an ActionListener for the reset button.
     * 
     * pre: reset button is enabled
     * 
     * post: When an reset button is clicked, it makes 3 changes
     * (a) it sets the picNo to 0
     * (b) sorts the buttonOrder in ascending Order to reset the image display order
     * (c) it calls the deafultSetting and ImageOrder method
     * 
     */

    private class ResetButtonHandler implements ActionListener {
        ResetButtonHandler() {
        }

        public void actionPerformed(ActionEvent e) {
            picNo = 0;
            Arrays.sort(buttonOrder);
            defaultSetting();
            imageOder();
            for (Entry<Integer, JPanel> entry : buttonHashMap.entrySet()) {
                JPanel imgPanel = entry.getValue();
                imgPanel.getComponent(0).setEnabled(true);
            }
        }

    }

    /**
     * HideRelevanceIcon is responsible to hide the checkBox.
     * 
     * pre: checkBox is visible
     * 
     * post: hides the checkBox
     * 
     */
    private void HideRelevanceIcon() {
        for (Entry<Integer, JPanel> entry : buttonHashMap.entrySet()) {
            JPanel imgPanel = entry.getValue();
            JCheckBox box = (JCheckBox) imgPanel.getComponent(1);
            box.setVisible(false);
        }

    }

    /**
     * RelevanceButtonHandler implements an ActionListener for the Relevancer
     * button.
     * 
     * pre: Relevance button is enabled
     * 
     * post: When an Relevance button is clicked, it shows the checkbox and hides
     * the unnecessary buttons.
     * 
     */
    private class RelevanceButtonHandler implements ActionListener {
        RelevanceButtonHandler() {

        }

        public void actionPerformed(ActionEvent e) {
            relevance.setEnabled(false);
            colorButton.setEnabled(false);
            intensityButton.setEnabled(false);
            for (Entry<Integer, JPanel> entry : buttonHashMap.entrySet()) {
                JPanel imgPanel = entry.getValue();
                JCheckBox box = (JCheckBox) imgPanel.getComponent(1);
                box.setVisible(true);
                imgPanel.getComponent(0).setEnabled(false);
            }

        }

    }

    /**
     * ColorButtonHandler implements an ActionListener for colorButton
     * 
     * pre: colorButton is enabled
     * 
     * post: ColorButtonHandler calls the calculateManhattanDistance method and pass
     * the
     * colorCodeMatrix 2D array. It arranges the images based on the color code
     * 
     */

    private class ColorButtonHandler implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            if (picNo == 0) {
                return;
            }

            calculate.calculateManhattanDistance(colorCodeMatrix, imageSize, picNo, buttonOrder);
            imageOder();
        }

    }

    /**
     * IntensityButtonHandler implements an ActionListener for intensityButton
     * 
     * pre: intensityButton is enabled
     * 
     * post: IntensityButtonHandler calls the calculateManhattanDistance method and
     * pass the
     * intensityMatrix 2D array. It arranges the images based on the intensity
     * 
     */
    private class IntensityButtonHandler implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            if (picNo == 0) {
                return;
            }

            calculate.calculateManhattanDistance(intensityMatrix, imageSize, picNo, buttonOrder);
            imageOder();
        }

    }

    /**
     * ColorIntensityButtonHandler implements an ActionListener for the
     * ColorIntensity button.
     * 
     * pre: ColorIntensity button is enabled
     * 
     * post: ColorButtonHandler calls the calculateWeight method and
     * calculateDistanceMetrix. It arranges the images based on the color code +
     * intensity and relevance feedback from the user
     * 
     */
    private class ColorIntensityButtonHandler implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            if (picNo == 0) {
                return;
            }
            relevance.setEnabled(true);
            colorButton.setEnabled(true);
            intensityButton.setEnabled(true);

            for (Entry<Integer, JPanel> entry : buttonHashMap.entrySet()) {
                JPanel imgPanel = entry.getValue();
                imgPanel.getComponent(0).setEnabled(true);
            }

            calculate.calculateWeight(weight, relevanceSet, normalizedFeatureMartix, picNo);
            calculate.calculateDistanceMetrix(normalizedFeatureMartix, weight, picNo, buttonOrder);
            imageOder();
        }

    }

    /**
     * readIntensityFile method calls the readFile to read the intensity.txt file
     * containing the intensity matrix with the histogram bin values of each image
     * 
     * pre: intensity.txt file is loaded
     * 
     * post: calls the readFile to read intensity.txt file and store the value in
     * intensityMatrix
     * 
     */
    private void readIntensityFile() {
        String fileName = "Intensity.txt";
        ReadTextFile rf = new ReadTextFile();
        intensityMatrix = rf.readFile(intensityMatrix, fileName);

    }

    /**
     * readColorCodeFile method calls the readFile to read the ColorCodes.txt file
     * containing the colorCode matrix with the histogram bin values of each image
     * 
     * pre: ColorCodes.txt file is loaded
     * 
     * post: calls the readFile to read ColorCodes.txt file and store the value in
     * colorCodeyMatrix
     * 
     */
    private void readColorCodeFile() {
        String fileName = "ColorCode.txt";
        ReadTextFile rf = new ReadTextFile();
        colorCodeMatrix = rf.readFile(colorCodeMatrix, fileName);

    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }

}
