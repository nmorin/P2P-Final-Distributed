import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.*;

public interface TrackerInterface extends Remote {
        public ArrayList<String> query(String fileName, String name, int portNum) throws RemoteException;
        public void seedFile(String fileName, String peerName, int peerPort, int fileSize, int numPieces) throws RemoteException;
}