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
@Table(name= "api_country")
public class ApiCountry extends BaseModel {
    private static final long serialVersionUID = 1460182295656888630L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="country_id")
    private Long countryId;
    @Column(name = "name")
    private String name;

    public ApiCountry(Date createDate, Date updateDate, Long createBy, Long updateBy, String name) {
        super(createDate, updateDate, createBy, updateBy);
        this.name = name;
    }
}
