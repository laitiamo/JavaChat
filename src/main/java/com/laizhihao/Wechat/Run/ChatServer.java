package com.laizhihao.Wechat.Run;

import com.laizhihao.Wechat.Api.Constants;
import com.laizhihao.Wechat.Api.ServerFrame;
import com.laizhihao.Wechat.Api.ServerProcess;

import java.io.IOException;
import java.net.BindException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;

/**
 * ������ChatServer
 * ����������һ�����������ҷ��������������������棬����ͻ��˽�����ϵ
 * Author Zahory Lai
 */
public class ChatServer extends Thread {
    ServerFrame serverFrame = null;
    ServerSocket serverSocket = null; // �������������׽���
    private static String sqlurl = "jdbc:mysql://localhost:3306/wechat";
    private static String sqluser = "root";//mysql��¼��
    private static String sqlpass = "lai18015098997";//mysql��¼����
    public boolean bServerIsRunning = false;

    public ChatServer() {
        try {
            serverSocket = new ServerSocket(Constants.SERVER_PORT); // ��������
            bServerIsRunning = true;

            serverFrame = new ServerFrame();
            getServerIP(); // �õ�����ʾ��������IP
            System.out.println("Server Port is:" + Constants.SERVER_PORT);
            serverFrame.taLog.setText("�������Ѿ�����...");
            while (true) {
                Socket socket = serverSocket.accept(); // �����ͻ��˵��������󣬲����ؿͻ���socket
                new ServerProcess(socket, serverFrame); // ����һ�����߳���������ÿͻ���ͨѶ
            }
        } catch (BindException e) {
            System.out.println("�˿�ʹ����....");
            System.out.println("��ص���س����������з�������");
            System.exit(0);
        } catch (IOException e) {
            System.out.println("[ERROR] Cound not start server." + e);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        this.start(); // �����߳�
    }

    /**
     * ��ȡ����������������IP��ַ
     */
    public void getServerIP() {
        try {
            InetAddress serverAddress = InetAddress.getLocalHost();
            byte[] ipAddress = serverAddress.getAddress();

            serverFrame.txtServerName.setText(serverAddress.getHostName());
            serverFrame.txtIP.setText(serverAddress.getHostAddress());
            serverFrame.txtPort.setText(String.valueOf(Constants.SERVER_PORT));

            System.out.println("Server IP is:" + (ipAddress[0] & 0xff) + "."
                    + (ipAddress[1] & 0xff) + "." + (ipAddress[2] & 0xff) + "."
                    + (ipAddress[3] & 0xff));
        } catch (Exception e) {
            System.out.println("###Cound not get Server IP." + e);
        }
    }

    /**
     * main������ʵ�����������˳���
     *
     * @param args
     */
    public static void main(String args[]) throws IOException, ClassNotFoundException {
        new ChatServer();
    }
}
