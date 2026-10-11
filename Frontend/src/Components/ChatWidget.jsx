import React, { useEffect, useState } from 'react'
import { askChatbot } from '../services/Chatbot-service';
import "./ChatWidget.css";

const SUGGESTIONS = [
  "Which hotel has the highest rating?",
  "Which hotels have good food?",
  "What do guests say about the staff?",
];

const WELCOME = {
  role: "bot",
  text: "Hi! I can help with hotels, ratings and reviews. What would you like to know?",
};

const IconChat = () => (
  <svg width="26" height="26" viewBox="0 0 24 24" fill="currentColor" aria-hidden="true">
    <path d="M4 3h16a2 2 0 0 1 2 2v11a2 2 0 0 1-2 2H9l-5 4v-4H4a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2z" />
  </svg>
);
const IconClose = () => (
  <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor"
       strokeWidth="2.4" strokeLinecap="round" aria-hidden="true">
    <path d="M6 6l12 12M18 6L6 18" />
  </svg>
);
const IconSend = () => (
  <svg width="20" height="20" viewBox="0 0 24 24" fill="currentColor" aria-hidden="true">
    <path d="M3 20.5v-7l9-1.5-9-1.5v-7l19 8.5-19 8.5z" />
  </svg>
);

const ChatWidget = () => {

  const [open, setOpen] = useState(false);
  const [input, setInput] = useState("");
  const [loading, setLoading] = useState(false);
  const [slow, setSlow] = useState(false);
  const [messages, setMessages] = useState([WELCOME]);

  const listRef = useRef(null);
  const inputRef = useRef(null);

  // Scroll the message list (not the page) to the newest message
  useEffect(() => {
    const el = listRef.current;
    if (el) el.scrollTo({ top: el.scrollHeight, behavior: "smooth" });
  }, [messages, loading, open]);

  // Focus the input when the panel opens
  useEffect(() => {
    if (open) inputRef.current?.focus();
  }, [open]);

  // Show a "still working" hint if the model takes a while
  useEffect(() => {
    if (!loading) {
      setSlow(false);
      return;
    }
    const t = setTimeout(() => setSlow(true), 8000);
    return () => clearTimeout(t);
  }, [loading]);

  // Close with Escape
  useEffect(() => {
    if (!open) return;
    const onKey = (e) => e.key === "Escape" && setOpen(false);
    window.addEventListener("keydown", onKey);
    return () => window.removeEventListener("keydown", onKey);
  }, [open]);


  const send = async () => {
    const text = (raw ?? input).trim();
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
        status === 401 ? "Your session has expired. Please log in again."
        : status === 429 ? "Too many questions at once. Please wait a moment."
        : e.code === "ECONNABORTED" ? "That took too long. Please try again."
        : "Sorry, something went wrong. Please try again.";
      setMessages((m) => [...m, { role: "bot", text: msg, error: true }]);
    } finally {
      setLoading(false);
    }
  };

  const onSubmit = (e) => {
    e.preventDefault();
    send();
  };

  return (
    <div className="chat-root">
      {open && (
        <section className="chat-panel" role="dialog" aria-label="Hotel assistant">
          <header className="chat-header">
            <div className="chat-avatar"><IconChat /></div>
            <div className="chat-title">
              <strong>Hotel Assistant</strong>
              <span>Ask about hotels &amp; ratings</span>
            </div>
            <button type="button" className="chat-icon-btn" onClick={() => setOpen(false)}
                    aria-label="Close chat">
              <IconClose />
            </button>
          </header>

          <div className="chat-messages" ref={listRef}>
            {messages.map((m, i) => (
              <div key={i} className={`chat-row ${m.role}`}>
                <div className={`chat-bubble ${m.role}${m.error ? " error" : ""}`}>{m.text}</div>
              </div>
            ))}

            {messages.length === 1 && !loading && (
              <div className="chat-chips">
                {SUGGESTIONS.map((s) => (
                  <button key={s} type="button" className="chat-chip" onClick={() => send(s)}>
                    {s}
                  </button>
                ))}
              </div>
            )}

            {loading && (
              <div className="chat-row bot">
                <div className="chat-bubble bot chat-typing" aria-label="Assistant is typing">
                  <span /><span /><span />
                </div>
              </div>
            )}
            {loading && slow && (
              <div className="chat-hint">Still working on it. This can take up to a minute.</div>
            )}
          </div>

          <form className="chat-footer" onSubmit={onSubmit}>
            <input
              ref={inputRef}
              value={input}
              onChange={(e) => setInput(e.target.value)}
              placeholder="Ask about a hotel..."
              maxLength={500}
              disabled={loading}
              aria-label="Your message"
            />
            <button type="submit" className="chat-send" disabled={loading || !input.trim()}
                    aria-label="Send message">
              <IconSend />
            </button>
          </form>
        </section>
      )}

      <button type="button" className="chat-launcher" onClick={() => setOpen((o) => !o)}
              aria-label={open ? "Close chat" : "Open chat"} aria-expanded={open}>
        {open ? <IconClose /> : <IconChat />}
      </button>
    </div>
  );

}

export default ChatWidget;