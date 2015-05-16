import java.util.*;

public class PeerFile {

	private static String fileName;
	private static int numPieces;
	private static ArrayList<Integer> completePieces;
	private static ArrayList<Integer> downloadingPieces;
	private static ArrayList<Integer> neededPieces;
	private static int size;
	private static int[] fileBitPieces;

	// Called after initiating a file request
	public PeerFile(String fileName, int numPieces, int size, boolean downloadingFile) {
		this.fileName = fileName;
		this.size = size;
		this.numPieces = numPieces;
		this.completePieces = new ArrayList<Integer>();
		this.neededPieces = new ArrayList<Integer>();
		fileBitPieces = new int[numPieces];
		if (downloadingFile) {
			for (int i = 0; i < numPieces; i++) {
				this.neededPieces.add((Integer)i);
				fileBitPieces[i] = 0;
			}
		} else {
			// Assume seeding
			for (int i = 0; i < numPieces; i++) {
				this.completePieces.add((Integer)i);
				fileBitPieces[i] = 1;
			}
		}
	}

	public ArrayList<Integer> getPiecesNeeded() { return neededPieces; }

	public ArrayList<Integer> getCompletePieces() { return completePieces; }

	public int getPieceValue(int pieceNum) { return fileBitPieces[pieceNum]; }

	public int getSize() { return size; }

	public int getNumPieces() { return numPieces; }

	public void finishedDownloadingPiece(int pieceNum) {
		downloadingPieces.remove((Integer)pieceNum);
		completePieces.add((Integer)pieceNum);
		fileBitPieces[pieceNum] = 1;
	}

	public void startDownloadingPiece(int pieceNum) {
		System.out.println("Needed pieces before:");
		printIList(neededPieces);
		fileBitPieces[pieceNum] = 2;
		neededPieces.remove((Integer)pieceNum);
		downloadingPieces.add((Integer)pieceNum);
		System.out.println("removed piece. Here is neededPieces now:");
		printIList(neededPieces);
	}
 
	private void printSList(ArrayList<String> list) {
		System.out.println("My list: ");
		for (String elem : list) {
			System.out.print(elem + ", ");
		}
	}

	private void printIList(ArrayList<Integer> list) {
		System.out.println("My list: ");
		for (Integer elem : list) {
			System.out.print(elem + ", ");
		}
	}
}

