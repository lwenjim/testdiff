package com.example.deleteemail;

import java.util.concurrent.CountDownLatch;

import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Store;
import javax.mail.internet.MimeMessage;

import org.junit.Test;

/**
 * mvn -q -Dtest=com.example.deleteemail.AppTest\#mail test
 */
public class AppTest {
	@Test
	public void mail() throws NoSuchProviderException, MessagingException, InterruptedException {
		Store store = EmailTest.getStore();
		Folder folder = store.getFolder("inbox/JSPP");
		int step = 20;
		int groupNum = 12;
		if (!folder.isOpen()) {
			folder.open(Folder.READ_WRITE);
		}
		Integer totalCount = folder.getMessageCount();
		if (totalCount <= 0) {
			return;
		}
		for (int page = 0; page <= totalCount / (step * groupNum) - 1; page++) {
			Message[] messages = EmailTest.getMessages(folder, page * step * groupNum + 1, (page + 1) * step * groupNum);
			System.out.printf("处理:%d - %d 之间的邮件\n", page * step * groupNum + 1, (page + 1) * step * groupNum);
			if (messages.length == 0) {
				break;
			}
			CountDownLatch waitGroup = new CountDownLatch(groupNum);
			for (int groupIndex = 0; groupIndex < groupNum; groupIndex++) {
				new Thread(new CurrentThread(groupIndex, step, messages, waitGroup)).start();
			}
			waitGroup.await();
			folder.expunge();
		}
		store.close();
	}
}

class CurrentThread implements Runnable {
	private int groupIndex;
	private int count;
	private Message[] messages;
	private CountDownLatch waitGroup;

	public CurrentThread(int groupIndex, int count, Message[] messages, CountDownLatch waitGroup) {
		this.groupIndex = groupIndex;
		this.count = count;
		this.messages = messages;
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
				if (senderName.indexOf("email.apple.com") == -1) {
					continue;
				}
				messages[i].setFlag(Flags.Flag.DELETED, true);
				System.out.printf("%s删除成功\n", senderName);
			}
		} catch (Exception e) {
			System.out.println("异常： " + e);
		}
		this.waitGroup.countDown();
	}
}