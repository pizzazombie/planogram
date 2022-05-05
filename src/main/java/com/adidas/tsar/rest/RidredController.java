package com.adidas.tsar.rest;

import com.adidas.tsar.dto.BaseRequest;
import com.adidas.tsar.dto.BaseResponse;
import com.adidas.tsar.dto.CreateEntitiesDto;
import com.adidas.tsar.dto.ridred.RidredCreateDto;
import com.adidas.tsar.mapper.ResponseFactory;
import com.adidas.tsar.service.RidredService;
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

@Api(tags = "RidRed API")
@Slf4j
@Validated
@RestController
@RequestMapping("/ridred")
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class RidredController {

    private final RidredService ridredService;

    @ApiOperation(value = "Create Ridred", code = 201, response = BaseResponse.class)
    @PostMapping
    public ResponseEntity<BaseResponse<CreateEntitiesDto>> createRidred(@RequestBody @Valid BaseRequest<List<RidredCreateDto>> requestBody) {
        Integer savedEntities = ridredService.createRidred(requestBody.getData());
        return new ResponseEntity<>(ResponseFactory.getCreateEntitiesResponse("New Ridred has been created", savedEntities), HttpStatus.CREATED);
    }

    @ApiOperation(value = "Delete Ridred", response = BaseResponse.class)
    @DeleteMapping
    public ResponseEntity<BaseResponse<Void>> deleteRidRed(
            @ApiParam(value = "Delete all Ridred with the TRUNCATE operator", example = "true")
            @RequestParam(value = "truncate", defaultValue = "false") boolean truncate,
            @RequestBody final BaseRequest<List<Long>> requestBody
    ) {
        if(truncate) {
            ridredService.truncateRidRed();
        } else {
            ridredService.deleteRidRed(requestBody.getData());
        }
        return new ResponseEntity<>(ResponseFactory.getSuccessModifyResponse("Ridred has been deleted", ridredService.countOfRidRed()), HttpStatus.OK);
    }

}
