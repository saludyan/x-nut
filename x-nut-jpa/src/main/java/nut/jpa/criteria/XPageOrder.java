package nut.jpa.criteria;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@AllArgsConstructor
@Accessors(chain = true)
public class XPageOrder {
    private String property;
    private String direction;

}
