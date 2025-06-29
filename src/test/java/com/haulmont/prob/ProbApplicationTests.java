package com.haulmont.prob;

import com.haulmont.prob.repository.tezis.TezisTaskCount;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import java.util.UUID;

import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@SpringBootTest
//@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
class ProbApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private TezisTaskCount tezisTaskCount;

	@Test
	void contextLoads() {
	}

	@Test
	void calculateTaskProbability_WhenDatabaseError_ShouldReturnInternalServerError() throws Exception {
		UUID userId = UUID.randomUUID();
		UUID taskId = UUID.randomUUID();

		when(tezisTaskCount.getAssignedTaskCount(userId)).thenThrow(new DataAccessException("DB error") {});

		mockMvc.perform(get("/api/probs/calculate-probability")
						.param("user_id", userId.toString())
						.param("task_id", taskId.toString()))
				.andExpect(status().isInternalServerError())
				.andExpect(content().string(containsString("Database error")));
	}

}
