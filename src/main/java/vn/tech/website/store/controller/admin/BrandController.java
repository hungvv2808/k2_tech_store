package vn.tech.website.store.controller.admin;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.primefaces.model.FilterMeta;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import vn.tech.website.store.dto.BrandDto;
import vn.tech.website.store.dto.BrandSearchDto;
import vn.tech.website.store.model.Brand;
import vn.tech.website.store.repository.BrandRepository;
import vn.tech.website.store.util.FacesUtil;

import javax.faces.context.FacesContext;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Named
@Scope(value = "session")
@Getter
@Setter
public class BrandController {
    @Autowired
    private BrandRepository brandRepository;

    private LazyDataModel<BrandDto> lazyDataModel;
    private BrandDto brandDto;
    private BrandSearchDto searchDto;

    public void initData() {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            //init();
            resetAll();
        }
    }

    public void resetAll() {
        brandDto = new BrandDto();
        searchDto = new BrandSearchDto();
        onSearch();
    }

    public void onSearch() {
        FacesUtil.resetDataTable("searchForm", "tblSearchResult");
        lazyDataModel = new LazyDataModel<BrandDto>() {
            @Override
            public List<BrandDto> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
                searchDto.setPageIndex(first);
                searchDto.setPageSize(pageSize);
                searchDto.setSortField(sortField);
                String sort;
                if (sortOrder.equals(sortOrder.ASCENDING)) {
                    sort = "ASC";
                } else {
                    sort = "DESC";
                }
                searchDto.setSortOrder(sort);
                return brandRepository.search(searchDto);
            }

            @Override
            public BrandDto getRowData(String rowKey) {
                List<BrandDto> dtoList = getWrappedData();
                String value = String.valueOf(rowKey);
                for (BrandDto obj : dtoList) {
                    if (obj.getBrandId().equals(Long.valueOf(value)) || obj.getBrandName().equals(value)) {
                        return obj;
                    }
                }
                return null;
            }
        };

        int count = brandRepository.countSearch(searchDto).intValue();
        lazyDataModel.setRowCount(count);
    }

    public boolean validateData() {
        if (StringUtils.isBlank(brandDto.getBrandName())) {
            FacesUtil.addErrorMessage("Bạn vui lòng nhập tên thương hiệu");
            return false;
        }
        if (StringUtils.isBlank(brandDto.getDescription())) {
            FacesUtil.addErrorMessage("Bạn vui lòng nhập mô tả thương hiệu");
            return false;
        }
        brandDto.setDescription(brandDto.getDescription().trim());
        List<Brand> brandList = new ArrayList<>();
        if (brandDto.getBrandId() == null) {
            brandList = brandRepository.findAll();
        } else {
            brandList = brandRepository.findAllGoodsExpertId(brandDto.getBrandId());
        }
        brandDto.setBrandName(removeSpaceOfString(brandDto.getBrandName()));
        for (Brand brand : brandList) {
            if (brandDto.getBrandName().equalsIgnoreCase(removeSpaceOfString(brand.getBrandName()))) {
                FacesUtil.addErrorMessage("Tên ngành hàng này đã tồn tại");
                return false;
            }
        }
        return true;
    }

    public void onSave() {
        if (!validateData()) {
            return;
        }
        Brand brand = new Brand();
        BeanUtils.copyProperties(brandDto, brand);
        brandRepository.save(brand);
        FacesUtil.addSuccessMessage("Lưu thành công.");
        FacesUtil.closeDialog("dialogInsertUpdate");
        onSearch();
    }

    public void showUpdatePopup(BrandDto resultDto) {
        BeanUtils.copyProperties(resultDto, brandDto);
    }

    public void onDelete(BrandDto resultDto) {
//        if (brandRepository.checkUseGood(resultDto.getGoodsId())) {
//            FacesUtil.addErrorMessage("Không thể xóa vì " + resultDto.getTitle() + " đang được sử dụng");
//            return;
//        }
        brandRepository.deleteById(resultDto.getBrandId());
        FacesUtil.addSuccessMessage("Xóa thành công.");
        onSearch();
    }

    public void resetDialog() {
        brandDto = new BrandDto();
    }

    public String removeSpaceOfString(String str){
        return str.trim().replaceAll("[\\s|\\u00A0]+"," ");
    }

}
