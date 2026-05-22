import { useAuth } from "../hooks/useAuth";
import { useNavigate, Link } from "react-router-dom";
import "../styles/HomePage.css";

export function HomePage() {
    const { user, logout } = useAuth();
    const navigate = useNavigate();

    async function handleLogout() {
        await logout();
        navigate("/login");
    }

    return (
        <div className="home-container">

            <header className="home-header">
                <h1>Elton's Books</h1>
                <nav className="home-nav">
                    <Link to="/">Me</Link>
                    <Link to="/bookcase">My Bookcase</Link>
                    <button onClick={handleLogout} className="logout-btn">
                        Logout
                    </button>
                </nav>
            </header>

            <main className="home-main">
                <section className="home-welcome">
                    <h2>Hello, {user?.name} 👋</h2>
                    <p>What are you going to read today?</p>
                </section>

                <section className="home-cards">

                    <div
                        className="home-card"
                        onClick={() => navigate("/search")}
                    >
                        <div className="home-card-icon">🔍</div>
                        <h3>Search books</h3>
                        <p>Find new books by title or author and add them to the central bookcase.</p>
                    </div>

                    <div
                        className="home-card"
                        onClick={() => navigate("/mainbookcase")}
                    >
                        <div className="home-card-icon">📚</div>
                        <h3>Main bookcase</h3>
                        <p>See the books saved in our main bookcase so you can add any that interest you to yours.</p>
                    </div>

                    <div
                        className="home-card"
                        onClick={() => navigate("/bookcase")}
                    >
                        <div className="home-card-icon">📖</div>
                        <h3>My bookcase</h3>
                        <p>See the books addded by you and manage your reading list.</p>
                    </div>

                </section>
            </main>

        </div>
    );
}