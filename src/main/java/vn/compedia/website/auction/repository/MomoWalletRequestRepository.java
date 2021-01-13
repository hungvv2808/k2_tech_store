package vn.compedia.website.auction.repository;

import org.springframework.data.repository.CrudRepository;
import vn.compedia.website.auction.model.payment.momo.MomoWalletRequest;

public interface MomoWalletRequestRepository extends CrudRepository<MomoWalletRequest, Long> {
}
