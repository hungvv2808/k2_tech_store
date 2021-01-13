package vn.compedia.website.auction.repository;

import org.springframework.data.repository.CrudRepository;
import vn.compedia.website.auction.model.Commune;
import vn.compedia.website.auction.model.CommuneApi;

import java.util.List;

public interface CommuneApiRepository extends CrudRepository<CommuneApi, Long>{

}
