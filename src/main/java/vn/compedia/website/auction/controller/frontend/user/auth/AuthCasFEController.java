/*
 * Author: Thinh Hoang
 * Date: 09/2020
 * Company: Compedia Software
 * Email: thinhhv@compedia.vn
 * Personal Website: https://vnnib.com
 */

package vn.compedia.website.auction.controller.frontend.user.auth;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import okhttp3.*;
import org.apache.commons.lang3.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import vn.compedia.website.auction.controller.frontend.AuthorizationFEController;
import vn.compedia.website.auction.controller.frontend.common.FacesNoticeController;
import vn.compedia.website.auction.entity.CasServiceResponse;
import vn.compedia.website.auction.model.Account;
import vn.compedia.website.auction.repository.AccountRepository;
import vn.compedia.website.auction.util.*;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Date;

@Log4j2
@Named
@Scope(value = "session")
@Getter
@Setter
public class AuthCasFEController {
    @Inject
    HttpServletRequest request;
    @Inject
    private AuthorizationFEController authorizationFEController;
    @Inject
    private FacesNoticeController facesNoticeController;
    @Autowired
    private AccountRepository accountRepository;

    @Value("${cas.server.url}")
    private String casSecurityServerUrl;
    @Value("${cas.server.login.url}")
    private String casSecurityServerLogin;
    @Value("${cas.client.frontend.login-sso.url}")
    private String casSecurityClientCallback;

    private final String PARAM_TICKET = "ticket";
    private final String URL_VALIDATE = "/serviceValidate";

    public void initData() {

        // check logged
        if (authorizationFEController.hasLogged()) {
            FacesUtil.redirect("/frontend/index.xhtml");
            return;
        }

        // CALLBACK: check ticket
        String ticket = request.getParameter(PARAM_TICKET);
        if (ticket != null) {
            try {
                String urlCasValidate = String.format(
                        "%s%s?service=%s&%s=%s",
                        casSecurityServerUrl,
                        URL_VALIDATE,
                        URLEncoder.encode(casSecurityClientCallback, "UTF-8"),
                        PARAM_TICKET,
                        ticket
                );

                // validate ticket
                CasServiceResponse serviceResponse = callCasServer(urlCasValidate);
                if (serviceResponse.getAuthenticationFailure() != null) {
                    String error = StringEscapeUtils.unescapeHtml4(serviceResponse.getAuthenticationFailure());
                    throw new Exception(error);
                }

                String email = serviceResponse.getAuthenticationSuccess().getUser();
                // check exists account + create new account
                String username = email.split("@")[0];
                Account account = accountRepository.findByEmail(email);
                if (account == null) {
                    Account accountUsername = accountRepository.findAccountByUsername(username);
                    if (accountUsername != null) {
                        username = username + "-" + StringUtil.generateSalt().toLowerCase();
                    }
                    account = new Account();
                    account.setEmail(email);
                    account.setUsername(username);
                    account.setFullName(email);
                    account.setAccountStatus(DbConstant.ACCOUNT_ACTIVE_STATUS);
                    account.setFirstTimeLogin(true);
                    account.setLoginFromSso(true);
                    account.setTimeToChangePassword(DateUtil.plusDay(new Date(), 50 * 365));
                    account.setRoleId(DbConstant.ROLE_ID_USER);
                    account.setCreateBy(0L);
                    account.setUpdateBy(0L);
                    accountRepository.save(account);
                } else {
                    if (account.getRoleId() != DbConstant.ROLE_ID_USER) {
                        throw new Exception("Không thể đăng nhập bằng email này (" + account.getEmail() + ")");
                    }
                    // validate account status
                    if (account.getAccountStatus().equals(DbConstant.ACCOUNT_INACTIVE_STATUS)) {
                        throw new Exception("Tài khoản này chưa được kích hoạt");
                    }
                    if (account.getAccountStatus().equals(DbConstant.ACCOUNT_LOCK_STATUS)) {
                        throw new Exception("Tài khoản này đã bị khóa");
                    }
                    if (account.getAccountStatus().equals(DbConstant.ACCOUNT_DELETE_STATUS)) {
                        throw new Exception("Tài khoản này đã bị xóa");
                    }
                }

                // process login
                authorizationFEController.processLogin(account);

                // Redirect to home page
                FacesUtil.addSuccessMessage(Constant.ERROR_MESSAGE_ID,"Đăng nhập thành công");
                FacesUtil.redirect("/frontend/index.xhtml");

            } catch (Exception e) {
                log.error(e);
                setErrorForm(e.getMessage());
                FacesUtil.redirect("/frontend/index.xhtml");
            }
            return;
        }

        // LOGIN: redirect to cas server
        try {
            String urlRedirect = String.format(
                    "%s?service=%s",
                    casSecurityServerLogin,
                    URLEncoder.encode(casSecurityClientCallback, "UTF-8"));
            FacesUtil.externalRedirect(urlRedirect);
        } catch (Exception e) {
            log.error(e);
            setErrorForm("Lỗi chuyển hướng");
            FacesUtil.redirect("/frontend/index.xhtml");
        }
    }

    private CasServiceResponse callCasServer(String url) throws Exception {
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();
        Call call = okHttpClient.newCall(request);
        try (Response response = call.execute();
             ResponseBody body = response.body()) {
            if (body == null) {
                log.error("[CAS] body null");
                throw new Exception("Lỗi phản hồi");
            }

            XmlMapper xmlMapper = new XmlMapper();
            return xmlMapper.readValue(body.string(), CasServiceResponse.class);
        } catch (IOException e) {
            log.error(e);
            throw new Exception("Lỗi kết nối");
        }
    }

    private void setErrorForm(String str) {
        //FacesUtil.addErrorMessage(str);
        facesNoticeController.addErrorMessage(str);
    }
}
