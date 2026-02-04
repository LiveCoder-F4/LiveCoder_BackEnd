package com.idea_l.livecoder.friend;

import com.idea_l.livecoder.common.RequestStatus;
import com.idea_l.livecoder.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FriendRequestRepository extends JpaRepository<FriendRequest, Long> {

    List<FriendRequest> findByReceiverAndStatus(User receiver, RequestStatus status);

    List<FriendRequest> findByRequester(User requester);

    Optional<FriendRequest> findByRequesterAndReceiver(User requester, User receiver);

    boolean existsByRequesterAndReceiverAndStatus(User requester, User receiver, RequestStatus status);
}