package org.xkp.lesson.socket;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;

public class SocketService {

    public static int ChangeLight(String leval) {
        ArrayList<Socket> sockets = ConnSocket.getSockets();

        for (Socket socket : sockets) {
            if (socket != null && !socket.isClosed()) {
                try {
                    OutputStreamWriter writer = new OutputStreamWriter(socket.getOutputStream());
                    writer.write(leval);
                    writer.flush();
                    //此处writer不能关闭，原因:如果关闭writer，则对应的socket也会被关闭

                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    sockets.remove(socket);
                    e.printStackTrace();
                    return 0;
                }

            } else {
                sockets.remove(socket);
                return 0;
            }
        }
        return 1;
    }

    /**
     * 通过Socket发送开灯或者关灯命令
     *
     * @param flag
     * @return
     */
    public static int openOrCloseLight(boolean flag) {
        ArrayList<Socket> sockets = ConnSocket.getSockets();

        for (int i = 0; i < 100; i++) {
            for (Socket socket : sockets) {

                if (socket != null && !socket.isClosed()) {
                    try {
                        OutputStreamWriter writer = new OutputStreamWriter(socket.getOutputStream());

                        //开灯
                        writer.write("10");
                        writer.flush();
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        //关灯
                        writer.write("11");

                        writer.flush();
                        //此处writer不能关闭，原因:如果关闭writer，则对应的socket也会被关闭
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } catch (IOException e) {
                        // TODO Auto-generated catch block

                        sockets.remove(socket);
                        e.printStackTrace();
                        return 0;
                    }
                } else {
                    sockets.remove(socket);
                    return 0;
                }
            }

        }
        for (Socket socket : sockets) {
            if (socket != null && !socket.isClosed()) {
                try {
                    OutputStreamWriter writer = new OutputStreamWriter(socket.getOutputStream());
                    if (flag) {
                        //开灯
                        writer.write("10");
                    } else {
                        //关灯
                        writer.write("11");
                    }
                    writer.flush();
                    //此处writer不能关闭，原因:如果关闭writer，则对应的socket也会被关闭

                } catch (IOException e) {
                    // TODO Auto-generated catch block

                    sockets.remove(socket);
                    e.printStackTrace();
                    return 0;
                }
            } else {
                sockets.remove(socket);
                return 0;
            }
        }
        return 1;
    }
}
