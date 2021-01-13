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
@Table(name = "asset_image")
public class AssetImage implements Serializable {
    private static final long serialVersionUID = 5969708162804203252L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "asset_image_id")
    private Long assetImageId;

    @Column(name = "asset_id")
    private Long assetId;

    @Column(name = "file_path")
    private String filePath;
}
