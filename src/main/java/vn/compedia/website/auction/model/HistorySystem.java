package vn.compedia.website.auction.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "history_system")
public class HistorySystem implements Serializable {
    private static final long serialVersionUID = -2061387372997025870L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "history_system_id")
    private Long historySystemId;

    @Column(name = "content_detail_id")
    private Long contentDetailId;

    @Column(name = "create_date")
    private Date createDate;

    @Column(name = "create_by")
    private Long createBy;

}

