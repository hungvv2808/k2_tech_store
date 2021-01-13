package vn.compedia.website.auction.repository;

import org.springframework.data.repository.CrudRepository;
import vn.compedia.website.auction.model.payment.momo.MomoWalletResponse;

public interface MomoWalletResponseRepository extends CrudRepository<MomoWalletResponse, Long> {
}
