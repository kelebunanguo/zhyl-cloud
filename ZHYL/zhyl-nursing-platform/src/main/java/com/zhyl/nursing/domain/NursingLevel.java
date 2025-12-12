package com.zhyl.nursing.domain;

import java.math.BigDecimal;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.zhyl.common.annotation.Excel;
import com.zhyl.common.core.domain.BaseEntity;

/**
 * 护理等级对象 nursing_level
 *
 * @author ruoyi
 * @date 2025-12-05
 */
        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        @ApiModel("护理等级实体")
        public class NursingLevel extends BaseEntity
        {
        private static final long serialVersionUID = 1L;

                /** 主键ID */
        @ApiModelProperty("主键ID")
        private Long id;

                /** 等级名称 */
                @Excel(name = "等级名称")
        @ApiModelProperty("等级名称")
        private String name;

                /** 护理计划ID */
                @Excel(name = "护理计划ID")
        @ApiModelProperty("护理计划ID")
        private Long planId;

                /** 护理费用 */
                @Excel(name = "护理费用")
        @ApiModelProperty("护理费用")
        private BigDecimal fee;

                /** 状态 */
                @Excel(name = "状态")
        @ApiModelProperty("状态")
        private Integer status;

                /** 等级说明 */
                @Excel(name = "等级说明")
        @ApiModelProperty("等级说明")
        private String description;

                                                                                                                                                                                                                    }