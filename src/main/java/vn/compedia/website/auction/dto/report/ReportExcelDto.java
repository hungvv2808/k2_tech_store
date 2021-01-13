package vn.compedia.website.auction.dto.report;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.compedia.website.auction.util.DateUtil;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReportExcelDto {
    private Date fromDate;
	private Date toDate;
	private Date exportDate;
    private String fromDateString;
    private String toDateString;
    private String exportDateString;
	private int totalContract;
    private List<ReportAssetDto> reportAssetDtoList;
	private List<ReportDataTable> reportDataTableList;

	public String getFromDateString() {
		return DateUtil.formatToPattern(fromDate, DateUtil.DDMMYYYY);
	}
	public String getToDateString() {
		return DateUtil.formatToPattern(toDate, DateUtil.DDMMYYYY);
	}
	public String getExportDateString() {
		return DateUtil.formatToPattern(exportDate, DateUtil.DDMMYYYY);
	}
}
