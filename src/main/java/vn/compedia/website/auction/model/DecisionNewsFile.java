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
@Table(name = "decision_news_file")
public class DecisionNewsFile implements Serializable {
    private static final long serialVersionUID = -8636979469151025067L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "decision_news_file_id")
    private Long decisionNewsFileId;

    @Column(name = "decision_news_id")
    private Long decisionNewsId;

    @Column(name = "image_path")
    private String imagePath;
}
