package vn.compedia.website.auction.dto.PdfSignatureInfo;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PdfSignatureInfoDto {
    public Map<String, Object> entries = new HashMap<>();
    public String reason;
    public String name;
    public String subFilter;
    public String filter;
    public String contactInfo;
    public String location;
    public Date signDate;
    public boolean coversWholeDocument;
    public boolean selfSigned;
    public String signatureVerified;
    public CertificateInfoDto certificateInfo;
}
