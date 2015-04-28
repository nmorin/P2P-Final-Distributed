/* 
 * Class TrackerFile
 * Megan Maher and Nicole Morin
 * Bowdoin College Class of 2016
 * Distributed Systems
 *
 * Created: April 27, 2015
 * Last Modified: April 27, 2015
 *
 * Holds a list with the same number of elements as the number of pieces
 * of that file. AKA the number of pieces we break the file up into.
 * Then, for each of these elements, we have a list of userIDs that have
 * that piece of the file.
 */

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.io.*;

public class TrackerFile {

	private static String fileName;
	private static List<Peer> peersWithFile;
	private static int fileSize;
	private static int numPieces;


	public Tracker() {
        super();
    }

	public int getNumPieces() {
		return numPieces;
	}

	public int getFileSize() {
		return fileSize;
	}

	public void updateWithUser(String userID) {
		ids.add(userID);
	}

}

