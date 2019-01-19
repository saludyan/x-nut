package nut.jpa.criteria;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Map;

@Data
@Accessors(chain = true)
@AllArgsConstructor
public class XJdbcType {
    private String processSql;
    private String countSql;
    private Map<String,Object> parameters;
}
