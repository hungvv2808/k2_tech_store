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
@Table(name = "contact_info")
public class ContactInfo extends BaseModel {
    private static final long serialVersionUID = -6915766342539661948L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "contact_info_id")
    private Long contactInfoId;

    @Column(name = "content")
    private String content;

    @Column(name = "type")
    private boolean type;

    @Column(name = "code")
    private String code;
}
