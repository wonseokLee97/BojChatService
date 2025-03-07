class WebSocketManager {
    constructor(token, problemId, userTier) {
        // WebSocket 관련 변수 초기화
        this.token = token;
        this.problemId = problemId;
        this.userTier = userTier;
        this.subscriptionId = 'entry-' + problemId;
        this.localhost = 'https://bojchat.store';
        this.stompClient = null;
        this.userName = null;
        this.nameTag = null;
        this.previousScrollPos = null;
        this.hasMoreData = true;
        this.lastMessageId = null;
        this.lastMessageSender = null;
        this.rec = false;
        this.userScrolled = null;
    }

    // WebSocket 연결 함수
    async connect() {

        const socket = new SockJS(this.localhost + '/connect');  // SockJS를 사용하여 WebSocket 연결
        this.stompClient = Stomp.over(socket);  // Stomp 클라이언트 설정

        // JWT 토큰 디코딩 후 사용자 정보 추출
        const decodedToken = await this.decodeJwt(this.token);
        const ipFromToken = decodedToken.aud[0];

        // this.userName = decodedToken.userName;
        this.userName = this.getUsernameFromDOM();
        this.nameTag = decodedToken.sub;

        // 화면에 접속한 사용자 정보 표시
        $("#current-user-name").text(this.userName);  // 사용자 이름
        $("#current-user-id").text(this.nameTag);   // 사용자 ID

        const headers = {
            Authorization: 'Bearer ' + this.token,
            'X-Client-IP': ipFromToken,  // IP 정보 설정
            'bojName': this.userName
        };


        // WebSocket 서버에 연결
        this.stompClient.connect(headers, this.onConnect.bind(this), this.onError.bind(this));
    }

    // JWT 토큰 디코딩 함수
    async decodeJwt(token) {
        const base64Url = token.split('.')[1];
        const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
        const jsonPayload = decodeURIComponent(atob(base64).split('').map(c => '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2)).join(''));
        return JSON.parse(jsonPayload);
    }

    // WebSocket 연결 성공 시 호출되는 콜백 함수
    onConnect(frame) {
        console.log('Connected: ' + frame);

        if (this.rec == false) this.loadChat();  // 기존 채팅 메시지 로드
        
        // 입장자 수 구독
        this.stompClient.subscribe('/sub/channel/entry' + this.problemId, (entryCount) => {
            $("#current-user-count").text(entryCount.body);  // 화면에 입장자 수 업데이트
        });

        // 에러 구독
        this.stompClient.subscribe('/user/queue/errors' + this.problemId, (message) => {
            const errorMessage = JSON.parse(message.body);  // 메시지를 파싱
            console.error("Error occurred:", errorMessage);  // 에러 메시지 출력
            console.log("Error Detail:", errorMessage.message);  // 에러 상세 메시지 출력
            
            // 추가적으로 UI에서 에러를 보여주는 부분을 처리할 수도 있음
            // 예: alert(errorMessage.message);
        });

        // 채팅 메시지 구독
        this.stompClient.subscribe('/sub/channel/' + this.problemId, (chatMessage) => {
            const message = JSON.parse(chatMessage.body);
            this.showChat(message);  // 새 채팅 메시지 표시
        });
    }

    // WebSocket 연결 실패 시 호출되는 콜백 함수
    onError(error) {
        alert(error + "!!!!!!");
    }

    
    // 사용자 이름을 DOM에서 추출하는 함수
    getUsernameFromDOM() {
        const usernameElement = document.querySelector('.username'); // 사용자 이름이 있는 a 태그를 선택
        if (usernameElement) {
        const href = usernameElement.getAttribute('href'); // href 속성 가져오기
        const userName = href.split('/user/')[1]; // '/user/' 이후의 부분 추출
        return userName; // 사용자 이름 반환
        }
        return null; // 사용자 이름을 찾을 수 없으면 null 반환
    }

    disconnect() {
        this.previousScrollPos = null;
        this.hasMoreData = true;
        this.lastMessageId = null;
        this.lastMessageSender = null;
        this.rec = false;
        this.userScrolled = null;
        this.stompClient.disconnect();
        console.log("끊어짐!");
    }

    // WebSocket 연결 상태 확인 함수
    isConnected() {
        return this.stompClient && this.stompClient.connected;
    }


    async loadChat() {
        const chatContainer = $("#chatting");
        chatContainer.empty();  // 기존 채팅 목록 초기화
    
        // 첫 10개 메시지를 화면에 추가
        await this.loadMoreChat();  // offset = 0, limit = 10
    }

    async loadMoreChat() {
        const chatContainer = $("#chatting");

        console.log(`Loading chats from lastMessageId: ${this.lastMessageId}`);

        // 첫 로딩시에는 가장 최근 메시지들을 가져온다.
        // 근데, hasMoreData가 False가 되는 경우는 가장 마지막 메시지에 닿았을 때 더 이상 가져오지 못하는 것이다.
        // 따라서 이 경우 데이터가 더 추가되던 말던 이미 마지막 메시지까지 다 긁어왔기 때문에 더 이상 데이터를 가져올 필요가 없다.
        let messagesToDisplay = null;
        // 서버에서 10개씩 데이터 가져오기
        if (this.hasMoreData) {
            if (this.lastMessageId == null) {
                messagesToDisplay = await this.getChatListByLimit(10); // offset과 limit 전달
            } else {
                messagesToDisplay = await this.getChatListByLastMessageId(this.lastMessageId, 10);
            }
        }

        if (!messagesToDisplay || messagesToDisplay.length === 0) {
            this.hasMoreData = false;
            console.log("No more chats to load.");
            return; // 더 이상 데이터가 없으면 종료
        }

        for (let i = 0; i < messagesToDisplay.length; i++) {
            const chatMessage = messagesToDisplay[i];
            if (i == messagesToDisplay.length - 1) {
                this.lastMessageId = chatMessage.id;
            }

            const chatHtml = this.createChatHtml(
                // chatMessage.userName + " " + chatMessage.nameTag,
                chatMessage.userName,
                chatMessage.userTier,
                chatMessage.message,
                chatMessage.createdAt,
                chatMessage.ipAddress
            );
            chatContainer.prepend(chatHtml);  // 위쪽에 추가 (스크롤 위치를 위로 올리기 위해)
        }
        
        if (this.previousScrollPos != null) {
            this.scrollToPreviousPosition();
        } else {
            this.scrollChatToBottom();
        }
    }

    setupScrollEvent() {
        const chatContainer = $("#chatting");
        // 스크롤 이벤트 처리
        chatContainer.on('scroll', () => {
            if (chatContainer.scrollTop() === 0) {  // 스크롤이 맨 위에 도달했을 때
                this.loadMoreChat(this.offset);  // 추가 메시지 로드
                // offset += 10;  // 10개씩 증가
            }
        });
    }

    
    // 채팅 목록을 하단으로 스크롤하는 함수
    scrollChatToBottom() {
        const chatContainer = document.getElementById('chatting');
        chatContainer.scrollTop = chatContainer.scrollHeight;
        // console.log(chatContainer.scrollHeight);
        // this.previousScrollPos = chatContainer.scrollHeight;
        this.previousScrollPos = 834;
    }

    // 스크롤을 마지막으로 본 채팅 내용 위치로 설정
    scrollToPreviousPosition() {
        const chatContainer = document.getElementById('chatting');

        if (chatContainer && this.previousScrollPos != null) {
            const scrollHeight = chatContainer.scrollHeight;
            chatContainer.scrollTop = scrollHeight - this.previousScrollPos;
            this.previousScrollPos = scrollHeight;
        }
    };

    
    
    // 날짜 구분선을 추가하는 함수
    addDateDivider(date) {
        const dateDivider = `<div class="date-divider">${date}</div>`;
        $("#chatting").append(dateDivider);
    }

    // 시간 형식 (오전/오후 HH:MM)으로 변환하는 함수
    formatDateTime(createdAt) {
        const date = new Date(createdAt);
        let hours = date.getHours();
        const minutes = String(date.getMinutes()).padStart(2, "0");

        const period = hours >= 12 ? "오후" : "오전"; // 12시 이후는 '오후', 그 전은 '오전'
        hours = hours % 12; // 12시간제로 변환
        hours = hours ? hours : 12; // 0시를 12로 변환

        const year = date.getFullYear();
        const month = String(date.getMonth() + 1).padStart(2, "0");
        const day = String(date.getDate()).padStart(2, "0");

        return `${year}년 ${month}월 ${day}일 | ${period} ${hours}:${minutes}`;
    }


    // limit 기준으로 페이지네이션
    async getChatListByLimit(limit) {
        try {
            const response = await fetch(
                this.localhost + `/message?problemId=${this.problemId}&limit=${limit}`, 
            {
                method: "GET",
                headers: { 
                    Authorization: 'Bearer ' + this.token, // Authorization 헤더 추가
                    'bojName': this.getUsernameFromDOM()
                }  
            });

            const chatList = await response.json();

            if (!response.ok) {
                throw new Error(`HTTP error! Status: ${response.status}`);
            }

            return chatList.response;  // 서버에서 받은 채팅 목록 반환
        } catch (error) {
            console.error(error + "!!!");
        }
    }

    // lastMessageId 기준으로 페이지네이션
    async getChatListByLastMessageId(lastMessageId, limit) {
        try {
            const response = await fetch(
                this.localhost + `/message/lastMessageId?problemId=${this.problemId}&limit=${limit}&lastMessageId=${lastMessageId}`, 
            {
                method: "GET",
                headers: { 
                    Authorization: 'Bearer ' + this.token,
                    'bojName': this.getUsernameFromDOM()
                }
            });

            if (!response.ok) {
                throw new Error(`HTTP error! Status: ${response.status}`);
            }

            const chatList = await response.json();
            return chatList.response;  // 서버에서 받은 채팅 목록 반환
        } catch (error) {
            console.error(error);
        }
    }


    // 새 채팅 메시지 화면에 표시하는 함수
    showChat(chatMessage) {
        const chatHtml = this.createChatHtml(
            // chatMessage.userName + " " + chatMessage.nameTag, 
            chatMessage.userName,
            chatMessage.userTier,
            chatMessage.message, 
            chatMessage.createdAt, 
            chatMessage.ipAddress
        );

        $("#chatting").append(chatHtml);  // 새 메시지 추가

        // 내가 입력한거면 알림 안떠야됨, 그리고 하단으로 스크롤 강제로 되어야됨
        // if (chatMessage.nameTag == this.nameTag) {
        //     this.scrollChatToBottom();  // 채팅 목록이 하단에 위치하도록 스크롤
        // }
        // 사용자가 스크롤을 건드렸는지 추적
        if (!this.userScrolled) {
            this.scrollChatToBottom();  // 자동 스크롤 다운
        }
        // 남이 입력한거면 알림 떠야됨, 그리고 하단 스크롤 ㄴㄴ
        else {
            if (chatMessage.nameTag == this.nameTag) {
                this.scrollChatToBottom();  // 채팅 목록이 하단에 위치하도록 스크롤
            } else {
                this.alertChat(chatMessage);
            }
        }
    }

    // 알림 표시 함수
    alertChat(chatMessage) {
        // 알림 박스 업데이트
            $("#new-message-alert").html(`${chatMessage.userName + " " + chatMessage.nameTag}: ${chatMessage.message}`);
            $("#new-message-alert").fadeIn(200);
        // }
    }

    // 사용자가 스크롤을 움직였는지 감지
    trackUserScroll() {
        const chatContainer = $("#chatting");

        chatContainer.on("scroll", () => {
            const currentScroll = chatContainer.scrollTop();
            const maxScroll = chatContainer[0].scrollHeight - chatContainer.outerHeight();

            // 사용자가 직접 스크롤을 올린 경우 감지
            this.userScrolled = currentScroll < maxScroll - 5;
        });
    }

    deleteAlert() {
        $("#chatting").on("scroll", () => {
            const chatContainer = $("#chatting");
            const isAtBottom = (chatContainer[0].scrollHeight - chatContainer.scrollTop()) <= chatContainer.outerHeight();
    
            // 스크롤이 최하단으로 이동했으면 알림을 사라지게 함
            if (isAtBottom) {
                $("#new-message-alert").fadeOut(200); // 알림 사라짐
            }
        });
    }


    // 채팅 메시지의 HTML 요소 생성 함수
    createChatHtml(sender, tier, message, createdAt, ip) {
        const formattedTime = this.formatDateTime(createdAt);  // 시간 형식 변환
        const tierImage = `<img src="${tier}" alt="Tier" class="tier-image">`;

        if (sender === this.userName) {
            return `<div class="chat ch2">
                        <div class="message-info">
                            <div class="sender">${tierImage}${sender}</div>
                            <div class="textbox">${message}</div>
                            <div class="extra-info">${formattedTime}</div>
                        </div>
                    </div>`;
        } else {
            return `<div class="chat ch1">
                        <div class="message-info">
                            <div class="sender">${tierImage}${sender}</div>
                            <div class="textbox">${message}</div>
                            <div class="extra-info">${formattedTime}</div>
                        </div>
                    </div>`;
        }
    }

    // IP 주소를 축약하는 함수 (예: 192.168.1.1 -> 192.168)
    getShortenedIp(ip) {
        const sectors = ip.split(".");
        return sectors.slice(0, 2).join(".");
    }


    // 채팅 메시지 전송 함수
    sendChat(message) {
        if (message.trim() === "") return;  // 빈 메시지는 전송하지 않음
        const headers = { token: this.token };
        const request = {
            "problemId": this.problemId,
            "message": message,
            "randId": this.userName,
            "userTier": this.userTier,
            "nameTag": this.nameTag
        };


        this.stompClient.send("/pub/chat", headers, JSON.stringify(request));  // 서버로 메시지 전송
    }
}

class ChatApp {
    constructor(problemId) {
        this.websocketManager = null;  // WebSocketManager 인스턴스 초기화
        this.localhost = 'https://bojchat.store';
        this.token = localStorage.getItem('token'); // localStorage 에서 가져옴
        this.problemId = problemId;
        this.userTier = null;
    }

    // 애플리케이션 초기화 함수
    async init() {
        try {
            let bojName = this.getUsernameFromDOM();
            const response = await fetch(this.localhost + '/init', 
                {
                    method: "GET",
                    headers: { 
                        'Authorization': 'Bearer ' + this.token,
                        'bojName': bojName
                    }  // Authorization 헤더 추가
                }
            );


            if (bojName == null) {
                throw new Error("BOJ 로그인 이후 사용해주세요.");            
            }

            this.userTier = await this.getUserTierFromDOM(bojName);
            const data = await response.json();

            // response가 오지 않는 경우, 성공적으로 토큰 유효성 검사 완료
            if (data.response) {
                // error가 발생한 경우 (EXPIRED_TOKEN, INVALID_TOKEN)
                if (data.success == false) {
                    // 새로운 토큰으로 변경
                    console.log("토큰이 유효하지 않아 새로운 토큰을 발급합니다.")
                    localStorage.setItem('token', data.response);
                } 
                // 성공적으로 발급 받은경우 (SUCCESS_TOKEN_ISSUANCE)
                else { 
                    console.log("토큰이 존재하지 않아 새로운 토큰을 발급합니다.")
                    localStorage.setItem('token', data.response);
                }
            }

            this.token = localStorage.getItem('token');
            
            this.websocketManager = new WebSocketManager(this.token, this.problemId, this.userTier);  // WebSocketManager 생성
            await this.websocketManager.connect();  // WebSocket 연결
            this.setupEventHandlers();  // 이벤트 핸들러 설정
            this.websocketManager.setupScrollEvent();  // 스크롤 이벤트 핸들러 설정
            this.websocketManager.trackUserScroll();
            this.websocketManager.deleteAlert();
        } catch (error) {
            console.error(error);
            alert(error);
        }
    }

    
    // WebSocket 연결 종료 함수
    disconnect() {
        this.websocketManager.disconnect();
        this.websocketManager = null;
        console.log("Disconnected");
    }

    // 이벤트 핸들러 설정 함수
    setupEventHandlers() {
        // 채팅 폼 제출 시 채팅 전송
        $("#chat-form").on('submit', (e) => {
            e.preventDefault();
            this.sendChat();  // 채팅 전송
        });

        // 'send' 버튼 클릭 시 채팅 전송
        $("#send").click(() => {
            this.sendChat();  // 채팅 전송
        });
    }

    // 채팅 전송 함수
    sendChat() {
        const message = $("#message").val();  // 입력된 메시지 가져오기
        this.websocketManager.sendChat(message);  // WebSocket을 통해 메시지 전송
        $("#message").val("");  // 입력 필드 초기화
    }


    // 사용자 이름을 DOM에서 추출하는 함수
    getUsernameFromDOM() {
        const usernameElement = document.querySelector('.username'); // 사용자 이름이 있는 a 태그를 선택

        

        if (usernameElement) {
            const href = usernameElement.getAttribute('href'); // href 속성 가져오기
            const userName = href.split('/user/')[1]; // '/user/' 이후의 부분 추출
            return userName; // 사용자 이름 반환
        }
        return null; // 사용자 이름을 찾을 수 없으면 null 반환
    }

    
    async getUserTierFromDOM(bojName) {
        try {
            const response = await fetch(`https://api-py.vercel.app/?r=https://solved.ac/api/v3/user/show?handle=${bojName}`);
            
            // 만약 404 또는 다른 에러 상태일 경우 기본 이미지를 반환
            if (!response.ok) {
                console.error(`Error: ${response.status}`);
                return `https://d2gd6pc034wcta.cloudfront.net/tier/0.svg`;
            }
    
            const data = await response.json();
            return `https://d2gd6pc034wcta.cloudfront.net/tier/${data.tier}.svg`;
        } catch (error) {
            console.error('Error fetching data:', error);
            // 예외가 발생하면 기본 이미지를 반환
            return `https://d2gd6pc034wcta.cloudfront.net/tier/0.svg`;
        }
    }
    
}

$(document).ready(() => {

    $('body').append('<button id="chat-button">💬</button>');

    $(document).on("click", "#new-message-alert", function () {
        $("#chatting").animate({ scrollTop: $("#chatting")[0].scrollHeight }, 300);
        $(this).fadeOut(200);
    });

    // 채팅방 HTML 구조
    const chatHtml = `
        <div id="chat-app" class="chat-modal" style="display: none;">
            <div class="chat-header">
                <h3><span id="current-problem">0</span>번 문제를 풀고있는 사람 (<span id="current-user-count">0</span>명)</h3>
                <button id="close-chat"> X </button>
            </div>
            
            <div id="chatting" class="chat-container"></div>

            <div id="user-info">
                <div id="new-message-alert"></div>  
                <span class="centered-text">+사용자:&nbsp;<span id="current-user-name"></span>&nbsp;<span id="current-user-id"></span></span>
            </div>

            <form id="chat-form">
                <input id="message" type="text" placeholder="메시지를 입력하세요" required>
                <button id="send" type="button">전송</button>
            </form>
        </div>
    `;

    // 채팅방 HTML을 body에 추가
    $('body').append(chatHtml);


    const url = window.location.href;
    const problemId = url.match(/problem\/(\d+)/); // URL에서 문제 번호 추출
    if (problemId) {
        // 문제 번호가 있을 경우 current-problem에 삽입
        $('#current-problem').text(problemId[1]);
    }


    // CSS 스타일이 적용되도록 하기
    const style = `
        #chat-button {
            position: fixed;
            bottom: 80px;
            right: 20px;
            width: 60px;
            height: 60px;
            border: none;
            background: #007BFF;
            color: white;
            border-radius: 50px !important;
            overflow: hidden;
            cursor: pointer;
            z-index: 1100;
            box-shadow: 4px 6px 10px rgba(0,0,0,0.2);
            font-size: 24px;
            display: flex;
            align-items: center;
            justify-content: center;
            transition: background 0.3s, transform 0.2s;
        }

        #chat-button:hover {
            background: #0056b3;
            transform: scale(1.05);
        }

        /* 채팅방 스타일 */
        .chat-modal {
            position: fixed;
            bottom: 80px; /* 버튼 높이와 맞추기 */
            right: 20px;
            width: 320px;
            background-color: #f9f9f9;
            border-radius: 12px !important;
            box-shadow: 2px 4px 10px rgba(0,0,0,0.3);
            z-index: 1000;
            display: flex;
            flex-direction: column;
            font-family: Arial, sans-serif;
            overflow: hidden;
            max-height: calc(100vh - 100px); /* 버튼 높이 + 여백 */
        }

        /* 채팅방 헤더 */
        .chat-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            background-color: #e0e0e0; /* 원래 색상으로 복구 */
            color: black;
            padding: 8px 12px; 
            border-top-left-radius: 12px !important;
            border-top-right-radius: 12px !important;
        }

        /* 닫기 버튼 */
        #close-chat {
            background: none;
            border: none;
            font-size: 28px;
            cursor: pointer;
            color: black;
            font-weight: bold;
        }

        #close-chat:hover {
            opacity: 0.7;
        }


        /* 채팅 메시지 영역 */
        .chat-container {
            overflow-y: auto;  /* 스크롤 가능하도록 추가 */
            flex-grow: 1;
            padding: 10px;
            background-color: #fff;
            max-height: 300px; /* 필요에 따라 높이 조절 가능 */
        }
        

        /* 스크롤 바 스타일 (더 깔끔하게) */
        .chat-container::-webkit-scrollbar {
            width: 6px;
        }

        .chat-container::-webkit-scrollbar-thumb {
            background-color: rgba(0, 0, 0, 0.2);
            border-radius: 3px !important;
        }

        .chat-container::-webkit-scrollbar-track {
            background: transparent;
        }

        /* 메시지 텍스트 박스 */
        .chat .textbox {
            border-radius: 6px !important;
            font-size: 13px;
            color: black; /* 글씨 색상을 검은색으로 변경 */
            word-wrap: break-word; /* 긴 단어가 자동으로 개행되도록 설정 */
            overflow-wrap: break-word; /* 긴 단어가 자동으로 개행되도록 설정 */
            max-width: 70%; /* 최대 너비 제한 */
            white-space: normal; /* 텍스트가 적절히 개행되도록 설정 */
            word-break: break-word; /* 긴 단어가 텍스트 박스를 넘지 않도록 줄바꿈 처리 */
            overflow: hidden; /* 넘치는 텍스트는 보이지 않도록 처리 */
            margin-bottom: 4px;
        }

        .chat .message-info {
            padding: 0px; /* 내부 여백 추가 */
            margin-bottom: 15px; /* 아래쪽 여백 추가 */
            margin-top: 15px
            border-radius: 8px; /* 둥글게 만들어서 깔끔하게 */
            background-color: rgba(255, 255, 255, 0.1); /* 살짝 배경색 추가 (투명도 조절 가능) */
        }

        .chat .message-info .extra-info {
            font-size: 12px;
        }

        .chat .message-info .sender {
            font-weight: bold;  /* 글자 굵게 설정 */
        }

        .tier-image {
            width: 16px;  /* 아이콘 크기 조절 */
            height: 16px;
            vertical-align: middle; /* 텍스트와 정렬 */
            margin-right: 5px; /* sender와 간격 추가 */
        }


        /* 왼쪽 정렬 채팅 */
        .ch1 {
            justify-content: flex-start; /* 왼쪽 정렬 */
            text-align: left;
        }

        /* 오른쪽 정렬 채팅 */
        .ch2 {
            justify-content: flex-end; /* 오른쪽 정렬 */
            text-align: right;
        }

        /* 왼쪽 채팅 박스 */
        .ch1 .textbox {
            margin-left: 0; /* 왼쪽 여백 제거 */
            margin-right: auto; /* 오른쪽으로 밀어내기 */
            max-width: 65%; /* 왼쪽 채팅 박스 최대 70% */
        }

        /* 오른쪽 채팅 박스 */
        .ch2 .textbox {
            margin-left: auto; /* 왼쪽으로 밀어내기 */
            margin-right: 0; /* 오른쪽 여백 제거 */
            max-width: 65%; /* 오른쪽 채팅 박스 최대 70% */
        }

        /* 사용자 정보 */
        #user-info {
            text-align: center;
            font-size: 17px;
            margin-top: 0px;
            margin-bottom: 10px;
            color: #bbb;
            position: relative; /* 자식 요소인 #new-message-alert의 기준이 되도록 설정 */
        }

        .centered-text {
            display: inline-flex;
            justify-content: center;
            width: 100%;
            text-align: center;
        }


        /* 새 메시지 버튼 - 채팅창 내부에 반투명하게 표시 */
        #new-message-alert {
            position: absolute;  /* 부모 요소인 #user-info 기준으로 고정 */
            font-size: 12px;     /* 원하는 폰트 크기로 조정 (예: 14px) */
            bottom: 50px;        /* 채팅창 하단에서 10px 위 */
            left: 50%;           /* 중앙 정렬 */
            transform: translateX(-50%); /* X축 중앙 정렬 */
            background-color: rgba(0, 0, 0, 0.8); /* 반투명 배경 */
            color: white;
            padding: 10px 20px;
            border-radius: 10px !important;
            font-weight: bold;
            display: none; /* 기본적으로 숨김 */
            align-items: center;
            justify-content: center;
            cursor: pointer;
            z-index: 10; /* 채팅 메시지 위에 배치 */
        }

         /* 버튼 클릭 시 살짝 눌리는 효과 */
        #new-message-alert:active {
            transform: translateX(-50%) scale(0.95);
        }


        /* 채팅 입력창 */
        #chat-form {
            display: flex;
            padding: 10px;
        }

        #message {
            flex: 1;
            padding: 8px;
            border: 1px solid #ccc;
            border-radius: 4px !important;
            margin-right: 5px;
        }

        #send {
            background-color:rgb(93, 125, 158);
            color: white;
            border: none;
            border-radius: 4px !important;
            padding: 10px 15px;
            cursor: pointer;
            transition: background-color 0.3s;
        }

        #send:hover {
            background-color:rgb(83, 111, 139);
        }
    `;

    // CSS 스타일을 페이지에 추가
    $('<style>').text(style).appendTo('head');


    // 페이지 로드 시 채팅창을 최하단으로 설정
    setTimeout(function() {
        $("#chatting").scrollTop($("#chatting")[0].scrollHeight);
    }, 0);  // DOM이 완전히 로드된 후 스크롤 최하단 설정



    // 채팅 열고 닫기 기능 추가
    $('#chat-button').on('click', function () {
        // 채팅창을 숨겨진 상태(렌더링되지만 보이지 않음)로 설정
        $('#chat-app').css({ display: 'block', opacity: 0 });
        
        // 채팅창이 렌더링된 상태에서 스크롤을 최하단으로 설정
        $("#chatting").scrollTop($("#chatting")[0].scrollHeight);
        
        // opacity 애니메이션을 통해 fade-in 효과로 나타나게 함
        $('#chat-app').animate({ opacity: 1 }, 200);
        $('#chat-button').hide();
    });

    $('#close-chat').on('click', function () {
        $('#chat-app').fadeOut(200);
        $('#chat-button').fadeIn(200);
    }); 

    const app = new ChatApp(problemId[1]);
    app.init();
});



