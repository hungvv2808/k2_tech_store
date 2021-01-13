package vn.compedia.website.auction.controller.admin.system;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import vn.compedia.website.auction.controller.admin.BaseController;
import vn.compedia.website.auction.controller.admin.auth.AuthorizationController;
import vn.compedia.website.auction.controller.admin.common.ActionSystemController;
import vn.compedia.website.auction.entity.EScope;
import vn.compedia.website.auction.model.HistorySystem;
import vn.compedia.website.auction.model.Link;
import vn.compedia.website.auction.repository.LinkRepository;
import vn.compedia.website.auction.util.Constant;
import vn.compedia.website.auction.util.FacesUtil;

import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.List;

@Named
@Scope(value = "session")
@Getter
@Setter
public class LinkController extends BaseController {

    @Inject
    private AuthorizationController authorizationController;
    @Inject
    private ActionSystemController actionSystemController;

    @Autowired
    private LinkRepository linkRepository;

    private List<Link> listLink;
    private Link link;
    private List<Link> removeLink;
    private List<HistorySystem> ListHistorySystem;
    private List<Link> oldLink;

    public void initData() {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            init();
            resetAll();
        }
        actionSystemController.resetAll();
    }

    public void resetAll() {
        oldLink = new ArrayList<>();
        listLink = (List<Link>) linkRepository.findAll();
        oldLink = (List<Link>) linkRepository.findAll();
        removeLink = new ArrayList<>();
        if (CollectionUtils.isEmpty(listLink)) {
            listLink = new ArrayList<>();
        }
    }

    public void onSave() {
        // Validate
        int i = 1;

        for (Link dsLink : listLink) {
            if (StringUtils.isBlank(dsLink.getTitle().trim())) {
                FacesUtil.addErrorMessage(Constant.ERROR_MESSAGE_ID, "Tiêu đề " + i + " là trường bắt buộc");
                FacesUtil.updateView("growl");
                return;
            }
            if (StringUtils.isBlank(dsLink.getLinkPath().trim())) {
                FacesUtil.addErrorMessage(Constant.ERROR_MESSAGE_ID, "Đường dẫn cho liên kết " + i + " là trường bắt buộc");
                FacesUtil.updateView("growl");
                return;
            }
            if (!dsLink.getLinkPath().trim().matches("https?:\\/\\/(www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()@:%_\\+.~#?&amp;//=]*)")) {
                FacesUtil.addErrorMessage(Constant.ERROR_MESSAGE_ID, "Đường dẫn cho liên kết " + i + " phải bắt đầu bằng http:// hoặc https://");
                FacesUtil.updateView("growl");
                return;
            }
            dsLink.setTitle(dsLink.getTitle().trim());
            dsLink.setLinkPath(dsLink.getLinkPath().trim());
            i++;
        }

        if (CollectionUtils.isNotEmpty(listLink)) {
            if (removeLink != null) {
                for (Link lk : removeLink) {
                    if (lk.getLinkId() != null) {
                        String str = String.format("Xóa liên kết %s (%s)",
                                lk.getTitle(),
                                lk.getLinkPath());
                        actionSystemController.onSave(str, authorizationController.getAccountDto().getAccountId());
                    }
                }
                linkRepository.deleteAll(removeLink);
            }

            saveHistorySystem();
            linkRepository.saveAll(listLink);
            FacesUtil.addSuccessMessage("Lưu thành công");
            FacesUtil.updateView("growl");
            FacesUtil.updateView("mainForm");
            resetAll();
        }
        resetAll();
    }

    public void onAddNew() {
        if (listLink.size() > 9) {
            FacesUtil.addErrorMessage("Bạn chỉ có thể thêm tối đa là 10 liên kết");
            FacesUtil.updateView("growl");
            return;
        }
        listLink.add(new Link());
    }

    public void onDelete(Link link) {
        listLink.remove(link);
        removeLink.add(link);
        FacesUtil.addSuccessMessage("Xóa thành công");
        FacesUtil.updateView("growl");
    }

    private void saveHistorySystem() {
        for (Link dsLink : listLink) {
            if (dsLink.getLinkId() != null) {
                dsLink.setUpdateBy(authorizationController.getAccountDto().getAccountId());
                for (Link lk : oldLink) {
                    if (dsLink.getLinkId().equals(lk.getLinkId()) && (!lk.getTitle().equals(dsLink.getTitle()) || !lk.getLinkPath().equals(dsLink.getLinkPath()))) {
                        String str = String.format("Sửa liên kết %s (%s) thành %s (%s)",
                                lk.getTitle(),
                                lk.getLinkPath(),
                                dsLink.getTitle(),
                                dsLink.getLinkPath());
                        actionSystemController.onSave(str, authorizationController.getAccountDto().getAccountId());
                    }
                }
            } else {
                dsLink.setCreateBy(authorizationController.getAccountDto().getAccountId());
                String str = String.format("Tạo liên kết %s (%s)",
                        dsLink.getTitle(),
                        dsLink.getLinkPath());
                actionSystemController.onSave(str, authorizationController.getAccountDto().getAccountId());
            }
        }
    }

    @Override
    protected EScope getMenuId() {
        return EScope.LINK;
    }
}
