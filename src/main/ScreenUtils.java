import javax.swing.*;
import java.util.HashMap;

public class ScreenUtils {

    public static void type(String string, int delay, JTextArea textArea, JLabel label) {
        new Thread() {
            @Override
            public void run() {
                int i = 0;
                while (string.length() > i) {
                    try {
                        sleep(delay);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                    if (textArea != null) {
                        textArea.setText(textArea.getText() + string.charAt(i++));
                    }
                    if (label != null) {
                        label.setText(label.getText() + string.charAt(i++));
                    }
                }
            }
        }.start();
    }

    public static void delete(int delay, JTextArea textArea, JLabel label) {
        new Thread() {
            @Override
            public void run() {
                String string = "";
                if (textArea != null) {
                    string = textArea.getText();
                }
                if (label != null) {
                    string = label.getText();
                }
                while (string.length() > 0) {
                    try {
                        sleep(delay);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                    if (textArea != null) {
                        textArea.setText(textArea.getText().substring(0, textArea.getText().length()-1));
                        string = textArea.getText();
                    }
                    if (label != null) {
                        label.setText(label.getText().substring(0, label.getText().length()-1));
                        string = label.getText();
                    }
                }
            }
        }.start();
    }

    public static String fillPlaceholders(String string, HashMap<String, String> placeholders) {
        String original = string;
        for (String placeholder : placeholders.keySet()) {
            if (original.contains(placeholder)) {
                original = original.replace(placeholder, placeholders.get(placeholder));
            }
        }
        return original;
    }
}
