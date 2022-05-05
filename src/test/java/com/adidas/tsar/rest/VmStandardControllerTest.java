package com.adidas.tsar.rest;

import com.adidas.tsar.BaseIntegrationTest;
import com.adidas.tsar.PlanogramApplication;
import com.adidas.tsar.TestUtils;
import com.adidas.tsar.data.VmStandardRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(
    classes = PlanogramApplication.class,
    webEnvironment = SpringBootTest.WebEnvironment.MOCK,
    properties = {"spring.liquibase.enabled=false"}
)
@AutoConfigureMockMvc
class VmStandardControllerTest extends BaseIntegrationTest {

    public static final String EXPORT_URL = "/vmstandards/export";

    @Autowired
    private MockMvc mvc;

    @MockBean
    private VmStandardRepository vmStandardRepository;

    @Test
    void exportVmStandards() throws Exception {
        var standards = List.of(
            TestUtils.prepareStandard(1, BRAND, AGE, CATEGORY, PRODUCT_TYPE, DIVISION, SIZE_SCALE, 1),
            TestUtils.prepareStandard(2, BRAND_2, AGE, CATEGORY, PRODUCT_TYPE, DIVISION, SIZE_SCALE, 1)
        );

        when(vmStandardRepository.findAll()).thenReturn(standards);

        mvc.perform(get(EXPORT_URL))
            .andExpect(status().isOk());

    }
}