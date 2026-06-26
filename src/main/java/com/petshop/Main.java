package com.petshop;

import com.formdev.flatlaf.FlatLightLaf;
import com.petshop.view.MainFrame;
import com.petshop.view.SplashScreen;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        try {
            FlatLightLaf.setup();
            UIManager.put("Button.arc", 8);
            UIManager.put("Component.arc", 6);
            UIManager.put("TextComponent.arc", 6);
            UIManager.put("ScrollBar.thumbArc", 999);
            UIManager.put("ScrollBar.width", 8);
        } catch (Exception e) {
            System.err.println("FlatLaf nao disponivel: " + e.getMessage());
        }

        SwingUtilities.invokeLater(() -> {
            SplashScreen splash = new SplashScreen();
            splash.exibir(1800);

            // Janela principal
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}