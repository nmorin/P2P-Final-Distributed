import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.io.*;

import java.util.Scanner;
import java.util.Random;
import java.util.*;
import java.util.ArrayList.*;

import java.net.ServerSocket;
import java.net.Socket;

import java.rmi.*;
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;

public class Peer implements PeerInterface {

    private static String myName;
    private static int myPortNum;

    private final int ID_MAX = 999999999;
    private final int ID_MIN = 100000000;
    private static final int PIECE_SIZE = 60;

    private static boolean alreadyConnectedToTracker = false;
    private static final String TRACKER_IP = "localhost";
    private static final String TRACKER_NAME = "Tracker";
    private static final int TRACKER_PORT = 6666;

    private static TrackerInterface leadTrackerStub;
    private static Map<String, PeerInterface> peerStubs;

    private static Map<String, PeerFile> myFiles;

    private static void print(String s) {
        System.out.println(s);
    }

	public Peer() {
        super();
        peerStubs = new HashMap<String, PeerInterface>();
        myFiles = new HashMap<String, PeerFile>();
	}

    public byte[] requestFile(String fileName, int piece) {
        try {
            RandomAccessFile file = new RandomAccessFile(fileName, "r");
            int size = myFiles.get(fileName).getSize();
            int numPieces = myFiles.get(fileName).getNumPieces();

            int offset = piece * PIECE_SIZE;
            int amountToRead = getThisPieceSize(myFiles.get(fileName).getNumPieces());
            byte[] fileBytes = new byte[amountToRead];

            print("Length: " + size + " Piece size:" + PIECE_SIZE);
            print("Numpieces: " + numPieces + " Offset: " + offset);
            print("amountToRead: " + amountToRead);

            file.seek((long) offset);
            file.read(fileBytes);

            String s = new String(fileBytes);
            print("File bytes: " + s);
            file.close();
            return fileBytes;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private int getThisPieceSize(PeerFile file, int pieceNum) {
        if ((pieceNum+1)*PIECE_SIZE > file.getSize()) {
            return file.getSize() - offset;
        }
        return PIECE_SIZE;
    }

    /* Returns a list of the pieces this peer has of the specified file */
    public ArrayList<Integer> requestPieceInfo(String fileName) {
        // int size = 0;
        // File f = new File(fileName);
        // size = (int) f.length();
        // int numpiece = getNumPieces(size);
        // ArrayList<Integer> temp = new ArrayList<Integer>();
        // for (int i = 0; i < numpiece; i++) {
        //     temp.add((Integer)i);
        // }
        // return temp;
        return myFiles.get(fileName).getCompletePieces();
    }

    private String createRandomID() {
        Random rand = new Random();
        int id = rand.nextInt((ID_MAX - ID_MIN) + 1) + ID_MIN;
        String idString = Integer.toString(id);
        return idString;
    }

    /* Creates and binds an instance of the peer's registry so it can
    be accessed by others and make connections */
    private static void createAndBindSelf() {
         try {
            // Commented out names to perform hardcoded testing
            // String name = createRandomID();
            // String name = String.valueOf(myPortNum);
            Peer peer = new Peer();
            PeerInterface peerStub = (PeerInterface) UnicastRemoteObject.exportObject(peer, 0);
            Registry registry = LocateRegistry.createRegistry(myPortNum);
            registry.bind(myName, peerStub);
            System.out.println("Binding complete");

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static void connectToPeer(String peerName, int peerPort) {
        try {
            // Checks if peer is already bound or not
            PeerInterface temp = peerStubs.get(peerName);
            if (temp != null) { return; }

            System.out.println("\n Peername: " + peerName);
            System.out.println("\n Port: " + peerPort);

            Registry theirReg = LocateRegistry.getRegistry("localhost", peerPort);
            PeerInterface boundPeerStub = (PeerInterface) theirReg.lookup(peerName);
            peerStubs.put(peerName, boundPeerStub);
            System.out.println("Found peer " + peerName);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* Given a file size, returns the number of pieces of the constant
    piece size are in the file */
    private static int getNumPieces(int fileSize) {
        int size = fileSize / PIECE_SIZE;
        if ((double)fileSize / (double)PIECE_SIZE != 0) { size++; }
        return size;
    }

    private static void connectToTracker() {
        if (alreadyConnectedToTracker) { return; }
        try {
            Registry trackerReg = LocateRegistry.getRegistry(TRACKER_IP, TRACKER_PORT);
            leadTrackerStub = (TrackerInterface) trackerReg.lookup(TRACKER_NAME);
            System.out.println("Found tracker!");
            alreadyConnectedToTracker = true;
        } catch (Exception e) {
            System.out.println("Exception occurred connecting to tracker");
            e.printStackTrace();
        }
    }

    /* Method to seed a file on the network */
    private static void seedFile(String fileName) {
        connectToTracker();
        File file = new File(fileName);
        if (!file.exists()) { 
            System.out.println("Error, file does not exist");
            return; 
        }
        int lengthInBytes = (int) file.length();
        int numPieces = getNumPieces(lengthInBytes);
        try {
            leadTrackerStub.seedFile(fileName, myName, myPortNum, lengthInBytes, numPieces);
            System.out.println("Seeding complete");
        } catch (Exception e) {
            System.out.println("Exception in seeding file");
            e.printStackTrace();
        }
        PeerFile newFile = new PeerFile(fileName, numPieces, lengthInBytes, false);
        myFiles.put(fileName, newFile);
    }

    private static void printDoubleList(ArrayList<ArrayList<String>> list) {
        int counter = 0;
        for (ArrayList<String> firstList : list) {
            System.out.print("LIST " + counter + ": ");
            for (String element : firstList) {
                System.out.print(element + ", ");
            }
            System.out.println();
            counter++;
        }
    }

    /*
     * Connects to head tracker and asks for peers that have a certain file.
     * Tracker returns a list of peerInfo strings with "peerName,portNo"
     * I parse this list, and establish connections to peers with whom I have
     *     not already established a connection, then ask them what pieces
     *     they have that I still need.
     * Based on what 
     */
    private static void makeRequest(String fileName) {
        try {
            connectToTracker();

            ArrayList<String> peersWithFile = leadTrackerStub.query(fileName, myName, myPortNum);

            int fileSize = 0; //bytes
            if (peersWithFile != null) {
                System.out.println("Found peer list!");
                fileSize = Integer.parseInt(peersWithFile.get(0));
                peersWithFile.remove(0);

            } else {
                System.out.println("File not found");
                return;
            }

            // returns num totalpieces
            int numFilePieces = getNumPieces(fileSize);

            // Check if already have record of that file in myFiles; if not, add
            if (myFiles.get(fileName) == null) {
                PeerFile temp = new PeerFile(fileName, numFilePieces, fileSize, true);
                myFiles.put(fileName, temp);
            }


            // PieceBreakdown is a 2d list, that holds lists of strings;
            // Each index corresponds to a piece of the file
            // The elements of the inner array lists are peer information of peers with that pieces
            ArrayList<ArrayList<String>> pieceBreakdown = new ArrayList<ArrayList<String>>();
            pieceBreakdown.addAll(getFilePieces(fileName, numFilePieces, peersWithFile));
            System.out.println("Broke down pieces!");

            printDoubleList(pieceBreakdown);

            downloadFile(fileName, pieceBreakdown);

        } catch (Exception e) {
            System.out.println("Exception in placing request");
            e.printStackTrace();
        }
    }

    private static ArrayList<ArrayList<String>> getFilePieces(String fileName, int numFilePieces, ArrayList<String> peersWithFile){
        try {
            ArrayList<ArrayList<String>> pieceBreakdown = new ArrayList<ArrayList<String>>();
            for (int i = 0; i < numFilePieces; i++) {
                ArrayList<String> temp = new ArrayList<String>();
                pieceBreakdown.add(temp);
            }

            // For every peer who the tracker says the file has, request information on how many pieces 
            // they have
            for (String peerInfo : peersWithFile) {
                ArrayList<Integer> peerHasMe = new ArrayList<Integer>();

                int colonIndex = peerInfo.indexOf(":");
                if (colonIndex == -1) { return null; } //error

                String peerName = peerInfo.substring(0, colonIndex);
                String portNo = peerInfo.substring(colonIndex+1, peerInfo.length());
                connectToPeer(peerName, Integer.parseInt(portNo)); //establishes connections

                if (peerName.equals(myName)) { continue; } // don't want to ask myself for file pieces!

                peerHasMe.addAll(peerStubs.get(peerName).requestPieceInfo(fileName));

                for (Integer piece : peerHasMe) {
                    pieceBreakdown.get((int)piece).add(peerName);
                }
            }

            return pieceBreakdown;

        } catch (Exception e) {
            System.out.println("Exception in 'getFilePieces'");
            e.printStackTrace();
        }
        return null;
    }

 
// public static Comparator<ArrayList<String>> StringListComparator 
//                           = new Comparator<ArrayList<String>>() {
 
//     public int compare(ArrayList<String> element1, ArrayList<String> element2) {
    
//         int size1 = element1.getSize(); 
//         int size2 = element2.getSize();

//         //ascending order
//         return size1 - size2;
 
//         //descending order
//         //return size2 - size1;
//     }
 
// };

    private static int[] sortArrayOfIndices(int numPieces) {
        int indexArray = new int[numPieces];
        // TODO: this is just so we have indices in the array, this is not correctly 'sorted'
        for (int i = 0; i < numPieces; i++) {
            indexArray[i] = i;
        }

        return indexArray;
    }

    private static void downloadFile(String fileName, ArrayList<ArrayList<String>> pieceBreakdown) {
        try {

            // get count in piece breakdown
            // get sorted list
            // have array of indices: {2, 3, 1, 5, 4, 0} says 2 has the least, 3 second least
            //              array is called indexArray
            // get min
            // get list
            // if peer list at index = null, re-ask tracker, reinit stuff, resort
            // else:
            // find a peer in list: not being downloaded from
            //      'start downloaindg' that piece from that peer
            //      if finish downloading: 'finish downloading'                
            //

            RandomAccessFile outFile = new RandomAccessFile("OUTPUT"+fileName, "rw");
            int numPieces = myFiles.get(fileName).getNumPieces();
            int indexArray = new int[numPieces];

            // copies into index array sortex indices
            System.arraycopy(sortArrayOfIndices(numPieces), 0, indexArray, 0, numPieces);

            // Iterate through pieces and request them
            int counter = -1;

            while (myFiles.get(fileName).getPiecesNeeded() != null &&
                   myFiles.get(fileName).getDownloadingPieces() != null) {

                counter++;
                if (counter >= numPieces) { counter = 0; } // this ensures we continuously loop through the indices of the index array

                if (!myFiles.get(fileName).needsPiece(indexArray[counter])) {
                    break; // I am already processing this piece
                }

                if (pieceBreakdown.get(indexArray[counter]) == null) {
                    //re-ask tracker, reinit stuff, resort
                }

                for (int k = 0; k < pieceBreakdown.get(indexArray[counter]).size(); k++) {
                    if (myFiles.get(fileName).getCurrentlyDownloadingFrom().contains(pieceBreakdown.get(indexArray[counter]).get(k))) {
                        continue;   // don't want to download from someone we are already downloading from
                    }
                    //otherwise download from k:
                    myFiles.get(fileName).startDownloadingPiece(indexArray[counter], pieceBreakdown.get(indexArray[counter]).get(k));
                    int sizeOfThisPiece = getThisPieceSize(fileName, indexArray[counter]);

                    byte[] answer = new byte[sizeOfThisPiece];
                    System.arraycopy(askForFilePiece(pieceBreakdown.get(indexArray[counter]).get(k), fileName, indexArray[counter]), 0, answer, 0, sizeOfThisPiece);

                    if (answer == null) {
                        // Request unsuccesful; handle here

                        myFiles.get(fileName).noLongerDownloadingPiece(indexArray[counter], pieceBreakdown.get(indexArray[counter]).get(k));
                    } else {
                        myFiles.get(fileName).finishedDownloadingPiece(indexArray[counter], pieceBreakdown.get(indexArray[counter]).get(k));
                        writeBytes(answer, outFile, counter);
                    }
                }
            }

            outFile.close();

        } catch (Exception e) {
            System.out.println("Exception in 'downloadFile'");
            e.printStackTrace();
        }
    }

    private static void writeBytes(byte[] data, RandomAccessFile fileName, int piece) {
        try {
            System.out.println("Wryting bytes");
            String s = new String(data);
            System.out.println(s);
            // first get offset with piece:
            int offset = piece * PIECE_SIZE;
            fileName.seek(offset);
            fileName.write(data);
        } catch (Exception e) {
            System.out.println("Exception in writing file");
            e.printStackTrace();
        }
    }

    /*
     * Asks a peer for a specific piece of the file. We have already determined
     * that this peer has the file, but we will use a timeout and check on the 
     * peer end to make sure this information is correct.
     */
    private static byte[] askForFilePiece(String peerName, String fileName, int piece) {
        print("I, " + myName + " am requesting " + fileName + " frm " + peerName + "\n");
        try {
            byte[] answer = peerStubs.get(peerName).requestFile(fileName, piece);
            return answer;
        } catch (Exception e) {
            System.out.println("Exception");
            e.printStackTrace();
        }
        return null;
    }

    /* Infinite loop that will query the user to input commands. To
    exit, type 'exit'. */
    private static void parseInput() {
        Scanner keyboard = new Scanner(System.in);
        String fileName = "YOLO.txt";
        
        while (true) {
            System.out.println("Enter a command");
            String command = keyboard.nextLine();
            String parsedRequest[] = command.split(" ");
            String secondPartOfRequest = "";

            // Add a peer
            if (parsedRequest[0].equals("connect")) {
                String name = parsedRequest[1];
                int port = Integer.parseInt(parsedRequest[2]);
                connectToPeer(name, port);
            }

            // Seed a file
            else if (parsedRequest[0].equals("seed")) {
                String name = parsedRequest[1];
                seedFile(name);
            }

            // Request a file frm peer directly
            else if (parsedRequest[0].equals("request")) {
                fileName = parsedRequest[1];
                makeRequest(fileName);
            }

            else if (parsedRequest[0].equals("exit")) {
                System.out.println("Goodbye!");
                break;
            }
        }
    }


    /* to run: 
    java Peer localhost <your port> <your name> 
    to connect to more peers: first start them on their ports, then enter command:
    connect <their name> <their port>
    to request a file directly, enter command:
    request <their name>
    to seed a file, enter command: 
    seed <file name>

    */
	public static void main(String[] argv) {
        String host = (argv.length < 1) ? "localhost" : argv[0];
        myPortNum = (argv.length < 2) ? 5000 : Integer.parseInt(argv[1]);
        myName = (argv.length < 3) ? "Howard" : (argv[2]);

        System.out.println("My portnum: " + myPortNum + "\n My name: " + myName + "\n");

        createAndBindSelf();
        parseInput();
    }


}







