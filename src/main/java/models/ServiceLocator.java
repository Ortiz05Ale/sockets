/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package models;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 *
 * @author Asus
 */
public interface ServiceLocator extends Remote {
    MedicoService getMedicoService() throws RemoteException;
    PacienteService getPacienteService() throws RemoteException;
    CitaService getCitaService() throws RemoteException;
}
    
