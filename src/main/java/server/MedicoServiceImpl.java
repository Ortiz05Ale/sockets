package server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import models.Medico;
import models.MedicoService;

public class MedicoServiceImpl extends UnicastRemoteObject implements MedicoService {
    
    public MedicoServiceImpl() throws RemoteException {
        super();
    }
    
    @Override
    public Medico crearMedico(Medico medico) throws RemoteException {
        String sql = "INSERT INTO medico (nombre, especialidad, cedula, correo_electronico) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, medico.getNombre());
            stmt.setString(2, medico.getEspecialidad());
            stmt.setString(3, medico.getCedula());
            stmt.setString(4, medico.getCorreoElectronico());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("La creación del médico falló, no se insertó ningúnn registro");
            }

            
            return medico;
            
        } catch (SQLException e) {
            System.err.println("Error al crear médico: " + e.getMessage());
            throw new RemoteException("Error al crear médico", e);
        }
    }

    @Override
    public Medico obtenerMedico(String cedula) throws RemoteException {
        String sql = "SELECT * FROM medico WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, cedula);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToMedico(rs);
                } else {
                    return null;
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener médico: " + e.getMessage());
            throw new RemoteException("Error al obtener médico", e);
        }
    }

    
    @Override
    public List<Medico> listarMedicos() throws RemoteException {
        String sql = "SELECT * FROM medico ORDER BY nombre";
        List<Medico> medicos = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                medicos.add(mapResultSetToMedico(rs));
            }
            
            return medicos;
            
        } catch (SQLException e) {
            System.err.println("Error al listar médicos: " + e.getMessage());
            throw new RemoteException("Error al listar médicos", e);
        }
    }
    
    @Override
    public boolean actualizarMedico(Medico medico) throws RemoteException {
        String sql = "UPDATE medico SET nombre = ?, especialidad = ?, cedula = ?, correo_electronico = ? WHERE cedula = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, medico.getNombre());
            stmt.setString(2, medico.getEspecialidad());
            stmt.setString(3, medico.getCedula());
            stmt.setString(4, medico.getCorreoElectronico());
            stmt.setString(5, medico.getCedula());
            
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al actualizar médico: " + e.getMessage());
            throw new RemoteException("Error al actualizar médico", e);
        }
    }
    
    @Override
    public boolean eliminarMedico(String cedula) throws RemoteException {
        String sql = "DELETE FROM medico WHERE cedula = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, cedula);
            
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al eliminar médico: " + e.getMessage());
            throw new RemoteException("Error al eliminar médico", e);
        }
    }
    
    private Medico mapResultSetToMedico(ResultSet rs) throws SQLException {
        Medico medico = new Medico();
        medico.setNombre(rs.getString("nombre"));
        medico.setEspecialidad(rs.getString("especialidad"));
        medico.setCedula(rs.getString("cedula"));
        medico.setCorreoElectronico(rs.getString("correo_electronico"));
        return medico;
    }
}