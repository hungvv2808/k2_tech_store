package vn.compedia.website.auction.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "accuracy")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Accuracy extends BaseModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "accuracy_id")
    private Long accuracyId;

    @Column(name = "account_id")
    private Long accountId;

    @Column(name = "image_card_id_front")
    private String imageCardIdFront;

    @Column(name = "image_card_id_back")
    private String imageCardIdBack;

    @Column(name = "accuracy_company_file_path")
    private String accuracyCompanyFilePath;

    @Column(name = "accuracy_company_file_name")
    private String accuracyCompanyFileName;

    @Column(name = "type")
    private boolean type;
}
