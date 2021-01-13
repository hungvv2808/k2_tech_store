package vn.compedia.website.auction.repository;

import vn.compedia.website.auction.dto.api.ApiFamilyRelationshipSearchDto;
import vn.compedia.website.auction.model.api.ApiFamilyRelationship;

import java.util.List;

public interface ApiFamilyRelationshipRepositoryCustom {
    List<ApiFamilyRelationship> search(ApiFamilyRelationshipSearchDto apiFamilyRelationshipSearchDto);
    Long countSearch(ApiFamilyRelationshipSearchDto apiFamilyRelationshipSearchDto);
    void deleteAllRecords();
}
