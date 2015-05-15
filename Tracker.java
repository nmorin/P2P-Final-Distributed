import java.util.*;

import java.rmi.*;
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;

public class Tracker implements TrackerInterface {

	private static Map<String, TrackerFile> trackerFiles;

	public Tracker() {
        super();
        this.trackerFiles = new HashMap<String, TrackerFile>();

	}

	public ArrayList<String> query(String fileName, String name, int portNum, String host) {
		ArrayList<String> response = new ArrayList<String>();
		if (trackerFiles.containsKey(fileName)) {
			TrackerFile temp = trackerFiles.get(fileName);
			response = temp.getPeerList();
			temp.addPeer(name, portNum, host);
			System.out.println("Yes, I have file " + fileName);
		} else {
			System.out.println("Tracker found no record of " + fileName);
			response = null;
		}
		return response;
	}

	public void seedFile(String fileName, String peerName, int peerPort, String host, int fileSize, int numPieces) {
		if (trackerFiles.containsKey(fileName)) { 
			System.out.println("A file by that name is already in tracker's database");
			return; 
		}
		TrackerFile temp = new TrackerFile(fileName, peerName, peerPort, host, fileSize, numPieces);
		trackerFiles.put(fileName, temp);
		System.out.println("Added file " + fileName + " from peer " + peerName);
		System.out.println("Peer list of " + fileName + ": " + temp.peerListToString());
	}

	private static void createAndBindSelf(int myPortNum, String myName) {
         try {
            Tracker tracker = new Tracker();
            TrackerInterface trackerStub = (TrackerInterface) UnicastRemoteObject.exportObject(tracker, 0);
            Registry registry = LocateRegistry.createRegistry(myPortNum);
            registry.bind(myName, trackerStub);
            System.out.println("Tracker binding complete");

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

	public static void main(String[] argv) {
		// Constant, public name and port number for tracker
		int myPortNum = 6666; 
		String myName = "Tracker";
		createAndBindSelf(myPortNum, myName);

	}

	
	
}