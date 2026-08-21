package org.ledgerark.system.entity.vo;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 分页响应VO
 *
 * @author LedgerArk
 */

@Data
@Builder
public class SysPageResponseVO {

    /**
     * 查询数据列表
     */
    private List<?> records;

    /**
     * 总数
     */
    private long total;
    /**
     * 每页显示条数，默认 10
     */
    private long size;

    /**
     * 当前页
     */
    private long current;




}
