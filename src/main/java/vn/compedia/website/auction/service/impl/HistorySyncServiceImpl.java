package vn.compedia.website.auction.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.compedia.website.auction.model.HistorySync;
import vn.compedia.website.auction.repository.HistorySyncRepository;
import vn.compedia.website.auction.service.HistorySyncService;

import javax.transaction.Transactional;

@Service
public class HistorySyncServiceImpl implements HistorySyncService {
    @Autowired
    private HistorySyncRepository historySyncRepository;

    @Override
    @Transactional
    public void save(HistorySync historySync) {
        historySyncRepository.save(historySync);
    }

}
