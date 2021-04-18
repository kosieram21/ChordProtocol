package ChordProtocol;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface INode extends Remote {
    String findSuccessor(int key) throws RemoteException;
    String findPredecessor(int key) throws RemoteException;
    String closestPrecedingFinger(int key) throws RemoteException;

    int getNodeId() throws RemoteException;

    String getSuccessorURL() throws RemoteException;
    void setSuccessorURL(String successorURL) throws RemoteException;
    String getPredecessorURL() throws RemoteException;
    void setPredecessorURL(String predecessorURL) throws RemoteException;

    boolean join(String nodeURL) throws RemoteException;
    boolean joinFinished(String nodeURL) throws RemoteException;

    void updateFingerTable(int s, int i) throws RemoteException;

    boolean insert(String word, String definition) throws RemoteException;
    String lookup(String word) throws RemoteException;

    String printFingerTable() throws RemoteException;
    String printDictionary() throws RemoteException;
}