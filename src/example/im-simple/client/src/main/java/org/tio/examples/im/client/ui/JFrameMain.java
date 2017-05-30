/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tio.examples.im.client.ui;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

import javax.swing.DefaultListModel;
import javax.swing.JList;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.client.ClientChannelContext;
import org.tio.client.ClientGroupContext;
import org.tio.client.ClientGroupStat;
import org.tio.core.Aio;
import org.tio.core.ChannelContext;
import org.tio.core.Node;
import org.tio.core.ObjWithLock;
import org.tio.core.stat.GroupStat;
import org.tio.core.utils.SystemTimer;
import org.tio.examples.im.client.ImClientStarter;
import org.tio.examples.im.client.ui.component.ImListCellRenderer;
import org.tio.examples.im.client.ui.component.MyTextArea;
import org.tio.examples.im.common.ImPacket;
import org.tio.examples.im.common.ImSessionContext;
import org.tio.examples.im.common.packets.ChatReqBody;
import org.tio.examples.im.common.packets.ChatType;
import org.tio.examples.im.common.packets.Command;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

/**
 *
 * @author Administrator
 */
public class JFrameMain extends javax.swing.JFrame {

	/**
	 * @含义: 
	 * @类型: long
	 */
	private static final long serialVersionUID = -7052228425670244367L;
	private static Logger log = LoggerFactory.getLogger(JFrameMain.class);
	private static JFrameMain instance = null;

	private static ImClientStarter imClientStarter = null;

	//这两个用来统计性能数据的
	public static final AtomicLong receivedPackets = new AtomicLong();
	public static final AtomicLong sentPackets = new AtomicLong();
	public static boolean isNeedUpdateList = false;
	public static final ReentrantReadWriteLock updatingListLock = new ReentrantReadWriteLock();

	public static boolean isNeedUpdateConnectionCount = false;
	public static boolean isNeedUpdateReceivedCount = false;
	public static boolean isNeedUpdateSentCount = false;

	public static final String REMOVE_REMARK = "管理员删除";

	public static int MAX_LIST_COUNT = 20; //列表最多显示多少条数据，多余的不显示

	/** 
	 * 设置窗口图标 
	 */
	protected void setWindowIcon() {
		//javax.swing.ImageIcon imageIcon = new javax.swing.ImageIcon(getClass().getResource("/img/icon.png"));
		//		this.setIconImage(imageIcon.getImage());
	}

	/**
	 * @return
	 */
	public static JFrameMain getInstance() {
		if (instance == null) {
			synchronized (log) {
				if (instance == null) {
					instance = new JFrameMain();
					instance.setWindowIcon();
				}
			}
		}
		return instance;
	}

	public static void updateConnectionCount() {
		if (isNeedUpdateConnectionCount) {
			isNeedUpdateConnectionCount = false;

			NumberFormat numberFormat = NumberFormat.getInstance();

			ClientGroupContext<ImSessionContext, ImPacket, Object> clientGroupContext = imClientStarter.getClientGroupContext();
			int connectionCount = clientGroupContext.connections.size();
			instance.connectionCountLabel.setText("总连接" + numberFormat.format(connectionCount));

			int connectedCount = clientGroupContext.connecteds.size();
			instance.connectedCountLabel.setText("正常链路" + numberFormat.format(connectedCount));

			int closedCount = clientGroupContext.closeds.size();
			instance.closedCountLabel.setText("断链" + numberFormat.format(closedCount));

			//			log.error("{},{},{}", connectionCount, connectedCount, closedCount);
		}
	}

	public static void updateReceivedLabel() {
		if (isNeedUpdateReceivedCount) {
			isNeedUpdateReceivedCount = false;

			NumberFormat numberFormat = NumberFormat.getInstance();
			ClientGroupContext<ImSessionContext, ImPacket, Object> clientGroupContext = imClientStarter.getClientGroupContext();
			GroupStat groupStat = clientGroupContext.getGroupStat();
			instance.receivedLabel.setText(numberFormat.format(groupStat.getReceivedPacket().get()) + "条共" + numberFormat.format(groupStat.getReceivedBytes().get()) + "B");
		}
	}

	public static void updateSentLabel() {
		if (isNeedUpdateSentCount) {
			isNeedUpdateSentCount = false;
			NumberFormat numberFormat = NumberFormat.getInstance();
			ClientGroupContext<ImSessionContext, ImPacket, Object> clientGroupContext = imClientStarter.getClientGroupContext();
			GroupStat groupStat = clientGroupContext.getGroupStat();
			instance.sentLabel.setText(numberFormat.format(groupStat.getSentPacket().get()) + "条共" + numberFormat.format(groupStat.getSentBytes().get()) + "B");

		}
	}

	long lastupdateTime = SystemTimer.currentTimeMillis();

	/**
	 * Creates new form JFrameMain
	 */
	private JFrameMain() {
		listModel = new DefaultListModel<ClientChannelContext<ImSessionContext, ImPacket, Object>>();
		initComponents();

		Config conf = ConfigFactory.load("app.conf");

		serverip.setText(conf.getString("server"));
		port.setText(conf.getString("port"));
		loginnameSufEndField.setText(conf.getString("client.count"));
		groupField.setText(conf.getString("group"));
		msgField.setText(conf.getString("chat.content"));
		loopcountField.setText(conf.getString("send.count"));

		//#2ecc71 OK
		//##f1c40f warn
		Color okColor = new Color(0x2e, 0xcc, 0x71);
		Color warnColor = new Color(0xe7, 0x4c, 0x3c);
		clients.setCellRenderer(new ImListCellRenderer(okColor, warnColor));
		try {
			imClientStarter = new ImClientStarter();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					long currTime = SystemTimer.currentTimeMillis();
					long iv = currTime - lastupdateTime;
					if (iv > 2000) {
						isNeedUpdateConnectionCount = true;
						isNeedUpdateReceivedCount = true;
						isNeedUpdateSentCount = true;
						isNeedUpdateList = true;
						lastupdateTime = SystemTimer.currentTimeMillis();
					}

					try {
						updateConnectionCount();
						//Thread.sleep(2);
						updateReceivedLabel();
						//Thread.sleep(2);
						updateSentLabel();
						//Thread.sleep(2);
					} catch (Exception e1) {
						log.error(e1.toString(), e1);
					}

					try {
						Thread.sleep(50L);
					} catch (InterruptedException e) {
						log.error(e.toString(), e);
					}
				}
			}

		}, "update ui task").start();
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	// <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
	private void initComponents() {

		serverip = new javax.swing.JTextField();
		jLabel1 = new javax.swing.JLabel();
		jLabel2 = new javax.swing.JLabel();
		port = new javax.swing.JTextField();
		lianjie = new javax.swing.JButton();
		jScrollPane1 = new javax.swing.JScrollPane();
		clients = new javax.swing.JList<>();
		msgField = new javax.swing.JTextField();
		sendBtn = new javax.swing.JButton();
		jScrollPane3 = new javax.swing.JScrollPane();
		msgTextArea = new MyTextArea();
		groupField = new javax.swing.JTextField();
		loopcountField = new javax.swing.JTextField();
		jLabel6 = new javax.swing.JLabel();
		loginnameSufEndField = new javax.swing.JTextField();
		jLabel3 = new javax.swing.JLabel();
		printLogBtn = new javax.swing.JButton();
		jLabel4 = new javax.swing.JLabel();
		jLabel5 = new javax.swing.JLabel();
		connectionCountLabel = new javax.swing.JLabel();
		connectedCountLabel = new javax.swing.JLabel();
		closedCountLabel = new javax.swing.JLabel();
		delBtn = new javax.swing.JButton();
		jLabel8 = new javax.swing.JLabel();
		receivedLabel = new javax.swing.JLabel();
		jLabel12 = new javax.swing.JLabel();
		sentLabel = new javax.swing.JLabel();

		setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
		setTitle("talent-im-simple-client-1.7.0.1.v20170601-RELEASE");

		serverip.setText("127.0.0.1");
		serverip.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				serveripActionPerformed(evt);
			}
		});

		jLabel1.setText("Server");

		jLabel2.setFont(new java.awt.Font("宋体", 1, 12)); // NOI18N
		jLabel2.setText(":");

		port.setText("9321");
		port.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				portActionPerformed(evt);
			}
		});

		lianjie.setFont(new java.awt.Font("宋体", 1, 14)); // NOI18N
		lianjie.setForeground(new java.awt.Color(51, 0, 255));
		lianjie.setText("连接并进入群");
		lianjie.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				lianjieActionPerformed(evt);
			}
		});

		clients.setFont(new java.awt.Font("宋体", 0, 18)); // NOI18N
		clients.setModel(listModel);
		clients.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
		jScrollPane1.setViewportView(clients);

		msgField.setText("he");

		sendBtn.setFont(new java.awt.Font("宋体", 1, 14)); // NOI18N
		sendBtn.setForeground(new java.awt.Color(51, 0, 255));
		sendBtn.setText("群聊");
		sendBtn.setEnabled(false);
		sendBtn.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				sendBtnActionPerformed(evt);
			}
		});

		msgTextArea.setColumns(20);
		msgTextArea.setFont(new java.awt.Font("宋体", 0, 18)); // NOI18N
		msgTextArea.setRows(5);
		msgTextArea.setText(
				"使用说明：\n1、设置好Server和端口\n2、设置好连接数量(可以用默认的)\n3、设置好群组名(可以用默认的)\n\n4、点击“连接并进入群”，在与服务器连接后，将会自动进入群组。\n5、点击“群聊”，将会收到连接数量乘以群发次数条消息(本例中的数据是: 1000*2000=2000000)\n\n\n");
		msgTextArea.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				msgTextAreaMouseClicked(evt);
			}
		});
		jScrollPane3.setViewportView(msgTextArea);

		groupField.setText("g");
		groupField.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				groupFieldActionPerformed(evt);
			}
		});

		loopcountField.setText("2000");
		loopcountField.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				loopcountFieldActionPerformed(evt);
			}
		});

		jLabel6.setText("次");

		loginnameSufEndField.setText("1000");
		loginnameSufEndField.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				loginnameSufEndFieldActionPerformed(evt);
			}
		});

		jLabel3.setText("连接数量");

		printLogBtn.setFont(new java.awt.Font("宋体", 1, 14)); // NOI18N
		printLogBtn.setForeground(new java.awt.Color(51, 0, 255));
		printLogBtn.setText("打印统计信息");
		printLogBtn.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				printLogBtnActionPerformed(evt);
			}
		});

		jLabel4.setText("群组名");

		jLabel5.setText("聊天内容");

		connectionCountLabel.setFont(new java.awt.Font("宋体", 0, 18)); // NOI18N
		connectionCountLabel.setForeground(new java.awt.Color(51, 0, 204));
		connectionCountLabel.setText("总连接0");

		connectedCountLabel.setFont(new java.awt.Font("宋体", 0, 18)); // NOI18N
		connectedCountLabel.setForeground(new java.awt.Color(0, 153, 0));
		connectedCountLabel.setText("正常链路0");

		closedCountLabel.setFont(new java.awt.Font("宋体", 0, 18)); // NOI18N
		closedCountLabel.setForeground(new java.awt.Color(255, 0, 0));
		closedCountLabel.setText("断链0");

		delBtn.setFont(new java.awt.Font("宋体", 1, 18)); // NOI18N
		delBtn.setForeground(new java.awt.Color(51, 0, 255));
		delBtn.setText("删除");
		delBtn.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				delBtnActionPerformed(evt);
			}
		});

		jLabel8.setFont(new java.awt.Font("宋体", 1, 18)); // NOI18N
		jLabel8.setText("已接收");

		receivedLabel.setFont(new java.awt.Font("宋体", 0, 18)); // NOI18N
		receivedLabel.setText("0");

		jLabel12.setFont(new java.awt.Font("宋体", 1, 18)); // NOI18N
		jLabel12.setText("已发送");

		sentLabel.setFont(new java.awt.Font("宋体", 0, 18)); // NOI18N
		sentLabel.setText("0");

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(layout.createSequentialGroup().addContainerGap()
						.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
								.addGroup(layout.createSequentialGroup().addGap(0, 40, Short.MAX_VALUE)
										.addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(serverip, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(jLabel2)
										.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(port, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE).addGap(18, 18, 18)
										.addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(loginnameSufEndField, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
								.addGroup(layout.createSequentialGroup().addComponent(delBtn).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(connectionCountLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(connectedCountLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
						.addGap(0, 0, 0)
						.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
								.addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(closedCountLabel,
										javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE))
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
								.addGroup(layout
										.createSequentialGroup().addComponent(groupField, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED).addComponent(lianjie)
										.addGap(74, 74, 74).addComponent(jLabel5))
								.addGroup(layout.createSequentialGroup().addComponent(jLabel8).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(receivedLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 295, javax.swing.GroupLayout.PREFERRED_SIZE)))
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
								.addGroup(layout.createSequentialGroup().addComponent(msgField, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
										.addComponent(loopcountField, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(jLabel6)
										.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(sendBtn)
										.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED).addComponent(printLogBtn).addGap(0, 0, Short.MAX_VALUE))
								.addGroup(layout.createSequentialGroup().addComponent(jLabel12).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(sentLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
						.addContainerGap())
				.addGroup(layout.createSequentialGroup().addGap(1, 1, 1)
						.addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(jScrollPane3)));
		layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(layout.createSequentialGroup().addContainerGap().addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
						.addComponent(serverip, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addComponent(jLabel1).addComponent(jLabel2)
						.addComponent(port, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addComponent(loginnameSufEndField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addComponent(lianjie)
						.addComponent(groupField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addComponent(msgField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addComponent(loopcountField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addComponent(jLabel6).addComponent(sendBtn).addComponent(jLabel3).addComponent(jLabel4).addComponent(printLogBtn).addComponent(jLabel5))
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 10, Short.MAX_VALUE)
						.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(connectionCountLabel).addComponent(connectedCountLabel)
								.addComponent(closedCountLabel).addComponent(delBtn).addComponent(jLabel8).addComponent(receivedLabel).addComponent(jLabel12).addComponent(
										sentLabel))
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
								.addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 554, Short.MAX_VALUE).addComponent(jScrollPane3))
						.addContainerGap()));

		pack();
	}// </editor-fold>//GEN-END:initComponents

	private void serveripActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_serveripActionPerformed
																				// TODO add your handling code here:
	}//GEN-LAST:event_serveripActionPerformed

	private void portActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_portActionPerformed
																			// TODO add your handling code here:
	}//GEN-LAST:event_portActionPerformed

	//	public void updateClientCount()
	//	{
	//		int clientSize = imClientStarter.getAioClient().getClientGroupContext().getConnections().getSet().getObj().size();
	//		clientCountLabel.setText(clientSize + "个客户端");
	//	}

	private void lianjieActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lianjieActionPerformed
		try {
			final String serverip_ = serverip.getText();
			final Integer port_ = Integer.parseInt(port.getText());

			int start = 0;//Integer.parseInt(loginnameSufStartField.getText());
			int end = Integer.parseInt(loginnameSufEndField.getText());
			//                int count = end - start + 1;

			int count = end - start;
			final Node serverNode = new Node(serverip_, port_);

			WriteLock writeLock = updatingListLock.writeLock();
			writeLock.lock();
			try {
				for (int i = 0; i < count; i++) {
					ClientChannelContext<ImSessionContext, ImPacket, Object> channelContext = imClientStarter.getAioClient().connect(serverNode);
					if (listModel.size() < MAX_LIST_COUNT) {
						if (channelContext != null) {
							listModel.addElement(channelContext);
						}
					}
				}
				clients.repaint();
			} catch (Exception e) {
				log.error(e.toString(), e);
			} finally {
				writeLock.unlock();
			}
		} catch (Exception e) {
			String str = ExceptionUtils.getStackTrace(e);
			msgTextArea.append(str);
		} finally {
			//			lianjie.setEnabled(false);
			//			serverip.setEnabled(false);
			//			port.setEnabled(false);
			//			loginnameSufEndField.setEnabled(false);
			//			groupField.setEnabled(false);

			sendBtn.setEnabled(true);
		}

	}//GEN-LAST:event_lianjieActionPerformed

	//	public static String getSelectedId()
	//	{
	//		int index = JFrameMain.getInstance().clients.getSelectedIndex();//.getModel();
	//		if (index < 0)
	//		{
	//			log.error("没有选中任何客户端");
	//			return null;
	//		}
	//		String id = (String) JFrameMain.getInstance().listModel.getElementAt(index);
	//		return id;
	//	}

	private long sendStartTime = SystemTimer.currentTimeMillis();
	private long startRecievedBytes; //点击发送时的收到的字节数
	private long startSentBytes; //点击发送时的发送的字节数

	private void sendBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sendBtnActionPerformed
		sendBtn.setEnabled(false);

		ClientGroupContext<ImSessionContext, ImPacket, Object> clientGroupContext = imClientStarter.getAioClient().getClientGroupContext();
		setStartRecievedBytes(clientGroupContext.getGroupStat().getReceivedBytes().get());
		setStartSentBytes(clientGroupContext.getGroupStat().getSentBytes().get());

		JFrameMain.getInstance().getMsgTextArea().setText("");
		receivedPackets.set(0);
		sentPackets.set(0);

		String msg = msgField.getText();
		int loopcount = Integer.parseInt(loopcountField.getText());
		String toGroup = groupField.getText();
		ChatReqBody.Builder builder = ChatReqBody.newBuilder();
		builder.setTime(SystemTimer.currentTimeMillis());
		builder.setGroup(toGroup);
		builder.setType(ChatType.CHAT_TYPE_PUBLIC);
		builder.setText(msg);

		ChatReqBody chatReqBody = builder.build();
		setSendStartTime(SystemTimer.currentTimeMillis());

		byte[] body = chatReqBody.toByteArray();
		ImPacket packet = new ImPacket(body, Command.COMMAND_CHAT_REQ);

		if (listModel.size() == 0) {
			return;
		}
		ClientChannelContext<ImSessionContext, ImPacket, Object> channelContext = listModel.getElementAt(0);
		for (int i = 0; i < loopcount; i++) {
			Aio.send(channelContext, packet);
		}

		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					log.error(e.toString(), e);
				}
				sendBtn.setEnabled(true);
			}
		}).start();

	}//GEN-LAST:event_sendBtnActionPerformed

	private void groupFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_groupFieldActionPerformed
																				// TODO add your handling code here:
	}//GEN-LAST:event_groupFieldActionPerformed

	private void loginnameSufEndFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loginnameSufEndFieldActionPerformed
																							// TODO add your handling code here:
	}//GEN-LAST:event_loginnameSufEndFieldActionPerformed

	private void msgTextAreaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_msgTextAreaMouseClicked

		if (evt.getButton() == MouseEvent.BUTTON3) {//右键
			log.error(evt.getButton() + "");
		}
	}//GEN-LAST:event_msgTextAreaMouseClicked

	private void loopcountFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loopcountFieldActionPerformed
																					// TODO add your handling code here:
	}//GEN-LAST:event_loopcountFieldActionPerformed

	private void printLogBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cleanBtn1ActionPerformed
		if (imClientStarter == null) {
			log.error("还没有连接");
			return;
		}

		//		String id = imClientStarter.getClientGroupContext().getId();
		ClientGroupContext<ImSessionContext, ImPacket, Object> clientGroupContext = imClientStarter.getClientGroupContext();

		ObjWithLock<Set<ChannelContext<ImSessionContext, ImPacket, Object>>> connectionsSetWithLock = clientGroupContext.connections.getSetWithLock();
		Set<ChannelContext<ImSessionContext, ImPacket, Object>> connectionsSet = connectionsSetWithLock.getObj();

		ObjWithLock<Set<ChannelContext<ImSessionContext, ImPacket, Object>>> connectedsSetWithLock = clientGroupContext.connecteds.getSetWithLock();
		Set<ChannelContext<ImSessionContext, ImPacket, Object>> connectedsSet = connectedsSetWithLock.getObj();

		ObjWithLock<Set<ChannelContext<ImSessionContext, ImPacket, Object>>> closedsSetWithLock = clientGroupContext.closeds.getSetWithLock();
		Set<ChannelContext<ImSessionContext, ImPacket, Object>> closedsSet = closedsSetWithLock.getObj();

		ClientGroupStat clientGroupStat = clientGroupContext.getClientGroupStat();
		log.error("<<--------------------\r\n当前时间:{}\r\n当前总连接数:{} = {}  + {} (连上的 + 关闭的)\r\n已经接受{}条消息共{}KB\r\n已经处理{}条消息\r\n已经发送{}条消息共{}KB\r\n-------------------->>",
				SystemTimer.currentTimeMillis(), connectionsSet.size(), connectedsSet.size(), closedsSet.size(), clientGroupStat.getReceivedPacket().get(),
				clientGroupStat.getReceivedBytes().get() / 1000, clientGroupStat.getHandledPacket().get(), clientGroupStat.getSentPacket().get(),
				clientGroupStat.getSentBytes().get() / 1000);
	}//GEN-LAST:event_cleanBtn1ActionPerformed

	private void delBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_delBtnActionPerformed
		// TODO add your handling code here:
		//    	synchronized (clients)
		//		{
		List<ClientChannelContext<ImSessionContext, ImPacket, Object>> selecteds = clients.getSelectedValuesList();
		if (selecteds == null || selecteds.size() == 0) {
			log.error("请选中要删除的连接");
			return;
		} else {
			//			WriteLock updatingListWriteLock = JFrameMain.updatingListLock.writeLock();
			//			updatingListWriteLock.lock();
			try {
				for (ClientChannelContext<ImSessionContext, ImPacket, Object> cc : selecteds) {
					if (cc != null) {
						//log.error("准备删除:{}", cc);
						//listModel.removeElement(cc);
						Aio.remove(cc, REMOVE_REMARK);
					}
				}
			} catch (Exception e) {
				log.error(e.toString(), e);
			} finally {
				//				updatingListWriteLock.unlock();
			}

		}
		//		}

		//.getSelectedValues();

	}//GEN-LAST:event_delBtnActionPerformed

	/**
	 * @param args the command line arguments
	 */
	public static void main(String args[]) {
		/* Set the Nimbus look and feel */
		//<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
		/* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
		 * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
		 */
		try {
			//			try
			//			{
			//				org.tio.examples.im.client.ImClientStarter.init();
			//			} catch (Exception e)
			//			{
			//				throw new RuntimeException(e);
			//			}

			for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					javax.swing.UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		} catch (ClassNotFoundException ex) {
			java.util.logging.Logger.getLogger(JFrameMain.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		} catch (InstantiationException ex) {
			java.util.logging.Logger.getLogger(JFrameMain.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		} catch (IllegalAccessException ex) {
			java.util.logging.Logger.getLogger(JFrameMain.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		} catch (javax.swing.UnsupportedLookAndFeelException ex) {
			java.util.logging.Logger.getLogger(JFrameMain.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		}
		//</editor-fold>

		/* Create and display the form */
		java.awt.EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				JFrameMain.getInstance().setVisible(true);
			}
		});
	}

	private DefaultListModel<ClientChannelContext<ImSessionContext, ImPacket, Object>> listModel = null;

	//    private Set<ChannelContext> clients_ = new HashSet<>();
	// Variables declaration - do not modify//GEN-BEGIN:variables
	private javax.swing.JList<ClientChannelContext<ImSessionContext, ImPacket, Object>> clients;
	private javax.swing.JLabel closedCountLabel;
	private javax.swing.JLabel connectedCountLabel;
	private javax.swing.JLabel connectionCountLabel;
	private javax.swing.JButton delBtn;
	private javax.swing.JTextField groupField;
	private javax.swing.JLabel jLabel1;
	private javax.swing.JLabel jLabel12;
	private javax.swing.JLabel jLabel2;
	private javax.swing.JLabel jLabel3;
	private javax.swing.JLabel jLabel4;
	private javax.swing.JLabel jLabel5;
	private javax.swing.JLabel jLabel6;
	private javax.swing.JLabel jLabel8;
	private javax.swing.JScrollPane jScrollPane1;
	private javax.swing.JScrollPane jScrollPane3;
	private javax.swing.JButton lianjie;
	private javax.swing.JTextField loginnameSufEndField;
	private javax.swing.JTextField loopcountField;
	private javax.swing.JTextField msgField;
	private javax.swing.JTextArea msgTextArea;
	private javax.swing.JTextField port;
	private javax.swing.JButton printLogBtn;
	private javax.swing.JLabel receivedLabel;
	private javax.swing.JButton sendBtn;
	private javax.swing.JLabel sentLabel;
	private javax.swing.JTextField serverip;
	// End of variables declaration//GEN-END:variables

	public JList<ClientChannelContext<ImSessionContext, ImPacket, Object>> getClients() {
		return clients;
	}

	public javax.swing.JTextArea getMsgTextArea() {
		return msgTextArea;
	}

	/**
	 * @return the imClientStarter
	 */
	public static ImClientStarter getImClientStarter() {
		return imClientStarter;
	}

	/**
	 * @param imClientStarter the imClientStarter to set
	 */
	public static void setImClientStarter(ImClientStarter imClientStarter) {
		JFrameMain.imClientStarter = imClientStarter;
	}

	/**
	 * @return the listModel
	 */
	@SuppressWarnings("rawtypes")
	public DefaultListModel getListModel() {
		return listModel;
	}

	//	/**
	//	 * @param listModel the listModel to set
	//	 */
	//	@SuppressWarnings("rawtypes")
	//	public void setListModel(DefaultListModel listModel)
	//	{
	//		this.listModel = listModel;
	//	}

	//	/**
	//	 * @return the cleanBtn
	//	 */
	//	public javax.swing.JButton getCleanBtn()
	//	{
	//		return cleanBtn;
	//	}
	//
	//	/**
	//	 * @param cleanBtn the cleanBtn to set
	//	 */
	//	public void setCleanBtn(javax.swing.JButton cleanBtn)
	//	{
	//		this.cleanBtn = cleanBtn;
	//	}

	/**
	 * @return the printBtn
	 */
	public javax.swing.JButton getPrintBtn() {
		return printLogBtn;
	}

	/**
	 * @param printBtn the printBtn to set
	 */
	public void setPrintBtn(javax.swing.JButton printBtn) {
		this.printLogBtn = printBtn;
	}

	/**
	 * @return the groupField
	 */
	public javax.swing.JTextField getGroupField() {
		return groupField;
	}

	/**
	 * @param groupField the groupField to set
	 */
	public void setGroupField(javax.swing.JTextField groupField) {
		this.groupField = groupField;
	}

	/**
	 * @return the jLabel1
	 */
	public javax.swing.JLabel getjLabel1() {
		return jLabel1;
	}

	/**
	 * @param jLabel1 the jLabel1 to set
	 */
	public void setjLabel1(javax.swing.JLabel jLabel1) {
		this.jLabel1 = jLabel1;
	}

	/**
	 * @return the jLabel2
	 */
	public javax.swing.JLabel getjLabel2() {
		return jLabel2;
	}

	/**
	 * @param jLabel2 the jLabel2 to set
	 */
	public void setjLabel2(javax.swing.JLabel jLabel2) {
		this.jLabel2 = jLabel2;
	}

	/**
	 * @return the jLabel3
	 */
	public javax.swing.JLabel getjLabel3() {
		return jLabel3;
	}

	/**
	 * @param jLabel3 the jLabel3 to set
	 */
	public void setjLabel3(javax.swing.JLabel jLabel3) {
		this.jLabel3 = jLabel3;
	}

	/**
	 * @return the jLabel6
	 */
	public javax.swing.JLabel getjLabel6() {
		return jLabel6;
	}

	/**
	 * @param jLabel6 the jLabel6 to set
	 */
	public void setjLabel6(javax.swing.JLabel jLabel6) {
		this.jLabel6 = jLabel6;
	}

	/**
	 * @return the jScrollPane1
	 */
	public javax.swing.JScrollPane getjScrollPane1() {
		return jScrollPane1;
	}

	/**
	 * @param jScrollPane1 the jScrollPane1 to set
	 */
	public void setjScrollPane1(javax.swing.JScrollPane jScrollPane1) {
		this.jScrollPane1 = jScrollPane1;
	}

	/**
	 * @return the jScrollPane3
	 */
	public javax.swing.JScrollPane getjScrollPane3() {
		return jScrollPane3;
	}

	/**
	 * @param jScrollPane3 the jScrollPane3 to set
	 */
	public void setjScrollPane3(javax.swing.JScrollPane jScrollPane3) {
		this.jScrollPane3 = jScrollPane3;
	}

	/**
	 * @return the lianjie
	 */
	public javax.swing.JButton getLianjie() {
		return lianjie;
	}

	/**
	 * @param lianjie the lianjie to set
	 */
	public void setLianjie(javax.swing.JButton lianjie) {
		this.lianjie = lianjie;
	}

	/**
	 * @return the loginnameSufEndField
	 */
	public javax.swing.JTextField getLoginnameSufEndField() {
		return loginnameSufEndField;
	}

	/**
	 * @param loginnameSufEndField the loginnameSufEndField to set
	 */
	public void setLoginnameSufEndField(javax.swing.JTextField loginnameSufEndField) {
		this.loginnameSufEndField = loginnameSufEndField;
	}

	/**
	 * @return the loopcountField
	 */
	public javax.swing.JTextField getLoopcountField() {
		return loopcountField;
	}

	/**
	 * @param loopcountField the loopcountField to set
	 */
	public void setLoopcountField(javax.swing.JTextField loopcountField) {
		this.loopcountField = loopcountField;
	}

	/**
	 * @return the msgField
	 */
	public javax.swing.JTextField getMsgField() {
		return msgField;
	}

	/**
	 * @param msgField the msgField to set
	 */
	public void setMsgField(javax.swing.JTextField msgField) {
		this.msgField = msgField;
	}

	/**
	 * @return the port
	 */
	public javax.swing.JTextField getPort() {
		return port;
	}

	/**
	 * @param port the port to set
	 */
	public void setPort(javax.swing.JTextField port) {
		this.port = port;
	}

	/**
	 * @return the sendBtn
	 */
	public javax.swing.JButton getSendBtn() {
		return sendBtn;
	}

	/**
	 * @param sendBtn the sendBtn to set
	 */
	public void setSendBtn(javax.swing.JButton sendBtn) {
		this.sendBtn = sendBtn;
	}

	/**
	 * @return the serverip
	 */
	public javax.swing.JTextField getServerip() {
		return serverip;
	}

	/**
	 * @param serverip the serverip to set
	 */
	public void setServerip(javax.swing.JTextField serverip) {
		this.serverip = serverip;
	}

	/**
	 * @param clients the clients to set
	 */
	public void setClients(JList<ClientChannelContext<ImSessionContext, ImPacket, Object>> clients) {
		this.clients = clients;
	}

	/**
	 * @param msgTextArea the msgTextArea to set
	 */
	public void setMsgTextArea(javax.swing.JTextArea msgTextArea) {
		this.msgTextArea = msgTextArea;
	}

	/**
	 * @return the sendStartTime
	 */
	public long getSendStartTime() {
		return sendStartTime;
	}

	/**
	 * @param sendStartTime the sendStartTime to set
	 */
	public void setSendStartTime(long sendStartTime) {
		this.sendStartTime = sendStartTime;
	}

	/**
	 * @return the startRecievedBytes
	 */
	public long getStartRecievedBytes() {
		return startRecievedBytes;
	}

	/**
	 * @param startRecievedBytes the startRecievedBytes to set
	 */
	public void setStartRecievedBytes(long startRecievedBytes) {
		this.startRecievedBytes = startRecievedBytes;
	}

	/**
	 * @return the startSentBytes
	 */
	public long getStartSentBytes() {
		return startSentBytes;
	}

	/**
	 * @param startSentBytes the startSentBytes to set
	 */
	public void setStartSentBytes(long startSentBytes) {
		this.startSentBytes = startSentBytes;
	}
}
