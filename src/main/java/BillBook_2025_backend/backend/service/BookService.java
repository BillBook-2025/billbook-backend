package BillBook_2025_backend.backend.service;

import BillBook_2025_backend.backend.dto.BookPostRequestDto;
import BillBook_2025_backend.backend.dto.PictureDto;
import BillBook_2025_backend.backend.dto.PictureDtoList;
import BillBook_2025_backend.backend.entity.*;
import BillBook_2025_backend.backend.exception.BookNotFoundException;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
        book.setLocation(dto.getLocation());
        book.setStatus(BookStatus.PENDING);
        book.setContent(dto.getContent());
        book.setTitle(dto.getTitle());
        book.setAuthor(dto.getAuthor());
        book.setPublisher(dto.getPublisher());
        book.setIsbn(dto.getIsbn());
        //book.setCategory(dto.getCategory());
        book.setDescription(dto.getDescription());
       // book.setTotal(dto.getTotal());

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

    public List<Book> findAllBooks(Long userId) {
        Member member = memberRepository.findById(userId).orElseThrow(() -> new UnauthorizedException("로그인한 사용자만 이용이 가능합니다."));
        List<Book> bookList = bookRepository.findAll();
        return bookList;


    }

    public Book getBookDetail(Long bookId, Long userId) {
        Member member = memberRepository.findById(userId).orElseThrow(() -> new UnauthorizedException("로그인한 사용자만 이용이 가능합니다."));
        if (bookRepository.findById(bookId).isEmpty()) {
            throw new BookNotFoundException("해당 책이 존재하지 않습니다.");
        } else {
            Book book = bookRepository.findById(bookId).get();
            return book;
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
    public Book updateBookDetail(Book book, Long bookId, Long userId, List<String> deleteImages, List<MultipartFile> files) throws IOException {
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
                            Picture picture = pictureRepository.findByUrl(deleteImage).orElseThrow(() -> new EntityNotFoundException("not found picture"));
                            findBook.getPicture().remove(picture);
                            s3UploadService.deleteImage(picture.getFilename());
                        }
                    }

                    findBook.setBookPoint(book.getBookPoint());
                    findBook.setLocation(book.getLocation());
                    findBook.setContent(book.getContent());
                    findBook.setTitle(book.getTitle());
                    findBook.setAuthor(book.getAuthor());
                    findBook.setPublisher(book.getPublisher());
                    findBook.setIsbn(book.getIsbn());
                    findBook.setCategory(book.getCategory());
                    findBook.setDescription(book.getDescription());
                    return findBook;
                } else {
                    Book findBook = bookRepository.findById(bookId).get();

                    if (deleteImages != null && deleteImages.size() > 0) {
                        for (String deleteImage : deleteImages) {
                            Picture picture = pictureRepository.findByUrl(deleteImage).orElseThrow(() -> new EntityNotFoundException("not found picture"));
                            findBook.getPicture().remove(picture);
                            s3UploadService.deleteImage(picture.getFilename());
                        }
                    }

                    findBook.setBookPoint(book.getBookPoint());
                    findBook.setLocation(book.getLocation());
                    findBook.setContent(book.getContent());
                    findBook.setTitle(book.getTitle());
                    findBook.setAuthor(book.getAuthor());
                    findBook.setPublisher(book.getPublisher());
                    findBook.setIsbn(book.getIsbn());
                    findBook.setCategory(book.getCategory());
                    findBook.setDescription(book.getDescription());

                    List<Picture> pictureList = findBook.getPicture();
                    for (MultipartFile file : files) {
                        PictureDto pictureDto = s3UploadService.saveFile(file);
                        Picture picture = new Picture(pictureDto.getFilename(), pictureDto.getUrl(), findBook);
                        pictureList.add(picture);
                    }

                    return findBook;

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
                throw new AccessDeniedException("직접 올린 게시물은 대출할 수 없습니다.");
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
    public void returnBook(Long bookId, Long userId) {
        Member member = memberRepository.findById(userId).orElseThrow(() -> new UnauthorizedException("로그인한 사용자만 이용이 가능합니다."));
        if (bookRepository.findById(bookId).isEmpty()) {
            throw new BookNotFoundException("해당 책이 존재하지 않습니다.");
        } else {
            if (!bookRepository.findById(bookId).get().getSeller().getId().equals(member.getId())) {
                throw new AccessDeniedException("판매자만 반납완료를 처리할 수 있습니다");
            } else {
                bookRepository.updateStatus(bookId, BookStatus.RETURNED); //반납처리 완료
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
}
