package com.idea_l.livecoder.friend;

import com.idea_l.livecoder.common.RequestStatus;
import com.idea_l.livecoder.user.User;
import com.idea_l.livecoder.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class FriendService {

    private final FriendRequestRepository friendRequestRepository;
    private final FriendshipRepository friendshipRepository;
    private final FriendMessageRepository friendMessageRepository;
    private final UserRepository userRepository;

    @Autowired
    public FriendService(FriendRequestRepository friendRequestRepository,
                         FriendshipRepository friendshipRepository,
                         FriendMessageRepository friendMessageRepository,
                         UserRepository userRepository) {
        this.friendRequestRepository = friendRequestRepository;
        this.friendshipRepository = friendshipRepository;
        this.friendMessageRepository = friendMessageRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<FriendResponse> getFriends(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        List<Friendship> friendships = friendshipRepository.findAllByUser(user);
        List<FriendResponse> friends = new ArrayList<>();

        for (Friendship friendship : friendships) {
            User friend = friendship.getUser1().equals(user) ? friendship.getUser2() : friendship.getUser1();
            friends.add(FriendResponse.of(friend, friendship.getCreatedAt()));
        }

        return friends;
    }

    @Transactional
    public FriendRequestResponse sendFriendRequest(Long requesterId, Long receiverId) {
        if (requesterId.equals(receiverId)) {
            throw new IllegalArgumentException("자기 자신에게 친구 요청을 보낼 수 없습니다.");
        }

        User requester = userRepository.findById(requesterId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상대방입니다."));

        if (friendshipRepository.existsByUsers(requester, receiver)) {
            throw new IllegalArgumentException("이미 친구입니다.");
        }

        if (friendRequestRepository.existsByRequesterAndReceiverAndStatus(requester, receiver, RequestStatus.PENDING)) {
            throw new IllegalArgumentException("이미 친구 요청을 보냈습니다.");
        }

        if (friendRequestRepository.existsByRequesterAndReceiverAndStatus(receiver, requester, RequestStatus.PENDING)) {
            throw new IllegalArgumentException("상대방이 이미 친구 요청을 보냈습니다. 받은 요청을 확인해주세요.");
        }

        FriendRequest request = new FriendRequest();
        request.setRequester(requester);
        request.setReceiver(receiver);
        request.setStatus(RequestStatus.PENDING);

        return FriendRequestResponse.from(friendRequestRepository.save(request));
    }

    @Transactional(readOnly = true)
    public List<FriendRequestResponse> getReceivedRequests(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        return friendRequestRepository.findByReceiverAndStatus(user, RequestStatus.PENDING)
                .stream()
                .map(FriendRequestResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<FriendRequestResponse> getSentRequests(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        return friendRequestRepository.findByRequester(user)
                .stream()
                .map(FriendRequestResponse::from)
                .toList();
    }

    @Transactional
    public void acceptFriendRequest(Long userId, Long requestId) {
        FriendRequest request = friendRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 친구 요청입니다."));

        if (!request.getReceiver().getUserId().equals(userId)) {
            throw new IllegalArgumentException("본인에게 온 요청만 수락할 수 있습니다.");
        }

        if (request.getStatus() != RequestStatus.PENDING) {
            throw new IllegalArgumentException("이미 처리된 요청입니다.");
        }

        request.setStatus(RequestStatus.ACCEPTED);

        User requester = request.getRequester();
        User receiver = request.getReceiver();

        Friendship friendship = new Friendship();
        if (requester.getUserId() < receiver.getUserId()) {
            friendship.setUser1(requester);
            friendship.setUser2(receiver);
        } else {
            friendship.setUser1(receiver);
            friendship.setUser2(requester);
        }
        friendshipRepository.save(friendship);
    }

    @Transactional
    public void rejectFriendRequest(Long userId, Long requestId) {
        FriendRequest request = friendRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 친구 요청입니다."));

        if (!request.getReceiver().getUserId().equals(userId)) {
            throw new IllegalArgumentException("본인에게 온 요청만 거절할 수 있습니다.");
        }

        if (request.getStatus() != RequestStatus.PENDING) {
            throw new IllegalArgumentException("이미 처리된 요청입니다.");
        }

        request.setStatus(RequestStatus.DECLINED);
    }

    @Transactional
    public void cancelFriendRequest(Long userId, Long requestId) {
        FriendRequest request = friendRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 친구 요청입니다."));

        if (!request.getRequester().getUserId().equals(userId)) {
            throw new IllegalArgumentException("본인이 보낸 요청만 취소할 수 있습니다.");
        }

        if (request.getStatus() != RequestStatus.PENDING) {
            throw new IllegalArgumentException("이미 처리된 요청입니다.");
        }

        friendRequestRepository.delete(request);
    }

    @Transactional
    public void deleteFriend(Long userId, Long friendId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
        User friend = userRepository.findById(friendId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상대방입니다."));

        Friendship friendship = friendshipRepository.findByUsers(user, friend)
                .orElseThrow(() -> new IllegalArgumentException("친구 관계가 아닙니다."));

        friendshipRepository.delete(friendship);
    }

    @Transactional
    public FriendMessageResponse sendMessage(Long senderId, Long receiverId, String content) {
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상대방입니다."));

        if (!friendshipRepository.existsByUsers(sender, receiver)) {
            throw new IllegalArgumentException("친구에게만 쪽지를 보낼 수 있습니다.");
        }

        FriendMessage message = new FriendMessage();
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setContent(content);

        return FriendMessageResponse.from(friendMessageRepository.save(message));
    }
}
