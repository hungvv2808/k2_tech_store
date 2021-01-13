package vn.compedia.website.auction.jsf;


import vn.compedia.website.auction.entity.EAction;

import javax.faces.convert.EnumConverter;
import javax.faces.convert.FacesConverter;

@FacesConverter(value = "roleCheckboxConverter")
public class RoleCheckboxConverter extends EnumConverter {
    public RoleCheckboxConverter() {
        super(EAction.class);
    }
}
