package vn.compedia.website.auction.dto.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UploadSingleFileFEDto {
    private String filePath;
    private String fileName;
}
