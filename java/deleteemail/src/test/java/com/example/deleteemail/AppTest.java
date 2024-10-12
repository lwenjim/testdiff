package com.example.deleteemail;

import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Store;
import javax.mail.internet.MimeMessage;

import org.junit.Test;

/**
 * mvn -q -Dtest=com.example.deleteemail.AppTest\#mail  test
 */
// start = 0, i = 0, 1000
// start = 1, i = 1000, 2000
// start = 2, i = 2000, 3000
// start = 3, i = 3000, 4000
// ...
// start = 11, i = 11000, 12000
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
                    final int start = j;
                    Thread t = new Thread(new Runnable() {
                        public void run() {
                            try {
                                for (int i = start * step; i < (start + 1) * step; i++) {
                                    String subject = messages[i].getSubject();
                                    String from = (messages[i].getFrom()[0]).toString();
                                    System.out.println(from);
                                    if (from.indexOf("notifications@github.com") == -1) {
                                        continue;
                                    }
                                    System.out.printf(
                                            "\n第 %d 封邮件\n标题: %s\n发件人：%s\n邮件总数: %d\n未读邮件数: " + "%d\n邮件是否已读: "
                                                    + "%s\n发送时间：%s\n邮件优先级：%s\n邮件大小：%skb\n",
                                            i + 1, subject, from, folder.getMessageCount(),
                                            folder.getUnreadMessageCount(),
                                            messages[i].getFlags().contains(Flags.Flag.SEEN) ? "是" : "否",
                                            EmailTest.getSentDate((MimeMessage) messages[i], null),
                                            EmailTest.getPriority((MimeMessage) messages[i]),
                                            ((MimeMessage) messages[i]).getSize() * 1024);
                                    messages[i].setFlag(Flags.Flag.DELETED, true);
                                }
                            } catch (Exception e) {
                                System.out.println("异常： " + e);
                            }
                        }
                    }, String.format("%d", j));
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
