import { useNavigate, Link } from "react-router-dom";
import { useAuth } from "../hooks/useAuth";
import { useBookcase } from "../hooks/useBookcase";
import "../styles/BookcasePage.css";

import bookcaseTopImg from "../assets/bookcaseTop.jpg";
import bookcaseBottomImg from "../assets/bookcaseBottom.jpg";

const BOOK_COLORS = [
    "#534AB7", "#3B6D11", "#993C1D", "#185FA5", "#854F0B",
    "#993556", "#0F6E56", "#5F5E5A", "#A32D2D", "#3C3489"
];

function getBookColor(index) {
    return BOOK_COLORS[index % BOOK_COLORS.length];
}

function BookSpine({ entry, index, onClick }) {
    const hasCover = entry.book.coverLink &&
        entry.book.coverLink !== "No cover link found.";

    return (
        <div
            className="bookcase-book-wrapper"
            onClick={() => onClick(entry.id)}
            title={entry.book.title}
        >
            <div
                className="bookcase-spine"
                style={hasCover
                    ? { backgroundImage: `url(${entry.book.coverLink})`,
                        backgroundSize: "cover",
                        backgroundPosition: "center" }
                    : { background: getBookColor(index) }
                }
            >
                {!hasCover && (
                    <span className="spine-title">{entry.book.title}</span>
                )}
            </div>
        </div>
    );
}

export function BookcasePage() {
    const { logout } = useAuth();
    const { bookcase, loading, error, updateStatus } = useBookcase();
    const navigate = useNavigate();

    async function handleLogout() {
        await logout();
        navigate("/login");
    }

    function handleBookClick(entryId) {
        navigate(`/bookcase/showbook/${entryId}`);
    }

    const SHELF_MAX_WIDTH = 1020;
    const BOOK_EFFECTIVE_WIDTH = 62;

    const booksPerShelf = 14;

    const shelves = [];
    for (let i = 0; i < bookcase.length; i += booksPerShelf) {
        shelves.push(bookcase.slice(i, i + booksPerShelf));
    }

    return (
        <div className="bookcase-container">
            <header className="home-header">
                <h1><Link to="/home">Eltons' Books</Link></h1>
                <nav className="home-nav">
                    <Link to="/home">Home</Link>
                    <Link to="/search">Search Books</Link>
                    <Link to="/bookcase">My Bookcase</Link>
                    <button onClick={handleLogout} className="logout-btn">
                        Logout
                    </button>
                </nav>
            </header>

            <main className="bookcase-main">
                <div className="bookcase-top">
                    <h2>My Bookcase</h2>
                    <p>{bookcase.length} {bookcase.length === 1 ? "book" : "books"}</p>
                </div>

                {loading && <p className="bookcase-feedback">Loading your bookcase...</p>}
                {error   && <p className="bookcase-feedback error">{error}</p>}

                {!loading && !error && bookcase.length === 0 && (
                    <div className="bookcase-empty">
                        <p>Your bookcase is empty.</p>
                        <Link to="/search">Search for books to add</Link>
                    </div>
                )}

                {/* Dynamic renderization for shelves */}
                {!loading && shelves.length > 0 && (
                    <div className="bookcase-multishelf-container">
                        {shelves.map((shelfBooks, shelfIndex) => {
                            const isTopShelf = shelfIndex === 0;
                            const backgroundImage = isTopShelf
                                ? `url(${bookcaseTopImg})`
                                : `url(${bookcaseBottomImg})`;

                            return (
                                <div key={shelfIndex} className="bookcase-shelf-wrapper">
                                    <div
                                        className="bookcase-shelf"
                                        style={{ backgroundImage: backgroundImage }}
                                    >
                                        {shelfBooks.map((entry, index) => (
                                            <BookSpine
                                                key={entry.id}
                                                index={shelfIndex * booksPerShelf + index}
                                                entry={entry}
                                                onClick={handleBookClick}
                                            />
                                        ))}
                                    </div>
                                </div>
                            );
                        })}
                    </div>
                )}
            </main>
        </div>
    );
}