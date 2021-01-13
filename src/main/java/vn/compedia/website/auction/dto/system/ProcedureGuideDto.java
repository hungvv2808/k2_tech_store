package vn.compedia.website.auction.dto.system;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.compedia.website.auction.dto.common.UploadMultipleFileDto;
import vn.compedia.website.auction.model.ProcedureGuide;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProcedureGuideDto extends ProcedureGuide {
    private UploadMultipleFileDto uploadMultipleFileDto;

    public void setUploadMultipleFileDto(UploadMultipleFileDto uploadMultipleFileDto) {
        this.uploadMultipleFileDto = uploadMultipleFileDto;
    }

}
