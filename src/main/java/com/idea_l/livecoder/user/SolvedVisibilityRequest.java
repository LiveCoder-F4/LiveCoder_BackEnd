package com.idea_l.livecoder.user;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SolvedVisibilityRequest {

    @NotNull(message = "공개 여부는 필수입니다")
    private Boolean isSolvedPublic;
}