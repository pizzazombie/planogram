package com.adidas.tsar.service;

import com.adidas.tsar.BaseIntegrationTest;
import com.adidas.tsar.PlanogramApplication;
import com.adidas.tsar.TestUtils;
import com.adidas.tsar.data.VmStandardRepository;
import com.adidas.tsar.domain.VmStandard;
import com.adidas.tsar.dto.BrandDto;
import com.adidas.tsar.dto.RmhCategoryDto;
import com.adidas.tsar.dto.RmhGenderAgeDto;
import com.adidas.tsar.dto.RmhProductDivisionDto;
import com.adidas.tsar.dto.RmhProductTypeDto;
import com.adidas.tsar.dto.SizeScaleDto;
import com.adidas.tsar.dto.vmstandard.VmStandardCreateDto;
import com.adidas.tsar.dto.vmstandard.VmStandardKey;
import com.adidas.tsar.dto.vmstandard.VmStandardResponse;
import com.adidas.tsar.exceptions.AppException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.ResourceUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Slf4j
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
@SpringBootTest(
    classes = PlanogramApplication.class,
    webEnvironment = SpringBootTest.WebEnvironment.MOCK,
    properties = {"spring.liquibase.enabled=false"}
)
class VmStandardServiceTest extends BaseIntegrationTest {

    private static final String XLSX_IMPORT_FILE_PATH = "classpath:import/vm-standard-right.xlsx";

    @Autowired
    private VmStandardService vmStandardService;

    @MockBean
    private VmStandardRepository vmStandardRepository;

    @Captor
    private ArgumentCaptor<List<VmStandard>> vmStandardListCaptor;

    @Captor
    private ArgumentCaptor<VmStandard> vmStandardCaptor;

    @Captor
    private ArgumentCaptor<Collection<VmStandardKey>> vmStandardKeyListCaptor;

    @Test
    void findVmStandards_dataExists_ok() {
        var standards = List.of(
            TestUtils.prepareStandard(1, BRAND, AGE, CATEGORY, PRODUCT_TYPE, DIVISION, SIZE_SCALE, 1),
            TestUtils.prepareStandard(2, BRAND_2, AGE, CATEGORY, PRODUCT_TYPE, DIVISION, SIZE_SCALE, 1)
        );
        when(vmStandardRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(standards));

        final Page<VmStandardResponse> vmStandards = vmStandardService.findVmStandards(Pageable.ofSize(PAGE_SIZE));

        final List<Long> ids = vmStandards.getContent().stream().map(VmStandardResponse::getId).collect(Collectors.toList());
        assertEquals(List.of(1L, 2L), ids);
    }

    @Test
    void createVmStandards_dataNotExists_created() {
        var createDtoList = List.of(
            new VmStandardCreateDto(BRAND.getName(), AGE.getName(), CATEGORY.getName(), PRODUCT_TYPE.getName(), DIVISION.getName(), SIZE_SCALE.getName(), 1),
            new VmStandardCreateDto(BRAND_2.getName(), AGE.getName(), CATEGORY.getName(), PRODUCT_TYPE.getName(), DIVISION.getName(), SIZE_SCALE.getName(), 2)
        );
        when(vmStandardRepository.findAllByKeys(any())).thenReturn(Lists.emptyList());

        vmStandardService.createVmStandards(createDtoList, USER);

        verify(vmStandardRepository, atLeastOnce()).saveAll(vmStandardListCaptor.capture());
        var createdStandards = vmStandardListCaptor.getValue();
        createdStandards.sort(Comparator.comparingInt(VmStandard::getPresMin));
        assertEquals(2, createdStandards.size());
        verifyStandard(createdStandards.get(0), BRAND, AGE, CATEGORY, PRODUCT_TYPE, DIVISION, SIZE_SCALE, 1);
        verifyStandard(createdStandards.get(1), BRAND_2, AGE, CATEGORY, PRODUCT_TYPE, DIVISION, SIZE_SCALE, 2);
    }

    @Test
    void createVmStandards_dataExists_exceptionThrown() {
        var createDtoList = List.of(
            new VmStandardCreateDto(BRAND.getName(), AGE.getName(), CATEGORY.getName(), PRODUCT_TYPE.getName(), DIVISION.getName(), SIZE_SCALE.getName(), 1)
        );
        var existEntity = TestUtils.prepareStandard(1, BRAND, AGE, CATEGORY, PRODUCT_TYPE, DIVISION, SIZE_SCALE, 1);
        when(vmStandardRepository.findAllByKeys(vmStandardKeyListCaptor.capture())).thenReturn(Collections.singletonList(existEntity));

        assertThrows(AppException.class, () -> vmStandardService.createVmStandards(createDtoList, USER));

        final var standardKey = vmStandardKeyListCaptor.getValue().iterator().next();
        assertEquals(BRAND.getId(), standardKey.getBrandId());
        assertEquals(AGE.getId(), standardKey.getRmhGenderAgeId());
        assertEquals(CATEGORY.getId(), standardKey.getRmhCategoryId());
        assertEquals(PRODUCT_TYPE.getId(), standardKey.getRmhProductTypeId());
        assertEquals(DIVISION.getId(), standardKey.getRmhProductDivisionId());
        assertEquals(SIZE_SCALE.getId(), standardKey.getSizeScaleId());
        verify(vmStandardRepository, never()).saveAll(any());
    }

    @Test
    void createVmStandards_duplicationInRequestBody_exceptionThrown() {
        var createDtoList = List.of(
            new VmStandardCreateDto(BRAND.getName(), AGE.getName(), CATEGORY.getName(), PRODUCT_TYPE.getName(), DIVISION.getName(), SIZE_SCALE.getName(), 1),
            new VmStandardCreateDto(BRAND.getName(), AGE.getName(), CATEGORY.getName(), PRODUCT_TYPE.getName(), DIVISION.getName(), SIZE_SCALE.getName(), 2)
        );

        assertThrows(AppException.class, () -> vmStandardService.createVmStandards(createDtoList, USER));
    }

    @Test
    void updatePresMin_dataExists_changed() {
        int existsId = 1;
        var existEntity = TestUtils.prepareStandard(existsId, BRAND, AGE, CATEGORY, PRODUCT_TYPE, DIVISION, SIZE_SCALE, 1);
        when(vmStandardRepository.findById(any())).thenReturn(Optional.of(existEntity));

        vmStandardService.updatePresMin(existsId, 2, USER);

        verify(vmStandardRepository, atLeastOnce()).save(vmStandardCaptor.capture());
        final var changedStandard = vmStandardCaptor.getValue();
        verifyStandard(changedStandard, BRAND, AGE, CATEGORY, PRODUCT_TYPE, DIVISION, SIZE_SCALE, 2);
    }

    @Test
    void deleteVmStandards_dataExists_deleted() {
        var deleteIds = List.of(1L, 2L);

        vmStandardService.deleteVmStandards(deleteIds);

        verify(vmStandardRepository, atLeastOnce()).deleteAllById(longListCaptor.capture());
        assertEquals(deleteIds, longListCaptor.getValue());
    }

    @Test
    void importFromExcel_validValues_imported() throws Exception {
        var uploadFile = new MockMultipartFile(
            "file",
            "vm-standard-right.xlsx",
            MediaType.MULTIPART_FORM_DATA_VALUE,
            FileUtils.readFileToByteArray(ResourceUtils.getFile(XLSX_IMPORT_FILE_PATH))
        );

        vmStandardService.importFromExcel(uploadFile, USER);

        verify(vmStandardRepository, atLeastOnce()).saveAll(vmStandardListCaptor.capture());
        final var createdStandards = vmStandardListCaptor.getValue();
        createdStandards.sort(Comparator.comparingInt(VmStandard::getPresMin));
        assertEquals(6, createdStandards.size());
        verifyStandard(createdStandards.get(0), BRAND_2, AGE, CATEGORY, PRODUCT_TYPE, DIVISION, SIZE_SCALE, 1);
        verifyStandard(createdStandards.get(1), BRAND_2, AGE_2, CATEGORY_2, PRODUCT_TYPE, DIVISION_FOOTWEAR, SIZE_SCALE, 2);
        verifyStandard(createdStandards.get(2), BRAND_2, AGE_2, CATEGORY, PRODUCT_TYPE, DIVISION, SIZE_SCALE, 3);
        verifyStandard(createdStandards.get(3), BRAND, AGE_2, CATEGORY_2, PRODUCT_TYPE_2, DIVISION_FOOTWEAR, SIZE_SCALE_2, 4);
        verifyStandard(createdStandards.get(4), BRAND, AGE, CATEGORY, PRODUCT_TYPE_2, DIVISION, SIZE_SCALE_2, 5);
        verifyStandard(createdStandards.get(5), BRAND, AGE, CATEGORY_2, PRODUCT_TYPE_2, DIVISION_FOOTWEAR, SIZE_SCALE_2, 6);
    }


    private void verifyStandard(VmStandard vmStandard, BrandDto brand, RmhGenderAgeDto age, RmhCategoryDto category, RmhProductTypeDto productType, RmhProductDivisionDto division, SizeScaleDto sizeScale, int presMin) {
        assertEquals(brand.getId(), vmStandard.getBrandId());
        assertEquals(age.getId(), vmStandard.getRmhGenderAgeId());
        assertEquals(category.getId(), vmStandard.getRmhCategoryId());
        assertEquals(productType.getId(), vmStandard.getRmhProductTypeId());
        assertEquals(division.getId(), vmStandard.getRmhProductDivisionId());
        assertEquals(sizeScale.getId(), vmStandard.getSizeScaleId());
        assertEquals(presMin, vmStandard.getPresMin());
    }

}