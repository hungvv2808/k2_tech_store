package vn.compedia.website.auction.dto.report;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReportDataTable {
    private Integer numericalOrder;
    private String typeAsset;
    private int totalRegulation;
    private int totalAsset;
    private int totalFormalityRoll;
    private int totalFormalityDirect;
    private int totalMethodUp;
    private int totalMethodDown;
    private Long regulationId;
    private Long typeAssetId;
}
