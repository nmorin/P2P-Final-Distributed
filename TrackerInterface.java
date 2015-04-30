import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.*;

public interface TrackerInterface extends Remote {
        public List<String> query(String fileName) throws RemoteException;
        public void seedFile(String fileName, String peerName, int peerPort, int fileSize, int numPieces) throws RemoteException;
}