package vn.compedia.website.auction.system.task.validation;

import vn.compedia.website.auction.system.task.dto.AssetDtoQueue;
import vn.compedia.website.auction.system.task.helper.RedisHelper;

import java.util.Date;
import java.util.Map;

public class AuctionValidate {

    public static boolean isExists(Map<Long, AssetDtoQueue> assetDtoList, Long assetId) {
        return assetDtoList.get(assetId) != null;
    }

    public static boolean isConfirm(AssetDtoQueue assetDtoQueue) {
        return assetDtoQueue.isInTimeConfirm();
    }

    public static boolean isProcessing(AssetDtoQueue assetDtoQueue) {
        return assetDtoQueue.isInProcessing();
    }

    public static boolean isOverEndRound(AssetDtoQueue assetDtoQueue) {
        return new Date().compareTo(assetDtoQueue.getEndTimeRound()) >= 1;
    }

    public static boolean isAllDeposited(AssetDtoQueue assetDtoQueue) {
        return assetDtoQueue.getAuctionRegisterList().size() == assetDtoQueue.getAccountIdDepositList().size();
    }
}
