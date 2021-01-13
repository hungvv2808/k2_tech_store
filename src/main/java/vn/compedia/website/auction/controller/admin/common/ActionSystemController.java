package vn.compedia.website.auction.controller.admin.common;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import vn.compedia.website.auction.model.ContentDetail;
import vn.compedia.website.auction.model.HistorySystem;
import vn.compedia.website.auction.repository.ContentDetailRepository;
import vn.compedia.website.auction.repository.HistorySystemRepository;

import javax.inject.Named;
import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@Named
@Scope(value = "session")
public class ActionSystemController implements Serializable {

    @Autowired
    private HistorySystemRepository historySystemRepository;
    @Autowired
    private ContentDetailRepository contentDetailRepository;

    private HistorySystem historySystem;
    private ContentDetail contentDetail;

    public void resetAll() {
        historySystem = new HistorySystem();
        contentDetail = new ContentDetail();
    }

    public void onSave(String content, Long accountId) {
        resetAll();
        Date now = new Date();
        contentDetail.setContent(content);
        contentDetailRepository.saveAndFlush(contentDetail);
        historySystem.setContentDetailId(contentDetail.getContentDetailId());
        historySystem.setCreateDate(now);
        historySystem.setCreateBy(accountId);
        historySystemRepository.saveAndFlush(historySystem);
    }
}
