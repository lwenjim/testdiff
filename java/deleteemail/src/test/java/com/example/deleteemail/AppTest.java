package com.example.deleteemail;

import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Store;
import javax.mail.internet.MimeMessage;

import java.io.UnsupportedEncodingException;
import java.util.concurrent.*;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AppTest {
	private static final Logger logger = LoggerFactory.getLogger(AppTest.class);
	private static final int BATCH_SIZE = 240; // STEP(20) * GROUP_NUM(12)
	private static final int THREAD_POOL_SIZE = 12;

	@Test
	public void mail() {
		try {
			Store store = EmailTest.getStore();
			Folder folder = store.getFolder("inbox");
			folder.open(Folder.READ_WRITE);
			final int totalMessages = folder.getMessageCount();

			if (totalMessages == 0)
				return;

			final ExecutorService executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

			for (int start = 1; start <= totalMessages; start += BATCH_SIZE) {
				int end = Math.min(start + BATCH_SIZE - 1, totalMessages);
				Message[] messages = folder.getMessages(start, end);

				logger.info("Processing messages: {} - {}", start, end);

				processMessagesConcurrently(messages, executor);
			}

			executor.shutdown();
			if (!executor.awaitTermination(5, TimeUnit.MINUTES)) {
				logger.warn("Thread pool did not terminate gracefully");
			}

			folder.expunge(); // 所有批次处理完成后统一提交删除
		} catch (Exception e) {
			logger.error("Critical error occurred", e);
		}
	}

	private void processMessagesConcurrently(Message[] messages, ExecutorService executor) {
		final int messagesPerThread = messages.length / THREAD_POOL_SIZE;
		final CountDownLatch latch = new CountDownLatch(THREAD_POOL_SIZE);

		for (int i = 0; i < THREAD_POOL_SIZE; i++) {
			int startIdx = i * messagesPerThread;
			int endIdx = (i == THREAD_POOL_SIZE - 1) ? messages.length : (i + 1) * messagesPerThread;

			executor.submit(new DeleteTask(messages, startIdx, endIdx, latch));
		}

		try {
			latch.await();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			logger.error("Thread interrupted", e);
		}
	}
}

class DeleteTask implements Runnable {
	private static final Logger logger = LoggerFactory.getLogger(DeleteTask.class);
	private final Message[] messages;
	private final int startIdx;
	private final int endIdx;
	private final CountDownLatch latch;

	DeleteTask(Message[] messages, int startIdx, int endIdx, CountDownLatch latch) {
		this.messages = messages;
		this.startIdx = startIdx;
		this.endIdx = endIdx;
		this.latch = latch;
	}

	@Override
	public void run() {
		for (int i = startIdx; i < endIdx; i++) {
			Message message = messages[i];
			if (message == null)
				continue;

			String sender;
			try {
				sender = EmailTest.getFrom((MimeMessage) message);
				if (sender.contains("email.apple.com")) {
					message.setFlag(Flags.Flag.DELETED, true);
					logger.debug("Deleted email from: {}", sender);
				}
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (MessagingException e) {
				e.printStackTrace();
			}
		}
	}
}