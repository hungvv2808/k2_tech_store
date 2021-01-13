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
@Table(name = "history_sync")
public class HistorySync implements Serializable {
    private static final long serialVersionUID = -8247634787537191400L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "history_sync_id")
    private Long historySyncId;

    @Column(name = "version_id")
    private Long versionId;

    @Column(name = "create_date")
    private Date createDate;

    @Column(name = "status_apply")
    private Long statusApply;

}
