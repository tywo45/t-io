/**
 * 
 */
package org.tio.examples.im.client.ui.component;

import javax.swing.JPopupMenu;
import javax.swing.JTextArea;

/**
 * 
 * @filename:	 org.tio.examples.im.client.ui.MyTextField
 * @copyright:   Copyright (c)2010
 * @company:     talent
 * @author:      谭耀武
 * @version:     1.0
 * @create time: 2013年3月30日 下午6:20:41
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
public class MyTextArea extends JTextArea
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 3378322477292463851L;
	private JPopupMenu jPopupMenu = null;

	public MyTextArea()
	{
		this.jPopupMenu = new MsgTextAreaJPopupMenu(this);
	}

	public MyTextArea(JPopupMenu jPopupMenu)
	{
		this.jPopupMenu = jPopupMenu;
	}

	@Override
	public void processMouseEvent(java.awt.event.MouseEvent e)
	{
		if (e.isPopupTrigger())
			jPopupMenu.show(this, e.getX(), e.getY());
		else
			super.processMouseEvent(e);
	}
}
