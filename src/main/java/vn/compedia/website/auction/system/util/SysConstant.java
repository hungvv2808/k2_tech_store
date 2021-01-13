/*
 * Author: Thinh Hoang
 * Date: 09/2020
 * Company: Compedia Software
 * Email: thinhhv@compedia.vn
 * Personal Website: https://vnnib.com
 */

package vn.compedia.website.auction.system.util;

public class SysConstant {
    // ID
    public static final String MESSAGE_GROWL_ID = "growl";
    public static final String DIALOG_ID = "dialogNotify";

    // ACTION
    public static final String ACTION_MESSAGE = "showMessage";
    public static final String ACTION_DIALOG = "showDialog";
    public static final String ACTION_UPDATE_SCOPE = "updateScope";
    public static final String ACTION_UPDATE_ENDED = "updateEnded";
    public static final String ACTION_SHOW_CONFIRM = "showConfirm";
    public static final String ACTION_SHOW_MODAL_DEPOSIT = "showModalDeposit";
    public static final String ACTION_SHOW_MODAL_CHANGE_ROUND = "showModalChangeRound";
    public static final String ACTION_SHOW_POPUP_AUCTION_LOST = "showPopupAuctionLost";
    public static final String ACTION_SHOW_POPUP_AUCTION_RANDOM = "showModalAuctionRandom";
    public static final String ACTION_SHOW_POPUP_AUCTION_WINNER = "showModalAuctionWinner";
    public static final String ACTION_SHOW_POPUP_CANCEL_ASSET = "showPopupCancelAsset";
    public static final String ACTION_SHOW_POPUP_CHANGE_ROUND = "showPopupChangeRound";
    public static final String ACTION_SHOW_POPUP_AUCTION_WINNER_FINISH = "showModalAuctionWinnerFinish";
    public static final String ACTION_SHOW_POPUP_AUCTION_OPPORTUNITY_D0 = "showModalAuctionOpportunityD0";
    public static final String ACTION_SHOW_POPUP_AUCTION_OPPORTUNITY_D1 = "showModalAuctionOpportunityD1";
    public static final String ACTION_SHOW_POPUP_AUCTION_OPPORTUNITY1 = "showModalAuctionOpportunity1";
    public static final String ACTION_SHOW_POPUP_AUCTION_OPPORTUNITY2 = "showModalAuctionOpportunity2";

    // CLICK ID
    public static final String ID_BUTTON_UPDATE_SCOPE = "refreshDataSubmit";

    // TITLE
    public static final String TITLE_STRING = "Thông báo";

    // TYPE: asset_started, session_state,...
    public static final String TYPE_ASSET_DETAIL = "asset_started";
    public static final String TYPE_SESSION_STATE = "session_state";
    public static final String TYPE_UPDATE_SCOPE = "update_scope";

    // REDIS SYNC
    public static final String SYNC_TO_NODES = "sync_node";
    public static final String SYNC_TO_CLIENT_NODES = "sync_client";
    // ACTION RedisSyncAuction
    public static final String SYNC_ACTION_ASSET = "asset";
    public static final String SYNC_ACTION_DEPOSIT = "deposit";
    public static final String SYNC_ACTION_DEPOSIT_BY_SYSTEM = "deposit-by-system";
    public static final String SYNC_ACTION_REFUSE_WINNER = "refuse_winner";
    public static final String SYNC_ACTION_RETRACT_PRICE = "retract_price";
    public static final String SYNC_ACTION_ACCEPT_WINNER = "accept_winner";
    public static final String SYNC_ACTION_ACCEPT_PRICE = "accept_price";
    public static final String SYNC_ACTION_BARGAIN = "bargain";
    public static final String SYNC_ACTION_CANCEL_ASSET = "cancel_asset";
    public static final String SYNC_ACTION_ASSET_CHANGE_ROUND = "asset_change_round";
    public static final String SYNC_ACTION_ASSET_ENDED = "asset_ended";
    public static final String SYNC_ACTION_CANCEL_REGULATION = "cancel_regulation";
    public static final String SYNC_ACTION_SOCKET_PING = "socket_ping";
    public static final String SYNC_ACTION_SOCKET_ONLINE = "socket_online";
    public static final String SYNC_ACTION_SOCKET_OFFLINE = "socket_offline";
}
