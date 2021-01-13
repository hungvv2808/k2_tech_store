package vn.compedia.website.auction.dto.system;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.primefaces.model.UploadedFile;
import vn.compedia.website.auction.model.DecisionNews;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DecisionNewsDto extends DecisionNews {
    protected String imagePath;
    protected String shortContent;
    @JsonIgnore
    private UploadedFile uploadedFile;


    public DecisionNewsDto(Long decisionNewsId, String title, String content, boolean type, boolean status, String decisionSummary) {
        super(decisionNewsId, title, content, type, status, decisionSummary);
    }
}
