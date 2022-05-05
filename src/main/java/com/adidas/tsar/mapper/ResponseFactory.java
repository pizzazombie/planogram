package com.adidas.tsar.mapper;

import com.adidas.tsar.dto.BaseResponse;
import com.adidas.tsar.dto.CreateEntitiesDto;
import lombok.experimental.UtilityClass;
import org.springframework.data.domain.Page;

import java.util.List;

@UtilityClass
public class ResponseFactory {

    public <T> BaseResponse<List<T>> getPageResponse(String title, Page<T> page) {
        return getPageResponse(title, null, page);
    }

    public <T> BaseResponse<List<T>> getPageResponse(String title, String details, Page<T> page) {
        return new BaseResponse<>(
            new BaseResponse.Info(title, details),
            new BaseResponse.Params(
                new BaseResponse.Page(page.getTotalPages(), page.getNumber(), page.getSize(), page.getTotalElements())
            ),
            page.getContent()
        );
    }

    public BaseResponse<Void> getSuccessModifyResponse(String title, long rowsCount) {
        return new BaseResponse<>(
            new BaseResponse.Info(
                title, null
            ),
            new BaseResponse.Params(new BaseResponse.Page(null, null, null, rowsCount)),
            null
        );
    }

    public BaseResponse<CreateEntitiesDto> getCreateEntitiesResponse(String title, Integer size) {
        return new BaseResponse<>(
            new BaseResponse.Info(
                title, null
            ),
            new BaseResponse.Params(new BaseResponse.Page()),
            new CreateEntitiesDto(size)
        );
    }

}