package com.idea_l.livecoder.collab;

import java.io.Serializable;
import java.util.Objects;

public class CollabMemberId implements Serializable {
    private Long collabTeam;
    private Long user;

    public CollabMemberId() {}

    public CollabMemberId(Long collabTeam, Long user) {
        this.collabTeam = collabTeam;
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CollabMemberId that = (CollabMemberId) o;
        return Objects.equals(collabTeam, that.collabTeam) && Objects.equals(user, that.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(collabTeam, user);
    }

}
