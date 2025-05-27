# billbook-backend

---

# 📘 BillBook Backend API 명세서

물론입니다! 지금까지 정리한 내용을 **하나의 `README.md` 파일용 마크다운 형식**으로 깔끔하게 정리해드릴게요:

---

````markdown
# 📚 BillBook API 명세서

---

## ✨ 기능 개요

- 회원 가입: 아이디, 비밀번호, 이름, 전화번호, 이메일 (전화번호 본인 인증 포함)
- 커뮤니티 게시글에 책 정보 연동 (인기검색어 클릭 → 책 상세 페이지로 이동)
- 이미지 업로드 기능
  - POST `/api/users/{userId}/profile-image`
  - POST `/api/bookboards/{boardId}/images`

---

## 🌐 URI 구조

```text
/api
  ├── /websocket
  │   ├── /ws-chat              → 클라이언트 웹소켓 연결
  │   ├── /app/chat.send        → 서버로 메시지 전송
  │   └── /topic/{chatroom_id}  → 채팅방 메시지 전파
  ├── /auth
  │   ├── /login [POST, DELETE]
  │   ├── /refresh [POST]
  │   └── /find
  │       ├── /id [POST]
  │       └── /password
  │           ├── [POST]
  │           ├── /choice [POST]
  │           └── /change [POST]
  │   └── /signup [POST, DELETE]
  ├── /books
  │   ├── [GET]
  │   ├── /{book_id}
  │   │   ├── [GET, PATCH]
  │   │   ├── /chatroom [POST]
  │   │   ├── /like [GET, POST, DELETE]
  │   │   ├── /upload-images [POST]
  │   │   └── /borrow [POST]
  │   └── /search
  │       ├── [GET]
  │       └── /history
  │           ├── [GET, DELETE]
  │           └── /{history_id} [DELETE]
  │   └── /register
  │       ├── /new [POST]
  │       ├── /existing [POST]
  │       └── /{book_id} [PATCH, DELETE]
  ├── /my
  │   ├── [GET, PATCH]
  │   ├── /register [GET]
  │   ├── /borrow [GET]
  │   ├── /like [GET]
  │   ├── /point [GET, POST]
  │   ├── /temporature [GET]
  │   ├── /boards [GET]
  │   ├── /profile-image [POST]
  │   ├── /follower [GET]
  │   └── /following [GET]
  ├── /profile/{user_id}
  │   ├── [GET]
  │   ├── /follow [GET, POST, DELETE]
  │   ├── /temporature [GET, POST]
  │   ├── /borrow [GET]
  │   ├── /register [GET]
  │   └── /history [GET]
  ├── /chatrooms
  │   ├── [GET, DELETE]
  │   └── /{chat_id}
  │       ├── [GET]
  │       ├── /chat [GET, POST]
  │       ├── /picture [GET, POST]
  │       ├── /deal [POST]
  │       └── /deadline [GET, POST]
  └── /boards
      ├── [GET, POST]
      ├── /{user_id} [GET]
      └── /{board_id}
          ├── [GET, PATCH, DELETE]
          ├── /like [GET, POST, DELETE]
          └── /comments
              ├── [GET, POST, DELETE]
              └── /{comment_id} [POST]
```
````

---

## 🔐 /auth

### 🔸 /auth/login

- **POST** 로그인

```http
POST /api/auth/login
```

#### 요청

```json
{
  "id": "whwnsdus",
  "password": "1234"
}
```

#### 응답

- 성공 (200)

```json
{
  "userId": 1,
  "username": "조준연",
  "email": "test@example.com",
  "profilePic": "https://your-s3-url.com/profile.jpg",
  "temperature": 36.5,
  "isPhoneVerified": true
}
```

- 실패:

  - 400 잘못된 요청
  - 409 로그인 실패
  - 500 서버 오류

---

- **DELETE** 로그아웃

```http
DELETE /api/auth/login
```

- 세션 ID를 이용해 세션 무효화

---

### 🔸 /auth/refresh

- **POST** Access Token 재발급

```http
POST /api/auth/refresh
```

- 응답 예시

```json
{
  "accessToken": "new.jwt.access.token"
}
```

---

### 🔸 /auth/find/id

- **POST** 아이디 찾기

```http
POST /api/auth/find/id
```

#### 요청

```json
{
  "email": "abcd1234@naver.com"
}
```

#### 응답

```json
{
  "message": "등록된 이메일로 아이디를 전송했습니다."
}
```

---

### 🔸 /auth/find/password

- **POST** 비밀번호 찾기 요청

```http
POST /api/auth/find/password
```

#### 요청

```json
{
  "id": "whwnsdus11"
}
```

#### 응답

```json
{
  "can_authenticate": true,
  "message": "휴대폰 인증을 진행하세요."
}
```

---

### 🔸 /auth/find/password/choice

- **POST** 본인 인증

```http
POST /api/auth/find/password/choice
```

#### 요청

```json
{
  "name": "조준연",
  "birth_date": "19990101",
  "carrier": "SKT",
  "phone_number": "01012345678"
}
```

#### 응답

```json
{
  "tx_id": "abcdef123456",
  "ci": "고유값",
  "name": "조준연",
  "phone_number": "01012345678",
  "birth_date": "19990101",
  "success": true
}
```

---

### 🔸 /auth/find/password/change

- **POST** 비밀번호 교체

```http
POST /api/auth/find/password/change
```

#### 요청

```json
{
  "password": "1q2w3e4r@",
  "re-password": "1q2w3e4r@"
}
```

#### 응답

```json
{
  "message": "비밀번호가 변경되었습니다."
}
```

---

### 🔸 /auth/signup

- **POST** 회원가입

```http
POST /api/auth/signup
```

#### 요청

```json
{
  "id": "whwnsdus",
  "password": "1q2w3e4r@",
  "re-password": "1q2w3e4r@",
  "name": "조준연",
  "phone": "01012345678",
  "email": "abcd1234@naver.com"
}
```

#### 응답

```json
{
  "userId": 1,
  "message": "회원가입이 완료되었습니다."
}
```

---
