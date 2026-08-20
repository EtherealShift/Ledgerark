package org.ledgerark.common.entity.base;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;


/**
 * Entity基类
 *
 */
@Data
public class BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    // 主键
    private Long id;

    // 创建者
    private String createBy;

    // 更新者
    private String updateBy;

    // 创建时间
    private Date createTime;

    // 更新时间
    private Date updateTime;

    // 备注
    private  String remark;


}

