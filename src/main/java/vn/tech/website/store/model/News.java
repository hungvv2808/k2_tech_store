package vn.tech.website.store.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "news")
public class News {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "news_id")
    private Long colorId;

    @Column(name = "title")
    private String title;

    @Column(name = "short_content")
    private String shortContent;

    @Column(name = "content")
    private String content;

    @Column(name = "category_id")
    private Integer categoryId;

    @Column(name = "status")
    private Integer status;
}
