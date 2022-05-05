package com.adidas.tsar.rest;

import com.adidas.tsar.BaseIntegrationTest;
import com.adidas.tsar.PlanogramApplication;
import com.adidas.tsar.TestUtils;
import com.adidas.tsar.data.FtwPriorityRepository;
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
class FtwPriorityControllerTest extends BaseIntegrationTest {

    public static final String EXPORT_URL = "/ftwpriorities/export";

    @Autowired
    private MockMvc mvc;

    @MockBean
    private FtwPriorityRepository ftwPriorityRepository;

    @Test
    void exportFtwPriorities() throws Exception {
        var priorities = List.of(
            TestUtils.prepareFtwPriority(1L, BRAND, AGE, SIZE_INDEX_1, 1),
            TestUtils.prepareFtwPriority(2L, BRAND_2, AGE, SIZE_INDEX_2, 2)
        );

        when(ftwPriorityRepository.findAll()).thenReturn(priorities);

        mvc.perform(get(EXPORT_URL))
            .andExpect(status().isOk());
    }

}