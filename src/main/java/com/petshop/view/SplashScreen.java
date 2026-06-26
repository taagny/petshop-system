package com.petshop.view;

import javax.swing.*;
import java.awt.*;

public class SplashScreen extends JWindow {

    public SplashScreen() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(new Color(41, 128, 185));
        p.setBorder(BorderFactory.createLineBorder(new Color(28, 90, 130), 3));

        // Ícone e título
        JLabel icon  = new JLabel("🐾", SwingConstants.CENTER);
        icon.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 60));
        icon.setForeground(Color.WHITE);

        JLabel titulo = new JLabel("PetShop System", SwingConstants.CENTER);
        titulo.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 26));
        titulo.setForeground(Color.WHITE);

        JLabel sub = new JLabel("Carregando...", SwingConstants.CENTER);
        sub.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 13));
        sub.setForeground(new Color(200, 230, 255));

        JProgressBar bar = new JProgressBar();
        bar.setIndeterminate(true);
        bar.setForeground(new Color(46, 204, 113));
        bar.setBackground(new Color(28, 90, 130));
        bar.setBorderPainted(false);
        bar.setPreferredSize(new Dimension(0, 6));

        JLabel rodape = new JLabel("UNIPAC Barbacena - POO 2026", SwingConstants.CENTER);
        rodape.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 11));
        rodape.setForeground(new Color(180, 210, 240));
        rodape.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));

        JPanel centro = new JPanel(new GridLayout(3, 1, 0, 6));
        centro.setOpaque(false);
        centro.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));
        centro.add(titulo);
        centro.add(sub);
        centro.add(bar);

        p.add(icon,   BorderLayout.NORTH);
        p.add(centro, BorderLayout.CENTER);
        p.add(rodape, BorderLayout.SOUTH);

        setContentPane(p);
        setSize(380, 220);
        setLocationRelativeTo(null);
    }

    /** Exibe o splash por {@code ms} milissegundos, depois fecha. */
    public void exibir(int ms) {
        setVisible(true);
        try { Thread.sleep(ms); } catch (InterruptedException ignored) {}
        setVisible(false);
        dispose();
    }
}