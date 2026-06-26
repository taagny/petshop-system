package com.petshop.view;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.draw.LineSeparator;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.petshop.controller.AnimalController;
import com.petshop.controller.LancamentoController;
import com.petshop.controller.ProprietarioController;
import com.petshop.controller.ServicoController;
import com.petshop.model.Animal;
import com.petshop.model.LancamentoServico;
import com.petshop.model.Proprietario;
import com.petshop.model.Servico;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class RelatorioView extends JDialog {

    private final ProprietarioController proprietarioController = new ProprietarioController();
    private final AnimalController        animalController       = new AnimalController();
    private final LancamentoController    lancamentoController   = new LancamentoController();
    private final ServicoController       servicoController      = new ServicoController();

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // Fontes iText — usando nome completo para evitar ambiguidade com java.awt.Font
    private static final com.itextpdf.text.Font F_TITULO =
            new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 16,
                    com.itextpdf.text.Font.BOLD);
    private static final com.itextpdf.text.Font F_SECAO =
            new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 12,
                    com.itextpdf.text.Font.BOLD);
    private static final com.itextpdf.text.Font F_NORMAL =
            new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 10,
                    com.itextpdf.text.Font.NORMAL);
    private static final com.itextpdf.text.Font F_NEGRITO =
            new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 10,
                    com.itextpdf.text.Font.BOLD);
    private static final com.itextpdf.text.Font F_CABECALHO =
            new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 10,
                    com.itextpdf.text.Font.BOLD, BaseColor.WHITE);

    // Campos do formulário
    private JComboBox<Proprietario> cbProprietario;
    private JTextField              tfInicio;
    private JTextField              tfFim;
    private JTextArea               taPreview;

    public RelatorioView(JFrame parent) {
        super(parent, "Relatorios", true);
        setSize(900, 650);
        setLocationRelativeTo(parent);
        setLayout(new java.awt.BorderLayout(10, 10));
        getRootPane().setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(criarPainelEsquerdo(), java.awt.BorderLayout.WEST);
        add(criarPainelPreview(),  java.awt.BorderLayout.CENTER);

        carregarProprietarios();
    }

    // ═══════════════════════════════════════════
    // PAINEL ESQUERDO — FILTROS
    // ═══════════════════════════════════════════
    private JPanel criarPainelEsquerdo() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setPreferredSize(new Dimension(290, 0));
        p.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Parametros do Relatorio",
                TitledBorder.LEFT, TitledBorder.TOP,
                new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 13)));

        GridBagConstraints g = new GridBagConstraints();
        g.insets  = new Insets(6, 8, 4, 8);
        g.fill    = GridBagConstraints.HORIZONTAL;
        g.weightx = 1.0;
        g.gridx   = 0;

        // Proprietário
        g.gridy = 0; p.add(new JLabel("Cliente *"), g);
        g.gridy = 1;
        cbProprietario = new JComboBox<>();
        cbProprietario.setRenderer(new DefaultListCellRenderer() {
            @Override public java.awt.Component getListCellRendererComponent(JList<?> l, Object v,
                                                                             int i, boolean sel, boolean foc) {
                super.getListCellRendererComponent(l, v, i, sel, foc);
                if (v instanceof Proprietario pr) setText(pr.getNome());
                return this;
            }
        });
        cbProprietario.addActionListener(e -> atualizarPreview());
        p.add(cbProprietario, g);

        // Período
        g.gridy = 2; p.add(new JLabel("Data inicio (dd/MM/aaaa)"), g);
        g.gridy = 3; tfInicio = new JTextField(); p.add(tfInicio, g);
        g.gridy = 4; p.add(new JLabel("Data fim  (dd/MM/aaaa)"), g);
        g.gridy = 5; tfFim = new JTextField(); p.add(tfFim, g);

        // Atalhos rápidos
        g.gridy = 6;
        JPanel atalhos = new JPanel(new GridLayout(2, 2, 4, 4));
        atalhos.setBorder(BorderFactory.createTitledBorder("Periodo rapido"));
        adicionarAtalho(atalhos, "Este mes",      0);
        adicionarAtalho(atalhos, "Este ano",      1);
        adicionarAtalho(atalhos, "Ultimos 30 d",  2);
        adicionarAtalho(atalhos, "Ultimos 90 d",  3);
        p.add(atalhos, g);

        // Separador
        g.gridy = 7; p.add(new JSeparator(), g);

        // Legenda do que inclui
        g.gridy = 8;
        JPanel opcoes = new JPanel(new GridLayout(3, 1, 0, 4));
        opcoes.setBorder(BorderFactory.createTitledBorder("Relatorio inclui"));
        opcoes.add(new JLabel("+ Dados do cliente e seus animais"));
        opcoes.add(new JLabel("+ Todos os servicos do periodo"));
        opcoes.add(new JLabel("+ Total por servico e resumo"));
        p.add(opcoes, g);

        // Botão Preview
        g.gridy = 9;
        JButton btnPreview = new JButton("Pre-visualizar");
        estilizar(btnPreview, new Color(108, 117, 125));
        btnPreview.addActionListener(e -> atualizarPreview());
        p.add(btnPreview, g);

        // Botão Gerar PDF
        g.gridy = 10;
        JButton btnPDF = new JButton("Gerar PDF");
        estilizar(btnPDF, new Color(220, 53, 69));
        btnPDF.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 14));
        btnPDF.addActionListener(e -> gerarPDF());
        p.add(btnPDF, g);

        g.gridy = 11; g.weighty = 1.0;
        p.add(new JLabel(), g);
        return p;
    }

    // ═══════════════════════════════════════════
    // PAINEL DIREITO — PREVIEW
    // ═══════════════════════════════════════════
    private JPanel criarPainelPreview() {
        JPanel p = new JPanel(new java.awt.BorderLayout());
        p.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Pre-visualizacao",
                TitledBorder.LEFT, TitledBorder.TOP,
                new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 13)));

        taPreview = new JTextArea();
        taPreview.setEditable(false);
        taPreview.setFont(new java.awt.Font("Courier New", java.awt.Font.PLAIN, 12));
        taPreview.setMargin(new Insets(10, 10, 10, 10));
        taPreview.setText("Selecione um cliente e clique em Pre-visualizar.");

        p.add(new JScrollPane(taPreview), java.awt.BorderLayout.CENTER);
        return p;
    }

    // ═══════════════════════════════════════════
    // LÓGICA
    // ═══════════════════════════════════════════
    private void carregarProprietarios() {
        try {
            proprietarioController.listarTodos().forEach(cbProprietario::addItem);
        } catch (SQLException ex) {
            erro("Erro ao carregar proprietarios:\n" + ex.getMessage());
        }
    }

    private void atualizarPreview() {
        Proprietario prop = (Proprietario) cbProprietario.getSelectedItem();
        if (prop == null) { taPreview.setText("Selecione um cliente."); return; }

        try {
            List<Animal>            animais  = animalController.listarPorProprietario(prop.getId());
            List<LancamentoServico> servicos = buscarServicos(prop);

            StringBuilder sb = new StringBuilder();
            sb.append("==================================================\n");
            sb.append("        RELATORIO DE SERVICOS - PET SHOP\n");
            sb.append("==================================================\n\n");

            sb.append("CLIENTE\n");
            sb.append("--------------------------------------------------\n");
            sb.append("Nome:     ").append(prop.getNome()).append("\n");
            sb.append("Telefone: ").append(prop.getTelefone()).append("\n");
            sb.append("Email:    ").append(prop.getEmail()    != null ? prop.getEmail()    : "-").append("\n");
            sb.append("Endereco: ").append(prop.getEndereco() != null ? prop.getEndereco() : "-").append("\n\n");

            sb.append("ANIMAIS CADASTRADOS (").append(animais.size()).append(")\n");
            sb.append("--------------------------------------------------\n");
            for (Animal a : animais) {
                sb.append(String.format("  %-15s | %-10s | %-10s\n",
                        a.getNome(), a.getEspecie(), a.getRaca() != null ? a.getRaca() : "-"));
            }
            sb.append("\n");

            sb.append("SERVICOS PRESTADOS (").append(servicos.size()).append(")\n");
            sb.append("--------------------------------------------------\n");

            if (servicos.isEmpty()) {
                sb.append("Nenhum servico encontrado no periodo.\n");
            } else {
                sb.append(String.format("  %-12s | %-20s | %-12s | %s\n",
                        "Data", "Servico", "Animal", "Valor"));
                sb.append("  ................................................\n");

                Map<String, Double> totalPorServico = new LinkedHashMap<>();
                double totalGeral = 0;

                for (LancamentoServico l : servicos) {
                    String data    = l.getDataLancamento() != null ? l.getDataLancamento().format(FMT) : "-";
                    String animal  = nomeAnimal(l.getAnimalId(), animais);
                    String servico = nomeServico(l.getServicoId());
                    sb.append(String.format("  %-12s | %-20s | %-12s | R$ %.2f\n",
                            data, servico, animal, l.getValor()));
                    totalPorServico.merge(servico, l.getValor(), Double::sum);
                    totalGeral += l.getValor();
                }

                sb.append("\nTOTAL POR SERVICO\n");
                sb.append("--------------------------------------------------\n");
                for (Map.Entry<String, Double> e : totalPorServico.entrySet())
                    sb.append(String.format("  %-30s R$ %.2f\n", e.getKey(), e.getValue()));
                sb.append(String.format("\n  TOTAL GERAL:                   R$ %.2f\n", totalGeral));
            }

            sb.append("\n==================================================\n");
            sb.append("Gerado em: ").append(LocalDate.now().format(FMT)).append("\n");

            taPreview.setText(sb.toString());
            taPreview.setCaretPosition(0);

        } catch (SQLException ex) {
            erro("Erro ao carregar dados:\n" + ex.getMessage());
        }
    }

    private void gerarPDF() {
        Proprietario prop = (Proprietario) cbProprietario.getSelectedItem();
        if (prop == null) { aviso("Selecione um cliente."); return; }

        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Salvar Relatorio PDF");
        fc.setSelectedFile(new File("relatorio_" + prop.getNome().replaceAll("\\s+", "_") + ".pdf"));
        if (fc.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;

        File arquivo = fc.getSelectedFile();
        if (!arquivo.getName().endsWith(".pdf"))
            arquivo = new File(arquivo.getAbsolutePath() + ".pdf");

        try {
            List<Animal>            animais  = animalController.listarPorProprietario(prop.getId());
            List<LancamentoServico> servicos = buscarServicos(prop);

            Document doc = new Document(PageSize.A4, 40, 40, 60, 40);
            PdfWriter.getInstance(doc, new FileOutputStream(arquivo));
            doc.open();

            // Cabeçalho
            Paragraph titulo = new Paragraph("PET SHOP SYSTEM", F_TITULO);
            titulo.setAlignment(Element.ALIGN_CENTER);
            doc.add(titulo);

            Paragraph sub = new Paragraph("Relatorio de Servicos por Cliente", F_SECAO);
            sub.setAlignment(Element.ALIGN_CENTER);
            sub.setSpacingAfter(4);
            doc.add(sub);

            String periodoStr = periodoTexto();
            doc.add(new Paragraph("Periodo: " + (periodoStr.isBlank() ? "Todo o periodo" : periodoStr), F_NORMAL));
            doc.add(new Paragraph("Gerado em: " + LocalDate.now().format(FMT), F_NORMAL));
            doc.add(new Chunk(new LineSeparator()));
            doc.add(Chunk.NEWLINE);

            // Dados do cliente
            doc.add(secao("DADOS DO CLIENTE"));
            PdfPTable tCliente = new PdfPTable(2);
            tCliente.setWidthPercentage(100);
            tCliente.setWidths(new float[]{1, 3});
            addCelula(tCliente, "Nome",     prop.getNome());
            addCelula(tCliente, "Telefone", prop.getTelefone());
            addCelula(tCliente, "Email",    prop.getEmail()    != null ? prop.getEmail()    : "-");
            addCelula(tCliente, "Endereco", prop.getEndereco() != null ? prop.getEndereco() : "-");
            doc.add(tCliente);
            doc.add(Chunk.NEWLINE);

            // Animais
            doc.add(secao("ANIMAIS CADASTRADOS (" + animais.size() + ")"));
            if (animais.isEmpty()) {
                doc.add(new Paragraph("Nenhum animal cadastrado.", F_NORMAL));
            } else {
                PdfPTable tAnimais = new PdfPTable(5);
                tAnimais.setWidthPercentage(100);
                tAnimais.setWidths(new float[]{2, 2, 2, 1, 1});
                addCabecalho(tAnimais, "Nome", "Especie", "Raca", "Sexo", "Peso (kg)");
                for (Animal a : animais) {
                    addLinha(tAnimais,
                            a.getNome(),
                            a.getEspecie(),
                            a.getRaca()  != null ? a.getRaca() : "-",
                            a.getSexo()  != null ? a.getSexo().toString() : "-",
                            a.getPeso()  >  0    ? String.format("%.1f", a.getPeso()) : "-");
                }
                doc.add(tAnimais);
            }
            doc.add(Chunk.NEWLINE);

            // Serviços
            doc.add(secao("SERVICOS PRESTADOS" + (periodoStr.isBlank() ? "" : periodoStr)
                    + " (" + servicos.size() + ")"));

            if (servicos.isEmpty()) {
                doc.add(new Paragraph("Nenhum servico encontrado no periodo informado.", F_NORMAL));
            } else {
                PdfPTable tServicos = new PdfPTable(5);
                tServicos.setWidthPercentage(100);
                tServicos.setWidths(new float[]{1.5f, 2.5f, 2f, 1.2f, 2.5f});
                addCabecalho(tServicos, "Data", "Servico", "Animal", "Valor (R$)", "Observacoes");

                Map<String, Double> totalPorServico = new LinkedHashMap<>();
                double totalGeral = 0;

                for (LancamentoServico l : servicos) {
                    String data    = l.getDataLancamento() != null ? l.getDataLancamento().format(FMT) : "-";
                    String animal  = nomeAnimal(l.getAnimalId(), animais);
                    String servico = nomeServico(l.getServicoId());
                    String obs     = l.getObservacoes() != null ? l.getObservacoes() : "";
                    addLinha(tServicos, data, servico, animal,
                            String.format("R$ %.2f", l.getValor()), obs);
                    totalPorServico.merge(servico, l.getValor(), Double::sum);
                    totalGeral += l.getValor();
                }
                doc.add(tServicos);
                doc.add(Chunk.NEWLINE);

                // Total por serviço
                doc.add(secao("TOTAL POR SERVICO"));
                PdfPTable tTotal = new PdfPTable(2);
                tTotal.setWidthPercentage(60);
                tTotal.setHorizontalAlignment(Element.ALIGN_LEFT);
                tTotal.setWidths(new float[]{3, 1.5f});
                addCabecalho(tTotal, "Servico", "Total (R$)");
                for (Map.Entry<String, Double> e : totalPorServico.entrySet())
                    addLinha(tTotal, e.getKey(), String.format("R$ %.2f", e.getValue()));

                PdfPCell cLabel = new PdfPCell(new Phrase("TOTAL GERAL", F_NEGRITO));
                PdfPCell cValor = new PdfPCell(new Phrase(String.format("R$ %.2f", totalGeral), F_NEGRITO));
                cLabel.setBackgroundColor(new BaseColor(220, 240, 220));
                cValor.setBackgroundColor(new BaseColor(220, 240, 220));
                cLabel.setPadding(5); cValor.setPadding(5);
                tTotal.addCell(cLabel); tTotal.addCell(cValor);
                doc.add(tTotal);
            }

            // Rodapé
            doc.add(Chunk.NEWLINE);
            doc.add(new Chunk(new LineSeparator()));
            Paragraph rodape = new Paragraph(
                    "UNIPAC Barbacena - Sistema de Gerenciamento PetShop - POO 2026", F_NORMAL);
            rodape.setAlignment(Element.ALIGN_CENTER);
            doc.add(rodape);

            doc.close();

            int abrir = JOptionPane.showConfirmDialog(this,
                    "PDF gerado com sucesso!\n" + arquivo.getAbsolutePath()
                            + "\n\nDeseja abrir o arquivo agora?",
                    "Sucesso", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);

            if (abrir == JOptionPane.YES_OPTION && Desktop.isDesktopSupported())
                Desktop.getDesktop().open(arquivo);

        } catch (Exception ex) {
            erro("Erro ao gerar PDF:\n" + ex.getMessage());
        }
    }

    // ═══════════════════════════════════════════
    // HELPERS — DADOS
    // ═══════════════════════════════════════════
    private List<LancamentoServico> buscarServicos(Proprietario prop) throws SQLException {
        String inicioStr = tfInicio.getText().trim();
        String fimStr    = tfFim.getText().trim();

        if (!inicioStr.isEmpty() && !fimStr.isEmpty()) {
            try {
                LocalDate inicio = LocalDate.parse(inicioStr, FMT);
                LocalDate fim    = LocalDate.parse(fimStr, FMT);
                return lancamentoController.listarPorProprietarioEPeriodo(prop.getId(), inicio, fim);
            } catch (DateTimeParseException ex) {
                aviso("Data invalida. Use dd/MM/aaaa."); return new ArrayList<>();
            }
        }

        // Sem período: todos os animais do proprietário
        List<Animal> animais = animalController.listarPorProprietario(prop.getId());
        List<LancamentoServico> todos = new ArrayList<>();
        for (Animal a : animais)
            todos.addAll(lancamentoController.listarPorAnimal(a.getId()));
        todos.sort((a, b) -> {
            if (a.getDataLancamento() == null) return 1;
            if (b.getDataLancamento() == null) return -1;
            return a.getDataLancamento().compareTo(b.getDataLancamento());
        });
        return todos;
    }

    private String nomeAnimal(int id, List<Animal> animais) {
        return animais.stream().filter(a -> a.getId() == id)
                .map(Animal::getNome).findFirst().orElse("ID " + id);
    }

    private String nomeServico(int id) {
        try {
            return servicoController.listarTodos().stream()
                    .filter(s -> s.getId() == id)
                    .map(Servico::getNome)
                    .findFirst().orElse("ID " + id);
        } catch (SQLException e) { return "ID " + id; }
    }

    private String periodoTexto() {
        String i = tfInicio.getText().trim();
        String f = tfFim.getText().trim();
        if (!i.isEmpty() && !f.isEmpty()) return " de " + i + " a " + f;
        if (!i.isEmpty()) return " a partir de " + i;
        if (!f.isEmpty()) return " ate " + f;
        return "";
    }

    // ═══════════════════════════════════════════
    // HELPERS — iText
    // ═══════════════════════════════════════════
    private Paragraph secao(String texto) {
        Paragraph p = new Paragraph(texto, F_SECAO);
        p.setSpacingBefore(8); p.setSpacingAfter(4);
        return p;
    }

    private void addCabecalho(PdfPTable t, String... colunas) {
        for (String c : colunas) {
            PdfPCell cell = new PdfPCell(new Phrase(c, F_CABECALHO));
            cell.setBackgroundColor(new BaseColor(41, 128, 185));
            cell.setPadding(5);
            t.addCell(cell);
        }
    }

    private void addLinha(PdfPTable t, String... valores) {
        for (String v : valores) {
            PdfPCell cell = new PdfPCell(new Phrase(v != null ? v : "-", F_NORMAL));
            cell.setPadding(4);
            t.addCell(cell);
        }
    }

    private void addCelula(PdfPTable t, String label, String valor) {
        PdfPCell cLabel = new PdfPCell(new Phrase(label, F_NEGRITO));
        PdfPCell cValor = new PdfPCell(new Phrase(valor, F_NORMAL));
        cLabel.setBackgroundColor(new BaseColor(235, 245, 255));
        cLabel.setPadding(4); cValor.setPadding(4);
        t.addCell(cLabel); t.addCell(cValor);
    }

    // ═══════════════════════════════════════════
    // HELPERS — UI
    // ═══════════════════════════════════════════
    private void adicionarAtalho(JPanel parent, String label, int tipo) {
        JButton btn = new JButton(label);
        btn.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 11));
        btn.addActionListener(e -> {
            LocalDate hoje = LocalDate.now();
            LocalDate inicio = switch (tipo) {
                case 0 -> hoje.withDayOfMonth(1);
                case 1 -> hoje.withDayOfYear(1);
                case 2 -> hoje.minusDays(30);
                default -> hoje.minusDays(90);
            };
            tfInicio.setText(inicio.format(FMT));
            tfFim.setText(hoje.format(FMT));
        });
        parent.add(btn);
    }

    private void estilizar(JButton b, Color c) {
        b.setBackground(c); b.setForeground(Color.WHITE); b.setFocusPainted(false);
    }
    private void aviso(String m) {
        JOptionPane.showMessageDialog(this, m, "Atencao", JOptionPane.WARNING_MESSAGE);
    }
    private void erro(String m) {
        JOptionPane.showMessageDialog(this, m, "Erro", JOptionPane.ERROR_MESSAGE);
    }
}