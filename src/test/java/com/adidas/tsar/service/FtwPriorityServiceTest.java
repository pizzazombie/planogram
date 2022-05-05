package com.adidas.tsar.service;

import com.adidas.tsar.BaseIntegrationTest;
import com.adidas.tsar.PlanogramApplication;
import com.adidas.tsar.TestUtils;
import com.adidas.tsar.data.FtwPriorityRepository;
import com.adidas.tsar.domain.FtwPriority;
import com.adidas.tsar.dto.BrandDto;
import com.adidas.tsar.dto.RmhGenderAgeDto;
import com.adidas.tsar.dto.ftwpriority.FtwPriorityCreateDto;
import com.adidas.tsar.dto.ftwpriority.FtwPriorityKey;
import com.adidas.tsar.dto.ftwpriority.FtwPriorityResponse;
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
import static org.mockito.Mockito.*;


@Slf4j
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
@SpringBootTest(
    classes = PlanogramApplication.class,
    webEnvironment = SpringBootTest.WebEnvironment.MOCK,
    properties = {"spring.liquibase.enabled=false"}
)
class FtwPriorityServiceTest extends BaseIntegrationTest {

    private static final String XLSX_IMPORT_FILE_PATH = "classpath:import/ftw-priority-right.xlsx";

    @Autowired
    private FtwPriorityService ftwPriorityService;

    @MockBean
    private FtwPriorityRepository ftwPriorityRepository;

    @Captor
    private ArgumentCaptor<List<FtwPriority>> ftwPriorityListCaptor;

    @Captor
    private ArgumentCaptor<FtwPriority> ftwPriorityCaptor;

    @Captor
    private ArgumentCaptor<Collection<FtwPriorityKey>> ftwPriorityKeyCollectionCaptor;

    @Test
    void findFtwPriorities_dataExists_ok() {
        var ftwPriorities = List.of(
            TestUtils.prepareFtwPriority(1L, BRAND, AGE, SIZE_INDEX_1, 1),
            TestUtils.prepareFtwPriority(2L, BRAND_2, AGE, SIZE_INDEX_2, 2)
        );
        when(ftwPriorityRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(ftwPriorities));

        final var foundPage = ftwPriorityService.findFtwPriorities(Pageable.ofSize(PAGE_SIZE));
        final var ids = foundPage.getContent().stream().map(FtwPriorityResponse::getId).collect(Collectors.toList());
        assertEquals(List.of(1L, 2L), ids);
    }

    @Test
    void deleteFtwPriorities_dataExists_deleted() {
        var deleteIds = List.of(1L, 2L);

        ftwPriorityService.deleteFtwPriorities(deleteIds);

        verify(ftwPriorityRepository, atLeastOnce()).deleteAllById(longListCaptor.capture());
        assertEquals(deleteIds, longListCaptor.getValue());
    }

    @Test
    void createFtwPriorities_dataNotExists_created() {
        var createPrioritiesDtoList = List.of(
            new FtwPriorityCreateDto(BRAND.getName(), AGE.getName(), SIZE_INDEX_1, 1),
            new FtwPriorityCreateDto(BRAND_2.getName(), AGE.getName(), SIZE_INDEX_2, 2)
        );
        when(ftwPriorityRepository.findAllByKeys(any())).thenReturn(Lists.emptyList());

        ftwPriorityService.createFtwPriorities(createPrioritiesDtoList, USER);

        verify(ftwPriorityRepository, atLeastOnce()).saveAll(ftwPriorityListCaptor.capture());
        final var createdPriorities = ftwPriorityListCaptor.getValue();
        createdPriorities.sort(Comparator.comparingInt(FtwPriority::getPriority));
        assertEquals(2, createdPriorities.size());
        verifyPriority(createdPriorities.get(0), BRAND, AGE, SIZE_INDEX_1, 1);
        verifyPriority(createdPriorities.get(1), BRAND_2, AGE, SIZE_INDEX_2, 2);
    }

    @Test
    void createFtwPriorities_dataExists_exceptionThrown() {
        var createPrioritiesDtoList = List.of(
            new FtwPriorityCreateDto(BRAND.getName(), AGE.getName(), SIZE_INDEX_1, 1)
        );
        var existEntity = TestUtils.prepareFtwPriority(1L, BRAND, AGE, SIZE_INDEX_1, 1);
        when(ftwPriorityRepository.findAllByKeys(ftwPriorityKeyCollectionCaptor.capture())).thenReturn(Collections.singletonList(existEntity));

        assertThrows(AppException.class, () -> ftwPriorityService.createFtwPriorities(createPrioritiesDtoList, TestUtils.USER));

        final var ftwPriorityKey = ftwPriorityKeyCollectionCaptor.getValue().iterator().next();
        assertEquals(BRAND.getId(), ftwPriorityKey.getBrandId());
        assertEquals(AGE.getId(), ftwPriorityKey.getRmhGenderAgeId());
        assertEquals(SIZE_INDEX_1, ftwPriorityKey.getSizeIndex());
        verify(ftwPriorityRepository, never()).saveAll(any());
    }

    @Test
    void createFtwPriorities_duplicationInRequestBody_exceptionThrown() {
        var createPrioritiesDtoList = List.of(
            new FtwPriorityCreateDto(BRAND.getName(), AGE.getName(), SIZE_INDEX_1, 1),
            new FtwPriorityCreateDto(BRAND.getName(), AGE.getName(), SIZE_INDEX_1, 2)
        );

        assertThrows(AppException.class, () -> ftwPriorityService.createFtwPriorities(createPrioritiesDtoList, USER));
    }

    @Test
    void updatePriority_dataExists_changed() {
        long existsId = 1L;
        var existEntity = TestUtils.prepareFtwPriority(existsId, BRAND, AGE, SIZE_INDEX_1, 1);
        when(ftwPriorityRepository.findById(eq(existsId))).thenReturn(Optional.of(existEntity));

        ftwPriorityService.updatePriority(existsId, 2, USER);

        verify(ftwPriorityRepository, atLeastOnce()).save(ftwPriorityCaptor.capture());
        final var changedPriority = ftwPriorityCaptor.getValue();
        verifyPriority(changedPriority, BRAND, AGE, SIZE_INDEX_1, 2);
    }

    @Test
    void importFromExcel_validValues_imported() throws Exception {
        var uploadFile = new MockMultipartFile(
            "file",
            "ftw-priority-right.xlsx",
            MediaType.MULTIPART_FORM_DATA_VALUE,
            FileUtils.readFileToByteArray(ResourceUtils.getFile(XLSX_IMPORT_FILE_PATH))
        );

        ftwPriorityService.importFromExcel(uploadFile, USER);

        verify(ftwPriorityRepository, atLeastOnce()).saveAll(ftwPriorityListCaptor.capture());
        final var createdPriorities = ftwPriorityListCaptor.getValue();
        createdPriorities.sort(Comparator.comparingInt(FtwPriority::getPriority));
        assertEquals(6, createdPriorities.size());
        verifyPriority(createdPriorities.get(0), BRAND, AGE, "210", 12);
        verifyPriority(createdPriorities.get(1), BRAND, AGE, "390", 13);
        verifyPriority(createdPriorities.get(2), BRAND, AGE_2, "680", 14);
        verifyPriority(createdPriorities.get(3), BRAND_2, AGE_2, "210", 15);
        verifyPriority(createdPriorities.get(4), BRAND, AGE, "790", 16);
        verifyPriority(createdPriorities.get(5), BRAND_2, AGE_2, "170", 17);
    }

    private void verifyPriority(FtwPriority ftwPriority, BrandDto brand, RmhGenderAgeDto age, String sizeIndex, int priority) {
        assertEquals(brand.getId(), ftwPriority.getBrandId());
        assertEquals(age.getId(), ftwPriority.getRmhGenderAgeId());
        assertEquals(sizeIndex, ftwPriority.getSizeIndex());
        assertEquals(priority, ftwPriority.getPriority());
        assertEquals(USER, ftwPriority.getModifiedBy());
    }
}