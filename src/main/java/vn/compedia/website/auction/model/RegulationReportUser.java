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
@Table(name = "regulation_report_user")
public class RegulationReportUser extends BaseModel {
    private static final long serialVersionUID = -6741645517445149873L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "regulation_report_user_id")
    private Long regulationReportUserId;

    @Column(name = "regulation_report_file_id")
    private Long regulationReportFileId;

    @Column(name = "asset_id")
    private Long assetId;

    @Column(name = "file_path")
    private String filePath;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "status")
    private Integer status;
}
