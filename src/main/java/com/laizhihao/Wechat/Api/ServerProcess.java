package com.laizhihao.Wechat.Api;

import java.io.*;
import java.net.*;
import java.sql.*;
import java.util.*;
import java.util.Date;

/**
 * ������ServerProcess
 * ���������յ��ͻ���socket��������Ϣ����н���������ת����
 * Author Zahory Lai
 */
public class ServerProcess extends Thread {
    private Socket socket = null;// ����ͻ����׽���
    private BufferedReader in;// ����������
    private PrintWriter out;// ���������
    Connection con = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/wechat?useSSL=false&characterEncoding=utf-8", "root", "lai18015098997");
    private static Vector onlineUser = new Vector(10, 5);
    private static Vector socketUser = new Vector(10, 5);
    private String strReceive, strKey;
    private StringTokenizer st;
    private ServerFrame sFrame = null;

    public ServerProcess(Socket client, ServerFrame frame) throws IOException, SQLException {
        socket = client;
        sFrame = frame;
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);// �ͻ������
        this.start();
    }

    public void run() {
        try {
            while (true) {
                strReceive = in.readLine();
                st = new StringTokenizer(strReceive, "|");
                strKey = st.nextToken();
                if (strKey.equals("login")) {
                    login();
                } else if (strKey.equals("talk")) {
                    talk();
                } else if (strKey.equals("init")) {
                    freshClientsOnline();
                } else if (strKey.equals("reg")) {
                    register();
                }
            }
        } catch (IOException | SQLException | ClassNotFoundException e) {
            String leaveUser = closeSocket();
            Date t = new Date();
            log(Constants.USER + leaveUser + Constants.HAD_EXIT + Constants.EXIT_TIME + t.toLocaleString());
            try {
                freshClientsOnline();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            System.out.println("[SYSTEM] " + leaveUser + " leave chatroom!");//�رո��û��׽��֡�
            sendAll("talk|>>>" + leaveUser + Constants.LEAVE_ROOM);
        }
    }

    private boolean isUserLogin(String name, String password) {
        try {
            String sql = "select username,password from user_info where username=? and password=?";
            PreparedStatement ptmt = con.prepareStatement(sql);
            ptmt.setString(1, name);
            ptmt.setString(2, password);
            ResultSet rs = ptmt.executeQuery();
            //�ӵ�¼�û��������˺�����������ѯ�����ݿ�����Ƿ������ͬ���˺�����
            if (rs.next()) {
                return true;
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }

    private void register() throws IOException, SQLException, ClassNotFoundException {
        String name = st.nextToken();
        String password = st.nextToken().trim();
        Date t = new Date();
        try {
            String sql = "select username,password from user_info where username=? and password=?";//�����Ƿ��Ѿ�ע������ͬ�û�
            PreparedStatement ptmt = con.prepareStatement(sql);
            ptmt.setString(1, name);
            ptmt.setString(2, password);
            ResultSet rs = ptmt.executeQuery();
            if (rs.next()) {
                System.out.println("[ERROR] " + name + " Register fail!");
                out.println("warning|���û��Ѵ��ڣ������!");
            } else {
                String sql2 = "insert into user_info (username,password) values(?,?)";
                PreparedStatement ptmt2 = con.prepareStatement(sql2);
                ptmt2.setString(1, name);
                ptmt2.setString(2, password);
                ptmt2.execute();
                log(Constants.USER + name + "ע��ɹ�, " + "ע��ʱ��:" + t.toLocaleString());
                userLoginSuccess(name);
            }
        }catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }


    private void login() throws IOException {
        String name = st.nextToken();
        String password = st.nextToken().trim();
        boolean succeed = false;
        Date t = new Date();
        log(Constants.USER + name + "���ڵ�½..." + "\n" + "���� :" + password + "\n" + "�˿� "
                + socket + t.toLocaleString());
        System.out.println("[USER LOGIN] " + name + ":" + password + ":"
                + socket);

        for (int i = 0; i < onlineUser.size(); i++) {
            if (onlineUser.elementAt(i).equals(name)) {
                System.out.println("[ERROR] " + name + " is logined!");
                out.println("warning|" + name + "�Ѿ���½������");
            }
        }
        if (isUserLogin(name, password)) {
            userLoginSuccess(name);
            succeed = true;
        }
        if (!succeed) {
            out.println("warning|" + name + "��½ʧ�ܣ�������������!");
            log(Constants.USER + name + "��½ʧ�ܣ�" + t.toLocaleString());
            System.out.println("[SYSTEM] " + name + " login fail!");
        }
    }

    private void userLoginSuccess(String name) throws IOException {
        Date t = new Date();
        out.println("login|succeed");
        sendAll("online|" + name);

        onlineUser.addElement(name);
        socketUser.addElement(socket);

        log(Constants.USER + name + "��¼�ɹ���" + "��¼ʱ��:" + t.toLocaleString());

        freshClientsOnline();
        sendAll("talk|>>>��ӭ " + name + " ����������һ��̸!");
        System.out.println("[SYSTEM] " + name + " login succeed!");
    }

    private void talk() throws IOException {
        String strTalkInfo = st.nextToken();
        String strSender = st.nextToken();
        String strReceiver = st.nextToken();
        System.out.println("[TALK_" + strReceiver + "] " + strTalkInfo);
        Socket socketSend;
        PrintWriter outSend;
        Date t = new Date();

        GregorianCalendar calendar = new GregorianCalendar();
        String strTime = "(" + calendar.get(Calendar.HOUR) + ":"
                + calendar.get(Calendar.MINUTE) + ":"
                + calendar.get(Calendar.SECOND) + ")";
        strTalkInfo += strTime;

        log(Constants.USER + strSender + "�� " + strReceiver + "˵:" + strTalkInfo);

        if (strReceiver.equals("All")) {
            sendAll("talk|" + strSender + " ��������˵��" + strTalkInfo);
        } else {
            if (strSender.equals(strReceiver)) {
                out.println("talk|>>>������������Ŷ!");
            } else {
                for (int i = 0; i < onlineUser.size(); i++) {
                    if (strReceiver.equals(onlineUser.elementAt(i))) {
                        socketSend = (Socket) socketUser.elementAt(i);
                        outSend = new PrintWriter(new BufferedWriter(
                                new OutputStreamWriter(
                                        socketSend.getOutputStream())), true);
                        outSend.println("talk|" + strSender + " ����˵��"
                                + strTalkInfo);
                    } else if (strSender.equals(onlineUser.elementAt(i))) {
                        socketSend = (Socket) socketUser.elementAt(i);
                        outSend = new PrintWriter(new BufferedWriter(
                                new OutputStreamWriter(
                                        socketSend.getOutputStream())), true);
                        outSend.println("talk|��� " + strReceiver + "˵��"
                                + strTalkInfo);
                    }
                }
            }
        }
    }

    private void freshClientsOnline() throws IOException {
        String strOnline = "online";
        String[] userList = new String[20];
        String useName = null;

        for (int i = 0; i < onlineUser.size(); i++) {
            strOnline += "|" + onlineUser.elementAt(i);
            useName = " " + onlineUser.elementAt(i);
            userList[i] = useName;
        }

        sFrame.txtNumber.setText("" + onlineUser.size());
        sFrame.lstUser.setListData(userList);
        System.out.println(strOnline);
        out.println(strOnline);
    }

    private void sendAll(String strSend) {
        Socket socketSend;
        PrintWriter outSend;
        try {
            for (int i = 0; i < socketUser.size(); i++) {
                socketSend = (Socket) socketUser.elementAt(i);
                outSend = new PrintWriter(new BufferedWriter(
                        new OutputStreamWriter(socketSend.getOutputStream())),
                        true);
                outSend.println(strSend);
            }
        } catch (IOException e) {
            System.out.println("[ERROR] send all fail!");
        }
    }

    public void log(String log) {
        String newlog = sFrame.taLog.getText() + "\n" + log;
        sFrame.taLog.setText(newlog);
    }

    private String closeSocket() {
        String strUser = "";
        for (int i = 0; i < socketUser.size(); i++) {
            if (socket.equals((Socket) socketUser.elementAt(i))) {
                strUser = onlineUser.elementAt(i).toString();
                socketUser.removeElementAt(i);
                onlineUser.removeElementAt(i);
                try {
                    freshClientsOnline();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                sendAll("remove|" + strUser);
            }
        }
        try {
            in.close();
            out.close();
            socket.close();
        } catch (IOException e) {
            System.out.println("[ERROR] " + e);
        }

        return strUser;
    }
}