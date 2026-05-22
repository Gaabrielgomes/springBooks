// src/pages/MainBookcasePage.jsx
import { useState, useEffect } from "react";
import { Link, useNavigate } from "react-router-dom";
import { useAuth } from "../hooks/useAuth";
import { getAllBooks } from "../services/api";
import "../styles/MainBookcasePage.css";

const BOOK_COLORS = [
    "#534AB7", "#3B6D11", "#993C1D", "#185FA5", "#854F0B",
    "#993556", "#0F6E56", "#5F5E5A", "#A32D2D", "#3C3489"
];

function getBookColor(index) {
    return BOOK_COLORS[index % BOOK_COLORS.length];
}

function BookCard({ book, index }) {
    const hasCover = book.coverLink &&
                     book.coverLink !== "No cover link found.";

    return (
        <div className="main-book-card">

            <div
                className="main-book-cover"
                style={hasCover
                    ? { backgroundImage: `url(${book.coverLink})`,
                        backgroundSize: "cover",
                        backgroundPosition: "center" }
                    : { background: getBookColor(index) }
                }
            >
                {!hasCover && (
                    <span className="main-book-cover-title">{book.title}</span>
                )}
            </div>

            <div className="main-book-info">
                <p className="main-book-title">{book.title}</p>
                <p className="main-book-author">{book.authorName}</p>
            </div>

        </div>
    );
}

export function MainBookcasePage() {
    const { logout } = useAuth();
    const navigate = useNavigate();

    const [books, setBooks]     = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError]     = useState(null);
    const [search, setSearch]   = useState("");

    useEffect(() => {
        getAllBooks()
            .then(setBooks)
            .catch(e => setError(e.message))
            .finally(() => setLoading(false));
    }, []);

    async function handleLogout() {
        await logout();
        navigate("/login");
    }

    const filtered = books.filter(book =>
        book.title.toLowerCase().includes(search.toLowerCase()) ||
        book.authorName.toLowerCase().includes(search.toLowerCase())
    );

    return (
        <div className="main-bookcase-container">

            <header className="home-header">
                <h1>Elton's Books</h1>
                <nav className="home-nav">
                    <Link to="/search">Search Books</Link>
                    <Link to="/mainbookcase">Main Bookcase</Link>
                    <Link to="/bookcase">My Bookcase</Link>
                    <button onClick={handleLogout} className="logout-btn">Logout</button>
                </nav>
            </header>

            <main className="main-bookcase-main">

                <div className="main-bookcase-top">
                    <div className="main-bookcase-top-text">
                        <h2>Main Bookcase</h2>
                        <p>{books.length} {books.length === 1 ? "book" : "books"} registered</p>
                    </div>

                    <input
                        type="text"
                        className="main-bookcase-search"
                        placeholder="Filter by title or author..."
                        value={search}
                        onChange={e => setSearch(e.target.value)}
                    />
                </div>

                {loading && <p className="main-bookcase-feedback">Loading...</p>}
                {error   && <p className="main-bookcase-feedback error">{error}</p>}

                {!loading && filtered.length === 0 && (
                    <p className="main-bookcase-feedback">No books found.</p>
                )}

                {!loading && filtered.length > 0 && (
                    <div className="main-bookcase-grid">
                        {filtered.map((book, index) => (
                            <BookCard
                                key={book.id ?? index}
                                book={book}
                                index={index}
                            />
                        ))}
                    </div>
                )}

            </main>

        </div>
    );
}