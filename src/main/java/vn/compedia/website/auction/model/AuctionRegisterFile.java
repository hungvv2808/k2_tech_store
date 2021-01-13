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
@Table(name = "auction_register_file")
public class AuctionRegisterFile implements Serializable {
    private static final long serialVersionUID = -8607589961873738065L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "auction_register_file_id")
    private Long auctionRegisterFileId;

    @Column(name = "auction_register_id")
    private Long auctionRegisterId;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "file_path")
    private String filePath;

}
