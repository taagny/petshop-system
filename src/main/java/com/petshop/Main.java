package com.petshop;

import com.formdev.flatlaf.FlatLightLaf;
import com.petshop.view.MainFrame;
import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        FlatLightLaf.setup();
        SwingUtilities.invokeLater(() -> {
            new MainFrame().setVisible(true);
        });
    }
}