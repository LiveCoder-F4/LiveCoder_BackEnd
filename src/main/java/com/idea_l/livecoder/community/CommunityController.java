package com.idea_l.livecoder.community;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts") // 필요하면 "/api/community/posts"로 바꿔도 됨
public class CommunityController {

    private final CommunityService communityService;

    @PostMapping
    public ResponseEntity<Long> create(@RequestBody @Valid CommunityCreateRequest req) {
        return ResponseEntity.ok(communityService.create(req));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommunityResponse> get(@PathVariable Long id) {
        return ResponseEntity.ok(communityService.get(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        communityService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<CommunityResponse> update(
            @PathVariable Long id,
            @RequestBody @Valid CommunityUpdateRequest req
    ) {
        return ResponseEntity.ok(communityService.update(id, req));

    }
}
