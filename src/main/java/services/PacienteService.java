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
public interface PacienteService extends Remote {
    Paciente crearPaciente(Paciente paciente) throws RemoteException;
    Paciente obtenerPaciente(int id) throws RemoteException;
    List<Paciente> listarPacientes() throws RemoteException;
    boolean actualizarPaciente(Paciente paciente) throws RemoteException;
    boolean eliminarPaciente(String cedula) throws RemoteException;
}