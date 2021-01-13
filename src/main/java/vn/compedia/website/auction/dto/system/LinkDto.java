package vn.compedia.website.auction.dto.system;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.primefaces.model.UploadedFile;
import vn.compedia.website.auction.model.Link;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LinkDto extends Link {
    private UploadedFile uploadedFile;

    public Link getParent() {
        Link ds = new Link();
        ds.setLinkPath(getLinkPath());
        ds.setTitle(getTitle());
        return ds;
    }
}
