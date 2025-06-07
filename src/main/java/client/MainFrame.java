package client;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private JTabbedPane tabbedPane;
    
    public MainFrame() {
        setTitle("Sistema de Gestión Médica");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        initComponents();
    }
    
    private void initComponents() {
        tabbedPane = new JTabbedPane();
        CitaPanel citaPanel = new CitaPanel();
        
        // Agregar los paneles para cada entidad
        tabbedPane.addTab("Médicos", new MedicoPanel());
        tabbedPane.addTab("Pacientes", new PacientePanel());
        tabbedPane.addTab("Citas", citaPanel);


        
        getContentPane().add(tabbedPane, BorderLayout.CENTER);
    }
}