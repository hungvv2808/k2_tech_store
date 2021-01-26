
package vn.tech.website.store.util;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vn.tech.website.store.dto.common.MailDto;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.util.*;

@Getter
@Setter
public class EmailUtil implements Runnable {
    private static final String UTF8_CHARACTER = "text/html; charset=UTF-8";

    private static EmailUtil INSTANCE = null;


    private SmtpAuthenticator smtpAuthenticator;
    private Queue<MailDto> mailDtoQueue;

    private static Logger log = LoggerFactory.getLogger(EmailUtil.class);

    public static EmailUtil getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new EmailUtil();
            new Thread(INSTANCE).start();
        }
        return INSTANCE;
    }

    public EmailUtil() {
        String email = PropertiesUtil.getEmailProperty("mail.user");
        String password = PropertiesUtil.getEmailProperty("mail.password");
        smtpAuthenticator = new SmtpAuthenticator(email, password);
        mailDtoQueue = new LinkedList<>();
    }

    public boolean sendPasswordUserEmail(String loaiTaiKhoan, String emailTo, String password, String userName, String fullName) {
        String subject = PropertiesUtil.getProperty("email.createUser.subject");
        String content = PropertiesUtil.getProperty("email.createUser.content")
                .replace("{LOAI_TAI_KHOAN}", loaiTaiKhoan)
                .replace("{TEN_TAI_KHOAN}", emailTo)
                .replace("{FULL_NAME}", fullName)
                .replace("{USER_NAME}", userName)
                .replace("{MAT_KHAU}", password);
        return mailDtoQueue.add(new MailDto(emailTo, subject, content));
    }
    public boolean sendPasswordAdminEmail(String loaiTaiKhoan, String emailTo, String password,String fullname, String userName) {
        String subject = PropertiesUtil.getProperty("email.createUser.subject");
        String content = PropertiesUtil.getProperty("email.createAdmin.content")
                .replace("{LOAI_TAI_KHOAN}", loaiTaiKhoan)
                .replace("{USER_NAME}", userName)
                .replace("{TEN_TAI_KHOAN}", emailTo)
                .replace("{FULL_NAME}", fullname)
                .replace("{MAT_KHAU}", password);
        return mailDtoQueue.add(new MailDto(emailTo, subject, content));
    }

    public boolean onLockAccount(String emailTo, String fullname) {
        String subject = PropertiesUtil.getProperty("email.blockAccount.subject");
        String content = PropertiesUtil.getProperty("email.reasonLock.content").replace("{USER_NAME}", fullname);
        return mailDtoQueue.add(new MailDto(emailTo, subject, content));
    }

    public boolean onUnLockAccount(String emailTo, String fullname) {
        String subject = PropertiesUtil.getProperty("email.unBlockAccount.subject");
        String content = PropertiesUtil.getProperty("email.notiUnlock.content").replace("{USER_NAME}", fullname);
        return mailDtoQueue.add(new MailDto(emailTo, subject, content));
    }

    public boolean sendCreateUserEmail(String emailTo, String link, String fullName) {
        String subject = PropertiesUtil.getProperty("email.createNewUser.subject");
        String content = PropertiesUtil.getProperty("email.createNewUser.content")
                .replace("{TEN_TAI_KHOAN}", fullName)
                .replace("{LINK}", link);
        return mailDtoQueue.add(new MailDto(emailTo, subject, content));
    }

    public boolean sendRefuseRegister(String emailTo, String assetName, String regulationCode, String reason, String fullName) {
        String subject = PropertiesUtil.getProperty("email.createRefuseRegister.subject");
        String content = PropertiesUtil.getProperty("email.createRefuseRegister.content")
                .replace("{TEN_TAI_KHOAN}", fullName)
                .replace("{ASSET_NAME}", assetName)
                .replace("{REGULATION_CODE}", regulationCode)
                .replace("{REASON}", reason);
        return mailDtoQueue.add(new MailDto(emailTo, subject, content));
    }

    public boolean sendLostPasswordEmail(String emailTo, String code, String fullName) {
        String subject = PropertiesUtil.getProperty("email.lostPassword.subject");
        String content = PropertiesUtil.getProperty("email.lostPassword.content")
                .replace("{USER_NAME}", fullName)
                .replace("{CODE}", code);
        return mailDtoQueue.add(new MailDto(emailTo, subject, content));
    }

    public boolean sendResetPasswordUserEmail(String emailTo,String username, String password, String fullname) {
        String subject = PropertiesUtil.getProperty("email.resetPassword.subject");
        String content = PropertiesUtil.getProperty("email.resetPassword.content")
                .replace("{USER_NAME}", username)
                .replace("{FULL_NAME}", fullname)
                .replace("{PASSWORD}", password);
        return mailDtoQueue.add(new MailDto(emailTo, subject, content));
    }


    public boolean sendBillPaid(String emailTo, String filePath, String fileName, String name, String assetName, String regulationCode, String username) {
        String subject = PropertiesUtil.getProperty("email.sendBill.subject");
        String content = PropertiesUtil.getProperty("email.sendBill.content")
                .replace("{USER_NAME}", username)
                .replace("{ASSET_NAME}", assetName)
                .replace("{CODE}", regulationCode)
                .replace("{NAME}", fileName);
        return mailDtoQueue.add(new MailDto(emailTo, subject, content, fileName, filePath));
    }

    public boolean sendBillRefund(String emailTo, String filePath, String fileName, String name, String assetName, String regulationCode, String username) {
        String subject = PropertiesUtil.getProperty("email.sendBillRefund.subject");
        String content = PropertiesUtil.getProperty("email.sendBillRefund.content")
                .replace("{USER_NAME}", username)
                .replace("{ASSET_NAME}", assetName)
                .replace("{CODE}", regulationCode)
                .replace("{NAME}", fileName);
        return mailDtoQueue.add(new MailDto(emailTo, subject, content, fileName, filePath));
    }

    public boolean sendBillTransactionControl(String emailTo, String fullname) {
        String subject = PropertiesUtil.getProperty("email.sendBillTransactionControl.subject");
        String content = PropertiesUtil.getProperty("email.sendBillTransactionControl.content")
                .replace("{USER_NAME}", fullname);
        return mailDtoQueue.add(new MailDto(emailTo, subject, content));
    }

    public boolean sendReasonCancelRegulation(String emailTo, String filePath, String fileName, String name, String code, String reasonCancel) {
        String subject = PropertiesUtil.getProperty("email.sendRegulationCancel.subject");
        String content = PropertiesUtil.getProperty("email.sendRegulationCancel.content")
                .replace("{CODE}", code)
                .replace("{REASON_CANCEL}", reasonCancel)
                .replace("{NAME}", name);
        return mailDtoQueue.add(new MailDto(emailTo, subject, content, fileName, filePath));
    }

    public boolean sendReasonCancelAsset(String emailTo, String filePath, String fileName, String name, String code, String regulationCode, String reasonCancel) {
        String subject = PropertiesUtil.getProperty("email.sendAssetCancel.subject");
        String content = PropertiesUtil.getProperty("email.sendAssetCancel.content")
                .replace("{CODE}", code)
                .replace("{REGULATION_CODE}", regulationCode)
                .replace("{REASON_CANCEL}", reasonCancel)
                .replace("{NAME}", name);
        return mailDtoQueue.add(new MailDto(emailTo, subject, content, fileName, filePath));
    }

    public boolean sendNotificationNotChecked(String emailTo, String name, String assetName, String regulationCode){
        String subject = PropertiesUtil.getProperty("email.notificationNotChecked.subject")
                .replace("{NAME}", name);
        String content = PropertiesUtil.getProperty("email.notificationNotChecked.content")
                .replace("{NAME}", name)
                .replace("{ASSET_NAME}", assetName)
                .replace("{REGULATION_CODE}", regulationCode);
        return mailDtoQueue.add(new MailDto(emailTo, subject, content));
    }

    public boolean sendNotificationAccepted(String emailTo, String name, String assetName, String regulationCode, String code){
        String subject = PropertiesUtil.getProperty("email.notificationAccepted.subject");
        String content = PropertiesUtil.getProperty("email.notificationAccepted.content")
                .replace("{TEN_TAI_KHOAN}", name)
                .replace("{ASSET_NAME}", assetName)
                .replace("{REGULATION_CODE}", regulationCode)
                .replace("{REASON}", code);
        return mailDtoQueue.add(new MailDto(emailTo, subject, content));
    }

    public boolean sendResultStore(String emailTo, String userName, Boolean status, String regulationCode, String assetName, Date startTime, Date endTime, Long price, Date timeAccept){
        String content;
        if (status == DbConstant.ASSET_MANAGEMENT_ENDING_GOOD) {
            content = PropertiesUtil.getProperty("email.sendResultStoreSuccess.content")
                    .replace("{USER_NAME}", userName)
                    .replace("{STATUS}", "Đấu giá thành công")
                    .replace("{REGULATION_CODE}", regulationCode)
                    .replace("{ASSET_NAME}", assetName)
                    .replace("{START_TIME}", Objects.requireNonNull(DateUtil.formatToPattern(startTime, DateUtil.HHMMSS_DDMMYYYY)))
                    .replace("{END_TIME}", Objects.requireNonNull(DateUtil.formatToPattern(endTime, DateUtil.HHMMSS_DDMMYYYY)))
                    .replace("{PRICE}", StringUtil.formatCurrency(price) + " VNĐ")
                    .replace("{ACCEPT_TIME}", Objects.requireNonNull(DateUtil.formatToPattern(timeAccept, DateUtil.HHMMSS_DDMMYYYY)));
        } else {
            content = PropertiesUtil.getProperty("email.sendResultStoreFaile.content")
                    .replace("{USER_NAME}", userName)
                    .replace("{STATUS}", "Đấu giá không thành")
                    .replace("{REGULATION_CODE}", regulationCode)
                    .replace("{ASSET_NAME}", assetName)
                    .replace("{START_TIME}", Objects.requireNonNull(DateUtil.formatToPattern(startTime, DateUtil.HHMMSS_DDMMYYYY)))
                    .replace("{END_TIME}", Objects.requireNonNull(DateUtil.formatToPattern(endTime, DateUtil.HHMMSS_DDMMYYYY)));
        }
        String subject = PropertiesUtil.getProperty("email.sendResultStore.subject");
        return mailDtoQueue.add(new MailDto(emailTo, subject, content));
    }

    private boolean send(MailDto mailDto) {
        try {
            Properties emailProps = new Properties();
            emailProps.load(PropertiesUtil.class.getResourceAsStream("/email.properties"));
            // Get the default Session object.
            Session session = Session.getDefaultInstance(emailProps, smtpAuthenticator);
            // Create a default MimeMessage object.
            MimeMessage message = new MimeMessage(session);
            // Set From: header field of the header.
            message.setFrom(new InternetAddress(PropertiesUtil.getEmailProperty("mail.user")));

            // Set To: header field of the header.
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(mailDto.getEmailTo()));
            // Set Subject: header field
            message.setSubject(mailDto.getSubject(), "UTF-8");
            // Send the actual HTML message, as big as you like
            message.setHeader("Content-Type", UTF8_CHARACTER);

            if (StringUtils.isNotBlank(mailDto.getFileName())) {
                // Create a multipar message
                Multipart multipart = new MimeMultipart();

                // Create the message part
                BodyPart bodyPart = new MimeBodyPart();
                bodyPart.setContent(mailDto.getContent(), UTF8_CHARACTER);
                multipart.addBodyPart(bodyPart);

                // Create attached file
                bodyPart = new MimeBodyPart();
                DataSource source = new FileDataSource(mailDto.getFilePath());
                bodyPart.setDataHandler(new DataHandler(source));
                bodyPart.setFileName(mailDto.getFileName());
                multipart.addBodyPart(bodyPart);

                message.setContent(multipart);
            } else {
                message.setContent(mailDto.getContent(), UTF8_CHARACTER);
            }
            // Send message
            Transport.send(message);
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(1000);
                MailDto mailDto = mailDtoQueue.poll();
                if (mailDto != null) {
                    String rs = send(mailDto) ? "success" : "fail";
                    log.info("Send mail is " + rs + " (" + mailDto + ")");
                }
            } catch (Exception e) {
                log.error("Lỗi", e);
            }
        }
    }

    public static void main(String[] args) {
      EmailUtil.getInstance().sendResultStore("thinhhoang3030@gmail.com", "truongvvt", true, "QC000012", "Tài sản A", new Date() , new Date(), 10000000L, new Date());
    }
}
