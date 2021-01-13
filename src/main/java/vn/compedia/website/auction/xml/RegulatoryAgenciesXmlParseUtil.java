package vn.compedia.website.auction.xml;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;

public class RegulatoryAgenciesXmlParseUtil {
    public static  RegulatoryAgenciesRoot convertStringToXml(String xml) {
        JAXBContext jaxbContext;
        try {
            jaxbContext = JAXBContext.newInstance(RegulatoryAgenciesRoot.class);

            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

            return (RegulatoryAgenciesRoot) jaxbUnmarshaller.unmarshal(new StringReader(xml));
        } catch (JAXBException e) {
            return null;
        }
    }
}
