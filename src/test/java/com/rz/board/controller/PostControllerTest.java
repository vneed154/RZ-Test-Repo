package com.rz.board.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rz.board.dto.request.PostCreateRequest;
import com.rz.board.dto.response.PostResponse;
import com.rz.board.exception.ErrorCode;
import com.rz.board.service.PostService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 웹 계층 슬라이스 테스트: 서비스는 Mock으로 대체하고 요청/응답 형식(검증, 상태코드,
 * JSON 바디)만 검증한다.
 */
@WebMvcTest(PostController.class)
class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PostService postService;

    @Test
    @DisplayName("제목이 빈 문자열이면 400과 검증 에러 코드를 반환한다")
    void create_blankTitle_returnsBadRequest() throws Exception {
        PostCreateRequest request = new PostCreateRequest(1L, "", "내용");

        mockMvc.perform(post("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(ErrorCode.INVALID_INPUT_VALUE.getCode()));
    }

    @Test
    @DisplayName("정상 요청이면 201과 생성된 게시글을 반환한다")
    void create_success() throws Exception {
        PostCreateRequest request = new PostCreateRequest(1L, "제목", "내용");
        PostResponse response = new PostResponse(1L, "제목", "테스터", 0L, LocalDateTime.now());

        given(postService.create(any(PostCreateRequest.class))).willReturn(response);

        mockMvc.perform(post("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("제목"))
                .andExpect(jsonPath("$.writerNickname").value("테스터"));
    }
}
