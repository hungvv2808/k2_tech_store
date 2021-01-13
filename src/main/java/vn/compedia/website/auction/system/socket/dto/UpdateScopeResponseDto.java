package vn.compedia.website.auction.system.socket.dto;

import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import vn.compedia.website.auction.dto.BaseResponseDto;
import vn.compedia.website.auction.system.util.SysConstant;

@Getter
@Setter
@AllArgsConstructor
public class UpdateScopeResponseDto extends BaseResponseDto {
    private String action;
    private String scopeId;
    private Object data;

    public UpdateScopeResponseDto() {
        this.action = SysConstant.ACTION_UPDATE_SCOPE;
    }

    ObjectNode BaseResponseDto(String scopeId, Object data) {
        this.scopeId = scopeId;
        this.data = data;
        return buildObjectNode();
    }

    ObjectNode BaseResponseDto() {
        return buildObjectNode();
    }

}
