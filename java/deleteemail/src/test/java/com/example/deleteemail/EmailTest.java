package com.example.deleteemail;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.NoSuchProviderException;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

import org.apache.commons.lang3.StringUtils;

public class EmailTest {
    // @Test
    // public void read() {
    //     try {
    //         Store store = getStore();
    //         Folder folder = store.getFolder("Inbox");
    //         folder.open(Folder.READ_WRITE);
    //         Message[] messages =
    //             folder.getMessages(folder.getMessageCount() - folder.getUnreadMessageCount() + 1,
    //                 folder.getMessageCount());
    //         System.out.println("邮件总数: " + folder.getMessageCount());
    //         parseFileMessage(messages);
    //         folder.setFlags(messages, new Flags(Flags.Flag.SEEN), true);
    //     } catch (Exception e) {
    //         System.out.println("异常： " + e);
    //     }
    // }

    //     @Test
    //     public void pop() {
    //         try {
    //             Store store = getStore();
    //             Folder folder = store.getFolder("Inbox");
    //
    //             folder.open(Folder.READ_WRITE);
    //             Message[] messages = getMessages(folder, "tensorflow/tensorflow", 1);
    //
    //             System.out.println("读取的邮件总数: " + messages.length);
    //             parseFileMessage(messages);
    //             folder.setFlags(messages, new Flags(Flags.Flag.SEEN), true);
    //             System.out.println("邮件解析任务执行完毕");
    //         } catch (Exception e) {
    //             System.out.println(e.getMessage());
    //         }
    //     }

    public static Message[] getMessages(Folder folder, Integer start, Integer end) throws MessagingException {
        // Calendar cal = Calendar.getInstance();
        // cal.add(Calendar.DATE, dayNum);
        // cal.set(Calendar.HOUR_OF_DAY, 0);
        // cal.set(Calendar.MINUTE, 0);
        // cal.set(Calendar.SECOND, 0);
        // cal.set(Calendar.MILLISECOND, 0);
        // Date mondayDate = cal.getTime();
        // SearchTerm comparisonTermGe = new SentDateTerm(ComparisonTerm.GE, mondayDate);
        // SearchTerm comparisonTermLe = new SentDateTerm(ComparisonTerm.LE, new Date());
        // SearchTerm comparisonAndTerm = new AndTerm(comparisonTermGe, comparisonTermLe);

        // SearchTerm text = new SearchTerm() {
        //     public boolean match(Message message) {
        //         try {
        //             if (message.getSubject().contains(keyword)) {
        //                 return true;
        //             }
        //         } catch (MessagingException ex) {
        //             ex.printStackTrace();
        //         }
        //         return false;
        //     }
        // };

        // Message[] messages = folder.search(comparisonAndTerm);
        Message[] messages = folder.getMessages(start, end);
        return messages;
    }

    public static Store getStore() throws NoSuchProviderException, MessagingException {
        Properties prop = new Properties();
        prop.setProperty("mail.store.protocol", "imap");
        prop.put("mail.imap.auth", "true");
        prop.put("mail.imap.socketFactory.port", "993");
        prop.put("mail.imap.starttls.enable", "true");
        prop.put("mail.imap.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        prop.put("mail.imap.ssl.protocols", "TLSv1.2");
        Session session = Session.getInstance(prop);
        Store store = session.getStore();
        System.out.println(System.getenv("EMAIL_ACCOUNT"));
        store.connect("imap.qq.com", System.getenv("EMAIL_ACCOUNT"), System.getenv("EMAIL_TOKEN"));
        return store;
    }

    public static void parseFileMessage(Message... messages) throws Exception {
        if (messages == null || messages.length < 1) {
            System.out.println("没有可读取邮件");
            return;
        }
        for (Message message : messages) {
            MimeMessage msg = (MimeMessage) message;
            System.out.println("------------------解析第" + msg.getMessageNumber() + "封邮件-------------------- ");
            System.out.println("主题: " + MimeUtility.decodeText(msg.getSubject()));
            System.out.println("发件人: " + getFrom(msg));
            System.out.println("收件人：" + getReceiveAddress(msg, null));
            System.out.println("发送时间：" + getSentDate(msg, null));
            System.out.println("是否已读：" + isSeen(msg));
            System.out.println("邮件优先级：" + getPriority(msg));
            System.out.println("是否需要回执：" + isReplySign(msg));
            System.out.println("邮件大小：" + msg.getSize() * 1024 + "kb");
            StringBuffer content = new StringBuffer(30);
            getMailTextContent(msg, content);
            System.out.println("邮件正文："+ (content.length() > 100 ? content.substring(0, 100) + "..." : content));

            System.out.println();
            boolean isContainerAttachment = isContainAttachment(msg);
            System.out.println("是否包含附件：" + isContainerAttachment);
            if (isContainerAttachment) {  
                saveAttachment(msg, "/tmp/email/File", msg.getFileName());
            }
            System.out.println("------------------第" + msg.getMessageNumber() + "封邮件解析结束-------------------- ");
        }
    }

    /**
     * 获得邮件发件人
     *
     * @param msg 邮件内容
     * @return 姓名 <Email地址>
     * @throws MessagingException
     * @throws UnsupportedEncodingException
     */
    public static String getFrom(MimeMessage msg)
            throws MessagingException, UnsupportedEncodingException {
            String from = "";
            Address[] froms = msg.getFrom();
            if (froms.length < 1) throw new MessagingException("没有发件人!");
            InternetAddress address = (InternetAddress) froms[0];
            String person = address.getPersonal();
            if (person != null) {
                person = MimeUtility.decodeText(person) + " ";
            } else {
                person = "";
            }
            from = person + "<" + address.getAddress() + ">";

            return from;
    }

    /**
     * 获得邮件发送时间
     *
     * @param msg 邮件内容
     * @return yyyy年mm月dd日 星期X HH:mm
     * @throws MessagingException
     */
    public static String getSentDate(MimeMessage msg, String pattern) throws MessagingException {
        Date receivedDate = msg.getSentDate();
        if (receivedDate == null) return "";
        if (pattern == null || "".equals(pattern)) pattern = "yyyy年MM月dd日 E HH:mm ";
        return new SimpleDateFormat(pattern).format(receivedDate);
    }

    /**
     * 判断邮件是否已读
     *
     * @param msg 邮件内容
     * @return 如果邮件已读返回true,否则返回false
     * @throws MessagingException
     */
    public static boolean isSeen(MimeMessage msg) throws MessagingException {
        return msg.getFlags().contains(Flags.Flag.SEEN);
    }

    /**
     * 判断邮件是否需要阅读回执
     *
     * @param msg 邮件内容
     * @return 需要回执返回true,否则返回false
     * @throws MessagingException
     */
    public static boolean isReplySign(MimeMessage msg) throws MessagingException {
        boolean replySign = false;
        String[] headers = msg.getHeader("Disposition-Notification-To");
        if (headers != null) replySign = true;
        return replySign;
    }

    /**
     * 获得邮件的优先级
     *
     * @param msg 邮件内容
     * @return 1(High):紧急 3:普通(Normal) 5:低(Low)
     * @throws MessagingException
     */
    public static String getPriority(MimeMessage msg) throws MessagingException {
        String priority = "普通";
        String[] headers = msg.getHeader("X-Priority");
        if (headers != null) {
            String headerPriority = headers[0];
            if (headerPriority.contains("1") || headerPriority.contains("High"))
                priority = "紧急";
            else if (headerPriority.contains("5") || headerPriority.contains("Low"))
                priority = "低";
            else
                priority = "普通";
        }
        return priority;
    }

    /**
     * 根据收件人类型，获取邮件收件人、抄送和密送地址。如果收件人类型为空，则获得所有的收件人
     * <p>
     * Message.RecipientType.TO 收件人
     * </p>
     * <p>
     * Message.RecipientType.CC 抄送
     * </p>
     * <p>
     * Message.RecipientType.BCC 密送
     * </p>
     *
     * @param msg  邮件内容
     * @param type 收件人类型
     * @return 收件人1 <邮件地址1>, 收件人2 <邮件地址2>, ...
     * @throws MessagingException
     */
    public static String getReceiveAddress(MimeMessage msg, Message.RecipientType type)
            throws MessagingException {
            StringBuilder receiveAddress = new StringBuilder();
            Address[] addresss;
            if (type == null) {
                addresss = msg.getAllRecipients();
            } else {
                addresss = msg.getRecipients(type);
            }

            if (addresss == null || addresss.length < 1) throw new MessagingException("没有收件人!");
            for (Address address : addresss) {
                InternetAddress internetAddress = (InternetAddress) address;
                receiveAddress.append(internetAddress.toUnicodeString()).append(",");
            }
            receiveAddress.deleteCharAt(receiveAddress.length() - 1); 
            return receiveAddress.toString();
    }

    /**
     * 判断邮件中是否包含附件
     *
     * @return 存在附件返回true，不存在返回false
     */
    public static boolean isContainAttachment(Part part) throws Exception {
        boolean flag = false;
        if (part.isMimeType("multipart/*")) {
            MimeMultipart multipart = (MimeMultipart) part.getContent();
            int partCount = multipart.getCount();
            for (int i = 0; i < partCount; i++) {
                BodyPart bodyPart = multipart.getBodyPart(i);
                String disp = bodyPart.getDisposition();
                if (disp != null  && (disp.equalsIgnoreCase(Part.ATTACHMENT)  || disp.equalsIgnoreCase(Part.INLINE))) {
                    flag = true;
                } else if (bodyPart.isMimeType("multipart/*")) {
                    flag = isContainAttachment(bodyPart);
                } else {
                    String contentType = bodyPart.getContentType();
                    if (contentType.contains("application")) {
                        flag = true;
                    }

                    if (contentType.contains("name")) {
                        flag = true;
                    }
                }

                if (flag) break;
            }
        } else if (part.isMimeType("message/rfc822")) {
            flag = isContainAttachment((Part) part.getContent());
        }
        return flag;
    }

    /**
     * 保存文件
     *
     * @param destDir  文件目录
     * @param fileName 文件名
     * @throws Exception 异常
     */
    public static void saveAttachment(Part part, String destDir, String fileName) throws Exception {
        if (part.isMimeType("multipart/*")) {
            // 复杂体邮件
            Multipart multipart = (Multipart) part.getContent();
            // 复杂体邮件包含多个邮件体
            int partCount = multipart.getCount();
            for (int i = 0; i < partCount; i++) {
                // 获得复杂体邮件中其中一个邮件体
                BodyPart bodyPart = multipart.getBodyPart(i);
                // 迭代处理邮件体，直到附件为止
                String disp = bodyPart.getDisposition();
                String decodeName = decodeText(bodyPart.getFileName());
                decodeName = StringUtils.isEmpty(decodeName) ? fileName : decodeName;
                if (disp != null && (disp.equalsIgnoreCase(Part.ATTACHMENT)|| disp.equalsIgnoreCase(Part.INLINE))) {
                    saveFile(bodyPart.getInputStream(), destDir, decodeName);
                } else if (bodyPart.isMimeType("multipart/*")) {
                    saveAttachment(bodyPart, destDir, fileName);
                } else {
                    String contentType = bodyPart.getContentType();
                    if (contentType.contains("name") || contentType.contains("application")) {
                        saveFile(bodyPart.getInputStream(), destDir, decodeName);
                    }
                }
            }
        } else if (part.isMimeType("message/rfc822")) {
            saveAttachment((Part) part.getContent(), destDir, fileName);
        }
    }

    /**
     * 获得邮件文本内容
     *
     * @param part    邮件体
     * @param content 存储邮件文本内容的字符串
     * @throws MessagingException
     * @throws IOException
     */
    public static void getMailTextContent(Part part, StringBuffer content)
            throws MessagingException, IOException {
            // 如果是文本类型的附件，通过getContent方法可以取到文本内容，但这不是我们需要的结果，所以在这里要做判断
            boolean isContainTextAttach = part.getContentType().indexOf("name") > 0;
            if (part.isMimeType("text/*") && !isContainTextAttach) {
                content.append(part.getContent().toString());
            } else if (part.isMimeType("message/rfc822")) {
                getMailTextContent((Part) part.getContent(), content);
            } else if (part.isMimeType("multipart/*")) {
                Multipart multipart = (Multipart) part.getContent();
                int partCount = multipart.getCount();
                for (int i = 0; i < partCount; i++) {
                    BodyPart bodyPart = multipart.getBodyPart(i);
                    getMailTextContent(bodyPart, content);
                }
            }
    }

    /**
     * 读取输入流中的数据保存至指定目录
     *
     * @param is       输入流
     * @param fileName 文件名
     * @param destDir  文件存储目录
     */
    private static void saveFile(InputStream is, String destDir, String fileName) throws Exception {
        createEmptyDirectory(destDir);
        BufferedInputStream bis = new BufferedInputStream(is);
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(new File(destDir + fileName)));
        int len;
        while ((len = bis.read()) != -1) {
            bos.write(len);
            bos.flush();
        }
        bos.close();
        bis.close();
    }

    /**
     * 创建一个空目录
     */
    public static void createEmptyDirectory(String directoryPath) {
        File file = new File(directoryPath);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    /**
     * 文本解码
     */
    public static String decodeText(String encodeText) throws Exception {
        if (encodeText == null || "".equals(encodeText)) {
            return "";
        } else {
            return MimeUtility.decodeText(encodeText);
        }
    }
}

