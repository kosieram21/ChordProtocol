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

    void join(String nodeURL) throws RemoteException, MalformedURLException, NotBoundException;
    void joinFinished(String nodeURL) throws RemoteException;

    void updateFingerTable(String url, int s, int i) throws RemoteException, MalformedURLException, NotBoundException;

    boolean insert(String word, String definition) throws RemoteException;
    String lookup(String word) throws RemoteException;

    String printFingerTable() throws RemoteException;
    String printDictionary() throws RemoteException;
}