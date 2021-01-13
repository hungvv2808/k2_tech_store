package vn.compedia.website.auction.controller.admin.export;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import vn.compedia.website.auction.dto.auction.AssetDto;
import vn.compedia.website.auction.dto.auction.BidDto;
import vn.compedia.website.auction.dto.auction.PriceRoundDto;
import vn.compedia.website.auction.dto.export.ExportExcelDto;
import vn.compedia.website.auction.dto.regulation.RegulationDto;
import vn.compedia.website.auction.repository.AssetRepository;
import vn.compedia.website.auction.repository.BidRepository;
import vn.compedia.website.auction.repository.PriceRoundRepository;
import vn.compedia.website.auction.repository.RegulationRepository;
import vn.compedia.website.auction.util.Constant;
import vn.compedia.website.auction.util.DbConstant;
import vn.compedia.website.auction.util.ExportUtil;

import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.servlet.ServletContext;
import java.io.*;
import java.util.Collections;
import java.util.List;

@Getter
@Setter
@Named
@Scope(value = "session")
public class ExportExcelController implements Serializable {

    @Autowired
    private AssetRepository assetRepository;
    @Autowired
    private PriceRoundRepository priceRoundRepository;
    @Autowired
    private BidRepository bidRepository;
    @Autowired
    private RegulationRepository regulationRepository;

    private List<ExportExcelDto> exportExcelDtoList;

    private ExportExcelDto exportExcelDto;
    private RegulationDto regulationDto;
    private List<AssetDto> assetDtoList;
    private Long regulationId;

    //Lấy data từ db
    public void getDataList(Integer auctionStatus) {
        if (regulationId == null) {
            return;
        }
        regulationDto = regulationRepository.getRegulationDto(regulationId, auctionStatus);
        assetDtoList = assetRepository.getByRegulationId(regulationId);
        for (AssetDto assetDto : assetDtoList) {
            List<PriceRoundDto> priceRoundDtoList = priceRoundRepository.getAllPriceRoundDtoByAssetId(assetDto.getAssetId());
            if (priceRoundDtoList.isEmpty()) {
                continue;
            }
            for (PriceRoundDto pr : priceRoundDtoList) {
                List<BidDto> bidDtoList = bidRepository.loadAllBidDtoByPriceRoundId(pr.getPriceRoundId());
                if (bidDtoList.isEmpty()) {
                    continue;
                }
                pr.setBidDtoList(bidDtoList);
            }
            assetDto.setPriceRoundList(priceRoundDtoList);
        }
    }

    //Ghi dữ liệu quy chế vào file quy chế hình thức trả giá trực tiếp
    public void setValueCellRegulationDirectPayment(Sheet sheet, RegulationDto regulationDto, Workbook workbook) {

        CellStyle cellStyleDateFormat = setCellStyleDateRegulation(workbook);
        CellStyle cellStyle = setCellStyleFont(workbook);

        Row rowCode = sheet.getRow(0);
        rowCode.getCell(2).setCellValue(regulationDto.getCode());
        rowCode.getCell(2).setCellStyle(cellStyle);

        Row rowAuctioneer = sheet.getRow(1);
        if (StringUtils.isNotBlank(regulationDto.getNameAccount())) {
            rowAuctioneer.getCell(2).setCellValue(regulationDto.getNameAccount());
        } else {
            rowAuctioneer.getCell(2).setCellValue("Chưa phân công đấu giá viên");
        }
        rowAuctioneer.getCell(2).setCellStyle(cellStyle);

        Row rowStartTime = sheet.getRow(2);
        rowStartTime.getCell(2).setCellValue(regulationDto.getStartTime());
        rowStartTime.getCell(2).setCellStyle(cellStyleDateFormat);

        Row rowEndTime = sheet.getRow(3);
        rowEndTime.getCell(2).setCellValue(regulationDto.getRealEndTime());
        rowEndTime.getCell(2).setCellStyle(cellStyleDateFormat);

        Row rowAuctionFormality = sheet.getRow(4);
        rowAuctionFormality.getCell(2).setCellValue(regulationDto.getAuctionFormalityName());
        rowAuctionFormality.getCell(2).setCellStyle(cellStyle);

        Row rowAuctionMethod = sheet.getRow(5);
        rowAuctionMethod.getCell(2).setCellValue(regulationDto.getAuctionMethodName());
        rowAuctionMethod.getCell(2).setCellStyle(cellStyle);
    }

    //Tạo bảng và ghi dữ liệu hình thức trả giá trực tiếp
    public void createTableAndWriteDataDirectPayment(Sheet sheet, List<AssetDto> assetDtoList, Workbook workbook, int i) {

        CellStyle cellStyleDateFormat = setCellStyleDateFormmat(workbook);
        CellStyle cellStyleBorder = setCellStyleBorder(workbook);
        CellStyle cellStyleTitle = setCellStyleTitle(workbook);
        CellStyle cellStyleAlign = setCellStyleAlign(workbook);
        CellStyle cellStyle = setCellStyleFont(workbook);
        CellStyle cellStyleMoney = setCellStyleMoney(workbook);

        for (AssetDto assetDto : assetDtoList) {

            Row rowAssetName = sheet.createRow(i);
            rowAssetName.createCell(0).setCellValue("Tài sản: ");
            rowAssetName.getCell(0).setCellStyle(cellStyle);
            sheet.addMergedRegion(new CellRangeAddress(i, i, 0, 1));
            rowAssetName.createCell(2).setCellValue(assetDto.getName());
            rowAssetName.getCell(2).setCellStyle(cellStyle);

            Row rowStartPrice = sheet.createRow(i + 1);
            rowStartPrice.createCell(0).setCellValue("Giá khởi điểm: ");
            rowStartPrice.getCell(0).setCellStyle(cellStyle);
            sheet.addMergedRegion(new CellRangeAddress(i + 1, i + 1, 0, 1));
            rowStartPrice.createCell(2).setCellValue(assetDto.getStartingPrice() + " (đồng)");
            rowStartPrice.getCell(2).setCellStyle(cellStyle);

            Row rowStepPrice = sheet.createRow(i + 2);
            rowStepPrice.createCell(0).setCellValue("Bước giá: ");
            rowStepPrice.getCell(0).setCellStyle(cellStyle);
            sheet.addMergedRegion(new CellRangeAddress(i + 2, i + 2, 0, 1));
            rowStepPrice.createCell(2).setCellValue(assetDto.getPriceStep() + " (đồng)");
            rowStepPrice.getCell(2).setCellStyle(cellStyle);

            Row rowTitle = sheet.createRow(i + 3);
            rowTitle.createCell(0).setCellValue("STT");
            rowTitle.getCell(0).setCellStyle(cellStyleTitle);

            rowTitle.createCell(1).setCellValue("Họ và tên");
            rowTitle.getCell(1).setCellStyle(cellStyleTitle);

            rowTitle.createCell(2).setCellValue("Thời gian");
            rowTitle.getCell(2).setCellStyle(cellStyleTitle);

            rowTitle.createCell(3).setCellValue("Mức trả giá (đồng)");
            rowTitle.getCell(3).setCellStyle(cellStyleTitle);

            if (assetDto.getStatus() == DbConstant.ASSET_STATUS_CANCELED) {
                rowAssetName.createCell(3).setCellValue("Tài sản đã bị tạm dừng đấu giá");
                rowAssetName.getCell(3).setCellStyle(cellStyle);
                i = i + 5;
                continue;
            }

            if (CollectionUtils.isEmpty(assetDto.getPriceRoundList())) {
                i = i + 5;
                continue;
            }

            int size = i + 4;
            for (PriceRoundDto priceRoundDto : assetDto.getPriceRoundList()) {

                if (CollectionUtils.isEmpty(priceRoundDto.getBidDtoList())) {
                    continue;
                }

                int n = 1;
                for (BidDto bidDto : priceRoundDto.getBidDtoList()) {
                    Row row = sheet.createRow(size++);

                    row.createCell(0).setCellValue(n);
                    row.getCell(0).setCellStyle(cellStyleAlign);

                    row.createCell(1).setCellValue(bidDto.getFullName());
                    row.getCell(1).setCellStyle(cellStyleBorder);

                    row.createCell(2).setCellValue(bidDto.getCreateDate());
                    row.getCell(2).setCellStyle(cellStyleDateFormat);

                    row.createCell(3).setCellValue(bidDto.getMoney());
                    row.getCell(3).setCellStyle(cellStyleMoney);
                    n++;
                }
            }
            i = size + 2;
        }
    }

    //Download file hình thức trả giá trực tiếp
    public StreamedContent downloadFileDirectPayment(Integer auctionStatus) throws Exception {
        // Mở kết nối đến file
        ServletContext servletContext = ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext());
        String templateFilePath = servletContext.getRealPath(Constant.TEMPLETE_EXPORT_FILE_DIRECT_PAYMENT);
        FileInputStream inputStream = new FileInputStream(new File(templateFilePath));

        Workbook workbook = new XSSFWorkbook(inputStream);
        Sheet sheet = workbook.getSheetAt(0);

        sheet.setColumnWidth(1, Constant.COLUMN_WIDTH);
        sheet.setColumnWidth(2, Constant.COLUMN_WIDTH);

        getDataList(auctionStatus);

        setValueCellRegulationDirectPayment(sheet, regulationDto, workbook);
        createTableAndWriteDataDirectPayment(sheet, assetDtoList, workbook, Constant.INDEX_ROW_START_EXPORT);

        // Truyền vào dữ liệu, tên file, file template, file export
        String fileName = ExportUtil.getFileNameExport("LichSuTraGia_" + regulationDto.getCode());
        String reportFilePath = servletContext.getRealPath(Constant.REPORT_EXPORT_FILE_DIRECT_PAYMENT + fileName);
        FileOutputStream outputStream = new FileOutputStream(reportFilePath);
        workbook.write(outputStream);
        workbook.close();

        //Download file
        InputStream osStream = new FileInputStream(reportFilePath);

        return new DefaultStreamedContent(osStream, "application/vnd.ms-excel", fileName);
    }

    //Ghi dữ liệu quy chế vào file quy chế hình thức bỏ phiếu
    public void setValueCellRegulationVote(Sheet sheet, RegulationDto regulationDto, Workbook workbook) {

        CellStyle cellStyleDateFormat = setCellStyleDateRegulation(workbook);
        CellStyle cellStyle = setCellStyleFont(workbook);

        Row rowCode = sheet.getRow(0);
        rowCode.getCell(2).setCellValue(regulationDto.getCode());
        rowCode.getCell(2).setCellStyle(cellStyle);

        Row rowAuctioneer = sheet.getRow(1);
        if (StringUtils.isNotBlank(regulationDto.getNameAccount())) {
            rowAuctioneer.getCell(2).setCellValue(regulationDto.getNameAccount());
        } else {
            rowAuctioneer.getCell(2).setCellValue("Chưa phân công đấu giá viên");
        }
        rowAuctioneer.getCell(2).setCellStyle(cellStyle);

        Row rowStartTime = sheet.getRow(2);
        rowStartTime.getCell(2).setCellValue(regulationDto.getStartTime());
        rowStartTime.getCell(2).setCellStyle(cellStyleDateFormat);

//        Row rowEndTime = sheet.getRow(3);
//        rowEndTime.getCell(2).setCellValue(regulationDto.getEndTime());
//        rowEndTime.getCell(2).setCellStyle(cellStyleDateFormat);

        Row rowAuctionFormality = sheet.getRow(3);
        rowAuctionFormality.getCell(2).setCellValue(regulationDto.getAuctionFormalityName());
        rowAuctionFormality.getCell(2).setCellStyle(cellStyle);

        Row rowAuctionMethod = sheet.getRow(4);
        rowAuctionMethod.getCell(2).setCellValue(regulationDto.getAuctionMethodName());
        rowAuctionMethod.getCell(2).setCellStyle(cellStyle);

        Row rowRound = sheet.getRow(5);
        rowRound.getCell(2).setCellValue(regulationDto.getNumberOfRounds() + " vòng");
        rowRound.getCell(2).setCellStyle(cellStyle);

        Row rowTimePerRow = sheet.getRow(6);
        rowTimePerRow.getCell(2).setCellValue(regulationDto.getTimePerRound() + " phút");
        rowTimePerRow.getCell(2).setCellStyle(cellStyle);
    }

    //Tạo bảng và ghi dữ liệu hình thức bỏ phiếu
    public void createTableAndWriteDataVote(Sheet sheet, List<AssetDto> assetDtoList, Workbook workbook, int i) {
        CellStyle cellStyleDateFormat = setCellStyleDateFormmat(workbook);
        CellStyle cellStyleBorder = setCellStyleBorder(workbook);
        CellStyle cellStyleTitle = setCellStyleTitle(workbook);
        CellStyle cellStyleAlign = setCellStyleAlign(workbook);
        CellStyle cellStyle = setCellStyleFont(workbook);
        CellStyle cellStyleMoney = setCellStyleMoney(workbook);

        for (AssetDto assetDto : assetDtoList) {

            Row rowAssetName = sheet.createRow(i);
            rowAssetName.createCell(0).setCellValue("Tài sản: ");
            rowAssetName.getCell(0).setCellStyle(cellStyle);
            sheet.addMergedRegion(new CellRangeAddress(i, i, 0, 1));
            rowAssetName.createCell(2).setCellValue(assetDto.getName());
            rowAssetName.getCell(2).setCellStyle(cellStyle);

            Row rowStartPrice = sheet.createRow(i + 1);
            rowStartPrice.createCell(0).setCellValue("Giá khởi điểm: ");
            rowStartPrice.getCell(0).setCellStyle(cellStyle);
            sheet.addMergedRegion(new CellRangeAddress(i + 1, i + 1, 0, 1));
            rowStartPrice.createCell(2).setCellValue(assetDto.getStartingPrice() + " (đồng)");
            rowStartPrice.getCell(2).setCellStyle(cellStyle);

            Row rowStepPrice = sheet.createRow(i + 2);
            rowStepPrice.createCell(0).setCellValue("Bước giá: ");
            rowStepPrice.getCell(0).setCellStyle(cellStyle);
            sheet.addMergedRegion(new CellRangeAddress(i + 2, i + 2, 0, 1));
            rowStepPrice.createCell(2).setCellValue(assetDto.getPriceStep() + " (đồng)");
            rowStepPrice.getCell(2).setCellStyle(cellStyle);

            if (assetDto.getStatus() == DbConstant.ASSET_STATUS_CANCELED) {
                rowAssetName.createCell(3).setCellValue("Tài sản đã bị tạm dừng đấu giá");
                rowAssetName.getCell(3).setCellStyle(cellStyle);
                i = i + 5;
                continue;
            }

            int j = 1;
            int n = 1;
            int m = 0;
            int k = 0;
            Row rowTitle = sheet.createRow(i + 3);
            Row rowSubTitle = sheet.createRow(i + 4);

            if (CollectionUtils.isEmpty(assetDto.getPriceRoundList())) {
                continue;
            }

            for (PriceRoundDto priceRoundDto : assetDto.getPriceRoundList()) {
                rowTitle.createCell(m).setCellValue("STT");
                rowSubTitle.createCell(m);
                rowSubTitle.getCell(m).setCellStyle(cellStyleTitle);
                rowTitle.getCell(m).setCellStyle(cellStyleTitle);
                sheet.addMergedRegion(new CellRangeAddress(i + 3, i + 4, m, m));

                rowTitle.createCell(m + 1).setCellValue("Họ và tên");
                rowSubTitle.createCell(m + 1);
                rowSubTitle.getCell(m + 1).setCellStyle(cellStyleTitle);
                rowTitle.getCell(m + 1).setCellStyle(cellStyleTitle);
                sheet.addMergedRegion(new CellRangeAddress(i + 3, i + 4, m + 1, m + 1));

                rowTitle.createCell(m + 2).setCellValue("Vòng " + j);
                rowTitle.createCell(m + 3);
                rowTitle.getCell(m + 2).setCellStyle(cellStyleTitle);
                rowTitle.getCell(m + 3).setCellStyle(cellStyleTitle);
                sheet.addMergedRegion(new CellRangeAddress(i + 3, i + 3, m + 2, m + 3));

                rowSubTitle.createCell(m + 2).setCellValue("Thời gian");
                rowSubTitle.getCell(m + 2).setCellStyle(cellStyleTitle);

                rowSubTitle.createCell(m + 3).setCellValue("Mức trả giá (đồng)");
                rowSubTitle.getCell(m + 3).setCellStyle(cellStyleTitle);

                if (CollectionUtils.isEmpty(priceRoundDto.getBidDtoList())) {
                    continue;
                }

                for (BidDto bidDto : priceRoundDto.getBidDtoList()) {
                    Row row = sheet.getRow(n + i + 4);
                    if (row == null) {
                        row = sheet.createRow(n + i + 4);
                    }
                    sheet.setColumnWidth(m, Constant.COLUMN_WIDTH_STT);
                    row.createCell(m).setCellValue(n);
                    row.getCell(m).setCellStyle(cellStyleAlign);

                    sheet.setColumnWidth(m + 1, Constant.COLUMN_REGISTER_NAME_WIDTH);
                    row.createCell(m + 1).setCellValue(bidDto.getFullName() + " (" + bidDto.getCode() + ")");
                    row.getCell(m + 1).setCellStyle(cellStyleBorder);

                    sheet.setColumnWidth(m + 2, Constant.COLUMN_WIDTH);
                    row.createCell(m + 2).setCellValue(bidDto.getTime());
                    row.getCell(m + 2).setCellStyle(cellStyleDateFormat);

                    sheet.setColumnWidth(m + 3, Constant.COLUMN_WIDTH);
                    row.createCell(m + 3).setCellValue(bidDto.getMoney());
                    row.getCell(m + 3).setCellStyle(cellStyleMoney);
                    n++;
                }
                j++;
                m = m + 5;
                n = 1;

                k = priceRoundDto.getBidDtoList().size();
            }
            i = i + k + 7;
        }
    }

    //Download file hình thức bỏ phiếu
    public StreamedContent downloadFileVote(Integer auctionStatus) throws Exception {
        // Mở kết nối đến file
        ServletContext servletContext = ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext());
        String templateFilePath = servletContext.getRealPath(Constant.TEMPLATE_EXPORT_FILE_VOTE);
        FileInputStream inputStream = new FileInputStream(new File(templateFilePath));

        Workbook workbook = new XSSFWorkbook(inputStream);
        Sheet sheet = workbook.getSheetAt(0);

        getDataList(auctionStatus);

        setValueCellRegulationVote(sheet, regulationDto, workbook);
        createTableAndWriteDataVote(sheet, assetDtoList, workbook, Constant.INDEX_ROW_START_EXPORT + 1);

        // Truyền vào dữ liệu, tên file, file template, file export
        String fileName = ExportUtil.getFileNameExport("LichSuTraGia_" + regulationDto.getCode());
        String reportFilePath = servletContext.getRealPath(Constant.REPORT_EXPORT_FILE_VOTE + fileName);
        FileOutputStream outputStream = new FileOutputStream(reportFilePath);
        workbook.write(outputStream);
        workbook.close();

        //Download file
        InputStream osStream = new FileInputStream(reportFilePath);
        return new DefaultStreamedContent(osStream, "application/vnd.ms-excel", fileName);
    }

    //Set fonr style
    public CellStyle setCellStyleFont(Workbook workbook) {
        CellStyle cellStyle = workbook.createCellStyle();
        Font font = setStyleFont(workbook);
        cellStyle.setFont(font);
        return cellStyle;
    }

    //Set format date style
    public CellStyle setCellStyleDateFormmat(Workbook workbook) {
        CreationHelper createHelper = workbook.getCreationHelper();
        CellStyle cellStyle = setCellStyleBorder(workbook);
        cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("HH:mm:ss, dd/MM/yyyy"));
        cellStyle.setAlignment(HorizontalAlignment.LEFT);
        return cellStyle;
    }

    //Set format date style for regulation
    public CellStyle setCellStyleDateRegulation(Workbook workbook) {
        CreationHelper createHelper = workbook.getCreationHelper();
        CellStyle cellStyle = workbook.createCellStyle();
        Font font = setStyleFont(workbook);
        cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("HH:mm:ss, dd/MM/yyyy"));
        cellStyle.setAlignment(HorizontalAlignment.LEFT);
        cellStyle.setFont(font);
        return cellStyle;
    }

    //Set format money
    public CellStyle setCellStyleMoney(Workbook workbook) {
        CreationHelper createHelper = workbook.getCreationHelper();
        CellStyle cellStyle = setCellStyleBorder(workbook);
        Font font = setStyleFont(workbook);
        cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("#,##0"));
        cellStyle.setAlignment(HorizontalAlignment.RIGHT);
        cellStyle.setFont(font);
        return cellStyle;
    }

    //Set border for cell
    public CellStyle setCellStyleBorder(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = setStyleFont(workbook);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setTopBorderColor(IndexedColors.BLACK.getIndex());
        style.setBottomBorderColor(IndexedColors.BLACK.getIndex());
        style.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        style.setRightBorderColor(IndexedColors.BLACK.getIndex());
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setFont(font);
        return style;
    }

    //Set style for title
    public CellStyle setCellStyleTitle(Workbook workbook) {
        CellStyle cellStyleAlign = setCellStyleBorder(workbook);
        cellStyleAlign.setAlignment(HorizontalAlignment.CENTER);
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontName("Times New Roman");
        font.setFontHeightInPoints((short) 13);
        cellStyleAlign.setFont(font);
        return cellStyleAlign;
    }

    //Set font for worbook
    public Font setStyleFont(Workbook workbook) {
        Font font = workbook.createFont();
        font.setFontName("Times New Roman");
        font.setFontHeightInPoints((short) 13);
        return font;
    }

    //Set cell style align
    public CellStyle setCellStyleAlign(Workbook workbook) {
        CellStyle cellStyleAlign = setCellStyleBorder(workbook);
        cellStyleAlign.setAlignment(HorizontalAlignment.CENTER);
        Font font = setStyleFont(workbook);
        cellStyleAlign.setFont(font);
        return cellStyleAlign;
    }
}
