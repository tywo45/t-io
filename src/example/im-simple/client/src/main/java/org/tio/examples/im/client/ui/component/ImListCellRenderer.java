package org.tio.examples.im.client.ui.component;

import java.awt.Color;
import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

import org.tio.client.ClientChannelContext;
import org.tio.examples.im.common.ImPacket;
import org.tio.examples.im.common.ImSessionContext;

/**
 * 
 * @author tanyaowu 
 *
 */
public class ImListCellRenderer extends DefaultListCellRenderer
{
	/**
	 * @含义: 
	 * @类型: long
	 */
	private static final long serialVersionUID = 1L;
	private Color okColor;
	private Color warnColor;

	//#5cb85c OK
	//#f0ad4e warn
	public ImListCellRenderer(Color okColor, Color warnColor)
	{
		this.okColor = okColor;
		this.warnColor = warnColor;
	}

	@Override
	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus)
	{
		@SuppressWarnings("unchecked")
		ClientChannelContext<ImSessionContext, ImPacket, Object> channelContext = (ClientChannelContext<ImSessionContext, ImPacket, Object>) value;
		super.getListCellRendererComponent(list, channelContext, index, isSelected, cellHasFocus);

		String text = channelContext.getServerNode() + "<--" + channelContext.getClientNode();
		setText(text);

		if (channelContext.isClosed())
		{
			//			Font font = new Font("宋体", Font.PLAIN, 36);
			setForeground(warnColor);
			//			setBackground(this.warnColor);
		} else
		{
			//			Font font = new Font("宋体", Font.PLAIN, 36);
			setForeground(okColor);
			//			setBackground(this.okColor);
		}

		return this;
	}
}
