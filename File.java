/* 
 * Class File
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

import java.util.*;

public class File {

	private List<List<String>> ids;
	private int numPieces;
	private long pieceSize;
	
	public File(String initialUser) {
		// decide how big to make pieces and how many pieces we will have
		// ids = new List(numPieces); //not correct java
		// add the initial user to the list for every piece of the file
		// 		for every index i in ids:
		// 			ids.get(i).add(initialUser);


		// things to think about: when a user leaves the system, we will have to go through
		// and delete their name from every string list of every index of ids
	}

	public int getNumPieces() {
		return numPieces;
	}

	public long getPieceSize() {
		return pieceSize;
	}

	public void updateUserWithFilePiece(String userID, int pieceNum) {
		ids.get(pieceNum).add(userID);
	}

	public boolean doesPieceExist(int pieceNum) {
		return (ids.get(pieceNum) == null);
	}

}