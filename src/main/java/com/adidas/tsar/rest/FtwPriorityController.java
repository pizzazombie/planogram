package com.adidas.tsar.rest;

import com.adidas.tsar.dto.BaseErrorResponse;
import com.adidas.tsar.dto.BaseRequest;
import com.adidas.tsar.dto.BaseResponse;
import com.adidas.tsar.dto.ftwpriority.FtwPriorityCreateDto;
import com.adidas.tsar.dto.ftwpriority.FtwPriorityEditDto;
import com.adidas.tsar.dto.ftwpriority.FtwPriorityResponse;
import com.adidas.tsar.mapper.ResponseFactory;
import com.adidas.tsar.service.FtwPriorityService;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.List;


@Api(tags = "FTW Priorities API")
@Slf4j
@Validated
@RestController
@RequestMapping("/ftwpriorities")
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class FtwPriorityController {

    private static final String EXPORT_HEADER_VALUE = "attachment; filename=";

    private final FtwPriorityService ftwPriorityService;

    @Value("${app.current-user}")
    private String currentUser;

    @ApiOperation(value = "Retrieve FTW Priorities by specified parameters", response = FtwPriorityResponse.class)
    @ApiResponses(value = {
        @ApiResponse(code = 400, message = "Bad Request", response = BaseErrorResponse.Error.class),
        @ApiResponse(code = 500, message = "Unexpected Internal Error", response = BaseErrorResponse.class)
    })
    @GetMapping
    public ResponseEntity<BaseResponse<List<FtwPriorityResponse>>> getFtwPriorities(
        @ApiParam(value = "Results page you want to retrieve (0..N)", example = "10")
        @RequestParam(value = "page", defaultValue = "0") int page,

        @ApiParam(value = "Number of records per page", example = "10")
        @RequestParam(value = "size", defaultValue = "50") int size
    ) {
        Pageable paging = PageRequest.of(page, size);
        final var ftwPriorities = ftwPriorityService.findFtwPriorities(paging);
        return ResponseEntity.ok(ResponseFactory.getPageResponse("The list of FTW Priority", ftwPriorities));
    }

    @ApiOperation(value = "Create FTW Priorities", code = 201, response = BaseResponse.class)
    @PostMapping
    public ResponseEntity<BaseResponse<Void>> createFtwPriorities(@RequestBody @Valid BaseRequest<List<FtwPriorityCreateDto>> requestBody) {
        ftwPriorityService.createFtwPriorities(requestBody.getData(), currentUser);
        return new ResponseEntity<>(ResponseFactory.getSuccessModifyResponse("New FTW Priorities has been uploaded", ftwPriorityService.countOfPriorities()), HttpStatus.CREATED);
    }

    @ApiOperation(value = "Edit FTW Priority", response = BaseResponse.class)
    @PatchMapping("/{ftwPriorityId}")
    public ResponseEntity<BaseResponse<Void>> updateFtwPriority(@PathVariable long ftwPriorityId, @RequestBody @Valid BaseRequest<FtwPriorityEditDto> requestBody) {
        ftwPriorityService.updatePriority(ftwPriorityId, requestBody.getData().getPriority(), currentUser);
        return ResponseEntity.ok(ResponseFactory.getSuccessModifyResponse("FTW Priority has been changed", ftwPriorityService.countOfPriorities()));
    }

    @ApiOperation(value = "Delete FTW Priorities")
    @DeleteMapping
    public ResponseEntity<BaseResponse<Void>> deleteFtwPriorities(
        @ApiParam(value = "List of ids to be deleted", required = true)
        @RequestBody final BaseRequest<List<Long>> requestBody
    ) {
        ftwPriorityService.deleteFtwPriorities(requestBody.getData());
        return ResponseEntity.ok(ResponseFactory.getSuccessModifyResponse("FTW Priorities has been deleted", ftwPriorityService.countOfPriorities()));
    }


    @ApiOperation(value = "Import FTW Priorities", notes = "Import batch of new FTW Priorities from XLSX file")
    @PutMapping(value = "/import")
    public ResponseEntity<BaseResponse<Void>> importFtwPriority(
        @ApiParam(value = "Imported Excel file in binary format", required = true)
        @RequestParam("file") MultipartFile file
    ) {
        ftwPriorityService.importFromExcel(file, currentUser);
        return ResponseEntity.ok(ResponseFactory.getSuccessModifyResponse("New FTW Priorities has been imported", ftwPriorityService.countOfPriorities()));
    }

    @ApiOperation(value = "Export FTW Priorities as excel file")
    @GetMapping(value = "/export")
    public ResponseEntity<?> exportFtwPriorities() {
        InputStream reportFile = ftwPriorityService.exportToExcel();
        HttpHeaders headers = new HttpHeaders();
        String fileName = ftwPriorityService.buildExportFileName(LocalDateTime.now());
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.add(HttpHeaders.CONTENT_DISPOSITION, EXPORT_HEADER_VALUE + fileName);
        log.info("Exporting FTW Priorities completed: {}", fileName);
        return ResponseEntity.ok()
            .headers(headers)
            .body(new InputStreamResource(reportFile));
    }

}
