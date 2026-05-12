import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import { ProtectedRoute } from "./components/ProtectedRoute";
import { PublicRoute } from "./components/PublicRoute";
import { LoginPage } from "./pages/LoginPage";
import { RegisterPage } from "./pages/RegisterPage";
import { HomePage } from "./pages/HomePage";
import { SearchPage } from "./pages/SearchPage";
import { BookcasePage } from "./pages/BookcasePage";

export default function App() {
    return (
        <BrowserRouter>
            <Routes>

                <Route path="/" element={<Navigate to="/home" replace />} />

                <Route path="/login" element={
                    <PublicRoute>
                        <LoginPage />
                    </PublicRoute>
                } />
                <Route path="/register" element={
                    <PublicRoute>
                        <RegisterPage />
                    </PublicRoute>
                } />

                <Route path="/home" element={
                    <ProtectedRoute>
                        <HomePage />
                    </ProtectedRoute>
                } />
                <Route path="/search" element={
                    <ProtectedRoute>
                        <SearchPage />
                    </ProtectedRoute>
                } />
                <Route path="/bookcase" element={
                    <ProtectedRoute>
                        <BookcasePage />
                    </ProtectedRoute>
                } />

                <Route path="*" element={<Navigate to="/home" replace />} />

            </Routes>
        </BrowserRouter>
    );
}