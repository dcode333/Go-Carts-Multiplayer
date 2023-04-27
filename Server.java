
import java.net.*;
import java.io.*;

public class Server {
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(4444);
        } catch (IOException e) {
            System.err.println("Could not listen on port: 4444.");
            System.exit(1);
        }

        boolean[] latestArray = new boolean[10]; // default value is all false

        while (true) {
            System.out.println("Waiting for connection...");
            Socket clientSocket = null;
            try {
                clientSocket = serverSocket.accept();
                System.out.println("Connection established with " + clientSocket.getInetAddress().getHostName());
            } catch (IOException e) {
                System.err.println("Accept failed.");
                System.exit(1);
            }

            ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());

            while (true) {
                try {
                    String request = (String) in.readObject();
                    if (request.equals("update")) {
                        boolean[] inputArray = (boolean[]) in.readObject();
                        System.out.println("Received array from client:");
                        for (boolean b : inputArray) {
                            System.out.print(b + " ");
                        }
                        System.out.println();
                        latestArray = inputArray; // update the latest array with the received array
                        out.writeObject("Array updated successfully.");
                    } else if (request.equals("getLatestArray")) {
                        out.writeObject(latestArray);
                    } else {
                        out.writeObject("Invalid request.");
                    }
                    out.flush();
                } catch (EOFException e) {
                    System.out.println("Client disconnected.");
                    break;
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }

            out.close();
            in.close();
            clientSocket.close();
        }
    }
}