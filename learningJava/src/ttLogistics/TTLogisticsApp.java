package ttLogistics;

import javax.swing.*;

public class TTLogisticsApp {

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName()); }
        catch (Exception ignored) {}
        SwingUtilities.invokeLater(AppFrame::new);
    }
}
