package com.adidas.tsar.rest;

import com.adidas.tsar.common.SearchUtils;
import com.adidas.tsar.dto.BaseResponse;
import com.adidas.tsar.dto.planogram.PlanogramResponseDto;
import com.adidas.tsar.dto.planogram.PlanogramSearchRequestDto;
import com.adidas.tsar.mapper.ResponseFactory;
import com.adidas.tsar.service.planogram.PlanogramCalculationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;


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
    public ResponseEntity<BaseResponse<Void>> calcPlanogram() {
        planogramCalculationService.calculatePlanogram();
        return ResponseEntity.ok(ResponseFactory.getSuccessModifyResponse(
            "Planogram was successfully calculated",
            planogramCalculationService.countOfPlanograms()
        ));
    }

    @ApiOperation(value = "Search planogram results", response = PlanogramResponseDto.class)
    @GetMapping
    public MappingJacksonValue searchPlanogramResults(
        @ApiParam(value = "filter string. Pattern: ?filter=<field>.<operation>:'<value>';<field2>.<operation>:'<value>'",
                example = "filter=code.contains='%AB12%';brand.name.contains='A';"
        )
        @RequestParam(value = "filter", defaultValue = "", required = false) String filter,

        @ApiParam(value = "Defines the fields to be returned", example = "fields=id,store,priority")
        @RequestParam(value = "fields", required = false) List<String> fields,

        @ApiParam(value = "Results page you want to retrieve (0..N)", example = "10")
        @RequestParam(value = "page", defaultValue = "0") int page,

        @ApiParam(value = "Number of records per page", example = "10")
        @RequestParam(value = "size", defaultValue = "50") int size
    ) {
        Pageable paging = PageRequest.of(page, size);
        //TODO make a filtering feature
        final var planograms = planogramCalculationService.getPlanograms(filter, paging);
        final var response = ResponseFactory.getPageResponse(
            "The List of Planogram Results",
            planograms
        );
        return SearchUtils.filterFields(response, fields);
    }


}
