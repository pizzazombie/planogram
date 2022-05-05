package com.adidas.tsar.rest;

import com.adidas.tsar.dto.BaseResponse;
import com.adidas.tsar.mapper.ResponseFactory;
import com.adidas.tsar.service.planogram.PlanogramCalculationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Api(tags = "Planogram API")
@Slf4j
@Validated
@RestController
@RequestMapping("/planogram")
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class PlanogramController {

    private final PlanogramCalculationService planogramCalculationService;

    @ApiOperation(value = "Start calculate planogram")
    @PostMapping
    public ResponseEntity<BaseResponse<Void>> calcPlanogramPriorities() {
        planogramCalculationService.calculatePlanogram();
        return ResponseEntity.ok(ResponseFactory.getSuccessModifyResponse(
            "Planogram was successfully calculated",
            planogramCalculationService.countOfPlanograms()
        ));
    }

}
