package com.example.deleteemail;

import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Store;
import javax.mail.internet.MimeMessage;

import org.junit.Test;

/**
 * mvn -q -Dtest=com.example.deleteemail.AppTest\#testMail test
 */
public class AppTest {

    @Test
    public void mail() {
        while (true) {
            try {
                Store store = EmailTest.getStore();
                Folder folder = store.getFolder("inbox");
                folder.open(Folder.READ_WRITE);

                Message[] messages = EmailTest.getMessages(folder, "tensorflow/tensorflow", -1);
                System.out.println("邮件数量为:" + messages.length);
                if (messages.length == 0) {
                    break;
                }
                for (int i = 0; i < messages.length; i++) {
                    String subject = messages[i].getSubject();
                    String from = (messages[i].getFrom()[0]).toString();
                    if (from.indexOf("notifications@github.com") == -1) {
                        continue;
                    }
                    System.out.printf(
                            "\n第 %d 封邮件\n标题: %s\n发件人：%s\n邮件总数: %d\n未读邮件数: %d\n邮件是否已读: %s\n发送时间：%s\n邮件优先级：%s\n邮件大小：%skb\n",
                            i + 1, subject, from, folder.getMessageCount(), folder.getUnreadMessageCount(),
                            messages[i].getFlags().contains(Flags.Flag.SEEN) ? "是" : "否",
                            EmailTest.getSentDate((MimeMessage) messages[i], null),
                            EmailTest.getPriority((MimeMessage) messages[i]),
                            ((MimeMessage) messages[i]).getSize() * 1024);
                    messages[i].setFlag(Flags.Flag.DELETED, true);
                    if (i % 10 != 0) {
                        folder.expunge();
                        folder.close(false);
                        folder.open(Folder.READ_WRITE);
                    }
                }
                store.close();
            } catch (Exception e) {
                System.out.println("异常： " + e);
            }
        }
    }
}
