package com.adidas.tsar.rest;

import com.adidas.tsar.dto.BaseRequest;
import com.adidas.tsar.dto.BaseResponse;
import com.adidas.tsar.dto.CreateEntitiesDto;
import com.adidas.tsar.dto.removal.RemovalCreateDto;
import com.adidas.tsar.mapper.ResponseFactory;
import com.adidas.tsar.service.RemovalService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@Api(tags = "Removal API")
@Slf4j
@Validated
@RestController
@RequestMapping("/removals")
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class RemovalController {

    private final RemovalService removalService;

    @ApiOperation(value = "Create Removals", code = 201, response = BaseResponse.class)
    @PostMapping
    public ResponseEntity<BaseResponse<CreateEntitiesDto>> createRemovals(@RequestBody @Valid BaseRequest<List<RemovalCreateDto>> requestBody) {
        Integer savedEntities = removalService.createRemovals(requestBody.getData());
        return new ResponseEntity<>(ResponseFactory.getCreateEntitiesResponse("New Removals has been created", savedEntities), HttpStatus.CREATED);
    }

    @ApiOperation(value = "Delete Removals", response = BaseResponse.class)
    @DeleteMapping
    public ResponseEntity<BaseResponse<Void>> deleteRemovals(
            @ApiParam(value = "Delete all Removals with the TRUNCATE operator", example = "true")
            @RequestParam(value = "truncate", defaultValue = "false") boolean truncate,

            @RequestBody final BaseRequest<List<Long>> requestBody
    ) {
        if(truncate) {
            removalService.truncateRemovals();
        } else {
            removalService.deleteRemovals(requestBody.getData());
        }
        return ResponseEntity.ok(ResponseFactory.getSuccessModifyResponse("Removals has been deleted", removalService.countOfRemovals()));
    }
}
