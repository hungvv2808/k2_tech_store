package vn.zeus.website.store.repository;

import org.springframework.data.repository.CrudRepository;
import vn.zeus.website.store.model.payment.momo.MomoWalletResponse;

public interface MomoWalletResponseRepository extends CrudRepository<MomoWalletResponse, Long> {
}
