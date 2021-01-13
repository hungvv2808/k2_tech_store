package vn.compedia.website.auction.dto.base;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.compedia.website.auction.model.Function;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FunctionDto extends Function {
    private static final long serialVersionUID = 3945970797200255307L;

    private Integer functionRoleId;
}