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
@Table(name="places_of_issue")
public class PlacesOfIssue implements Serializable {
    private static final long serialVersionUID = 2630178230543455172L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="places_of_issue_id")
    private Long placesOfIssueId;

    @Column(name = "name")
    private String name;
}
