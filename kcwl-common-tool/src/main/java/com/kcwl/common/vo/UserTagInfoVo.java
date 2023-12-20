package com.kcwl.common.vo;

import lombok.Data;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author ckwl
 */
@Data
public class UserTagInfoVo {
    private CopyOnWriteArrayList<String> selectPlatformList;
    private CopyOnWriteArrayList<String> discardPlatformList;
}
