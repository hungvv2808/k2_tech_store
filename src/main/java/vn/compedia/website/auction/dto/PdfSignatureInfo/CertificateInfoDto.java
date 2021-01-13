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
public class CertificateInfoDto {
    public String issuerDN;
    public String subjectDN;

    public Date notValidBefore;
    public Date notValidAfter;

    public String signAlgorithm;
    public String serial;

    public Map<String, String> issuerOIDs = new HashMap<>();

    public Map<String, String> subjectOIDs = new HashMap<>();
}
