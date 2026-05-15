export function SearchPage() {
    const { logout } = useAuth();
    const { results, loading, error, searchByTitle, searchByAuthor, save } = useSearch();
    const { addBook } = useBookcase();
    const navigate = useNavigate();

    const [query, setQuery]   = useState("");
    const [mode, setMode]     = useState("title");
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
                <h1>Eltons' Books</h1>
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