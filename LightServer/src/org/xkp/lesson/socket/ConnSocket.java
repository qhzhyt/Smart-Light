package org.xkp.lesson.socket;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class ConnSocket extends Thread {
    private static Socket socket;
    private ServerSocket serverSocket;
    private static ArrayList<Socket> sockets;


    public ConnSocket() {
        sockets = new ArrayList<>();
        try {
            //监听端口
            serverSocket = new ServerSocket(8888);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void closeAllSocket() {

        for (Socket socket : sockets) {
            try {
                if (socket != null && !socket.isClosed()) {
                    socket.close();
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }


    }

    @Override
    public void run() {
        // TODO Auto-generated method stub
        super.run();
        while (true) {
            try {
                //等待客户端连接
                socket = serverSocket.accept();
                System.out.println(socket.getInetAddress() + ":" + socket.getPort() + "连接成功");
                sockets.add(socket);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public static Socket getSocket() {
        return socket;
    }

    public static ArrayList<Socket> getSockets() {

        return sockets;
    }

}
