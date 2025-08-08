package BillBook_2025_backend.backend.service;

import BillBook_2025_backend.backend.dto.BookListResponse;
import BillBook_2025_backend.backend.dto.BookResponse;
import BillBook_2025_backend.backend.dto.DeleteMemberDto;
import BillBook_2025_backend.backend.dto.UserInfoDto;
import BillBook_2025_backend.backend.entity.Book;
import BillBook_2025_backend.backend.entity.LikeBook;
import BillBook_2025_backend.backend.entity.Member;
import BillBook_2025_backend.backend.repository.BookRepository;
import BillBook_2025_backend.backend.repository.LikeBookRepository;
import BillBook_2025_backend.backend.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {
    private final MemberRepository memberRepository;
    private final LikeBookRepository likeBookRepository;
    private final BookRepository bookRepository;

    @Autowired
    public UserService(MemberRepository memberRepository, LikeBookRepository likeBookRepository, BookRepository bookRepository) {
        this.memberRepository = memberRepository;
        this.likeBookRepository = likeBookRepository;
        this.bookRepository = bookRepository;
    }

    public Member signup(Member member) {
        if (memberRepository.findByUserId(member.getUserId()).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 사용자입니다.");
        }

        Member newMember = new Member(member.getUserId(), member.getPassword(), member.getEmail(), member.getUserName());
        return memberRepository.save(newMember);
    }

    public void delete(DeleteMemberDto request, Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new EntityNotFoundException("해당 유저가 존재하지 않습니다."));

        if (request.getPassword().equals(member.getPassword()) && request.getText().equals("탈퇴하겠습니다.")) {
            memberRepository.delete(member);
        } else {
            throw new RuntimeException("회원탈퇴를 실패하였습니다.");
        }
    }

    public Member login(Member member) {
        if(!memberRepository.findById(member.getId()).isPresent()) {
            throw new IllegalArgumentException("해당 아이디를 찾을 수 없습니다.");
        } else {
            Member finduser = memberRepository.findByUserId(member.getUserId()).get();
            if (!finduser.getPassword().equals(member.getPassword())) {
                throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
            }

            return finduser;
        }
    }

    @Transactional
    public void changePassword(Long userId, String password, String confirmPassword) {
        if (!password.equals(confirmPassword)) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        } else {
            Member member = memberRepository.findById(userId).get();
            member.setPassword(password);
        }

    }

    public void checkPermission(Long id, Long userId) {
        if (!id.equals(userId)) {
            throw new AccessDeniedException("접근 권한이 없습니다.");
        }
    }

    public UserInfoDto getMyInfoDetails(Long id) {
        Member member = memberRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("해당 유저가 존재하지 않습니다."));
        UserInfoDto userInfoDto = new UserInfoDto(member);
        return userInfoDto;

    }

    @Transactional
    public void updateInfo(Long id, UserInfoDto request) {
        Member member = memberRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("해당 유저가 존재하지 않습니다."));
        if (request.getEmail() != null) {
            member.setEmail(request.getEmail());
        }
        if (request.getPassword() != null) {
            member.setPassword(request.getPassword());
        }
        if (request.getUserName() != null) {
            member.setUserName(request.getUserName());
        }
        if (request.getTemperature() != 0){  // 온도가 0이게 되면
            throw new IllegalArgumentException("온도는 임의로 변경할 수 없습니다.");
        }
        if (request.getUserId() != null) {
            throw new IllegalArgumentException("아이디는 변경할 수 없습니다.");
        }


    }

    public BookListResponse getBookLikeList(Long id) {
        List<BookResponse> bookList = new ArrayList<>();
        List<LikeBook> byUserId = likeBookRepository.findByUserId(id);
        for (LikeBook likeBook : byUserId) {
            Book book = bookRepository.findById(likeBook.getBookId()).orElseThrow(() -> new EntityNotFoundException("해당 거래글이 존재하지 않습니다."));
            BookResponse bookResponse = new BookResponse(book);
            bookList.add(bookResponse);
        }
        BookListResponse bookListResponse = new BookListResponse(bookList);
        return bookListResponse;


    }

    public UserInfoDto getPoints(Long userId) {
        Member member = memberRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("해당 유저가 존재하지 않습니다."));
        UserInfoDto userInfoDto = new UserInfoDto(member.getPoints());
        return userInfoDto;
    }
}
