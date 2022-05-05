package com.adidas.tsar.rest;

import com.adidas.tsar.dto.BaseErrorResponse;
import com.adidas.tsar.dto.BaseRequest;
import com.adidas.tsar.dto.BaseResponse;
import com.adidas.tsar.dto.vmstandard.VmStandardCreateDto;
import com.adidas.tsar.dto.vmstandard.VmStandardEditDto;
import com.adidas.tsar.dto.vmstandard.VmStandardResponse;
import com.adidas.tsar.mapper.ResponseFactory;
import com.adidas.tsar.service.VmStandardService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
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


@Api(tags = "VM Standards API")
@Slf4j
@Validated
@RestController
@RequestMapping("/vmstandards")
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class VmStandardController {

    private static final String EXPORT_HEADER_VALUE = "attachment; filename=";

    private final VmStandardService vmStandardService;
    @Value("${app.current-user}")
    private String currentUser;

    @ApiOperation(value = "Retrieve VM Standards by specified parameters", response = VmStandardResponse.class)
    @ApiResponses(value = {
        @ApiResponse(code = 400, message = "Bad Request", response = BaseErrorResponse.class),
        @ApiResponse(code = 500, message = "Unexpected Internal Error", response = BaseErrorResponse.class)
    })
    @GetMapping
    public ResponseEntity<BaseResponse<List<VmStandardResponse>>> getVmStandards(
        @ApiParam(value = "Results page you want to retrieve (0..N)", example = "10")
        @RequestParam(value = "page", defaultValue = "0") int page,

        @ApiParam(value = "Number of records per page", example = "10")
        @RequestParam(value = "size", defaultValue = "50") int size
    ) {
        Pageable paging = PageRequest.of(page, size);
        final var vmStandards = vmStandardService.findVmStandards(paging);
        return ResponseEntity.ok(ResponseFactory.getPageResponse("The list of VM Standard", vmStandards));
    }

    @ApiOperation(value = "Create VM Standards", code = 201, response = BaseResponse.class)
    @PostMapping
    public ResponseEntity<BaseResponse<Void>> createVmStandards(@RequestBody @Valid BaseRequest<List<VmStandardCreateDto>> requestBody) {
        vmStandardService.createVmStandards(requestBody.getData(), currentUser);
        return new ResponseEntity<>(ResponseFactory.getSuccessModifyResponse("New VM Standards has been uploaded", vmStandardService.countOfStandards()), HttpStatus.CREATED);
    }

    @ApiOperation(value = "Edit VM Standard", response = BaseResponse.class)
    @PatchMapping("/{standardId}")
    public ResponseEntity<BaseResponse<Void>> updateVmStandard(@PathVariable long standardId, @RequestBody @Valid BaseRequest<VmStandardEditDto> requestBody) {
        vmStandardService.updatePresMin(standardId, requestBody.getData().getPresMin(), currentUser);
        return ResponseEntity.ok(ResponseFactory.getSuccessModifyResponse("VM Standard has been changed", vmStandardService.countOfStandards()));
    }

    @ApiOperation(value = "Delete VM Standards", response = BaseResponse.class)
    @DeleteMapping
    public ResponseEntity<BaseResponse<Void>> deleteVmStandards(
        @ApiParam(value = "List of ids to be deleted", required = true)
        @RequestBody final BaseRequest<List<Long>> requestBody
    ) {
        vmStandardService.deleteVmStandards(requestBody.getData());
        return ResponseEntity.ok(ResponseFactory.getSuccessModifyResponse("VM Standards has been deleted", vmStandardService.countOfStandards()));
    }

    @ApiOperation(value = "Import VM Standards", response = BaseResponse.class)
    @PutMapping(value = "/import")
    public ResponseEntity<BaseResponse<Void>> importVmStandards(
        @ApiParam(value = "Imported Excel file in binary format", required = true)
        @RequestParam("file") MultipartFile file
    ) {
        vmStandardService.importFromExcel(file, currentUser);
        return ResponseEntity.ok(ResponseFactory.getSuccessModifyResponse("New VM Standards has been imported", vmStandardService.countOfStandards()));
    }

    @ApiOperation(value = "Export VM Standards as excel file")
    @GetMapping(value = "/export")
    public ResponseEntity<?> exportVmStandards() {
        log.info("Start exporting VM Standards in excel");
        InputStream reportFile = vmStandardService.exportToExcel();
        HttpHeaders headers = new HttpHeaders();
        String fileName = vmStandardService.buildExportFileName(LocalDateTime.now());
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.add(HttpHeaders.CONTENT_DISPOSITION, EXPORT_HEADER_VALUE + fileName);
        log.info("Exporting VM Standards completed: {}", fileName);
        return ResponseEntity.ok()
            .headers(headers)
            .body(new InputStreamResource(reportFile));
    }

}
