package vn.compedia.website.auction.controller.admin.system;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import vn.compedia.website.auction.controller.admin.BaseController;
import vn.compedia.website.auction.controller.admin.auth.AuthorizationController;
import vn.compedia.website.auction.entity.EScope;
import vn.compedia.website.auction.model.ContactInfo;
import vn.compedia.website.auction.repository.ContactInfoRepository;
import vn.compedia.website.auction.util.DbConstant;
import vn.compedia.website.auction.util.FacesUtil;

import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;

@Named
@Scope(value = "session")
@Getter
@Setter
public class ContactInfoController extends BaseController {

    @Autowired
    private ContactInfoRepository contactInfoRepository;

    @Inject
    private AuthorizationController authorizationController;

//    private ContactInfo contactInfo;
//    private ContactInfo objBackup;

    private ContactInfo aboutInfor;
    private ContactInfo aboutInforBackup;
    private String aboutUnitInfor;
    private String aboutHumanResource;
    private String aboutFunction;

    private List<ContactInfo> aboutInforList;

    public void initData() {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            init();
            resetAll();
        }
    }

    public void resetAll() {
        resetDialog();
        findAboutInfor();
    }

    public void onSaveData(ContactInfo aboutInfor) {
        if (StringUtils.isBlank(aboutInfor.getContent())) {
            FacesUtil.addErrorMessage( " Bạn vui lòng nhập "+ aboutInfor.getCode().toLowerCase());
            return;
        }
        if (aboutInfor.getCreateBy() == null) {
            aboutInfor.setCreateBy(authorizationController.getAccountDto().getAccountId());
            aboutInfor.setUpdateBy(authorizationController.getAccountDto().getAccountId());
        }
        aboutInfor.setType(true);
        ContactInfo oldContact = contactInfoRepository.findById(aboutInfor.getContactInfoId()).orElse(null);
        if(aboutInfor.getCode().toLowerCase().trim().equals("thông tin đơn vị") && !oldContact.getContent().equals(aboutInfor.getContent())){
            actionSystemController().onSave("Cập nhật lại mục "+aboutInfor.getCode()+" của thông tin giới thiệu " , authorizationController.getAccountDto().getAccountId());
        }
        if(aboutInfor.getCode().toLowerCase().trim().equals("nhân sự") && !oldContact.getContent().equals(aboutInfor.getContent())){
            actionSystemController().onSave("Cập nhật lại mục "+aboutInfor.getCode()+" của thông tin giới thiệu " , authorizationController.getAccountDto().getAccountId());
        }
        if(aboutInfor.getCode().toLowerCase().trim().equals("chức năng nhiệm vụ") && !oldContact.getContent().equals(aboutInfor.getContent())){
            actionSystemController().onSave("Cập nhật lại mục "+aboutInfor.getCode()+" của thông tin giới thiệu " , authorizationController.getAccountDto().getAccountId());
        }
        contactInfoRepository.save(aboutInfor);
        FacesUtil.addSuccessMessage("Lưu thành công");
        resetAll();
    }

    public void showUpdatePopup(ContactInfo obj) {
        aboutInforBackup = new ContactInfo();
        BeanUtils.copyProperties(obj, aboutInfor);
        BeanUtils.copyProperties(obj, aboutInforBackup);
    }

    public void findAboutInfor(){
        aboutInforList = contactInfoRepository.getAllByType(true);
        if (aboutInforList.isEmpty()) {
            return;
        }
        for(ContactInfo about :aboutInforList) {
            if(about.getContactInfoId() == DbConstant.ABOUT_UNIT_INFOR) {
                aboutUnitInfor = about.getContent();
            }
            if(about.getContactInfoId() == DbConstant.ABOUT_HUMAN_RESOURCE) {
                aboutHumanResource = about.getContent();
            }
            if(about.getContactInfoId() == DbConstant.ABOUT_FUNCTION) {
                aboutFunction = about.getContent();
            }
        }
        FacesUtil.updateView("form");
    }

    public void resetDialog() {
        aboutInfor = new ContactInfo();
    }

    @Override
    protected EScope getMenuId() {
        return EScope.ABOUT_INFO;
    }
}
