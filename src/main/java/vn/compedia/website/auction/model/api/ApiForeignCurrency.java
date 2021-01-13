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
@Table(name= "api_foreign_currency")
public class ApiForeignCurrency extends BaseModel {
    private static final long serialVersionUID = -2725501020230643340L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="foreign_currency_id")
    private Long foreignCurrencyId;
    @Column(name = "name")
    private String name;

    public ApiForeignCurrency(Date createDate, Date updateDate, Long createBy, Long updateBy, String name) {
        super(createDate, updateDate, createBy, updateBy);
        this.name = name;
    }
}
