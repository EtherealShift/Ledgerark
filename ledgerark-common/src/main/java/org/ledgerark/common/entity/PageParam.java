package org.ledgerark.common.entity;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Getter;
import org.ledgerark.common.constant.CommonConstant;
import org.ledgerark.common.entity.base.PageQuery;

/**
 * 分页执行参数：由 PageQuery 经校验/兜底处理后得到，
 * 可直接用于构建 MyBatis-Plus Page&lt;T&gt;。
 */
@Getter
public class PageParam {

    /** 当前页码（≥1） */
    private final long current;
    /** 每页大小（1~MAX_PAGE_SIZE） */
    private final long size;

    private PageParam(long current, long size) {
        this.current = current;
        this.size = size;
    }

    /** 从查询入参构造：页码 <1 置 1；页大小越界截断到 MAX_PAGE_SIZE */
    public static PageParam of(PageQuery query) {
        int current = (query.getPageNum() == null || query.getPageNum() < 1) ? 1 : query.getPageNum();
        int size = (query.getPageSize() == null || query.getPageSize() < 1)
                ? 10 : Math.min(query.getPageSize(), CommonConstant.MAX_PAGE_SIZE);
        return new PageParam(current, size);
    }

    public <T> Page<T> toPage() {
        return new Page<>(current, size);
    }

}