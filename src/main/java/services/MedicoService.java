/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package services;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
/**
 *
 * @author Asus
 */
public interface MedicoService extends Remote {
    Medico crearMedico(Medico medico) throws RemoteException;
    Medico obtenerMedico(String cedula) throws RemoteException;
    List<Medico> listarMedicos() throws RemoteException;
    boolean actualizarMedico(Medico medico) throws RemoteException;
    boolean eliminarMedico(String cedula) throws RemoteException;
}