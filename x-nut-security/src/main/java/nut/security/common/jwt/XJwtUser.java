package nut.security.common.jwt;


import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@Accessors(chain = true)
public class XJwtUser {

    private String username;
    private Date createTime;
}
