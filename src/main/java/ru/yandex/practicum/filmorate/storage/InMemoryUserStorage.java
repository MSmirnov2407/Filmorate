package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

@Component
@Qualifier("inMemoryUser")
public class InMemoryUserStorage extends AbstractElementStorage<User> implements UserStorage {
    @Override
    public void addFriend(User user, User friend) {
        user.getFriends().add(friend.getId());
    }

    @Override
    public void deleteFriend(User user, User friend) {
        user.getFriends().remove(friend.getId());
    }

    @Override
    public Set<User> getFriendsByUserId(int userId) {
        Set<User> friends = new TreeSet<>((u1, u2) -> {
            int cmp = 0;
            if (u1.getId() > u2.getId()) {
                cmp = -1;
            } else if (u1.getId() < u2.getId()) {
                cmp = 1;
            }
            return -1 * cmp;
        });

        User user = storagedData.get(userId); //взяли пользователя по id
        Set<Integer> friendsId = user.getFriends(); //взяли Id его друзей
        friendsId.stream().forEach(i -> friends.add(storagedData.get(i))); //сложили друзей в список другей пользователя

        return friends;
    }

    @Override
    public List<User> getCommonFriends(User user1, User user2) {
        int user1Id = user1.getId();
        int user2Id = user2.getId();

        List<User> user1friends = new ArrayList<>(getFriendsByUserId(user1Id));
        List<User> user2friends = new ArrayList<>(getFriendsByUserId(user2Id));

        return user1friends.stream()
                .filter(user2friends::contains)
                .collect(Collectors.toList());
    }
}
