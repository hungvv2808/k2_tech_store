package vn.tech.website.store.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.passay.CharacterData;
import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.PasswordGenerator;
import vn.tech.website.store.dto.user.AccountDto;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.websocket.Session;
import java.io.*;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.NumberFormat;
import java.util.Formatter;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

@Log4j2
public class StringUtil {

    private static final String PUNCTUATION = "~`!@#$%^&*()-_=+[{]}\\\\|;:\\'\\\",<.>/?\"";

    public static String encryptPassword(String password) {
        String sha1 = "";
        try {
            MessageDigest crypt = MessageDigest.getInstance("SHA-256");
            crypt.reset();
            crypt.update(password.getBytes("UTF-8"));
            sha1 = byteToHex(crypt.digest());
        } catch (NoSuchAlgorithmException e) {
            log.error("[encryptPassword|NoSuchAlgorithmException] cause error", e);
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            log.error("[encryptPassword|UnsupportedEncodingException] cause error", e);
            e.printStackTrace();
        }
        return sha1;
    }

    public static String encryptPassword(String password, String salt) {
        return encryptPassword(password + salt);
    }

    public static String generatePassword() {
        PasswordGenerator gen = new PasswordGenerator();
        CharacterData lowerCaseChars = EnglishCharacterData.LowerCase;
        CharacterRule lowerCaseRule = new CharacterRule(lowerCaseChars);
        lowerCaseRule.setNumberOfCharacters(3);

        CharacterData upperCaseChars = EnglishCharacterData.UpperCase;
        CharacterRule upperCaseRule = new CharacterRule(upperCaseChars);
        upperCaseRule.setNumberOfCharacters(3);

        CharacterData digitChars = EnglishCharacterData.Digit;
        CharacterRule digitRule = new CharacterRule(digitChars);
        digitRule.setNumberOfCharacters(3);

        CharacterData specialChars = new CharacterData() {
            public String getErrorCode() {
                return "error";
            }

            public String getCharacters() {
                return PUNCTUATION;
            }
        };
        CharacterRule splCharRule = new CharacterRule(specialChars);
        splCharRule.setNumberOfCharacters(3);

        String password = gen.generatePassword(12, splCharRule, lowerCaseRule,
                upperCaseRule, digitRule);
        return password;
    }

    public static String generateSalt() {
        return RandomStringUtils.randomAlphanumeric(10);
    }

    private static String byteToHex(final byte[] hash) {
        Formatter formatter = new Formatter();
        for (byte b : hash) {
            formatter.format("%02x", b);
        }
        String result = formatter.toString();
        formatter.close();
        return result;
    }

    public static boolean isValidEmailAddress(String email) {
        boolean result = true;
        try {
            InternetAddress emailAddr = new InternetAddress(email);
            emailAddr.validate();
        } catch (AddressException ex) {
            log.error("[isValidEmailAddress] cause error", ex);
            result = false;
        }
        return result;
    }
    public static boolean isNumber(String number) {
        try {
            Long.valueOf(number);
            return true;
        } catch (Exception e) {
            log.error("[isNumber] cause error", e);
        }
        return false;
    }

    public static String br2nl(String html) {
        Document document = Jsoup.parse(html);
        //document.select("br").append("\\n");
        document.select("p").prepend("\\n");
        return document.text().replace("\\n", "\n");
    }

    public static String nl2br(String text) {
        return text.replace("\n", "<br>");
    }

    public static String nl2space(String text) {
        return text.replace("\n", " ");
    }

    public static String removeHtmlTags(String str){
        Document doc = Jsoup.parse(str);
        return doc.body().text();
    }

    public static String toJson(Object object) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(JsonGenerator.Feature.WRITE_NUMBERS_AS_STRINGS, true);
            return objectMapper.writeValueAsString(object);
        } catch (IOException e) {
            log.error("[toJson] cause error", e);
            e.printStackTrace();
            return null;
        }
    }

    public static Object jsonToObject(String json, Class<?> objectClass) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(json, objectClass);
        } catch (IOException e) {
            log.error("[jsonToObject] cause error", e);
            e.printStackTrace();
            return null;
        }
    }

    public static byte[] objectToByte(Object object) {
        try {
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            ObjectOutputStream objStream = new ObjectOutputStream(byteStream);
            objStream.writeObject(object);

            return byteStream.toByteArray();
        } catch (Exception e) {
            log.error(e);
            return null;
        }
    }

    public static Object byteToObject(byte[] bytes) {
        try {
            ByteArrayInputStream byteStream = new ByteArrayInputStream(bytes);
            ObjectInputStream objStream = new ObjectInputStream(byteStream);

            return objStream.readObject();
        } catch (Exception e) {
            log.error(e);
            return null;
        }
    }

    public static AccountDto getAccountDto(Session session) {
        try {
            ObjectMapper mapper = new ObjectMapper();
//            if (!session.isOpen()) {
//                return null;
//            }
            return mapper.readValue(session.getUserPrincipal().getName(), AccountDto.class);
        } catch (Exception e) {
            log.error("[StringUtil|getAccountDto] cause error: ", e);
            return null;
        }
    }

    public <K, V> Stream<K> keys(Map<K, V> map, V value) {
        return map
                .entrySet()
                .stream()
                .filter(entry -> value.equals(entry.getValue()))
                .map(Map.Entry::getKey);
    }

    public static String buildQueryParam(Map<String, String> params) {
        Set<String> query = new LinkedHashSet<>();
        for (String tmp : params.keySet()) {
            String value = "";
            try {
                value = URLEncoder.encode(params.get(tmp), "UTF-8");
            } catch (Exception e) {
                log.error(e);
            }
            query.add(tmp + "=" + value);
        }
        return String.join("&", query);
    }

    public static String formatCurrency(long money) {
        String output = NumberFormat.getCurrencyInstance().format(money).substring(1);
        return StringUtils.removeEnd(output, ".00");
    }

    public static void main(String args[]) {
        System.out.println(encryptPassword("123456a@"));
    }
}
