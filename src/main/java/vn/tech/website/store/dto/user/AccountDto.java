package vn.tech.website.store.dto.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.primefaces.model.UploadedFile;
import vn.tech.website.store.model.Account;

import java.util.Date;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccountDto extends Account {
    private static final long serialVersionUID = 5987638366889233412L;

    private String roleName;
    private String provinceName;
    private String storeMethodName;
    private String storeFormalityName;
    private String filePath;
    private String rePassword;
    private Long regulationId;
    private Long action;
    private String assetName;
    private Date date;
    private String name;
    private String nameProvice;
    private String nameDistrict;
    private String nameCommune;
    private String updateName;
    private String imageCardIdFrontPath;
    private String imageCardIdBackPath;
    private Map<String, String> accuracyCompanyFile; // file_path/file_name
    @JsonIgnore
    private UploadedFile uploadedFile;

    public AccountDto(String nameProvice, String nameDistrict, String nameCommune) {
        this.nameProvice = nameProvice;
        this.nameDistrict = nameDistrict;
        this.nameCommune = nameCommune;
    }

    public AccountDto(Long accountId, String fullName, String orgName, boolean org) {
        //super(accountId, fullName, orgName, org);
    }
}
