package vn.compedia.website.auction.model.api;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.compedia.website.auction.model.BaseModel;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "api_nation")
public class ApiNation extends BaseModel {
    private static final long serialVersionUID = -8479930754425756144L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "nation_id")
    private Long nationId;

    @Column(name = "name")
    private String name;


    public ApiNation(Date createDate, Date updateDate, Long createBy, Long updateBy, String name) {
        super(createDate, updateDate, createBy, updateBy);
        this.name = name;
    }
}
