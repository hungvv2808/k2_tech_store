package vn.compedia.website.auction.xml;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

@JsonIgnoreProperties(ignoreUnknown = true)
public class TokenAdministrativeUnits {
    private String access_token;
    private String scope;
    private String token_type;
    private String expires_in;
}
