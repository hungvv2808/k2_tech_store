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
@Table(name="link")
public class Link extends BaseModel {
    private static final long serialVersionUID = -5544821657003964811L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="link_id")
    private Long linkId;

    @Column(name ="title")
    private String title;

    @Column(name ="link_path")
    private String linkPath;

}
