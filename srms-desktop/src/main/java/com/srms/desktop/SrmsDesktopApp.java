package com.srms.desktop;

import com.srms.desktop.ui.LoginFrame;

import javax.swing.*;

/** Entry point of the Swing + JDBC desktop client. */
public class SrmsDesktopApp {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception ignored) {
            // fall back to the default look and feel
        }
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}
