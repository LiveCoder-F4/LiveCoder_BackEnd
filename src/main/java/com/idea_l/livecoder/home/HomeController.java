package com.idea_l.livecoder.home;

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
}

