package vn.compedia.website.auction.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "account")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@NamedQuery(name = "Account.findAll", query = "SELECT a FROM Account a")
public class Account extends BaseModel {
    private static final long serialVersionUID = -1609232659532090425L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "account_id")
    private Long accountId;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "sex")
    private Integer sex;

    @Column(name = "date_of_birth")
    private Date dateOfBirth;

    @Column(name = "address")
    private String address;

    @Column(name = "phone")
    private String phone;

    @Column(name = "role_id")
    private Integer roleId;

    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "salt")
    private String salt;

    @Column(name = "id_card_number") //cmnd
    private String idCardNumber;

    @Column(name = "province_id_of_issue")
    private Long provinceIdOfIssue;

    @Column(name = "date_of_issue")
    private Date dateOfIssue;

    @Column(name = "relative_name")
    private String relativeName;

    @Column(name = "relative_id_card_number")
    private String relativeIdCardNumber;

    @Column(name = "permanent_residence")
    private String permanentResidence;

    @Column(name = "org_name")
    private String orgName;

    @Column(name = "business_license")
    private String businessLicense;

    @Column(name = "org_address")
    private String orgAddress;

    @Column(name = "position")
    private String position;

    @Column(name = "org_phone")
    private String orgPhone;

    @Column(name = "status")
    private Integer accountStatus;

    @Column(name = "is_org")
    private boolean org;

    @Column(name = "username")
    private String username;

    @Column(name = "login_failed")
    private Integer loginFailed = 0;

    @Column(name = "first_time_login")
    private boolean firstTimeLogin;

    @Column(name ="avatar_path")
    private String avatarPath;

    @Column(name = "finish_info_status")
    private boolean finishInfoStatus;

    @Column(name = "province_id")
    private Long provinceId;

    @Column(name = "district_id")
    private Long districtId;

    @Column(name = "commune_id")
    private Long communeId;

    @Column(name = "time_to_change_password")
    private Date TimeToChangePassword;

    @Column(name = "login_from_sso")
    private boolean loginFromSso;

    public Account(String fullName, String idCardNumber, Date dateOfBirth, Long provinceIdOfIssue, String address, Date dateOfIssue, String phone, String relativeIdCardNumber, String relativeName, String email, String permanentResidence, Integer roleId) {
        this.fullName = fullName;
        this.dateOfBirth = dateOfBirth;
        this.address = address;
        this.phone = phone;
        this.roleId = roleId;
        this.email = email;
        this.idCardNumber = idCardNumber;
        this.provinceIdOfIssue = provinceIdOfIssue;
        this.dateOfIssue = dateOfIssue;
        this.relativeName = relativeName;
        this.relativeIdCardNumber = relativeIdCardNumber;
        this.permanentResidence = permanentResidence;
    }

    public Account(String fullName, String idCardNumber, Date dateOfBirth, Long provinceIdOfIssue, String address, Date dateOfIssue, String phone, String email, String permanentResidence, Integer roleId) {
        this.fullName = fullName;
        this.dateOfBirth = dateOfBirth;
        this.address = address;
        this.phone = phone;
        this.roleId = roleId;
        this.email = email;
        this.idCardNumber = idCardNumber;
        this.provinceIdOfIssue = provinceIdOfIssue;
        this.dateOfIssue = dateOfIssue;
        this.permanentResidence = permanentResidence;
    }

    public Account(Long accountId, String fullName, Integer sex, Date dateOfBirth, String address, String phone, Integer roleId, String email, String password, String salt, String idCardNumber, Long provinceIdOfIssue, Date dateOfIssue, String relativeName, String relativeIdCardNumber, String permanentResidence, String orgName, String businessLicense, String orgAddress, String position, String orgPhone, Integer status, boolean org) {
        this.accountId = accountId;
        this.fullName = fullName;
        this.sex = sex;
        this.dateOfBirth = dateOfBirth;
        this.address = address;
        this.phone = phone;
        this.roleId = roleId;
        this.email = email;
        this.password = password;
        this.salt = salt;
        this.idCardNumber = idCardNumber;
        this.provinceIdOfIssue = provinceIdOfIssue;
        this.dateOfIssue = dateOfIssue;
        this.relativeName = relativeName;
        this.relativeIdCardNumber = relativeIdCardNumber;
        this.permanentResidence = permanentResidence;
        this.orgName = orgName;
        this.businessLicense = businessLicense;
        this.orgAddress = orgAddress;
        this.position = position;
        this.orgPhone = orgPhone;
        this.accountStatus = status;
        this.org = org;
    }

    public Account(Long accountId, String fullName) {
        this.accountId = accountId;
        this.fullName = fullName;
    }

    public Account(Long accountId, String fullName, String orgName, boolean org) {
        this.accountId = accountId;
        this.fullName = fullName;
        this.orgName = orgName;
        this.org = org;
    }
}
