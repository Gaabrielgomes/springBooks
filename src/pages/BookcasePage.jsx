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
        WANT_TO_READ: "Want to Read",
        READING:      "Reading",
        FINISHED:     "Finished"
    };

    return (
        <span className={`status-badge status-${status.toLowerCase()}`}>
            {labels[status]}
        </span>
    );
}

function BookCard({ entry, index, onRemove, onStatusChange }) {
    const fallbackColor = getBookColor(index);
    const hasCover = entry.book.coverLink &&
                     entry.book.coverLink !== "No cover link found.";

    return (
        <div className="bookcase-book-wrapper">

            <div
                className="bookcase-spine"
                style={hasCover
                    ? { backgroundImage: `url(${entry.book.coverLink})`,
                        backgroundSize: "cover",
                        backgroundPosition: "center" }
                    : { background: fallbackColor }
                }
                title={entry.book.title}
            >
                {!hasCover && (
                    <span className="spine-title">{entry.book.title}</span>
                )}
            </div>

            <div className="bookcase-detail">
                <h3>{entry.book.title}</h3>
                <p className="detail-author">{entry.book.authorName}</p>

                <StatusBadge status={entry.readingStatus} />

                <select
                    className="status-select"
                    value={entry.readingStatus}
                    onChange={e => onStatusChange(entry.id, e.target.value)}
                >
                    <option value="WANT_TO_READ">Want to Read</option>
                    <option value="READING">Reading</option>
                    <option value="FINISHED">Finished</option>
                </select>

                {entry.review && (
                    <p className="detail-review">"{entry.review}"</p>
                )}

                <button
                    className="remove-btn"
                    onClick={() => onRemove(entry.book.id)}
                >
                    Remove
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

    function handleStatusChange(entryId, newStatus) {
        console.log("Change status:", entryId, newStatus);
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
                    <h2>My bookcase</h2>
                    <p>{bookcase.length} {bookcase.length === 1 ? "book" : "books"}</p>
                </div>

                {loading && <p className="bookcase-feedback">Loading...</p>}
                {error   && <p className="bookcase-feedback error">{error}</p>}

                {!loading && !error && bookcase.length === 0 && (
                    <div className="bookcase-empty">
                        <p>Your bookcase is empty.</p>
                        <Link to="/search">Search for books to add</Link>
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