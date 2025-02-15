package com.example.deleteemail;

import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Store;
import javax.mail.internet.MimeMessage;

import org.junit.Test;

/**
 * mvn -q -Dtest=com.example.deleteemail.AppTest\#mail test
 */
public class AppTest {
	@Test
	public void mail() throws Exception {
		Store store = EmailTest.getStore();
		Folder folder = store.getFolder("inbox");
		int step = 10;
		while (true) {
			try {
				if (!folder.isOpen()) {
					folder.open(Folder.READ_WRITE);
				}
				Integer totalCount = folder.getMessageCount();
				if (totalCount <= 0) {
					break;
				}
				Message[] messages = EmailTest.getMessages(folder, totalCount - step * 12, totalCount);
				if (messages.length == 0) {
					break;
				}
				Thread[] list = new Thread[12];
				for (int j = 0; j < 12; j++) {
					Thread t = new Thread(new MyThread(j, 10, messages, folder));
					t.start();
					list[j] = t;
				}
				for (Runnable r : list) {
					r.wait();
				}
				folder.expunge();
			} catch (Exception e) {
				System.out.println("异常： " + e);
			}
		}
		store.close();
	}
}

class MyThread implements Runnable {
	int start;
	int step;
	Message[] messages;
	Folder folder;

	public MyThread(int start, int step, Message[] messages, Folder folder) {
		this.start = start;
		this.step = step;
		this.messages = messages;
		this.folder = folder;
	}

	@Override
	public void run() {
		try {
			for (int i = start * step; i < (start + 1) * step; i++) {
				String subject = messages[i].getSubject();
				String from = (messages[i].getFrom()[0]).toString();
				System.out.println(from);
				if (from.indexOf("notifications@github.com") == -1) {
					continue;
				}
				System.out.printf("第 %d 封邮件\n", i + 1);
				System.out.printf("标题: %s\n", subject);
				System.out.printf("发件人：%s\n", from);
				System.out.printf("邮件总数: %d\n", folder.getMessageCount());
				System.out.printf("未读邮件数: %d\n", folder.getUnreadMessageCount());
				System.out.printf("邮件是否已读: %s\n", messages[i].getFlags().contains(Flags.Flag.SEEN) ? "是" : "否");
				System.out.printf("发送时间：%s\n", EmailTest.getSentDate((MimeMessage) messages[i], null));
				System.out.printf("邮件优先级：%s\n", EmailTest.getPriority((MimeMessage) messages[i]));
				System.out.printf("邮件大小：%skb\n", ((MimeMessage) messages[i]).getSize() * 1024);
				messages[i].setFlag(Flags.Flag.DELETED, true);
			}
		} catch (Exception e) {
			System.out.println("异常： " + e);
		}
	}
}