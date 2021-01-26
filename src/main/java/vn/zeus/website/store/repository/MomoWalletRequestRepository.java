package vn.zeus.website.store.repository;

import org.springframework.data.repository.CrudRepository;
import vn.zeus.website.store.model.payment.momo.MomoWalletRequest;

public interface MomoWalletRequestRepository extends CrudRepository<MomoWalletRequest, Long> {
}
