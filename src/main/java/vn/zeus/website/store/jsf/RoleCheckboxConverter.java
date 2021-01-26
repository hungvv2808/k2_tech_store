package vn.zeus.website.store.jsf;


import vn.zeus.website.store.entity.EAction;

import javax.faces.convert.EnumConverter;
import javax.faces.convert.FacesConverter;

@FacesConverter(value = "roleCheckboxConverter")
public class RoleCheckboxConverter extends EnumConverter {
    public RoleCheckboxConverter() {
        super(EAction.class);
    }
}
