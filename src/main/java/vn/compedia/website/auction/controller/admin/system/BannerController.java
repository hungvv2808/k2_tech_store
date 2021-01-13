package vn.compedia.website.auction.controller.admin.system;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.primefaces.event.FileUploadEvent;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import vn.compedia.website.auction.controller.admin.BaseController;
import vn.compedia.website.auction.controller.admin.auth.AuthorizationController;
import vn.compedia.website.auction.controller.admin.common.ActionSystemController;
import vn.compedia.website.auction.controller.admin.common.UploadSingleImageController;
import vn.compedia.website.auction.dto.system.BannerDto;
import vn.compedia.website.auction.entity.EScope;
import vn.compedia.website.auction.model.Banner;
import vn.compedia.website.auction.model.Link;
import vn.compedia.website.auction.repository.BannerRepository;
import vn.compedia.website.auction.util.Constant;
import vn.compedia.website.auction.util.ErrorConstant;
import vn.compedia.website.auction.util.FacesUtil;
import vn.compedia.website.auction.util.FileUtil;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.List;

@Named
@Scope(value = "session")
@Getter
@Setter
public class BannerController extends BaseController {
    @Inject
    private AuthorizationController authorizationController;
    @Inject
    private ActionSystemController actionSystemController;
    @Inject
    private UploadSingleImageController uploadSingleImageController;
    @Autowired
    private BannerRepository bannerRepository;

    private Banner banner;
    private BannerDto bannerDto;
    private List<BannerDto> listBanner;
    private List<Banner> removeList;
    private List<BannerDto> oldBanner;

    public void initData() {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            init();
            resetAll();
        }
    }

    public void resetAll() {
        listBanner = bannerRepository.getAllBannerDto();
        removeList = new ArrayList<>();
        oldBanner = (List<BannerDto>) bannerRepository.getAllBannerDto();
        if (CollectionUtils.isEmpty(listBanner)) {
            listBanner = new ArrayList<>();
        }
        actionSystemController.resetAll();
    }

    public void onSave() {
        int i = 1;
        List<Banner> nhatKyList = new ArrayList<>();
        for (BannerDto dsBanner : listBanner) {
            if(!validateData(dsBanner, i)){
                return;
            }
            Banner banner = new Banner();
            BeanUtils.copyProperties(dsBanner, banner);
            if (banner.getBannerId() != null) {
                banner.setUpdateBy(authorizationController.getAccountDto().getAccountId());
            } else {
                banner.setCreateBy(authorizationController.getAccountDto().getAccountId());
            }
            banner.setBannerLink(banner.getBannerLink().trim());
            i++;
            nhatKyList.add(banner);
        }
        if (CollectionUtils.isNotEmpty(nhatKyList)) {
            if (removeList != null) {
                for (Banner ba : removeList) {
                    if (ba.getBannerId() != null) {
                        String str = String.format("Xóa banner %s (%s)",
                                ba.getImagePath(),
                                ba.getBannerLink());
                        actionSystemController.onSave(str, authorizationController.getAccountDto().getAccountId());
                    }
                }
                bannerRepository.deleteAll(removeList);
            }
        }
        saveHistorySystem();
        bannerRepository.saveAll(nhatKyList);
        FacesUtil.addSuccessMessage("Lưu thành công");
        FacesUtil.updateView("growl");
        resetAll();
    }

    public boolean validateData(BannerDto banner, int i) {
        if (StringUtils.isBlank(banner.getImagePath())) {
            FacesUtil.addErrorMessage(Constant.ERROR_MESSAGE_ID, "Bạn vui lòng chọn hình ảnh cho banner "+i+"");
            FacesUtil.updateView("growl");
            return false;
        }
        if (StringUtils.isBlank(banner.getBannerLink())) {
            FacesUtil.addErrorMessage(Constant.ERROR_MESSAGE_ID, "Liên kết "+i+" là trường bắt buộc");
            FacesUtil.updateView("growl");
            return false;
        }
        if(!banner.getBannerLink().matches("https?:\\/\\/(www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()@:%_\\+.~#?&//=]*)")){
            FacesUtil.addErrorMessage(Constant.ERROR_MESSAGE_ID,"Đường dẫn của banner "+i+" không hợp lệ");
            FacesUtil.updateView("growl");
            return false;
        }
        return true;
    }

    public void onAddNew() {
        if(listBanner.size()>9){
            FacesUtil.addErrorMessage("Bạn chỉ có thể thêm tối đa 10 banner");
            return;
        }
        listBanner.add(new BannerDto());
    }

    public void onDelete(BannerDto bannerDto) {
        listBanner.remove(bannerDto);
        Banner banner = new Banner();
        BeanUtils.copyProperties(bannerDto, banner);
        removeList.add(banner);
        FacesUtil.addSuccessMessage("Xóa thành công");
        FacesUtil.updateView("growl");
    }

    private void saveHistorySystem() {
        for (Banner dsbanner : listBanner) {
            if (dsbanner.getBannerId() != null) {
                dsbanner.setUpdateBy(authorizationController.getAccountDto().getAccountId());
                for (Banner db : oldBanner) {
                    if(dsbanner.getBannerId().equals(db.getBannerId()) && (!db.getImagePath().equals(dsbanner.getImagePath()) || !db.getBannerLink().equals(dsbanner.getBannerLink())) ){
                        String str = String.format("Sửa banner %s (%s) thành %s (%s)",
                                db.getImagePath(),
                                db.getBannerLink(),
                                dsbanner.getImagePath(),
                                dsbanner.getBannerLink());
                        actionSystemController.onSave(str, authorizationController.getAccountDto().getAccountId());
                    }
                }
            } else {
                dsbanner.setCreateBy(authorizationController.getAccountDto().getAccountId());
                String str = String.format("Tạo banner %s (%s)",
                        dsbanner.getBannerLink(),
                        dsbanner.getImagePath());
                actionSystemController.onSave(str, authorizationController.getAccountDto().getAccountId());
            }
        }
    }

    public void onUpload(FileUploadEvent e) {
        if (!FileUtil.isAcceptFileType(e.getFile())) {
            setErrorForm("Loại file không được phép. Những file được phép " + FileUtil.getAcceptFileString());
            return;
        }
        if (e.getFile().getSize() > Constant.MAX_FILE_SIZE) {
            setErrorForm("Dung lượng file quá lớn. Dung lượng tối đa " + Constant.MAX_FILE_SIZE / 1000000 + "Mb");
            return;
        }
        BannerDto bannerDto = (BannerDto) e.getComponent().getAttributes().get("bannerVar");
        bannerDto.setUploadedFile(e.getFile());
        bannerDto.setImagePath(FileUtil.saveImageFile(e.getFile()));
        FacesUtil.updateView("growl");
    }

    @Override
    protected EScope getMenuId() {
        return EScope.BANNER;
    }



}
