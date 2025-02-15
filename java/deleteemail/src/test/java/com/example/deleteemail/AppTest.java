package com.example.deleteemail;

import java.util.concurrent.CountDownLatch;

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
		String data = "App Store Connect <no_reply@email.apple.com>";
		boolean i = data.indexOf("no_reply@email.apple.com") < 0;
		System.out.println(i);
		Store store = EmailTest.getStore();
		Folder folder = store.getFolder("inbox");
		int step = 10;
		int groupNum = 12;
		while (true) {
			if (!folder.isOpen()) {
				folder.open(Folder.READ_WRITE);
			}
			Integer totalCount = folder.getMessageCount();
			if (totalCount <= 0) {
				break;
			}
			Message[] messages = EmailTest.getMessages(folder, totalCount - step * groupNum, totalCount - 1);
			if (messages.length == 0) {
				break;
			}
			CountDownLatch waitGroup = new CountDownLatch(groupNum);
			for (int groupIndex = 0; groupIndex < groupNum; groupIndex++) {
				new Thread(new InstanceThread(groupIndex, step, messages, folder, waitGroup)).start();
			}
			waitGroup.await();
			folder.expunge();
		}
		store.close();
	}
}

class InstanceThread implements Runnable {
	protected int groupIndex;
	protected int count;
	protected Message[] messages;
	protected Folder folder;
	CountDownLatch waitGroup;

	public InstanceThread(int groupIndex, int count, Message[] messages, Folder folder, CountDownLatch waitGroup) {
		this.groupIndex = groupIndex;
		this.count = count;
		this.messages = messages;
		this.folder = folder;
		this.waitGroup = waitGroup;
	}

	@Override
	public void run() {
		try {
			for (int i = groupIndex * count; i < (groupIndex + 1) * count; i++) {
				if (messages[i] == null) {
					continue;
				}
				String senderName = EmailTest.getFrom((MimeMessage) messages[i]);
				if (senderName.indexOf("no_reply@email.apple.com") == -1) {
					System.out.println(senderName);
					continue;
				}
				EmailTest.parseFileMessage(messages[0]);
			}
		} catch (Exception e) {
			System.out.println("异常： " + e);
		}
		this.waitGroup.countDown();
	}
}