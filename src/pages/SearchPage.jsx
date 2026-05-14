import { useState } from "react";
import { useNavigate, Link } from "react-router-dom";
import { useAuth } from "../hooks/useAuth";
import { useSearch } from "../hooks/useSearch";
import { useBookcase } from "../hooks/useBookcase";
import "../styles/SearchPage.css";

const BOOK_COLORS = [
    "#534AB7", "#3B6D11", "#993C1D", "#185FA5", "#854F0B",
    "#993556", "#0F6E56", "#5F5E5A", "#A32D2D", "#3C3489"
];

function getBookColor(index) {
    return BOOK_COLORS[index % BOOK_COLORS.length];
}

function BookResult({ book, index, onSaveAndAdd }) {
    const hasCover = book.coverLink &&
                     book.coverLink !== "No cover link found.";

    return (
        <div className="book-result-card">

            <div
                className="book-result-cover"
                style={hasCover
                    ? { backgroundImage: `url(${book.coverLink})`,
                        backgroundSize: "cover",
                        backgroundPosition: "center" }
                    : { background: getBookColor(index) }
                }
            >
                {!hasCover && (
                    <span className="book-result-cover-title">{book.title}</span>
                )}
            </div>

            <div className="book-result-info">
                <h3>{book.title}</h3>
                <p className="book-result-author">{book.authorName}</p>
                <p className="book-result-desc">{book.description}</p>

                <div className="book-result-meta">
                    {book.publishedDate && (
                        <span>{new Date(book.publishedDate).getFullYear()}</span>
                    )}
                    {book.pagesNumber && book.pagesNumber > 1 && (
                        <span>{book.pagesNumber} pages</span>
                    )}
                </div>

                <button
                    className="add-btn"
                    onClick={() => onSaveAndAdd(book)}
                >
                    + Add to Bookcase
                </button>
            </div>

        </div>
    );
}

export function SearchPage() {
    const { logout } = useAuth();
    const { results, loading, error, searchByTitle, searchByAuthor, save } = useSearch();
    const { addBook } = useBookcase();
    const navigate = useNavigate();

    const [query, setQuery]   = useState("");
    const [mode, setMode]     = useState("title"); // "title" | "author"
    const [feedback, setFeedback] = useState(null);

    async function handleSearch(e) {
        e.preventDefault();
        if (!query.trim()) return;
        setFeedback(null);

        if (mode === "title") {
            await searchByTitle(query.trim());
        } else {
            await searchByAuthor(query.trim());
        }
    }

    async function handleSaveAndAdd(book) {
        try {
            const saved = await save(book);

            await addBook(saved.id);

            setFeedback({ type: "success", text: `"${book.title}" added to your bookcase.` });
        } catch (err) {
            setFeedback({ type: "error", text: err.message });
        }
    }

    async function handleLogout() {
        await logout();
        navigate("/login");
    }

    return (
        <div className="search-container">

            <header className="home-header">
                <h1>Elton's Books</h1>
                <nav className="home-nav">
                    <Link to="/search">Search Books</Link>
                    <Link to="/bookcase">My Bookcase</Link>
                    <button onClick={handleLogout} className="logout-btn">
                        Logout
                    </button>
                </nav>
            </header>

            <main className="search-main">

                <div className="search-top">
                    <h2>Search Books</h2>
                </div>

                <form onSubmit={handleSearch} className="search-form">

                    <div className="search-mode">
                        <button
                            type="button"
                            className={`mode-btn ${mode === "title" ? "active" : ""}`}
                            onClick={() => setMode("title")}
                        >
                            By Title
                        </button>
                        <button
                            type="button"
                            className={`mode-btn ${mode === "author" ? "active" : ""}`}
                            onClick={() => setMode("author")}
                        >
                            By Author
                        </button>
                    </div>

                    <div className="search-input-row">
                        <input
                            type="text"
                            value={query}
                            onChange={e => setQuery(e.target.value)}
                            placeholder={mode === "title"
                                ? "Enter the book title..."
                                : "Enter the author name..."
                            }
                            autoFocus
                        />
                        <button type="submit" className="search-btn" disabled={loading}>
                            {loading ? "Searching..." : "Search"}
                        </button>
                    </div>

                </form>

                {feedback && (
                    <p className={`search-feedback ${feedback.type}`}>
                        {feedback.text}
                    </p>
                )}

                {error && (
                    <p className="search-feedback error">{error}</p>
                )}

                {!loading && results.length > 0 && (
                    <div className="search-results">
                        {results.map((book, index) => (
                            <BookResult
                                key={`${book.title}-${index}`}
                                book={book}
                                index={index}
                                onSaveAndAdd={handleSaveAndAdd}
                            />
                        ))}
                    </div>
                )}

                {!loading && results.length === 0 && query && (
                    <p className="search-empty">No results found.</p>
                )}

            </main>

        </div>
    );
}