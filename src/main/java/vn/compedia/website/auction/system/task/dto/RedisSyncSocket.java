package vn.compedia.website.auction.system.task.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class RedisSyncSocket implements Serializable {
    private static final long serialVersionUID = -1398154305563187697L;

    private Long assetId;
    private String type;
    private String action;
    private List<Long> accountIdList;
    private boolean accountIdNotIn;
    private boolean sendToAll;

}
