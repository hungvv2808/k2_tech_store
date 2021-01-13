package vn.compedia.website.auction.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.compedia.website.auction.dto.BaseSearchDto;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AccountSearchDto extends BaseSearchDto {
    private Long accountId;
    private Integer roleId;
    private String fullName;
    private Integer sex;
    private Date dateOfBirth;
    private String address;
    private String phone;
    private String email;
    private String password;
    private String idCardNumber;
    private Long provinceIdOfIssue;
    private Date dateOfIssue;
    private Boolean relativeInfor;
    private String permanentResidence;
    private String orgName;
    private String businessLicense;
    private String orgAddress;
    private String position;
    private String orgPhone;
    private Boolean org;
    private Integer accountStatus;
    private String username;
}
