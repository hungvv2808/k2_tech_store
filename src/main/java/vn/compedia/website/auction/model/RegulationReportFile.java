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
@Table(name = "regulation_report_file")
public class RegulationReportFile extends BaseModel {
    private static final long serialVersionUID = 7943566701570445065L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "regulation_report_file_id")
    private Long regulationReportFileId;

    @Column(name = "regulation_id")
    private Long regulationId;

    @Column(name = "file_path")
    private String filePath;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "total_winner")
    private Integer totalWinner;

    @Column(name = "people_signed")
    private Integer peopleSigned;

    @Column(name = "status")
    private Integer status;
}
