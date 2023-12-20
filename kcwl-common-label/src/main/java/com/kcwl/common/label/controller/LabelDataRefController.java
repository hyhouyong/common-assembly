package com.kcwl.common.label.controller;


import com.kcwl.common.label.service.LabelDataRefService;
import com.kcwl.ddd.infrastructure.api.ResponseMessage;
import com.kcwl.framework.rest.helper.ResponseHelper;
import com.kcwl.support.label.command.LabelDataRefRemoveCommand;
import com.kcwl.support.label.command.LabelDataRefSaveBatchCommand;
import com.kcwl.support.label.command.LabelDataRefSaveCommand;
import com.kcwl.support.label.dto.LabelDataRefDto;
import com.kcwl.support.label.dto.LabelInfoAvailableDto;
import com.kcwl.support.label.query.LabelDataRefQuery;
import com.kcwl.support.label.query.LabelInfoAvailableQuery;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 数据与标签关联表 前端控制器
 * </p>
 *
 * @author renyp
 * @since 2023-03-29
 */
@Api(tags = "数据与标签 管理")
@RestController
@RequestMapping("/user/label")
public class LabelDataRefController {

    @Autowired
    private LabelDataRefService labelDataRefService;

    @PostMapping("/data_mark")
    @ApiOperation("数据打标")
    public ResponseMessage<Boolean> saveDataLabelRelationship(@RequestBody @Validated LabelDataRefSaveCommand labelDataRefSaveCommand) {
        return ResponseHelper.success(labelDataRefService.save(labelDataRefSaveCommand));
    }

    @PostMapping("/data_mark_batch")
    @ApiOperation("数据批量打标")
    public ResponseMessage<Boolean> saveDataLabelRelationship(@RequestBody @Validated LabelDataRefSaveBatchCommand labelDataRefSaveBatchCommand) {
        return ResponseHelper.success(labelDataRefService.saveBatchDataLabelRef(labelDataRefSaveBatchCommand));
    }

    @PostMapping("/remove")
    @ApiOperation("数据删除指定标签")
    public ResponseMessage<Boolean> removeRelatedLabel(@RequestBody @Validated(LabelDataRefRemoveCommand.Detail.class) LabelDataRefRemoveCommand labelDataRefRemoveCommand) {
        return ResponseHelper.success(labelDataRefService.removeRelatedLabel(labelDataRefRemoveCommand));
    }

    @PostMapping("/remove/all")
    @ApiOperation("数据删除全部标签")
    public ResponseMessage<Boolean> removeRelatedLabelAll(@RequestBody @Validated(LabelDataRefRemoveCommand.Batch.class) LabelDataRefRemoveCommand labelDataRefRemoveCommand) {
        return ResponseHelper.success(labelDataRefService.removeRelatedLabelAll(labelDataRefRemoveCommand));
    }

    @PostMapping("/list")
    @ApiOperation("查询数据标签")
    public ResponseMessage<List<LabelDataRefDto>> listRelatedLabel(@RequestBody @Validated LabelDataRefQuery labelDataRefQuery) {
        return ResponseHelper.success(labelDataRefService.listRelatedLabel(labelDataRefQuery));
    }


    @PostMapping("/availableList")
    @ApiOperation("查询标签可用标签列表")
    public ResponseMessage<List<LabelInfoAvailableDto>> availableList(@RequestBody @Validated LabelInfoAvailableQuery labelInfoAvailableQuery) {
        return ResponseHelper.success(labelDataRefService.availableList(labelInfoAvailableQuery));
    }



}

