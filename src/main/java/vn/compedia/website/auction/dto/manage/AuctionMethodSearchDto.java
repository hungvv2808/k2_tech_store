package vn.compedia.website.auction.dto.manage;

import vn.compedia.website.auction.dto.BaseSearchDto;

public class AuctionMethodSearchDto extends BaseSearchDto {
    private Long auctionMethodId;
    private String code;
    private String name;
    private Long status;

    public AuctionMethodSearchDto() {
    }

    public AuctionMethodSearchDto(Long auctionMethodId, String code, String name, Long status) {
        this.auctionMethodId = auctionMethodId;
        this.code = code;
        this.name = name;
        this.status = status;
    }

    public Long getAuctionMethodId() {
        return auctionMethodId;
    }

    public void setAuctionMethodId(Long auctionMethodId) {
        this.auctionMethodId = auctionMethodId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getStatus() {
        return status;
    }

    public void setStatus(Long status) {
        this.status = status;
    }
}
