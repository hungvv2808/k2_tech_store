package vn.compedia.website.auction.service;

import vn.compedia.website.auction.model.api.ApiPaper;

import java.util.List;

public interface ApiPaperService {
    int uploadedPaper(List<ApiPaper> list);
}
