import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.*;

import java.util.Scanner;
import java.util.Random;
import java.util.*;
import java.util.ArrayList;

import java.net.ServerSocket;
import java.net.Socket;

import java.rmi.*;
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;

public class Peer implements PeerInterface {

    private static PeerInterface boundPeerStub;
    private static TrackerInterface leadTrackerStub;
    private final int ID_MAX = 999999999;
    private final int ID_MIN = 100000000;
    private static Map<String, PeerInterface> peerStubs;

	public Peer() {
        super();
        peerStubs = new HashMap<String, PeerInterface>();
	}

    public byte[] requestFile(String fileName) {
        try {
            int bytesRead;
            FileInputStream fileInput = new FileInputStream(fileName);
            byte[] fileBytes = new byte[(int)(new File(fileName).length())];
            bytesRead = fileInput.read(fileBytes);
            assert(bytesRead == fileBytes.length);
            return fileBytes;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private String createRandomID() {
        Random rand = new Random();
        int id = rand.nextInt((ID_MAX - ID_MIN) + 1) + ID_MIN;
        String idString = Integer.toString(id);
        return idString;
    }

    private static void createAndBindSelf(int myPortNum, String myName) {
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
            if (peerStubs.contains(peerName)) { return; }

            Registry theirReg = LocateRegistry.getRegistry("localhost", peerPort);
            boundPeerStub = (PeerInterface) theirReg.lookup(peerName);
            peerStubs.put(peerName, boundPeerStub);
            System.out.println("Found peer " + peerName);

        } catch (Exception e) {
            e.printStackTrace();
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
    private static void makeRequest(String trackerID, String fileName) {
        try {
            // connect to tracker
            List<String> peersWithFile = new List<String>();
            peersWithFile.addall(leadTrackerStub.getPeers(fileName));

            int filesize = 0; //bytes
            if (peersWithFile) {
                filesize = String.parseInt(peersWithFile.get(0));
                peersWithFile.remove(0);
            } else {
                System.out.println("File not found");
                return;
            }

            // returns num totalpieces
            int numFilePieces = divideFile(fileName);

            List<List<String>> pieceBreakdown = new List<List<String>>();
            for (int i = 0; i < numFilePieces; i++) {
                List<String> temp = new List<String>();
                temp.add("PLACEHOLDER");
                pieceBreakdown.add(temp);
            }

            for (String peerInfo : peersWithFile) {
                int commaIndex = peerInfo.indexOf(",");
                if (commaIndex == -1) { return; } //error

                String peerName = peerInfo.substring(commaIndex);
                String portNo = peerInfo.substring(commaIndex+1, substring.length());
                connectToPeer(peerName, portNo); //establishes connections

                List<int> peerHasMe = new List<int>();
                peerHasMe.addall(peerStubs.get(peerName).requestPieceInfo(fileName));

                for (int piece : peerHasMe) {
                    pieceBreakdown.get(piece).add(peerName);
                }
            }

            int counter = -1;
            for (List<String> peersWhoHavePiece : pieceBreakdown) {
                counter++;

                // always should have the "placeholder" in position 0, so start with 1
                if (peersWhoHavePiece.elementAtOrDefault(1) == null) {
                    System.out.println("NO PERSON HAS PIECE FOR FILE "+fileName);
                    continue;
                }

                // temp testing just go with the first peer in the list:
                byte[] answer = askForFilePiece(peersWhoHavePiece.get(1), fileName, counter);
                writeBytes(answer, counter, fileName);
            }

        } catch (Exception e) {
            System.out.println("Exception");
            e.printStackTrace();
        }
        
    }

    private static void writeBytes(byte[] data, int piece, String filename) {
        //RANDOM ACCESS FILE STUFF


            // ALSO NEED TO FILL IN SORRY - MEGZ



            // byte[] result;
            // System.out.println("Looking for file " + fileName);
            // result = peerStubs.get(peerName).requestFile(fileName);

            // File outFile = new File("result.txt");
            // FileOutputStream fileOutput = new FileOutputStream(outFile, true);
            // fileOutput.write(result);
            // fileOutput.close();
            // System.out.println("Got file");
    }


    /*
     * Asks a peer for a specific piece of the file. We have already determined
     * that this peer has the file, but we will use a timeout and check on the 
     * peer end to make sure this information is correct.
     */
    private static void askForFilePiece(String peerName, String fileName, int piece) {
        try {

            // I NEED TO FILL IN SORRY -MEGZ


            // byte[] result;
            // System.out.println("Looking for file " + fileName);
            // result = peerStubs.get(peerName).requestFile(fileName);

            // File outFile = new File("result.txt");
            // FileOutputStream fileOutput = new FileOutputStream(outFile, true);
            // fileOutput.write(result);
            // fileOutput.close();
            // System.out.println("Got file");

        } catch (Exception e) {
            System.out.println("Exception");
            e.printStackTrace();
        }
        
    }

    /* to run: 
    java Peer localhost <your name> <your port>
    to connect to more peers: first start them on their ports, then enter command:
    connect <their name> <their port>
    to request file, enter command:
    request <their name>
    */
	public static void main(String[] argv) {
        String host = (argv.length < 1) ? "localhost" : argv[0];
        int myPortNum = (argv.length < 2) ? 5000 : Integer.parseInt(argv[1]);
        String myName = (argv.length < 3) ? "Howard" : (argv[2]);

        String fileName = "YOLO.txt";

        System.out.println("My portnum: " + myPortNum + "\n My name: " + myName + "\n");

        createAndBindSelf(myPortNum, myName);

        Scanner keyboard = new Scanner(System.in);
        /* Infinite loop that will query the user to input commands. To
        exit, type 'exit'. */
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

            // Request a file
            else if (parsedRequest[0].equals("request")) {
                String peerName = parsedRequest[1];
                makeRequest(peerName, fileName);
            }

            else if (parsedRequest[0].equals("exit")) {
                System.out.println("Goodbye!");
                break;
            }
        }
        
    }

	


}







