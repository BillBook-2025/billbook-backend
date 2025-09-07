package BillBook_2025_backend.backend.service;

import BillBook_2025_backend.backend.dto.*;
import BillBook_2025_backend.backend.entity.*;
import BillBook_2025_backend.backend.exception.AlreadyExistException;
import BillBook_2025_backend.backend.repository.BookRepository;
import BillBook_2025_backend.backend.repository.FollowRepository;
import BillBook_2025_backend.backend.repository.LikeBookRepository;
import BillBook_2025_backend.backend.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.rmi.AlreadyBoundException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class UserService {
    private final MemberRepository memberRepository;
    private final LikeBookRepository likeBookRepository;
    private final BookRepository bookRepository;
    private final FollowRepository followRepository;
    private final S3UploadService s3UploadService;

    @Autowired
    public UserService(MemberRepository memberRepository, LikeBookRepository likeBookRepository, BookRepository bookRepository, FollowRepository followRepository, S3UploadService s3UploadService) {
        this.memberRepository = memberRepository;
        this.likeBookRepository = likeBookRepository;
        this.bookRepository = bookRepository;
        this.followRepository = followRepository;
        this.s3UploadService = s3UploadService;
    }

    public Member signup(Member member) {
        if (memberRepository.findByUserId(member.getUserId()).isPresent()) {
            throw new AlreadyExistException("이미 존재하는 사용자입니다.");
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

    public Member login(MemberDto member) {
        if(!memberRepository.findByUserId(member.getUserId()).isPresent()) {
            throw new EntityNotFoundException("해당 아이디를 찾을 수 없습니다.");
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
        UserInfoDto userInfoDto = UserInfoDto.builder()
                .userName(member.getUserName())
                .email(member.getEmail())
                .temperature(member.getTemperature())
                .points(member.getPoints())
                .userId(member.getUserId())
                .build();
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
        List<LikeBook> byUserId = likeBookRepository.findByMemberId(id);
        for (LikeBook likeBook : byUserId) {
            Book book = bookRepository.findById(likeBook.getBook().getId()).orElseThrow(() -> new EntityNotFoundException("해당 거래글이 존재하지 않습니다."));
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

    public ProfileDto getProfileDetail(Long userId) {
        Member member = memberRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("해당 유저가 존재하지 않습니다."));
        Long sellNum = (long)bookRepository.findBySellerId(userId).size();
        Long buyNum = (long)bookRepository.findByBuyerId(userId).size();
        return new ProfileDto(member, buyNum, sellNum);
    }

    public List<FollowDto> getFollowers(Long userId) {
        Member member = memberRepository.findById(userId).orElseThrow(
                () -> new EntityNotFoundException("해당 유저가 존재하지 않습니다."));

        List<FollowDto> followings = new ArrayList<>();
        for (Follow follow : followRepository.findByFollower(member)) {
            FollowDto dto = new FollowDto(follow.getFollowing());
            followings.add(dto);
        }

        return followings;

    }


    public List<FollowDto> getFollowings(Long userId) {
        Member member = memberRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("해당 유저가 존재하지 않습니다."));
        List<FollowDto> followers = new ArrayList<>();
        for (Follow follow : followRepository.findByFollowing(member)) {
            FollowDto dto = new FollowDto(follow.getFollower());
            followers.add(dto);
        }

        return followers;
    }

    public void addFollowing(Long userId, Long followingId) {
        Member following = memberRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("해당 유저가 존재하지 않습니다."));
        Member follower = memberRepository.findById(followingId).orElseThrow(() -> new EntityNotFoundException("해당 유저가 존재하지 않습니다."));

        Follow follow = new Follow(follower, following);
        following.getFollowing().add(follow);
        follower.getFollower().add(follow);
        followRepository.save(follow);

    }

    public void deleteFollowing(Long userId, Long followingId) {
        Member following = memberRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("해당 유저가 존재하지 않습니다."));
        Member follower = memberRepository.findById(followingId).orElseThrow(() -> new EntityNotFoundException("해당 유저가 존재하지 않습니다."));

        Follow follow = followRepository.findByFollowerAndFollowing(follower, following)
                .orElseThrow(() -> new EntityNotFoundException("팔로우 관계가 존재하지 않습니다."));
        following.getFollower().remove(follow);
        follower.getFollowing().remove(follow);
        followRepository.delete(follow);
    }

    public void checkAccessRight(Long id, Long userId) {
        if (id.equals(userId)) {
            throw new AccessDeniedException("자신과의 거래는 불가능 합니다.");
        }
    }

    public DealHistory getDealHistory(Long otherUserId, Long userId) {
        Member user = memberRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("not found member"));
        Member otherUser = memberRepository.findById(otherUserId).orElseThrow(() -> new EntityNotFoundException("not found member"));
        List<Book> sellHistory = bookRepository.findBySellerAndBuyer(user, otherUser); //해당 유저에게 판매한 기록
        List<Book> buyHistory = bookRepository.findBySellerAndBuyer(otherUser, user);  //해당 유저 게시물의 구매 기록
        log.info("sellHistoryNum = {}", sellHistory.size());
        log.info("buyHistoryNum = {}", buyHistory.size());
        DealHistory response = new DealHistory((long) (sellHistory.size() + buyHistory.size()));
        return response;
    }

    public void checkBookSeller(Long userId, Long bookId) {
        Book book = bookRepository.findById(bookId).orElseThrow(() -> new EntityNotFoundException("해당 거래글을 찾을 수 없습니다."));
        Long sellerId = book.getSeller().getId();
        if (sellerId.equals(userId)) {
            throw new AccessDeniedException("접근권한이 없습니다.");
        }
    }

    @Transactional
    public PictureDto uploadProfileImage(Long userId, MultipartFile file) throws IOException {
        Member member = memberRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("해당 유저는 존재하지 않습니다."));
        if (member.getPicture() == null) {  //프로필 사진이 없는 경우
            PictureDto pictureDto = s3UploadService.saveFile(file);
            Picture picture = new Picture(pictureDto.getFilename(), pictureDto.getUrl(), member);
            member.setPicture(picture);
            return pictureDto;
        } else { //프로필 사진이 이미 존재하는 경우 교체
            Picture propfilePicture = member.getPicture();
            s3UploadService.deleteImage(propfilePicture.getFilename());
            PictureDto pictureDto = s3UploadService.saveFile(file);
            Picture picture = new Picture(pictureDto.getFilename(), pictureDto.getUrl(), member);
            member.setPicture(picture);
            return pictureDto;
        }
    }

    public DataResponse getBuyList(Long userId) {
        List<Book> byBuyerId = bookRepository.findByBuyerId(userId);
        List<BookResponse> responses = new ArrayList<>();
        for (Book book : byBuyerId) {
            BookResponse bookResponse = new BookResponse(book);
            responses.add(bookResponse);
        }
        BookListResponse bookListResponse = new BookListResponse(responses);
        DataResponse dataResponse = new DataResponse(bookListResponse, new Pagenation());  // Pagenation은 지금 공백임
        return dataResponse;

    }

    public DataResponse getSellList(Long userId) {

        List<Book> bySellerId = bookRepository.findBySellerId(userId);
        List<BookResponse> responses = new ArrayList<>();
        for (Book book : bySellerId) {
            BookResponse bookResponse = new BookResponse(book);
            responses.add(bookResponse);
        }
        BookListResponse bookListResponse = new BookListResponse(responses);
        DataResponse dataResponse = new DataResponse(bookListResponse, new Pagenation());  // Pagenation은 지금 공백임
        return dataResponse;

    }
}
