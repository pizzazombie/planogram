package com.adidas.tsar.rest;

import com.adidas.tsar.dto.BaseRequest;
import com.adidas.tsar.dto.BaseResponse;
import com.adidas.tsar.dto.CreateEntitiesDto;
import com.adidas.tsar.dto.matrix.MatrixCreateDto;
import com.adidas.tsar.mapper.ResponseFactory;
import com.adidas.tsar.service.MatrixService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

@Api(tags = "Matrix API")
@Slf4j
@Validated
@RestController
@RequestMapping("/matrix")
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class MatrixController {

    private final MatrixService matrixService;

    @ApiOperation(value = "Create Matrix", code = 201, response = BaseResponse.class)
    @PostMapping
    public ResponseEntity<BaseResponse<CreateEntitiesDto>> createMatrix(@RequestBody @Valid BaseRequest<List<MatrixCreateDto>> requestBody) {
        Integer savedEntities = matrixService.createMatrix(requestBody.getData());
        return new ResponseEntity<>(ResponseFactory.getCreateEntitiesResponse("New Matrix has been created", savedEntities), HttpStatus.CREATED);
    }

    @ApiOperation(value = "Delete Matrix", response = BaseResponse.class)
    @DeleteMapping
    public ResponseEntity<BaseResponse<Void>> deleteMatrix(
            @ApiParam(value = "Delete all Matrix with the TRUNCATE operator", example = "true")
            @RequestParam(value = "truncate", defaultValue = "false") boolean truncate,

            @RequestBody final BaseRequest<List<Long>> requestBody
    ) {
        if(truncate) {
            matrixService.truncateMatrix();
        } else {
            matrixService.deleteMatrix(requestBody.getData());
        }
        return ResponseEntity.ok(ResponseFactory.getSuccessModifyResponse("Matrix has been deleted", matrixService.countOfMatrix()));
    }

}
