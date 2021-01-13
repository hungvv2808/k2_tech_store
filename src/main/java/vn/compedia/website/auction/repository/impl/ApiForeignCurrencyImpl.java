package vn.compedia.website.auction.repository.impl;

import org.springframework.stereotype.Service;
import vn.compedia.website.auction.model.api.ApiForeignCurrency;
import vn.compedia.website.auction.service.ApiForeignCurrencyService;
import vn.compedia.website.auction.util.Constant;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.List;

@Service
public class ApiForeignCurrencyImpl implements ApiForeignCurrencyService {
    @PersistenceContext
    private EntityManager entityManager;

    private void clearBatch() {
        entityManager.flush();
        entityManager.clear();
    }

    @Override
    @Transactional
    public int uploadApiForeignCurrency(List<ApiForeignCurrency> list) {
        for (int i = 0; i < list.size(); i++) {
            if (i > 0 && i % Constant.BATCH_SIZE == 0) {
                clearBatch();
            }
            entityManager.persist(list.get(i));
            list.remove(i--);
        }
        return 0;
    }
}
