package rafialif.spring_restful_api.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import rafialif.spring_restful_api.entity.User;
import rafialif.spring_restful_api.model.RegisterUserReq;
import rafialif.spring_restful_api.model.UpdateUserReq;
import rafialif.spring_restful_api.model.UserResponse;
import rafialif.spring_restful_api.model.WebResponse;
import rafialif.spring_restful_api.repository.UserRepository;
import rafialif.spring_restful_api.security.BCrypt;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private UserRepository userRepository;

        @Autowired
        private ObjectMapper objectMapper;

        @BeforeEach
        void setUp() {
                userRepository.deleteAll();
        }

        @Test
        void testRegisterSuccess() throws Exception {
                RegisterUserReq request = new RegisterUserReq();
                request.setUsername("test");
                request.setPassword("rahasia");
                request.setName("Test");

                mockMvc.perform(
                                post("/api/users")
                                                .accept(MediaType.APPLICATION_JSON)
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(objectMapper.writeValueAsString(request)))
                                .andExpectAll(
                                                status().isOk())
                                .andDo(result -> {
                                        WebResponse<String> response = objectMapper.readValue(
                                                        result.getResponse().getContentAsString(),
                                                        new TypeReference<>() {
                                                        });

                                        assertEquals("OK", response.getData());
                                });
        }

        @Test
        void testRegisterBadRequest() throws Exception {
                RegisterUserReq request = new RegisterUserReq();
                request.setUsername("");
                request.setPassword("");
                request.setName("");

                mockMvc.perform(
                                post("/api/users")
                                                .accept(MediaType.APPLICATION_JSON)
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(objectMapper.writeValueAsString(request)))
                                .andExpectAll(
                                                status().isBadRequest())
                                .andDo(result -> {
                                        WebResponse<String> response = objectMapper.readValue(
                                                        result.getResponse().getContentAsString(),
                                                        new TypeReference<>() {
                                                        });

                                        assertNotNull(response.getErrors());
                                });
        }

        @Test
        void testRegisterDuplicate() throws Exception {
                User user = new User();
                user.setUsername("test");
                user.setPassword(BCrypt.hashpw("rahasia", BCrypt.gensalt()));
                user.setName("Test");
                userRepository.save(user);

                RegisterUserReq request = new RegisterUserReq();
                request.setUsername("test");
                request.setPassword("rahasia");
                request.setName("Test");

                mockMvc.perform(
                                post("/api/users")
                                                .accept(MediaType.APPLICATION_JSON)
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(objectMapper.writeValueAsString(request)))
                                .andExpectAll(
                                                status().isBadRequest())
                                .andDo(result -> {
                                        WebResponse<String> response = objectMapper.readValue(
                                                        result.getResponse().getContentAsString(),
                                                        new TypeReference<>() {
                                                        });

                                        assertNotNull(response.getErrors());
                                });
        }

        @Test
        void getUserUnauthorized() throws Exception {
                mockMvc.perform(
                                get("/api/users/current")
                                                .accept(MediaType.APPLICATION_JSON)
                                                .header("X-API-TOKEN", "notfound"))
                                .andExpectAll(
                                                status().isUnauthorized())
                                .andDo(result -> {
                                        WebResponse<String> response = objectMapper.readValue(
                                                        result.getResponse().getContentAsString(),
                                                        new TypeReference<>() {
                                                        });

                                        assertNotNull(response.getErrors());
                                });
        }

        @Test
        void getUserUnauthorizedTokenNotSend() throws Exception {
                mockMvc.perform(
                                get("/api/users/current")
                                                .accept(MediaType.APPLICATION_JSON))
                                .andExpectAll(
                                                status().isUnauthorized())
                                .andDo(result -> {
                                        WebResponse<String> response = objectMapper.readValue(
                                                        result.getResponse().getContentAsString(),
                                                        new TypeReference<>() {
                                                        });

                                        assertNotNull(response.getErrors());
                                });
        }

        @Test
        void getUserSuccess() throws Exception {
                User user = new User();
                user.setUsername("test");
                user.setPassword(BCrypt.hashpw("rahasia", BCrypt.gensalt()));
                user.setName("Test");
                user.setToken("test");
                user.setTokenExpiredAt(System.currentTimeMillis() + 10000000000L);
                userRepository.save(user);

                mockMvc.perform(
                                get("/api/users/current")
                                                .accept(MediaType.APPLICATION_JSON)
                                                .header("X-API-TOKEN", "test"))
                                .andExpectAll(
                                                status().isOk())
                                .andDo(result -> {
                                        WebResponse<UserResponse> response = objectMapper
                                                        .readValue(result.getResponse().getContentAsString(),
                                                                        new TypeReference<>() {
                                                                        });

                                        assertNull(response.getErrors());
                                        assertEquals("test", response.getData().getUsername());
                                        assertEquals("Test", response.getData().getName());
                                });
        }

        @Test
        void getUserTokenExpired() throws Exception {
                User user = new User();
                user.setUsername("test");
                user.setPassword(BCrypt.hashpw("rahasia", BCrypt.gensalt()));
                user.setName("Test");
                user.setToken("test");
                user.setTokenExpiredAt(System.currentTimeMillis() - 10000000);
                userRepository.save(user);

                mockMvc.perform(
                                get("/api/users/current")
                                                .accept(MediaType.APPLICATION_JSON)
                                                .header("X-API-TOKEN", "test"))
                                .andExpectAll(
                                                status().isUnauthorized())
                                .andDo(result -> {
                                        WebResponse<String> response = objectMapper.readValue(
                                                        result.getResponse().getContentAsString(),
                                                        new TypeReference<>() {
                                                        });

                                        assertNotNull(response.getErrors());
                                });
        }

        @Test
        void updateUserUnauthorized() throws Exception {
                UpdateUserReq request = new UpdateUserReq();

                mockMvc.perform(
                                patch("/api/users/current")
                                                .accept(MediaType.APPLICATION_JSON)
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(objectMapper.writeValueAsString(request)))
                                .andExpectAll(
                                                status().isUnauthorized())
                                .andDo(result -> {
                                        WebResponse<String> response = objectMapper.readValue(
                                                        result.getResponse().getContentAsString(),
                                                        new TypeReference<>() {
                                                        });

                                        assertNotNull(response.getErrors());
                                });
        }

        @Test
        void updateUserSuccess() throws Exception {
                User user = new User();
                user.setUsername("test");
                user.setPassword(BCrypt.hashpw("rahasia", BCrypt.gensalt()));
                user.setName("Test");
                user.setToken("test");
                user.setTokenExpiredAt(System.currentTimeMillis() + 100000000000L);
                userRepository.save(user);

                UpdateUserReq request = new UpdateUserReq();
                request.setName("Rafi");
                request.setPassword("rafi12345");

                mockMvc.perform(
                                patch("/api/users/current")
                                                .accept(MediaType.APPLICATION_JSON)
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(objectMapper.writeValueAsString(request))
                                                .header("X-API-TOKEN", "test"))
                                .andExpectAll(
                                                status().isOk())
                                .andDo(result -> {
                                        WebResponse<UserResponse> response = objectMapper
                                                        .readValue(result.getResponse().getContentAsString(),
                                                                        new TypeReference<>() {
                                                                        });

                                        assertNull(response.getErrors());
                                        assertEquals("rafi", response.getData().getName());
                                        assertEquals("test", response.getData().getUsername());

                                        User userDb = userRepository.findById("test").orElse(null);
                                        assertNotNull(userDb);
                                        assertTrue(BCrypt.checkpw("rafii12345", userDb.getPassword()));
                                });
        }
}
