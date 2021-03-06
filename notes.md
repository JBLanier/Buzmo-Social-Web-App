# General


# Homepage (Sign Up and Login)

    POST /auth/signup
    Body: UserCreationRequest
    UserDAO.createUser(User)
    
    POST /auth/
    Body: LoginRequest
    UserDAO.getPasswd(String email)
    
    Get /user?userid=123
    UserDAO.getUser(long userid)
    Get /user?email=dude@gmail.com
    UserDAO.getUser(String email)
    
    Get /user/profile
    return user based on cookie

# Friends

    GET /friends/requests
    FriendsDAO.getRequests(long userid)
    
    POST /friends/request/respond
    ?mid=123&accept=true      (true or false)
    FriendsDAO.acceptRequest(long mid)
    FriendsDAO.declineRequest(long mid)
    
    POST /friends/request/create
    FriendRequest Json object:
    {"mid":41,"recipient":1,"msg":"Let's be friends!","msg_timestamp":1479679336488,"sender":15,"sender_name":"KDTrey5"}
    
    GET /chatgroups/invites
    ChatGroupsDAO.getInvites(long userid)
    
    POST /chatgroups/invite/respond
    ChatGroupsDAO.acceptInvite(long mid)
    ChatGroupsDAO.declineInvite(long mid)
    
    GET /friends/list
    Get list of all my friends.
    FriendsDAO.getFriends(long userid)
    
    POST /users/search
    searches all users in database
    {"email": null, "screenname": "Josh", "topics": ["dogs","cats"], "mostRecentPostingInLastNDays": null, "minMessagesSentInLast7Days": 3}
    UserDAO.search(String email, String screenname, List<String> topics, Integer mostRecentPostingInLastNDays, 
        Integer minMessagesSentInLast7Days) 

# MyCircle 

    GET /mycircle/list?userid=1&offset=0
    returns mycirle messages directed to user and broadcast by friends
    MyCircleDAO.getUserMessages(long userid, int offset, int count)
    
    POST /mycircle/search
    {"offset": 0, "topics": ["dogs", "bikes"]}
    MyCircleDAO.getSearchMessages(List<String> topics, int offset, int count)
    
    both above resources will use this
    MyCircleDAO.getMessageTopics(List<long> mids)
    
    POST /mycircle/create
    {"msg": "Hello I'm at UCSB", "topics": ["dogs", "bikes"], "public":true, "recipients": []}
    MyCircleDAO.createMessage(String msg, List<String> topics, Boolean public, List<long> userids)
    
    POST /mycircle/delete?mid=343

# Messages

## Private Messages
    
    GET /messages/list
    returns users friends sorted by latest massage to or from them (not counting those deleted by home user)
    PrivateMessageDAO.getConversations(long userid, int offset, int count);
    Hard coded limit of 100 conversations will be returned.
    
    GET /messages/conversation?offset=7&user=123
    returns messages to and from that friend sorted by timestamp (not counting those deleted by home user)
    PrivateMessageDAO.getConversation(long userid, long friend_userid, int offset, int count);
    Limit is 7 per request.
    
    POST /messages/delete?mid=3421
    PrivateMessageDAO.markForDeletion(long userid, long mid)
    
    POST /messages/send
    Body: MessageInABottle
    {"msg": "Hi Brian this is a PM", "recipient":recipient_id}
    PrivateMessageDAO.sendMessage(long sender_userid, long recipient_userid, String msg)
    
## ChatGroups
    
    GET /chatgroups/list
    returns users chatgroups sorted by latest massage to or from them (not counting those deleted by home user)
    ChatGroupsDAO.getConversations(long userid, int offset, count)
    
    GET /chatgroups/conversation?cgid=3434&offset=0
    returns messages to and from that chatgroup sorted by timestamp (not counting those deleted by home user)
    ChatGroupsDAO.getConversation(long userid, long cgid, int offset, int count)
    
    POST /chatgroups/conversation/delete?mid=123
    ChatGroupsDAO.markForDeletion(long userid, long cgid, long mid)
    
    POST /chatgroups/conversation/send
    Body: MessageInABottle
    {"msg": "Hello group! This is a message", "recipient": recipient_id}
    ChatGroupsDAO.createNewMessage(long sender_userid, long cgid, String msg)
    
    GET /chatgroups?cgid=434343
    ChatGroupsDAO.getChatGroup(long cgid)
    
    POST /chatgroups/invite/create?cgid=123
    Body: MessageInABottle
    ChatGroupsDAO.createInvite(long cgid, long recipient_userid)

    POST /chatgroups/invite/respond?cgid=213&accept=true

    POST /chatgroups/create
    ChatGroupsDAO.createChatGroup(long owner_userid, String name, int message_duration)
    
    POST /chatgroups/delete
    Before calling the DAO method, we must verify that the userid posting this is the owner of the group.
    ChatGroupsDAO.deleteChatGroup(long cgid);
    
    POST /chatgroups/update
    Before calling the DAO method, we must verify that the userid posting this is the owner of the group.
    ChatGroupsDAO.updateName(long cgid, String new_name, int new_duration)


