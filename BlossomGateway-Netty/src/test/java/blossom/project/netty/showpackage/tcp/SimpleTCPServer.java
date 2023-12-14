package blossom.project.netty.showpackage.tcp;

import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class SimpleTCPServer {
    public static void main(String[] args) throws Exception {
        ServerSocket serverSocket = new ServerSocket(8080);
        System.out.println("TCP Server started");

        while (true) {
            Socket clientSocket = serverSocket.accept();
            InputStream inputStream = clientSocket.getInputStream();

            byte[] dataBuffer = new byte[1024];
            while (true) {
                int readBytes = inputStream.read(dataBuffer);
                if (readBytes <= 0) {
                    break;
                }
                System.out.println("Received: " + new String(dataBuffer, 0, readBytes));
            }
            clientSocket.close();
        }
    }
}
