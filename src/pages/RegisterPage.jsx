import { useState } from "react";
import { useNavigate, Link } from "react-router-dom";
import { register } from "../services/api";
import "../styles/RegisterPage.css";

export function RegisterPage() {
    const navigate = useNavigate();

    const [form, setForm] = useState({
        name: "",
        password: "",
        confirmPassword: "",
        birth: "",
        gender: "",
        selfDescription: ""
    });

    const [error, setError]     = useState(null);
    const [loading, setLoading] = useState(false);

    function handleChange(e) {
        setForm(prev => ({ ...prev, [e.target.name]: e.target.value }));
    }

    async function handleSubmit(e) {
        e.preventDefault();
        setError(null);

        if (form.password !== form.confirmPassword) {
            setError("The passwords do not match.");
            return;
        }

        setLoading(true);

        try {
            await register(
                form.name,
                form.password,
                form.birth,
                form.gender,
                form.selfDescription
            );
            navigate("/login");
        } catch (err) {
            setError(err.message);
        } finally {
            setLoading(false);
        }
    }

    return (
        <div className="register-container">
            <div className="register-card">

                <div className="register-header">
                    <h1>Eltons' Books</h1>
                    <p>Create your account</p>
                </div>

                <form onSubmit={handleSubmit} className="register-form">

                    <div className="field">
                        <label htmlFor="name">Username</label>
                        <input
                            id="name"
                            name="name"
                            type="text"
                            value={form.name}
                            onChange={handleChange}
                            placeholder="Your username"
                            required
                            autoFocus
                        />
                    </div>

                    <div className="field-row">
                        <div className="field">
                            <label htmlFor="password">Password</label>
                            <input
                                id="password"
                                name="password"
                                type="password"
                                value={form.password}
                                onChange={handleChange}
                                placeholder="Your password"
                                required
                            />
                        </div>

                        <div className="field">
                            <label htmlFor="confirmPassword">Confirm Password</label>
                            <input
                                id="confirmPassword"
                                name="confirmPassword"
                                type="password"
                                value={form.confirmPassword}
                                onChange={handleChange}
                                placeholder="Confirm your password"
                                required
                            />
                        </div>
                    </div>

                    <div className="field-row">
                        <div className="field">
                            <label htmlFor="birth">Birth date</label>
                            <input
                                id="birth"
                                name="birth"
                                type="date"
                                value={form.birth}
                                onChange={handleChange}
                                required
                            />
                        </div>

                        <div className="field">
                            <label htmlFor="gender">Gender</label>
                            <select
                                id="gender"
                                name="gender"
                                value={form.gender}
                                onChange={handleChange}
                                required
                            >
                                <option value="">Select</option>
                                <option value="MALE">Male</option>
                                <option value="FEMALE">Female</option>
                            </select>
                        </div>
                    </div>

                    <div className="field">
                        <label htmlFor="selfDescription">About you <span>(optional)</span></label>
                        <textarea
                            id="selfDescription"
                            name="selfDescription"
                            value={form.selfDescription}
                            onChange={handleChange}
                            placeholder="Tell us a bit about you and your interests..."
                            rows={3}
                        />
                    </div>

                    {error && <p className="register-error">{error}</p>}

                    <button
                        type="submit"
                        className="register-btn"
                        disabled={loading}
                    >
                        {loading ? "Creating account..." : "Create account"}
                    </button>

                </form>

                <p className="register-footer">
                    Already have an account? <Link to="/login">Sign in</Link>
                </p>

            </div>
        </div>
    );
}