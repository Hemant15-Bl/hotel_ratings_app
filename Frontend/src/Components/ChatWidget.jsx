import React, { useState } from 'react'
import { askChatbot } from '../services/Chatbot-service';

const ChatWidget = () => {

  const [open, setOpen] = useState(false);
  const [input, setInput] = useState("");
  const [loading, setLoading] = useState(false);
  const [messages, setMessages] = useState([
    { role: "bot", text: "Hi! Ask me about hotels, ratings and reviews." },
  ]);

  const send = async () => {
    const text = input.trim();
    if (!text || loading) return;
    setMessages((m) => [...m, { role: "user", text }]);
    setInput("");
    setLoading(true);
    try {
      const answer = await askChatbot(text);
      setMessages((m) => [...m, { role: "bot", text: answer }]);
    } catch (e) {
      const status = e.response?.status;
      const msg =
        status === 401 ? "Please log in to use the assistant."
        : status === 429 ? "Too many questions, please wait a moment."
        : "Sorry, something went wrong. Please try again.";
      setMessages((m) => [...m, { role: "bot", text: msg }]);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={{ position: "fixed", bottom: 20, right: 20, zIndex: 1000 }}>
      {open && (
        <div style={{ width: 340, height: 440, background: "#fff", border: "1px solid #ddd",
                      borderRadius: 12, display: "flex", flexDirection: "column",
                      boxShadow: "0 4px 16px rgba(0,0,0,.2)", marginBottom: 10 }}>
          <div style={{ flex: 1, overflowY: "auto", padding: 12 }}>
            {messages.map((m, i) => (
              <div key={i} style={{ textAlign: m.role === "user" ? "right" : "left", margin: "6px 0" }}>
                <span style={{ display: "inline-block", padding: "8px 12px", borderRadius: 10,
                               whiteSpace: "pre-wrap",
                               background: m.role === "user" ? "#0d6efd" : "#f1f1f1",
                               color: m.role === "user" ? "#fff" : "#000" }}>
                  {m.text}
                </span>
              </div>
            ))}
            {loading && <div style={{ color: "#888" }}>Thinking...</div>}
          </div>
          <div style={{ display: "flex", borderTop: "1px solid #eee" }}>
            <input value={input} onChange={(e) => setInput(e.target.value)}
                   onKeyDown={(e) => e.key === "Enter" && send()}
                   placeholder="Ask about a hotel..."
                   style={{ flex: 1, padding: 10, border: "none", outline: "none" }} />
            <button onClick={send} disabled={loading}>Send</button>
          </div>
        </div>
      )}
      <button onClick={() => setOpen(!open)}
              style={{ width: 56, height: 56, borderRadius: "50%", border: "none",
                       background: "#0d6efd", color: "#fff", fontSize: 24, cursor: "pointer" }}>
        💬
      </button>
    </div>
  );

}

export default ChatWidget;