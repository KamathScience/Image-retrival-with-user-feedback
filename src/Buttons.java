
/**
 * The Buttons class extends JButton. 
 * It is responsible for instantiating button with default values.
 * 
 * @author Divya Kamath
 */

import java.awt.Font;
import javax.swing.JButton;

public class Buttons extends JButton {

    Buttons(String label, int xAxis, int yAxis, int width) {
        this.setText(label);
        this.setFocusable(false);
        this.setFont(new Font("Comic Sans", Font.BOLD, 15));
        this.setBounds(xAxis, yAxis, width, 40); // 40 == height
    }
}
