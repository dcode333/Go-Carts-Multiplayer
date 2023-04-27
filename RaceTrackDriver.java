import javax.swing.*;
import java.io.*;

public class RaceTrackDriver {
    public static void main(String[] args) throws IOException {
        JFrame frame = new JFrame("Racetrack");

        Racetrack racetrack = new Racetrack();
        frame.setResizable(false);
        frame.add(racetrack);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        JFrame frame2 = new JFrame("Racetrack2");
        Racetrack2 racetrack2 = new Racetrack2();
        frame2.setResizable(false);
        frame2.add(racetrack2);
        frame2.pack();
        frame2.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame2.setLocationRelativeTo(null);
        frame2.setVisible(true);
    }
}
