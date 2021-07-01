package com.laizhihao.Wechat.Api;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;

/**
 * 类名：ServerFrame
 * 描述：The Server UI. 服务器窗口
 * Author Zahory Lai
 */
public class ServerFrame extends JFrame implements ActionListener {
    protected JTabbedPane tpServer;

    // 服务器信息面板
    protected JPanel pnlServer;
    protected JPanel pnlServerInfo;

    // 服务器详细信息面板
    protected JLabel lblNumber;//当前在线人数
    protected JLabel lblServerName;//服务器名称
    protected JLabel lblIP;//服务器IP
    protected JLabel lblPort;//服务器端口
    protected JLabel lblLog;//服务器日志

    protected JTextField txtNumber;
    public JTextField txtServerName;
    public JTextField txtIP;
    public JTextField txtPort;
    protected JButton btnStop;
    protected JButton btnSaveLog;
    public JTextArea taLog;
    public JScrollPane taLogpane;

    // 用户信息面板
    protected JPanel pnlUser;
    protected JLabel lblUser;
    protected JList lstUser;
    protected JScrollPane spUser;

    // 关于本软件
    protected JPanel pnlAbout;
    protected JLabel lblVersionNo;
    protected JLabel lblAbout;
    protected JLabel lblSchoolAbout;

    public ServerFrame() {
        super(Constants.APP_SERVER_NAME);
        setSize(500, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        Dimension scr = Toolkit.getDefaultToolkit().getScreenSize();// 在屏幕居中显示
        Dimension fra = this.getSize();
        if (fra.width > scr.width) {
            fra.width = scr.width;
        }
        if (fra.height > scr.height) {
            fra.height = scr.height;
        }
        this.setLocation((scr.width - fra.width) / 2,
                (scr.height - fra.height) / 2);

        // 服务器信息
        pnlServerInfo = new JPanel(new GridLayout(14, 1));
        pnlServerInfo.setBackground(new Color(52, 130, 203));
        pnlServerInfo.setFont(new Font(Constants.FONT_SONG, 0, 12));
        pnlServerInfo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(""),
                BorderFactory.createEmptyBorder(1, 1, 1, 1)));

        lblLog = new JLabel(Constants.SERVER_LOG);
        lblLog.setForeground(Color.WHITE);
        lblLog.setFont(new Font(Constants.FONT_SONG, 0, 15));
        lblLog.setBounds(110, 5, 50, 30);

        lblNumber = new JLabel(Constants.ONLINE_USERS_NUM);
        lblNumber.setForeground(Color.WHITE);
        lblNumber.setFont(new Font(Constants.FONT_SONG, 0, 12));
        txtNumber = new JTextField(Constants.ZERO_NUM, 10);
        txtNumber.setBackground(Color.decode("#d6f4f2"));
        txtNumber.setFont(new Font(Constants.FONT_SONG, 0, 12));
        txtNumber.setEditable(false);

        lblServerName = new JLabel(Constants.SERVER_NAME);
        lblServerName.setForeground(Color.WHITE);
        lblServerName.setFont(new Font(Constants.FONT_SONG, 0, 12));
        txtServerName = new JTextField(10);
        txtServerName.setBackground(Color.decode("#d6f4f2"));
        txtServerName.setFont(new Font(Constants.FONT_SONG, 0, 12));
        txtServerName.setEditable(false);

        lblIP = new JLabel(Constants.SERVER_IP);
        lblIP.setForeground(Color.WHITE);
        lblIP.setFont(new Font(Constants.FONT_SONG, 0, 12));
        txtIP = new JTextField(10);
        txtIP.setBackground(Color.decode("#d6f4f2"));
        txtIP.setFont(new Font(Constants.FONT_SONG, 0, 12));
        txtIP.setEditable(false);

        lblPort = new JLabel(Constants.SERVER_PORT_DESC);
        lblPort.setForeground(Color.WHITE);
        lblPort.setFont(new Font(Constants.FONT_SONG, 0, 12));
        txtPort = new JTextField(String.valueOf(Constants.SERVER_PORT), 10);
        txtPort.setBackground(Color.decode("#d6f4f2"));
        txtPort.setFont(new Font(Constants.FONT_SONG, 0, 12));
        txtPort.setEditable(false);

        btnStop = new JButton(Constants.CLOSE_SERVER);
        btnStop.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                closeServer();
            }
        });
        btnStop.setBackground(Color.WHITE);
        btnStop.setFont(new Font(Constants.FONT_SONG, 0, 12));

        btnSaveLog = new JButton(Constants.SAVE_LOG);
        btnSaveLog.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                saveLog();
            }
        });
        btnSaveLog.setBackground(Color.WHITE);
        btnSaveLog.setFont(new Font(Constants.FONT_SONG, 0, 12));

        pnlServerInfo.setBounds(5, 5, 120, 300);
        pnlServerInfo.add(lblLog);
        pnlServerInfo.add(lblNumber);
        pnlServerInfo.add(txtNumber);
        pnlServerInfo.add(lblServerName);
        pnlServerInfo.add(txtServerName);
        pnlServerInfo.add(lblIP);
        pnlServerInfo.add(txtIP);
        pnlServerInfo.add(lblPort);
        pnlServerInfo.add(txtPort);
        pnlServerInfo.add(btnStop);
        pnlServerInfo.add(btnSaveLog);

        // 服务器面板
        pnlServer = new JPanel();
        pnlServer.setLayout(new FlowLayout(FlowLayout.LEFT));
        pnlServer.setBackground(Color.WHITE);

        taLog = new JTextArea(20, 50);
        taLog.setFont(new Font(Constants.FONT_SONG, 0, 12));
        taLog.setBounds(110, 35, 300, 200);
        taLogpane = new JScrollPane(taLog);

        btnStop.setBounds(200, 410, 120, 30);
        btnSaveLog.setBounds(320, 410, 120, 30);

        //
        pnlServer.add(pnlServerInfo);
        pnlServer.add(taLogpane,new FlowLayout(FlowLayout.CENTER));

        // 用户面板
        pnlUser = new JPanel();
        pnlUser.setLayout(null);
        pnlUser.setBackground(Color.WHITE);
        pnlUser.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(""),
                BorderFactory.createEmptyBorder(1, 1, 1, 1)));

        lblUser = new JLabel(Constants.ONLINE_USERS_LIST);
        lblUser.setFont(new Font(Constants.FONT_SONG, 0, 12));
        lblUser.setForeground(Color.BLACK);

        lstUser = new JList();
        lstUser.setFont(new Font(Constants.FONT_SONG, 0, 12));
        lstUser.setVisibleRowCount(17);
        lstUser.setFixedCellWidth(180);
        lstUser.setFixedCellHeight(18);

        spUser = new JScrollPane();
        spUser.setBackground(Color.cyan);
        spUser.setFont(new Font(Constants.FONT_SONG, 0, 12));
        spUser.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        spUser.getViewport().setView(lstUser);

        pnlUser.setBounds(50, 5, 300, 400);
        lblUser.setBounds(50, 10, 100, 30);
        spUser.setBounds(50, 35, 200, 250);

        pnlUser.add(lblUser);
        pnlUser.add(spUser);

        // 软件信息
        pnlAbout = new JPanel();
        pnlAbout.setLayout(null);
        pnlAbout.setBackground(Color.WHITE);
        pnlAbout.setFont(new Font(Constants.FONT_SONG, 0, 14));

        lblVersionNo = new JLabel();
        lblVersionNo.setFont(new Font(Constants.FONT_SONG, 0, 14));
        lblVersionNo.setText(Constants.VERSION_INFO);
        lblVersionNo.setForeground(Color.BLACK);

        lblAbout = new JLabel();
        lblAbout.setFont(new Font(Constants.FONT_SONG, 0, 14));
        lblAbout.setText(Constants.APP_ABOUT_INFO);
        lblAbout.setForeground(Color.BLACK);

        lblSchoolAbout = new JLabel();
        lblSchoolAbout.setFont(new Font(Constants.FONT_SONG, 0, 14));
        lblSchoolAbout.setText(Constants.SCHOOL_ABOUT_INFO);
        lblSchoolAbout.setForeground(Color.BLACK);

        lblVersionNo.setBounds(110, 5, 100, 30);
        lblAbout.setBounds(110, 35, 400, 50);
        lblSchoolAbout.setBounds(110, 65, 500, 70);

        pnlAbout.add(lblVersionNo);
        pnlAbout.add(lblAbout);
        pnlAbout.add(lblSchoolAbout);

        // 主标签面板
        tpServer = new JTabbedPane(JTabbedPane.TOP);
        tpServer.setBackground(Color.WHITE);
        tpServer.setFont(new Font(Constants.FONT_SONG, 0, 14));

        tpServer.add(Constants.SERVER_ADMIN, pnlServer);
        tpServer.add(Constants.ONLINE_USERS, pnlUser);
        tpServer.add(Constants.ABOUT_APP, pnlAbout);

        this.getContentPane().add(tpServer);
        setVisible(true);
    }

    protected void closeServer() {
        this.dispose();
    }

    protected void saveLog() {
        try {
            FileOutputStream fileoutput = new FileOutputStream("log.txt", true);
            String temp = taLog.getText();
            fileoutput.write(temp.getBytes());
            fileoutput.close();
            JOptionPane.showMessageDialog(null, Constants.SAVE_LOG_FILE);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void actionPerformed(ActionEvent evt) {
    }

    /**
     * 服务器窗口
     */
    public static void main(String[] args) {
        new ServerFrame();
    }
}
