package BillBook_2025_backend.backend.service;

import BillBook_2025_backend.backend.dto.*;
import BillBook_2025_backend.backend.entity.*;
import BillBook_2025_backend.backend.exception.BookNotFoundException;
import BillBook_2025_backend.backend.exception.FaultAccessException;
import BillBook_2025_backend.backend.exception.UnauthorizedException;
import BillBook_2025_backend.backend.repository.BookRepository;
import BillBook_2025_backend.backend.repository.LikeBookRepository;
import BillBook_2025_backend.backend.repository.MemberRepository;
import BillBook_2025_backend.backend.repository.PictureRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class BookService {
    private final BookRepository bookRepository;
    private final MemberRepository memberRepository;
    private final LikeBookRepository likeBookRepository;
    private final S3UploadService s3UploadService;
    private final PictureRepository pictureRepository;

    @Autowired
    public BookService(BookRepository bookRepository, MemberRepository memberRepository, LikeBookRepository likeBookRepository, S3UploadService s3UploadService, PictureRepository pictureRepository) {
        this.bookRepository = bookRepository;
        this.memberRepository = memberRepository;
        this.likeBookRepository = likeBookRepository;
        this.s3UploadService = s3UploadService;
        this.pictureRepository = pictureRepository;
    }

    public void register(BookPostRequestDto dto, Long userId, List<MultipartFile> files) throws IOException {
        Member member = memberRepository.findById(userId).orElseThrow(() -> new UnauthorizedException("로그인한 사용자만 등록이 가능합니다."));

        Book book = new Book();
        book.setSeller(member);
        book.setBookPoint(dto.getBookPoint());
        book.setStatus(BookStatus.PENDING);
        book.setContent(dto.getContent());
        book.setTitle(dto.getTitle());
        book.setAuthor(dto.getAuthor());
        book.setPublisher(dto.getPublisher());
        book.setCondition(dto.getCondition());
        book.setIsbn(dto.getIsbn());
        //book.setCategory(dto.getCategory());
        book.setDescription(dto.getDescription());
        book.setTime(LocalDateTime.now());
       // book.setTotal(dto.getTotal());
        book.setAddress(dto.getLocate().getAddress());
        book.setLatitude(dto.getLocate().getLatitude());
        book.setLongitude(dto.getLocate().getLongitude());
        book.setRegionLevel1(dto.getLocate().getRegionLevel1());
        book.setRegionLevel2(dto.getLocate().getRegionLevel2());
        book.setRegionLevel3(dto.getLocate().getRegionLevel3());

        List<Picture> pictures = new ArrayList<>();
        for (MultipartFile file : files) {
            PictureDto pictureDto = s3UploadService.saveFile(file);
            Picture picture = new Picture(pictureDto.getFilename(), pictureDto.getUrl(), book);
            pictures.add(picture);
        }
        book.setPicture(pictures);

        bookRepository.save(book);
        pictureRepository.saveAll(pictures);
    }

    public List<BookResponse> findAllBooks(Long userId) {
        Member member = memberRepository.findById(userId).orElseThrow(() -> new UnauthorizedException("로그인한 사용자만 이용이 가능합니다."));
        List<Book> bookList = bookRepository.findAll();
        List<BookResponse> bookResponseList = new ArrayList<>();
        for (Book book : bookList) {
            bookResponseList.add(new BookResponse(book));
        }
        return bookResponseList;


    }

    public BookResponse getBookDetail(Long bookId, Long userId) {
        Member member = memberRepository.findById(userId).orElseThrow(() -> new UnauthorizedException("로그인한 사용자만 이용이 가능합니다."));
        if (bookRepository.findById(bookId).isEmpty()) {
            throw new BookNotFoundException("해당 책이 존재하지 않습니다.");
        } else {
            Book book = bookRepository.findById(bookId).get();
            return new BookResponse(book);
        }
    }

    public Long like(Long bookId, Long userId) { //좋아요 누르기
        Member member = memberRepository.findById(userId).orElseThrow(() -> new UnauthorizedException("로그인한 사용자만 이용이 가능합니다."));
        if (bookRepository.findById(bookId).isEmpty()) {
            throw new BookNotFoundException("해당 책이 존재하지 않습니다.");
        } else {
            Optional<LikeBook> existing = likeBookRepository.findByBookIdAndMemberId(bookId, userId);
            if (existing.isPresent()) { //좋아요 취소
                likeBookRepository.delete(existing.get());
            } else { //좋아요
                Book book = bookRepository.findById(bookId).get();
                LikeBook likeBook = new LikeBook(book, member);
                likeBookRepository.save(likeBook);
            }
            return likeBookRepository.countByBookId(bookRepository.findById(bookId).get());
        }
    }

    public Long checkLike(Long bookId) {
        if (bookRepository.findById(bookId).isEmpty()) {
            throw new BookNotFoundException("해당 책이 존재하지 않습니다.");
        } else {
            return likeBookRepository.countByBookId(bookRepository.findById(bookId).get());
        }
    }

    @Transactional
    public BookResponse updateBookDetail(BookPostRequestDto book, Long bookId, Long userId, List<String> deleteImages, List<MultipartFile> files) throws IOException {
        Member member = memberRepository.findById(userId).orElseThrow(() -> new UnauthorizedException("로그인한 사용자만 수정이 가능합니다.")); //로그인x일때
        if (bookRepository.findById(bookId).isEmpty()) { //책 게시물이 존재하지 않을 경우
            throw new BookNotFoundException("해당 책이 존재하지 않습니다.");
        } else {
            if (!bookRepository.findById(bookId).get().getSeller().getId().equals(member.getId())) { //판매자 아이디가 아닐 경우
                throw new AccessDeniedException("판매자만 수정할 수 있습니다");
            } else {
                if (files == null || files.size() == 0) {
                    Book findBook = bookRepository.findById(bookId).get();

                    if (deleteImages != null && deleteImages.size() > 0) {
                        for (String deleteImage : deleteImages) {
                            Picture picture = pictureRepository.findByBookAndUrl(findBook, deleteImage).orElseThrow(() -> new EntityNotFoundException("not found picture"));
                            findBook.getPicture().remove(picture);
                            s3UploadService.deleteImage(picture.getFilename());
                        }
                    }

                    findBook.setBookPoint(book.getBookPoint());
                    findBook.setCondition(book.getCondition());
                    findBook.setContent(book.getContent());
                    findBook.setTitle(book.getTitle());
                    findBook.setAuthor(book.getAuthor());
                    findBook.setPublisher(book.getPublisher());
                    findBook.setIsbn(book.getIsbn());
                    findBook.setAddress(book.getLocate().getAddress());
                    findBook.setLatitude(book.getLocate().getLatitude());
                    findBook.setLongitude(book.getLocate().getLongitude());
                    findBook.setRegionLevel1(book.getLocate().getRegionLevel1());
                    findBook.setRegionLevel2(book.getLocate().getRegionLevel2());
                    findBook.setRegionLevel3(book.getLocate().getRegionLevel3());
                    //findBook.setCategory(book.getCategory());
                    findBook.setDescription(book.getDescription());
                    return new BookResponse(findBook);
                } else {
                    Book findBook = bookRepository.findById(bookId).get();

                    if (deleteImages != null && deleteImages.size() > 0) {
                        for (String deleteImage : deleteImages) {
                            Picture picture = pictureRepository.findByBookAndUrl(findBook, deleteImage).orElseThrow(() -> new EntityNotFoundException("not found picture"));
                            findBook.getPicture().remove(picture);
                            s3UploadService.deleteImage(picture.getFilename());
                        }
                    }

                    findBook.setBookPoint(book.getBookPoint());
                    findBook.setCondition(book.getCondition());
                    findBook.setContent(book.getContent());
                    findBook.setTitle(book.getTitle());
                    findBook.setAuthor(book.getAuthor());
                    findBook.setPublisher(book.getPublisher());
                    findBook.setIsbn(book.getIsbn());
                    //findBook.setCategory(book.getCategory());
                    findBook.setDescription(book.getDescription());
                    findBook.setAddress(book.getLocate().getAddress());
                    findBook.setLatitude(book.getLocate().getLatitude());
                    findBook.setLongitude(book.getLocate().getLongitude());
                    findBook.setRegionLevel1(book.getLocate().getRegionLevel1());
                    findBook.setRegionLevel2(book.getLocate().getRegionLevel2());
                    findBook.setRegionLevel3(book.getLocate().getRegionLevel3());

                    List<Picture> pictureList = findBook.getPicture();
                    for (MultipartFile file : files) {
                        PictureDto pictureDto = s3UploadService.saveFile(file);
                        Picture picture = new Picture(pictureDto.getFilename(), pictureDto.getUrl(), findBook);
                        pictureList.add(picture);
                    }

                    return new BookResponse(findBook);

                }

            }

        }
    }

    @Transactional
    public void borrow(Long bookId, Long userId) {
        Member member = memberRepository.findById(userId).orElseThrow(() -> new UnauthorizedException("로그인한 사용자만 이용이 가능합니다."));
        if (bookRepository.findById(bookId).isEmpty()) {
            throw new BookNotFoundException("해당 책이 존재하지 않습니다.");
        } else {
            if (bookRepository.findById(bookId).get().getSeller().getId().equals(member.getId())) {
                throw new FaultAccessException("직접 올린 게시물은 대출할 수 없습니다.");
            } else {
                bookRepository.updateBuyerId(bookId, member, BookStatus.BORROWING);
            }
        }
    }

    public void delete(Long bookId, Long userId) {
        Member member = memberRepository.findById(userId).orElseThrow(() -> new UnauthorizedException("로그인한 사용자만 이용이 가능합니다."));
        if (bookRepository.findById(bookId).isEmpty()) {
            throw new BookNotFoundException("해당 책이 존재하지 않습니다.");
        } else {
            if (!bookRepository.findById(bookId).get().getSeller().getId().equals(member.getId())) {
                throw new AccessDeniedException("판매자만 글을 삭제할 수 있습니다");
            } else {
                bookRepository.delete(bookRepository.findById(bookId).get());
            }
        }
    }

    @Transactional
    public BookResponse returnBook(Long bookId, Long userId) {
        Member member = memberRepository.findById(userId).orElseThrow(() -> new UnauthorizedException("로그인한 사용자만 이용이 가능합니다."));
        if (bookRepository.findById(bookId).isEmpty()) {
            throw new BookNotFoundException("해당 책이 존재하지 않습니다.");
        } else {
            if (!bookRepository.findById(bookId).get().getSeller().getId().equals(member.getId())) {
                throw new FaultAccessException("판매자만 반납완료를 처리할 수 있습니다");
            } else {
                Book book = bookRepository.findById(bookId).get();
                if (book.getStatus().equals(BookStatus.RETURNED)){
                    throw new FaultAccessException("이미 반납 처리된 게시물입니다.");
                }
                if (book.getBuyer() == null || book.getStatus().equals(BookStatus.PENDING)) {
                    throw new FaultAccessException("거래전 게시물은 반납처리할 수 없습니다.");
                }
                book.setStatus(BookStatus.RETURNED); //반납처리 완료
                book.setReturnTime(LocalDateTime.now());


                Book newBook = Book.builder()
                        .title(book.getTitle())
                        .author(book.getAuthor())
                        .bookPoint(book.getBookPoint())
                        .content(book.getContent())
                        .category(book.getCategory())
                        .condition(book.getCondition())
                        .isbn(book.getIsbn())
                        .publisher(book.getPublisher())
                        .location(book.getLocation())
                        .status(BookStatus.PENDING)
                        .address(book.getAddress())
                        .latitude(book.getLatitude())
                        .longitude(book.getLongitude())
                        .regionLevel1(book.getRegionLevel1())
                        .regionLevel2(book.getRegionLevel2())
                        .regionLevel3(book.getRegionLevel3())
                        .time(LocalDateTime.now())
                        .description(book.getDescription())
                        .seller(member)
                        .build();

                List<Picture> pictureList = new ArrayList<>();
                for (Picture oldPic : book.getPicture()) {
                    String originalFilename = oldPic.getFilename();
                    int idx = originalFilename.indexOf("_");
                    String cleanName = (idx != -1) ? originalFilename.substring(idx+1) : originalFilename;
                    String uniqueName = UUID.randomUUID() + "_" + cleanName;

                    Picture newPic = new Picture(uniqueName, oldPic.getUrl(), newBook);
                    pictureList.add(newPic);
                }
                newBook.setPicture(pictureList);

                Book save = bookRepository.save(newBook);

                return new BookResponse(save);


            }
        }
    }

    @Transactional
    public PictureDtoList uploadImages(Long bookId, Long userId, List<MultipartFile> files) throws IOException {
        Book book = bookRepository.findById(bookId).orElseThrow(() -> new EntityNotFoundException("해당 게시물을 찾을 수 없습니다."));
        List<PictureDto> pictureDtos = new ArrayList<>();
        for (MultipartFile file : files) {
            PictureDto request = s3UploadService.saveFile(file);
            Picture picture = new Picture(request.getFilename(), request.getUrl(), book);
            book.getPicture().add(picture);
            pictureDtos.add(request);
        }
        PictureDtoList response = new PictureDtoList(pictureDtos);  //파일명도 줘야하나
        return response;
    }

    public void deleteImages(PictureDto request) {
        String filename = request.getFilename();
        Picture picture = pictureRepository.findByFilename(filename)
                .orElseThrow(() -> new EntityNotFoundException(filename + "가 존재하지 않습니다."));
        pictureRepository.delete(picture);
        s3UploadService.deleteImage(filename);
    }

    public List<BookResponse> searchBooks(String keyword, Long userId) {
        List<BookResponse> bookList = new ArrayList<>();
        for (Book book : bookRepository.searchBooksByKeyword(keyword)) {
            bookList.add(new BookResponse(book));
        }
        return bookList;

    }
}
