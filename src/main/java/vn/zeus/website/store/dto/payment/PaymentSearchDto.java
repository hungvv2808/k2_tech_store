package vn.zeus.website.store.dto.payment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.zeus.website.store.dto.BaseSearchDto;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PaymentSearchDto extends BaseSearchDto {
    private Long accountId;
    private Long assetId;
    private Long paymentFormality;
    private Date fromDate;
    private Date toDate;
    private Integer status;
    private List<Integer> statusList;
    private String code;
    private String regulationCode;
}
