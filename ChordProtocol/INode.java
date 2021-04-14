package ChordProtocol;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface INode extends Remote {
    String findSuccessor(int key, boolean traceFlag) throws RemoteException;
    String findPredecessor(int key) throws RemoteException;
    String closestPrecedingFinger(int key) throws RemoteException;
    String successor() throws RemoteException;
    String predecessor() throws RemoteException;
    boolean join(String nodeURL) throws RemoteException;
    boolean joinFinished(String nodeURL) throws RemoteException;
    boolean insert(String word, String definition) throws RemoteException;
    String lookup(String word) throws RemoteException;
    String printFingerTable() throws RemoteException;
    String printDictionary() throws RemoteException;
}