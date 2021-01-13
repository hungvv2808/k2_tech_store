package vn.compedia.website.auction.exception;

public class AuctionException extends Exception {

    private String message;

    public AuctionException(String message) {
        super(message);
        this.message = message;
    }

    public AuctionException(String message, Throwable cause) {
        super(message, cause);
        this.message = message;
    }

    public AuctionException(Throwable cause) {
        super(cause);
    }

    public AuctionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.message = message;
    }
}
