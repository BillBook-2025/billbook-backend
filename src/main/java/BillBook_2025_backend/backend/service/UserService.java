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
    public UserService(MemberRepository memberRepository, LikeBookRepository likeBookRepository,
                       BookRepository bookRepository, FollowRepository followRepository,
                       S3UploadService s3UploadService) {
        this.memberRepository = memberRepository;
        this.likeBookRepository = likeBookRepository;
        this.bookRepository = bookRepository;
        this.followRepository = followRepository;
        this.s3UploadService = s3UploadService;
    }

    // 회원가입
    public Member signup(Member member) {
        if (memberRepository.findByUserId(member.getUserId()).isPresent()) {
            throw new AlreadyExistException("이미 존재하는 사용자입니다.");
        }
        Member newMember = new Member(member.getUserId(), member.getPassword(),
                member.getEmail(), member.getUserName());
        return memberRepository.save(newMember);
    }

    // 회원 탈퇴
    public void delete(DeleteMemberDto request, Long memberId) {
        Member member = findMemberById(memberId);

        if (request.getPassword().equals(member.getPassword()) && "탈퇴하겠습니다.".equals(request.getText())) {
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

        if (!findUser.getPassword().equals(member.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
        return findUser;
    }
      
    @Transactional
    public void changePassword(Long userId, String password, String confirmPassword) {
        if (!password.equals(confirmPassword)) throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        Member member = findMemberById(userId);
        member.setPassword(password);
    }

    // 접근 권한 체크
    public void checkPermission(Long targetId, Long userId) {
        if (!targetId.equals(userId)) throw new AccessDeniedException("접근 권한이 없습니다.");
    }

    public void checkSelfTransaction(Long targetId, Long userId) {
        if (targetId.equals(userId)) throw new AccessDeniedException("자신과의 거래는 불가능 합니다.");
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

    // 내 정보 조회
    public UserInfoDto getMyInfoDetails(Long id) {
        Member member = findMemberById(id);
        return new UserInfoDto(member);
    }

    @Transactional
    public void updateInfo(Long id, UserInfoDto request) {
        Member member = findMemberById(id);

        if (request.getEmail() != null) member.setEmail(request.getEmail());
        if (request.getPassword() != null) member.setPassword(request.getPassword());
        if (request.getUserName() != null) member.setUserName(request.getUserName());

        if (request.getTemperature() != 0) throw new IllegalArgumentException("온도는 임의로 변경할 수 없습니다.");
        if (request.getUserId() != null) throw new IllegalArgumentException("아이디는 변경할 수 없습니다.");
    }

    // 좋아요한 책 리스트 조회
    public BookListResponse getBookLikeList(Long id) {
        List<BookResponse> bookList = new ArrayList<>();
        List<LikeBook> byUserId = likeBookRepository.findByMemberId(id);
        for (LikeBook likeBook : byUserId) {
            Book book = bookRepository.findById(likeBook.getBook().getId()).orElseThrow(() -> new EntityNotFoundException("해당 거래글이 존재하지 않습니다."));
            BookResponse bookResponse = new BookResponse(book);
            bookList.add(bookResponse);
        }
        return new BookListResponse(bookList);
    }

    // 포인트 조회
    public UserInfoDto getPoints(Long userId) {
        Member member = findMemberById(userId);
        return new UserInfoDto(member.getPoints());
    }

    // 프로필 조회
    public ProfileDto getProfileDetail(Long userId) {
        Member member = findMemberById(userId);
        long sellNum = bookRepository.findBySellerId(userId).size();
        long buyNum = bookRepository.findByBuyerId(userId).size();
        return new ProfileDto(member, buyNum, sellNum);
    }

    // 팔로워/팔로잉 조회
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

    // 팔로우 추가/삭제
    @Transactional
    public void addFollowing(Long userId, Long targetId) {
        Member me = findMemberById(userId);
        Member target = findMemberById(targetId);

        Follow follow = new Follow(me, target);
        me.getFollowing().add(follow);
        target.getFollower().add(follow);
        followRepository.save(follow);
    }

    @Transactional
    public void deleteFollowing(Long userId, Long targetId) {
        Member me = findMemberById(userId);
        Member target = findMemberById(targetId);

        Follow follow = followRepository.findByFollowerAndFollowing(me, target)
                .orElseThrow(() -> new EntityNotFoundException("팔로우 관계가 존재하지 않습니다."));
        me.getFollowing().remove(follow);
        target.getFollower().remove(follow);
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

    // 프로필 사진 업로드
    @Transactional
    public PictureDto uploadProfileImage(Long userId, MultipartFile file) throws IOException {
        Member member = findMemberById(userId);
        PictureDto pictureDto;

        if (member.getPicture() != null) {
            s3UploadService.deleteImage(member.getPicture().getFilename());
        }
        pictureDto = s3UploadService.saveFile(file);
        member.setPicture(new Picture(pictureDto.getFilename(), pictureDto.getUrl(), member));

        return pictureDto;
    }

    // 구매/판매 리스트 조회
    public DataResponse getBuyList(Long userId) {
        return getBookListResponse(bookRepository.findByBuyerId(userId));
    }

    public DataResponse getSellList(Long userId) {
        return getBookListResponse(bookRepository.findBySellerId(userId));
    }

    // =========================
    // 공통 메서드
    // =========================
    private Member findMemberById(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("해당 유저가 존재하지 않습니다."));
    }

    private Book findBookById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("해당 거래글이 존재하지 않습니다."));
    }

    private DataResponse getBookListResponse(List<Book> books) {
        List<BookResponse> responses = new ArrayList<>();
        for (Book book : books) responses.add(new BookResponse(book));
        return new DataResponse(new BookListResponse(responses), new Pagenation());
    }
    public void checkAccessRight(Long id, Long userId) {
        if (id.equals(userId)) {
            throw new AccessDeniedException("자신과의 거래는 불가능 합니다.");
        }
    }    
}