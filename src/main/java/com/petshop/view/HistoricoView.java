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
import java.util.ArrayList;
import java.util.List;

public class HistoricoView extends JDialog {

    private final LancamentoController lancamentoController = new LancamentoController();
    private final AnimalController     animalController     = new AnimalController();
    private final ServicoController    servicoController    = new ServicoController();

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // Filtros
    private JComboBox<Animal>  cbAnimal;
    private JComboBox<Object>  cbServico;   // "Todos" + Servico
    private JTextField         tfInicio;
    private JTextField         tfFim;

    // Tabela
    private JTable            tabela;
    private DefaultTableModel modelo;

    // Rodapé
    private JLabel lblTotal;
    private JLabel lblValor;

    public HistoricoView(JFrame parent) {
        super(parent, "🔍 Histórico de Serviços", true);
        setSize(1000, 620);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));
        getRootPane().setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(criarPainelFiltros(), BorderLayout.WEST);
        add(criarPainelTabela(), BorderLayout.CENTER);
        add(criarRodape(),       BorderLayout.SOUTH);

        carregarCombos();
    }

    // ═══════════════════════════════════════════
    // PAINEL ESQUERDO — FILTROS
    // ═══════════════════════════════════════════
    private JPanel criarPainelFiltros() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setPreferredSize(new Dimension(260, 0));
        p.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Filtros",
                TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 13)));

        GridBagConstraints g = new GridBagConstraints();
        g.insets  = new Insets(5, 8, 4, 8);
        g.fill    = GridBagConstraints.HORIZONTAL;
        g.weightx = 1.0;
        g.gridx   = 0;

        // Animal — obrigatório para busca
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
        p.add(cbAnimal, g);

        // Serviço — opcional
        g.gridy = 2; p.add(new JLabel("Tipo de Serviço (opcional)"), g);
        g.gridy = 3;
        cbServico = new JComboBox<>();
        cbServico.setRenderer(new DefaultListCellRenderer() {
            @Override public Component getListCellRendererComponent(JList<?> l, Object v,
                                                                    int i, boolean sel, boolean foc) {
                super.getListCellRendererComponent(l, v, i, sel, foc);
                if (v instanceof Servico s) setText(s.getNome());
                return this;
            }
        });
        p.add(cbServico, g);

        // Período
        g.gridy = 4; p.add(new JLabel("Data início  (dd/MM/aaaa)"), g);
        g.gridy = 5; tfInicio = new JTextField(); p.add(tfInicio, g);
        g.gridy = 6; p.add(new JLabel("Data fim  (dd/MM/aaaa)"), g);
        g.gridy = 7; tfFim = new JTextField(); p.add(tfFim, g);

        // Atalhos rápidos de período
        g.gridy = 8;
        JPanel atalhos = new JPanel(new GridLayout(2, 2, 4, 4));
        atalhos.setBorder(BorderFactory.createTitledBorder("Período rápido"));
        adicionarAtalho(atalhos, "Hoje",        0);
        adicionarAtalho(atalhos, "Esta semana", 1);
        adicionarAtalho(atalhos, "Este mês",    2);
        adicionarAtalho(atalhos, "Este ano",    3);
        p.add(atalhos, g);

        // Botão Buscar
        g.gridy = 9;
        JButton btnBuscar = new JButton("🔍 Buscar");
        estilizar(btnBuscar, new Color(0, 123, 255));
        btnBuscar.addActionListener(e -> buscar());
        p.add(btnBuscar, g);

        // Botão Limpar
        g.gridy = 10;
        JButton btnLimpar = new JButton("🔄 Limpar Filtros");
        btnLimpar.addActionListener(e -> limpar());
        p.add(btnLimpar, g);

        g.gridy = 11; g.weighty = 1.0; p.add(new JLabel(), g);
        return p;
    }

    // ═══════════════════════════════════════════
    // PAINEL CENTRAL — TABELA
    // ═══════════════════════════════════════════
    private JPanel criarPainelTabela() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Resultados",
                TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 13)));

        String[] cols = {"ID", "Animal", "Espécie", "Serviço", "Data", "Valor (R$)", "Observações"};
        modelo = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tabela = new JTable(modelo);
        tabela.setRowHeight(24);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.getColumnModel().getColumn(0).setMaxWidth(45);
        tabela.getColumnModel().getColumn(4).setPreferredWidth(100);
        tabela.getColumnModel().getColumn(5).setPreferredWidth(85);

        // Linhas alternadas (zebra)
        tabela.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object v,
                                                                     boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                if (!sel) setBackground(row % 2 == 0 ? Color.WHITE : new Color(240, 245, 255));
                return this;
            }
        });

        p.add(new JScrollPane(tabela), BorderLayout.CENTER);
        return p;
    }

    // ═══════════════════════════════════════════
    // RODAPÉ
    // ═══════════════════════════════════════════
    private JPanel criarRodape() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 6));
        p.setBorder(BorderFactory.createEtchedBorder());
        lblTotal = new JLabel("Registros: 0");
        lblValor  = new JLabel("Total: R$ 0,00");
        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblValor.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblValor.setForeground(new Color(40, 167, 69));
        p.add(lblTotal);
        p.add(new JSeparator(SwingConstants.VERTICAL));
        p.add(lblValor);
        return p;
    }

    // ═══════════════════════════════════════════
    // LÓGICA
    // ═══════════════════════════════════════════
    private void carregarCombos() {
        try {
            animalController.listarTodos().forEach(cbAnimal::addItem);
        } catch (SQLException ex) {
            erro("Erro ao carregar animais:\n" + ex.getMessage());
        }

        cbServico.addItem("Todos os serviços");
        try {
            servicoController.listarTodos().forEach(cbServico::addItem);
        } catch (SQLException ex) {
            erro("Erro ao carregar serviços:\n" + ex.getMessage());
        }
    }

    private void buscar() {
        Animal animal = (Animal) cbAnimal.getSelectedItem();
        if (animal == null) { aviso("Selecione um animal para buscar o histórico."); return; }

        // Datas — obrigatórias para usar os métodos do controller
        String inicioStr = tfInicio.getText().trim();
        String fimStr    = tfFim.getText().trim();

        // Se não preencheu datas, busca tudo do animal (ou filtrado por serviço)
        List<LancamentoServico> lista = new ArrayList<>();

        try {
            boolean temServico = cbServico.getSelectedItem() instanceof Servico;
            boolean temPeriodo = !inicioStr.isEmpty() && !fimStr.isEmpty();

            if (temServico && !temPeriodo) {
                // Filtro: animal + serviço (sem período)
                Servico s = (Servico) cbServico.getSelectedItem();
                lista = lancamentoController.listarPorAnimalEServico(animal.getId(), s.getId());

            } else if (temPeriodo) {
                // Validar datas
                LocalDate inicio, fim;
                try {
                    inicio = LocalDate.parse(inicioStr, FMT);
                } catch (DateTimeParseException ex) {
                    aviso("Data início inválida. Use dd/MM/aaaa."); return;
                }
                try {
                    fim = LocalDate.parse(fimStr, FMT);
                } catch (DateTimeParseException ex) {
                    aviso("Data fim inválida. Use dd/MM/aaaa."); return;
                }

                if (temServico) {
                    // animal + serviço + período: busca por período e filtra serviço localmente
                    Servico s = (Servico) cbServico.getSelectedItem();
                    List<LancamentoServico> porPeriodo =
                            lancamentoController.listarPorAnimalEPeriodo(animal.getId(), inicio, fim);
                    for (LancamentoServico l : porPeriodo)
                        if (l.getServicoId() == s.getId()) lista.add(l);
                } else {
                    // animal + período
                    lista = lancamentoController.listarPorAnimalEPeriodo(animal.getId(), inicio, fim);
                }

            } else {
                // Só animal — sem período nem serviço
                lista = lancamentoController.listarPorAnimal(animal.getId());
            }

        } catch (IllegalArgumentException ex) {
            aviso(ex.getMessage()); return;
        } catch (SQLException ex) {
            erro("Erro ao buscar histórico:\n" + ex.getMessage()); return;
        }

        // Ordem decrescente por data
        lista.sort((a, b) -> {
            if (a.getDataLancamento() == null) return 1;
            if (b.getDataLancamento() == null) return -1;
            return b.getDataLancamento().compareTo(a.getDataLancamento());
        });

        popularTabela(lista, animal);
    }

    private void popularTabela(List<LancamentoServico> lista, Animal animal) {
        modelo.setRowCount(0);
        double total = 0;

        for (LancamentoServico l : lista) {
            String data = l.getDataLancamento() != null ? l.getDataLancamento().format(FMT) : "";
            modelo.addRow(new Object[]{
                    l.getId(),
                    animal.getNome(),
                    animal.getEspecie(),
                    nomeServico(l.getServicoId()),
                    data,
                    String.format("%.2f", l.getValor()),
                    l.getObservacoes()
            });
            total += l.getValor();
        }

        lblTotal.setText("Registros: " + lista.size());
        lblValor.setText(String.format("Total: R$ %.2f", total).replace('.', ','));
    }

    private void limpar() {
        if (cbAnimal.getItemCount()  > 0) cbAnimal.setSelectedIndex(0);
        cbServico.setSelectedIndex(0);
        tfInicio.setText("");
        tfFim.setText("");
        modelo.setRowCount(0);
        lblTotal.setText("Registros: 0");
        lblValor.setText("Total: R$ 0,00");
    }

    private void adicionarAtalho(JPanel parent, String label, int tipo) {
        JButton btn = new JButton(label);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        btn.addActionListener(e -> {
            LocalDate hoje = LocalDate.now();
            LocalDate inicio = switch (tipo) {
                case 0 -> hoje;
                case 1 -> hoje.minusDays(hoje.getDayOfWeek().getValue() - 1L);
                case 2 -> hoje.withDayOfMonth(1);
                default -> hoje.withDayOfYear(1);
            };
            tfInicio.setText(inicio.format(FMT));
            tfFim.setText(hoje.format(FMT));
        });
        parent.add(btn);
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