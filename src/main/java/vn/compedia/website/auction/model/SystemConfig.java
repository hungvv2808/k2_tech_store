package vn.compedia.website.auction.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "system_config")
public class SystemConfig implements Serializable {
    private static final long serialVersionUID = 4243921269143500382L;

    @Id
    @Column(name = "system_config_id")
    private Long systemConfigId;

    @Column(name = "title")
    private String title;

    @Column(name = "value")
    private Float value;

    @Column(name = "type")
    private Integer note;
}
