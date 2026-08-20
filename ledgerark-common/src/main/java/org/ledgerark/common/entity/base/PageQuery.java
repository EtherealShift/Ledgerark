package org.ledgerark.common.entity.base;


import lombok.Data;


/**
 * 通用分页查询参数
 */
@Data
public class PageQuery {

    /**
     * 单页最大条数，防止恶意大分页拖垮数据库
     */
    public static final int MAX_PAGE_SIZE = 100;

    // 页码，从 1 开始
    private Integer pageNum;

    // 每页条数
    private Integer pageSize;

}
