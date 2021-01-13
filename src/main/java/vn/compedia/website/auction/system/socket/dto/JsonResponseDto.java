package vn.compedia.website.auction.system.socket.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import vn.compedia.website.auction.dto.BaseResponseDto;
import vn.compedia.website.auction.system.util.SysConstant;

@Getter
@Setter
@AllArgsConstructor
public class JsonResponseDto extends BaseResponseDto {
    private String type;
    private String name;
    private String title;
    private String action;
    @JsonProperty("button_id")
    private String buttonId;
    private Object data;

    public JsonResponseDto() {
        this.title = SysConstant.TITLE_STRING;
        this.buttonId = SysConstant.ID_BUTTON_UPDATE_SCOPE;
    }
}
