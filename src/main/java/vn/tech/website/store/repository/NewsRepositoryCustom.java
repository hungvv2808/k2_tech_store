package vn.tech.website.store.repository;

import vn.tech.website.store.dto.NewsDto;
import vn.tech.website.store.dto.NewsSearchDto;

import java.util.List;

public interface NewsRepositoryCustom {
    List search(NewsSearchDto searchDto);

    Long countSearch(NewsSearchDto searchDto);
}
