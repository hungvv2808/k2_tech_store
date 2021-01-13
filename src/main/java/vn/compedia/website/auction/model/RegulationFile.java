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
@Table(name = "regulation_file")
public class RegulationFile implements Serializable {
    private static final long serialVersionUID = 2729583118137795909L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "regulation_file_id")
    private Long regulationFileId;

    @Column(name = "regulation_id")
    private Long regulationId;

    @Column(name = "file_path")
    private String filePath;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "type")
    private Integer type;

}
