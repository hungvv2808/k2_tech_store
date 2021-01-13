package vn.compedia.website.auction.service;

import vn.compedia.website.auction.model.api.ApiFamilyRelationship;

import java.util.List;

public interface ApiFamilyRelationshipService {
    int uploadFamilyRelationship(List<ApiFamilyRelationship> list);
}
