package vn.compedia.website.auction.dto.base;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.compedia.website.auction.dto.BaseSearchDto;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HistorySystemSearchDto extends BaseSearchDto {
    private static final long serialVersionUID = -7284512318782367813L;

    private Long createBy;
    private String content;
}
