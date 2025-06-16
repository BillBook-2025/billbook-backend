package BillBook_2025_backend.backend.repository;

import BillBook_2025_backend.backend.entity.LikeBook;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class MemoryLikeBookRepository implements LikeBookRepository {
    private final Map<Long, LikeBook> store = new HashMap<>();
    private Long nextId = 1L;

    public LikeBook save(LikeBook post) {
        store.put(nextId++, post);
        return post;
    }

    public Optional<LikeBook> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    public List<LikeBook> findByBookId(Long bookId) {
        List<LikeBook> likes = store.values().stream()
                .filter(likeBook -> likeBook.getBookId().equals(bookId))
                .collect(Collectors.toList());
        return likes;
    }

    public Optional<LikeBook> findByBookIdAndUserId(Long bookId, String userId) {
        List<LikeBook> likes = findByBookId(bookId);
        return likes.stream().filter(likeBook -> likeBook.getUserId().equals(userId)).findFirst();
    }

    public Long countByBookId(Long bookId) {
        List<LikeBook> likes = store.values().stream()
                .filter(likeBook -> likeBook.getBookId().equals(bookId))
                .toList();
        return (long) likes.size();
    }

    public void delete(LikeBook removeLike) {
        List<LikeBook> likes = findByBookId(removeLike.getBookId());
        Optional<LikeBook> deleteLike = likes.stream().filter(likeBook -> likeBook.getUserId().equals(removeLike.getUserId())).findFirst();
        store.remove(deleteLike.get().getBookId());
    }
}
