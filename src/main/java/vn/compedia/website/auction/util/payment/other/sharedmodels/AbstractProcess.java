package vn.compedia.website.auction.util.payment.other.sharedmodels;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import vn.compedia.website.auction.util.payment.other.utils.Execute;

public abstract class AbstractProcess<T, V> {
    protected Execute execute = new Execute();

    public static Gson getGson() {
        return new GsonBuilder()
                .disableHtmlEscaping()
                .create();
    }
}
