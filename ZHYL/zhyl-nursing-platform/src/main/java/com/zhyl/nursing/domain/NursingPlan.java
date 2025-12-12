package com.zhyl.nursing.domain;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.zhyl.common.annotation.Excel;
import com.zhyl.common.core.domain.BaseEntity;

/**
 * 护理计划对象 nursing_plan
 *
 * @author ruoyi
 * @date 2025-12-05
 */
        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        @ApiModel("护理计划实体")
        public class NursingPlan extends BaseEntity
        {
        private static final long serialVersionUID = 1L;

                /** 编号 */
        @ApiModelProperty("编号")
        private Integer id;

                /** 排序号 */
                @Excel(name = "排序号")
        @ApiModelProperty("排序号")
        private Integer sortNo;

                /** 名称 */
                @Excel(name = "名称")
        @ApiModelProperty("名称")
        private String planName;

                /** 状态  */
                @Excel(name = "状态 ")
        @ApiModelProperty("状态 ")
        private Long status;

                                                                                                                                                                                                                    }