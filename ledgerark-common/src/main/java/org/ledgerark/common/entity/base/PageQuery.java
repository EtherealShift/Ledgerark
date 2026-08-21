package org.ledgerark.common.entity.base;


import lombok.Data;


/**
 * 通用分页查询参数
 */
@Data
public class PageQuery {

    // 页码，从 1 开始
    private Integer pageNum;

    // 每页条数
    private Integer pageSize;

}
