package client;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;

import models.Medico;
import models.MedicoService;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.rmi.RemoteException;
import java.util.List;

public class MedicoPanel extends JPanel {
    private JTable tableMedicos;
    private DefaultTableModel tableModel;
    private JTextField txtNombre, txtEspecialidad, txtCedula, txtCorreo;
    private JButton btnAgregar, btnActualizar, btnEliminar, btnLimpiar;
    private MedicoService medicoService;
    private Medico medicoSeleccionado;
    
    // Colores del tema moderno
    private static final Color PRIMARY_COLOR = new Color(34, 139, 230);
    private static final Color SECONDARY_COLOR = new Color(245, 247, 250);
    private static final Color ACCENT_COLOR = new Color(52, 168, 83);
    private static final Color DANGER_COLOR = new Color(234, 67, 53);
    private static final Color TEXT_DARK = new Color(33, 33, 33);
    private static final Color BORDER_COLOR = new Color(218, 220, 224);
    private static final Color HOVER_COLOR = new Color(232, 240, 254);
    
    public MedicoPanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        try {
            medicoService = Cliente.getServiceLocator().getMedicoService();
            initComponents();
            cargarMedicos();
        } catch (RemoteException e) {
            showErrorDialog("Error al conectar con el servidor: " + e.getMessage());
        }
    }
    
    private void initComponents() {
        // Panel principal del formulario con diseño card
        JPanel mainFormPanel = createCardPanel();
        mainFormPanel.setLayout(new BorderLayout(0, 20));
        
        // Título del formulario
        JLabel titleLabel = new JLabel("Gestión de Médicos", JLabel.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(TEXT_DARK);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        // Panel de campos del formulario
        JPanel formFieldsPanel = new JPanel(new GridBagLayout());
        formFieldsPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        
        // Configuración base para los componentes
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Primera fila - Nombre y Especialidad
        gbc.gridx = 0; gbc.gridy = 0;
        formFieldsPanel.add(createStyledLabel("Nombre:      "), gbc);
        
        gbc.gridx = 1;
        gbc.gridwidth=2;
        txtNombre = createStyledTextField("Nombre");
        formFieldsPanel.add(txtNombre, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        formFieldsPanel.add(createStyledLabel("Especialidad:"), gbc);
        
        gbc.gridx = 2; 
        gbc.gridwidth=2;
        txtEspecialidad = createStyledTextField("Especialidad");
        formFieldsPanel.add(txtEspecialidad, gbc);
        
        // Segunda fila - Cédula y Correo
        gbc.gridx = 0; gbc.gridy = 2;
        formFieldsPanel.add(createStyledLabel("Cédula:      "), gbc);
        
        gbc.gridx = 1;
        gbc.gridwidth=2;
        txtCedula = createStyledTextField("cédula");
        formFieldsPanel.add(txtCedula, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
        formFieldsPanel.add(createStyledLabel("Correo:      "), gbc);
        
        gbc.gridx = 1;
        gbc.gridwidth=2;
        txtCorreo = createStyledTextField("correo");
        formFieldsPanel.add(txtCorreo, gbc);
        
        // Panel de botones con diseño moderno
        JPanel buttonPanel = createButtonPanel();
        
        // Ensamblar el formulario
        mainFormPanel.add(titleLabel, BorderLayout.NORTH);
        mainFormPanel.add(formFieldsPanel, BorderLayout.CENTER);
        mainFormPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        // Configurar tabla con estilo moderno
        setupModernTable();
        JScrollPane scrollPane = new JScrollPane(tableMedicos);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);
        
        // Panel contenedor de la tabla
        JPanel tablePanel = createCardPanel();
        tablePanel.setLayout(new BorderLayout());
        
        JLabel tableTitle = new JLabel("Lista de Médicos", JLabel.LEFT);
        tableTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        tableTitle.setForeground(TEXT_DARK);
        tableTitle.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        tablePanel.add(tableTitle, BorderLayout.NORTH);
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        
        // Agregar componentes al panel principal
        add(mainFormPanel, BorderLayout.NORTH);
        add(tablePanel, BorderLayout.CENTER);
        
        // Configurar eventos (manteniendo la funcionalidad original)
        setupEventHandlers();
        
        // Estado inicial de los botones
        btnActualizar.setEnabled(false);
        btnEliminar.setEnabled(false);
    }
    
    private JPanel createCardPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        return panel;
    }
    
    private JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setForeground(TEXT_DARK);
        return label;
    }
    
    private JTextField createStyledTextField(String placeholder) {
        JTextField textField = new JTextField(15);
        textField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        textField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 2),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        textField.setToolTipText(placeholder);
        
        // Efecto hover
        textField.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (textField.isEnabled()) {
                    textField.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(PRIMARY_COLOR, 2),
                        BorderFactory.createEmptyBorder(8, 12, 8, 12)
                    ));
                }
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                if (textField.isEnabled()) {
                    textField.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(BORDER_COLOR, 2),
                        BorderFactory.createEmptyBorder(8, 12, 8, 12)
                    ));
                }
            }
        });
        
        return textField;
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 20));
        panel.setBackground(Color.WHITE);
        
        btnAgregar = createStyledButton("+ Agregar", ACCENT_COLOR);
        btnActualizar = createStyledButton("✓ Actualizar", PRIMARY_COLOR);
        btnEliminar = createStyledButton("✗ Eliminar", DANGER_COLOR);
        btnLimpiar = createStyledButton("⟲ Limpiar", new Color(108, 117, 125));
        
        panel.add(btnAgregar);
        panel.add(btnActualizar);
        panel.add(btnEliminar);
        panel.add(btnLimpiar);
        
        return panel;
    }
    
    private JButton createStyledButton(String text, Color backgroundColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(backgroundColor);
        button.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(120, 40));
        
        // Efectos hover
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (button.isEnabled()) {
                    button.setBackground(backgroundColor.darker());
                }
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                if (button.isEnabled()) {
                    button.setBackground(backgroundColor);
                }
            }
        });
        
        return button;
    }
    
    private void setupModernTable() {
        tableModel = new DefaultTableModel(
            new Object[][] {},
            new String[] {"Cédula", "Nombre", "Especialidad", "Correo Electrónico"}
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tableMedicos = new JTable(tableModel);
        tableMedicos.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tableMedicos.setRowHeight(45);
        tableMedicos.setGridColor(BORDER_COLOR);
        tableMedicos.setSelectionBackground(HOVER_COLOR);
        tableMedicos.setSelectionForeground(TEXT_DARK);
        tableMedicos.setShowVerticalLines(true);
        tableMedicos.setShowHorizontalLines(true);
        
        // Estilizar header
        JTableHeader header = tableMedicos.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(SECONDARY_COLOR);
        header.setForeground(TEXT_DARK);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, PRIMARY_COLOR));
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 50));
        
        // Centrar contenido de celdas
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < tableMedicos.getColumnCount(); i++) {
            tableMedicos.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
        
        // Ajustar ancho de columnas
        tableMedicos.getColumnModel().getColumn(0).setPreferredWidth(120); // Cédula
        tableMedicos.getColumnModel().getColumn(1).setPreferredWidth(200); // Nombre
        tableMedicos.getColumnModel().getColumn(2).setPreferredWidth(150); // Especialidad
        tableMedicos.getColumnModel().getColumn(3).setPreferredWidth(250); // Correo
    }
    
    private void setupEventHandlers() {
        btnAgregar.addActionListener(this::agregarMedico);
        btnActualizar.addActionListener(this::actualizarMedico);
        btnEliminar.addActionListener(this::eliminarMedico);
        btnLimpiar.addActionListener(e -> limpiarFormulario());
        
        tableMedicos.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = tableMedicos.getSelectedRow();
                if (row >= 0) {
                    medicoSeleccionado = new Medico();
                    medicoSeleccionado.setCedula((String) tableModel.getValueAt(row, 0));
                    medicoSeleccionado.setNombre((String) tableModel.getValueAt(row, 1));
                    medicoSeleccionado.setEspecialidad((String) tableModel.getValueAt(row, 2));
                    medicoSeleccionado.setCorreoElectronico((String) tableModel.getValueAt(row, 3));
                    
                    txtCedula.setText(medicoSeleccionado.getCedula());
                    txtNombre.setText(medicoSeleccionado.getNombre());
                    txtEspecialidad.setText(medicoSeleccionado.getEspecialidad());
                    txtCorreo.setText(medicoSeleccionado.getCorreoElectronico());
                    
                    btnActualizar.setEnabled(true);
                    btnEliminar.setEnabled(true);
                    btnAgregar.setEnabled(false);
                }
            }
        });
    }
    
    private void cargarMedicos() {
        try {
            // Limpiar la tabla
            tableModel.setRowCount(0);
            
            // Obtener la lista de médicos
            List<Medico> medicos = medicoService.listarMedicos();
            
            // Agregar los médicos a la tabla
            for (Medico medico : medicos) {
                tableModel.addRow(new Object[] {
                    medico.getCedula(),
                    medico.getNombre(),
                    medico.getEspecialidad(),
                    medico.getCorreoElectronico()
                });
            }
        } catch (RemoteException e) {
            showErrorDialog("Error al cargar médicos: " + e.getMessage());
        }
    }
    
    private void agregarMedico(ActionEvent e) {
        if (!validarFormulario()) {
            return;
        }
        
        try {
            Medico medico = new Medico();
            medico.setNombre(txtNombre.getText());
            medico.setEspecialidad(txtEspecialidad.getText());
            medico.setCedula(txtCedula.getText());
            medico.setCorreoElectronico(txtCorreo.getText());
            
            medicoService.crearMedico(medico);
            
            showSuccessDialog("Médico agregado correctamente");
            
            limpiarFormulario();
            cargarMedicos();
            
        } catch (RemoteException ex) {
            showErrorDialog("Error al agregar médico: " + ex.getMessage());
        }
    }
    
    private void actualizarMedico(ActionEvent e) {
        if (medicoSeleccionado == null) {
            showWarningDialog("Debe seleccionar un médico para actualizar");
            return;
        }
        
        if (!validarFormulario()) {
            return;
        }
        
        try {
            medicoSeleccionado.setNombre(txtNombre.getText());
            medicoSeleccionado.setEspecialidad(txtEspecialidad.getText());
            medicoSeleccionado.setCedula(txtCedula.getText());
            medicoSeleccionado.setCorreoElectronico(txtCorreo.getText());
            
            medicoService.actualizarMedico(medicoSeleccionado);
            
            showSuccessDialog("Médico actualizado correctamente");
            
            limpiarFormulario();
            cargarMedicos();
            
        } catch (RemoteException ex) {
            showErrorDialog("Error al actualizar médico: " + ex.getMessage());
        }
    }
    
    private void eliminarMedico(ActionEvent e) {
        if (medicoSeleccionado == null) {
            showWarningDialog("Debe seleccionar un médico para eliminar");
            return;
        }
        
        int option = JOptionPane.showConfirmDialog(this, 
                                                 "¿Está seguro de eliminar el médico " + medicoSeleccionado.getNombre() + "?", 
                                                 "Confirmar eliminación", 
                                                 JOptionPane.YES_NO_OPTION);
        
        if (option == JOptionPane.YES_OPTION) {
            try {
                medicoService.eliminarMedico(medicoSeleccionado.getCedula());
                
                showSuccessDialog("Médico eliminado correctamente");
                
                limpiarFormulario();
                cargarMedicos();
                
            } catch (RemoteException ex) {
                showErrorDialog("Error al eliminar médico: " + ex.getMessage());
            }
        }
    }
    
    private void limpiarFormulario() {
        txtNombre.setText("");
        txtEspecialidad.setText("");
        txtCedula.setText("");
        txtCorreo.setText("");
        medicoSeleccionado = null;
        btnActualizar.setEnabled(false);
        btnEliminar.setEnabled(false);
        btnAgregar.setEnabled(true);
    }
    
    private boolean validarFormulario() {
        if (txtNombre.getText().trim().isEmpty()) {
            showWarningDialog("El nombre es obligatorio");
            return false;
        }
        
        if (txtEspecialidad.getText().trim().isEmpty()) {
            showWarningDialog("La especialidad es obligatoria");
            return false;
        }
        
        if (txtCedula.getText().trim().isEmpty()) {
            showWarningDialog("La cédula es obligatoria");
            return false;
        }
        
        if (txtCorreo.getText().trim().isEmpty()) {
            showWarningDialog("El correo electrónico es obligatorio");
            return false;
        }
        
        // Validar formato de correo electrónico
        if (!txtCorreo.getText().matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            showWarningDialog("El formato del correo electrónico no es válido");
            return false;
        }
        
        return true;
    }
    
    // Métodos helper para mostrar diálogos con estilo
    private void showErrorDialog(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
    
    private void showWarningDialog(String message) {
        JOptionPane.showMessageDialog(this, message, "Advertencia", JOptionPane.WARNING_MESSAGE);
    }
    
    private void showSuccessDialog(String message) {
        JOptionPane.showMessageDialog(this, message, "Información", JOptionPane.INFORMATION_MESSAGE);
    }
}