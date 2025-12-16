import animation.AnimationManager;
import animation.Event; // Import Event class

import javax.swing.*;
import java.awt.event.ActionEvent;

public class ExampleGUI extends JFrame {

    private final AnimationManager animationManager; // Make animationManager a field
    private final JButton button; // Make button a field

    public ExampleGUI() {
        setTitle("Animation Example");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(null); // Using null layout for manual positioning
        add(panel);

        button = new JButton("Click me!");
        button.setBounds(50, 50, 100, 30);
        panel.add(button);

        animationManager = new AnimationManager(20); // Initialize AnimationManager with a custom update interval

        // Animate the button after a delay
        Timer startTimer = new Timer(1000, (ActionEvent e) -> {
            animationManager.animateMove(button, 250, 100, 2, AnimationManager.Easing.EASE_IN_OUT_CUBIC);
            ((Timer)e.getSource()).stop(); // Stop this timer after starting animation
        });
        startTimer.setRepeats(false);
        startTimer.start();

        // Add events to the EventManager
        animationManager.getEventManager().addEvent(new Event(1.5f, () -> {
            button.setText("Event 1 triggered!");
            System.out.println("Event 1 triggered at 1.5 seconds!");
        }));

        animationManager.getEventManager().addEvent(new Event(3.5f, () -> {
            button.setText("Event 2 triggered!");
            System.out.println("Event 2 triggered at 3.5 seconds!");
        }));

        animationManager.getEventManager().addEvent(new Event(5.0f, () -> {
            button.setText("Animation and Events done!");
            System.out.println("All animations and events finished at 5.0 seconds!");
            animationManager.stop(); // Stop the animation manager
        }));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ExampleGUI ex = new ExampleGUI();
            ex.setVisible(true);
        });
    }
}
