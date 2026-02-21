package com.idea_l.livecoder.home;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/home")
public class HomeController {

    private final HomeService homeService;

    public HomeController(HomeService homeService) {
        this.homeService = homeService;
    }

    // 예: GET /home(always returns top 5)
    @GetMapping
    public ResponseEntity<HomeApiResponse<HomeResponse>> getHome(
            @RequestParam(defaultValue = "1") Long userId
    ) {
        return ResponseEntity.ok(HomeApiResponse.ok(homeService.getHome(userId)));
    }

    @GetMapping("/news")
    public ResponseEntity<HomeApiResponse<Page<HomeNewsItem>>> getAllNews(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "newsId"));
        return ResponseEntity.ok(HomeApiResponse.ok(homeService.getAllNews(pageable)));
    }
}

