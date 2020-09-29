package tech.neverzore.common.domain.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * @Author: zhouzb
 * @Date: 2019/12/19
 */
@Data
@EqualsAndHashCode(callSuper = true)
public abstract class TraceableModel extends BaseModel {
    public static Integer DELETED = 1;
    public static Integer UN_DELETED = 0;

    /**
     * 创建者ID
     */
    private Long cid;
    private Long uid;
    private LocalDateTime cdt;
    private LocalDateTime udt;
    private Integer del;
}
