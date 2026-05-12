import { useState } from "react";
import { useNavigate, Link } from "react-router-dom";
import { useAuth } from "../hooks/useAuth";
import "../styles/LoginPage.css";

export function LoginPage() {
    const { login } = useAuth();
    const navigate = useNavigate();

    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const [error, setError]       = useState(null);
    const [loading, setLoading]   = useState(false);

    async function handleSubmit(e) {
        e.preventDefault();
        setError(null);
        setLoading(true);

        try {
            await login(username, password);
            navigate("/home");
        } catch (err) {
            setError(err.message);
        } finally {
            setLoading(false);
        }
    }

    return (
        <div className="login-container">
            <div className="login-card">

                <div className="login-header">
                    <h1>Elton's Books</h1>
                    <p>Sign in to your account</p>
                </div>

                <form onSubmit={handleSubmit} className="login-form">

                    <div className="field">
                        <label htmlFor="username">Username</label>
                        <input
                            id="username"
                            type="text"
                            value={username}
                            onChange={e => setUsername(e.target.value)}
                            placeholder="Your username"
                            required
                            autoFocus
                        />
                    </div>

                    <div className="field">
                        <label htmlFor="password">Password</label>
                        <input
                            id="password"
                            type="password"
                            value={password}
                            onChange={e => setPassword(e.target.value)}
                            placeholder="Your password"
                            required
                        />
                    </div>

                    {error && <p className="login-error">{error}</p>}

                    <button
                        type="submit"
                        className="login-btn"
                        disabled={loading}
                    >
                        {loading ? "Signing in..." : "Sign in"}
                    </button>

                </form>

                <p className="login-footer">
                    Don't have an account yet? <Link to="/register">Register</Link>
                </p>

            </div>
        </div>
    );
}