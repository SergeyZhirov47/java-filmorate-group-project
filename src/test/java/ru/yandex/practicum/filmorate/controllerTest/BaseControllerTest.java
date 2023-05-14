package ru.yandex.practicum.filmorate.controllerTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

public abstract class BaseControllerTest {
    protected static final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();
    protected String endPoint;
    @Autowired
    protected MockMvc mockMvc;

    public BaseControllerTest() {
        this.endPoint = "/";
    }

    protected static String asJsonString(final Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected MvcResult addEntity(final Object entity) throws Exception {
        final String json = asJsonString(entity);

        final MvcResult result = this.mockMvc.perform(MockMvcRequestBuilders.post(endPoint)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andReturn();

        return result;
    }
}
