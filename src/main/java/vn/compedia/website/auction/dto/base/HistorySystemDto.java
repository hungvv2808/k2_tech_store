package vn.compedia.website.auction.dto.base;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import vn.compedia.website.auction.model.HistorySystem;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
public class HistorySystemDto extends HistorySystem {
    private static final long serialVersionUID = 5191050253537348285L;

    private String content;
    private String fullName;

    public HistorySystemDto() {
    }

    public HistorySystemDto(Long historySystemId, Long contentDetailId, Date createDate, Long createBy, String content, String fullName) {
        super(historySystemId, contentDetailId, createDate, createBy);
        this.content = content;
        this.fullName = fullName;
    }
}
