package blossom.project.netty.showpackage.tcp;

import java.io.OutputStream;
import java.net.Socket;

public class SimpleTCPClient {
    public static void main(String[] args) throws Exception {
        Socket socket = new Socket("localhost", 8080);
        OutputStream outputStream = socket.getOutputStream();

        // 发送多个消息
        for (int i = 0; i < 10; i++) {
            String message = "Message " + i;
            outputStream.write(message.getBytes());
        }

        socket.close();
    }
}
