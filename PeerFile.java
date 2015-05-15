import java.util.*;

public class PeerFile {

	private static String fileName;
	private static ArrayList<String> peersWithFile;
	private static int numPieces;
	private static ArrayList<Integer> completePieces;
	private static ArrayList<Integer> downloadingPieces;
	private static ArrayList<Integer> neededPieces;
	private static int size;

	// Called after initiating a file request
	public PeerFile(String fileName, int numPieces, int size, boolean downloadingFile) {
		this.fileName = fileName;
		this.size = size;
		this.numPieces = numPieces;
		// this.peersWithFile = new ArrayList<String>();
		// this.peersWithFile.addAll(peersWithFile);
		this.completePieces = new ArrayList<Integer>();
		this.neededPieces = new ArrayList<Integer>();
		if (downloadingFile) {
			for (int i = 0; i < numPieces; i++) {
				this.neededPieces.add((Integer)i);
			}
		} else {
			// Assume seeding
			for (int i = 0; i < numPieces; i++) {
				this.completePieces.add((Integer)i);
			}
		}
		
	}

	public ArrayList<Integer> getPiecesNeeded() { return neededPieces; }

	public ArrayList<Integer> getCompletePieces() { return completePieces; }

	public ArrayList<String> getPeerList() { return peersWithFile; }

	public void successFullyDownloadedPiece(int pieceNum) {
		neededPieces.remove((Integer)pieceNum);
		completePieces.add((Integer)pieceNum);
	}

	public int getSize() { return size; }

	public int getNumPieces() { return numPieces; }


}

