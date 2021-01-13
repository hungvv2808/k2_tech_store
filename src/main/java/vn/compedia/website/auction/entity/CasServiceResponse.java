/*
 * Author: Thinh Hoang
 * Date: 10/2020
 * Company: Compedia Software
 * Email: thinhhv@compedia.vn
 * Personal Website: https://vnnib.com
 */

package vn.compedia.website.auction.entity;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@JacksonXmlRootElement(namespace = "cas", localName = "serviceResponse")
public class CasServiceResponse {
    @JacksonXmlProperty(namespace = "cas")
    private AuthenticationSuccess authenticationSuccess;
    @JacksonXmlProperty(namespace = "cas")
    private String authenticationFailure;

    @Getter @Setter
    @JacksonXmlRootElement(namespace = "cas", localName = "authenticationSuccess")
    public static class AuthenticationSuccess {
        @JacksonXmlProperty(namespace = "cas")
        private String user;
        @JacksonXmlProperty(namespace = "cas")
        private AuthenticationSuccess.Attributes attributes;

        @Getter @Setter
        @JacksonXmlRootElement(namespace = "cas", localName = "attributes")
        public static class Attributes {
            @JacksonXmlProperty(namespace = "cas")
            private String credentialType;
            @JacksonXmlProperty(namespace = "cas")
            private boolean isFromNewLogin;
            @JacksonXmlProperty(namespace = "cas")
            private Date authenticationDate;
            @JacksonXmlProperty(namespace = "cas")
            private String authenticationMethod;
            @JacksonXmlProperty(namespace = "cas")
            private String successfulAuthenticationHandlers;
            @JacksonXmlProperty(namespace = "cas")
            private String longTermAuthenticationRequestTokenUsed;
        }
    }
}
