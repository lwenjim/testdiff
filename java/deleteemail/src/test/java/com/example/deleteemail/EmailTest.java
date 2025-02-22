package com.example.deleteemail;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

public class EmailTest {

	private static final Logger logger = LoggerFactory.getLogger(EmailTest.class);
	private static final String ATTACHMENT_SAVE_DIR = "/tmp/email/File/";
	private static final String DATE_PATTERN = "yyyy年MM月dd日 E HH:mm";

	/**
	 * 获取邮件消息
	 *
	 * @param folder 邮件文件夹
	 * @param start  起始索引
	 * @param end    结束索引
	 * @return 邮件消息数组
	 */
	public static Message[] getMessages(Folder folder, int start, int end) throws MessagingException {
		return folder.getMessages(start, end);
	}

	/**
	 * 获取邮件存储对象
	 *
	 * @return Store对象
	 */
	public static Store getStore() throws MessagingException {
		Properties prop = new Properties();
		prop.setProperty("mail.store.protocol", "imap");
		prop.put("mail.imap.auth", "true");
		prop.put("mail.imap.socketFactory.port", "993");
		prop.put("mail.imap.starttls.enable", "true");
		prop.put("mail.imap.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		prop.put("mail.imap.ssl.protocols", "TLSv1.2");

		Session session = Session.getInstance(prop);
		Store store = session.getStore();
		store.connect("imap.qq.com", System.getenv("EMAIL_ACCOUNT"), System.getenv("EMAIL_TOKEN"));
		return store;
	}

	/**
	 * 解析邮件内容
	 *
	 * @param messages 邮件消息数组
	 */
	public static void parseFileMessage(Message... messages) {
		if (messages == null || messages.length == 0) {
			logger.info("没有可读取邮件");
			return;
		}

		for (Message message : messages) {
			try {
				MimeMessage msg = (MimeMessage) message;
				logger.info("------------------解析第{}封邮件--------------------", msg.getMessageNumber());
				logger.info("主题: {}", MimeUtility.decodeText(msg.getSubject()));
				logger.info("发件人: {}", getFrom(msg));
				logger.info("收件人：{}", getReceiveAddress(msg, null));
				logger.info("发送时间：{}", getSentDate(msg, DATE_PATTERN));
				logger.info("是否已读：{}", isSeen(msg));
				logger.info("邮件优先级：{}", getPriority(msg));
				logger.info("是否需要回执：{}", isReplySign(msg));
				logger.info("邮件大小：{}kb", msg.getSize() * 1024);

				StringBuffer content = new StringBuffer(30);
				getMailTextContent(msg, content);
				logger.info("邮件正文：{}", content.length() > 100 ? content.substring(0, 100) + "..." : content);

				boolean isContainerAttachment = isContainAttachment(msg);
				logger.info("是否包含附件：{}", isContainerAttachment);
				if (isContainerAttachment) {
					saveAttachment(msg, ATTACHMENT_SAVE_DIR, msg.getFileName());
				}
				logger.info("------------------第{}封邮件解析结束--------------------", msg.getMessageNumber());
			} catch (Exception e) {
				logger.error("解析邮件失败", e);
			}
		}
	}

	/**
	 * 获取发件人信息
	 */
	public static String getFrom(MimeMessage msg) throws MessagingException, UnsupportedEncodingException {
		Address[] froms = msg.getFrom();
		if (froms.length < 1)
			throw new MessagingException("没有发件人!");

		InternetAddress address = (InternetAddress) froms[0];
		String person = address.getPersonal();
		if (person != null) {
			person = MimeUtility.decodeText(person) + " ";
		} else {
			person = "";
		}
		return person + "<" + address.getAddress() + ">";
	}

	/**
	 * 获取邮件发送时间
	 */
	public static String getSentDate(MimeMessage msg, String pattern) throws MessagingException {
		Date receivedDate = msg.getSentDate();
		if (receivedDate == null)
			return "";
		return new SimpleDateFormat(pattern).format(receivedDate);
	}

	/**
	 * 判断邮件是否已读
	 */
	public static boolean isSeen(MimeMessage msg) throws MessagingException {
		return msg.getFlags().contains(Flags.Flag.SEEN);
	}

	/**
	 * 判断邮件是否需要回执
	 */
	public static boolean isReplySign(MimeMessage msg) throws MessagingException {
		String[] headers = msg.getHeader("Disposition-Notification-To");
		return headers != null;
	}

	/**
	 * 获取邮件优先级
	 */
	public static String getPriority(MimeMessage msg) throws MessagingException {
		String[] headers = msg.getHeader("X-Priority");
		if (headers == null)
			return "普通";

		String headerPriority = headers[0];
		if (headerPriority.contains("1") || headerPriority.contains("High"))
			return "紧急";
		if (headerPriority.contains("5") || headerPriority.contains("Low"))
			return "低";
		return "普通";
	}

	/**
	 * 获取收件人地址
	 */
	public static String getReceiveAddress(MimeMessage msg, Message.RecipientType type) throws MessagingException {
		Address[] addresses = (type == null) ? msg.getAllRecipients() : msg.getRecipients(type);
		if (addresses == null || addresses.length == 0)
			throw new MessagingException("没有收件人!");

		StringBuilder receiveAddress = new StringBuilder();
		for (Address address : addresses) {
			receiveAddress.append(((InternetAddress) address).toUnicodeString()).append(",");
		}
		return receiveAddress.substring(0, receiveAddress.length() - 1);
	}

	/**
	 * 判断邮件是否包含附件
	 */
	public static boolean isContainAttachment(Part part) throws Exception {
		if (part.isMimeType("multipart/*")) {
			MimeMultipart multipart = (MimeMultipart) part.getContent();
			for (int i = 0; i < multipart.getCount(); i++) {
				BodyPart bodyPart = multipart.getBodyPart(i);
				String disp = bodyPart.getDisposition();
				if (disp != null && (disp.equalsIgnoreCase(Part.ATTACHMENT) || disp.equalsIgnoreCase(Part.INLINE))) {
					return true;
				} else if (bodyPart.isMimeType("multipart/*")) {
					return isContainAttachment(bodyPart);
				} else if (bodyPart.getContentType().contains("application") || bodyPart.getContentType().contains("name")) {
					return true;
				}
			}
		} else if (part.isMimeType("message/rfc822")) {
			return isContainAttachment((Part) part.getContent());
		}
		return false;
	}

	/**
	 * 保存附件
	 */
	public static void saveAttachment(Part part, String destDir, String fileName) throws Exception {
		if (part.isMimeType("multipart/*")) {
			Multipart multipart = (Multipart) part.getContent();
			for (int i = 0; i < multipart.getCount(); i++) {
				BodyPart bodyPart = multipart.getBodyPart(i);
				String decodeName = decodeText(bodyPart.getFileName());
				decodeName = StringUtils.isEmpty(decodeName) ? fileName : decodeName;
				if (bodyPart.getDisposition() != null || bodyPart.getContentType().contains("name") || bodyPart.getContentType().contains("application")) {
					saveFile(bodyPart.getInputStream(), destDir, decodeName);
				} else if (bodyPart.isMimeType("multipart/*")) {
					saveAttachment(bodyPart, destDir, fileName);
				}
			}
		} else if (part.isMimeType("message/rfc822")) {
			saveAttachment((Part) part.getContent(), destDir, fileName);
		}
	}

	/**
	 * 获取邮件文本内容
	 */
	public static void getMailTextContent(Part part, StringBuffer content) throws MessagingException, IOException {
		if (part.isMimeType("text/*") && !part.getContentType().contains("name")) {
			content.append(part.getContent().toString());
		} else if (part.isMimeType("message/rfc822")) {
			getMailTextContent((Part) part.getContent(), content);
		} else if (part.isMimeType("multipart/*")) {
			Multipart multipart = (Multipart) part.getContent();
			for (int i = 0; i < multipart.getCount(); i++) {
				getMailTextContent(multipart.getBodyPart(i), content);
			}
		}
	}

	/**
	 * 保存文件到指定目录
	 */
	private static void saveFile(InputStream is, String destDir, String fileName) throws Exception {
		FileUtil.createEmptyDirectory(destDir);
		try (BufferedInputStream bis = new BufferedInputStream(is); BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(new File(destDir + fileName)))) {
			byte[] buffer = new byte[1024];
			int len;
			while ((len = bis.read(buffer)) != -1) {
				bos.write(buffer, 0, len);
			}
		}
	}

	/**
	 * 文本解码
	 */
	public static String decodeText(String encodeText) throws Exception {
		return StringUtils.isEmpty(encodeText) ? "" : MimeUtility.decodeText(encodeText);
	}
}