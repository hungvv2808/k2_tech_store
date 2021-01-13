package vn.compedia.website.auction.controller.admin.common;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSString;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.PDSignature;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cms.*;
import org.bouncycastle.cms.jcajce.JcaSimpleSignerInfoVerifierBuilder;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.util.Store;
import org.bouncycastle.util.StoreException;
import org.primefaces.event.FileUploadEvent;
import org.springframework.context.annotation.Scope;
import vn.compedia.website.auction.controller.admin.BaseController;
import vn.compedia.website.auction.dto.PdfSignatureInfo.CertificateInfoDto;
import vn.compedia.website.auction.dto.PdfSignatureInfo.PdfSignatureInfoDto;
import vn.compedia.website.auction.entity.EScope;
import vn.compedia.website.auction.util.Constant;
import vn.compedia.website.auction.util.FileUtil;

import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.naming.InvalidNameException;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.*;
import java.util.*;

@Named
@Scope(value = "session")
@Log4j2
@Getter
@Setter
public class PdfSignatureInfoController extends BaseController {
    private String filePath;
    private String fileName;
    private List<PdfSignatureInfoDto> info;
    private PdfSignatureInfoDto pdfSignatureInfoDto;
    private CertificateInfoDto certificateInfoDto;
    public void initData(){
        if(!FacesContext.getCurrentInstance().isPostback()){
            init();
            resetAll();
        }
    }

    public void resetAll(){
        this.filePath = filePath;
        if (StringUtils.isNotBlank(filePath)) {
            this.fileName = FileUtil.getFilenameFromFilePath(filePath);
        }
        info = new ArrayList<>();
        pdfSignatureInfoDto = new PdfSignatureInfoDto();
        certificateInfoDto = new CertificateInfoDto();
    }

    public void upload(FileUploadEvent f){
        if (!FileUtil.isAcceptFilePDFType(f.getFile())) {
            setErrorForm("File đăng tải không đúng định dạng, những định dạng được phép đăng tải " + FileUtil.getAccpetFilePDFString());
            return;
        }
        if (f.getFile().getSize() > Constant.MAX_FILE_SIZE) {
            setErrorForm("Dung lượng file quá lớn. Dung lượng tối đa " + Constant.MAX_FILE_SIZE / 1000000 + "Mb");
            return;
        }
        try {
            InputStream is = f.getFile().getInputstream();
            info = getPdfSignatureInfoDto(is);
            filePath = FileUtil.saveFile(f.getFile());
            fileName = f.getFile().getFileName();
        } catch (IOException | InvalidNameException | CertificateException | NoSuchAlgorithmException | NoSuchProviderException e) {
            e.printStackTrace();
        }
    }

    private  byte[] getbyteArray(InputStream is) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        byte[] buffer = new byte[1024];
        int len;
        while ((len = is.read(buffer)) > -1 ) {
            baos.write(buffer, 0, len);
        }
        baos.flush();

        return baos.toByteArray();
    }


    public List<PdfSignatureInfoDto> getPdfSignatureInfoDto(InputStream is) throws IOException, CertificateException,
            NoSuchAlgorithmException, NoSuchProviderException, InvalidNameException {

        byte[] byteArray = getbyteArray(is);
        return getPdfSignatureInfoDto(byteArray);
    }

    public  List<PdfSignatureInfoDto> getPdfSignatureInfoDto(byte[] byteArray ) throws IOException, CertificateException,
            NoSuchAlgorithmException, NoSuchProviderException, InvalidNameException {

        List<PdfSignatureInfoDto> lpsi = new ArrayList<PdfSignatureInfoDto>();

        // Try to open the input file as PDF
        try (PDDocument document = PDDocument.load(new ByteArrayInputStream(byteArray))) {
            // Get Signature dictionaries of PDF
            for (PDSignature sig : document.getSignatureDictionaries()) {
                PdfSignatureInfoDto psi = new PdfSignatureInfoDto();
                lpsi.add(psi);

                COSDictionary sigDict = sig.getCOSObject();
                COSString contents = (COSString) sigDict.getDictionaryObject(COSName.CONTENTS);

                Set<Map.Entry<COSName, COSBase>> entries = sigDict.entrySet();
                for(Map.Entry<COSName, COSBase> entry: entries) {
                    // Don't return contents
                    if(!entry.getKey().equals(COSName.CONTENTS)) {
                        psi.entries.put(entry.getKey().getName(), entry.getValue().toString());
                    }
                }

                psi.reason = sig.getReason();
                psi.name = sig.getName();
                psi.signDate = sig.getSignDate().getTime();
                psi.subFilter= sig.getSubFilter();
                psi.contactInfo = sig.getContactInfo();
                psi.filter = sig.getFilter();
                psi.location = sig.getLocation();

                // download the signed content
                byte[] buf;
                buf = sig.getSignedContent(new ByteArrayInputStream(byteArray));

                int[] byteRange = sig.getByteRange();
                if (byteRange.length != 4) {
                    throw new IOException("Signature byteRange must have 4 items");
                } else {
                    long fileLen = byteArray.length;
                    long rangeMax = byteRange[2] + (long) byteRange[3];
                    // multiply content length with 2 (because it is in hex in the PDF) and add 2 for < and >
                    int contentLen = sigDict.getString(COSName.CONTENTS).length() * 2 + 2;
                    if (fileLen != rangeMax || byteRange[0] != 0 || byteRange[1] + contentLen != byteRange[2]) {
                        // a false result doesn't necessarily mean that the PDF is a fake
                        psi.coversWholeDocument = false;
                    } else {
                        psi.coversWholeDocument = true;
                    }
                }

                String subFilter = sig.getSubFilter();
                if (subFilter != null) {
                    switch (subFilter) {
                        case "adbe.pkcs7.detached":
                            verifyPKCS7(buf, contents, sig, psi);

                            //TODO check certificate chain, revocation lists, timestamp...
                            break;
                        case "adbe.pkcs7.sha1": {
                            COSString certString = (COSString) sigDict.getDictionaryObject( COSName.CONTENTS);
                            byte[] certData = certString.getBytes();
                            CertificateFactory factory = CertificateFactory.getInstance("X.509");
                            ByteArrayInputStream certStream = new ByteArrayInputStream(certData);
                            Collection<? extends Certificate> certs = factory.generateCertificates(certStream);
                            byte[] hash = MessageDigest.getInstance("SHA1").digest(buf);
                            verifyPKCS7(hash, contents, sig, psi);

                            //TODO check certificate chain, revocation lists, timestamp...
                            break;
                        }
                        case "adbe.x509.rsa_sha1": {
                            COSString certString = (COSString) sigDict.getDictionaryObject( COSName.getPDFName("Cert"));
                            byte[] certData = certString.getBytes();
                            CertificateFactory factory = CertificateFactory.getInstance("X.509");
                            ByteArrayInputStream certStream = new ByteArrayInputStream(certData);
                            Collection<? extends Certificate> certs = factory.generateCertificates(certStream);

                            //TODO verify signature
                            psi.signatureVerified="Unable to verify adbe.x509.rsa_sha1 subfilter";
                            break;
                        }
                        default:
                            throw new IOException("Unknown certificate type " + subFilter);

                    }
                } else {
                    throw new IOException("Missing subfilter for cert dictionary");
                }
            }
        } catch (CMSException | OperatorCreationException ex) {
            throw new IOException(ex);
        }

        return lpsi;
    }

    /**
     * Verify a PKCS7 signature.
     *
     * @param byteArray the byte sequence that has been signed
     * @param contents the /Contents field as a COSString
     * @param sig the PDF signature (the /V dictionary)
     * @throws CertificateException
     * @throws CMSException
     * @throws StoreException
     * @throws OperatorCreationException
     */
    private  void verifyPKCS7(byte[] byteArray, COSString contents, PDSignature sig, PdfSignatureInfoDto psi)
            throws CMSException, CertificateException, StoreException, OperatorCreationException,
            NoSuchAlgorithmException, NoSuchProviderException, InvalidNameException {
        CMSProcessable signedContent = new CMSProcessableByteArray(byteArray);
        CMSSignedData signedData = new CMSSignedData(signedContent, contents.getBytes());
        Store certificatesStore = signedData.getCertificates();
        Collection<SignerInformation> signers = signedData.getSignerInfos().getSigners();
        SignerInformation signerInformation = signers.iterator().next();
        Collection matches = certificatesStore.getMatches(signerInformation.getSID());
        X509CertificateHolder certificateHolder = (X509CertificateHolder) matches.iterator().next();
        X509Certificate certFromSignedData = new JcaX509CertificateConverter().getCertificate(certificateHolder);
        

        CertificateInfoDto ci = new CertificateInfoDto();
        psi.certificateInfo = ci;
        ci.issuerDN = certFromSignedData.getIssuerDN().toString();
        ci.subjectDN = certFromSignedData.getSubjectDN().toString();

        ci.notValidAfter = certFromSignedData.getNotAfter();
        ci.notValidBefore = certFromSignedData.getNotBefore();

        ci.signAlgorithm = certFromSignedData.getSigAlgName();
        ci.serial = certFromSignedData.getSerialNumber().toString();

        LdapName ldapDN = new LdapName(ci.issuerDN);
        for(Rdn rdn: ldapDN.getRdns()) {
            ci.issuerOIDs.put(rdn.getType(), rdn.getValue().toString());
        }

        ldapDN = new LdapName(ci.subjectDN);
        for(Rdn rdn: ldapDN.getRdns()) {
            ci.subjectOIDs.put(rdn.getType(), rdn.getValue().toString());
        }

        certFromSignedData.checkValidity(sig.getSignDate().getTime());

        if (isSelfSigned(certFromSignedData)) {
            psi.selfSigned = true;
        } else { ;
            psi.selfSigned = false;
            // todo rest of chain
        }

        if (signerInformation.verify(new JcaSimpleSignerInfoVerifierBuilder().build(certFromSignedData))) {
            psi.signatureVerified="YES";
        } else {
            psi.signatureVerified="NO";
        }
    }

    // https://svn.apache.org/repos/asf/cxf/tags/cxf-2.4.1/distribution/src/main/release/samples/sts_issue_operation/src/main/java/demo/sts/provider/cert/CertificateVerifier.java
    /**
     * Checks whether given X.509 certificate is self-signed.
     */
    private  boolean isSelfSigned(X509Certificate cert) throws CertificateException, NoSuchAlgorithmException, NoSuchProviderException {
        try {
            // Try to verify certificate signature with its own public key
            PublicKey key = cert.getPublicKey();
            cert.verify(key);
            return true;
        } catch (SignatureException | InvalidKeyException sigEx) {
            return false;
        }
    }

    public String getAcceptFilePDFString() {
        return FileUtil.getAccpetFilePDFString();
    }

    @Override
    protected EScope getMenuId() {
        return EScope.CHECK_DIGITAL_SIGN;
    }
}
