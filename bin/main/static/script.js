document.addEventListener('DOMContentLoaded', () => {

  /* --------------------------------- */
  /* ----- Theme Switching Logic -----*/
  /* --------------------------------- */
  const switchThemeEl = document.querySelector('input[type="checkbox"]#theme-switch');
  if (switchThemeEl) {
    const storedTheme = localStorage.getItem("theme");
    switchThemeEl.checked = (storedTheme === "dark" || storedTheme === null);
    switchThemeEl.addEventListener("click", () => {
      if (switchThemeEl.checked) {
        document.body.classList.add("dark");
        document.body.classList.remove("light");
        localStorage.setItem("theme", "dark");
      } else {
        document.body.classList.remove("dark");
        document.body.classList.add("light");
        localStorage.setItem("theme", "light");
      }
    });
  }

  /* --------------------------------- */
  /* ----- Footer Year Logic -----*/
  /* --------------------------------- */
  const yearEl = document.querySelector(".footer-text span");
  if (yearEl) {
    yearEl.textContent = new Date().getFullYear();
  }

  /* --------------------------------- */
  /* ----- Tarot AI Chat Logic -----*/
  /* --------------------------------- */
  const chatToggleButton = document.getElementById('tarot-chat-toggle');
  const chatCloseButton = document.getElementById('tarot-chat-close');
  const chatResetButton = document.getElementById('tarot-chat-reset'); // Nút mới
  const chatWindow = document.getElementById('tarot-chat-window');
  const chatForm = document.getElementById('tarot-chat-form');
  const chatInput = document.getElementById('tarot-chat-input');
  const messagesContainer = document.getElementById('tarot-chat-messages');

  if (chatToggleButton && chatCloseButton && chatResetButton && chatWindow && chatForm && chatInput && messagesContainer) {
    const toggleChatWindow = () => {
      chatWindow.classList.toggle('hidden');
    };

    chatToggleButton.addEventListener('click', toggleChatWindow);
    chatCloseButton.addEventListener('click', toggleChatWindow);

    const addMessage = (text, sender) => {
      const messageDiv = document.createElement('div');
      messageDiv.className = `chat-message ${sender}-message`;
      const messageP = document.createElement('p');
      messageP.innerHTML = text;
      messageDiv.appendChild(messageP);
      messagesContainer.appendChild(messageDiv);
      messagesContainer.scrollTop = messagesContainer.scrollHeight;
    };

    const showTypingIndicator = () => { /* Giữ nguyên hàm này */ };
    const hideTypingIndicator = () => { /* Giữ nguyên hàm này */ };

    // ---- BẮT ĐẦU LOGIC NÚT RESET MỚI ----
    const handleResetChat = () => {
      fetch('/api/tarot-chat/reset', {
        method: 'POST'
      })
          .then(response => response.json())
          .then(data => {
            // Xóa tất cả tin nhắn cũ trên giao diện
            messagesContainer.innerHTML = '';
            // Thêm tin nhắn chào mừng mới từ server
            addMessage(data.response, 'ai');
          })
          .catch(error => {
            console.error('Lỗi khi reset chat:', error);
            addMessage('Đã có lỗi xảy ra, vui lòng thử lại.', 'ai');
          });
    };

    chatResetButton.addEventListener('click', handleResetChat);
    // ---- KẾT THÚC LOGIC NÚT RESET MỚI ----

    chatForm.addEventListener('submit', (event) => {
      event.preventDefault();
      const userMessage = chatInput.value.trim();
      if (userMessage === '') return;

      addMessage(userMessage, 'user');
      chatInput.value = '';
      showTypingIndicator();

      fetch('/api/tarot-chat', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ message: userMessage }),
      })
          .then(response => {
            if (!response.ok) throw new Error('Network response was not ok');
            return response.json();
          })
          .then(data => {
            hideTypingIndicator();
            const formattedResponse = data.response.replace(/\n/g, '<br>').replace(/\*\*(.*?)\*\*/g, '<strong>$1</strong>');
            addMessage(formattedResponse, 'ai');
          })
          .catch(error => {
            console.error('Lỗi khi gọi API Tarot:', error);
            hideTypingIndicator();
            addMessage('Xin lỗi, có một chút nhiễu loạn năng lượng khiến tôi không thể kết nối. Vui lòng thử lại sau.', 'ai');
          });
    });
  }
});