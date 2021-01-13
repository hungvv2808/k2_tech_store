package vn.compedia.website.auction.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "status_api")
public class StatusApi implements Serializable {
    private static final long serialVersionUID = -2463357642833323873L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "status_api_id")
    private Long statusApiId;

    @Column(name = "status")
    private Integer status;

    @Column(name = "province_id_first_add")
    private Integer provinceIdFirstAdd;

    @Column(name = "district_id_first_add")
    private Integer districtIdFirstAdd;

    @Column(name = "commune_id_first_add")
    private Integer communeIdFirstAdd;

}
