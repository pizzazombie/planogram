package com.adidas.tsar.rest;

import com.adidas.tsar.BaseIntegrationTest;
import com.adidas.tsar.PlanogramApplication;
import com.adidas.tsar.data.PlanogramRepository;
import com.adidas.tsar.domain.Planogram;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
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
class PlanogramControllerTest extends BaseIntegrationTest {

    public static final String SEARCH_PLANOGRAM_URL = "/planogram";

    @Autowired
    private MockMvc mvc;

    @MockBean
    private PlanogramRepository planogramRepository;

    @Test
    void searchPlanogramResults_ok() throws Exception {
        when(planogramRepository.findAll((Specification<Planogram>) any(), any(Pageable.class))).thenReturn(Page.empty());

        mvc.perform(get(SEARCH_PLANOGRAM_URL))
            .andExpect(status().isOk());
    }

}