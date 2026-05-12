import { Navigate } from "react-router-dom";
import { useAuth } from "../hooks/useAuth";

export function PublicRoute({ children }) {
    const { user, loading } = useAuth();

    if (loading) return <p>Loading...</p>;

    if (user) return <Navigate to="/home" replace />;

    return children;
}