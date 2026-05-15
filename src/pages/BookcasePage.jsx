// src/pages/BookcasePage.jsx
import { useNavigate, Link } from "react-router-dom";
import { useAuth } from "../hooks/useAuth";
import { useBookcase } from "../hooks/useBookcase";
import "../styles/BookcasePage.css";

const BOOK_COLORS = [
    "#534AB7", "#3B6D11", "#993C1D", "#185FA5", "#854F0B",
    "#993556", "#0F6E56", "#5F5E5A", "#A32D2D", "#3C3489"
];

function getBookColor(index) {
    return BOOK_COLORS[index % BOOK_COLORS.length];
}

function StatusBadge({ status }) {
    const labels = {
        WANT_TO_READ: "Quero ler",
        READING:      "Lendo",
        FINISHED:     "Lido"
    };

    return (
        <span className={`status-badge status-${status.toLowerCase()}`}>
            {labels[status]}
        </span>
    );
}

function BookCard({ entry, index, onRemove, onStatusChange }) {
    const color = getBookColor(index);

    return (
        <div className="bookcase-book-wrapper">

            {/* Espinha do livro — efeito estante */}
            <div
                className="bookcase-spine"
                style={{ background: color }}
                title={entry.book.title}
            >
                <span className="spine-title">{entry.book.title}</span>
            </div>

            {/* Card de detalhes — aparece no hover */}
            <div className="bookcase-detail">
                <h3>{entry.book.title}</h3>
                <p className="detail-author">{entry.book.authorName}</p>

                <StatusBadge status={entry.readingStatus} />

                <select
                    className="status-select"
                    value={entry.readingStatus}
                    onChange={e => onStatusChange(entry.id, e.target.value)}
                >
                    <option value="WANT_TO_READ">Quero ler</option>
                    <option value="READING">Lendo</option>
                    <option value="FINISHED">Lido</option>
                </select>

                {entry.review && (
                    <p className="detail-review">"{entry.review}"</p>
                )}

                <button
                    className="remove-btn"
                    onClick={() => onRemove(entry.book.id)}
                >
                    Remover
                </button>
            </div>

        </div>
    );
}

export function BookcasePage() {
    const { user, logout } = useAuth();
    const { bookcase, loading, error, removeBook } = useBookcase();
    const navigate = useNavigate();

    async function handleLogout() {
        await logout();
        navigate("/login");
    }

    // Status change será implementado quando o endpoint existir no back-end
    function handleStatusChange(entryId, newStatus) {
        console.log("Mudar status:", entryId, newStatus);
    }

    return (
        <div className="bookcase-container">

            <header className="home-header">
                <h1>Eltons' Books</h1>
                <nav className="home-nav">
                    <Link to="/search">Search nooks</Link>
                    <Link to="/bookcase">My bookcase</Link>
                    <button onClick={handleLogout} className="logout-btn">
                        Logout
                    </button>
                </nav>
            </header>

            <main className="bookcase-main">

                <div className="bookcase-top">
                    <h2>Minha estante</h2>
                    <p>{bookcase.length} {bookcase.length === 1 ? "livro" : "livros"}</p>
                </div>

                {loading && <p className="bookcase-feedback">Loading bookcase...</p>}
                {error   && <p className="bookcase-feedback error">{error}</p>}

                {!loading && !error && bookcase.length === 0 && (
                    <div className="bookcase-empty">
                        <p>Sua estante está vazia.</p>
                        <Link to="/search">Buscar livros para adicionar</Link>
                    </div>
                )}

                {!loading && bookcase.length > 0 && (
                    <div className="bookcase-shelf-wrapper">
                        <div className="bookcase-shelf">
                            {bookcase.map((entry, index) => (
                                <BookCard
                                    key={entry.id}
                                    entry={entry}
                                    index={index}
                                    onRemove={removeBook}
                                    onStatusChange={handleStatusChange}
                                />
                            ))}
                        </div>
                        <div className="shelf-floor" />
                    </div>
                )}

            </main>

        </div>
    );
}