
/**
 * The App class contains the main method. 
 * Main method is responsible for instantiating the MainFrame object.
 * 
 * @author Divya Kamath
 */

import javax.swing.SwingUtilities;

public class App {
    public static void main(String[] args) throws Exception {

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                MainFrame app = new MainFrame();
                app.setVisible(true);
            }
        });
    }
}
