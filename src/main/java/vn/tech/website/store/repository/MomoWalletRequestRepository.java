package vn.tech.website.store.repository;

import org.springframework.data.repository.CrudRepository;
import vn.tech.website.store.model.payment.momo.MomoWalletRequest;

public interface MomoWalletRequestRepository extends CrudRepository<MomoWalletRequest, Long> {
}
