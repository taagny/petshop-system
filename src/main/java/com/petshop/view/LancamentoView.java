package com.petshop.view;

import com.petshop.controller.AnimalController;
import com.petshop.controller.LancamentoController;
import com.petshop.controller.ServicoController;
import com.petshop.model.Animal;
import com.petshop.model.LancamentoServico;
import com.petshop.model.Servico;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.List;

public class LancamentoView extends JDialog {

    private final LancamentoController lancamentoController = new LancamentoController();
    private final AnimalController     animalController     = new AnimalController();
    private final ServicoController    servicoController    = new ServicoController();

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // Formulário
    private JComboBox<Animal>  cbAnimal;
    private JComboBox<Servico> cbServico;
    private JTextField         tfData;
    private JTextField         tfValor;
    private JTextArea          taObservacoes;

    // Tabela
    private JTable            tabela;
    private DefaultTableModel modelo;

    public LancamentoView(JFrame parent) {
        super(parent, "📋 Lançamento de Serviços", true);
        setSize(950, 600);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));
        getRootPane().setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(criarPainelFormulario(), BorderLayout.WEST);
        add(criarPainelTabela(),     BorderLayout.CENTER);

        carregarCombos();
        carregarTabela();
    }

    // ═══════════════════════════════════════════
    // PAINEL ESQUERDO — FORMULÁRIO
    // ═══════════════════════════════════════════
    private JPanel criarPainelFormulario() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setPreferredSize(new Dimension(320, 0));
        p.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Novo Lançamento",
                TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 13)));

        GridBagConstraints g = new GridBagConstraints();
        g.insets  = new Insets(6, 8, 4, 8);
        g.fill    = GridBagConstraints.HORIZONTAL;
        g.weightx = 1.0;
        g.gridx   = 0;

        // Animal
        g.gridy = 0; p.add(new JLabel("Animal *"), g);
        g.gridy = 1;
        cbAnimal = new JComboBox<>();
        cbAnimal.setRenderer(new DefaultListCellRenderer() {
            @Override public Component getListCellRendererComponent(JList<?> l, Object v,
                                                                    int i, boolean sel, boolean foc) {
                super.getListCellRendererComponent(l, v, i, sel, foc);
                if (v instanceof Animal a) setText(a.getNome() + " (" + a.getEspecie() + ")");
                return this;
            }
        });
        cbAnimal.addActionListener(e -> { carregarTabela(); preencherValor(); });
        p.add(cbAnimal, g);

        // Serviço
        g.gridy = 2; p.add(new JLabel("Serviço *"), g);
        g.gridy = 3;
        cbServico = new JComboBox<>();
        cbServico.setRenderer(new DefaultListCellRenderer() {
            @Override public Component getListCellRendererComponent(JList<?> l, Object v,
                                                                    int i, boolean sel, boolean foc) {
                super.getListCellRendererComponent(l, v, i, sel, foc);
                if (v instanceof Servico s) setText(s.getNome() + String.format(" — R$ %.2f", s.getPreco()));
                return this;
            }
        });
        cbServico.addActionListener(e -> preencherValor());
        p.add(cbServico, g);

        // Data
        g.gridy = 4; p.add(new JLabel("Data *  (dd/MM/aaaa)"), g);
        g.gridy = 5;
        tfData = new JTextField(LocalDate.now().format(FMT));
        p.add(tfData, g);

        // Valor
        g.gridy = 6; p.add(new JLabel("Valor (R$) *"), g);
        g.gridy = 7;
        tfValor = new JTextField();
        p.add(tfValor, g);

        // Observações
        g.gridy = 8; p.add(new JLabel("Observações"), g);
        g.gridy = 9; g.weighty = 1.0; g.fill = GridBagConstraints.BOTH;
        taObservacoes = new JTextArea(4, 18);
        taObservacoes.setLineWrap(true);
        taObservacoes.setWrapStyleWord(true);
        taObservacoes.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        p.add(new JScrollPane(taObservacoes), g);

        // Botões
        g.gridy = 10; g.weighty = 0; g.fill = GridBagConstraints.HORIZONTAL;
        JPanel botoes = new JPanel(new GridLayout(1, 2, 5, 0));

        JButton btnSalvar = new JButton("💾 Salvar");
        JButton btnLimpar = new JButton("🔄 Limpar");

        estilizar(btnSalvar, new Color(40, 167, 69));
        btnSalvar.addActionListener(e -> salvar());
        btnLimpar.addActionListener(e -> limpar());

        botoes.add(btnSalvar);
        botoes.add(btnLimpar);
        p.add(botoes, g);

        return p;
    }

    // ═══════════════════════════════════════════
    // PAINEL DIREITO — TABELA
    // ═══════════════════════════════════════════
    private JPanel criarPainelTabela() {
        JPanel p = new JPanel(new BorderLayout(5, 5));
        p.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Lançamentos do Animal Selecionado",
                TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 13)));

        String[] cols = {"ID", "Serviço", "Data", "Valor (R$)", "Observações"};
        modelo = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tabela = new JTable(modelo);
        tabela.setRowHeight(24);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.getColumnModel().getColumn(0).setMaxWidth(45);
        tabela.getColumnModel().getColumn(2).setPreferredWidth(100);
        tabela.getColumnModel().getColumn(3).setPreferredWidth(85);

        p.add(new JScrollPane(tabela), BorderLayout.CENTER);

        // Rodapé com total
        JLabel lblInfo = new JLabel(" Selecione um animal para ver o histórico");
        lblInfo.setForeground(Color.GRAY);
        p.add(lblInfo, BorderLayout.SOUTH);

        return p;
    }

    // ═══════════════════════════════════════════
    // LÓGICA
    // ═══════════════════════════════════════════
    private void carregarCombos() {
        cbAnimal.removeAllItems();
        cbServico.removeAllItems();

        try {
            List<Animal> animais = animalController.listarTodos();
            if (animais.isEmpty()) {
                aviso("Nenhum animal cadastrado. Cadastre um animal antes de lançar serviços.");
            }
            animais.forEach(cbAnimal::addItem);
        } catch (SQLException ex) {
            erro("Erro ao carregar animais:\n" + ex.getMessage());
        }

        try {
            List<Servico> servicos = servicoController.listarTodos();
            servicos.forEach(cbServico::addItem);
        } catch (SQLException ex) {
            erro("Erro ao carregar serviços:\n" + ex.getMessage());
        }

        preencherValor();
    }

    private void preencherValor() {
        if (cbServico.getSelectedItem() instanceof Servico s)
            tfValor.setText(String.format("%.2f", s.getPreco()).replace('.', ','));
    }

    private void salvar() {
        Animal  animal  = (Animal)  cbAnimal.getSelectedItem();
        Servico servico = (Servico) cbServico.getSelectedItem();

        if (animal == null || servico == null) { aviso("Selecione animal e serviço."); return; }

        String dataStr  = tfData.getText().trim();
        String valorStr = tfValor.getText().trim().replace(',', '.');

        if (dataStr.isEmpty() || valorStr.isEmpty()) { aviso("Preencha data e valor."); return; }

        LocalDate data;
        try {
            data = LocalDate.parse(dataStr, FMT);
        } catch (DateTimeParseException ex) {
            aviso("Data inválida. Use dd/MM/aaaa."); return;
        }

        double valor;
        try {
            valor = Double.parseDouble(valorStr);
            if (valor <= 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            aviso("Valor inválido. Digite um número positivo."); return;
        }

        LancamentoServico ls = new LancamentoServico();
        ls.setAnimalId(animal.getId());
        ls.setServicoId(servico.getId());
        ls.setDataLancamento(data);
        ls.setValor(valor);
        ls.setObservacoes(taObservacoes.getText().trim());

        try {
            lancamentoController.salvar(ls);
            JOptionPane.showMessageDialog(this, "Lançamento salvo com sucesso!",
                    "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            limpar();
            carregarTabela();
        } catch (IllegalArgumentException ex) {
            aviso(ex.getMessage());
        } catch (SQLException ex) {
            erro("Erro ao salvar lançamento:\n" + ex.getMessage());
        }
    }

    private void limpar() {
        if (cbServico.getItemCount() > 0) cbServico.setSelectedIndex(0);
        tfData.setText(LocalDate.now().format(FMT));
        taObservacoes.setText("");
        preencherValor();
    }

    /** Carrega lançamentos do animal atualmente selecionado no combo. */
    private void carregarTabela() {
        modelo.setRowCount(0);
        if (!(cbAnimal.getSelectedItem() instanceof Animal animal)) return;

        List<LancamentoServico> lista;
        try {
            lista = lancamentoController.listarPorAnimal(animal.getId());
        } catch (SQLException ex) {
            erro("Erro ao carregar lançamentos:\n" + ex.getMessage());
            return;
        }

        // Ordem decrescente por data
        lista.sort((a, b) -> {
            if (a.getDataLancamento() == null) return 1;
            if (b.getDataLancamento() == null) return -1;
            return b.getDataLancamento().compareTo(a.getDataLancamento());
        });

        double total = 0;
        for (LancamentoServico l : lista) {
            String data = l.getDataLancamento() != null ? l.getDataLancamento().format(FMT) : "";
            modelo.addRow(new Object[]{
                    l.getId(),
                    nomeServico(l.getServicoId()),
                    data,
                    String.format("%.2f", l.getValor()),
                    l.getObservacoes()
            });
            total += l.getValor();
        }
    }

    // ═══════════════════════════════════════════
    // HELPERS
    // ═══════════════════════════════════════════
    private String nomeServico(int id) {
        try {
            return servicoController.listarTodos().stream()
                    .filter(s -> s.getId() == id).map(Servico::getNome).findFirst().orElse("ID " + id);
        } catch (SQLException e) {
            return "ID " + id;
        }
    }

    private void estilizar(JButton b, Color c) {
        b.setBackground(c); b.setForeground(Color.WHITE); b.setFocusPainted(false);
    }
    private void aviso(String m) {
        JOptionPane.showMessageDialog(this, m, "Atenção", JOptionPane.WARNING_MESSAGE);
    }
    private void erro(String m) {
        JOptionPane.showMessageDialog(this, m, "Erro", JOptionPane.ERROR_MESSAGE);
    }
}