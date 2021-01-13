package vn.compedia.website.auction.controller.admin.common;

import org.apache.commons.collections.CollectionUtils;
import org.primefaces.event.FileUploadEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import vn.compedia.website.auction.controller.admin.BaseController;
import vn.compedia.website.auction.dto.common.UploadMultipleFileDto;
import vn.compedia.website.auction.entity.EScope;
import vn.compedia.website.auction.util.Constant;
import vn.compedia.website.auction.util.FileUtil;

import javax.inject.Named;
import java.util.*;

@Named
@Scope(value = "session")
public class UploadMultipleImageController extends BaseController {
    private static Logger log = LoggerFactory.getLogger(UploadMultipleImageController.class);

    private UploadMultipleFileDto uploadMultipleFileDto;

    public void resetAll(Set<String> listImage) {
        uploadMultipleFileDto = new UploadMultipleFileDto();
        if (CollectionUtils.isEmpty(listImage)) {
            listImage = new LinkedHashSet<>();
        }
        uploadMultipleFileDto.setListToShow(listImage);
        uploadMultipleFileDto.setListToAdd(new LinkedHashSet<>());
        uploadMultipleFileDto.setListToDelete(new LinkedHashSet<>());
    }

    public void onUploadImage(FileUploadEvent e) {
        try {
            if (!FileUtil.isAcceptImageType(e.getFile())) {
                setErrorForm("Loại file không hợp lệ. Bạn vui lòng chọn file hình ảnh có các định dạng sau:" + FileUtil.getAcceptImageString());
                return;
            }
            if (e.getFile().getSize() > Constant.MAX_FILE_SIZE) {
                setErrorForm("Dung lượng file quá lớn. Dung lượng tối đa " + Constant.MAX_FILE_SIZE / 1000000 + "Mb");
                return;
            }
            String image = FileUtil.saveImageFile(e.getFile());
            uploadMultipleFileDto.getListToShow().add(image);
            uploadMultipleFileDto.getListToAdd().add(image);
        } catch (Exception ex) {
            log.error("Error", ex);
        }
    }

    public void onRemoveImage(String image) {
        uploadMultipleFileDto.getListToShow().remove(image);
        // Nếu chưa có trong database
        if (uploadMultipleFileDto.getListToAdd().contains(image)) {
            uploadMultipleFileDto.getListToAdd().remove(image);
        } else {
            uploadMultipleFileDto.getListToDelete().add(image);
        }
    }

    public UploadMultipleFileDto getUploadMultipleFileDto() {
        return uploadMultipleFileDto;
    }

    public void setUploadMultipleFileDto(UploadMultipleFileDto uploadMultipleFileDto) {
        this.uploadMultipleFileDto = uploadMultipleFileDto;
    }

    @Override
    protected EScope getMenuId() {
        return null;
    }
}
