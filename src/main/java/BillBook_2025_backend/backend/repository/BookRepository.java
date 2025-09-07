package BillBook_2025_backend.backend.repository;

import BillBook_2025_backend.backend.entity.Book;
import BillBook_2025_backend.backend.entity.BookStatus;
import BillBook_2025_backend.backend.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long> {



    List<Book> findBySellerId(Long sellerId); //빌린 사람 아이디로 게시물 찾기
    List<Book> findByBuyerId(Long buyerId);  //빌린 사람 아이디로 게시물 찾기
    List<Book> findBySellerAndBuyer(Member seller, Member buyer);
    List<Book> findByTitle(String title);
    List<Book> findByAuthor(String author);
    List<Book> findAll();

    @Modifying
    @Query("update Book b set b.status = :status where b.id = :id")
    void updateStatus(@Param("id") Long id, @Param("status") BookStatus status);

    @Modifying
    @Query("update Book b set b.buyer = :buyer, b.status = :status where b.id = :id") //빌리는 유저 아이디 setting
    void updateBuyerId(@Param("id") Long id, @Param("buyer") Member buyer, @Param("status") BookStatus status);


}
