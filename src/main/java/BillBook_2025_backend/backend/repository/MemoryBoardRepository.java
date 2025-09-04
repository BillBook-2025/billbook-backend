package BillBook_2025_backend.backend.repository;

import BillBook_2025_backend.backend.entity.Board;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class MemoryBoardRepository {
    private final Map<Long, Board> store = new ConcurrentHashMap<>();
    private long sequence = 0L;

    public Board save(Board board) {
        if (board.getBoardId() == null) {
            board.setBoardId(++sequence);
            board.setCreatedAt(LocalDateTime.now());
        }
        store.put(board.getBoardId(), board);
        return board;
    }

    public List<Board> findAll() {
        return new ArrayList<>(store.values());
    }

    public Optional<Board> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    public void delete(Long id) {
        store.remove(id);
    }
}