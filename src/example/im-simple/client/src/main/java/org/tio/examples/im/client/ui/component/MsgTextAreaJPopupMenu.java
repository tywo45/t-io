/**
 * 
 */
package org.tio.examples.im.client.ui.component;

import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.text.JTextComponent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @filename:	 org.tio.examples.im.client.ui.MsgTextAreaJPopupMenu
 * @copyright:   Copyright (c)2010
 * @company:     talent
 * @author:      谭耀武
 * @version:     1.0
 * @create time: 2013年3月30日 下午6:14:00
 * @record
 * <table cellPadding="3" cellSpacing="0" style="width:600px">
 * <thead style="font-weight:bold;background-color:#e3e197">
 * 	<tr>   <td>date</td>	<td>author</td>		<td>version</td>	<td>description</td></tr>
 * </thead>
 * <tbody style="background-color:#ffffeb">
* 	<tr><td>2013年3月30日</td>	<td>谭耀武</td>	<td>1.0</td>	<td>create</td></tr>
 * </tbody>
 * </table>
 */
public class MsgTextAreaJPopupMenu extends JPopupMenu implements ActionListener
{
	private static Logger log = LoggerFactory.getLogger(MsgTextAreaJPopupMenu.class);

	/**
	 * 
	 */
	public MsgTextAreaJPopupMenu()
	{

	}

	/**
	 * @param label
	 */
	public MsgTextAreaJPopupMenu(String label)
	{
		super(label);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
	}

	private JTextComponent jTextComponent = null;

	public MsgTextAreaJPopupMenu(JTextComponent jTextComponent)
	{
		this.jTextComponent = jTextComponent;
		add(getCleanMenuItem());
		add(getSelectallMenuItem());
		add(getCopyMenuItem());
		add(getCutMenuItem());
		add(getPasteMenuItem());

	}

	private JMenuItem getCopyMenuItem()
	{
		JMenuItem copyMenuItem = new JMenuItem();
		copyMenuItem.setText("copy");
		copyMenuItem.addActionListener(this);
		return copyMenuItem;
	}

	private JMenuItem getCutMenuItem()
	{
		JMenuItem cutMenuItem = new JMenuItem();
		cutMenuItem.setText("cut");
		cutMenuItem.addActionListener(this);
		return cutMenuItem;
	}

	private JMenuItem getPasteMenuItem()
	{
		JMenuItem pasteMenuItem = new JMenuItem();
		pasteMenuItem.setText("paste");
		pasteMenuItem.addActionListener(this);
		return pasteMenuItem;
	}

	private JMenuItem getCleanMenuItem()
	{
		JMenuItem pasteMenuItem = new JMenuItem();
		pasteMenuItem.setText("clean");
		pasteMenuItem.addActionListener(this);
		return pasteMenuItem;
	}

	private JMenuItem getSelectallMenuItem()
	{
		JMenuItem pasteMenuItem = new JMenuItem();
		pasteMenuItem.setText("select all");
		pasteMenuItem.addActionListener(this);
		return pasteMenuItem;
	}

	@Override
	public void actionPerformed(java.awt.event.ActionEvent e)
	{
		java.awt.datatransfer.Clipboard clipbd = new java.awt.datatransfer.Clipboard("");
		String command = e.getActionCommand();
		if (command.equalsIgnoreCase("select all"))
		{
			jTextComponent.selectAll();
		} else if (command.equalsIgnoreCase("clean"))
		{
			jTextComponent.setText("");
		} else if (command.equalsIgnoreCase("copy"))
		{
			jTextComponent.copy();
		} else if (command.equalsIgnoreCase("cut"))
		{
			jTextComponent.cut();
		} else if (command.equalsIgnoreCase("paste"))
		{
			jTextComponent.paste();
		}
	}

}
