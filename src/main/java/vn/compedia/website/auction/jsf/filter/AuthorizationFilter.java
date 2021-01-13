/*
 * Author: Thinh Hoang
 * Date: 09/2020
 * Company: Compedia Software
 * Email: thinhhv@compedia.vn
 * Personal Website: https://vnnib.com
 */

package vn.compedia.website.auction.jsf.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;
import vn.compedia.website.auction.controller.frontend.AuthorizationFEController;
import vn.compedia.website.auction.jsf.CookieHelper;
import vn.compedia.website.auction.model.Account;
import vn.compedia.website.auction.repository.AccountRepository;
import vn.compedia.website.auction.util.Constant;
import vn.compedia.website.auction.util.DbConstant;
import vn.compedia.website.auction.util.StringUtil;

import javax.inject.Inject;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AuthorizationFilter implements Filter {
	@Inject
	private AuthorizationFEController authorizationFEController;
	@Autowired
	private AccountRepository accountRepository;

	private static final Logger LOGGER = LoggerFactory.getLogger(AuthorizationFilter.class);

    @Override
	public void init(FilterConfig filterConfig) {
        LOGGER.info("Initializing Authorization Filter");
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;
		SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
		if (!authorizationFEController.hasLogged()) {
			String cookieAccount = CookieHelper.getCookieValue(req, Constant.COOKIE_ACCOUNT);
			String cookiePassword = CookieHelper.getCookieValue(req, Constant.COOKIE_PASSWORD);

			if (!StringUtils.isEmpty(cookieAccount) && !StringUtils.isEmpty(cookiePassword)) {
				Account account = accountRepository.findAccountByUsername(cookieAccount);

				if (account != null
						&& StringUtil.encryptPassword(account.getPassword(), account.getSalt() + account.getCreateDate()).equals(cookiePassword)
						&& account.getAccountStatus().equals(DbConstant.ACCOUNT_ACTIVE_STATUS)
						&& account.getRoleId() == DbConstant.ROLE_ID_USER) {
					//login
					authorizationFEController.resetAll();
					authorizationFEController.processLogin(account);
					LOGGER.info("[AuthorizationFilter] AutoLogin from Cookie, RequestURI: " + req.getRequestURI());
				} else {
					LOGGER.error("[AuthorizationFilter] login failed: {}", cookieAccount);
					CookieHelper.removeCookie(res, Constant.COOKIE_ACCOUNT);
					CookieHelper.removeCookie(res, Constant.COOKIE_PASSWORD);
				}
			}
		}

		chain.doFilter(request, response);
	}

	@Override
	public void destroy() {
		LOGGER.info("Destroy Authorization filter");
	}
}
