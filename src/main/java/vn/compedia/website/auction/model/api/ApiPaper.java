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
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="api_paper")
public class ApiPaper extends BaseModel {
    private static final long serialVersionUID = -5205794469787212959L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="api_paper_id")
    private Long apiPaperId;

    @Column(name="api_paper_name")
    private String name;

    public ApiPaper(Date createDate, Date updateDate, Long createBy, Long updateBy,String name) {
        super(createDate, updateDate, createBy, updateBy);
        this.name = name;
    }
}
