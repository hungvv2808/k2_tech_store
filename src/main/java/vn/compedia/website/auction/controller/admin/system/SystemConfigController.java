package vn.compedia.website.auction.controller.admin.system;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import vn.compedia.website.auction.controller.admin.BaseController;
import vn.compedia.website.auction.controller.admin.auth.AuthorizationController;
import vn.compedia.website.auction.controller.admin.common.ActionSystemController;
import vn.compedia.website.auction.entity.EScope;
import vn.compedia.website.auction.model.SystemConfig;
import vn.compedia.website.auction.repository.SystemConfigRepository;
import vn.compedia.website.auction.util.DbConstant;
import vn.compedia.website.auction.util.FacesUtil;

import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Named
@Scope(value = "session")
public class SystemConfigController extends BaseController {

    @Inject
    private ActionSystemController actionSystemController;
    @Inject
    private AuthorizationController authorizationController;

    @Autowired
    private SystemConfigRepository systemConfigRepository;

    private List<SystemConfig> systemConfigList;
    private List<SystemConfig> systemConfigListBk;
    private SystemConfig systemConfigs;
    private boolean value;
    public void initData() {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            init();
            resetAll();
        }
    }

    public void resetAll() {
        actionSystemController.resetAll();
        systemConfigList = (List<SystemConfig>) systemConfigRepository.findAll();
        for (SystemConfig sc : systemConfigList) {
            if (sc.getSystemConfigId() == DbConstant.SYSTEM_CONFIG_ID_SHOW_CAPTCHA) {
                if (sc.getValue() == 1) {
                    value = true;
                } else {
                    value = false;
                }
            }
        }
        if (CollectionUtils.isEmpty(systemConfigList)) {
            systemConfigList = new ArrayList<>();
        }
        systemConfigs = new SystemConfig();
    }

    public void onSave() {
        if (CollectionUtils.isEmpty(systemConfigList)) {
            return;
        }
        for (SystemConfig systemConfig : systemConfigList) {
            if(systemConfig.getSystemConfigId() == DbConstant.SYSTEM_CONFIG_ID_SHOW_CAPTCHA){
                systemConfig.setValue((float)(value ? 1 : 0));
                continue;
            }
            if (systemConfig.getValue() == null) {
                FacesUtil.addErrorMessage("Bạn vui lòng nhập " + systemConfig.getTitle().toLowerCase());
                return;
            }
            if (systemConfig.getValue() < 0) {
                FacesUtil.addErrorMessage(systemConfig.getTitle()+ " không thể nhỏ hơn 0");
                return;
            }
        }
        systemConfigRepository.saveAll(systemConfigList);
        FacesUtil.addSuccessMessage("Lưu thành công");
        actionSystemController.onSave("Chỉnh sửa thông tin cấu hình tham số hệ thống ", authorizationController.getAccountDto().getAccountId());
        FacesUtil.updateView("mainForm");
    }

    public void refeshData() {
        systemConfigListBk = new ArrayList<>();
        if (CollectionUtils.isEmpty(systemConfigList)) {
            BeanUtils.copyProperties(systemConfigListBk, systemConfigList);
        } else {
            resetAll();
        }
    }
    public void change() {
        if(value){
            systemConfigs.setValue(1F);
        } else {
            systemConfigs.setValue(0F);
        }
    }


    @Override
    protected EScope getMenuId() {
        return EScope.SYSTEM_CONFIG;
    }
}
