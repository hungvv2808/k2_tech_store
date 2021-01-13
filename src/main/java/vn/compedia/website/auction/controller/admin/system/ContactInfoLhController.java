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
public class ContactInfoLhController extends BaseController {

    @Autowired
    private ContactInfoRepository contactInfoRepository;

    @Inject
    private AuthorizationController authorizationController;

    private ContactInfo contactInfo;
    private ContactInfo objBackup;
    private String contentMaps;
    private String contentTitle;
    private String contentAddress;
    private String contentPhone;
    private String contentEmail;

    private List<ContactInfo> contactInfos;

    public void initData() {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            init();
            resetAll();
        }
    }

    public void resetAll() {
        findContactInfo();
        contactInfo = new ContactInfo();
    }

    public void findContactInfo(){
        contactInfos = contactInfoRepository.getAllByType(false);

        if (contactInfos.isEmpty()) {
            return;
        }

        for(ContactInfo ci :contactInfos) {
            if(ci.getContactInfoId() == DbConstant.MA_TITLE) {
                contentTitle = ci.getContent();
            }
            if(ci.getContactInfoId() == DbConstant.MA_ADDRESS) {
                contentAddress = ci.getContent();
            }
            if(ci.getContactInfoId() == DbConstant.MA_EMAIL) {
                contentEmail = ci.getContent();
            }
            if(ci.getContactInfoId() == DbConstant.MA_MAPS) {
                contentMaps = ci.getContent();
            }
            if(ci.getContactInfoId() == DbConstant.MA_PHONE) {
                contentPhone = ci.getContent();
            }
        }
        FacesUtil.updateView("form");
    }

    public void onSaveData(ContactInfo contactInfo) {
        if (StringUtils.isBlank(contactInfo.getContent().trim())) {
            FacesUtil.addErrorMessage("Bạn vui lòng nhập "+contactInfo.getCode().toLowerCase());
            return;
        }

        if (contactInfo.getCreateBy() == null) {
            contactInfo.setCreateBy(authorizationController.getAccountDto().getAccountId());
            contactInfo.setUpdateBy(authorizationController.getAccountDto().getAccountId());
        } else {
            contactInfo.setCreateBy(contactInfo.getCreateBy());
            contactInfo.setCreateDate(contactInfo.getCreateDate());
            contactInfo.setUpdateBy(authorizationController.getAccountDto().getAccountId());
        }
        contactInfo.setType(false);
        contactInfoRepository.save(contactInfo);
        actionSystemController().onSave("Cập nhật thông tin liên hệ " + contactInfo.getCode() , authorizationController.getAccountDto().getAccountId());
        FacesUtil.addSuccessMessage("Lưu thành công");
        resetAll();
    }

    public void showUpdatePopup(ContactInfo obj) {
        objBackup = new ContactInfo();
        contactInfo = new ContactInfo();
        BeanUtils.copyProperties(obj, contactInfo);
        BeanUtils.copyProperties(obj, objBackup);
    }

    public void resetDialog() {
        contactInfo = new ContactInfo();
    }

    @Override
    protected EScope getMenuId() {
        return EScope.CONTACT_INFO;
    }

}
