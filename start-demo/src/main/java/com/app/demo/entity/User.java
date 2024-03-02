package com.app.demo.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

/**
 * 用户表
 * @author qiangt
 * @since 2022-10-24
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@ApiModel(description = "用户表")
public class User implements Serializable {

    @ApiModelProperty(value = "id")
    private String id;

    @NotEmpty(message = "用户名不能为空")
    @ApiModelProperty(value = "用户名")
    private String userName;

}
