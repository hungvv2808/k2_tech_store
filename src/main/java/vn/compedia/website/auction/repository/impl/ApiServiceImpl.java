package vn.compedia.website.auction.repository.impl;

import org.springframework.stereotype.Service;
import vn.compedia.website.auction.model.*;
import vn.compedia.website.auction.service.ApiService;
import vn.compedia.website.auction.util.Constant;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.List;

@Service
public class ApiServiceImpl implements ApiService {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public int uploadProvince(List<ProvinceApi> list) {
        for (int i = 0; i < list.size(); i++) {
            if (i > 0 && i % Constant.BATCH_SIZE == 0) {
                clearBatch();
            }
            entityManager.persist(list.get(i));
            list.remove(i--);
        }

        return 0;
    }

    @Override
    @Transactional
    public int uploadProvinceDb(List<Province> list) {
        for (int i = 0; i < list.size(); i++) {
            if (i > 0 && i % Constant.BATCH_SIZE == 0) {
                clearBatch();
            }
            entityManager.merge(list.get(i));
            list.remove(i--);
        }

        return 0;
    }

    @Override
    @Transactional
    public int uploadProvinceVersion(List<ProvinceVersion> list) {
        for (int i = 0; i < list.size(); i++) {
            if (i > 0 && i % Constant.BATCH_SIZE == 0) {
                clearBatch();
            }
            entityManager.persist(list.get(i));
            list.remove(i--);
        }

        return 0;
    }

    @Override
    @Transactional
    public int uploadDistrict(List<DistrictApi> list) {
        for (int i = 0; i < list.size(); i++) {
            if (i > 0 && i % Constant.BATCH_SIZE == 0) {
                clearBatch();
            }
            entityManager.persist(list.get(i));
            list.remove(i--);
        }
        return 0;
    }

    @Override
    @Transactional
    public int uploadDistrictDb(List<District> list) {
        for (int i = 0; i < list.size(); i++) {
            if (i > 0 && i % Constant.BATCH_SIZE == 0) {
                clearBatch();
            }
            entityManager.merge(list.get(i));
            list.remove(i--);
        }
        return 0;
    }

    @Override
    @Transactional
    public int uploadDistrictVersion(List<DistrictVersion> list) {
        for (int i = 0; i < list.size(); i++) {
            if (i > 0 && i % Constant.BATCH_SIZE == 0) {
                clearBatch();
            }
            entityManager.persist(list.get(i));
            list.remove(i--);
        }

        return 0;
    }

    @Override
    @Transactional
    public int uploadCommune(List<CommuneApi> list) {
        for (int i = 0; i < list.size(); i++) {
            if (i > 0 && i % Constant.BATCH_SIZE == 0) {
                clearBatch();
            }
            entityManager.persist(list.get(i));
            list.remove(i--);
        }
        return 0;
    }

    @Override
    @Transactional
    public int uploadCommuneDb(List<Commune> list) {
        for (int i = 0; i < list.size(); i++) {
            if (i > 0 && i % Constant.BATCH_SIZE == 0) {
                clearBatch();
            }
            entityManager.merge(list.get(i));
            list.remove(i--);
        }
        return 0;
    }

    @Override
    @Transactional
    public int uploadCommuneVersion(List<CommuneVersion> list) {
        for (int i = 0; i < list.size(); i++) {
            if (i > 0 && i % Constant.BATCH_SIZE == 0) {
                clearBatch();
            }
            entityManager.persist(list.get(i));
            list.remove(i--);
        }

        return 0;
    }

    @Override
    @Transactional
    public int uploadHistorySync(List<HistorySync> list) {
        for (int i = 0; i < list.size(); i++) {
            if (i > 0 && i % Constant.BATCH_SIZE == 0) {
                clearBatch();
            }
            entityManager.merge(list.get(i));
            list.remove(i--);
        }
        return 0;
    }

    @Override
    @Transactional
    public int uploadProvinceComeBack(List<ProvinceApi> list) {
        for (int i = 0; i < list.size(); i++) {
            if (i > 0 && i % Constant.BATCH_SIZE == 0) {
                clearBatch();
            }
            entityManager.merge(list.get(i));
            list.remove(i--);
        }
        return 0;
    }

    @Override
    @Transactional
    public int uploadDistrictComeBack(List<DistrictApi> list) {
        for (int i = 0; i < list.size(); i++) {
            if (i > 0 && i % Constant.BATCH_SIZE == 0) {
                clearBatch();
            }
            entityManager.merge(list.get(i));
            list.remove(i--);
        }
        return 0;
    }

    @Override
    @Transactional
    public int uploadCommuneComeBack(List<Commune> list) {
        for (int i = 0; i < list.size(); i++) {
            if (i > 0 && i % Constant.BATCH_SIZE == 0) {
                clearBatch();
            }
            entityManager.merge(list.get(i));
            list.remove(i--);
        }
        return 0;
    }

    private void clearBatch() {
        entityManager.flush();
        entityManager.clear();
    }
}
