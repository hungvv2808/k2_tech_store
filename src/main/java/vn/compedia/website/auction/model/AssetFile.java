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
@Table(name = "asset_file")
public class AssetFile implements Serializable {
    private static final long serialVersionUID = -8477288492371905269L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "asset_file_id")
    private Long assetFileId;

    @Column(name = "asset_id")
    private Long assetId;

    @Column(name = "file_path")
    private String filePath;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "file_path_name")
    private String filePathName;

    @Column(name = "reason_cancel_asset")
    private String reasonCancelAsset;

    @Column(name = "type")
    private Integer type;
}
