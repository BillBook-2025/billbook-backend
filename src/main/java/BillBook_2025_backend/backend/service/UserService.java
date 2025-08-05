package BillBook_2025_backend.backend.service;

import BillBook_2025_backend.backend.dto.UserInfoDto;
import BillBook_2025_backend.backend.entity.Member;
import BillBook_2025_backend.backend.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {
    private final MemberRepository memberRepository;

    @Autowired
    public UserService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public Member signup(Member member) {
        if (memberRepository.findByUserId(member.getUserId()).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 사용자입니다.");
        }

        Member newMember = new Member(member.getUserId(), member.getPassword(), member.getEmail(), member.getUserName());
        return memberRepository.save(newMember);
    }

    public void delete(Member member) {
        if (!memberRepository.findByUserId(member.getUserId()).isPresent()) {
            throw new IllegalArgumentException("해당 유저가 존재하지 않습니다.");
        }

        memberRepository.delete(member);
    }

    public Member login(Member member) {
        if(!memberRepository.findByUserId(member.getUserId()).isPresent()) {
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
}
