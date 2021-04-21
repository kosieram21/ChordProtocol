package ChordProtocol;

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface INode extends Remote {
    String findSuccessor(int key) throws RemoteException, MalformedURLException, NotBoundException;
    String findPredecessor(int key) throws RemoteException, MalformedURLException, NotBoundException;
    String closestPrecedingFinger(int key) throws RemoteException;

    int getNodeId() throws RemoteException;

    String getSuccessorURL() throws RemoteException;
    void setSuccessorURL(String successorURL) throws RemoteException;
    String getPredecessorURL() throws RemoteException;
    void setPredecessorURL(String predecessorURL) throws RemoteException;

    void join(String nodeURL) throws RemoteException, MalformedURLException, NotBoundException, InterruptedException;
    void acquireLock() throws RemoteException, InterruptedException;
    void releaseLock() throws RemoteException;

    void updateFingerTable(String url, int s, int i) throws RemoteException, MalformedURLException, NotBoundException;

    void insert(String word, String definition) throws RemoteException, MalformedURLException, NotBoundException;
    String lookup(String word) throws RemoteException, MalformedURLException, NotBoundException;

    String printFingerTable() throws RemoteException;
    String printDictionary() throws RemoteException;
}