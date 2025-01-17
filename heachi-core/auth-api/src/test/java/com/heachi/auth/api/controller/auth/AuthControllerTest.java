package com.heachi.auth.api.controller.auth;

import com.heachi.auth.TestConfig;
import com.heachi.auth.api.service.oauth.OAuthService;
import com.heachi.auth.api.service.oauth.response.OAuthResponse;
import com.heachi.mysql.define.user.constant.UserPlatformType;
import com.heachi.mysql.define.user.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static com.heachi.mysql.define.user.constant.UserPlatformType.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest
class AuthControllerTest extends TestConfig {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OAuthService oAuthService;

    @Autowired
    private UserRepository userRepository;

    @AfterEach
    void tearDown() {
        userRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("카카오 로그인 성공 테스트")
    void loginSuccessTest() throws Exception {
        // given
        UserPlatformType platformType = KAKAO;
        String code = "abcdefg";

        OAuthResponse oAuthResponse = OAuthResponse.builder()
                .platformId("12345")
                .platformType(platformType)
                .email("kms@kakao.com")
                .name("김민수")
                .profileImageUrl("google.com")
                .build();
        when(oAuthService.login(any(UserPlatformType.class), any(String.class))).thenReturn(oAuthResponse); // 성공

        // when
        mockMvc.perform(
                get("/auth/KAKAO/login?code=" + code)
        )
                // then
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("카카오 로그인 실패 테스트")
    void loginFailTest() throws Exception {
        // given
        UserPlatformType platformType = KAKAO;
        String code = "abcdefg";

        when(oAuthService.login(any(UserPlatformType.class), any(String.class))).thenThrow(RuntimeException.class); // 실패

        // when
        mockMvc.perform(
                        get("/auth/KAKAO/login")
                )
                // then
                .andExpect(status().isBadRequest());
    }
}