package com.idea_l.livecoder.collab;

import java.io.Serializable;
import java.util.Objects;

public class CollabMemberId implements Serializable {
    private Long collabId;
    private Long userId;

    public CollabMemberId() {}

    public CollabMemberId(Long collabId, Long userId) {
        this.collabId = collabId;
        this.userId = userId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CollabMemberId that)) return false;
        return Objects.equals(collabId, that.collabId) && Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(collabId, userId);
    }
}
