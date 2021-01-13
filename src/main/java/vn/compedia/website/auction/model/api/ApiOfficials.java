package vn.compedia.website.auction.model.api;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.compedia.website.auction.model.BaseModel;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name= "api_officials")
public class ApiOfficials extends BaseModel {
    private static final long serialVersionUID = -2663338701380574815L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="api_officials_id")
    private Long apiOfficialsId;
    @Column(name = "phone")
    private String phone;
    @Column(name = "identity_card")
    private String identityCard;
    @Column(name = "organization_id")
    private Long organizationId;
    @Column(name = "position")
    private String position;
    @Column(name = "place_identity_card")
    private String placeIdentityCard;
    @Column(name = "organization")
    private String organization;
    @Column(name = "name")
    private String name;
    @Column(name = "position_id")
    private String positionId;
    @Column(name = "code")
    private String code;
    @Column(name = "date")
    private Date date;
    @Column(name = "id_api")
    private Long idApi;
    @Column(name = "email")
    private String email;
    @Column(name = "date_identity_card")
    private Date dateIdentityCard;

    public ApiOfficials(Date createDate, Date updateDate, Long createBy, Long updateBy, String phone, String identityCard, Long organizationId, String position, String placeIdentityCard, String organization, String name, String positionId, String code, Date date, Long idApi, String email, Date dateIdentityCard) {
        super(createDate, updateDate, createBy, updateBy);
        this.phone = phone;
        this.identityCard = identityCard;
        this.organizationId = organizationId;
        this.position = position;
        this.placeIdentityCard = placeIdentityCard;
        this.organization = organization;
        this.name = name;
        this.positionId = positionId;
        this.code = code;
        this.date = date;
        this.idApi = idApi;
        this.email = email;
        this.dateIdentityCard = dateIdentityCard;
    }

}
