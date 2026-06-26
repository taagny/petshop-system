package com.petshop.view;

import com.formdev.flatlaf.FlatLightLaf;
import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    public MainFrame() {
        setTitle("🐾 PetShop System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Painel do título
        JPanel painelTopo = new JPanel();
        painelTopo.setBackground(new Color(41, 128, 185));
        painelTopo.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        JLabel titulo = new JLabel("🐾 Sistema de Gerenciamento - PetShop");
        titulo.setForeground(Color.WHITE);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        painelTopo.add(titulo);
        add(painelTopo, BorderLayout.NORTH);

        // Painel central com botões
        JPanel painelCentro = new JPanel(new GridBagLayout());
        painelCentro.setBackground(new Color(236, 240, 241));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.ipadx = 60;
        gbc.ipady = 30;

        // Linha 1
        gbc.gridx = 0; gbc.gridy = 0;
        painelCentro.add(criarBotao("👤 Proprietários", new Color(52, 152, 219), e -> abrirProprietarios()), gbc);

        gbc.gridx = 1; gbc.gridy = 0;
        painelCentro.add(criarBotao("🐶 Animais", new Color(46, 204, 113), e -> abrirAnimais()), gbc);

        gbc.gridx = 2; gbc.gridy = 0;
        painelCentro.add(criarBotao("✂️ Serviços", new Color(155, 89, 182), e -> abrirServicos()), gbc);

        // Linha 2
        gbc.gridx = 0; gbc.gridy = 1;
        painelCentro.add(criarBotao("📋 Lançar Serviço", new Color(230, 126, 34), e -> abrirLancamento()), gbc);

        gbc.gridx = 1; gbc.gridy = 1;
        painelCentro.add(criarBotao("🔍 Histórico", new Color(26, 188, 156), e -> abrirHistorico()), gbc);

        gbc.gridx = 2; gbc.gridy = 1;
        painelCentro.add(criarBotao("📊 Relatórios", new Color(231, 76, 60), e -> abrirRelatorios()), gbc);

        add(painelCentro, BorderLayout.CENTER);

        // Rodapé
        JLabel rodape = new JLabel("UNIPAC Barbacena - POO 2026", SwingConstants.CENTER);
        rodape.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        rodape.setForeground(Color.GRAY);
        rodape.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));
        add(rodape, BorderLayout.SOUTH);
    }

    private JButton criarBotao(String texto, Color cor, java.awt.event.ActionListener acao) {
        JButton btn = new JButton(texto);
        btn.setBackground(cor);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addActionListener(acao);
        return btn;
    }

    private void abrirProprietarios() {
        new ProprietarioView(this).setVisible(true);
    }

    private void abrirAnimais() {
        new AnimalView(this).setVisible(true);
    }

    private void abrirServicos() {
        new ServicoView(this).setVisible(true);
    }

    private void abrirLancamento() {
        new LancamentoView(this).setVisible(true);
    }

    private void abrirHistorico() {
        new HistoricoView(this).setVisible(true);
    }

    private void abrirRelatorios() {
        new RelatorioView(this).setVisible(true);
    }
}