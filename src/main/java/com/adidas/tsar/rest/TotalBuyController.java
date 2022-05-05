package com.adidas.tsar.rest;

import com.adidas.tsar.dto.BaseErrorResponse;
import com.adidas.tsar.dto.BaseRequest;
import com.adidas.tsar.dto.BaseResponse;
import com.adidas.tsar.dto.CreateEntitiesDto;
import com.adidas.tsar.dto.totalbuy.TotalBuyCreateDto;
import com.adidas.tsar.dto.totalbuy.TotalBuyResponse;
import com.adidas.tsar.mapper.ResponseFactory;
import com.adidas.tsar.service.TotalBuyService;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;


@Api(tags = "Total Buy API")
@Slf4j
@Validated
@RestController
@RequestMapping("/totalbuys")
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class TotalBuyController {

    private final TotalBuyService totalBuyService;
    @Value("${app.current-user}")
    private String currentUser;

    @ApiOperation(value = "Retrieve Total Buys by specified parameters", response = TotalBuyResponse.class)
    @ApiResponses(value = {
        @ApiResponse(code = 400, message = "Bad Request", response = BaseErrorResponse.class),
        @ApiResponse(code = 500, message = "Unexpected Internal Error", response = BaseErrorResponse.class)
    })
    @GetMapping
    public ResponseEntity<BaseResponse<List<TotalBuyResponse>>> getTotalBuys(
        @ApiParam(value = "Results page you want to retrieve (0..N)", example = "10")
        @RequestParam(value = "page", defaultValue = "0") int page,

        @ApiParam(value = "Number of records per page", example = "10")
        @RequestParam(value = "size", defaultValue = "50") int size
    ) {
        Pageable paging = PageRequest.of(page, size);
        final var totalBuys = totalBuyService.findTotalBuys(paging);
        return ResponseEntity.ok(ResponseFactory.getPageResponse("The list of Total Buy", totalBuys));
    }

    @ApiOperation(value = "Create Total Buys", code = 201, response = BaseResponse.class)
    @PostMapping
    public ResponseEntity<BaseResponse<CreateEntitiesDto>> createTotalBuys(@RequestBody @Valid BaseRequest<List<TotalBuyCreateDto>> requestBody) {
        Integer savedEntitiesCount = totalBuyService.createTotalBuys(requestBody.getData(), currentUser);
        return new ResponseEntity<>(ResponseFactory.getCreateEntitiesResponse("New Total Buys has been created", savedEntitiesCount), HttpStatus.CREATED);
    }

    @ApiOperation(value = "Delete Total Buys", response = BaseResponse.class)
    @DeleteMapping
    public ResponseEntity<BaseResponse<Void>> deleteTotalBuys(
        @ApiParam(value = "Delete all Total Buys with the TRUNCATE operator", example = "true")
        @RequestParam(value = "truncate", defaultValue = "false") boolean truncate,

        @RequestBody final BaseRequest<List<Long>> requestBody
    ) {
        if(truncate) {
            totalBuyService.truncateTotalBuys();
        } else {
            totalBuyService.deleteTotalBuys(requestBody.getData());
        }
        return ResponseEntity.ok(ResponseFactory.getSuccessModifyResponse("Total Buys has been deleted", totalBuyService.countOfTotalBuys()));
    }

}
