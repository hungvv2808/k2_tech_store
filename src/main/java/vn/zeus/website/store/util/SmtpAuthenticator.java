package vn.zeus.website.store.util;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

public class SmtpAuthenticator extends Authenticator {

    private String username = "user";
    private String pw = "pw";

    public SmtpAuthenticator(String username, String password) {
        super();
        this.username = username;
        this.pw = password;
    }

    @Override
    public PasswordAuthentication getPasswordAuthentication() {

        if ((username != null) && (username.length() > 0) && (pw != null)
                && (pw.length() > 0)) {

            return new PasswordAuthentication(username, pw);
        }

        return null;
    }

}
