package com.example.deleteemail;

import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Store;

import org.junit.Test;

/**
 * mvn -q -Dtest=com.example.deleteemail.AppTest\#testMail test
 */
public class AppTest {
    @Test
    public void mail() {
        try {
            Store store = EmailTest.getStore();
            Folder folder = store.getFolder("inbox");
            folder.open(Folder.READ_WRITE);

            System.out.printf("未读数为: %s\n", folder.getUnreadMessageCount());
            System.out.printf("新邮件数为: %s\n", folder.getNewMessageCount());
            System.out.printf("已删除邮件数为: %s\n", folder.getDeletedMessageCount());
            System.out.printf("邮件数为: %s\n", folder.getMessageCount());
            for (int j = 1; j < 30; j++) {
                Message[] messages = EmailTest.getMessages(folder, "tensorflow/tensorflow", -j);
                System.out.println("邮件数量为:" + messages.length);
                for (int i = 0; i < messages.length; i++) {
                    String subject = messages[i].getSubject();
                    if (subject.indexOf("flutter/flutter") == -1) {
                        continue;
                    }
                    messages[i].setFlag(Flags.Flag.DELETED, true);
                    System.out.println("前" + j + "天 第 " + (i + 1) + "封邮件的主题：" + subject);
                    folder.expunge();
                    folder.close(false);
                }
                store.close();
                break;
            }
        } catch (Exception e) {
            System.out.println("异常： " + e);
        }
    }
}
