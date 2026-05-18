// src/pages/BookPage.jsx
import { useEffect, useState } from "react";
import { useParams, useNavigate, Link } from "react-router-dom";
import { useAuth } from "../hooks/useAuth";
import { useBookcase } from "../hooks/useBookcase";
import "../styles/BookPage.css";

const STATUS_LABELS = {
    WANT_TO_READ: "Quero ler",
    READING:      "Lendo",
    FINISHED:     "Lido"
};

export function BookPage() {
    const { entryId } = useParams();
    const { logout } = useAuth();
    const { bookcase, removeBook, loading } = useBookcase();
    const navigate = useNavigate();

    const [review, setReview]         = useState("");
    const [editingReview, setEditing] = useState(false);
    const [feedback, setFeedback]     = useState(null);

    const entry = bookcase.find(e => String(e.id) === entryId);

    useEffect(() => {
        if (!loading && !entry) {
            navigate("/bookcase");
        }
    }, [entry, loading]);

    useEffect(() => {
        if (entry?.review) {
            setReview(entry.review);
        }
    }, [entry]);

    async function handleRemove() {
        if (!window.confirm(`Remover "${entry.book.title}" da sua estante?`)) return;
        await removeBook(entry.book.id);
        navigate("/bookcase");
    }

    async function handleSaveReview() {
        try {
            await fetch(`http://localhost:8889/user/bookcase/addreview/${entry.book.id}`, {
                method: "PUT",
                headers: { "Content-Type": "application/json" },
                credentials: "include",
                body: JSON.stringify(review)
            });
            setEditing(false);
            setFeedback({ type: "success", text: "Resenha salva!" });
        } catch {
            setFeedback({ type: "error", text: "Erro ao salvar resenha." });
        }
    }

    async function handleLogout() {
        await logout();
        navigate("/login");
    }

    if (loading || !entry) return null;

    const hasCover = entry.book.coverLink &&
                     entry.book.coverLink !== "No cover link found.";

    return (
        <div className="bookpage-container">

            <header className="home-header">
                <h1>Elton's Books</h1>
                <nav className="home-nav">
                    <Link to="/search">Buscar livros</Link>
                    <Link to="/bookcase">Minha estante</Link>
                    <button onClick={handleLogout} className="logout-btn">Sair</button>
                </nav>
            </header>

            <main className="bookpage-main">

                <Link to="/bookcase" className="bookpage-back">
                    ← Voltar para a estante
                </Link>

                <div className="bookpage-content">

                    {/* Capa */}
                    <div className="bookpage-cover-wrapper">
                        {hasCover
                            ? <img
                                src={entry.book.coverLink}
                                alt={entry.book.title}
                                className="bookpage-cover"
                              />
                            : <div className="bookpage-cover-fallback">
                                <span>{entry.book.title}</span>
                              </div>
                        }
                    </div>

                    {/* Informações */}
                    <div className="bookpage-info">

                        <h2>{entry.book.title}</h2>
                        <p className="bookpage-author">{entry.book.authorName}</p>

                        <div className="bookpage-meta">
                            {entry.book.publishedDate && (
                                <span>{new Date(entry.book.publishedDate).getFullYear()}</span>
                            )}
                            {entry.book.pagesNumber > 1 && (
                                <span>{entry.book.pagesNumber} páginas</span>
                            )}
                            <span className={`status-badge status-${entry.readingStatus.toLowerCase()}`}>
                                {STATUS_LABELS[entry.readingStatus]}
                            </span>
                        </div>

                        {entry.book.description && (
                            <p className="bookpage-desc">{entry.book.description}</p>
                        )}

                        {/* Resenha */}
                        <div className="bookpage-review-section">
                            <h3>Minha resenha</h3>

                            {editingReview ? (
                                <div className="bookpage-review-edit">
                                    <textarea
                                        value={review}
                                        onChange={e => setReview(e.target.value)}
                                        rows={5}
                                        placeholder="Escreva sua resenha..."
                                    />
                                    <div className="bookpage-review-actions">
                                        <button
                                            className="save-review-btn"
                                            onClick={handleSaveReview}
                                        >
                                            Salvar resenha
                                        </button>
                                        <button
                                            className="cancel-btn"
                                            onClick={() => setEditing(false)}
                                        >
                                            Cancelar
                                        </button>
                                    </div>
                                </div>
                            ) : (
                                <div className="bookpage-review-display">
                                    {entry.review
                                        ? <p className="review-text">"{entry.review}"</p>
                                        : <p className="review-empty">Nenhuma resenha ainda.</p>
                                    }
                                    <button
                                        className="edit-review-btn"
                                        onClick={() => setEditing(true)}
                                    >
                                        {entry.review ? "Editar resenha" : "Escrever resenha"}
                                    </button>
                                </div>
                            )}

                            {feedback && (
                                <p className={`review-feedback ${feedback.type}`}>
                                    {feedback.text}
                                </p>
                            )}
                        </div>

                        {/* Ações */}
                        <div className="bookpage-actions">
                            <button className="remove-book-btn" onClick={handleRemove}>
                                Remover da estante
                            </button>
                        </div>

                    </div>
                </div>
            </main>
        </div>
    );
}