package s5021toxls;

import java.io.File;
import javax.swing.UIManager;

/**
 *
 * @author Kata
 */
public class S5021toXLS {

    public static void main(String[] args) throws Exception {
        if (args.length == 2) {
            Converter c = new Converter(new File(args[0]));
            c.saveToXls(new File(args[1]));
        } else {
            try {
                UIManager.setLookAndFeel(
                        UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
            }

            Gui gui = new Gui();
            gui.setVisible(true);
        }
    }
}
