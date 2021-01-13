package vn.compedia.website.auction.controller.frontend.system;


import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import vn.compedia.website.auction.controller.frontend.BaseFEController;
import vn.compedia.website.auction.model.ContactInfo;
import vn.compedia.website.auction.repository.ContactInfoRepository;
import vn.compedia.website.auction.util.*;

import javax.faces.context.FacesContext;
import javax.inject.Named;
import java.sql.Connection;
import java.util.List;

@Named
@Scope(value = "session")
@Getter
@Setter
public class ContactInfoLhFEController extends BaseFEController {

    @Autowired
    private ContactInfoRepository contactInfoRepository;

    // thông tin liên hệ
    private String contentMaps;
    private String contentTitle;
    private String contentAddress;
    private String contentPhone;
    private String contentEmail;
    private ContactInfo contactInfo;
    private List<ContactInfo> contactInfos;

    // thông tin giới thiệu
    private Connection aboutInfor;
    private String aboutUnitInfor;
    private String aboutHumanResource;
    private String aboutFunction;
    private List<ContactInfo> aboutInforList;

    public void initData() {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            init();
        }
    }

    public void findContactInfo() {
        contactInfos = contactInfoRepository.getAllByType(false);

        if (contactInfos.isEmpty()) {
            return;
        }

        for (ContactInfo ci : contactInfos) {
            if (ci.getContactInfoId() == DbConstant.MA_TITLE) {
                contentTitle = ci.getContent();
            }
            if (ci.getContactInfoId() == DbConstant.MA_ADDRESS) {
                contentAddress = ci.getContent();
            }
            if (ci.getContactInfoId() == DbConstant.MA_EMAIL) {
                contentEmail = ci.getContent();
            }
            if (ci.getContactInfoId() == DbConstant.MA_MAPS) {
                contentMaps = ci.getContent();
            }
            if (ci.getContactInfoId() == DbConstant.MA_PHONE) {
                contentPhone = ci.getContent();
            }
        }
    }

    public void findAboutInfo() {
        aboutInforList = contactInfoRepository.getAllByType(true);

        if (aboutInforList.isEmpty()) {
            return;
        }

        for (ContactInfo about : aboutInforList) {
            if (about.getContactInfoId() == DbConstant.ABOUT_UNIT_INFOR) {
                aboutUnitInfor = about.getContent();
            }
            if (about.getContactInfoId() == DbConstant.ABOUT_HUMAN_RESOURCE) {
                aboutHumanResource = about.getContent();
            }
            if (about.getContactInfoId() == DbConstant.ABOUT_FUNCTION) {
                aboutFunction = about.getContent();
            }
        }
    }

    @Override
    protected String getMenuId() {
        return Constant.ID_THONG_TIN_LH;
    }
}
