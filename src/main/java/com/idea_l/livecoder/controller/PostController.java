package com.idea_l.livecoder.controller;

import com.idea_l.livecoder.dto.PostCreateRequest;
import com.idea_l.livecoder.dto.PostResponse;
import com.idea_l.livecoder.dto.PostUpdateRequest;
import com.idea_l.livecoder.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "게시글 관리", description = "게시글 CRUD API")
@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;

    @Autowired
    public PostController(PostService postService) {
        this.postService = postService;
    }

    @PostMapping
    @Operation(summary = "게시글 작성", description = "새로운 게시글을 작성합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "게시글 작성 성공", 
                    content = @Content(schema = @Schema(implementation = Long.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 사용자")
    })
    public ResponseEntity<Long> createPost(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "게시글 작성 요청 정보", 
                    required = true,
                    content = @Content(schema = @Schema(implementation = PostCreateRequest.class))
            )
            @RequestBody @Valid PostCreateRequest request
    ) {
        return ResponseEntity.ok(postService.createPost(request));
    }

    @GetMapping("/{id}")
    @Operation(summary = "게시글 조회", description = "특정 게시글의 상세 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "게시글 조회 성공",
                    content = @Content(schema = @Schema(implementation = PostResponse.class))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 게시글")
    })
    public ResponseEntity<PostResponse> getPost(
            @Parameter(description = "조회할 게시글의 ID", required = true, example = "1") 
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(postService.getPost(id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "게시글 삭제", description = "특정 게시글을 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "게시글 삭제 성공"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 게시글")
    })
    public ResponseEntity<Void> deletePost(
            @Parameter(description = "삭제할 게시글의 ID", required = true, example = "1") 
            @PathVariable Long id
    ) {
        postService.deletePost(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    @Operation(summary = "게시글 수정", description = "특정 게시글의 제목과 내용을 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "게시글 수정 성공",
                    content = @Content(schema = @Schema(implementation = PostResponse.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 게시글")
    })
    public ResponseEntity<PostResponse> updatePost(
            @Parameter(description = "수정할 게시글의 ID", required = true, example = "1") 
            @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "게시글 수정 요청 정보", 
                    required = true,
                    content = @Content(schema = @Schema(implementation = PostUpdateRequest.class))
            )
            @RequestBody @Valid PostUpdateRequest request
    ) {
        return ResponseEntity.ok(postService.updatePost(id, request));
    }
}