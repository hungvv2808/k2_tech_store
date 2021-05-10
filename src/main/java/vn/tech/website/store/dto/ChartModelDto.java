package vn.tech.website.store.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChartModelDto {
    private Long categoryId;
    private Double totalJanuary;
    private Double totalFebruary;
    private Double totalMarch;
    private Double totalApril;
    private Double totalMay;
    private Double totalJune;
    private Double totalJuly;
    private Double totalAugust;
    private Double totalSeptember;
    private Double totalOctober;
    private Double totalNovember;
    private Double totalDecember;
}
