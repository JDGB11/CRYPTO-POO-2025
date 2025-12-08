package main;

import javax.swing.JFrame;

public class Main {
    public static void main(String[] args) {
        JFrame window = new JFrame("CRYPTO LEGENDS");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);

        GamePanel panel = new GamePanel();
        window.add(panel);
        window.pack();

        window.setLocationRelativeTo(null);
        window.setVisible(true);

        // ¡CRÍTICO! Esto asegura que las teclas funcionen
        panel.requestFocusInWindow();

        panel.startGameThread();
    }
}