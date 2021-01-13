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
@Table(name = "decision_news")
public class DecisionNews extends BaseModel {
    private static final long serialVersionUID = 9094815522080600436L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "decision_news_id")
    private Long decisionNewsId;
    @Column(name = "title")
    private String title;
    @Column(name = "content")
    private String content;
    @Column(name = "type")
    private boolean type;
    @Column(name = "status")
    private boolean status;
    @Column(name = "decision_summary")
    private String decisionSummary;

}
