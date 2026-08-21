package org.ledgerark.common.util;

import org.ledgerark.common.constant.CommonConstant;
import org.ledgerark.common.entity.base.PageQuery;

public class CommonUtils {


    /**
     * 通用分页参数校验
     */
    private void validatePageQuery(PageQuery pageQuery) {
        int current = (pageQuery.getPageNum() == null || pageQuery.getPageNum() < 1) ? 1 : pageQuery.getPageNum();
        int size = (pageQuery.getPageSize() == null || pageQuery.getPageSize() < 1) ? 10 : Math.min(pageQuery.getPageSize(), CommonConstant.MAX_PAGE_SIZE);
    }
}
